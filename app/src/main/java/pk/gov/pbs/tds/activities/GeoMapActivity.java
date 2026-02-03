package pk.gov.pbs.tds.activities;

import static pk.gov.pbs.geomap.GeoJsonOverlayUtil.addKmlDocumentOverlay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pk.gov.pbs.formbuilder.core.ThemedCustomActivity;
import pk.gov.pbs.formbuilder.database.FormBuilderRepository;
import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.geomap.LocationUtils;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.util.DebugHelper;
import pk.gov.pbs.tds.R;
import pk.gov.pbs.tds.database.FormRepository;
import pk.gov.pbs.utils.StaticUtils;
import pk.gov.pbs.utils.ExceptionReporter;
import pk.gov.pbs.utils.location.ILocationChangeCallback;
import pk.gov.pbs.utils.location.LocationService;

public class GeoMapActivity extends ThemedCustomActivity {
    private static final String TAG = "GeoMapActivity";
    private final double mDefaultZoom = 16.;
    private HashMap<String, RadiusMarkerClusterer> mMarkersCache;
    private RadiusMarkerClusterer mCurrentMarkersOverlay;
    private FormRepository mRepository;
    private FormBuilderRepository mFormBuilderRepository;
    private MapView mapView;
    private IMapController mapController;
    private ILocationChangeCallback callback;
    private FolderOverlay blockBoundary;
    private KmlDocument blockBoundaryKml;
    private Polyline mDistanceLine;
    private Marker mCurrentLocationMarker;
    private List<GeoPoint> mDistanceLinePoints;
    private Location mCurrentLocation;
    private TextView tvDistanceLabel;
    private TextView tvMapSource;

