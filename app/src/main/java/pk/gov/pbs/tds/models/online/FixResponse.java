package pk.gov.pbs.tds.models.online;

public class FixResponse {
//    "id": "1",
//    "oid": "1111111111111",
//    "version": "30000",
//    "title": "Test issue for development",
//    "description": "A test entry created during development of the feature",
//    "requirements": null,
//    "db_fix": null,
//    "conf_fix": "{}",
//    "delivery_status": "0",
//    "ts_created": "1728792090"

    public Integer status;
    public Integer id;
    public String oid;
    public Integer version;
    public String title;
    public String description;
    public String requirements;
    public String db_fix;
    public String conf_fix;
    public Integer delivery_status;
    public String message;
    public String backtrace;
    public Long ts_created;

    public FixResponse(){}
}
