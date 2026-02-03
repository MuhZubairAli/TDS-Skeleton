package pk.gov.pbs.tds.models.online;

public class WebResponse {
    public int status;
    public int[] ids;
    public String message;
    public String backtrace;

    public WebResponse() {
    }

    public WebResponse(int status, int[] ids, String message, String backtrace) {
        this.status = status;
        this.ids = ids;
        this.message = message;
        this.backtrace = backtrace;
    }
}