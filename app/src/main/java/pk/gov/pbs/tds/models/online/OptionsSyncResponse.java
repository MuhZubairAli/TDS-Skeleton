package pk.gov.pbs.tds.models.online;

import java.io.Serializable;
import java.util.List;

public class OptionsSyncResponse implements Serializable {
    public int status;
    public List<SyncedOption> syncedOptions;
    public Integer lastId;
    public String message;
    public String backtrace;
}
