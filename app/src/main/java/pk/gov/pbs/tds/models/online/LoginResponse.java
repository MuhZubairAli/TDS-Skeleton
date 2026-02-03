package pk.gov.pbs.tds.models.online;

import java.io.Serializable;

public class LoginResponse implements Serializable {


//    {
//        "status": 1,
//            "Id": "1017",
//            "Name": "Dr  Naveed Iqbal",
//            "FatherName": "",
//            "GenderId": "1",
//            "Email": "admin1@pbs.gov.pk",
//            "CNIC": "3840321464425",
//            "PhoneNumber": "03008701902",
//            "Address": "PBS",
//            "WhatsAppNo": "03008701902",
//            "Designation": "DEPUTY DIRECTOR ",
//            "Subject": "",
//            "DateOfBirth": "2000-12-22",
//            "BPS": "19",
//            "message": "OK",
//            "backtrace": ""
//    }
//

    public Integer status;
    public Integer Id;
    public String Name;
    public String FatherName;
    public Integer GenderId;
    public String Email;
    public String CNIC;
    public String PhoneNumber;
    public String Address;
    public String WhatsAppNo;
    public String Designation;
    public String Subject;
    public String DateOfBirth;
    public String BPS;
    public String message;
    public String backtrace;
    public String token;

    public LoginResponse(Integer status, Integer id, String name, String fatherName, Integer genderId, String email, String CNIC, String phoneNumber, String address, String whatsAppNo, String designation, String subject, String dateOfBirth, String BPS, String message, String backtrace, String token) {
        this.status = status;
        Id = id;
        Name = name;
        FatherName = fatherName;
        GenderId = genderId;
        Email = email;
        this.CNIC = CNIC;
        PhoneNumber = phoneNumber;
        Address = address;
        WhatsAppNo = whatsAppNo;
        Designation = designation;
        Subject = subject;
        DateOfBirth = dateOfBirth;
        this.BPS = BPS;
        this.message = message;
        this.backtrace = backtrace;
        this.token = token;
    }
}
