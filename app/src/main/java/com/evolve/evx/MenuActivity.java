package com.evolve.evx;
//IMPORTS

//region Imports

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.evolve.evx.EntityObjects.Eloop;
import com.evolve.evx.EntityObjects.Lines;
import com.evolve.evx.EntityObjects.Routes;
import com.evolve.evx.EntityObjects.Terminal;
import com.evolve.evx.EntityObjects.UserMarker;
import com.evolve.evx.EntityObjects.Users;
import com.evolve.evx.Listeners.DatabaseReferenceListeners.AddPassengerCountLabel;
import com.evolve.evx.Listeners.DatabaseReferenceListeners.AddUserMarkers;
import com.evolve.evx.Listeners.DatabaseReferenceListeners.AddVehicleMarkers;
import com.evolve.evx.Listeners.DatabaseReferenceListeners.DriversListener;
import com.evolve.evx.Modules.AdminUsers.AdminUsersFragment;
import com.evolve.evx.Modules.AdminUsers.mySQLGetAdminUsers;
import com.evolve.evx.Modules.DriverUsers.DriverUsersFragment;
import com.evolve.evx.Modules.DriverUsers.mySQLGetDriverUsers;
import com.evolve.evx.Modules.ManageLines.ManageLinesFragment;
import com.evolve.evx.Modules.ManageLines.mySQLLinesDataProvider;
import com.evolve.evx.Modules.ManageRoutes.ManageRoutesFragment;
import com.evolve.evx.Modules.ManageRoutes.mySQLRoutesDataProvider;
import com.evolve.evx.Modules.ManageStations.ManageStationsFragment;
import com.evolve.evx.Modules.ManageStations.mySQLStationProvider;
import com.evolve.evx.Modules.SuperAdminUsers.SuperAdminUsersFragment;
import com.evolve.evx.POJO.HTMLDirections.Directions;
import com.evolve.evx.POJO.HTMLDirections.Setting;
import com.evolve.evx.POJO.HTMLDirections.Settings;
import com.evolve.evx.R.id;
import com.evolve.evx.RouteTabs.Route2;
import com.evolve.evx.RouteTabs.Route3;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.douglasjunior.androidSimpleTooltip.OverlayView;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.evolve.evx.Constants.LOG_TAG;
import static com.evolve.evx.Constants.MY_PERMISSIONS_REQUEST_SEND_SMS;
import static com.evolve.evx.Constants.MY_PERMISSION_REQUEST_LOCATION;

