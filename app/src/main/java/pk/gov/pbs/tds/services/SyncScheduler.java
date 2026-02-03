package pk.gov.pbs.tds.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import pk.gov.pbs.formbuilder.database.FormBuilderRepository;
import pk.gov.pbs.formbuilder.models.Option;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.database.FormRepository;
import pk.gov.pbs.tds.database.DataProvider;
import pk.gov.pbs.tds.Constants;
import pk.gov.pbs.tds.models.online.OptionsSyncResponse;
import pk.gov.pbs.tds.models.online.SyncResponse;
import pk.gov.pbs.tds.models.online.SyncedOption;
import pk.gov.pbs.utils.ExceptionReporter;
import pk.gov.pbs.utils.FileManager;
import pk.gov.pbs.utils.StaticUtils;
import pk.gov.pbs.utils.web.GsonWebRequest;

public class SyncScheduler extends JobService {
    private static final String TAG = "SyncScheduler";
    public static final String BROADCAST_ACTION_HOUSEHOLD_SYNCED = "pk.gov.pbs.BROADCAST_ACTION_HOUSEHOLD_SYNCED";
    RequestQueue webRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        webRequestQueue = Volley.newRequestQueue(this);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob: onStartJob is being called");

        if (CustomApplication.getLoginPayload() == null){
            Toast.makeText(getApplicationContext(), "SyncService rejected sync request, user not Logged in", Toast.LENGTH_LONG).show();
            return false;
        }

//        if (CustomApplication.isDeveloperMode()) {
//            Toast.makeText(getApplicationContext(), "SyncService rejected sync request, user in Practice Mode", Toast.LENGTH_LONG).show();
//            return false;
//        }

        PersistableBundle bundle = jobParameters.getExtras();
        String[] hhIDs = bundle.getStringArray("hhIds");

        if (hhIDs != null && hhIDs.length > 0){
            Log.d(TAG, "onStartJob: "+hhIDs.length + " HH IDs received for sync request");
            for (String hh : hhIDs){
                String[] hhData = hh.split("@");
                syncForm(hhData[1], Integer.parseInt(hhData[0]));
            }
        }

        String optionsJson = bundle.getString("optionsJson", null);
        if (optionsJson != null && !optionsJson.isEmpty()){
            syncOptions(optionsJson);
        }

        if (bundle.getBoolean("fetchOptions", false))
            fetchNewOptions();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private void syncForm(String pcode, Integer sno){
        Log.d(TAG, "syncForm: sending http request to sync "+sno+"@"+pcode);
        if (pcode == null || pcode.isEmpty() || sno == null || CustomApplication.getLoginPayload() == null) {
            Log.d(TAG, "syncForm: Can't proceed to sync, either user not logged in or form identifiers are missing");
            return;
        }

//        if (pcode != null && sno != null) {
//            Toast.makeText(getApplicationContext(), "Syncing Household " + sno + "@" + pcode, Toast.LENGTH_LONG).show();
//            return;
//        }

        final FormRepository mFormRepo = FormRepository.getInstance(getApplication());
        Map<String, List<?>> hhData = DataProvider.getFarmData(mFormRepo, pcode, sno);
        String url = CustomApplication.getHostWebAPI() + "sync_entries.php/";
        //                    CustomApplication.CrashlyticsLog(String.valueOf(error.getMessage()) + " -||- " + Arrays.toString(error.getStackTrace()));
        GsonWebRequest<SyncResponse> geoRequest = new GsonWebRequest<>(
                Request.Method.POST
                , url
                , StaticUtils.getSimpleGson().toJson(hhData).getBytes(StandardCharsets.UTF_8)
                , SyncResponse.class
                , response -> {
                    if (response != null && response.status == 1) {
                        if (response.message != null && !response.message.equals("OK") && response.message.contains("|"))
                            FileManager.pathToFile("mainNotice.txt").inInternal().write(response.message);

                        for (String tbl : response.ids.keySet()) {
                            int[][] ids = response.ids.get(tbl);
                            if (ids != null) {
                                for (int[] id : ids) {
                                    ContentValues cv = new ContentValues();
                                    cv.put("sid", id[1]);
                                    mFormRepo.getDatabase().getWritableDatabase()
                                            .update(tbl, cv, "aid = ?", new String[]{String.valueOf(id[0])});
                                }

                                Toast.makeText(SyncScheduler.this, "PoultryFarm " + sno + "@" + pcode + " synced!", Toast.LENGTH_LONG).show();

                                Intent i = new Intent();
                                i.setAction(BROADCAST_ACTION_HOUSEHOLD_SYNCED);
                                i.setPackage(this.getPackageName());
                                i.putExtra(SyncService.BROADCAST_EXTRA_HOUSEHOLD, sno + "@" + pcode);
                                sendBroadcast(i);
                            }
                        }
                    } else if (response != null && response.status == 0) {
                        if (CustomApplication.getGlobalInt("reported_" + sno + "@" + pcode, 0) == 0) {
                            CustomApplication
                                    .getSharedPreferencesManager()
                                    .edit()
                                    .putInt("reported_" + sno + "@" + pcode, 1).apply();

                            FileManager.getInstance(CustomApplication.getInstance())
                                    .pathToFile("PoultryFarms", "error.log").inPublic().append("\n\n" + sno + "@" + pcode + response.message + " -||- " + response.backtrace);
                        }
                    }
                }
                , error -> {

                }
        );
        geoRequest.setSecurityToken(CustomApplication.getLoginPayload().token);
        webRequestQueue.add(geoRequest);
    }

