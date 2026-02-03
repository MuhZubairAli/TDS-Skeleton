package pk.gov.pbs.tds.models.pojo;

import org.osmdroid.util.GeoPoint;

import pk.gov.pbs.geomap.views.CustomOfflineMapView;
import pk.gov.pbs.utils.StaticUtils;
import pk.gov.pbs.utils.SystemUtils;

public class Location {
    private final Double latitude;
    private final Double longitude;
    private final Double altitude;
    private final Float accuracy;
    private final String provider;
    private final Long timestamp;

    public Location(String json){
        Location loc = StaticUtils.getGson().fromJson(json, Location.class);
        this.latitude = loc.latitude;
        this.longitude = loc.longitude;
        this.altitude = loc.altitude;
        this.accuracy = loc.accuracy;
        this.provider = loc.provider;
        this.timestamp = loc.timestamp;
    }

    public Location(Double lat, Double lon, Double alt, Float acc, String prov){
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = alt;
        this.accuracy = acc;
        this.provider = prov;
        this.timestamp = SystemUtils.getUnixTs();
    }
    public Location(android.location.Location loc){
        this.latitude = loc.getLatitude();
        this.longitude = loc.getLongitude();
        this.altitude = loc.getAltitude();
        this.accuracy = loc.getAccuracy();
        this.provider = loc.getProvider();
        this.timestamp = loc.getTime();
    }

    public Location(GeoPoint gp, float zoom){
        this.latitude = gp.getLatitude();
        this.longitude = gp.getLongitude();
        this.altitude = gp.getAltitude();
        this.accuracy = zoom;
        this.provider = CustomOfflineMapView.LOCATION_PROVIDER_GEO_PICKER;
        this.timestamp = SystemUtils.getUnixTs();
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public String getProvider() {
        return provider;
    }

    public Long getTime() {
        return timestamp;
    }
}
