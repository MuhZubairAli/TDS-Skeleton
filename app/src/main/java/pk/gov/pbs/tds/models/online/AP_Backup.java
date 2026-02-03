package pk.gov.pbs.tds.models.online;

public class AP_Backup {
//    [id]
//    ,[oid]
//    ,[version]
//    ,[user_db_name]
//    ,[user_db_json]
//    ,[main_db_name]
//    ,[main_db_json]
//    ,[ts_created]

    public Integer status;
    public Integer id;
    public String oid;
    public String version;
    public String user_db_name;
    public String user_db_json;
    public String main_db_name;
    public String main_db_json;
    public Long ts_created;
    public String message;
    public String backtrace;
}
