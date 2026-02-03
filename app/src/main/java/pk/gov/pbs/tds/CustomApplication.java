package pk.gov.pbs.tds;

import java.util.Calendar;

import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.formbuilder.models.LoginPayload;
import pk.gov.pbs.tds.services.SyncService;
import pk.gov.pbs.utils.Application;
import pk.gov.pbs.utils.DateTimeUtil;
import pk.gov.pbs.utils.StaticUtils;
import pk.gov.pbs.utils.SystemUtils;

public class CustomApplication extends Application {
    private static pk.gov.pbs.tds.Constants.AppConfigurations sConfigurations;

    @Override
    public void onCreate() {
        super.onCreate();

        SystemUtils.createNotificationChannel(
                this
                , SyncService.Notification_Channel_Name
                , SyncService.Notification_Channel_ID
        );

//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!pk.gov.pbs.utils.Constants.DEBUG_MODE);
//        if (getLoginPayload() != null && getConfigurations() != null) {
//            FirebaseCrashlytics.getInstance().setUserId(getLoginPayload().userName);
//            FirebaseCrashlytics.getInstance().setCustomKey("enum_gender", getLoginPayload().gender);
//            FirebaseCrashlytics.getInstance().setCustomKey("enum_name", getLoginPayload().fullName);
//            FirebaseCrashlytics.getInstance().setCustomKey("enum_username", getLoginPayload().userName);
//            FirebaseCrashlytics.getInstance().setCustomKey("enum_selected_block", String.valueOf(getLoginPayload().selectedBlock));
//            FirebaseCrashlytics.getInstance().setCustomKey("version_code", String.valueOf(getApplicationVersionCode()));
//            FirebaseCrashlytics.getInstance().setCustomKey("login_version_code", String.valueOf(getLoginPayload().appVersion));
//            FirebaseCrashlytics.getInstance().setCustomKey("version", getApplicationVersion());
//            FirebaseCrashlytics.getInstance().setCustomKey("host", getConfigurations().WEB_API_HOST);
//            FirebaseCrashlytics.getInstance().setCustomKey("lang", getConfigurations().LANGUAGE);
//            FirebaseCrashlytics.getInstance().setCustomKey("contact_number", getGlobalString("PhoneNumber_"+CustomApplication.getLoginPayload().userName, "N/A"));
//        }

    }

    public static String getApplicationVersion(){
        return getInstance().getString(pk.gov.pbs.tds.R.string.app_version)
                .replace("(-)","("+ DateTimeUtil.getCalendar().get(Calendar.YEAR)+")");
    }

    public static int getApplicationVersionCode(){
        return Integer.parseInt(
                getInstance().getString(R.string.app_version_code)
        );
    }

//    public static void CrashlyticsLog(String message){
//        FirebaseCrashlytics.getInstance().log(message);
//    }

    public static LoginPayload getLoginPayload(){
        String payload = getSharedPreferencesManager().getString(Constants.Index.SHARED_PREFERENCE_LOGIN_PAYLOAD, "");
        if (!payload.isEmpty()){
            return StaticUtils.getSimpleGson().fromJson(payload, LoginPayload.class);
        }
        return LoginPayload.getDummyPayload();
    }

    public static String getUsername(){
        return getLoginPayload() != null ? getLoginPayload().getUserName() : "";
    }

    public static Boolean isDeveloperMode(){
        return pk.gov.pbs.utils.Constants.DEBUG_MODE;
    }

    public static void setLoginPayload(LoginPayload loginPayload){
        getSharedPreferencesManager()
                .edit()
                .putString(
                        Constants.Index.SHARED_PREFERENCE_LOGIN_PAYLOAD,
                        StaticUtils.getSimpleGson().toJson(loginPayload)
                ).apply();
    }

    public static void setConfigurations(pk.gov.pbs.tds.Constants.AppConfigurations configurations){
        if (configurations.TARGET != null && !configurations.TARGET.isEmpty()) {
            setConfigurations(configurations.TARGET, configurations);
            return;
        }

        sConfigurations = configurations;
        getSharedPreferencesManager()
                .edit()
                .putString(
                        pk.gov.pbs.tds.Constants.CONFIGURATIONS_KEY,
                        StaticUtils.getSimpleGson().toJson(configurations)
                ).apply();
    }
    public static void setConfigurations(String target, pk.gov.pbs.tds.Constants.AppConfigurations configurations){
        getSharedPreferencesManager()
                .edit()
                .putString(
                        target,
                        StaticUtils.getSimpleGson().toJson(configurations)
                ).apply();
    }

    public static pk.gov.pbs.tds.Constants.AppConfigurations getConfigurations(){
        if (sConfigurations != null)
            return sConfigurations;

        String config = getSharedPreferencesManager()
                .getString(pk.gov.pbs.tds.Constants.CONFIGURATIONS_KEY, "");
        if (!config.isEmpty()) {
            sConfigurations = StaticUtils.getGson()
                    .fromJson(config, pk.gov.pbs.tds.Constants.AppConfigurations.class);
        } else
            sConfigurations = pk.gov.pbs.tds.Constants.getDefaultConfigurations();

        return sConfigurations;
    }

    public static pk.gov.pbs.tds.Constants.AppConfigurations getConfigurations(String target){
        String config = getSharedPreferencesManager()
                .getString(target, "");
        if (!config.isEmpty()) {
            return StaticUtils.getGson()
                    .fromJson(config, pk.gov.pbs.tds.Constants.AppConfigurations.class);
        }
        return null;
    }

    public static void updateConfigurations(UpdateConfiguration updateConfiguration){
        pk.gov.pbs.tds.Constants.AppConfigurations configurations = getConfigurations();
        updateConfiguration.update(configurations);
        setConfigurations(configurations);
    }

    public static void clearLoginPayload(){
        getSharedPreferencesManager()
                .edit()
                .remove(Constants.Index.SHARED_PREFERENCE_LOGIN_PAYLOAD).apply();
    }

    public static String getHostWebAPI() {
        return getConfigurations().WEB_API_HOST;
    }

    public static String makeWebApiUrl(String path) {
        return getHostWebAPI() + path + "/";
    }

    public interface UpdateConfiguration {
        void update(pk.gov.pbs.tds.Constants.AppConfigurations conf);
    }
}
