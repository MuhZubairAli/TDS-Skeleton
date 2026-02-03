package pk.gov.pbs.tds.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayWithIW;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import egolabsapps.basicodemine.offlinemap.Interfaces.GeoPointListener;
import pk.gov.pbs.formbuilder.core.ThemedCustomActivity;
import pk.gov.pbs.formbuilder.database.FormBuilderRepository;
import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.formbuilder.models.FormContext;
import pk.gov.pbs.formbuilder.models.LoginPayload;
import pk.gov.pbs.forms.meta.MetaManifest;
import pk.gov.pbs.geomap.LocationUtils;
import pk.gov.pbs.geomap.views.CustomOfflineMapView;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.R;
import pk.gov.pbs.tds.database.FormRepository;
import pk.gov.pbs.utils.ExceptionReporter;
import pk.gov.pbs.utils.location.ILocationChangeCallback;
import pk.gov.pbs.utils.location.LocationService;
import pk.gov.pbs.tds.databinding.ActivityFormBeginnerBinding;

public class FormBeginnerActivity extends ThemedCustomActivity implements GeoPointListener{
    private static final String TAG = "FormBeginnerActivity";
    ActivityFormBeginnerBinding binding;
    private final double mDefaultZoom = 14.0f;
    private boolean mAutoLocation = true;
    private FormContext mFormContext;
    private final Location mCurrentLocation = new Location(LocationManager.GPS_PROVIDER);
    MapView mapView;
    FormRepository mFormRepository;
    FormBuilderRepository mFormBuilderRepository;
    IMapController mapController;
    ILocationChangeCallback mOnLocationChangedListener;
//    Establishment mAssignment;
    LoginPayload mLoginPayload;
    Marker mCurrentLocationMarker, mTargetHouseholdMarker;
    KmlDocument kmlDocument;
    Polyline mDistanceLine;
    Polygon mAccuracyCircle;
    Button mBtnProceed;
    FloatingActionButton btnLocate, btnBoundary;
    MapEventsReceiver mEventsReceiver;
    MapEventsOverlay mManualLocationOverlay;
    CircularProgressIndicator mLocationWaitIndicator;

    private final BroadcastReceiver LOCATION_BROADCAST = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(LocationService.BROADCAST_ACTION_LOCATION_CHANGED)) {
                Log.d(TAG, "onReceive: Broadcast Received for change of location");
                Location location = intent.getParcelableExtra(LocationService.BROADCAST_EXTRA_LOCATION_DATA);

                if (!isFinishing() && !isDestroyed()) {
                    if (mOnLocationChangedListener != null) {
                        mOnLocationChangedListener.onLocationChange(location);
                    }

                    if (mAutoLocation && LocationUtils.isValidLocation(location)) { // Todo: in case of manual mode, save this somewhere also
                        mCurrentLocation.set(location);
                        updateCoordinatesTextViews(location);
                        updateActionButton();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setting osm configuration before layout is inflated
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()
        ));

        binding = ActivityFormBeginnerBinding.inflate(getLayoutInflater());
        super.setContentView(binding.getRoot());

        try {
            if (!LocationService.isRunning())
                startLocationService(
                        LocationService.Mode.ACTIVE,
                        GeoMapActivity.class
                );
            else
                LocationService.getInstance().setModeActive();
        } catch (Exception e) {
            ExceptionReporter.handle(e);
        }

        mLoginPayload = CustomApplication.getLoginPayload();
        mFormBuilderRepository = FormBuilderRepository.getInstance(getApplication());
        mFormRepository = FormRepository.getInstance(getApplication());

        mFormContext = ((FormContext) getIntent().getSerializableExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT));
