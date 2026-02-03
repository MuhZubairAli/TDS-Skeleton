package pk.gov.pbs.tds.models.online;

import pk.gov.pbs.tds.models.pojo.Location;
import pk.gov.pbs.utils.StaticUtils;

public class LoginRequest {
    public String CNIC;
    public String Password;
    public String DeviceId;
    public String Location;
    public String AppVersion;

    public LoginRequest(String username, String password, String deviceID, Location location, String appVersion) {
        this.CNIC = username;
        this.Password = password;
        this.DeviceId = deviceID;
        this.Location = StaticUtils.getGson().toJson(location);
        this.AppVersion = appVersion;
    }
}