    private void syncOptions(String optionsJson){
        if (CustomApplication.getLoginPayload() == null)
            return;

        String url = CustomApplication.getHostWebAPI() + "sync_options.php/";
        GsonWebRequest<OptionsSyncResponse> geoRequest = new GsonWebRequest<>(
                Request.Method.POST
                , url
                , optionsJson.getBytes(StandardCharsets.UTF_8)
                , OptionsSyncResponse.class
                , response -> {
                    if (response != null && response.status == 1) {
                        if (response.lastId != null)
                            CustomApplication.setGlobalLong(Constants.OPTIONS_LAST_ID_KEY, response.lastId);
                        SQLiteDatabase db = FormBuilderRepository.getInstance(getApplication()).getDatabase().getWritableDatabase();
                        for (SyncedOption opt : response.syncedOptions) {
                            ContentValues cv = new ContentValues();
                            cv.put("sid", opt.sid);
                            db.update(
                                    Option.class.getSimpleName(), cv, "aid = ?", new String[]{String.valueOf(opt.aid)}
                            );
                        }

                        if (response.message != null)
                            Toast.makeText(getApplicationContext(), response.message, Toast.LENGTH_LONG).show();

                        Toast.makeText(getApplicationContext(), response.syncedOptions.size() + " options synced", Toast.LENGTH_LONG).show();
                    } else if (response != null && response.status == 0)
                        ExceptionReporter.handle(new RuntimeException("sync_options (process) returned error: " + response.message + " -||- " + response.backtrace));
                }
                , error -> {

                }
        );
        geoRequest.setSecurityToken(CustomApplication.getLoginPayload().token);
        webRequestQueue.add(geoRequest);
    }

    private void fetchNewOptions(){
        if (CustomApplication.getLoginPayload() == null)
            return;

        long lastId = CustomApplication.getGlobalLong(Constants.OPTIONS_LAST_ID_KEY, 0);
        String url = CustomApplication.getHostWebAPI() + "get_options.php/?lastId="+lastId;
        GsonWebRequest<Option[]> request = new GsonWebRequest<>(
                Request.Method.GET,
                url,
                null,
                Option[].class,
                response -> {
                    if (response != null && response.length > 0) {
                        FormBuilderRepository repo = FormBuilderRepository.getInstance(getApplication());
                        long maxSid = 0;
                        for (Option opt : response) {
                            repo.getDatabase().insert(opt);
                            if (opt.sid > maxSid)
                                maxSid = opt.sid;
                        }

                        CustomApplication.setGlobalLong(Constants.OPTIONS_LAST_ID_KEY, maxSid);
                        Toast.makeText(getApplicationContext(), response.length + " options fetched", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {

                }
        );

        request.setSecurityToken(CustomApplication.getLoginPayload().getToken());
        webRequestQueue.add(request);
    }
}
