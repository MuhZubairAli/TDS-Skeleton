package pk.gov.pbs.tds.models.online;

public class IssueResponse {
//    "id": "22",
//    "oid": "3330321275123",
//    "subject": "goes to home page",
//    "description": "after completion of non agriculture sheet tab goes to home page and data erases",
//    "type": "Application Crash (Developer)",
//    "section": "11A. Non Agriculture Sheet",
//    "ebcode": "201080217",
//    "hhno": "16 ",
//    "contact": "03055498877",
//    "enum_gender": "1",
//    "version": "3.5.5-beta(2024)",
//    "db_json": "{\"S5DModel\":"

    public Integer status;
    public String message;
    public String backtrace;
    public String id;
    public String oid;
    public String subject;
    public String description;
    public String type;
    public String section;
    public String ebcode;
    public String hhno;
    public String contact;
    public String enum_gender;
    public String version;
    public String db_json;

    public IssueResponse(){}
}