    public final BroadcastReceiver GPS_LOCATION_PROVIDER = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(LocationService.BROADCAST_ACTION_LOCATION_CHANGED)) {
                Location location = intent.getParcelableExtra(LocationService.BROADCAST_EXTRA_LOCATION_DATA);
                mUXToolkit.toast("Location Received! " + location.toString());
                if (LocationUtils.isValidLocation(location) && !isFinishing() && !isDestroyed() && callback != null)
                    callback.onLocationChange(location);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideSystemControls();
        super.onCreate(savedInstanceState);

        //setting osm configuration before layout is inflated
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()
        ));

        setContentView(R.layout.activity_geo_map);

        try {
            startLocationService(LocationService.Mode.ACTIVE, GeoMapActivity.class);
        } catch (Exception e) { ExceptionReporter.handle(e);}

        init();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationService.BROADCAST_ACTION_LOCATION_CHANGED);
        registerReceiver(GPS_LOCATION_PROVIDER, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(GPS_LOCATION_PROVIDER);
        if(LocationService.isRunning())
            LocationService.getInstance().setModePassive();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    private void init(){
        mFormBuilderRepository = FormBuilderRepository.getInstance(getApplication());
        mRepository = FormRepository.getInstance(getApplication());

        tvDistanceLabel = findViewById(R.id.tvDistance);
        tvMapSource = findViewById(R.id.tv_map_source);
        registerForContextMenu(tvMapSource);

        mapView = findViewById(R.id.onlineMap);
        mapView.setMultiTouchControls(true);
        mapView.setExpectedCenter(new GeoPoint(30.375321D, 69.345116D));

        updateMapSource(CustomApplication.getConfigurations().MAP_TILES_PROVIDER); //default map source
        initMap();

        mMarkersCache = new HashMap<>();
        mDistanceLinePoints = new ArrayList<>();
//
//        List<String> blockCodes = mRepository.getUtilsDao().getActiveBlockCodes();
//        ArrayList<String> bco = new ArrayList<>();
//        bco.add("Select Block Code");
//        if (blockCodes != null)
//            bco.addAll(blockCodes);
//
//        Spinner sp = findViewById(R.id.spi);
//        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bco);
//        ad.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
//        sp.setAdapter(ad);
//        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0 || mapView == null)
//                    return;
//                String ebcode = parent.getSelectedItem().toString().substring(3);
//                LoginPayload loginPayload = CustomApplication.getLoginPayload();
//                if (loginPayload != null) {
//                    if (loginPayload.getSelectedBlock() == null || !ebcode.equalsIgnoreCase(loginPayload.getSelectedBlock())) {
//                        loginPayload.setSelectedBlock(ebcode);
//                        mFormBuilderRepository.getLoginDao().setLoginPayload(loginPayload);
//                        CustomApplication.setLoginPayload(loginPayload);
//                    }
//                }
//
//                if (mCurrentMarkersOverlay != null)
//                    mapView.getOverlays().remove(mCurrentMarkersOverlay);
//
//                if (blockBoundary != null)
//                    mapView.getOverlays().remove(blockBoundary);
//
//                mapView.invalidate();
//                if (!mMarkersCache.containsKey(ebcode)) {
//                    mCurrentMarkersOverlay = new RadiusMarkerClusterer(GeoMapActivity.this);
//                    Bitmap clusterIcon = BonusPackHelper.getBitmapFromVectorDrawable(GeoMapActivity.this, org.osmdroid.bonuspack.R.drawable.marker_cluster);
//                    mCurrentMarkersOverlay.setIcon(clusterIcon);
//
//                    List<S1Model> poultryFarms = new ArrayList<>();
//                    List<Establishment> establishments = mRepository.getDatabase().query(Establishment.class, "EBCode=?", ebcode);
//
//                    for (Establishment estb : establishments){
//                        S1Model s1 = mRepository.getDatabase().querySingle(S1Model.class, "ebcode=? and srno=?", ebcode, String.valueOf(estb.id));
//                        if (s1 != null) {
//                            poultryFarms.add(s1);
//                        } else if (estb.getPosition() != null){
//                            s1 = new S1Model();
//                            s1.EBCode = estb.EBCode;
//                            s1.pcode = estb.PCode;
//                            s1.farmName = estb.name;
//                            s1.ownerName = estb.manager;
//                            s1.phone = estb.contactno;
//                            s1.email = estb.email;
//                            s1.website = estb.website;
//                            s1.province = estb.province;
//                            s1.district = estb.district;
//                            s1.address = estb.address;
//                            s1.srNo = estb.id;
//
//                            s1.setDeviceLocation(new pk.gov.pbs.tds.models.pojo.Location(
//                                    estb.getPosition(), 14f
//                            ));
//
//                            if (estb.record_type != null)
//                                s1.entryStatus = Integer.parseInt(estb.record_type);
//                            else
//                                s1.entryStatus = null;
//
//                            poultryFarms.add(s1);
//                        }
//                    }
//
//                    for (S1Model farm : poultryFarms) {
//                        Marker marker = new Marker(mapView);
//                        marker.setPosition(farm.getPosition());
//
//                        HouseholdInformationWindow window = new HouseholdInformationWindow(mapView, farm, GeoMapActivity.this);
//                        marker.setIcon(getDrawable(R.drawable.ic_bussiness));
//                        marker.setInfoWindow(window);
//                        mCurrentMarkersOverlay.add(marker);
//                    }
//
//                    mMarkersCache.put(ebcode, mCurrentMarkersOverlay);
//                } else
//                    mCurrentMarkersOverlay = mMarkersCache.get(ebcode);
//
//                Establishment assignment = mRepository.getAssignmentDao().getAssignment(ebcode);
//                if (assignment != null) {
//                    String blockGeoJson = assignment.BoundaryGeoJson;
//                    if (blockGeoJson != null && !blockGeoJson.isEmpty()) {
//                        blockBoundaryKml = parseGeoJson(blockGeoJson);
//                        blockBoundary = addKmlDocumentOverlay(mapView, blockBoundaryKml);
//                        mapView.invalidate();
//                        mapView.getController().animateTo(
//                                blockBoundary.getBounds().getCenterWithDateLine()
//                        );
//                    }
//                }
//
//                if (mCurrentMarkersOverlay != null) {
//                    mapView.getOverlays().add(mCurrentMarkersOverlay);
//                    mapView.invalidate();
//                    StaticUtils.getHandler().post(()-> {
//                        if (!mCurrentMarkersOverlay.getItems().isEmpty())
//                            mapController.animateTo(mCurrentMarkersOverlay.getItems().get(0).getPosition());
//                    });
//                }
//
//                if (mCurrentLocation != null) {
//                    mapView.getOverlays().remove(mDistanceLine);
//                    tvDistanceLabel.setVisibility(View.GONE);
//                    if (blockBoundaryKml != null && !LocationUtils.isPointInPolygon(new GeoPoint(mCurrentLocation), blockBoundaryKml)){
//                        mDistanceLinePoints.clear();
//                        mDistanceLinePoints.add(LocationUtils.getAccurateNearestPointFromBoundary(new GeoPoint(mCurrentLocation), blockBoundaryKml));
//                        mDistanceLinePoints.add(new GeoPoint(mCurrentLocation));
//                        mDistanceLine.setPoints(mDistanceLinePoints);
//                        mapView.getOverlays().add(mDistanceLine);
//                        tvDistanceLabel.setText(String.format("Approximate distance from block: %.2f km", (LocationUtils.getDistanceBetweenGeoPoints(mDistanceLinePoints.get(1), mDistanceLinePoints.get(0)) / 1000.0f)));
//                        tvDistanceLabel.setVisibility(View.VISIBLE);
//                    }
//                    mapView.invalidate();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        // Loading selected block in spinner
//        if(CustomApplication.getLoginPayload().getSelectedBlock() != null){
//            int pos = -1;
//            for (int i = 0; i < bco.size(); i++){
//                if(bco.get(i).substring(3).equalsIgnoreCase(CustomApplication.getLoginPayload().getSelectedBlock())){
//                    pos = i;
//                    break;
//                }
//            }
//            if(pos != -1)
//                sp.setSelection(pos, true);
//        }
    }

    private void initMap() {
        mapController = mapView.getController();
        mapController.setZoom(16f);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mCurrentLocationMarker = new Marker(mapView);
        mCurrentLocationMarker.setIcon(getDrawable(pk.gov.pbs.geomap.R.drawable.ic_location_pin));
        mapView.getOverlays().add(mCurrentLocationMarker);
        mapView.invalidate();

        mDistanceLine = new Polyline();
        mDistanceLine.setColor(Color.parseColor("#993322DD"));
        mDistanceLine.setWidth(5f);
        mapView.getOverlays().add(mDistanceLine);

        //Attempt to add accuracy circle on map
        Polygon circle = new Polygon(mapView);
        int fillColor = Color.parseColor("#663322DD");
        circle.setFillColor(fillColor);
        circle.setStrokeColor(Color.BLACK);
        circle.setStrokeWidth(3);

        mapController.animateTo(DebugHelper.getPakistanCenterPoint());
        //---------------------------------------------------------

        findViewById(pk.gov.pbs.geomap.R.id.btnLocate).setOnClickListener((view) -> {
            if (getLocationService() != null && LocationUtils.isValidLocation(getLocationService().getLocation()))
                mapController.animateTo(new GeoPoint(
                        getLocationService().getLocation().getLatitude(),
                        getLocationService().getLocation().getLongitude()
                ), mDefaultZoom, 1000L);

        });

        findViewById(R.id.btnBoundary).setOnClickListener(v -> {
            if (blockBoundary != null){
                mapController.animateTo(blockBoundary.getBounds().getCenterWithDateLine());
            } else {
                getUXToolkit().toast("Select block first!");
            }
        });

        callback = (Location mLocation) -> {
            mCurrentLocation = mLocation;
            GeoPoint gp = new GeoPoint(mLocation);
            if (LocationUtils.isValidLocation(mLocation)) {
                mCurrentLocationMarker.setPosition(gp);
                mCurrentLocationMarker.setTitle(gp.toDoubleString() + " (Accuracy -> "+mLocation.getAccuracy()+")");
                if (!mapView.getOverlays().contains(mCurrentLocationMarker))
                    mapView.getOverlays().add(mCurrentLocationMarker);

                circle.setPoints(Polygon.pointsAsCircle(gp, mLocation.getAccuracy()));
                if (!mapView.getOverlays().contains(circle))
                    mapView.getOverlays().add(circle);

                if (blockBoundary != null) {
                    mapView.getOverlays().remove(mDistanceLine);
                    if (!LocationUtils.isPointInPolygon(gp, blockBoundaryKml)){
                        mDistanceLinePoints.clear();
                        GeoPoint nearestPoint = LocationUtils.getNearestPointFromBoundary(gp, blockBoundaryKml);
                        if (nearestPoint != null) {
                            mDistanceLinePoints.add(nearestPoint);
                            mDistanceLinePoints.add(gp);
                            mDistanceLine.setPoints(mDistanceLinePoints);
                            mapView.getOverlays().add(mDistanceLine);
                            tvDistanceLabel.setText(String.format("Approximate distance from block: %.2f km", LocationUtils.getDistanceFromBoundary(new GeoPoint(mCurrentLocation), blockBoundaryKml)/1000));
                            tvDistanceLabel.setVisibility(View.VISIBLE);
                        }
                    } else
                        tvDistanceLabel.setText("You're inside the block boundary!");
                }
                mapView.invalidate();
            }
        };
        StaticUtils.getHandler().post(()->verifyCurrentLocation(callback, true, true));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(pk.gov.pbs.geomap.R.menu.menu_map_sources, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item != null)
            updateMapSource(item.getTitle().toString());
        return super.onContextItemSelected(item);
    }

    private void updateMapSource(String source){
        if (source.contains("Mapnik"))
            updateMapSource("Mapnik", TileSourceFactory.MAPNIK);
        else if (source.contains("Topo"))
            updateMapSource("Topo", TileSourceFactory.OpenTopo);
        else if (source.contains("Transport"))
            updateMapSource("Transport",TileSourceFactory.PUBLIC_TRANSPORT);
        else if (source.contains("Satellite"))
            updateMapSource("Satellite", TileSourceFactory.USGS_SAT);
    }

    private void updateMapSource(String source, OnlineTileSourceBase tileSource){
        mapView.setTileSource(tileSource);
        tvMapSource.setText("Map Source: " + source + " " + Constants.Symbols.StartYellow);
        CustomApplication.updateConfigurations(conf -> {
            conf.MAP_TILES_PROVIDER = source;
        });
    }
}