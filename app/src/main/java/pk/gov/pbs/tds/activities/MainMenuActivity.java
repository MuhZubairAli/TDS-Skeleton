package pk.gov.pbs.tds.activities;

import static pk.gov.pbs.tds.CustomApplication.getConfigurations;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import pk.gov.pbs.formbuilder.core.ThemedCustomActivity;
import pk.gov.pbs.formbuilder.database.FormBuilderRepository;
import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.formbuilder.models.LoginPayload;
import pk.gov.pbs.formbuilder.utils.ThemeUtils;
import pk.gov.pbs.forms.MainActivity;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.database.FormRepository;
import pk.gov.pbs.tds.services.SyncService;
import pk.gov.pbs.utils.Application;
import pk.gov.pbs.utils.FileManager;
import pk.gov.pbs.utils.SystemUtils;
import pk.gov.pbs.formbuilder.R;
import pk.gov.pbs.utils.UXEvent;
import pk.gov.pbs.utils.location.LocationService;

public class MainMenuActivity extends ThemedCustomActivity {
    FormBuilderRepository mFormBuilderRepo;
    FormRepository mFormRepo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(pk.gov.pbs.tds.R.layout.activity_main_menu);
        setActivityTitle("Poultry Farms | Main Menu", "Main Menu");

        mFormBuilderRepo = FormBuilderRepository.getInstance(getApplication());

//        LoginPayload payload = CustomApplication.getLoginPayload();
//        if (payload == null) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finishAffinity();
//        }

        mFormRepo = FormRepository.getInstance(getApplication());
        /////////////////////////////////////////////////////

        if (!FileManager.hasAllPermissions(this))
            FileManager.requestAllPermissions(this);

        if (!LocationService.hasAllPermissions(this)) {
            if (!LocationService.hasRequiredPermissions(this))
                LocationService.requestRequiredPermissions(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!LocationService.hasPermissionBackgroundAccess(this))
                    LocationService.requestPermissionBackgroundAccess(this);
            }
        }

        if (!SyncService.isRunning()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, SyncService.class));
            } else
                startService(new Intent(this, SyncService.class));
        }

        //in older version this configuration setting is not available, so setting it to english by default
