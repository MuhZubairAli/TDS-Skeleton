package pk.gov.pbs.tds.models.online;

import java.util.HashMap;

public class SyncResponse {
    public int status;
    public HashMap<String, int[][]> ids;
    public String message;
    public String backtrace;

    public SyncResponse() {
    }

    public SyncResponse(int status, HashMap<String, int[][]> ids, String message, String backtrace) {
        this.status = status;
        this.ids = ids;
        this.message = message;
        this.backtrace = backtrace;
    }
}
