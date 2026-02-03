package pk.gov.pbs.tds.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pk.gov.pbs.database.IDatabaseOperation;
import pk.gov.pbs.database.ModelBasedDatabaseHelper;
import pk.gov.pbs.formbuilder.core.ThemedCustomActivity;
import pk.gov.pbs.formbuilder.database.FormBuilderRepository;
import pk.gov.pbs.formbuilder.database.IModelClassProvider;
import pk.gov.pbs.formbuilder.models.Annexure;
import pk.gov.pbs.formbuilder.models.BackupHistory;
import pk.gov.pbs.formbuilder.models.LoginPayload;
import pk.gov.pbs.formbuilder.models.PrimaryModel;
import pk.gov.pbs.formbuilder.models.Table;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.R;
import pk.gov.pbs.tds.database.FormRepository;
import pk.gov.pbs.forms.meta.MetaManifest;
import pk.gov.pbs.tds.models.online.WebResponse;
import pk.gov.pbs.tds.models.online.AP_Backup;
import pk.gov.pbs.utils.Constants;
import pk.gov.pbs.utils.ExceptionReporter;
import pk.gov.pbs.utils.FileManager;
import pk.gov.pbs.utils.StaticUtils;
import pk.gov.pbs.utils.SystemUtils;
import pk.gov.pbs.utils.UXEvent;
import pk.gov.pbs.utils.DateTimeUtil;
import pk.gov.pbs.utils.web.GsonWebRequest;

public class BackupActivity extends ThemedCustomActivity {

    RequestQueue webRequestQueue;
    FormBuilderRepository mFormBuilderRepo;
    FormRepository mFormRepo;
    Button btnScanOnline, btnBackupOnline, btnBackupLocal;
    ViewGroup mContainerBackupsOnline, mContainerBackupsLocal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BackupActivity.this.setContentView(pk.gov.pbs.tds.R.layout.activity_backup);
        setActivityTitle("Poultry Farms | Backup Data", "Import & Export Database Backups");

        if (!FileManager.hasAllPermissions(this))
            FileManager.requestAllPermissions(this);