//        mAssignment = mFormRepository.getAssignmentDao().getAssignment(mFormContext);

        setActivityTitle("TDS | Locate the Unit and Enumerate", "Locate and Proceed");
        populateInfoTable();
        initFormActions();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationService.BROADCAST_ACTION_LOCATION_CHANGED);
        registerReceiver(LOCATION_BROADCAST, intentFilter);

        initMap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(LOCATION_BROADCAST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
            if (LocationService.isRunning())
                LocationService.getInstance().setModePassive();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
            if (LocationService.isRunning())
                LocationService.getInstance().setModeActive();
        }
    }

    public void initMap() {
//        mLocationWaitIndicator = findViewById(R.id.circularProgressIndicator);
//        int waitTime = CustomApplication.getConfigurations().WAIT_TIME_MANUAL_MODE;
//        int interval = 1000;
//        mLocationWaitIndicator.setMax(waitTime);
//        CountDownTimer countDownTimer = new CountDownTimer(waitTime, interval) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                mLocationWaitIndicator.setProgress((int) (millisUntilFinished - interval));
//                if (LocationService.isRunning() && LocationService.getInstance().getLocation() != null) {
//                    mOnLocationChangedListener.onLocationChange(LocationService.getInstance().getLocation());
//                    mLocationWaitIndicator.setProgress(mLocationWaitIndicator.getMax());
//                    this.cancel();
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                mLocationWaitIndicator.setProgress(0);
//                if (LocationService.isRunning() && LocationService.getInstance().getLocation() == null) {
//                    switchLocationMode(btnLocate);
//                    // allow user to switch location mode after wait time is over
////                    if (CustomApplication.getLoginPayload().isPracticeMode()) {
////                        findViewById(pk.gov.pbs.geomap.R.id.btnLocate)
////                                .setOnLongClickListener(FormBeginnerActivity.this::switchLocationMode);
////                    }
//                }
//            }
//        };
//        countDownTimer.start();
//
//        mapView = findViewById(pk.gov.pbs.geomap.R.id.map);
//        mapView.setMultiTouchControls(true);
//        setMapSource(CustomApplication.getConfigurations().MAP_TILES_PROVIDER);
//        mapController = mapView.getController();
//
//        mapController.setZoom(14f);
//        mapController.animateTo(mAssignment.getPosition(), mDefaultZoom, 500L);
//        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
//
//        //Current location marker
//        if (LocationService.isRunning() && LocationService.getInstance().getLocation() != null) {
//            GeoPoint loc = new GeoPoint(LocationService.getInstance().getLocation());
//            mCurrentLocationMarker = GeoJsonOverlayUtil.addMarker(mapView, loc);
//            mapController.animateTo(loc);
//        } else
//            mapController.animateTo(DebugHelper.getPakistanCenterPoint());
//
//        if (mAssignment.getPosition() != null) {
//            //Target household marker
//            mTargetHouseholdMarker = GeoJsonOverlayUtil.addMarker(mapView, mAssignment.getPosition(), R.drawable.ic_bussiness);
//        }
//
//        //Distance line from current position to target household
//        mDistanceLine = new Polyline();
//        mDistanceLine.setColor(Color.parseColor("#993322DD"));
//        mDistanceLine.setWidth(5f);
//
//        //Attempt to add accuracy mAccuracyCircle on map
//        mAccuracyCircle = new Polygon(mapView);
//        int fillColor = Color.parseColor("#663322DD");
//        mAccuracyCircle.setFillColor(fillColor);
//        mAccuracyCircle.setStrokeColor(Color.BLACK);
//        mAccuracyCircle.setStrokeWidth(3);
//
//        //Initializing map buttons
        initMapActions();
//
//        //Adding GeoJSON layer on map
//        //--------------------------------------------------------
//        String boundary = mAssignment.BoundaryGeoJson;
//        if (boundary != null && !boundary.isEmpty()) {
//            kmlDocument = GeoJsonOverlayUtil.parseGeoJson(boundary);
//            GeoPoint center = GeoJsonOverlayUtil.addKmlDocumentOverlay(mapView, kmlDocument, 0)
//                    .getBounds().getCenterWithDateLine();
//            if ((LocationService.isRunning() && LocationService.getInstance().getLocation() == null) || mCurrentLocation == null)
//                mapController.animateTo(center);
//        }
//
//        //Defining onLocationChangedListener
//        //---------------------------------------------------------
//        mOnLocationChangedListener = (Location mLocation) -> {
//            if (!LocationUtils.isValidLocation(mLocation))
//                return;
//
//            GeoPoint gp = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude(), mLocation.getAltitude());
//            if (mAutoLocation) {
//                //update line from current location to target household
//                updateTargetHouseholdLineAndCurrentLocationMarker(gp);
//
//                // update accuracy circle
//                if (!mapView.getOverlays().contains(mAccuracyCircle))
//                    mapView.getOverlays().add(mAccuracyCircle);
//                mAccuracyCircle.setPoints(Polygon.pointsAsCircle(gp, mLocation.getAccuracy()));
//
//            }
//
//        };

//        StaticUtils.getHandler().postDelayed(()->{
//            if (LocationService.isRunning())
//                verifyCurrentLocation(mOnLocationChangedListener);
//        }, 2000);
    }

    public void onGeoPointRecieved(GeoPoint p) {
        setGeoPointAsCurrentLocation(p);
        updateCoordinatesTextViews(mCurrentLocation);

        updateActionButton();

        //update line between current location and target household
        updateTargetHouseholdLineAndCurrentLocationMarker(p);
        mapController.animateTo(p); //align center of the map to current location
    }

    private void setGeoPointAsCurrentLocation(GeoPoint p){
        //mCurrentLocation.set(new Location(LocationManager.GPS_PROVIDER));
        mCurrentLocation.setLatitude(p.getLatitude());
        mCurrentLocation.setLongitude(p.getLongitude());
        //Todo: this should be based on current zoom level
        mCurrentLocation.setAccuracy(1);
        mCurrentLocation.setProvider(CustomOfflineMapView.LOCATION_PROVIDER_GEO_PICKER);
        mCurrentLocation.setTime(System.currentTimeMillis());
    }

    private String getDistanceStringFromTargetHousehold(){
        return getString(R.string.target_household_distance_title, String.format("%.2f km", mDistanceLine.getDistance()/1000));
    }

    private void updateDistanceBubbleText(OverlayWithIW item){
        if (item == null)
            return;

        item.setTitle(getDistanceStringFromTargetHousehold());
        item.setSubDescription(getString(R.string.target_household_distance_description, String.format("%.2f", mDistanceLine.getDistance())));
    }

    private void updateTargetHouseholdLineAndCurrentLocationMarker(GeoPoint newCurrentLocation){
//        if(kmlDocument != null && !LocationUtils.isPointInPolygon(newCurrentLocation, kmlDocument)) {
//            GeoPoint nearestPoint = LocationUtils.getNearestPointFromBoundary(newCurrentLocation, kmlDocument);
//            mDistanceLine.setPoints(Arrays.asList(
//                    nearestPoint,
//                    newCurrentLocation
//            ));
//            if (!mapView.getOverlays().contains(mDistanceLine))
//                mapView.getOverlays().add(mDistanceLine);
//
//        } else {
//            if (mDistanceLine != null && mAssignment.getPosition() != null) {
//                GeoPoint nearestPoint = mAssignment.getPosition();
//                mDistanceLine.setPoints(Arrays.asList(
//                        nearestPoint,
//                        newCurrentLocation
//                ));
//                if (!mapView.getOverlays().contains(mDistanceLine))
//                    mapView.getOverlays().add(mDistanceLine);
//            }
//        }
//
//        // update current location marker
//        if (mCurrentLocationMarker == null)
//            mCurrentLocationMarker = GeoJsonOverlayUtil.addMarker(mapView, newCurrentLocation);
//        else {
//            // if (LocationUtils.getDistanceBetweenGeoPoints(
//            //         (GeoPoint) mapView.getMapCenter(),
//            //         mCurrentLocationMarker.getPosition()
//            // ) < 5)
//            //     mapController.animateTo(newCurrentLocation);
//
//            mCurrentLocationMarker.setPosition(newCurrentLocation);
//            if (!mapView.getOverlays().contains(mCurrentLocationMarker))
//                mapView.getOverlays().add(mCurrentLocationMarker);
//        }
//
////        double distHouseholdMeters = mDistanceLine.getDistance();
////        String dist = String.format(Locale.getDefault(), "%.2f km", distHouseholdMeters/1000f);
////        setTextViewText(R.id.tv_distance, dist);
//
//        mapController.animateTo(newCurrentLocation);
//
//        updateCoordinatesTextViews(mCurrentLocation);
//        updateDistanceBubbleText(mCurrentLocationMarker);
//        updateDistanceBubbleText(mAccuracyCircle);
//
//        mapView.invalidate();
    }

    private void updateTargetHouseholdLineAndCurrentLocationMarker(Location location){
        updateTargetHouseholdLineAndCurrentLocationMarker(new GeoPoint(location));
    }

    /**
     * This function is only use able if manual location picker is enabled
     * because it depends on accuracy circle polygon
     * @param p GeoPoint to be tested
     * @return true if given point is inside accuracy circle
     */
    private boolean isPointInsideAccuracyCircle(GeoPoint p){
        if (mAccuracyCircle != null) {
            return LocationUtils.isPointInPolygon(p, mAccuracyCircle.getPoints());
        }
        return true;
    }

    /**
     *
     * @param p geopoint to be tested
     * @return if block boundary found then checks if point inside, else always returns true
     */
    private boolean isLocationAcceptable(GeoPoint p){
//        pk.gov.pbs.tds.meta.Constants.AppConfigurations config =
//                CustomApplication.getConfigurations(mAssignment.getEBCode());
//        if (config == null)
//            config = CustomApplication.getConfigurations();
//
//        if (kmlDocument != null) {
//            if(LocationUtils.isPointInPolygon(p, kmlDocument))
//                return true;
//            else {
//                float distMeters = LocationUtils.getAccurateDistanceFromBoundary(p, kmlDocument);
//                double lineLength = 0;
//                if (mDistanceLine != null && mDistanceLine.isVisible())
//                    lineLength = mDistanceLine.getDistance();
//                return distMeters <= config.ALLOWED_DISTANCE_FROM_BOUNDARY ||
//                        (lineLength != 0 && lineLength <= config.ALLOWED_DISTANCE_FROM_HOUSEHOLD);
//            }
//        } else if (mAssignment.getPosition() != null){
//            float distMeters = LocationUtils.getDistanceBetweenGeoPoints(p, mAssignment.getPosition());
//            double lineLength = 0;
//            if (mDistanceLine != null && mDistanceLine.isVisible())
//                lineLength = mDistanceLine.getDistance();
//            return distMeters <= config.ALLOWED_DISTANCE_FROM_HOUSEHOLD ||
//                    (lineLength != 0 && lineLength <= config.ALLOWED_DISTANCE_FROM_HOUSEHOLD);
//        }
//        return kmlDocument == null && mAssignment.getPosition() == null;
        return true;
    }

    private void updateCoordinatesTextViews(Location location) {
//        setTextViewText(R.id.tv_longitude, String.valueOf(location.getLongitude()));
//        setTextViewText(R.id.tv_latitude, String.valueOf(location.getLatitude()));
//        setTextViewText(R.id.tv_accuracy, String.valueOf(location.getAccuracy()));
    }

    private void updateActionButton(){
        if (LocationUtils.isValidLocation(mCurrentLocation)) {
            GeoPoint p = new GeoPoint(mCurrentLocation);
            if (!Constants.CAN_LOCATION_BE_OUTSIDE_ACCURACY_CIRCLE) {
                if(isPointInsideAccuracyCircle(p) && isLocationAcceptable(p))
                    enableProceedButton();
                else
                    disableProceedButton();
            }

            if(isLocationAcceptable(p))
                enableProceedButton();
            else
                disableProceedButton();
        } else {
            if (getLocationService() != null) {
                Location l = getLocationService().getLocation();
                if (LocationUtils.isValidLocation(l)) {
                    mCurrentLocation.set(l);
                    updateActionButton();
                } else
                   disableProceedButton();
            } else {
                disableProceedButton();
            }
        }
    }

    private void enableProceedButton(){
        mBtnProceed.setEnabled(true);
        mBtnProceed.setText("Proceed");
    }

    private void disableProceedButton(){
        mBtnProceed.setEnabled(false);
        mBtnProceed.setText("Location Not Acceptable");
    }

    private void initMapActions(){
        mEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                onGeoPointRecieved(p);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        mManualLocationOverlay = new MapEventsOverlay(mEventsReceiver);

//        btnBoundary.setOnClickListener((view)-> {
//            if (kmlDocument != null) {
//                mapController.animateTo(
//                        kmlDocument.mKmlRoot.mItems.get(0).getBoundingBox().getCenterWithDateLine()
//                );
//            } else
//                mapController.animateTo(mAssignment.getPosition());
//        });

        btnLocate.setOnClickListener((view) -> {
            Location location = getLocationService().getLocation();
            if (!LocationUtils.isValidLocation(location))
                location = mCurrentLocation;
            else
                mCurrentLocation.set(location);

            if (!LocationUtils.isValidLocation(mCurrentLocation))
                return;

            mapController.animateTo(new GeoPoint(location), mDefaultZoom, 1000L);
            updateActionButton();
            updateTargetHouseholdLineAndCurrentLocationMarker(location);
        });

        if (Constants.DEBUG_MODE){
            btnLocate.setOnLongClickListener(v -> {
                switchLocationMode(btnLocate);
                return true;
            });
        }
    }

    private void switchLocationMode(View v){
        mAutoLocation = !mAutoLocation;
        //offlineMapView.setAnimatedLocationPicker(!mAutoLocation, this, mapUtils);
        if (mAutoLocation) {
            btnLocate.setImageDrawable(getDrawable(pk.gov.pbs.geomap.R.drawable.ic_current_location));
            Location location = getLocationService().getLocation();
            if (!LocationUtils.isValidLocation(location))
                location = mCurrentLocation;
            else
                mCurrentLocation.set(location);

            if (location != null) {
                mapController.animateTo(new GeoPoint(location));
                updateActionButton();
            }

            mAccuracyCircle.setVisible(true);
            mapView.getOverlays().remove(mManualLocationOverlay);
            if (mCurrentLocationMarker != null)
                updateTargetHouseholdLineAndCurrentLocationMarker(mCurrentLocationMarker.getPosition());
        } else {
            btnLocate.setImageDrawable(getDrawable(R.drawable.ic_manual_location));

            mBtnProceed.setEnabled(false);
            mAccuracyCircle.setVisible(false);

            mapView.getOverlays().add(mManualLocationOverlay);

            GeoPoint point = kmlDocument == null ?
                    (GeoPoint) mapView.getMapCenter() :
                    kmlDocument.mKmlRoot.mItems.get(0)
                            .getBoundingBox().getCenterWithDateLine();

            mapView.getController().animateTo(point);
            onGeoPointRecieved(point);
        }
        mapView.invalidate();
    }

    private void initFormActions(){
//        if (mAssignment.name != null && !mAssignment.name.isEmpty())
//            binding.etNameFarm.setText(mAssignment.name);
//        if (mAssignment.manager != null && !mAssignment.manager.isEmpty())
//            binding.etNameOwner.setText(mAssignment.manager);
//
        btnLocate = findViewById(pk.gov.pbs.geomap.R.id.btnLocate);
        btnBoundary = findViewById(R.id.btnBoundary);
        mBtnProceed = findViewById(R.id.btnAction);
//
//
//        binding.btnNavigate.setOnClickListener((view)->{
//            if (!isGoogleMapsInstalled()) {
//                mUXToolkit.alert("<b>Google Maps</b> is not installed on this device.");
//                return;
//            }
//
//            if (mAssignment.getPosition() != null) {
//                mUXToolkit.confirm(
//                        "Open Navigation Application",
//                        "Are you sure to open navigation application to reach selected household. Navigation application may require internet to operate.<br /><b>Please note that <u>Google Maps</u> must be installed on this tablet in order to start navigation.</u>",
//                        new UXEvent.ConfirmDialogue() {
//                            @Override
//                            public void onCancel(DialogInterface dialog, int which) {
//                            }
//
//                            @Override
//                            public void onOK(DialogInterface dialog, int which) {
//                                Intent intent = new Intent(Intent.ACTION_VIEW,
//                                        Uri.parse("http://maps.google.com/maps?daddr=" + mAssignment.getPosition().getLatitude() + "," + mAssignment.getPosition().getLongitude()));
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//            } else
//                mUXToolkit.alert("There are no coordinates for this Farm available in database, Please try to find it manually");
//        });


        updateActionButton();

//        ViewGroup etReasonTitle = findViewById(R.id.tv_et_reason_title);
//        EditText etReason = findViewById(R.id.et_action_detail);
//        etReason.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.toString().length() >= 256){
//                    etReason.setText(s.toString().substring(0, 255));
//                    mUXToolkit.toast(pk.gov.pbs.formbuilder.R.string.e15);
//                }
//            }
//        });

//        Spinner spAction = findViewById(R.id.sp_form_status);
//        String[] options = getResources().getStringArray(pk.gov.pbs.formbuilder.R.array.arr_labels_form_begin_actions);
//
//        ArrayAdapter<String> ad = new ArrayAdapter<>(this, pk.gov.pbs.formbuilder.R.layout.item_list_sp, options);
//        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spAction.setAdapter(ad);
//        spAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(position == 0){
//                    etReason.setVisibility(View.GONE);
//                    etReasonTitle.setVisibility(ViewGroup.GONE);
//                }else if(position == 1 || position == 2){
//                    etReason.setVisibility(View.VISIBLE);
//                    etReasonTitle.setVisibility(ViewGroup.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });

        mBtnProceed.setOnClickListener((View view)->{
//            int cc = etReason.getText().length();
//            if(spAction.getSelectedItemPosition() != 0 && cc == 0){
//                mUXToolkit.alert("Please specify reason in order to continue");
//                return;
//            }
//            if (mAutoLocation)
//                verifyCurrentLocation(this::doProceed);
//            else
//                doProceed(mCurrentLocation);

            Intent intent = new Intent(this, MetaManifest.getInstance().getSection(mFormContext.getSection()));
            intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, mFormContext);

            startActivity(intent);
            finish();
        });
    }

    private void doProceed(Location location){
//        if (binding.etNameFarm.getText().toString().isEmpty()) {
//            binding.etNameFarm.requestFocus();
//            binding.etNameFarm.setError("Please enter Farm Name");
//            return;
//        }
//
//        if (binding.etNameOwner.getText().toString().isEmpty()){
//            binding.etNameOwner.requestFocus();
//            binding.etNameOwner.setError("Please enter Owner Name");
//            return;
//        }
//        mFormContext.setSection(MetaManifest.getInstance().getSectionNumberFromClass(S1Activity.class));
//        S1Model s1Model = new S1Model();
//        s1Model.entryStatus = Constants.Status.ENTRY_OPENED;
//        s1Model.pcode = mAssignment.getPCode();
//        s1Model.EBCode = mAssignment.getEBCode();
//        s1Model.srNo = mFormContext.getSecondaryIdentifier();
//        s1Model.farmName = binding.etNameFarm.getText().toString().trim();
//        s1Model.ownerName = binding.etNameOwner.getText().toString().trim();
//        s1Model.district = mAssignment.district;
//        s1Model.tehsil = mAssignment.tehsil;
//        s1Model.province = mAssignment.province;
//        if (mAutoLocation)
//            s1Model.setDeviceLocation(location);
//        else
//            s1Model.setManualLocation(location);
//        s1Model.setFormContext(mFormContext);
//        //Storing persisting form context
//        mFormRepository
//                .getUtilsDao()
//                .setFormContext(mFormContext);
//
//        s1Model.aid = mFormRepository.getDatabase().insert(s1Model);
//
//        if (s1Model.aid != Constants.INVALID_NUMBER) {
//            Intent intent = new Intent(this,
//                    MetaManifest.getInstance().getSection(mFormContext.getSection()));
//            intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, mFormContext);
//            intent.putExtra(Constants.Index.INTENT_EXTRA_SECTION_RESUME_MODEL, s1Model);
//
//            startActivity(intent);
//            finish();
//        } else
//            mUXToolkit.alert("Failed to Save", "System failed to save data, Please try again later");

//        if (spAction.getSelectedItemPosition() == 0) {
//        }
//        else {
//            if ((spAction.getSelectedItemPosition() == 1)) { // form refused
//                s1Model.form_status = Constants.Status.FORM_REFUSED;
//                s1Model.aid = DatabaseUtils.getFutureValue(
//                        mFormRepository.insert(s1Model)
//                );
//                Intent intent = new Intent(this,
//                        MetaManifest.getInstance().getSection(mFormContext.getSection()));
//                intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, mFormContext);
//                intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_STATUS, Constants.Status.FORM_REFUSED);
//                intent.putExtra(Constants.Index.INTENT_EXTRA_SECTION_RESUME_MODEL, s1Model);
//
//                startActivity(intent);
//                finish();
//            } else if (spAction.getSelectedItemPosition() == 2) { // form non contacted
//                mUXToolkit.confirm(getString(pk.gov.pbs.formbuilder.R.string.mark_hh_nc_confirm_msg, s1Model.hhno),
//                        new UXEvent.ConfirmDialogue() {
//                            @Override
//                            public void onCancel(DialogInterface dialog, int which) {
//
//                            }
//
//                            @Override
//                            public void onOK(DialogInterface dialog, int which) {
//                                s1Model.entry_status = Constants.Status.ENTRY_COMPLETED;
//                                s1Model.form_status = Constants.Status.FORM_NON_CONTACTED;
//
//                                s1Model.address = mAssignment.getAddress();
//                                s1Model.hhhcno = mAssignment.getPhoneNumber();
//                                s1Model.remarks = etReason.getText().toString();
//
//                                Long insertID = DatabaseUtils.getFutureValue(
//                                        mFormRepository.insert(s1Model)
//                                );
//
//                                if (insertID != null && insertID != Constants.INVALID_NUMBER) {
//                                    Intent intent = new Intent(FormBeginnerActivity.this, SelectHouseholdActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                } else {
//                                    mUXToolkit.alert("Failed to Save", "System failed to save data, Please try again later");
//                                }
//                            }
//                        });
//            }
//        }
    }

    private void populateInfoTable(){
//        setTextViewText(R.id.tv_pc, getString(pk.gov.pbs.formbuilder.R.string.concat_2_string, "PC-", mAssignment.getPCode()));
//        setTextViewText(R.id.tv_bc, getString(pk.gov.pbs.formbuilder.R.string.concat_2_string, "EB-", mAssignment.getEBCode()));
//        setTextViewText(R.id.tv_hh, String.format(Locale.getDefault(),"SR-%03d",mFormContext.srNo));
//
//        setTextViewText(R.id.tv_enumerator_name, getString(
//                pk.gov.pbs.formbuilder.R.string.concat_2_string_enclosed,
//                mLoginPayload.fullName,
//                mLoginPayload.getUserName()
//        ));
//        setTextViewText(R.id.tv_province, mAssignment.province);
//        setTextViewText(R.id.tv_district, mAssignment.district);
//        setTextViewText(R.id.tv_assign_start_date, mAssignment.getDateBegin());
//        setTextViewText(R.id.tv_assign_end_date, mAssignment.getDateEnd());
    }

    private void setTextViewText(int resource, String data){
        ((TextView) findViewById(resource)).setText(Html.fromHtml(String.valueOf(data)));
    }

    private void setMapSource(String source){
        if (source.contains("Mapnik"))
            mapView.setTileSource(TileSourceFactory.MAPNIK);
        else if (source.contains("Topo"))
            mapView.setTileSource(TileSourceFactory.OpenTopo);
        else if (source.contains("Transport"))
            mapView.setTileSource(TileSourceFactory.PUBLIC_TRANSPORT);
        else if (source.contains("Satellite"))
            mapView.setTileSource(TileSourceFactory.USGS_SAT);
    }

    public boolean isGoogleMapsInstalled() {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}