//endregion
public class MenuActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        Route1.OnFragmentInteractionListener,
        Route2.OnFragmentInteractionListener,
        Route3.OnFragmentInteractionListener,
        Html.ImageGetter,
        LocationListener {
    //region STATIC VARIABLES
    //Put here all global variables related to Firebase
    public static FirebaseDatabase _firebaseDB;
    public static DatabaseReference _usersDBRef;
    public static DatabaseReference _terminalsDBRef;
    public static DatabaseReference _driversDBRef;
    public DatabaseReference _vehicle_destinationsDBRef,
            _DriversDatabaseReference,
            _driverRoutesDatabaseReference;

    //Put here all global Collection Variables
    public static List<Terminal> _terminalList;
    public static HashMap<String, Terminal> _distinctTerminalList;
    public static HashMap<String, Marker> _terminalMarkerHashmap = new HashMap<>();
    public static List<Eloop> _eloopList;
    public static List<Eloop> _eloopListFilteredBySignedInAdmin;
    public static ArrayList<Routes> _routeList;
    public static ArrayList<Lines> _lineList;
    public static HashMap<Integer, ArrayList<Terminal>> _HM_GroupedTerminals;
    public static ArrayList<Users> _driverList;
    public static ArrayList<Setting> _AL_applicationSettings;
    public static HashMap _driverMarkerHashmap = new HashMap();

    //Put here all global variables related to sending SMS
    public static PendingIntent _sentSMSPendingIntent;
    public static PendingIntent _deliveredSMSPendingIntent;

    //Put here all global variables for UI Objects
    public static LinearLayout _RoutesPane;
    public static SlidingUpPanelLayout _SlideUpPanelContainer;
    public static TabLayout _RouteTabLayout;
    public static WebView _RouteStepsText;

    public static ScrollView _StepsScroller;
    public static TextView _TimeOfArrivalTextView;
    public static MenuItem _UserNameMenuItem;
    public static NavigationView _NavView;
    public static Menu _MenuNav;
    public static CircleImageView _ProfilePictureImg;
    public static View _NavHeaderView;
    public static TextView _HeaderUserFullName;
    public static TextView _HeaderUserEmail;
    public static AppBarLayout _AppBar;
    public static LinearLayout _MapsHolderLinearLayout, _LL_MapActions;
    public static FloatingActionMenu _AdminToolsFloatingMenu;
    public static ImageView FAB_SammIcon;
    public static FrameLayout FrameSearchBarHolder;
    public static ImageView Search_BackBtn;
    public static TextView _DestinationTextView;
    public static ProgressBar _LoopArrivalProgress;
    public static String _FragmentTitle, _SelectedTerminalMarkerTitle;
    private static TextView placeAutoCompleteFragmentInstance;
    public static LocationManager _locationManager;
    public static Place _placeSearchSelected;

    //Arrival Info elements
    public static LinearLayout _LL_Arrival_Info, _LL_MapStyleHolder;
    public static TextView _TV_Vehicle_Identifier, _TV_Vehicle_Description, _TV_TimeofArrival;
    public static ShimmerLayout _SL_Map_Fragment, _SL_Vehicle_ETA, _SL_AppBar_Top_TextView;

    //Put here other global variables
    public static GoogleApiClient _googleAPI;
    public static LatLng _userCurrentLoc;
    public static GoogleMap _googleMap;
    public static LevelListDrawable _drawable = new LevelListDrawable();
    public static Terminal _selectedPickUpPoint;
    public static ValueAnimator _markerAnimator;
    public static Boolean _isUserLoggingOut = false, _BOOL_IsGoogleMapShownAndAppIsOnHomeScreen = false;
    public static Boolean _isVehicleMarkerRotating = false, _IsPolyLineDrawn = false;
    public static String _smsMessageForGPS;
    public static String _GPSMobileNumber;
    public static Boolean _IsOnSearchMode = false, _BOOL_IsGPSAcquired = false;
    public static Terminal[] _PointsArray;
    public static Typeface FONT_PLATE, FONT_STATION, FONT_RUBIK_REGULAR, FONT_RUBIK_BOLD, FONT_RUBIK_MEDIUM, FONT_ROBOTO_CONDENDSED_BOLD, FONT_RUBIK_BLACK;
    public static int _currentRouteIDSelected;
    public static Integer _currentLineIDSelected;
    public static Boolean _HasExitedInfoLayout = false;
    public static CustomFrameLayout _mapRoot;
    public static Integer _RouteTabSelectedIndex = 0;
    public static TabLayout RouteTabs;
    public static ViewPager viewPager;
    public static Resources _GlobalResource;
    public static CoordinatorLayout.LayoutParams _AppBarLayout;
    public static DrawerLayout _MainDrawerLayout;
    public static View _MAINCONTENT;
    public static Activity _activity;
    public static Context _context;
    public static Boolean _BOOL_IsPlateNumberVisible = false;
    public static HashMap<String, String> _currentRoutesOfEachLoop = new HashMap<>();
    public static ArrayList<Users> _adminUsers = new ArrayList<Users>();
    public static String _currentFragment = "";
    //Declare all fragments here:
    public static ManageLinesFragment _manageLinesFragment = new ManageLinesFragment();
    public static ManageRoutesFragment _manageRoutesFragment = new ManageRoutesFragment();
    public static ManageStationsFragment _manageStationsFragment = new ManageStationsFragment();
    //endregion
    //region NON-STATIC VARIABLES
    public HashMap _userMarkerHashmap = new HashMap();
    private UserMovementBroadcastReceiver _userMovementBroadcastReceiver = new UserMovementBroadcastReceiver();
    public FloatingActionButton _AddGPSFloatingButton, _AddPointFloatingButton, _ViewGPSFloatingButton;
    public Button _BTN_SignUp_NavDrawer;
    private TextView _infoTitleTV, _infoDescriptionTV, _infoDescriptionTV2;
    private LinearLayout _infoLayout;
    private ImageButton _infoPanelBtnClose, _IB_Maptype, _IB_MyLocation, _IB_MapType_Normal,
            _IB_MapType_Satellite, _IB_MapType_Hybrid, _IB_MapType_Terrain;
    private ImageView _infoImage;
    private Animation slide_down, slide_down_bounce, slide_up, slide_up_bounce;
    public Helper _helper;
    public ArrivalHelper _arrivalHelper;
    public Marker _userCurrentLocMarker;
    public LocationRequest _locationRequest;
    public SessionManager _sessionManager;
    public Marker _vehicleMarker;
    public String _facebookImg;
    public Constants _constants;
    public Boolean _allowLogin;
    public Boolean _isAppFirstLoad;
    public int AUTOCOMPLETE_REQUEST_CODE = 1;
    public boolean _InfoPanel_IsEditingEnabled = true;
    private Terminal TM_ClickedTerminal = new Terminal();
    private int _passengerCountInTerminal = 0;
    public final FragmentManager _fragmentManager = getSupportFragmentManager();
    //endregion

    public boolean checkLocationPermission() {
        Log.i(_constants.LOG_TAG, "Checking location permission (checkLocationPermission)");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(MenuActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                _placeSearchSelected = Autocomplete.getPlaceFromIntent(data);
                _DestinationTextView.setText(_constants.DESTINATION_PREFIX + _placeSearchSelected.getName().toString());
                _DestinationTextView.setBackgroundResource(R.color.colorGrassGreen);
                _DestinationTextView.setTextSize(Helper.dpToPx(8, _context));
                _DestinationTextView.setTextColor(getApplication().getResources().getColor(R.color.colorWhite));
                _DestinationTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(_placeSearchSelected.getLatLng(), 16));
                        _userMarkerHashmap.put("destination", _googleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                                .position(_placeSearchSelected.getLatLng())));
                    }
                });
                new asyncProcessSelectedDestination(MenuActivity.this, getApplicationContext(), _terminalList, _placeSearchSelected).execute();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Log.i(_constants.LOG_TAG, "Creating MenuActivity...");
            setTheme(R.style.SplashTheme);
            super.onCreate(savedInstanceState);
            _context = getApplicationContext();
            _activity = MenuActivity.this;
            _helper = new Helper(_activity, this._context);
            _arrivalHelper = new ArrivalHelper(_activity, _context);
            _GlobalResource = getResources();
            _constants = new Constants();

            if (_sessionManager == null)
                _sessionManager = new SessionManager(_context);
            if (!_sessionManager.isLoggedIn()) {
                String username = "";
                if (_sessionManager.getUsername().isEmpty())
                    username = _constants.GUEST_USERNAME_PREFIX + UUID.randomUUID().toString();
                else
                    username = _sessionManager.getUsername();
                _sessionManager.CreateLoginSession(_constants.GUEST_FIRSTNAME, _constants.GUEST_LASTNAME, username, 0, "", "", false, Constants.GUEST_USERTYPE);
            }
            _sessionManager.setKeyEnteredStation("");
            // new asyncCheckInternetConnectivity(MenuActivity.this).execute();
            if (_helper.isOnline(MenuActivity.this, getApplicationContext())) {
                _isAppFirstLoad = true;

                displayLocationSettingsRequest(_context);

                new mySQLRoutesDataProvider(_activity, _context).execute();
                if (_sessionManager.getIsSuperAdmin()) {
                    new mySQLGetAdminUsers(_context).execute();
                    new mySQLLinesDataProvider(_activity, null, _manageLinesFragment, null).execute("0");
                } else if (_sessionManager.getIsAdmin()) {
                    new mySQLLinesDataProvider(_activity, null, _manageLinesFragment, null).execute(_sessionManager.getUserID().toString());
                    new mySQLGetDriverUsers(_context).execute(_sessionManager.getUserID());
                }
                //region EloopList
                new mySQLGetEloopList(_context).execute();
                new asyncGetApplicationSettings(MenuActivity.this, _context, true).execute();
                //endregion


                setContentView(R.layout.activity_menu);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(_constants.WEB_API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RetrofitDatabase service = retrofit.create(RetrofitDatabase.class);
                Call<Settings> call = service.getSettings();
                call.enqueue(new Callback<Settings>() {
                    @Override
                    public void onResponse(final Response<Settings> response, Retrofit retrofit) {
                        try {
                            if (response.body() != null) {
                                if (response.body().getSetting() != null) {
                                    for (Setting setting : response.body().getSetting()) {
                                        if (setting.getName().toLowerCase().equals(_sessionManager.KEY_ISBETA.toLowerCase()))
                                            _sessionManager.setIsBeta(Boolean.valueOf(setting.getValue()));
                                        if (setting.getName().toLowerCase().equals("developerdeviceid")) {
                                            List<String> developerDeviceIds = Arrays.asList(setting.getValue().toLowerCase().split(","));
                                            String androidId = android.provider.Settings.Secure.getString(getContentResolver(),
                                                    android.provider.Settings.Secure.ANDROID_ID).toLowerCase();
                                            if (developerDeviceIds.contains(androidId))
                                                _sessionManager.setIsDeveloper(true);
                                            else
                                                _sessionManager.setIsDeveloper(false);
                                        }


                                    }

                                }
                            }
                        } catch (Exception ex) {
                            Helper.logger(ex, true);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d(_constants.LOG_TAG, t.toString());
                    }
                });

                if (_firebaseDB == null || _usersDBRef == null || _vehicle_destinationsDBRef == null || _driverRoutesDatabaseReference == null) {
                    _firebaseDB = FirebaseDatabase.getInstance();
                    _usersDBRef = _firebaseDB.getReference("users");
                    _terminalsDBRef = _firebaseDB.getReference("terminals");
                    _vehicle_destinationsDBRef = _firebaseDB.getReference("vehicle_destinations");
                    _DriversDatabaseReference = _firebaseDB.getReference("drivers");
                    _driverRoutesDatabaseReference = _firebaseDB.getReference("driversroute");
                }
                if (_driversDBRef == null)
                    _driversDBRef = _firebaseDB.getReference("drivers");

                //Instantiate UI objects~
                _MAINCONTENT = findViewById(id.main_content);
                _AppBar = findViewById(R.id.appBarLayout);
                _AppBarLayout = (CoordinatorLayout.LayoutParams) _AppBar.getLayoutParams();
                _RoutesPane = findViewById(R.id.route_content);
                _SlideUpPanelContainer = findViewById(R.id.sliding_layout);
                _TV_TimeofArrival = findViewById(id.TV_VehicleTimeOfArrival);
                _TV_Vehicle_Identifier = findViewById(id.TV_Vehicle_Identifier);
                _TV_Vehicle_Description = findViewById(id.TV_Vehicle_Description);
                _LL_Arrival_Info = findViewById(id.LL_Arrival_Info);
                _RouteTabLayout = findViewById(R.id.route_tablayout);
                _StepsScroller = findViewById(R.id.step_scroll_view);
                _TimeOfArrivalTextView = findViewById(R.id.toatextview);

                _NavView = findViewById(R.id.nav_view);
                _MenuNav = _NavView.getMenu();
                _UserNameMenuItem = _MenuNav.findItem(R.id.menu_username);
                _NavHeaderView = _NavView.getHeaderView(0);
                _ProfilePictureImg = _NavHeaderView.findViewById(R.id.imgLogo);
                _HeaderUserFullName = _NavHeaderView.findViewById(R.id.HeaderUserFullName);
                _HeaderUserEmail = _NavHeaderView.findViewById(R.id.HeaderUserEmail);
                _UserNameMenuItem.setTitle(_sessionManager.getFullName().toUpperCase());
                _HeaderUserFullName.setText(_sessionManager.getFullName().toUpperCase());
                _ProfilePictureImg.setBackgroundResource(_sessionManager.isFacebook() ? 0 : GetUserNavigationMenuPicture());
                _HeaderUserEmail.setText(_sessionManager.getEmail());
                _AddGPSFloatingButton = findViewById(R.id.subFloatingAddGPS);
                _AddPointFloatingButton = findViewById(R.id.subFloatingAddPoint);
                _ViewGPSFloatingButton = findViewById(R.id.subFloatingViewGPS);
                _AdminToolsFloatingMenu = findViewById(R.id.AdminFloatingActionMenu);
                _IB_Maptype = findViewById(id.IB_mapType);
                _IB_MyLocation = findViewById(id.IB_myLocation);
                InitializeMapStyle();
                _LL_MapStyleHolder = findViewById(id.LL_mapStyleHolder);
                _LL_MapActions = findViewById(id.LL_mapActions);

                //Hide or view nav menus based on user type
                _NavView.getMenu().findItem(id.nav_superadminusers).setVisible(_sessionManager.getIsSuperAdmin());
                _NavView.getMenu().findItem(id.nav_adminusers).setVisible(_sessionManager.getIsSuperAdmin());

                _NavView.getMenu().findItem(id.nav_drivers).setVisible(_sessionManager.getIsAdmin());
                _NavView.getMenu().findItem(id.nav_lines).setVisible(_sessionManager.getIsSuperAdmin() || _sessionManager.getIsAdmin());
                _NavView.getMenu().findItem(R.id.nav_vehicles).setVisible(_sessionManager.getIsAdmin());


                _NavView.getMenu().findItem(R.id.nav_logout).setVisible(!_sessionManager.isGuest());
                _NavView.getMenu().findItem(R.id.nav_login).setVisible(_sessionManager.isGuest());
                _NavView.getMenu().findItem(R.id.nav_vehiclesummary).setVisible(_sessionManager.getIsAdmin());
                _NavView.getMenu().findItem(id.nav_numberofrounds).setVisible(_sessionManager.getIsAdmin());
                _NavView.getMenu().findItem(R.id.nav_vehiclesummary).setVisible(_sessionManager.getIsAdmin());

                FAB_SammIcon = findViewById(R.id.SAMMLogoFAB);
                FrameSearchBarHolder = findViewById(R.id.FrameSearchBarHolder);
                Search_BackBtn = findViewById(id.Search_BackBtn);
                _DestinationTextView = findViewById(id.DestinationTV);
                _LoopArrivalProgress = findViewById(R.id.progressBarLoopArrival);
                _MapsHolderLinearLayout = findViewById(R.id.mapsLinearLayout);
                InitializeInfoPanel();
                InitializeFonts();
                InitializeAnimations();
                MenuActivity.buttonEffect(Search_BackBtn);
                _DestinationTextView.setTypeface(FONT_RUBIK_REGULAR);
                _SlideUpPanelContainer.setTouchEnabled(false);
                _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                _SL_Map_Fragment = findViewById(id.SL_Map_Fragment);
                _SL_Vehicle_ETA = findViewById(id.SL_Vehicle_ETA);
                _SL_AppBar_Top_TextView = findViewById(id.SL_Appbar_Top_TextView);
                _SL_Map_Fragment.startShimmerAnimation();
                RouteTabs = findViewById(R.id.route_tablayout);
                viewPager = findViewById(R.id.routepager);
                _RouteStepsText = findViewById(R.id.route_steps);
                ShowGPSLoadingInfo(_GlobalResource.getString(R.string.GM_acquiring_gps), true);
                _BOOL_IsGoogleMapShownAndAppIsOnHomeScreen = true;
                _MainDrawerLayout = findViewById(id.drawer_layout);
                _BTN_SignUp_NavDrawer = findViewById(id.btn_nav_signup);
                _BTN_SignUp_NavDrawer.setVisibility(_sessionManager.isGuest() ? View.VISIBLE : View.INVISIBLE); //setVisibility(_sessionManager.isGuest());
                if (!Places.isInitialized()) {
                    Places.initialize(getApplicationContext(), _GlobalResource.getString(R.string.google_places_api_key));
                }
                _BTN_SignUp_NavDrawer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent SignUp = new Intent(MenuActivity.this, SignUpActivity.class);
                        SignUp.putExtra("IsFromNavDrawer", "true");
                        startActivity(SignUp);
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                        finish();
                    }
                });
                _MainDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        _MAINCONTENT.setTranslationX(slideOffset * drawerView.getWidth());
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        UpdateUI(Enums.UIType.SHOWING_NAVIGATION_DRAWER);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {

                    }
                });
                FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        _MainDrawerLayout.openDrawer(Gravity.LEFT);
                    }
                });
                Search_BackBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            Marker marker = (Marker) _userMarkerHashmap.get("destination");
                            if (marker != null)
                                marker.remove();
                            ZoomAndAnimateMapCamera(_userCurrentLoc, 15);
                            ArrivalHelper.RemoveListenerFromLoop();
                            _SlideUpPanelContainer.setTouchEnabled(false);
                            _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                            final Handler HND_ZoomGoogleMapToUserLocation = new Handler();
                            final Handler HND_ZoomGoogleMapToUserLocationLateCallback = new Handler();
                            HND_ZoomGoogleMapToUserLocation.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    HideRouteTabsAndSlidingPanel();
                                    if (_sessionManager.getAppRatingStatus() == false) {
                                        TutorialDialog TD_AppRating = new TutorialDialog(MenuActivity.this,
                                                new String[]{_GlobalResource.getString(R.string.TUT_app_rating)}, new String[]{_GlobalResource.getString(R.string.TUT_app_rating_inst)}, new Integer[]{R.drawable.tut_app_rating});
                                        TD_AppRating.show();
                                        TD_AppRating.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialogInterface) {
                                                _sessionManager.TutorialStatus(Enums.UIType.ASK_RATING, true);
                                            }
                                        });
                                    }
                                    HND_ZoomGoogleMapToUserLocationLateCallback.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (_BOOL_IsGoogleMapShownAndAppIsOnHomeScreen) {
                                                _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                                if (_SlideUpPanelContainer.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED) {
                                                    _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                                                }
                                            }
                                        }
                                    }, 3000);
                                }
                            }, 500);

                        } catch (Exception ex) {
                            Helper.logger(ex);
                        }
                    }

                });

                _ProfilePictureImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserProfileEvent();
                        _MainDrawerLayout.closeDrawer(Gravity.START, false);
                    }
                });
                _IB_MyLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (_userCurrentLoc != null) {
                            MenuActivity.buttonEffect(view);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(_userCurrentLoc, 16);
                            _googleMap.animateCamera(cameraUpdate);
                        }
                    }
                });
                _IB_Maptype.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MenuActivity.buttonEffect(view);
                        _LL_MapStyleHolder.setVisibility(_LL_MapStyleHolder.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
                    }
                });
                FrameSearchBarHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RenderSearchAutocomplete();
                    }
                });
                placeAutoCompleteFragmentInstance = findViewById(id.placeAutoCompleteFragmentInstance);
                placeAutoCompleteFragmentInstance.setTextColor(Color.parseColor(_GlobalResource.getString(R.string.PlaceAutoCompleteFragment_FontColor)));
                placeAutoCompleteFragmentInstance.setTypeface(FONT_RUBIK_REGULAR);
                placeAutoCompleteFragmentInstance.setTextSize(18);
                placeAutoCompleteFragmentInstance.setHint(_GlobalResource.getString(R.string.PlaceAutoCompleteFragment_Hint));
                placeAutoCompleteFragmentInstance.setText(null);

                _facebookImg = _GlobalResource.getString(R.string.Facebook_ProfilePic_Graph_URL) + _sessionManager.getUsername().trim() + _GlobalResource.getString(R.string.Facebook_ProfilePic_Graph_URL_QueryString);
                try {
                    FetchFBDPTask dptask = new FetchFBDPTask();
                    dptask.execute();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), _GlobalResource.getString(R.string.Error_NonFacebook_Username), Toast.LENGTH_LONG).show();
                }

                NavigationView navigationView = findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkLocationPermission();
                }

                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(id.map);
                mapFragment.getMapAsync(this);
                _usersDBRef.addChildEventListener(new AddUserMarkers(getApplicationContext(), this));
                _terminalsDBRef.addChildEventListener(new AddPassengerCountLabel(getApplicationContext(), this));
                IntentFilter intentFilter = new IntentFilter(GeofenceTransitionsIntentService.ACTION_MyIntentService);
                intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

                if (_sessionManager.isDriver()) {
                    _allowLogin = false;
                    _usersDBRef.child(_sessionManager.getUsername()).runTransaction(new Transaction.Handler() {

                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("connections") == null) {
                                _allowLogin = true;
                            } else if (dataSnapshot.child("connections").getValue() == null) {
                                _allowLogin = true;
                            } else if (Boolean.valueOf(dataSnapshot.child("connections").getValue().toString()) != true) {
                                _allowLogin = true;
                            }
                            if (_allowLogin) {
                                MenuActivity._TimeOfArrivalTextView.setTypeface(FONT_RUBIK_REGULAR);
                                MenuActivity._TimeOfArrivalTextView.setTextSize(15);
                                MenuActivity._TimeOfArrivalTextView.setGravity(Gravity.LEFT);
                                MenuActivity._TimeOfArrivalTextView.setText("GPS information not yet available");
                                MenuActivity._TimeOfArrivalTextView.setBackgroundResource(R.drawable.pill_shaped_eloop_status_error);
                                _driversDBRef.child(_sessionManager.getKeyDeviceid()).addValueEventListener(new DriversListener(getApplicationContext(), _terminalsDBRef));
                                ShowRouteTabsAndSlidingPanel();
                            } else {

                                _sessionManager.logoutUser();
                                String username = _constants.GUEST_USERNAME_PREFIX + UUID.randomUUID().toString();
                                _sessionManager.CreateLoginSession(_constants.GUEST_FIRSTNAME, _constants.GUEST_LASTNAME, username, 0, "", "", false, Constants.GUEST_USERTYPE);
                                startActivity(getIntent());
                                finish();
                                Toast.makeText(MenuActivity.this, _GlobalResource.getString(R.string.error_concurrent_driver_login), Toast.LENGTH_LONG).show();
                            }
                            initializeOnlinePresence();

                        }
                    });
                } else {
                    initializeOnlinePresence();
                }
                _mapRoot = findViewById(R.id.mapCFL);
            } else {
                _helper.showNoInternetPrompt(MenuActivity.this);
            }

            _fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    FragmentManager.BackStackEntry backStackEntryAt = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
                    _currentFragment = backStackEntryAt.getName();

                }
            });
            _context = getApplicationContext();


        } catch (Exception ex) {
            _helper.logger(ex, true);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(_constants.LOG_TAG, _GlobalResource.getString(R.string.GM_Ready));
        try {
            _googleMap = googleMap;
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                _googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        _LL_MapStyleHolder.setVisibility(View.INVISIBLE);
                    }
                });
                Enums.GoogleMapType mapType = Enums.GoogleMapType.MAP_TYPE_NORMAL;
                try {
                    mapType = Enums.GoogleMapType.valueOf(_sessionManager.getMapStylePreference());
                } catch (Exception ex) {
                    mapType = Enums.GoogleMapType.MAP_TYPE_NORMAL;
                }
                SetMapType(_googleMap, mapType);
                //Initialize Google Play Services
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        buildGoogleApiClient();
                    }
                } else {
                    buildGoogleApiClient();
                }
                if (mapType == Enums.GoogleMapType.MAP_TYPE_NORMAL) {
                    Integer mapsStyle = IsNight() ? R.raw.night_maps_style_darker : R.raw.maps_style;
                    boolean success = _googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, mapsStyle));

                    if (!success) {
                        _helper.logger(_GlobalResource.getString(R.string.GM_Style_Parse_Failed));
                    }
                }
                _driversDBRef.addChildEventListener(new AddVehicleMarkers(getApplicationContext(), this));
                _googleMap.setMyLocationEnabled(true);
                _googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                _googleMap.getUiSettings().setCompassEnabled(false);
                _googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        if (!_IsOnSearchMode && _infoLayout.getVisibility() != View.VISIBLE) {
                        }

                    }
                });
                _googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                    @Override
                    public void onCameraMoveStarted(int reason) {
                        if (reason == REASON_GESTURE) {
                            if (_BOOL_IsGoogleMapShownAndAppIsOnHomeScreen && (!_sessionManager.getIsSuperAdmin() && !_sessionManager.getIsAdmin()) && (MenuActivity._terminalList != null && MenuActivity._terminalList.size() > 0)
                                    && _SlideUpPanelContainer.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED && _BOOL_IsGPSAcquired) {
                                Handler HND_ShowSearchBar = new Handler();
                                HND_ShowSearchBar.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                    }
                                }, 500);
                            }
                        } else if (reason == REASON_API_ANIMATION) {

                        } else if (reason == REASON_DEVELOPER_ANIMATION) {
                        }
                    }

                });
            }
        } catch (Exception ex) {
            Helper.logger(ex);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(_constants.LOG_TAG, _GlobalResource.getString(R.string.G_API_Building));
        try {
            if (_helper.isGooglePlayInstalled(_context)) {
                _googleAPI = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                _googleAPI.connect();
            } else {
                Toast.makeText(_context, _GlobalResource.getString(R.string.G_Play_Services_Not_Installed), Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            _helper.logger(ex);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (!_BOOL_IsGPSAcquired) {
                _BOOL_IsGPSAcquired = true;
                ShowGPSLoadingInfo(_GlobalResource.getString(R.string.GM_gps_acquired), false);

            }
            if (!_isUserLoggingOut) {
                if (!_sessionManager.isDriver()) {
                    if (_userCurrentLocMarker != null) {
                        _userCurrentLocMarker.remove();
                    }
                    //Place current location marker
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();
                    _userCurrentLoc = new LatLng(lat, lng);
                    saveLocation(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title("YOU");
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.samm_user_location_map_icon));
                    markerOptions.position(_userCurrentLoc);

                    _userCurrentLocMarker = _googleMap.addMarker(markerOptions);

                    if (_isAppFirstLoad) {
                        _isAppFirstLoad = false;
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(_userCurrentLoc, 16);
                        _googleMap.animateCamera(cameraUpdate);
                    }

                    //stop location updates
                    if (_googleAPI != null) {
                        LocationServices.FusedLocationApi.removeLocationUpdates(_googleAPI, this);
                    }
                    if (_distinctTerminalList != null)
                        new asyncGeofence(_context).execute(new LatLng(location.getLatitude(), location.getLongitude()));
                }

            }
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void ZoomAndAnimateMapCamera(LatLng LATLNG_var1, int INT_zoomLevel) {
        _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LATLNG_var1, INT_zoomLevel));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void saveLocation(final double lat, final double lng) {
        final HashMap<String, Object> latitude = new HashMap<>();
        final HashMap<String, Object> longitude = new HashMap<>();
        final HashMap<String, Object> hashLastUpdated = new HashMap<>();
        final String lastUpdated = new Date().toString();
        hashLastUpdated.put("lastUpdated", lastUpdated);
        latitude.put("Latitude", lat);
        longitude.put("Longitude", lng);

        Map<String, Object> nodes = new HashMap<>();
        nodes.put(_sessionManager.getUsername() + "/Longitude", lng);
        nodes.put(_sessionManager.getUsername() + "/Latitude", lat);
        nodes.put(_sessionManager.getUsername() + "/lastUpdated", lastUpdated);
        nodes.put(_sessionManager.getUsername() + "/UserType", _sessionManager.getUserType());

        _usersDBRef.updateChildren(nodes);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Log.i(_constants.LOG_TAG, "Google API Client is connected...");
            _locationRequest = new LocationRequest();
            _locationRequest.setInterval(0);
            _locationRequest.setFastestInterval(0);
            _locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (_googleAPI.isConnected()) {
                    new mySQLStationProvider(_context, MenuActivity.this, "", _googleMap, _googleAPI).execute();
                    LocationServices.FusedLocationApi.requestLocationUpdates(_googleAPI, _locationRequest, this);
                    _locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    _googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            try {
                                final String markerTitle = marker.getTitle();
                                Integer markerID = 0;
                                final String markerValue;
                                if (markerTitle != null) {
                                    if (markerTitle.contains("-") && !markerTitle.contains("guestuser")) {
                                        String[] str = markerTitle.split("-");
                                        markerID = Integer.parseInt(str[0]);
                                        markerValue = str[1];
                                    } else {
                                        markerValue = markerTitle;
                                    }

                                    if (_terminalMarkerHashmap.containsKey(markerValue)) {
                                        for (Terminal terminal : _terminalList) {
                                            if (terminal.Value.equals(markerValue)) {
                                                TM_ClickedTerminal = terminal;
                                            }

                                        }
                                        _SelectedTerminalMarkerTitle = TM_ClickedTerminal.getValue();
                                        final Handler HND_Loc_DataFetchDelay = new Handler();
                                        final Handler HND_PassengerCountDelay = new Handler();
                                        final Terminal F_TM_ClickedTerminal = TM_ClickedTerminal;
                                        ShowInfoLayout(TM_ClickedTerminal.LineName + "-" + TM_ClickedTerminal.Description, "Init", _helper.getEmojiByUnicode(0x1F68C) + " : " +
                                                        _GlobalResource.getString(R.string.dialog_fetching_data_with_ellipsis),
                                                R.drawable.ic_ecoloopstop_for_info, Enums.InfoLayoutType.INFO_STATION);
                                        _terminalsDBRef = _firebaseDB.getReference("terminals");

                                        HND_PassengerCountDelay.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                _terminalsDBRef.child(markerValue).runTransaction(new Transaction.Handler() {
                                                    @Override
                                                    public Transaction.Result doTransaction(MutableData currentData) {

                                                        return Transaction.success(currentData);
                                                    }

                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot DS_Terminals) {
                                                        long passengercount = 0;
                                                        passengercount = DS_Terminals.getChildrenCount();
                                                        _passengerCountInTerminal = (int) passengercount;
                                                        UpdateInfoPanelDetails(TM_ClickedTerminal.LineName + "-" + TM_ClickedTerminal.Description,
                                                                _helper.getEmojiByUnicode(0x1F6BB) + " : " + (_passengerCountInTerminal == 0 ? "No passenger" : _passengerCountInTerminal +
                                                                        (_passengerCountInTerminal == 1 ? " passenger" : " passengers")) + " waiting (approx.)", null);
                                                        AttachListenerToTerminalsForPassengerCount();

                                                    }
                                                });
                                            }
                                        }, 1000);
                                        HND_Loc_DataFetchDelay.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                _arrivalHelper.GetGPSDetailsFromFirebase(F_TM_ClickedTerminal, false);
                                            }
                                        }, 2000);
                                    }
                                    //vehicle has been clicked instead
                                    else if (_driverMarkerHashmap.containsKey(markerValue)) {
                                        _SelectedTerminalMarkerTitle = null;
                                        Eloop eloopDetails = Helper.GetEloopEntry(markerValue);
                                        ShowInfoLayout(eloopDetails.PlateNumber,
                                                eloopDetails.DriverName,
                                                null,
                                                R.drawable.e_jeep_universal,
                                                Enums.InfoLayoutType.INFO_VEHICLE);
                                    } else if (markerValue.equals("YOU")) {
                                        _SelectedTerminalMarkerTitle = null;
                                    }
                                    //samm user marker has been clicked
                                    else {
                                        _SelectedTerminalMarkerTitle = null;
                                        String STR_IconGetterFlag = marker.getSnippet() == null ? null : marker.getSnippet();

                                        final UserMarker UM_result = new UserMarker(STR_IconGetterFlag, _context);
                                        Handler HD_FetchFBName = new Handler();

                                        if (UM_result.UserType == Enums.UserType.EVX_FACEBOOK) {
                                            HD_FetchFBName.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    GetFacebookUsername(markerValue, UM_result);
                                                }
                                            }, 1500);
                                        }
                                        ShowInfoLayout(UM_result.UserTitle, UM_result.UserType.toString(), null, UM_result.UserInfoLayoutIcon, Enums.InfoLayoutType.INFO_PERSON);
                                    }
                                }
                            } catch (Exception ex) {
                                _helper.logger(ex);
                            }

                            return true;
                        }
                    });
                }
            } else {

            }
        } catch (Exception ex) {
            Helper.logger(ex);
        }

    }

    private void AttachListenerToTerminalsForPassengerCount() {
        try {

            _terminalsDBRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    CountTerminalPassengersAndUpdateInfoLayout(dataSnapshot, false);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    CountTerminalPassengersAndUpdateInfoLayout(dataSnapshot, false);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    CountTerminalPassengersAndUpdateInfoLayout(dataSnapshot, true);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (Exception ex) {
            Helper.logger(ex);
        }
    }

    private void CountTerminalPassengersAndUpdateInfoLayout(DataSnapshot DS_Terminal, Boolean WasDeleted) {
        try {

            String STR_TerminalFirebaseKey = DS_Terminal.getKey() != null ? DS_Terminal.getKey().toString() : null;
            long passengercount = 0;
            if (STR_TerminalFirebaseKey.equalsIgnoreCase(_SelectedTerminalMarkerTitle)) {
                if (!WasDeleted) {
                    for (DataSnapshot DS_Entry : DS_Terminal.getChildren()) {
                        passengercount++;
                    }
                }
                _passengerCountInTerminal = (int) passengercount;
                UpdateInfoPanelDetails(TM_ClickedTerminal.LineName + "-" + TM_ClickedTerminal.Description,
                        _helper.getEmojiByUnicode(0x1F6BB) + " : " + (_passengerCountInTerminal == 0 ? "No passenger" : _passengerCountInTerminal +
                                (_passengerCountInTerminal == 1 ? " passenger" : " passengers")) + " waiting (approx.)", null);
            }
        } catch (Exception ex) {
            Helper.logger(ex);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (_googleAPI == null) {
                            buildGoogleApiClient();

                        }
                        if (_googleAPI.isConnected())
                            _googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(_GPSMobileNumber, null, this._smsMessageForGPS, _sentSMSPendingIntent, _deliveredSMSPendingIntent);

                    Log.i(_constants.LOG_TAG, this._smsMessageForGPS + " sent");
                    Toast.makeText(this, this._smsMessageForGPS + " sent", Toast.LENGTH_LONG).show();
                } else {
                    _helper.logger("Sending of SMS failed");
                    Toast.makeText(this, "SMS Failed, please try again", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (_currentFragment != "") {
                switch (_currentFragment) {

                    case Constants.FRAGMENTNAME_MANAGELINES:
                        UpdateUI(Enums.UIType.ADMIN_SHOW_MAPS_LINEARLAYOUT);
                        Enums.GoogleMapType mapType = Enums.GoogleMapType.MAP_TYPE_NORMAL;
                        try {
                            mapType = Enums.GoogleMapType.valueOf(_sessionManager.getMapStylePreference());
                        } catch (Exception ex) {
                            Helper.logger(ex);
                        }
                        SetMapType(_googleMap, mapType);

                        return;
                    case Constants.FRAGMENTNAME_MANAGEROUTES:
                        _fragmentManager.beginTransaction().replace(R.id.content_frame, _manageLinesFragment)
                                .addToBackStack(Constants.FRAGMENTNAME_MANAGELINES)
                                .commit();
                        return;
                    case Constants.FRAGMENTNAME_MANAGESTATIONS:
                        _manageStationsFragment.HandleChangesInOrderOfPoints();
                        return;
                }
            } else {
                if (_MainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    _MainDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        } catch (Exception ex) {
            _helper.logger(ex);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                else
                    builder = new AlertDialog.Builder(this);
                builder.setTitle(_GlobalResource.getString(R.string.USER_logout_dialog_title))
                        .setMessage(_GlobalResource.getString(R.string.USER_logout_confirm))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    FacebookSdk.sdkInitialize(MenuActivity.this);
                                    LoginManager.getInstance().logOut();
                                } catch (Exception ex) {
                                    _helper.logger(ex, true);
                                }
                                _helper.DisconnectPreviousUser();
                                _sessionManager.logoutUser();
                                String username = _constants.GUEST_USERNAME_PREFIX + UUID.randomUUID().toString();
                                _sessionManager.CreateLoginSession(_constants.GUEST_FIRSTNAME, _constants.GUEST_LASTNAME, username, 0, "", "", false, Constants.GUEST_USERTYPE);
                                finish();
                                startActivity(getIntent());
                                Toast.makeText(MenuActivity.this, _GlobalResource.getString(R.string.USER_logged_out), Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(R.drawable.ic_logout_white)
                        .show();


            } else if (id == R.id.nav_about) {
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                _fragmentManager.beginTransaction().replace(R.id.content_frame, new AboutActivity()).commit();

            } else if (id == R.id.nav_login) {
                if (_sessionManager.getIsBeta() && !_sessionManager.getIsDeveloper())
                    Toast.makeText(_context, _GlobalResource.getString(R.string.USER_login_not_available_in_beta), Toast.LENGTH_LONG).show();
                else {
                    try {
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        LoginManager.getInstance().logOut();
                        _sessionManager.clearTutorialFlags();
                    } catch (Exception ex) {
                        _helper.logger(ex, true);
                    }

                    Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            } else if (id == R.id.nav_numberofrounds) {
                _sessionManager.PassReportType(_constants.VEHICLE_ROUNDS_REPORT);
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                UpdateUI(Enums.UIType.APPBAR_MIN_HEIGHT);
                _fragmentManager.beginTransaction().replace(R.id.content_frame, new ReportsActivity()).commit();
            } else if (id == R.id.nav_vehiclesummary) {
                _sessionManager.PassReportType(_constants.DISTANCE_SPEED_REPORT);
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                _fragmentManager.beginTransaction().replace(R.id.content_frame, new ReportsActivity()).commit();
            } else if (id == R.id.menu_home) {
                UpdateUI(Enums.UIType.ADMIN_SHOW_MAPS_LINEARLAYOUT);
                Enums.GoogleMapType mapType = Enums.GoogleMapType.MAP_TYPE_NORMAL;
                try {
                    mapType = Enums.GoogleMapType.valueOf(_sessionManager.getMapStylePreference());
                } catch (Exception ex) {
                    Helper.logger(ex);
                }
                SetMapType(_googleMap, mapType);
            } else if (id == R.id.menu_username)
                UserProfileEvent();
            else if (id == R.id.nav_superadminusers) {
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                SuperAdminUsersFragment superAdminUsersFragment = new SuperAdminUsersFragment();
                _fragmentManager.beginTransaction().replace(R.id.content_frame, superAdminUsersFragment).commit();
            } else if (id == R.id.nav_adminusers) {
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                AdminUsersFragment adminUsersFragment = new AdminUsersFragment();
                _fragmentManager.beginTransaction().replace(R.id.content_frame, adminUsersFragment).commit();
            } else if (id == R.id.nav_drivers) {
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                DriverUsersFragment driverUsersFragment = new DriverUsersFragment();
                _fragmentManager.beginTransaction().replace(R.id.content_frame, driverUsersFragment).commit();
            } else if (id == R.id.nav_lines) {
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                if (_sessionManager.getIsAdmin()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("adminUserID", _sessionManager.getUserID());
                    _manageLinesFragment.setArguments(bundle);
                }
                _fragmentManager.beginTransaction().replace(R.id.content_frame, _manageLinesFragment)
                        .addToBackStack(Constants.FRAGMENTNAME_MANAGELINES)
                        .commit();
            } else if (id == R.id.nav_vehicles) {
                try {
                    UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                    _fragmentManager.beginTransaction().replace(R.id.content_frame, new ViewGPSFragment()).commit();
                } catch (Exception ex) {
                    Helper.logger(ex, true);
                }
            }
            _MainDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } catch (Exception ex) {
            Helper.logger(ex, true);

        }
        return true;
    }

    private void displayLocationSettingsRequest(Context context) {
        Log.i(_constants.LOG_TAG, "Requesting to access user location...");
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(_constants.LOG_TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(_constants.LOG_TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            status.startResolutionForResult(MenuActivity.this, _constants.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(_constants.LOG_TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(_constants.LOG_TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public Drawable getDrawable(String arg0) {
        int id = 0;

        if (arg0.equals("ic_walking.png")) {
            id = R.drawable.ic_walking;
        }

        Drawable empty = getResources().getDrawable(id);
        _drawable.addLevel(0, 0, empty);
        _drawable.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

        return _drawable;
    }


    public void ShowLoopTimeofArrival(final LatLng Looploc, final float bearing, final String deviceid) {
        //
        // February 18, 2108 4:00AM (sobrang effective ng REDBULL)
        //Dear Eleaz, sana mabasa mo ito. Thank you sa lahat ng tulong mo dito sa app na ito.
        //Thank you for lifting my spirits up when I'm feeling down
        //Thank you for giving me hope when I'm losing it =)
        //Thank you for staying positive when I'm thinking negative
        //I know you're also pagod and puyat, but thanks for staying pagod and puyat with me. =)
        //Eto na nga, seems like hindi 'to aabot para sa #march2018 goal, pero thank you
        //kasi andyan ka to uplift my emotions.
        //Malungkot ako, kasi I keep on remembering all the hustling and cramming para lang umabot
        //pero still, hindi umabot. Pero nawawala ung lungkot ko kapag iniisip kong dalawa naman tayo dito.
        //Hindi ako nag-iisa =) Diba? =)
        //Now, #march2018 becomes #may2018. PUSH parin tayo baby ha?
        //I am sure this will all be worth it! Gandahan natin ang app. Go go go!!! :*
        //I LOVE YOU!
        try {
            if (_selectedPickUpPoint != null) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(_constants.GOOGLE_API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RetrofitMaps service = retrofit.create(RetrofitMaps.class);
                Call<Directions> call = service.getDistanceDuration(_GlobalResource.getString(R.string.GM_unit), _selectedPickUpPoint.Lat + "," + _selectedPickUpPoint.Lng, Looploc.latitude + "," + Looploc.longitude, _GlobalResource.getString(R.string.GM_mode));
                call.enqueue(new Callback<Directions>() {
                    @Override
                    public void onResponse(Response<Directions> response, Retrofit retrofit) {
                        try {
                            for (int i = 0; i < response.body().getRoutes().size(); i++) {
                                if (_googleMap != null) {
                                    String TimeofArrival = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();
                                    MarkerOptions markerOpt = new MarkerOptions();
                                    LatLng latLng = new LatLng(Looploc.latitude, Looploc.longitude);
                                    markerOpt.position(latLng);
                                    markerOpt.title(TimeofArrival);
                                    markerOpt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ecoloop));
                                    markerOpt.anchor(0.5f, 0.5f);
                                    markerOpt.rotation(bearing);
                                    if (_vehicleMarker == null) {
                                        _vehicleMarker = _googleMap.addMarker(markerOpt);
                                    }
                                    _vehicleMarker.showInfoWindow();
                                    _vehicleMarker.setPosition(latLng);
                                    _TimeOfArrivalTextView.setText(deviceid + " - " + TimeofArrival.toString());
                                }
                            }
                        } catch (Exception e) {
                            _helper.logger(e, true);

                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d(_constants.LOG_TAG, t.toString());
                    }
                });
            }
        } catch (Exception ex) {
            _helper.logger(ex, true);

        }

    }

    @Override
    public void onMapLoaded() {

    }


    public class UserMovementBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "Passenger movement to terminal detected...");
            String eventType = intent.getStringExtra(GeofenceTransitionsIntentService.KEY_EVENT_TYPE);
            String geofenceRequestId = intent.getStringExtra(GeofenceTransitionsIntentService.KEY_GEOFENCEREQUESTID);
            for (Terminal d : _terminalList) {
                if (d.GeofenceId.equals(geofenceRequestId)) {
                    if (!_sessionManager.isDriver() && !_sessionManager.getIsAdmin()) {
                        Toast.makeText(context, "You " + eventType + " " + d.Description, Toast.LENGTH_LONG).show();
                    }

                }

            }
        }

    }

    private void initializeOnlinePresence() {
        Log.i(LOG_TAG, "Initializing online presence...");
        String node = "";
        node = "users/" + _sessionManager.getUsername();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference("users/" + _sessionManager.getUsername());
        final DatabaseReference myConnectionsRef = database.getReference(node + "/connections");
        final DatabaseReference lastOnlineRef = database.getReference("users/" + _sessionManager.getUsername() + "/lastOnline");
        final DatabaseReference connectedRef = database.getReference(".info/connected");


        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    if (_sessionManager.isGuest())
                        userRef.onDisconnect().removeValue();
                    else {
                        myConnectionsRef.onDisconnect().setValue(false);
                        lastOnlineRef.onDisconnect().setValue(DateFormat.getDateTimeInstance().format(new Date()));
                    }
                    myConnectionsRef.setValue(true);


                    if (_sessionManager.isDriver()) {
                        _driverRoutesDatabaseReference.child(_sessionManager.getKeyDeviceid()).onDisconnect().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });
    }


    private class FetchFBDPTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            URL imageURL = null;
            try {
                imageURL = new URL(_facebookImg);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            try {
                bitmap = BitmapFactory.decodeStream(fetch(imageURL.toString()), null, options);
            } catch (OutOfMemoryError e) {
                try {
                    options.inSampleSize = 2;
                    bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    return bitmap;
                } catch (Exception ex) {

                }

            } catch (IOException exc) {

            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                _ProfilePictureImg.setImageBitmap(result);
            }
        }

        private InputStream fetch(String address) throws MalformedURLException, IOException {
            HttpGet httpRequest = new HttpGet(URI.create(address));
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream instream = bufHttpEntity.getContent();
            return instream;
        }
    }

    public static Boolean IsNight() {
        Integer currentTime = Calendar.getInstance().getTime().getHours();
        return (currentTime >= 18 || currentTime <= 5) ? true : false;
    }

    public void BuildToolTip(String text, Activity activity, View view, int gravity, int highlightShape, Boolean hasOverlay, int BGColor, final Enums.TutorialType tutorialType) {
        new SimpleTooltip.Builder(activity)
                .anchorView(view)
                .text(text)
                .gravity(gravity)
                .animated(true)
                .transparentOverlay(hasOverlay)
                .backgroundColor(getApplication().getResources().getColor(BGColor))
                .textColor(getApplication().getResources().getColor(R.color.colorWhite))
                .arrowColor(getApplication().getResources().getColor(BGColor))
                .onDismissListener(new SimpleTooltip.OnDismissListener() {
                    @Override
                    public void onDismiss(SimpleTooltip tooltip) {
                        switch (tutorialType) {
                            case NONE:
                                break;
                            case MAP_LAYER_STYLE:
                                TutorialDialog MapTutorial = new TutorialDialog(MenuActivity.this, new String[]{"Welcome!", "Search", "Select", "Map Style", "My Location"}, new String[]{"Welcome to EVX!\nKnow about the key features of this app.", "Start by searching your desired target destination.", "Pick your destination from the list.", "You can change your map style any way you want!", "Just tap once to show your current location."}, new Integer[]{R.drawable.tut_welcome, R.drawable.tut_searchbar, R.drawable.tut_destinationsuggest, R.drawable.tut_changemaps, R.drawable.tut_mylocation});
                                MapTutorial.show();
                                break;
                            default:
                                break;
                        }
                    }
                })
                .highlightShape(highlightShape)
                .build()
                .show();
    }

    public void ShowGPSLoadingInfo(String STR_message, Boolean IsVisible) {
        int Visibility = IsVisible ? View.VISIBLE : View.INVISIBLE;
        if (!IsVisible) {
            if (_BOOL_IsGoogleMapShownAndAppIsOnHomeScreen && (!_sessionManager.getIsSuperAdmin() && !_sessionManager.getIsAdmin()))
                MenuActivity._SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            final Handler HND_ShowPanel = new Handler();
            HND_ShowPanel.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UpdateUI(Enums.UIType.APPBAR_MIN_HEIGHT);
                    UpdateUI(Enums.UIType.MAIN);
                    _SL_AppBar_Top_TextView.stopShimmerAnimation();
                    new asyncGetApplicationSettings(_activity, _context, true).execute();
                }
            }, 2000);
        } else {
            MenuActivity._BOOL_IsGPSAcquired = false;
            _AppBarLayout.height = _helper.dpToPx(20, _context);
            _AppBar.setLayoutParams(_AppBarLayout);
            _AppBar.setVisibility(Visibility);
            MenuActivity._SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
        _SL_AppBar_Top_TextView.startShimmerAnimation();
        _DestinationTextView.setVisibility(View.VISIBLE);
        _DestinationTextView.setTextColor(getApplication().getResources().getColor(R.color.colorWhite));
        _DestinationTextView.setText(STR_message != null ? STR_message.toUpperCase() : null);
        _DestinationTextView.setBackgroundResource(IsVisible ? R.color.colorElectronBlue : R.color.colorWebFlatGreen);
    }

    public void UpdateUI(Enums.UIType type) {
        try {
            _LL_MapStyleHolder.setVisibility(View.INVISIBLE);
            if (_sessionManager != null && _sessionManager.getMainTutorialStatus() != null) {
                switch (type) {
                    case MAIN:
                        if (!_sessionManager.getMainTutorialStatus() && _BOOL_IsGPSAcquired && _BOOL_IsGoogleMapShownAndAppIsOnHomeScreen) {
                            {
                                if (!_sessionManager.getIsSuperAdmin() && !_sessionManager.getIsAdmin() && !_sessionManager.isDriver()) {
                                    TutorialDialog MapTutorial = new TutorialDialog(MenuActivity.this, new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_welcome_title), MenuActivity._GlobalResource.getString(R.string.TUT_search_title), MenuActivity._GlobalResource.getString(R.string.TUT_select_title), MenuActivity._GlobalResource.getString(R.string.TUT_map_style_title), MenuActivity._GlobalResource.getString(R.string.TUT_my_location_title), MenuActivity._GlobalResource.getString(R.string.TUT_explore_more_title)},
                                            new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_welcome_to_samm_inst), MenuActivity._GlobalResource.getString(R.string.TUT_search_target_destination_inst), MenuActivity._GlobalResource.getString(R.string.TUT_pick_your_destination_inst), MenuActivity._GlobalResource.getString(R.string.TUT_change_map_style_inst), MenuActivity._GlobalResource.getString(R.string.TUT_my_location_inst), MenuActivity._GlobalResource.getString(R.string.TUT_samm_drawer_inst)},
                                            new Integer[]{R.drawable.tut_welcome, R.drawable.tut_searchbar, R.drawable.tut_destinationsuggest, R.drawable.tut_changemaps, R.drawable.tut_mylocation, R.drawable.tut_evxdrawericon});
                                    MapTutorial.show();
                                } else if (_sessionManager.getIsAdmin() || _sessionManager.getIsSuperAdmin()) {
                                    TutorialDialog MapTutorial = new TutorialDialog(MenuActivity.this,
                                            new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_welcome_title),
                                                    MenuActivity._GlobalResource.getString(R.string.TUT_explore_more_title)},
                                            new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_welcome_to_samm_admin_inst) + (_sessionManager.getIsSuperAdmin() ? MenuActivity._GlobalResource.getString(R.string.TUT_welcome_superadmin) : MenuActivity._GlobalResource.getString(R.string.TUT_welcome_admin)), MenuActivity._GlobalResource.getString(R.string.TUT_samm_drawer_inst)},
                                            new Integer[]{R.drawable.tut_welcome, R.drawable.tut_evxdrawericon});
                                    MapTutorial.show();
                                } else if (_sessionManager.isDriver()) {
                                    TutorialDialog MapTutorial = new TutorialDialog(MenuActivity.this,
                                            new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_welcome_title)
                                                    , MenuActivity._GlobalResource.getString(R.string.TUT_driver_slide_up_panel_title)
                                                    , MenuActivity._GlobalResource.getString(R.string.TUT_driver_slide_up_panel_title)
                                                    , MenuActivity._GlobalResource.getString(R.string.TUT_driver_slide_up_panel_title)},
                                            new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_welcome_to_samm_driver_inst)
                                                    , MenuActivity._GlobalResource.getString(R.string.TUT_driver_slideup_panel_inst)
                                                    , MenuActivity._GlobalResource.getString(R.string.TUT_driver_slideup_panel_expanded_inst)
                                                    , MenuActivity._GlobalResource.getString(R.string.TUT_driver_select_routes_inst)},
                                            new Integer[]{R.drawable.tut_welcome,
                                                    R.drawable.tut_driverslideuppanel, R.drawable.tut_driverselectroute, R.drawable.tut_driverrouteselected});
                                    MapTutorial.show();
                                }
                            }
                            _sessionManager.TutorialStatus(Enums.UIType.MAIN, true);
                        }
                        break;
                    case SHOWING_ROUTES:
                        if (!_sessionManager.getRouteTutorialStatus()) {
                            BuildToolTip(_GlobalResource.getString(R.string.ToolTip_search_again), this, Search_BackBtn, Gravity.END, OverlayView.HIGHLIGHT_SHAPE_OVAL, false, R.color.colorElectronBlue, Enums.TutorialType.NONE);
                            BuildToolTip(_GlobalResource.getString(R.string.ToolTip_navigation_instructions), this, _RoutesPane, Gravity.TOP, OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR, false, R.color.colorElectronBlue, Enums.TutorialType.NONE);
                            _sessionManager.TutorialStatus(Enums.UIType.SHOWING_ROUTES, true);
                            _HasExitedInfoLayout = false;
                        }
                        if (_infoLayout.getVisibility() == View.VISIBLE)
                            InfoPanelHide();
                        _HasExitedInfoLayout = false;

                        break;
                    case SHOWING_INFO:
                        FAB_SammIcon.setVisibility(View.INVISIBLE);
                        _LL_MapActions.setVisibility(View.INVISIBLE);
                        break;
                    case HIDE_SEARCH_FRAGMENT_ON_SEARCH:
                        FAB_SammIcon.setVisibility(View.INVISIBLE);
                        break;
                    case HIDE_INFO:
                        FAB_SammIcon.setVisibility(View.VISIBLE);
                        _LL_MapActions.setVisibility(View.VISIBLE);
                        _infoLayout.setVisibility(View.INVISIBLE);
                        _infoPanelBtnClose.setVisibility(View.VISIBLE);
                        break;
                    case HIDE_INFO_SEARCH:
                        Search_BackBtn.setVisibility(View.VISIBLE);
                        _infoLayout.setVisibility(View.INVISIBLE);
                        _infoPanelBtnClose.setVisibility(View.VISIBLE);
                        break;
                    case ADMIN_HIDE_MAPS_LINEARLAYOUT:
                        _MapsHolderLinearLayout.setVisibility(View.GONE);
                        UpdateUI(Enums.UIType.APPBAR_MIN_HEIGHT);
                        _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                        _RoutesPane.setVisibility(View.INVISIBLE);
                        _BOOL_IsGoogleMapShownAndAppIsOnHomeScreen = false;
                        break;
                    case ADMIN_SHOW_MAPS_LINEARLAYOUT:
                        _MapsHolderLinearLayout.setVisibility(View.VISIBLE);
                        _BOOL_IsGoogleMapShownAndAppIsOnHomeScreen = true;
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                        if (!_BOOL_IsGPSAcquired)
                            ShowGPSLoadingInfo(_GlobalResource.getString(R.string.GM_acquiring_gps), true);
                        if (_BOOL_IsGPSAcquired) {
                            new asyncGetApplicationSettings(_activity, _context, true).execute();
                            if (_BOOL_IsGoogleMapShownAndAppIsOnHomeScreen && (!_sessionManager.getIsSuperAdmin() && !_sessionManager.getIsAdmin()) && (MenuActivity._terminalList != null && MenuActivity._terminalList.size() > 0)) {
                                Handler HND_ShowSearchBar = new Handler();
                                HND_ShowSearchBar.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                                        _RoutesPane.setVisibility(View.VISIBLE);
                                    }
                                }, 500);
                            }
                        }
                        break;
                    case APPBAR_MIN_HEIGHT:
                        _AppBarLayout.height = 0;
                        _AppBar.setLayoutParams(_AppBarLayout);
                        break;
                    case SHOWING_NAVIGATION_DRAWER:
                        if (_sessionManager.getNavigationDrawerTutotialStatus() != null && !_sessionManager.getNavigationDrawerTutotialStatus()) {
                            if (_sessionManager.getIsSuperAdmin()) {
                                // admin tools enabled
                                TutorialDialog MapTutorial = new TutorialDialog(MenuActivity.this, new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_explore_more_title), MenuActivity._GlobalResource.getString(R.string.TUT_super_admin_title), MenuActivity._GlobalResource.getString(R.string.TUT_admin_title), MenuActivity._GlobalResource.getString(R.string.TUT_lines_title)},
                                        new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_explore_super_administartor_inst), MenuActivity._GlobalResource.getString(R.string.TUT_superadmin_users_inst), MenuActivity._GlobalResource.getString(R.string.TUT_admin_users_inst)
                                                , MenuActivity._GlobalResource.getString(R.string.TUT_lines_inst)},
                                        new Integer[]{R.drawable.tut_navsuperadmintools, R.drawable.tut_navitemsuperadminusers, R.drawable.tut_navitemadminusers, R.drawable.tut_navitemlines});
                                MapTutorial.show();

                            } else if (_sessionManager.getIsAdmin()) {
                                // admin tools enabled
                                TutorialDialog MapTutorial = new TutorialDialog(MenuActivity.this, new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_explore_more_title), MenuActivity._GlobalResource.getString(R.string.TUT_drivers_title), MenuActivity._GlobalResource.getString(R.string.TUT_lines_title), MenuActivity._GlobalResource.getString(R.string.TUT_tracked_puvs_title), MenuActivity._GlobalResource.getString(R.string.TUT_shortcuts_title), MenuActivity._GlobalResource.getString(R.string.TUT_shortcuts_title)},
                                        new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_explore_administartor_inst), MenuActivity._GlobalResource.getString(R.string.TUT_drivers_inst), MenuActivity._GlobalResource.getString(R.string.TUT_lines_inst),
                                                MenuActivity._GlobalResource.getString(R.string.TUT_tracked_puvs_inst)
                                                , MenuActivity._GlobalResource.getString(R.string.TUT_floating_action_button_inst), MenuActivity._GlobalResource.getString(R.string.TUT_floating_action_button_inst)},
                                        new Integer[]{R.drawable.tut_navadmintools, R.drawable.tut_navitemdrivers, R.drawable.tut_navitemlines, R.drawable.tut_navitemtrackedpuvs, R.drawable.tut_fabcollapsed, R.drawable.tut_fabexpanded});
                                MapTutorial.show();
                            } else if (_sessionManager.isGuest()) {
                                TutorialDialog MapTutorial = new TutorialDialog(MenuActivity.this, new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_sign_up_title)},
                                        new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_sign_up_inst)},
                                        new Integer[]{R.drawable.tut_onetapsignup});
                                MapTutorial.show();
                            }
                            _sessionManager.TutorialStatus(Enums.UIType.SHOWING_NAVIGATION_DRAWER, true);
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            _helper.logger(ex);

        }

    }

    public void ShowRouteTabsAndSlidingPanel() {
        _SL_Map_Fragment.stopShimmerAnimation();
        if (_sessionManager.isDriver()) {
            final Handler HND_CollapsePanel = new Handler();
            HND_CollapsePanel.postDelayed(new Runnable() {
                @Override
                public void run() {
                    _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }
            }, 1000);
            _SlideUpPanelContainer.setTouchEnabled(false);
            _SlideUpPanelContainer.setPanelHeight(220);
            _RoutesPane.setVisibility(View.VISIBLE);
            _TimeOfArrivalTextView.setVisibility(View.VISIBLE);
            _StepsScroller.setVisibility(View.GONE);
            FrameSearchBarHolder.setVisibility(View.INVISIBLE);
        } else {
            _SlideUpPanelContainer.setTouchEnabled(true);
            _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            _AppBarLayout.height = Helper.dpToPx(60, _context);
            _AppBar.setLayoutParams(_AppBarLayout);
            _RouteTabLayout.setVisibility(View.VISIBLE);
            _RoutesPane.setVisibility(View.VISIBLE);
            _RouteStepsText.setVisibility(View.VISIBLE);
            FAB_SammIcon.setVisibility(View.INVISIBLE);
            Search_BackBtn.setVisibility(View.VISIBLE);
            _IsOnSearchMode = true;
        }
    }

    public void HideRouteTabsAndSlidingPanel() {
        final Handler HND_CollapsePanel = new Handler();
        _AppBarLayout.height = 0;
        _AppBar.setLayoutParams(_AppBarLayout);
        asyncPrepareRouteData.clearLines();
        Search_BackBtn.setVisibility(View.INVISIBLE);
        FAB_SammIcon.setVisibility(View.VISIBLE);
        HND_CollapsePanel.postDelayed(new Runnable() {
            @Override
            public void run() {
                FrameSearchBarHolder.setVisibility(View.VISIBLE);
                _RouteTabLayout.setVisibility(View.INVISIBLE);
                _RouteStepsText.setVisibility(View.INVISIBLE);
                placeAutoCompleteFragmentInstance.setText(null);
                _IsOnSearchMode = false;
                _selectedPickUpPoint = null;

                asyncPrepareRouteData.RemoveListenerFromLoop();
                _HasExitedInfoLayout = true;
                _SL_Vehicle_ETA.stopShimmerAnimation();
                _SL_Map_Fragment.startShimmerAnimation();
                Helper.InitializeSearchingRouteUI(true, false, null, null, null, _context);
                _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }, 1000);
        //_SlideUpPanelContainer.setPanelHeight(_helper.dpToPx(60,_context));
        _SlideUpPanelContainer.setTouchEnabled(false);
        new asyncGetApplicationSettings(_activity, _context, true).execute();

    }

    public void SetMapType(GoogleMap gmap, Enums.GoogleMapType mapType) {
        UnselectMapTypes();
        switch (mapType) {
            case MAP_TYPE_NORMAL:
                gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Integer mapsStyle = IsNight() ? R.raw.night_maps_style_darker : R.raw.maps_style;
                boolean success = _googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, mapsStyle));

                if (!success) {
                    _helper.logger(_GlobalResource.getString(R.string.GM_Style_Parse_Failed));
                }
                _IB_MapType_Normal.setBackgroundResource(R.drawable.selected_border);
                break;
            case MAP_TYPE_HYBRID:
                gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                _IB_MapType_Hybrid.setBackgroundResource(R.drawable.selected_border);
                break;
            case MAP_TYPE_TERRAIN:
                gmap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                _IB_MapType_Terrain.setBackgroundResource(R.drawable.selected_border);
                break;
            case MAP_TYPE_SATELLITE:
                gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                _IB_MapType_Satellite.setBackgroundResource(R.drawable.selected_border);
                break;
            default:
                gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                _IB_MapType_Normal.setBackgroundResource(R.drawable.selected_border);

        }
        _sessionManager.SetMapStylePreference(mapType);

    }

    public void UnselectMapTypes() {
        List<ImageButton> IB_MapStyleList = new ArrayList<ImageButton>();
        IB_MapStyleList.add(_IB_MapType_Normal);
        IB_MapStyleList.add(_IB_MapType_Hybrid);
        IB_MapStyleList.add(_IB_MapType_Satellite);
        IB_MapStyleList.add(_IB_MapType_Terrain);
        for (ImageButton IB_entry : IB_MapStyleList) {
            IB_entry.setBackgroundResource(0);
        }
    }

    //Public methods for showing Dialogs from fragments which are previously attached to FABs onclick events.

    public void ShowInfoLayout(String Title, String Description, String Description2, Integer Icon, Enums.InfoLayoutType InfoType) {
        try {
            ClearInfoPanelDetails();
            ToggleMapTools();
            InfoPanelShow(Title, Description, Description2, Icon, InfoType);
        } catch (Exception ex) {
            Toast.makeText(MenuActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void InitializeInfoPanel() {
        try {
            _infoTitleTV = findViewById(R.id.TextView_InfoTitle);
            _infoDescriptionTV = findViewById(R.id.TextView_InfoDesc);
            _infoDescriptionTV2 = findViewById(id.TextView_InfoDesc2);
            _infoLayout = findViewById(R.id.Info_Layout);
            _infoPanelBtnClose = findViewById(id.btnCloseInfoPanel);
            _infoTitleTV.setVisibility(View.INVISIBLE);
            _infoDescriptionTV.setVisibility(View.INVISIBLE);
            _infoDescriptionTV2.setVisibility(View.INVISIBLE);
            _infoImage = findViewById(id.ImageView_InfoImage);
            _infoPanelBtnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InfoPanelHide();
                }
            });


        } catch (Exception ex) {
            Toast.makeText(MenuActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void InfoPanelShow(final String InfoTitle, final String InfoDescription, final String InfoDescription2, final Integer Icon, final Enums.InfoLayoutType infoType) {
        //final int imageResId = isStation ? R.drawable.ic_ecoloopstop_for_info : R.drawable.eco_loop_for_info_transparent;
        final String finalTitle = InfoTitle;
        final Typeface finalTitleFont = GetFontStyle(infoType);
        _infoTitleTV.setTypeface(null);
        if (_infoLayout.getVisibility() == View.INVISIBLE) {
            if (!_IsOnSearchMode)
                UpdateUI(Enums.UIType.SHOWING_INFO);
            else
                Search_BackBtn.setVisibility(View.INVISIBLE);
            _infoLayout.setVisibility(View.VISIBLE);
            _infoLayout.startAnimation(slide_down);
            slide_down.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    _infoLayout.startAnimation(slide_up_bounce);
                    slide_up_bounce.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            UpdateInfoPanelDetails(finalTitle, InfoDescription, InfoDescription2);
                            _infoImage.setImageResource(Icon);
                            _infoTitleTV.setTypeface(finalTitleFont);
                            _infoTitleTV.setVisibility(View.VISIBLE);
                            _infoDescriptionTV.setVisibility(View.VISIBLE);
                            _infoDescriptionTV.setTypeface(FONT_RUBIK_REGULAR);
                            _infoDescriptionTV2.setTypeface(FONT_RUBIK_REGULAR);
                            _infoTitleTV.setSelected(true);
                            if (InfoDescription2 != null && !InfoDescription2.equals("")) {
                                _infoDescriptionTV2.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else if (_infoLayout.getVisibility() == View.VISIBLE) {
            _LL_MapActions.setVisibility(View.INVISIBLE);
            _infoLayout.startAnimation(slide_down_bounce);
            slide_down_bounce.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    _infoLayout.startAnimation(slide_up);
                    _infoLayout.setVisibility(View.INVISIBLE);
                    slide_up.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            InfoPanelShow(InfoTitle, InfoDescription, InfoDescription2, Icon, infoType);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

    }

    public void UpdateInfoPanelDetails(String Title, String Description, String Description2) {
        if (_InfoPanel_IsEditingEnabled) {
            _infoTitleTV.setText(Title);
            if (Description != null) {
                if (Description.equalsIgnoreCase("init"))
                    _infoDescriptionTV.setText(_helper.getEmojiByUnicode(0x1F6BB) + " : " + _GlobalResource.getString(R.string.dialog_fetching_data_with_ellipsis));
                else
                    _infoDescriptionTV.setText(Description);
            }
            if (Description2 != null)
                _infoDescriptionTV2.setText(Description2);
        }
    }

    public void ClearInfoPanelDetails() {
        _infoImage.setImageResource(0);
        _infoTitleTV.setText(null);
        _infoDescriptionTV.setText(null);
        _infoDescriptionTV2.setText(null);
    }

    public void InfoPanelHide() {
        ToggleMapTools();
        Animation slide_down_bounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_bounce);
        _infoLayout.startAnimation(slide_down_bounce);
        ClearInfoPanelDetails();
        ArrivalHelper.RemoveListenerFromLoop();
        slide_down_bounce.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_up);
                _infoLayout.startAnimation(slide_up);
                if (!_IsOnSearchMode)
                    UpdateUI(Enums.UIType.HIDE_INFO);
                else {
                    UpdateUI(Enums.UIType.HIDE_INFO_SEARCH);
                }
                slide_up.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        _infoLayout.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public static void buttonEffect(View button) {
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

    private void InitializeFonts() {
        try {
            FONT_PLATE = Typeface.createFromAsset(_context.getAssets(),
                    _GlobalResource.getString(R.string.FONT_LICENSE_PLATE));
            FONT_STATION = Typeface.createFromAsset(_context.getAssets(),
                    _GlobalResource.getString(R.string.FONT_ROBOTO_MEDIUM));
            FONT_RUBIK_BOLD = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_Rubik_Bold));
            FONT_RUBIK_REGULAR = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_Rubik_Regular));
            FONT_RUBIK_MEDIUM = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_ROBOTO_MEDIUM));
            FONT_ROBOTO_CONDENDSED_BOLD = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_RobotoCondensed_Bold));
            FONT_RUBIK_BLACK = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_Rubik_Black));
        } catch (Exception ex) {
            Helper.logger(ex);
        }
    }

    private void InitializeAnimations() {
        slide_down_bounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_bounce);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        slide_up_bounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up_bounce);
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

    }

    public void GetFacebookUsername(final String STR_FB_id, final UserMarker userMarkerCache) {
        try {
            MenuActivity._usersDBRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    String STR_firstName = dataSnapshot.child(STR_FB_id).child("firstName").getValue() == null ? null : dataSnapshot.child(STR_FB_id).child("firstName").getValue().toString();
                    String STR_lastName = dataSnapshot.child(STR_FB_id).child("lastName").getValue() == null ? null : dataSnapshot.child(STR_FB_id).child("lastName").getValue().toString();
                    if (STR_firstName == null && STR_lastName == null)
                        userMarkerCache.UserTitle = null;
                    else
                        userMarkerCache.UserTitle = STR_firstName + " " + STR_lastName;
                    UpdateInfoPanelDetails(userMarkerCache.UserTitle, null, null);

                }
            });

        } catch (Exception ex) {
            Helper.logger(ex);
        }

    }

    public int GetUserNavigationMenuPicture() {
        int result = R.drawable.evxuser_registered;
        try {
            if (_sessionManager.isGuest())
                result = R.drawable.evxuser_default;
            else if (_sessionManager.getIsAdmin())
                result = R.drawable.evxuser_admin;
            else if (_sessionManager.getIsSuperAdmin())
                result = R.drawable.evxuser_admin;

        } catch (Exception ex) {
            Helper.logger(ex);
        }
        return result;
    }

    private Typeface GetFontStyle(Enums.InfoLayoutType layouttype) {
        Typeface TF_result = FONT_STATION;
        try {
            switch (layouttype) {
                case INFO_VEHICLE:
                    TF_result = FONT_RUBIK_BOLD;
                    break;
                case INFO_PERSON:
                    TF_result = FONT_ROBOTO_CONDENDSED_BOLD;
                    break;
                default:
                    TF_result = FONT_ROBOTO_CONDENDSED_BOLD;
            }
        } catch (Exception ex) {
            Helper.logger(ex);
        }
        return TF_result;
    }

    private void InitializeMapStyle() {
        _IB_MapType_Normal = findViewById(id.IB_mapType_normal);
        _IB_MapType_Hybrid = findViewById(id.IB_mapType_hybrid);
        _IB_MapType_Satellite = findViewById(id.IB_mapType_satellite);
        _IB_MapType_Terrain = findViewById(id.IB_mapType_terrain);
        _IB_MapType_Normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetMapType(_googleMap, Enums.GoogleMapType.MAP_TYPE_NORMAL);
                view.setBackgroundResource(R.drawable.selected_border);
            }
        });
        _IB_MapType_Terrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetMapType(_googleMap, Enums.GoogleMapType.MAP_TYPE_TERRAIN);
                view.setBackgroundResource(R.drawable.selected_border);
            }
        });
        _IB_MapType_Satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetMapType(_googleMap, Enums.GoogleMapType.MAP_TYPE_SATELLITE);
                view.setBackgroundResource(R.drawable.selected_border);
            }
        });
        _IB_MapType_Hybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetMapType(_googleMap, Enums.GoogleMapType.MAP_TYPE_HYBRID);
                view.setBackgroundResource(R.drawable.selected_border);
            }
        });
    }

    private void ToggleMapTools() {
        _LL_MapActions.setVisibility(_LL_MapActions.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
        _LL_MapStyleHolder.setVisibility(View.INVISIBLE);
    }

    public void InflateDriverRoutesPanel() {
        ViewStub _VS_RoutesPanel = findViewById(id.ViewStub_RoutesPanel);
        _VS_RoutesPanel.inflate();
        ShimmerLayout SL_SelectYourRoute = findViewById(id.SL_SelectYourRoute);
        final TextView TV_DriverRoutePanelTitle = findViewById(id.TV_RoutePanelTitle);
        SL_SelectYourRoute.startShimmerAnimation();
        final LinearLayout LL_RoutesListDisplay = findViewById(id.LL_RoutesListDisplay);
        if (_routeList.size() > 0) {
            for (final Routes routeEntry : _routeList) {
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(Helper.dpToPx(130, _context), Helper.dpToPx(130, _context));
                btnParams.setMargins(7, 0, 7, 0);
                Button BTN_routeButton = new Button(this);
                BTN_routeButton.setLayoutParams(btnParams);
                BTN_routeButton.setTextSize(Helper.dpToPx(7, _context));
                BTN_routeButton.setText(routeEntry.getRouteName());
                BTN_routeButton.setTypeface(FONT_RUBIK_BOLD);
                BTN_routeButton.setBackgroundResource(R.drawable.rounded_corner_route_panel);
                BTN_routeButton.setMaxLines(1);
                BTN_routeButton.setEllipsize(TextUtils.TruncateAt.END);
                BTN_routeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //unselect other routes
                        for (Integer i = 0; LL_RoutesListDisplay.getChildCount() != i; i++) {
                            View v = LL_RoutesListDisplay.getChildAt(i);
                            v.setBackgroundResource(R.drawable.rounded_corner_route_panel);
                            ((Button) v).setTextColor(getApplication().getResources().getColor(R.color.colorBlack));
                        }
                        //then apply UI style to the selected
                        view.setBackgroundResource(R.drawable.selected_route);
                        ((Button) view).setTextColor(getApplication().getResources().getColor(R.color.colorWhite));
                        ((Button) view).setTypeface(FONT_RUBIK_BOLD);
                        TV_DriverRoutePanelTitle.setText("Selected: " + routeEntry.getRouteName().toUpperCase());
                        _driverRoutesDatabaseReference.child(_sessionManager.getKeyDeviceid()).child("routeID").setValue(String.valueOf(routeEntry.getID()));
                    }
                });
                LL_RoutesListDisplay.addView(BTN_routeButton);
            }
        }
    }

    private void UserProfileEvent() {
        if (!_sessionManager.isGuest()) {
            UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
            _fragmentManager.beginTransaction().replace(R.id.content_frame, new UserProfileActivity()).commit();
        } else {
            InfoDialog GuestInfo = new InfoDialog(MenuActivity.this, getResources().getString(R.string.guest_nav_drawer_message));
            GuestInfo.show();
        }
    }

    public void UpdateInfoPanelForTimeofArrival(String Title, String Description, String Description2) {
        try {
            UpdateInfoPanelDetails(Title, null, Description2);
        } catch (Exception ex) {
            Helper.logger(ex);
        }

    }

    public void RenderSearchAutocomplete() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    public static Location GetCurrentLocation() {
        if (ContextCompat.checkSelfPermission(_context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location current = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return current;
        }
        return null;

    }

}









