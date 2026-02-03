package pk.gov.pbs.tds;

import java.io.Serializable;
import java.util.Locale;

import pk.gov.pbs.formbuilder.core.LabelProvider;

public class Constants {
    public static final boolean DEBUG_MODE = pk.gov.pbs.utils.Constants.DEBUG_MODE;
    public static final String LAST_ISSUE_REPORT_TIME = "LastIssueReportTime";

    public static final String WEB_API_HOST = "http://lfs.pbos.gov.pk/api_tds/";
    public static final String WEB_API_HOST_SP_KEY = "webApiHost";
    public static final String CONFIGURATIONS_KEY = "configurations";
    public static final String FIRST_RUN_KEY = "firstRun";
    public static final String OPTIONS_LAST_ID_KEY = "optionLastId";

    public static final int MIN_HHH_AGE = 15;
    public static final int PARENT_CHILD_AGE_THRESHOLD = 15;
    public static final int PARENT_GRANDCHILD_AGE_THRESHOLD = 30;
    public static final int MIN_MARRIAGEABLE_AGE = 10;

    public static class AppConfigurations implements Serializable {
        public int MEMBER_COUNT_PER_QR;
        public double ALLOWED_DISTANCE_FROM_BOUNDARY;
        public double ALLOWED_DISTANCE_FROM_HOUSEHOLD;
        public String WEB_API_HOST;
        public String MAP_TILES_PROVIDER;
        public int WAIT_TIME_MANUAL_MODE; //in millis
        public int WAIT_TIME_ISSUE_REPORTS; //in seconds
        public boolean VERIFY_USER_ON_BACKUP_RESTORE;
        public boolean VERIFY_INTEGRITY_ON_BACKUP_RESTORE;
        //Target block code for which configs are intended
        public String TARGET;
        public String LANGUAGE;

        public AppConfigurations(int MEMBER_COUNT_PER_QR, float ALLOWED_DISTANCE_FROM_BOUNDARY, float ALLOWED_DISTANCE_FROM_HOUSEHOLD, String WEB_API_HOST, String MAP_TILES_PROVIDER, int WAIT_TIME_MANUAL_MODE, int WAIT_TIME_ISSUE_REPORTS, boolean VERIFY_USER_ON_BACKUP_RESTORE, boolean VERIFY_INTEGRITY_ON_BACKUP_RESTORE) {
            this.MEMBER_COUNT_PER_QR = MEMBER_COUNT_PER_QR;
            this.ALLOWED_DISTANCE_FROM_BOUNDARY = ALLOWED_DISTANCE_FROM_BOUNDARY;
            this.ALLOWED_DISTANCE_FROM_HOUSEHOLD = ALLOWED_DISTANCE_FROM_HOUSEHOLD;
            this.WEB_API_HOST = WEB_API_HOST;
            this.MAP_TILES_PROVIDER = MAP_TILES_PROVIDER;
            this.WAIT_TIME_MANUAL_MODE = WAIT_TIME_MANUAL_MODE;
            this.WAIT_TIME_ISSUE_REPORTS = WAIT_TIME_ISSUE_REPORTS;
            this.VERIFY_USER_ON_BACKUP_RESTORE = VERIFY_USER_ON_BACKUP_RESTORE;
            this.VERIFY_INTEGRITY_ON_BACKUP_RESTORE = VERIFY_INTEGRITY_ON_BACKUP_RESTORE;
            this.LANGUAGE = "en";
        }

        public AppConfigurations(int MEMBER_COUNT_PER_QR, float ALLOWED_DISTANCE_FROM_BOUNDARY, float ALLOWED_DISTANCE_FROM_HOUSEHOLD, String WEB_API_HOST, String MAP_TILES_PROVIDER, int WAIT_TIME_MANUAL_MODE, int WAIT_TIME_ISSUE_REPORTS) {
            this.MEMBER_COUNT_PER_QR = MEMBER_COUNT_PER_QR;
            this.ALLOWED_DISTANCE_FROM_BOUNDARY = ALLOWED_DISTANCE_FROM_BOUNDARY;
            this.ALLOWED_DISTANCE_FROM_HOUSEHOLD = ALLOWED_DISTANCE_FROM_HOUSEHOLD;
            this.WEB_API_HOST = WEB_API_HOST;
            this.MAP_TILES_PROVIDER = MAP_TILES_PROVIDER;
            this.WAIT_TIME_MANUAL_MODE = WAIT_TIME_MANUAL_MODE;
            this.WAIT_TIME_ISSUE_REPORTS = WAIT_TIME_ISSUE_REPORTS;
            this.VERIFY_USER_ON_BACKUP_RESTORE = true;
            this.VERIFY_INTEGRITY_ON_BACKUP_RESTORE = false;
            this.LANGUAGE = "en";
        }

        public Locale getLocale(){
            return (LANGUAGE != null && LANGUAGE.equals("ur")) ? LabelProvider.PK_LOCALE : LabelProvider.USA_LOCALE;
        }
    }

    public static AppConfigurations getDefaultConfigurations(){
        return new AppConfigurations(
                7,
                5000f, //5 Km
                10000f, //10 Km
                Constants.WEB_API_HOST,
                "Topo",
                30 * 1000, //seconds in 2 mins
                60 * 5, //seconds in 24 hours
                true,
                false
        );
    }
}
