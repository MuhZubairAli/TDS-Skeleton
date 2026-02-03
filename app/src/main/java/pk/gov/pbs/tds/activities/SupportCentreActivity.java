package pk.gov.pbs.tds.activities;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pk.gov.pbs.database.IDatabaseOperation;
import pk.gov.pbs.database.ModelBasedDatabaseHelper;
import pk.gov.pbs.formbuilder.core.ThemedCustomActivity;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.R;
import pk.gov.pbs.tds.database.FormRepository;
import pk.gov.pbs.forms.meta.MetaManifest;
import pk.gov.pbs.tds.models.online.FixResponse;
import pk.gov.pbs.tds.models.online.WebResponse;
import pk.gov.pbs.utils.Constants;
import pk.gov.pbs.utils.ExceptionReporter;
import pk.gov.pbs.utils.FileManager;
import pk.gov.pbs.utils.StaticUtils;
import pk.gov.pbs.utils.SystemUtils;
import pk.gov.pbs.utils.DateTimeUtil;
import pk.gov.pbs.utils.web.GsonWebRequest;

public class SupportCentreActivity extends ThemedCustomActivity {

    RequestQueue webRequestQueue;
    protected FormRepository mRepository;
    EditText etSubject, etDescription, etContactNo;
    Button btnSubmit, btnScanFixes;
    Spinner spType, spSection, spEBC, spHH;
    LinearLayout containerEbcHh, mContainerFixes;
    List<String> usefulLinks;