//        if (getConfigurations().LANGUAGE == null || getConfigurations().LANGUAGE.isEmpty()){
//            CustomApplication.updateConfigurations(conf -> {
//                conf.LANGUAGE = "en";
//            });
//        }

        initButton();
        ((TextView) findViewById(pk.gov.pbs.tds.R.id.copyright_version))
                .setText(CustomApplication.getApplicationVersion());
    }

    private void initButton(){
//        if (!Constants.DEBUG_MODE)
        findViewById(pk.gov.pbs.tds.R.id.tv_debug_notice).setVisibility(View.GONE);


        (findViewById(pk.gov.pbs.tds.R.id.btnStartSurvey)).setOnClickListener((View view)->{
//            Intent intent = new Intent(this, FormsListingActivity.class);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        (findViewById(pk.gov.pbs.tds.R.id.btnHousehold)).setOnClickListener((View view)->{
            Intent intent = new Intent(this, BlockStatusActivity.class);
            startActivity(intent);
        });

        (findViewById(pk.gov.pbs.tds.R.id.btnSync)).setOnClickListener((View view)->{

//            List<Map<String,List<?>>> data = DataProvider.getUnsyncedClosedForms(
//                    mFormRepo
//            );
//
//            assert data != null;
//            int l=data.size();


//            Intent i = new Intent(this, SyncService.SyncBroadcastReceiver.class);
//            i.setAction(SyncService.BROADCAST_ACTION_HOUSEHOLD_COMPLETED);
//            i.putExtra(SyncService.BROADCAST_EXTRA_HOUSEHOLD, "1111111111@123");
//            if (i != null) {
//                sendBroadcast(i);
//                Log.d("SyncService", "Broadcast sent to sync service");
//                return;
//            }


//            /**
//             * below script separates cnic and host address from login edittext string
//             * it will be used in login activity
//             * CNIC = 123456789
//             * Host = http://192.168.10.100:2021/lfs/
//             */
//            String userName = "123456789@192.168.10.100 2021 for lfs";
//            if (userName.contains("@")) {
//                String[] parts = userName.split("@");
//                if (parts.length>1) {
//                    String host = parts[1];
//                    host = host.replaceAll("(\\s*(f|F)(o|O)(r|R)\\s*)", "/");
//                    if (host.contains(" "))
//                        host = host.replaceAll("\\s+", ":");
//
//                    String newHost = "http://" + host.toLowerCase() + "/";
//
//                    SharedPreferences sp = Application.getSharedPreferencesManager();
//                    sp.edit().putString("api_host", newHost).apply();
//                }
//                userName = parts[0];
//            }


//            String confTableSql = SqlGenerator.generateSqlFromModels(new Class[]{pk.gov.pbs.tds.meta.Constants.AppConfigurations.class});
//            int l = confTableSql.length();

//            String sql = SqlGenerator.generateSqlFromModels(MetaManifest.getInstance().getModels());
//            int l = sql.length();
//
//            String formBuilder = SqlGenerator.generateSqlFromModels(FormBuilderDatabase.MAIN_MODELS);
//            int fl = formBuilder.length();


            Intent intent = new Intent(this, SyncActivity.class);
            startActivity(intent);
        });

        findViewById(pk.gov.pbs.tds.R.id.btnReportIssue).setOnClickListener((View view)->{
            Intent intent = new Intent(this, SupportCentreActivity.class);
            startActivity(intent);
        });

        (findViewById(pk.gov.pbs.tds.R.id.btnMap)).setOnClickListener((View view) -> {
            Intent intent = new Intent(this, GeoMapActivity.class);
            startActivity(intent);
        });

        (findViewById(pk.gov.pbs.tds.R.id.btnExit)).setOnClickListener((View view)->{
            finishAffinity();
            System.exit(0);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (CustomApplication.isDeveloperMode() && CustomApplication.getLoginPayload().userName.equalsIgnoreCase("1111111111111")){
//            List<String> bCount = mFormRepo.getUtilsDao().getBlockCodes();
//
//            if (bCount == null || bCount.isEmpty())
//                DebugHelper.createDummyBlock(mFormRepo);


        }

        if(FileManager.pathToFile("mainNotice.txt").inInternal().exists()){
            String notice = FileManager.pathToFile("mainNotice.txt").inInternal().read();
            if (!notice.isEmpty()) {
                TextView tvNotice = findViewById(pk.gov.pbs.tds.R.id.tv_notice);
                String[] parts = notice.split("\\|");
                long exipiry = Long.parseLong(parts[0]);
                if (exipiry > SystemUtils.getUnixTs()) {
                    tvNotice.setTextColor(Color.parseColor(parts[1]));
                    tvNotice.setText(Html.fromHtml(parts[2]));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_settings)
                showSettingsDialogue();
        else if (item.getItemId() == R.id.action_sign_out_exit)
                logout();

        return super.onOptionsItemSelected(item);
    }

    private void showSettingsDialogue(){
        SharedPreferences sp = Application.getSharedPreferencesManager();
        int currentThemeIndex = sp.getInt(Constants.Index.SHARED_PREFERENCE_THEME, 0);

        LoginPayload config = CustomApplication.getLoginPayload();
        LayoutInflater mLayoutInflater = LayoutInflater.from(this);
        View dlg = mLayoutInflater.inflate(R.layout.custom_dialogue_alert_settings,null);

        AlertDialog alert = mUXToolkit.getDialogBuilder()
                .setView(dlg)
                .setCancelable(false)
                .setPositiveButton(null, null)
                .setNegativeButton(null, null)
                .create();
        ((TextView) dlg.findViewById(R.id.tv_name)).setText(config.getFullName());
        ((TextView) dlg.findViewById(R.id.tv_cnic)).setText(config.getUserName());
        ((TextView) dlg.findViewById(R.id.tv_desig)).setText(String.valueOf(config.designation).trim());
        ((TextView) dlg.findViewById(R.id.tv_phone)).setText(String.valueOf(config.phoneNumber).trim());
        ((TextView) dlg.findViewById(R.id.tv_whatsapp)).setText(String.valueOf(config.whatsAppNo).trim());
        ((TextView) dlg.findViewById(R.id.tv_email)).setText(String.valueOf(config.email).trim());
        ((TextView) dlg.findViewById(R.id.tv_address)).setText(String.valueOf(config.address).trim());

//        long days = (config.getExpiry() - SystemUtils.getUnixTs()) / (3600 * 24);
//        ((TextView) dlg.findViewById(R.id.tv_login_expiry)).setText(days + " Days");

        // --------------------------- theme spinner ----------------------------------
        Spinner spTheme = dlg.findViewById(R.id.spTheme);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_list_sp, ThemeUtils.themeList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTheme.setAdapter(adapter);
        spTheme.setSelection(currentThemeIndex);

        spTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != currentThemeIndex){
                    sp.edit().putInt(Constants.Index.SHARED_PREFERENCE_THEME, position).apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        // --------------------------- allow manual entry ----------------------------------
//        SwitchCompat swMode = dlg.findViewById(pk.gov.pbs.formbuilder.R.id.swManualMode);
//        swMode.setChecked(CustomApplication.getGlobalInt(Configurations.ALLOW_MANUAL_BALANCE_SHEET_ENTRIES) == 1);

        // --------------------------- use secondary base address ----------------------------------
        SwitchCompat swIP = dlg.findViewById(R.id.swIP);
        LinearLayout containerIP = dlg.findViewById(R.id.container_ip_code);
        EditText etIP = dlg.findViewById(R.id.etIP);
        EditText etCode = dlg.findViewById(R.id.etCode);

        swIP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    containerIP.setVisibility(View.VISIBLE);
                else
                    containerIP.setVisibility(View.GONE);
            }
        });

        if (!pk.gov.pbs.tds.Constants.WEB_API_HOST.equalsIgnoreCase(CustomApplication.getHostWebAPI())) {
            swIP.setChecked(true);
            etIP.setText(CustomApplication.getHostWebAPI());
        }

        //--------------------- language spinner ----------------------------------
//        Spinner spLang = dlg.findViewById(R.id.spLang);
//        ArrayAdapter<String> langAdapter = new ArrayAdapter<String>(this, R.layout.item_list_sp, new String[]{"English Only", "English & Urdu"});
//
//        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spLang.setAdapter(langAdapter);
//        spLang.setSelection(getConfigurations().LANGUAGE.equalsIgnoreCase("en") ? 0 : 1);
//
//        spLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String lang = position == 0 ? "en" : "ur";
//                CustomApplication.updateConfigurations(conf -> {
//                    conf.LANGUAGE = lang;
//                });
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        // ------------------- Save btn ------------------------------------
        dlg.findViewById(R.id.btnClose).setOnClickListener(view -> {

            if(spTheme.getSelectedItemPosition() != currentThemeIndex){
                MainMenuActivity.this.recreate();
            }

//            int manualMode = swMode.isChecked() ? 1 : 0;
//            CustomApplication.getSharedPreferencesManager().edit().putInt(
//                    Configurations.ALLOW_MANUAL_BALANCE_SHEET_ENTRIES, manualMode
//            ).apply();
//            CustomApplication.getSharedPreferencesManager().edit().putInt(
//                    Configurations.ALLOW_MANUAL_ROSTER_INTEGRATION, manualMode
//            ).apply();

            //storing IP
            if (swIP.isChecked()) {
                String webApiHost = etIP.getText().toString();
                String code = etCode.getText().toString();
                String ipMd5 = SystemUtils.MD5(webApiHost, pk.gov.pbs.utils.Constants.INTEGRITY_CHECK_KEY);

                if (code.equals("2247") || code.equals(ipMd5.substring(0, 4)) || (code.isEmpty() && Constants.DEBUG_MODE)) {
                    if (!webApiHost.startsWith("http://"))
                        webApiHost = "http://" + webApiHost;
                    if (!webApiHost.endsWith("/"))
                        webApiHost = webApiHost + "/";

                    String finalWebApiHost = webApiHost;
                    CustomApplication.updateConfigurations(conf -> {
                        conf.WEB_API_HOST = finalWebApiHost;
                    });

                    getUXToolkit().toast("Web API Host changed!");
                } else {
                    if (!webApiHost.equalsIgnoreCase(CustomApplication.getHostWebAPI()))
                        getUXToolkit().toast("Invalid Code, Web API Host not changed!");
                }
            } else {
                if (!CustomApplication.getHostWebAPI().equalsIgnoreCase(pk.gov.pbs.tds.Constants.WEB_API_HOST)) {
                    CustomApplication.updateConfigurations(conf -> {
                        conf.WEB_API_HOST = pk.gov.pbs.tds.Constants.WEB_API_HOST;
                    });
                    getUXToolkit().toast("Web API Host set to primary server!");
                }
            }

            alert.dismiss();
        });

        // ------------------- Debug checking ---------------------------
        dlg.findViewById(R.id.btnClose).setOnLongClickListener(view -> {
            if (CustomApplication.isDeveloperMode())
                mUXToolkit.toast("Practice Mode is ON");
            else
                mUXToolkit.toast("Practice Mode is OFF");
            return false;
        });

        // ==================== Show dialogue =====================
        alert.show();

    }

    private void logout(){
        mUXToolkit.confirm("Sign out", "Are you sure to sign out and exit application?",
                new UXEvent.ConfirmDialogue() {
                    @Override
                    public void onCancel(DialogInterface dialog, int which) {

                    }

                    @Override
                    public void onOK(DialogInterface dialog, int which) {
                        doLogout();
                    }
                });
    }

    private void doLogout(){
        mFormBuilderRepo.getLoginDao().logout();
        CustomApplication.clearLoginPayload();
//        Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
//        startActivity(intent);
        finishAffinity();
    }
}