        init();
    }

    private void init(){
        webRequestQueue = Volley.newRequestQueue(this);
        mFormBuilderRepo = FormBuilderRepository.getInstance(getApplication());
        mFormRepo = FormRepository.getInstance(getApplication());

        btnBackupLocal = findViewById(R.id.btn_backup_local);
        btnBackupLocal.setOnClickListener(v -> {
            backupLocal(this, this::populateLocalBackups);
        });

        btnBackupOnline = findViewById(R.id.btn_backup_online);
        btnBackupOnline.setOnClickListener(this::backupOnline);

        btnScanOnline = findViewById(R.id.btn_scan_online);
        btnScanOnline.setOnClickListener(this::scanOnline);

        mContainerBackupsLocal = findViewById(R.id.local_backup_container);
        mContainerBackupsOnline = findViewById(R.id.online_backup_container);

        populateLocalBackups();
        populateOnlineBackups();
    }

    private void populateLocalBackups(){
        mContainerBackupsLocal.removeAllViews();
        try {
            File backupDir = FileManager.pathToDirectory("PoultryFarms", "backups").inPublic().createIfNotExists();
            if (backupDir == null || !backupDir.exists()) {
                mUXToolkit.toast("Backup directory <b><u>/PoultryFarms/backups/</u></b> not exists or not accessible.");
                return;
            }

            if (!backupDir.canRead() || !backupDir.canWrite() || !backupDir.canExecute()) {
                mUXToolkit.alert("Storage not Accessible", "Can't read and write in external storage, Kindly verify storage related permissions"
                        , (dialog, which) -> FileManager.requestAllPermissions(BackupActivity.this));
                return;
            }

            File[] backupFiles = backupDir.listFiles();

            if (backupFiles == null || backupFiles.length == 0) {
                mUXToolkit.toast("There are no backup files in <b><u>/PoultryFarms/backups/</u></b> directory");
                return;
            }

            int count = 0;
            for (File buf : backupFiles){
                String rawName = buf.getName();
                if (!rawName.contains(".zip"))
                    continue;

                String[] nameParts = rawName.substring(0, rawName.indexOf(".")).split("_");
                if (nameParts.length == 2 && nameParts[0].equals(CustomApplication.getUsername())) {
                    count++;
                    final long backupNo = Long.parseLong(nameParts[1]);
                    LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.item_backup_row, mContainerBackupsLocal, false);
                    ((TextView) row.findViewById(R.id.tv_1)).setText(String.valueOf(count));
                    ((TextView) row.findViewById(R.id.tv_2)).setText(DateTimeUtil.formatDateTime(backupNo, DateTimeUtil.defaultDateTimeFormat));
                    ((TextView) row.findViewById(R.id.tv_3)).setText("Local");

                    row.findViewById(R.id.btn).setOnClickListener(v -> {
                        restoreLocal(backupNo);
                    });
                    mContainerBackupsLocal.addView(row);
                }
            }
        } catch (Exception e){
            mUXToolkit.alert("Invalid Backup File", "There is invalid backup file err: " + e.getMessage());
            ExceptionReporter.handle(e);
        }
    }

    private void populateOnlineBackups(){
        mContainerBackupsOnline.removeAllViews();
        try {
            File backupDir = FileManager.pathToDirectory("PoultryFarms", "backups", "online").inPublic().createIfNotExists();
            if (backupDir == null || !backupDir.exists()) {
                mUXToolkit.toast("Backup directory <b><u>/PoultryFarms/backups/online/</u></b> not exists or not accessible.");
                return;
            }

            if (!backupDir.canRead() || !backupDir.canWrite() || !backupDir.canExecute()) {
                mUXToolkit.alert("Storage not Accessible", "Can't read and write in external storage, Kindly verify storage related permissions"
                        , (dialog, which) -> FileManager.requestAllPermissions(BackupActivity.this));
                return;
            }

            FileManager.FileOperator onlineBackupList =  FileManager.pathToFile("PoultryFarms", "backups", "online", CustomApplication.getUsername()+"_list.dat").inPublic();

            if (!onlineBackupList.exists())
                return;

            String buList = onlineBackupList.read();

            if (buList == null || buList.isEmpty()){
                mUXToolkit.toast("No online backups found, please scan online backups first");
                return;
            }

            AP_Backup[] buObjects = StaticUtils.getGson().fromJson(buList, AP_Backup[].class);
            for (AP_Backup bu : buObjects){
                if (bu.status != null && bu.status == 1 && bu.id != null) {
                    if (!bu.oid.equals(CustomApplication.getUsername()))
                        continue;

                    final long backupNo = bu.id;
                    LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.item_backup_row, mContainerBackupsLocal, false);
                    ((TextView) row.findViewById(R.id.tv_1)).setText(String.valueOf(backupNo));
                    ((TextView) row.findViewById(R.id.tv_2)).setText(DateTimeUtil.formatDateTime(bu.ts_created, DateTimeUtil.defaultDateTimeFormat));
                    ((TextView) row.findViewById(R.id.tv_3)).setText("Online");

                    row.findViewById(R.id.btn).setOnClickListener(v -> {
                        restoreOnline(backupNo);
                    });
                    mContainerBackupsOnline.addView(row);
                }
            }
        } catch (Exception e){
            mUXToolkit.toast("err: " + e.getMessage());
            ExceptionReporter.handle(e);
        }
    }

    private void scanOnline(View btn){
        mUXToolkit.showProgressDialogue("Loading Backups from server");

        GsonWebRequest<AP_Backup[]> geoRequest = new GsonWebRequest<>(
                Request.Method.GET
                , CustomApplication.getHostWebAPI() + "get_backup_list.php?oid="+CustomApplication.getUsername()
                , null
                , AP_Backup[].class
                , response -> {
                    if (response != null && response.length > 0) {
                        FileManager.pathToFile("PoultryFarms", "backups", "online", CustomApplication.getUsername()+"_list.dat").inPublic().write(
                                StaticUtils.getGson().toJson(response)
                        );
                        populateOnlineBackups();
                        StaticUtils.getHandler().post(() -> {
                            mUXToolkit.dismissProgressDialogue();
                            mUXToolkit.alert("Operation Successful", response.length + " Backup entries imported from server.");
                        });
                    } else {
                        StaticUtils.getHandler().post(() -> {
                            mUXToolkit.dismissProgressDialogue();
                            mUXToolkit.alert("No Online Backup", "No backups found on server.");
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
                                    "Failed to download boundaries",
                                    error.getMessage()
                            );
                        }
                    });
                }
        );
        geoRequest.setSecurityToken(CustomApplication.getLoginPayload().token);
        webRequestQueue.add(geoRequest);
    }

    public static void backupLocal(ThemedCustomActivity context, Runnable callback){
        context.getUXToolkit().showProgressDialogue("Creating data backup...");
        FormRepository mFormRepo = FormRepository.getInstance(context.getApplication());
        mFormRepo.executeDatabaseOperation(new IDatabaseOperation<String[]>() {
            @Override
            public String[] execute(ModelBasedDatabaseHelper db) {
                Map<String, List<String>> userDb = new HashMap<>();
                for (Class<?> model : MetaManifest.getInstance().getModels()) {
                    List<?> data = mFormRepo.getDatabase().query(model);
                    if (!data.isEmpty()) {
                        userDb.put(model.getSimpleName(), convertObjectListToStringList(data));
                    }
                }

                Map<String, List<?>> mdb = FormBuilderRepository.getInstance(context.getApplication()).getAllData(Annexure.class, LoginPayload.class);
                Map<String, List<String>> mainDb = new HashMap<>();
                for (String key : mdb.keySet()){
                    mainDb.put(key, convertObjectListToStringList(mdb.get(key)));
                }

                String folder = CustomApplication.getUsername() + "_" + SystemUtils.getUnixTs();
                try {
                    FileManager.pathToDirectory("PoultryFarms", "backups", folder).inPublic().createIfNotExists();
                    FileManager.pathToFile("PoultryFarms", "backups", folder, "main.json").inPublic()
                            .write(StaticUtils.getGson().toJson(mainDb));
                    FileManager.pathToFile("PoultryFarms", "backups", folder, "user.json").inPublic()
                            .write(StaticUtils.getGson().toJson(userDb));
                    FileManager.pathToDirectory("PoultryFarms", "backups", folder).inPublic().compress();

                    FileManager.pathToDirectory("PoultryFarms", "backups", folder).inPublic()
                            .emptyAndDelete();
                } catch (IOException e) {
                    ExceptionReporter.handle(e);
                    return new String[]{"Operation Failed", "Failed to create backup. " + e.getMessage()};
                }

                return new String[]{"Operation Successful", "Backup created successfully"};
            }

            @Override
            public void postExecute(String[] result) {
                context.getUXToolkit().dismissProgressDialogue();
                context.getUXToolkit().alert(result[0], result[1], new UXEvent.AlertDialogue() {
                    @Override
                    public void onOK(DialogInterface dialog, int which) {
                        callback.run();
                    }
                });
            }
        });
    }

    private void restoreLocal(long backupNo) {
        mFormRepo.getDatabase().executeDatabaseOperation(new IDatabaseOperation<String[]>() {
            @Override
            public void preExecute() {
                mUXToolkit.showProgressDialogue("Restoring data from backup");
            }

            @Override
            public String[] execute(ModelBasedDatabaseHelper db) {
                try {
                    File backupDir = FileManager.pathToDirectory("PoultryFarms", "backups").inPublic().createIfNotExists();
                    if (backupDir == null || !backupDir.exists())
                        return str("Operation Dismissed", "Backup directory <b><u>/PoultryFarms/backups/</u></b> not exists or not accessible.");

                    if (!backupDir.canRead() || !backupDir.canWrite() || !backupDir.canExecute())
                        return str("Storage not Accessible", "Can't read and write in external storage, Kindly verify storage related permissions");

                    File[] backupFiles = backupDir.listFiles();

                    if (backupFiles == null)
                        return new String[]{"No Backups", "There are no backups files in <b><u>/PoultryFarms/backups/</u></b> directory"};

                    FileManager.DirectoryOperator backupFileOperator = FileManager.pathToDirectory("PoultryFarms", "backups", CustomApplication.getUsername() +"_" + backupNo + ".zip").inPublic();

                    if (backupFileOperator == null || !backupFileOperator.exists())
                        return new String[]{"Backup File Not Found", "Backup file not found in <b><u>/PoultryFarms/backups/</u></b> directory"};

                    int count = backupFileOperator.decompress();
                    if (count == 0)
                        return str("Empty Backup", "There are no data files in the specified backup file");

                    String user_db_json = FileManager.pathToFile("PoultryFarms", "backups", CustomApplication.getUsername() +"_" + backupNo, "user.json")
                            .inPublic().read();
                    HashMap<String, List<String>> userDatabase = StaticUtils.getGson().fromJson(user_db_json, HashMap.class);
                    //Todo: verify integrity constraint
                    for (String key : userDatabase.keySet()) {
                        for (String modelAsString : userDatabase.get(key)) {
                            Class<?> modelClass = MetaManifest.getInstance().getModelClassByName(key);
                            Object model = StaticUtils.getGson().fromJson(modelAsString, modelClass);
                            if (CustomApplication.getConfigurations().VERIFY_USER_ON_BACKUP_RESTORE) {
                                if (PrimaryModel.class.isAssignableFrom(modelClass)) {
                                    PrimaryModel modelTable = (PrimaryModel) model;
                                    if (modelTable != null && modelTable.operatorId != null && !modelTable.operatorId.equals(CustomApplication.getUsername()))
                                        return str("Operation Dismissed", "This backup is corrupt or invalid, please use unmodified backup file");
                                }
                            }
                            mFormRepo.getDatabase().replace(model);
                        }
                    }

                    String main_db_json = FileManager.pathToFile("PoultryFarms", "backups", CustomApplication.getUsername() +"_" + backupNo, "main.json")
                            .inPublic().read();
                    HashMap<String, List<String>> mainDatabase = StaticUtils.getGson().fromJson(main_db_json, HashMap.class);
                    //Todo: verify integrity constraints
                    for (String key : mainDatabase.keySet()) {
                        for (String modelAsString : mainDatabase.get(key)) {
                            Class<?> modelClass = ((IModelClassProvider) mFormBuilderRepo.getDatabase()).getModelClassByName(key);
                            Object model = StaticUtils.getGson().fromJson(modelAsString, modelClass);
                            mFormBuilderRepo.getDatabase().replace(model);
                        }
                    }

                    FileManager.pathToDirectory("PoultryFarms", "backups", CustomApplication.getUsername()).inPublic().emptyAndDelete();
                } catch (Exception e){
                    ExceptionReporter.handle(e);
                    return str("Operation Failed", "Failed to restore backup. err: " + e.getMessage());
                }
                return str("Operation Successful", "Data restored from backup successfully");
            }

            @Override
            public void postExecute(String[] result) {
                StaticUtils.getHandler().post(() -> {
                    mUXToolkit.dismissProgressDialogue();
                    mUXToolkit.alert(result[0], result[1]);
                });
            }
        });
    }

    private void backupOnline(View btn){
        StaticUtils.getHandler().post(()-> {
            mUXToolkit.showProgressDialogue("Creating data backup...");
        });

        mFormRepo.executeDatabaseOperation(new IDatabaseOperation<String[]>() {
            @Override
            public String[] execute(ModelBasedDatabaseHelper db) {
                Gson gson = new GsonBuilder().create();
                Map<String, String> request = new HashMap<>();

                request.put("oid", CustomApplication.getUsername());
                request.put("version", CustomApplication.getApplicationVersion());
                request.put("user_db_name", mFormRepo.getDatabase().getDatabaseName());
                request.put("main_db_name", mFormBuilderRepo.getDatabase().getDatabaseName());

                Map<String, List<String>> userDb = new HashMap<>();
                for (Class<?> model : MetaManifest.getInstance().getModels()) {
                    String criteria = null;
                    if (SystemUtils.modelHasField(model, "sid"))
                        criteria = "`sid` is null";

                    List<?> data = mFormRepo.getDatabase().query(
                            model, criteria
                    );

                    if (!data.isEmpty()) {
                        userDb.put(model.getSimpleName(), convertObjectListToStringList(data));
                    }
                }

                Map<String, List<?>> mdb = mFormBuilderRepo.getAllData(true, Annexure.class, LoginPayload.class);
                Map<String, List<String>> mainDb = new HashMap<>();
                for (String key : mdb.keySet()){
                    mainDb.put(key, convertObjectListToStringList(mdb.get(key)));
                }
                request.put("user_db_json", gson.toJson(userDb));
                request.put("main_db_json", gson.toJson(mainDb));

                String dbJson = gson.toJson(request);
                StaticUtils.getHandler().post(()->{
                    mUXToolkit.showProgressDialogue("Uploading data...");
                });
                GsonWebRequest<WebResponse> backupRequest = new GsonWebRequest<WebResponse>(
                        Constants.WebRequestMethod.POST,
                        CustomApplication.makeWebApiUrl("db_backup.php"),
                        dbJson.getBytes(StandardCharsets.UTF_8),
                        WebResponse.class,
                        new com.android.volley.Response.Listener<WebResponse>() {
                            @Override
                            public void onResponse(WebResponse response) {
                                if (response.status == 1) {
                                    BackupHistory bh = new BackupHistory();
                                    bh.sid = (long) response.ids[0];
                                    bh.type = 1;
                                    mFormBuilderRepo.getDatabase().insert(bh);

                                    StaticUtils.getHandler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            StaticUtils.getHandler().post(() -> {
                                                mUXToolkit.dismissProgressDialogue();
                                                mUXToolkit.alert("Operation Successful", "Created backup (online) successfully");
                                                populateOnlineBackups();
                                            });
                                        }
                                    });
                                } else {
                                    StaticUtils.getHandler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            StaticUtils.getHandler().post(() -> {
                                                mUXToolkit.dismissProgressDialogue();
                                                mUXToolkit.alert("Operation Failed", "Failed to upload data backup to server, err: " + response.message);
                                            });
                                        }
                                    });
                                }
                            }
                        },
                        new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                StaticUtils.getHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        StaticUtils.getHandler().post(()->{
                                            mUXToolkit.dismissProgressDialogue();
                                            if (error.getMessage() != null && error.getMessage().contains("timeout")) {
                                                mUXToolkit.alert(
                                                        "Connection Timeout"
                                                        , "Please check your internet connection and try again."
                                                );
                                            }
                                            else if (error.getMessage() != null && error.getMessage().contains("Unable to resolve host")) {
                                                mUXToolkit.alert(
                                                        "No Internet Connection"
                                                        , "Please check your internet connection and try again."
                                                );
                                            } else {
                                                mUXToolkit.alert(
                                                        "Unknown Error Occurred",
                                                        error.getMessage()
                                                );
                                            }
                                        });
                                    }
                                });
                            }
                        }
                );

                backupRequest.setSecurityToken(CustomApplication.getLoginPayload().token);
                webRequestQueue.add(backupRequest);

                return null;
            }
        });
    }

    private void restoreOnline(long backupNo){
        mUXToolkit.showProgressDialogue("Downloading latest backup");
        String url = CustomApplication.getHostWebAPI() + "db_restore.php?oid=" + CustomApplication.getUsername() + "&id=" + backupNo;
        GsonWebRequest<AP_Backup> geoRequest = new GsonWebRequest<>(
                Request.Method.GET
                , url
                , null
                , AP_Backup.class
                , response -> {
                    if (response.status == 1) {
                        try {
                            HashMap<String, List<String>> userDatabase = StaticUtils.getGson().fromJson(response.user_db_json, HashMap.class);
                            for (String key : userDatabase.keySet()) {
                                for (String modelAsString : userDatabase.get(key)) {
                                    Class<?> modelClass = MetaManifest.getInstance().getModelClassByName(key);
                                    Object model = StaticUtils.getGson().fromJson(modelAsString, modelClass);
                                    mFormRepo.getDatabase().replace(model);
                                }
                            }

                            HashMap<String, List<String>> mainDatabase = StaticUtils.getGson().fromJson(response.main_db_json, HashMap.class);
                            for (String key : mainDatabase.keySet()) {
                                for (String modelAsString : mainDatabase.get(key)) {
                                    Class<?> modelClass = ((IModelClassProvider) mFormBuilderRepo.getDatabase()).getModelClassByName(key);
                                    Object model = StaticUtils.getGson().fromJson(modelAsString, modelClass);
                                    mFormBuilderRepo.getDatabase().replace(model);
                                }
                            }

                            StaticUtils.getHandler().post(() -> {
                                mUXToolkit.dismissProgressDialogue();
                                mUXToolkit.alert("Operation Successful", "Backup restored successfully.");
                            });
                        } catch (Exception e){
                            StaticUtils.getHandler().post(() -> {
                                mUXToolkit.dismissProgressDialogue();
                                mUXToolkit.alert("Operation Failed", "Failed to restore backup. err: " + e.getMessage());
                            });
                            ExceptionReporter.handle(e);
                        }
                    } else {
                        StaticUtils.getHandler().post(() -> {
                            mUXToolkit.dismissProgressDialogue();
                            mUXToolkit.alert("Operation Failed", "Failed to retrieve backup from server. err: " + response.message);
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
                        } else if (error.getMessage() != null && error.getMessage().contains("Unable to resolve host")) {
                            mUXToolkit.alert(
                                    "No Internet Connection"
                                    , "Please check your internet connection and try again."
                            );
                        } else {
                            mUXToolkit.alert(
                                    "Failed to download backup",
                                    "err: " + error.getMessage()
                            );
                        }
                    });
                }
        );
        geoRequest.setSecurityToken(CustomApplication.getLoginPayload().token);
        webRequestQueue.add(geoRequest);
    }

    private String[] str(String... args){ //just for syntactic sugar
        return args;
    }

    public static List<String> convertObjectListToStringList(List<?> sourceList){
        List<String> targetList = new ArrayList<>();
        for (Object obj : sourceList) {
            if (obj instanceof Table)
                ((Table) obj).setupDataIntegrity();
            targetList.add(StaticUtils.getGson().toJson(obj));
        }
        return targetList;
    }
}