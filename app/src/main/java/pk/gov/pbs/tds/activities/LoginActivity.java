package pk.gov.pbs.tds.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.nio.charset.StandardCharsets;

import pk.gov.pbs.formbuilder.core.ThemedCustomActivity;
import pk.gov.pbs.formbuilder.database.FormBuilderRepository;
import pk.gov.pbs.formbuilder.models.LoginPayload;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.R;
import pk.gov.pbs.tds.database.FormRepository;
import pk.gov.pbs.tds.Constants;
import pk.gov.pbs.tds.models.online.LoginRequest;
import pk.gov.pbs.tds.models.online.LoginResponse;
import pk.gov.pbs.tds.models.pojo.Location;
import pk.gov.pbs.utils.ExceptionReporter;
import pk.gov.pbs.utils.StaticUtils;
import pk.gov.pbs.utils.SystemUtils;
import pk.gov.pbs.utils.UXEvent;
import pk.gov.pbs.utils.location.LocationService;
import pk.gov.pbs.utils.web.GsonWebRequest;

public class LoginActivity extends ThemedCustomActivity {
    RequestQueue webRequestQueue;
    FormBuilderRepository mFormBuilderRepo;
    FormRepository mFormRepo;
    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CustomApplication.getLoginPayload() != null) {
            if (CustomApplication.getLoginPayload().getExpiry() > SystemUtils.getUnixTs()) {
                gotoMainMenu();
                return;
            } else
                mUXToolkit.alert("Session Expired", "You have surpassed 180 days of login. Please refresh login state");
        }

        setContentView(R.layout.activity_login);

        try {
            startLocationService(LocationService.Mode.ACTIVE);
        } catch (Exception e) { ExceptionReporter.handle(e);}

        setActivityTitle(R.string.app_name, R.string.app_subtitle);
        ((TextView) findViewById(R.id.copyright_version))
                .setText(CustomApplication.getApplicationVersion());

        init();
        checkLogin();
    }

    private void init() {
//        if (Constants.DEBUG_MODE)
        (findViewById(R.id.tv_debug_notice)).setVisibility(View.GONE);



        webRequestQueue = Volley.newRequestQueue(this);
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);

        if (Constants.DEBUG_MODE) {
            (findViewById(R.id.btnTestLogin)).setOnClickListener(v -> {
                mUXToolkit.confirm("Training Mode", "With training mode, application would login without any user account and with dummy block assignments. You can logout and login with your credentials later.", "OK, I understand", "Go Back",
                        new UXEvent.ConfirmDialogue() {
                            @Override
                            public void onCancel(DialogInterface dialog, int which) {
                            }

                            @Override
                            public void onOK(DialogInterface dialog, int which) {
                                _login();
                            }
                        });
            });
        } else {
            (findViewById(R.id.btnTestLogin)).setVisibility(View.GONE);
            (findViewById(R.id.tv_debug_notice)).setVisibility(View.GONE);
        }
    }

    private void checkLogin() {
        if (CustomApplication.getLoginPayload() != null) {
            if (CustomApplication.getLoginPayload().getExpiry() > SystemUtils.getUnixTs())
                gotoMainMenu();
        }
    }

    private void gotoMainMenu(){
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void login(View v){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        LoginRequest requestBody = new LoginRequest(
                username, password,
                SystemUtils.getDeviceID(this),
                new Location(0D,0D,0D,0f, "manual"),
                String.valueOf(CustomApplication.getApplicationVersionCode())
        );

        if (!validateLoginRequest())
            return;

        verifyCurrentLocation((location)->{
            requestBody.Location =StaticUtils.getGson().toJson(new Location(location));
            makeLoginRequest(requestBody);
        }, 20, true, false);

    }

    private boolean validateLoginRequest(){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (username.length() != 13) {
            etUsername.setError("Please enter valid CNIC");
            return false;
        }

        if (password.isEmpty()) {
            etUsername.setError("Please enter password");
            return false;
        }

        return true;
    }

    private synchronized void makeLoginRequest(LoginRequest request){
        mUXToolkit.showProgressDialogue("Making login request");
        String url = Constants.WEB_API_HOST + "login.php/";

        mUXToolkit.showProgressDialogue("Signing in, please wait...");
        GsonWebRequest<LoginResponse> httpRequest = new GsonWebRequest<>(
                Request.Method.POST
                , url
                , StaticUtils.getGson().toJson(request).getBytes(StandardCharsets.UTF_8)
                , LoginResponse.class
                , response -> {
                    if (response != null && response.status == 1){
                        try {
                            LoginPayload payload = new LoginPayload(
                                    response.CNIC,
                                    response.Name,
                                    response.Designation,
                                    response.GenderId,
                                    SystemUtils.getUnixTs() + 3600 * 24 * 180, //180 day from now
                                    response.Id + "@" + response.token
                                    , CustomApplication.getApplicationVersionCode()
                            );
                            payload.Id = response.Id;
                            payload.fatherName = response.FatherName;
                            payload.email = response.Email;
                            payload.phoneNumber = response.PhoneNumber;
                            payload.address = response.Address;
                            payload.whatsAppNo = response.WhatsAppNo;
                            payload.subject = response.Subject;
                            payload.dateOfBirth = response.DateOfBirth;
                            payload.BPS = response.BPS;

                            doLogin(payload, Constants.getDefaultConfigurations());

                            //-------------Insert annexures if not already inserted------------------
//                            Integer count = mFormBuilderRepo.getAnnexDao().getAnnexCount();
//                            if (count == null || count == 0) {
//                                LoadAnnexures.load(mFormBuilderRepo);
//                            }

                            checkLogin();
                        } catch (Exception e){
                            StaticUtils.getHandler().post(() -> {
                                mUXToolkit.alert("Failed to login, err: " + e.getMessage());
                            });

                            ExceptionReporter.handle(e);
                        }
                    } else if (response != null && response.status == 0){
                        mUXToolkit.alert(
                                "Failed to login"
                                , "Request rejected by server. err: "+response.message+" <br /> " + response.backtrace
                        );
                    } else if (response != null){
                        mUXToolkit.alert (
                                "Failed to login",
                                 "err-code: " + String.valueOf(response.status) + " | "
                                        + "err: " + String.valueOf(response.message) + " | "
                                         + String.valueOf(response.backtrace));
                    }

                    mUXToolkit.dismissProgressDialogue();
                }
                , error -> {
                    mUXToolkit.dismissProgressDialogue();
                    if (
                            error.networkResponse != null &&
                                    error.networkResponse.statusCode == 400
                    ) {
                        mUXToolkit.alert(
                                "Incorrect Username or Password"
                                , "Please check your username and password and try again."
                        );
                    }

                    else if (error.getMessage() != null && error.getMessage().contains("timeout")) {
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
                                "Failed to login",
                                ((error.networkResponse != null) ? "err-code: " + String.valueOf(error.networkResponse.statusCode) + " | " : "")
                                        + "err: " + String.valueOf(error.getMessage())
                        );
                    }
                }
        );
        webRequestQueue.add(httpRequest);
    }
    public void _login(){
        String username = "1111111111111";
        LoginPayload payload = new LoginPayload(
                username,
                "Test User",
                "Developer",
                1,
                SystemUtils.getUnixTs() + 3600 * 24 * 90,
                "dummy token",
                CustomApplication.getApplicationVersionCode()
        );

        Constants.AppConfigurations configs = Constants.getDefaultConfigurations();
        configs.WAIT_TIME_MANUAL_MODE = 10 * 1000;
        configs.WEB_API_HOST = Constants.WEB_API_HOST;
        doLogin(payload, configs);

        
//        //------------------------------
//        DebugHelper.createDummyBlock(mFormRepo);
//        //-------------------------------
//        Integer count = mFormBuilderRepo.getAnnexDao().getAnnexCount();
//        if (count == null || count == 0) {
//            LoadAnnexures.load(mFormBuilderRepo);
//        }

        checkLogin();
    }

    private void doLogin(LoginPayload payload, Constants.AppConfigurations appConfigurations){
        CustomApplication.setLoginPayload(payload);
        CustomApplication.setConfigurations(appConfigurations);
        mFormBuilderRepo = FormBuilderRepository.getInstance(getApplication());
        mFormBuilderRepo.getLoginDao().setLoginPayload(payload);
        mFormRepo = FormRepository.getInstance(getApplication());
    }
}