    ViewGroup form, links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_centre);
        setActivityTitle("Poultry Farms | Help Centre", "Data Backup");
        init();
    }

    private void init(){
//        webRequestQueue = Volley.newRequestQueue(this);
//        mRepository = FormRepository.getInstance(getApplication());
//        etSubject = findViewById(R.id.et_issue_subject);
//        etDescription = findViewById(R.id.et_issue_description);
//        etContactNo = findViewById(R.id.et_contact_number);
//        String phone = CustomApplication.getGlobalString("PhoneNumber_"+CustomApplication.getLoginPayload().userName, "");
//        if (!phone.isEmpty())
//            etContactNo.setText(phone);
//
//        containerEbcHh = findViewById(R.id.container_spEbHh);
//
//        mContainerFixes = findViewById(R.id.container_fixes);
//        btnScanFixes = findViewById(R.id.btn_scan_fixes);
//        btnScanFixes.setOnClickListener(this::scanFixes);
//        populateFixes();
//
//        //-------------------spinner for household  ---------------------------------
//        spHH = findViewById(R.id.sp_hh);
//        List<String> optHH = new ArrayList<>(Collections.singletonList("Specify Household"));
//        ArrayAdapter<String> adapterHH = new ArrayAdapter<>(this, pk.gov.pbs.formbuilder.R.layout.item_list_sp, optHH);
//        adapterHH.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spHH.setAdapter(adapterHH);
//
//        //-------------------spinner for block code ---------------------------------
//        spEBC = findViewById(R.id.sp_ebc);
//        List<String> blockCodes = mRepository.getUtilsDao().getBlockCodes();
//        ArrayList<String> bco = new ArrayList<>();
//        bco.add("Specify Block Code");
//        if (blockCodes != null)
//            bco.addAll(blockCodes);
//        ArrayAdapter<String> adapterEBC = new ArrayAdapter<>(this, pk.gov.pbs.formbuilder.R.layout.item_list_sp, bco);
//        adapterEBC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spEBC.setAdapter(adapterEBC);
//        spEBC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mRepository.executeDatabaseOperation(new IDatabaseOperation<List<S1Model>>() {
//                    @Override
//                    public List<S1Model> execute(ModelBasedDatabaseHelper db) {
//                        return mRepository.getDatabase().query(
//                                S1Model.class,
//                                "`EBCode`=?",
//                                parent.getSelectedItem().toString().substring(3)
//                        );
//                    }
//
//                    @Override
//                    public void postExecute(List<S1Model> result) {
//                        adapterHH.clear();
//                        adapterHH.add("Specify Poultry Farm Entry");
//                        for (S1Model farm : result){
//                            if (farm.status == 1)
//                                adapterHH.add(farm.srNo + " | " + farm.farmName + " | " + farm.ownerName);
//                        }
//                        adapterHH.notifyDataSetChanged();
//                    }
//                });
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        //------------------- expandable form ---------------------------------
//        form = findViewById(R.id.form_header);
//        form.setOnClickListener(v -> {
//            ViewGroup container = (ViewGroup) v.getParent();
//            TransitionManager.beginDelayedTransition(container, new AutoTransition());
//
//            ViewGroup viewGroup = container.findViewById(R.id.form_body);
//            ImageView expand = container.findViewById(R.id.img_expand_icon);
//            if (viewGroup.getVisibility() == View.VISIBLE) {
//                viewGroup.setVisibility(View.GONE);
//                expand.setImageResource(R.drawable.ic_arrow_circle_down);
//            } else {
//                viewGroup.setVisibility(View.VISIBLE);
//                expand.setImageResource(R.drawable.ic_arrow_circle_up);
//            }
//        });
//
//        //-------------------issue type ---------------------------------
//        spType = findViewById(R.id.sp_issue_type);
//        String[] opts = getResources().getStringArray(R.array.issue_categories);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, pk.gov.pbs.formbuilder.R.layout.item_list_sp, opts);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spType.setAdapter(adapter);
//
//        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 2){
//                    containerEbcHh.setVisibility(View.VISIBLE);
//                } else{
//                    containerEbcHh.setVisibility(View.GONE);
//                }
//
//                if (position == 3){
//                    spSection.setVisibility(View.GONE);
//                } else{
//                    spSection.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        //------------------- section relevant to issue ---------------------------------
//        spSection = findViewById(R.id.sp_screen);
//        List<String> screens = new ArrayList<>(Arrays.asList("Specify Section / Screen", "Sync Data", "View Map", "Block Status"));
//        screens.addAll(Arrays.asList(MetaManifest.getInstance().getSectionIdentifiers()));
//
//        ArrayAdapter<String> adptScreen = new ArrayAdapter<>(this, pk.gov.pbs.formbuilder.R.layout.item_list_sp, screens);
//        adptScreen.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spSection.setAdapter(adptScreen);
//
//        //-------------------button to submit issue ---------------------------------
//        btnSubmit = findViewById(R.id.btn_issue_submit);
//        btnSubmit.setOnClickListener(v -> sendCrashReport());
//
//        //========================= useful links ==================================
//        usefulLinks = new ArrayList<>();
//        usefulLinks.add("View Survey User Manual");
//        usefulLinks.add("Understanding questionnaire structure");
//        usefulLinks.add("Understanding application flow");
//        usefulLinks.add("Understanding questions types and response methods");
//        usefulLinks.add("Understanding sections types and their flows");
//        usefulLinks.add("Example entries of forms with different scenarios");
//
//        links = findViewById(R.id.container_links);
//        for (String link : usefulLinks){
//            View view = getLayoutInflater().inflate(R.layout.item_img_tv, links, false);
//            ((TextView) view.findViewById(R.id.tv_link)).setText(link);
//            links.addView(view);
//        }
//
//        if (Constants.DEBUG_MODE) {
//            links.getChildAt(0).setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    startActivity(new Intent(SupportCentreActivity.this, DebugActivity.class));
//                    return true;
//                }
//            });
//        }
//
////        links.getChildAt(usefulLinks.size()-1).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                mRepository.getDatabase().getWritableDatabase().execSQL(
////                        "DELETE FROM `"+ FormContext.class.getSimpleName() +"`"
////                );
////            }
////        });
    }

    private void sendCrashReport(){
        final String subject = etSubject.getText().toString();
        if (subject.isEmpty()){
            etSubject.setError("Subject is required");
            return;
        }
        final String description = etDescription.getText().toString();
        if (description.isEmpty()){
            etDescription.setError("Detail of the issue is required");
            return;
        }
        final String contactNo = etContactNo.getText().toString();
        if (!(contactNo.length() == 11 && contactNo.startsWith("03")) && contactNo.length() != 10) {
            etContactNo.setError("Valid phone number is required");
            return;
        }

        CustomApplication.getSharedPreferencesManager().edit()
                .putString("PhoneNumber_"+CustomApplication.getLoginPayload().userName, contactNo).commit();

        final String type = spType.getSelectedItem().toString();
        final Integer typePosition = spType.getSelectedItemPosition();
        final String section = spSection.getSelectedItem().toString();
        final String version = CustomApplication.getApplicationVersion();
        final String ebc = spEBC.getSelectedItemPosition() == 0 ? null : spEBC.getSelectedItem().toString().substring(3);
        final String sno = spHH.getSelectedItemPosition() == 0 ? null : spHH.getSelectedItem().toString().split(" - ")[0];

        mUXToolkit.showProgressDialogue("Uploading crash report and database to server...",false);
        mRepository.executeDatabaseOperation(new IDatabaseOperation<Map<String,List<?>>>() {
            @Override
            public Map<String,List<?>> execute(ModelBasedDatabaseHelper db) {
                Map<String, List<?>> database = new HashMap<>();

                List<KeyValuePair> formDataList = new ArrayList<>();
                formDataList.add(new KeyValuePair("subject", subject));
                formDataList.add(new KeyValuePair("description", description));
                formDataList.add(new KeyValuePair("contact_no", contactNo));
                formDataList.add(new KeyValuePair("type", type));
                formDataList.add(new KeyValuePair("section", section));
                formDataList.add(new KeyValuePair("ebcode", ebc));
                formDataList.add(new KeyValuePair("sno", sno));
                formDataList.add(new KeyValuePair("version", version));
                formDataList.add(new KeyValuePair("enum_gender", "" + CustomApplication.getLoginPayload().gender));
                formDataList.add(new KeyValuePair("oid", CustomApplication.getUsername()));
                database.put("FormData", formDataList);

                if (typePosition == 2) {
                    StringBuilder sb = new StringBuilder();
                    List<String> args = new ArrayList<>();

                    sb.append("`ebcode`=?");
                    args.add(ebc);

                    if (sno != null) {
                        sb.append(" AND `sno`=?");
                        args.add(sno);
                    }

                    for (Class<?> model : MetaManifest.getInstance().getModels()) {
                        String whereClause = sb.length() > 0 ? sb.toString() : null;
                        try {
                            if (whereClause != null && whereClause.contains("EBCode")) {
                                Field EBCodeField = model.getField("EBCode");
                            }

                            if (whereClause != null && whereClause.contains("SNo")) {
                                Field sNoField = model.getField("SNo");
                            }
                        } catch (NoSuchFieldException ignored) {
                            continue;
                        }

                        String[] argsArray = new String[args.size()+1];
                        argsArray[0] = whereClause;
                        for (int i = 0; i < args.size(); i++) {
                            argsArray[i+1] = args.get(i);
                        }

                        try {
                            List<?> data = mRepository.getDatabase().query(
                                    model,
                                    argsArray
                            );
                            if (!data.isEmpty()) {
                                database.put(model.getSimpleName(), data);
                            }
                        } catch (Exception e){
                            ExceptionReporter.handle(e);
                        }
                    }
                }

                String json = StaticUtils.getGson().toJson(database);
                GsonWebRequest<WebResponse> request = new GsonWebRequest<WebResponse>(
                        Constants.WebRequestMethod.POST,
                        CustomApplication.makeWebApiUrl("report_issue.php"),
                        json.getBytes(StandardCharsets.UTF_8),
                        WebResponse.class,
                        new com.android.volley.Response.Listener<WebResponse>() {
                            @Override
                            public void onResponse(WebResponse response) {
                                StaticUtils.getHandler().post(() -> {
                                    StaticUtils.getHandler().post(()-> {
                                        clearLockForm();
                                        mUXToolkit.dismissProgressDialogue();
                                        mUXToolkit.alert("Report submitted successfully");
                                    });
                                    CustomApplication.getSharedPreferencesManager()
                                            .edit()
                                            .putLong(pk.gov.pbs.tds.Constants.LAST_ISSUE_REPORT_TIME, SystemUtils.getUnixTs())
                                            .apply();
                                });
                            }
                        },
                        new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                StaticUtils.getHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mUXToolkit.dismissProgressDialogue();
                                        mUXToolkit.alert("Report submission failed. "+error.getMessage());
                                    }
                                });
                            }
                        }
                );
                request.setSecurityToken(CustomApplication.getLoginPayload().token);
                StaticUtils.queueWebRequest(
                        SupportCentreActivity.this,
                        request
                );
                return null;
            }
        });
    }

    private void scanFixes(View btn){
        mUXToolkit.showProgressDialogue("Scanning for fixes from server");

        GsonWebRequest<FixResponse[]> geoRequest = new GsonWebRequest<>(
                Request.Method.POST
                , CustomApplication.getHostWebAPI() + "get_fixes.php/"
                , ("{\"oid\":\""+CustomApplication.getUsername()+"\",\"version\":\""+CustomApplication.getApplicationVersionCode()+"\"}").getBytes(StandardCharsets.UTF_8)
                , FixResponse[].class
                , response -> {
                    if (response != null && response.length > 0) {
                        if (Objects.equals(response[0].status, 0)) {
                            StaticUtils.getHandler().post(() -> {
                                mUXToolkit.dismissProgressDialogue();
                                mUXToolkit.alert("Operation Failed", "err: " + response[0].message);
//                                FirebaseCrashlytics.getInstance().recordException(new Exception("http request failed for get_fixes.php/ - " + response[0].message + " - " + response[0].backtrace));
                            });
                            return;
                        }
                        for (FixResponse fix : response) {
                            if (!Objects.equals(fix.status, 0)) {
                                FileManager.pathToFile("fixes", fix.oid + "_" + fix.id + ".dat").inInternal().write(
                                        StaticUtils.getGson().toJson(fix)
                                );
                            } else {
                                mUXToolkit.toast(String.valueOf(fix.message) + " - " + String.valueOf(fix.backtrace));
                            }
                        }
                        populateFixes();
                        StaticUtils.getHandler().post(() -> {
                            mUXToolkit.dismissProgressDialogue();
                            mUXToolkit.alert("Operation Successful", response.length + " Fixes downloaded from server.");
                        });
                    } else {
                        StaticUtils.getHandler().post(() -> {
                            mUXToolkit.dismissProgressDialogue();
                            mUXToolkit.alert("No Fix Available", "No fix found on the server. If you have reported any issue, You will be contacted by <b><u>DP Centre</u></b> if applicable the fix will be provided.");
                        });
                    }
                }
                , error -> {
                    StaticUtils.getHandler().post(() -> {
                        mUXToolkit.dismissProgressDialogue();
                        if (error.getMessage() != null && error.getMessage().contains("timeout")) {
                            mUXToolkit.alert(
                                    "Connection Timeout"
                                    , "Please check your internet connection and try again."
                            );
                        } else if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            mUXToolkit.alert(
                                    "Not Found (404)"
                                    , "Endpoint not found on server."
                            );
                        } else if (error.getMessage() != null && error.getMessage().contains("Unable to resolve host")) {
                            mUXToolkit.alert(
                                    "No Internet Connection"
                                    , "Please check your internet connection and try again."
                            );
                        } else {
                            mUXToolkit.alert(
                                    "Failed to complete request",
                                    "err " + String.valueOf(error.networkResponse) + " " + String.valueOf(error.getMessage())
                            );
                        }
                    });
                }
        );

        geoRequest.setSecurityToken(CustomApplication.getLoginPayload().token);
        webRequestQueue.add(geoRequest);
    }

    private void populateFixes(){
        try {
            File[] files = FileManager.pathToDirectory("fixes").inInternal().get().listFiles();
            if (files != null){
                mContainerFixes.removeAllViews();
                for (File file : files) {
                    if (file.getName().startsWith(CustomApplication.getLoginPayload().userName) && file.getName().endsWith(".dat")) {
                        try {
                            String[] nameSplit = file.getName().split("\\.")[0].split("_");
                            FixResponse fix = StaticUtils.getGson().fromJson(
                                    FileManager.getInstance(getApplication()).readFileString(file),
                                    FixResponse.class
                            );
                            LinearLayout itemFix = (LinearLayout) getLayoutInflater().inflate(
                                    R.layout.item_fix,
                                    mContainerFixes,
                                    false
                            );

                            if (fix.title == null || fix.title.isEmpty())
                                fix.title = "Fix# "+fix.id + " - " + DateTimeUtil.formatDateTime(fix.ts_created, "dd/MM/yyyy hh:mm");

                            if (nameSplit.length == 2)
                                fix.title = "<b>"+nameSplit[1] + "</b> - " + fix.title;

                            ((TextView) itemFix.findViewById(R.id.tv_title)).setText(Html.fromHtml(fix.title));
                            if (fix.description == null || fix.description.isEmpty())
                                fix.description = "No description provided.<br />";

                            ((TextView) itemFix.findViewById(R.id.tv_desc)).setText(Html.fromHtml(fix.description));

                            if ((fix.conf_fix == null || fix.conf_fix.isEmpty()) && (fix.db_fix == null || fix.db_fix.isEmpty())) {
                                itemFix.findViewById(R.id.container_actions).setVisibility(View.GONE);
                            } else {
                                Button btnFix = itemFix.findViewById(R.id.btn_fix);
                                setupFixButton(btnFix, fix);
                            }

                            mContainerFixes.addView(itemFix);
                        } catch (Exception e){
                            ExceptionReporter.handle(e);
                        }
                    }
                }
            }
        } catch (Exception e){
            ExceptionReporter.handle(e);
        }
    }

    private void setupFixButton(Button btn, FixResponse fix){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.equals(fix.db_fix, "-") && Objects.equals(fix.conf_fix,"-")){
                    mUXToolkit.alert("Operation Skipped", "Fix already applied, if this fix do not fix your issue please contact <u>DP Centre, Support Services Wing, PBS</u> and provide reference #"+fix.id+" for assistance.");
                    return;
                }

                if (fix.conf_fix != null && !fix.conf_fix.isEmpty() && !fix.conf_fix.equals("-")){
                    try {
                        pk.gov.pbs.tds.Constants.AppConfigurations conf = StaticUtils.getGson().fromJson(
                                fix.conf_fix, pk.gov.pbs.tds.Constants.AppConfigurations.class
                        );
                        CustomApplication.setConfigurations(conf);
                        fix.conf_fix = "-";
                        mUXToolkit.toast("Configurations updated successfully");
                    } catch (Exception e){
                        mUXToolkit.alert("Error!", "Failed to parse configuration. Kindly contact <u>DP Centre, Support Services Wing, PBS</u> and provide reference #"+fix.id+" for assistance.");
                        ExceptionReporter.handle(e);
                    }
                } else
                    fix.conf_fix = "-";

                if (fix.db_fix != null && !fix.db_fix.isEmpty() && !fix.db_fix.equals("-")) {
                    BackupActivity.backupLocal(
                            SupportCentreActivity.this,
                            () -> {
                                int ec = 0;
                                for (String query : fix.db_fix.split(";")) {
                                    try {
                                        mRepository.getDatabase().getWritableDatabase().execSQL(query);
                                    } catch (Exception e) {
                                        ec++;
                                        mUXToolkit.alert("Error!", "Failed to execute database fix. Kindly contact <u>DP Centre, Support Services Wing, PBS</u> and provide reference #" + fix.id + " for assistance.");
                                        ExceptionReporter.handle(e);
                                    }
                                }

                                fix.db_fix = "-";
                                FileManager.pathToFile("fixes", fix.oid + "_" + fix.id + ".dat").inPublic().write(
                                        StaticUtils.getGson().toJson(fix)
                                );

                                mUXToolkit.alert("Operation Finished", "Process to apply fix finished "+(ec==0?"successfully":"with "+ec+" failed queries")+", please restart the application.");
                            }
                    );
                } else
                    fix.db_fix = "-";

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        long lastRequestTime = CustomApplication.
                getGlobalLong(pk.gov.pbs.tds.Constants.LAST_ISSUE_REPORT_TIME, 0);
        lastRequestTime += CustomApplication.getConfigurations().WAIT_TIME_ISSUE_REPORTS;

        if (lastRequestTime > SystemUtils.getUnixTs()){
            clearLockForm();
        }
    }

    private void clearLockForm(){
        etSubject.setText("");
        etDescription.setText("");
        etContactNo.setText("");
        spType.setSelection(0);
        spSection.setSelection(0);
        spEBC.setSelection(0);
        spHH.setSelection(0);

        etSubject.setEnabled(false);
        etDescription.setEnabled(false);
        etContactNo.setEnabled(false);
        spType.setEnabled(false);
        spSection.setEnabled(false);
        spEBC.setEnabled(false);

        btnSubmit.setEnabled(false);
    }

    public static class KeyValuePair {
        public String key;
        public String value;
        public KeyValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}