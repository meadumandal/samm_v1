package com.umandalmead.samm_v1;
//IMPORTS

//region Imports

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.umandalmead.samm_v1.EntityObjects.Eloop;
import com.umandalmead.samm_v1.EntityObjects.Lines;
import com.umandalmead.samm_v1.EntityObjects.Routes;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.EntityObjects.UserMarker;
import com.umandalmead.samm_v1.EntityObjects.Users;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.AddPassengerCountLabel;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.AddUserMarkers;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.AddVehicleMarkers;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.Vehicle_DestinationsListener;
import com.umandalmead.samm_v1.Modules.AdminUsers.AdminUsersFragment;
import com.umandalmead.samm_v1.Modules.AdminUsers.mySQLGetAdminUsers;
import com.umandalmead.samm_v1.Modules.DriverUsers.DriverUsersFragment;
import com.umandalmead.samm_v1.Modules.DriverUsers.mySQLGetDriverUsers;
import com.umandalmead.samm_v1.Modules.DriverUsers.mySQLGetDrivers;
import com.umandalmead.samm_v1.Modules.SuperAdminUsers.SuperAdminUsersFragment;
import com.umandalmead.samm_v1.POJO.HTMLDirections.Directions;
import com.umandalmead.samm_v1.POJO.HTMLDirections.Setting;
import com.umandalmead.samm_v1.POJO.HTMLDirections.Settings;
import com.umandalmead.samm_v1.R.id;
import com.umandalmead.samm_v1.RouteTabs.Route2;
import com.umandalmead.samm_v1.RouteTabs.Route3;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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


import static com.umandalmead.samm_v1.Constants.APPBAR_MIN_HEIGHT;
import static com.umandalmead.samm_v1.Constants.LOG_TAG;
import static com.umandalmead.samm_v1.Constants.MY_PERMISSIONS_REQUEST_SEND_SMS;
import static com.umandalmead.samm_v1.Constants.MY_PERMISSION_REQUEST_LOCATION;




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
        //Put here all global variables related to Firebase
        public static FirebaseDatabase _firebaseDB;
        public static DatabaseReference _usersDBRef;
        public static DatabaseReference _terminalsDBRef;
        public static DatabaseReference _driversDBRef;
        public DatabaseReference _vehicle_destinationsDBRef, _DriversDatabaseReference;

        //Put here all global Collection Variables
        public static List<Terminal> _possiblePickUpPointList;
        public static List<Terminal> _terminalList;
        public static HashMap<String, Marker> _terminalMarkerHashmap = new HashMap<>();
        public HashMap _userMarkerHashmap = new HashMap();
        public HashMap<String, Long> _passengerCount = new HashMap<>();
        public static List<Eloop> _eloopList;
        public static ArrayList<Routes> _routeList;
        public static ArrayList<Lines> _lineList;

        public static ArrayList<Users> _driverList;
        public static HashMap _driverMarkerHashmap = new HashMap();

        //Put here all global variables related to sending SMS
        private UserMovementBroadcastReceiver _userMovementBroadcastReceiver;
        private SentSMSBroadcastReceiver _smsSentBroadcastReceiver;
        private SMSDeliveredBroadcastReceiver _smsDeliveredBroadcastReceiver;
        private PendingIntent _sentSMSPendingIntent;
        private PendingIntent _deliveredSMSPendingIntent;

        //Put here all global variables for UI Objects
        public static LinearLayout _RoutesPane;
        public static SlidingUpPanelLayout _SlideUpPanelContainer;
        public static TabLayout _RouteTabLayout;
        public static WebView _RouteStepsText;
        public ProgressDialog _ProgressDialog;
        public static ImageView _Slide_Expand;
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
        public static Toolbar _Toolbar;
        public static LinearLayout _SearchLinearLayout;
        public  static EditText _CurrentLocationEditText;
        public static LinearLayout _MapsHolderLinearLayout, _LL_MapActions;
        public FloatingActionButton _AddGPSFloatingButton, _AddPointFloatingButton, _ViewGPSFloatingButton;
        public static FloatingActionMenu _AdminToolsFloatingMenu;
        public Button _ReconnectGPSButton;
        public static ImageView FAB_SammIcon;
        public static FrameLayout FrameSearchBarHolder;
        public static ImageView Search_BackBtn;
        public  static TextView _DestinationTextView;
        //public static CardView _RoutesContainer_CardView;
        public static ProgressBar _LoopArrivalProgress;
        public static String _FragmentTitle, _SelectedTerminalMarkerTitle, _STR_SAMM_USERNAME;
        public static MediaPlayer _buttonClick;
        private TextView _infoTitleTV, _infoDescriptionTV, _infoDescriptionTV2;
        private LinearLayout _infoLayout;
        private ImageButton _infoPanelBtnClose, _IB_Maptype, _IB_MyLocation, _IB_MapType_Normal,
                _IB_MapType_Satellite, _IB_MapType_Hybrid, _IB_MapType_Terrain;
        private ImageView _infoImage;
        private Animation  slide_down, slide_down_bounce, slide_up, slide_up_bounce;
        private static EditText placeAutoCompleteFragmentInstance;
        private LocationManager locationManager;

        //Arrival Info elements
        public static LinearLayout _LL_Arrival_Info, _LL_MapStyleHolder;
        public static TextView _TV_Vehicle_Identifier, _TV_Vehicle_Description, _TV_TimeofArrival;
        public static ShimmerLayout _SL_Map_Fragment,_SL_Vehicle_ETA, _SL_AppBar_Top_TextView;


        //Put here other global variables
        public static GoogleApiClient _googleAPI;
        public Helper _helper;
        public Context _context;
        public static LatLng _userCurrentLoc;
        public Marker _userCurrentLocMarker;
        public LocationRequest _locationRequest;
        public static GoogleMap _googleMap;
        public SessionManager _sessionManager;
        boolean _isAppFirstLoad;
        public static LevelListDrawable _drawable = new LevelListDrawable();
        public static Terminal _selectedPickUpPoint;
        public Terminal _chosenDropOffPoint;
        public static ValueAnimator _markerAnimator;
        public Marker _vehicleMarker;
        public Boolean _isUserLoggingOut = false, _BOOL_IsGoogleMapShownAndAppIsOnHomeScreen = false;
        public String _facebookImg;
        public static Boolean _isVehicleMarkerRotating = false, _IsPolyLineDrawn=false;
        public String _smsMessageForGPS;
        private String _GPSMobileNumber;
        private String _gpsPlateNumber;
        private Integer _gpsTblUsersID;
        private Integer _gpsTblRoutesID;
        private String _gpsNetwork;
        public String _smsAPN;
        public Boolean _isGPSReconnect = false;
        private String _gpsIMEI;
        public Constants _constants;
        public static Boolean _IsOnSearchMode = false, _BOOL_IsTerminalDataFetchDone = false, _BOOL_IsTerminalDataFetchOnGoing = false, _BOOL_IsGPSAcquired=false;
        public static Terminal[] _PointsArray;
        public static Routes[] _RoutesArray;
        public static Typeface FONT_PLATE,FONT_STATION, FONT_RUBIK_REGULAR, FONT_RUBIK_BOLD, FONT_RUBIK_MEDIUM, FONT_ROBOTO_CONDENDSED_BOLD, FONT_RUBIK_BLACK;
        public static int _currentRouteIDSelected;
        public static int _currentLineIDSelected;
        public static Boolean _HasExitedInfoLayout = false;
        public static CustomFrameLayout _mapRoot;
        public static Integer _DestinationTblRouteID, _RouteTabSelectedIndex=0;
        public AddGPSDialog _dialog;
        public LoaderDialog _LoaderDialog;
        public static TabLayout RouteTabs;
        public static ViewPager viewPager;
        public static Resources _GlobalResource;
        public static  CoordinatorLayout.LayoutParams _AppBarLayout;
        public static DrawerLayout _MainDrawerLayout;
        public static View _MAINCONTENT;



        public  boolean _IsAllLoopParked,_InfoPanel_IsEditingEnabled=true;
        private Terminal  TM_ClickedTerminal = new Terminal();
        private List<Integer> _ListOfLoops = new ArrayList<Integer>();
        private String _AssignedELoop = "";
        private int _passengerCountInTerminal =0;
        public static HashMap<String, String> _currentRoutesOfEachLoop = new HashMap<>();

        public static ArrayList<Users> _adminUsers = new ArrayList<Users>();



        //button effect
        private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        /**
         * This method checks if the app has permission to access user lcoation
         * @return Returns true if permission granted. Otherwise, false
         */
        public boolean checkLocationPermission()
        {
            Log.i(_constants.LOG_TAG, "Checking location permission (checkLocationPermission)");
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        public static void HideNetCheckerDialog(Context context) {
            Toast ifx = Toast.makeText(context, "Unable to connect to server!", Toast.LENGTH_SHORT);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        try {

            Log.i(_constants.LOG_TAG, "Creating MenuActivity...");
            setTheme(R.style.SplashTheme);
            super.onCreate(savedInstanceState);
            _context = getApplicationContext();
            _helper = new Helper(MenuActivity.this, this._context);
            _GlobalResource = getResources();
            _constants = new Constants();
           // new asyncCheckInternetConnectivity(MenuActivity.this).execute();
            if(_helper.isOnline(MenuActivity.this, getApplicationContext())) {
                _isAppFirstLoad = true;

                displayLocationSettingsRequest(_context);
                //region EloopList
                new mySQLGetEloopList(_context).execute();
                //endregion
                new mySQLRoutesDataProvider(MenuActivity.this, _context).execute();
//                new mySQLLinesDataProvider(_context).execute();

                new mySQLGetDriverUsers(_context).execute();
                new mySQLGetAdminUsers(_context).execute();
                new mySQLGetDrivers(MenuActivity.this, getApplicationContext()).execute();



                if (_sessionManager == null)
                    _sessionManager = new SessionManager(_context);
                if (!_sessionManager.isLoggedIn()) {
                    String username = _constants.GUEST_USERNAME_PREFIX + UUID.randomUUID().toString();
                    _sessionManager.CreateLoginSession(_constants.GUEST_FIRSTNAME, _constants.GUEST_LASTNAME, username, "", "", false, Constants.GUEST_USERTYPE);
                }
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
                            Helper.logger(ex,true);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d(_constants.LOG_TAG, t.toString());
                    }
                });

                if (_firebaseDB == null || _usersDBRef == null || _vehicle_destinationsDBRef == null) {
                    _firebaseDB = FirebaseDatabase.getInstance();
                    _usersDBRef = _firebaseDB.getReference("users");
                    _terminalsDBRef = _firebaseDB.getReference("terminals");
                    _vehicle_destinationsDBRef = _firebaseDB.getReference("vehicle_destinations");
                    _DriversDatabaseReference = _firebaseDB.getReference("drivers");
                }
                if (_driversDBRef == null)
                    _driversDBRef = _firebaseDB.getReference("drivers");

                //Instantiate UI objects~
                _MAINCONTENT = (View) findViewById(id.main_content);
                _AppBar = (AppBarLayout) findViewById(R.id.appBarLayout);
                _AppBarLayout = (CoordinatorLayout.LayoutParams) _AppBar.getLayoutParams();
                _RoutesPane = (LinearLayout) findViewById(R.id.route_content);
                _SlideUpPanelContainer = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
                _TV_TimeofArrival = (TextView) findViewById(id.TV_VehicleTimeOfArrival);
                _TV_Vehicle_Identifier = (TextView) findViewById(id.TV_Vehicle_Identifier);
                _TV_Vehicle_Description = (TextView) findViewById(id.TV_Vehicle_Description);
                _LL_Arrival_Info = (LinearLayout) findViewById(id.LL_Arrival_Info);
                _RouteTabLayout = (TabLayout) findViewById(R.id.route_tablayout);
                _StepsScroller = (ScrollView) findViewById(R.id.step_scroll_view);
                _TimeOfArrivalTextView = (TextView) findViewById(R.id.toatextview);
                _NavView = (NavigationView) findViewById(R.id.nav_view);
                _MenuNav = (Menu) _NavView.getMenu();
                _UserNameMenuItem = _MenuNav.findItem(R.id.menu_username);
                _NavHeaderView = _NavView.getHeaderView(0);
                _ProfilePictureImg = (CircleImageView) _NavHeaderView.findViewById(R.id.imgLogo);
                _HeaderUserFullName = (TextView) _NavHeaderView.findViewById(R.id.HeaderUserFullName);
                _HeaderUserEmail = (TextView) _NavHeaderView.findViewById(R.id.HeaderUserEmail);
                _UserNameMenuItem.setTitle(_sessionManager.getFullName().toUpperCase());
                _HeaderUserFullName.setText(_sessionManager.getFullName().toUpperCase());
                _ProfilePictureImg.setBackgroundResource(_sessionManager.isFacebook() ? 0: GetUserNavigationMenuPicture());
                _HeaderUserEmail.setText(_sessionManager.getEmail());
                _AddGPSFloatingButton = (FloatingActionButton) findViewById(R.id.subFloatingAddGPS);
                _AddPointFloatingButton = (FloatingActionButton) findViewById(R.id.subFloatingAddPoint);
                _ViewGPSFloatingButton = (FloatingActionButton) findViewById(R.id.subFloatingViewGPS);
                _AdminToolsFloatingMenu = (FloatingActionMenu) findViewById(R.id.AdminFloatingActionMenu);
                _IB_Maptype = (ImageButton) findViewById(id.IB_mapType);
                _IB_MyLocation = (ImageButton) findViewById(id.IB_myLocation);
                InitializeMapStyle();
                _LL_MapStyleHolder = (LinearLayout) findViewById(id.LL_mapStyleHolder);
                _LL_MapActions = (LinearLayout) findViewById(id.LL_mapActions);

                //Hide or view nav menus based on user type
                _NavView.getMenu().findItem(id.nav_superadminusers).setVisible(_sessionManager.getIsSuperAdmin());
                _NavView.getMenu().findItem(id.nav_adminusers).setVisible(_sessionManager.getIsSuperAdmin());
                _NavView.getMenu().findItem(id.nav_drivers).setVisible(_sessionManager.getIsAdmin());
                _NavView.getMenu().findItem(id.nav_lines).setVisible(_sessionManager.getIsSuperAdmin() || _sessionManager.getIsAdmin());
                _NavView.getMenu().findItem(R.id.nav_vehicles).setVisible(_sessionManager.getIsAdmin());
                _NavView.getMenu().findItem(id.nav_drivers).setVisible(_sessionManager.getIsAdmin());
                _NavView.getMenu().findItem(R.id.nav_logout).setVisible(!_sessionManager.isGuest());
                _NavView.getMenu().findItem(R.id.nav_login).setVisible(_sessionManager.isGuest());
                _NavView.getMenu().findItem(R.id.nav_passengerpeakandlean).setVisible(_sessionManager.getIsAdmin() || _sessionManager.getIsPassenger() || _sessionManager.isGuest());
                _NavView.getMenu().findItem(R.id.nav_ecolooppeakandlean).setVisible(_sessionManager.getIsAdmin());

                FAB_SammIcon = (ImageView) findViewById(R.id.SAMMLogoFAB);
                FrameSearchBarHolder = (FrameLayout) findViewById(R.id.FrameSearchBarHolder);
                Search_BackBtn = (ImageView) findViewById(id.Search_BackBtn);
                _DestinationTextView = (TextView) findViewById(id.DestinationTV);
                _LoopArrivalProgress = (ProgressBar) findViewById(R.id.progressBarLoopArrival);
                _MapsHolderLinearLayout = (LinearLayout) findViewById(R.id.mapsLinearLayout);
                InitializeInfoPanel();
                InitializeFonts();
                InitializeAnimations();
                MenuActivity.buttonEffect(Search_BackBtn);
                _DestinationTextView.setTypeface(FONT_RUBIK_REGULAR);
                _SlideUpPanelContainer.setTouchEnabled(false);
                _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                _SL_Map_Fragment = (ShimmerLayout) findViewById(id.SL_Map_Fragment);
                _SL_Vehicle_ETA = (ShimmerLayout) findViewById(id.SL_Vehicle_ETA);
                _SL_AppBar_Top_TextView = (ShimmerLayout) findViewById(id.SL_Appbar_Top_TextView);
                _SL_Map_Fragment.startShimmerAnimation();
                RouteTabs = (TabLayout) findViewById(R.id.route_tablayout);
                viewPager = (ViewPager) findViewById(R.id.routepager);
                _RouteStepsText = (WebView) findViewById(R.id.route_steps);
                ShowGPSLoadingInfo(_GlobalResource.getString(R.string.GM_acquiring_gps), true);
                _BOOL_IsGoogleMapShownAndAppIsOnHomeScreen = true;
                _buttonClick = MediaPlayer.create(this, R.raw.button_click);
                _MainDrawerLayout = (DrawerLayout) findViewById(id.drawer_layout);

                if (_sessionManager.getIsAdmin())
                    _AdminToolsFloatingMenu.setVisibility(View.VISIBLE);
                else
                    _AdminToolsFloatingMenu.setVisibility(View.GONE);
               _MainDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                   @Override
                   public void onDrawerSlide(View drawerView, float slideOffset) {
                       _MAINCONTENT.setTranslationX(slideOffset * drawerView.getWidth());
                   }

                   @Override
                   public void onDrawerOpened(View drawerView) {

                   }

                   @Override
                   public void onDrawerClosed(View drawerView) {

                   }

                   @Override
                   public void onDrawerStateChanged(int newState) {

                   }
               });
                _AddGPSFloatingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _dialog = new AddGPSDialog(MenuActivity.this);
                        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        _dialog.show();
                        PlayButtonClickSound();
                    }
                });
                FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        _MainDrawerLayout.openDrawer(Gravity.LEFT);
                        PlayButtonClickSound();
                    }
                });
                Search_BackBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        ZoomAndAnimateMapCamera(_userCurrentLoc, 15);
                        PlayButtonClickSound();
                        final Handler HND_ZoomGoogleMapToUserLocation = new Handler();
                        HND_ZoomGoogleMapToUserLocation.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                HideRouteTabsAndSlidingPanel();
                            }
                        }, 500);

                    }
                });

                _AddPointFloatingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddPointDialog dialog = new AddPointDialog(MenuActivity.this, "ADD");
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        PlayButtonClickSound();
                    }
                });



                _ViewGPSFloatingButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        PlayButtonClickSound();
                        try {
                            UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                            FragmentManager fragment = getSupportFragmentManager();
                            fragment.beginTransaction().replace(R.id.content_frame, new ViewGPSFragment()).commit();
                        }
                        catch(Exception ex)
                        {
                            Helper.logger(ex,true);
                        }

                    }
                });

                _IB_MyLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(_userCurrentLoc!=null) {
                            PlayButtonClickSound();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(_userCurrentLoc, 16);
                            _googleMap.animateCamera(cameraUpdate);
                        }
                    }
                });
                _IB_Maptype.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlayButtonClickSound();
                       _LL_MapStyleHolder.setVisibility(_LL_MapStyleHolder.getVisibility() == View.INVISIBLE ? View.VISIBLE :View.INVISIBLE);
                    }
                });
                placeAutoCompleteFragmentInstance = (EditText) findViewById(id.place_autocomplete_search_input);
                placeAutoCompleteFragmentInstance.setTextColor(Color.parseColor(_GlobalResource.getString(R.string.PlaceAutoCompleteFragment_FontColor)));
                placeAutoCompleteFragmentInstance.setTypeface(FONT_RUBIK_REGULAR);
                placeAutoCompleteFragmentInstance.setTextSize(18);
                placeAutoCompleteFragmentInstance.setHint(_GlobalResource.getString(R.string.PlaceAutoCompleteFragment_Hint));
                placeAutoCompleteFragmentInstance.setText(null);


                AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(Place.TYPE_COUNTRY)
                        .setCountry("PH")
                        .build();
                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                autocompleteFragment.setFilter(autocompleteFilter);
                //set bounds to search within bounds only~
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(14.427248, 120.996781));
                builder.include(new LatLng(14.413897, 121.077285));

                autocompleteFragment.setBoundsBias(builder.build());

                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    //GO-2R
                    public void onPlaceSelected(Place place) {
                        try {
                            _DestinationTextView.setText(_constants.DESTINATION_PREFIX + place.getName().toString());
                            _DestinationTextView.setBackgroundResource(R.color.colorGrassGreen);
                            _DestinationTextView.setTextSize(Helper.dpToPx(8,_context));
                            new asyncProcessSelectedDestination(MenuActivity.this, getApplicationContext(), _terminalList, place).execute();

                        } catch (Exception ex) {
                            Log.i(_constants.LOG_TAG, ex.toString());
                        }
                    }

                    @Override
                    public void onError(Status status) {
                        // TODO: Handle the error.
                        Log.i(_constants.LOG_TAG, _GlobalResource.getString(R.string.error_an_error_occurred) + status);
                    }
                });
                autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                PlayButtonClickSound();
                                HideRouteTabsAndSlidingPanel();
                                placeAutoCompleteFragmentInstance.setText(null);
                            }
                        });

                _LoaderDialog = new LoaderDialog(this, _GlobalResource.getString(R.string.Adding_Vehicle_GPS), _GlobalResource.getString(R.string.GPS_Initialize));
                _LoaderDialog.setCancelable(false);

                if (_sessionManager.isGuest() || _sessionManager.isDriver() || _sessionManager.getIsAdmin()) {


                    //LinearLayout searchContainer = (LinearLayout) findViewById(R.id.searchlayoutcontainer);
                    //EditText tvcurrentlocation = (EditText) findViewById(R.id.tvcurrentlocation);

                    //searchContainer.setVisibility(View.GONE);
                    //tvcurrentlocation.setVisibility(View.GONE);
                    AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appBarLayout);

                    //CoordinatorLayout.LayoutParams appBarLayoutParam = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
                    //appBarLayoutParam.height = _constants.APPBAR_MIN_HEIGHT;
                }
                if (_sessionManager.isDriver()) {
                    _vehicle_destinationsDBRef.addChildEventListener(new Vehicle_DestinationsListener(getApplicationContext(), _terminalsDBRef));
                    ShowRouteTabsAndSlidingPanel();
                }

                // _RouteTabLayout.setMinimumWidth(150);
                _facebookImg = _GlobalResource.getString(R.string.Facebook_ProfilePic_Graph_URL) + _sessionManager.getUsername().trim() + _GlobalResource.getString(R.string.Facebook_ProfilePic_Graph_URL_QueryString);
                try {
                    FetchFBDPTask dptask = new FetchFBDPTask();
                    dptask.execute();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), _GlobalResource.getString(R.string.Error_NonFacebook_Username), Toast.LENGTH_LONG).show();
                }

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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

                _userMovementBroadcastReceiver = new UserMovementBroadcastReceiver();
                _smsSentBroadcastReceiver = new SentSMSBroadcastReceiver();
                _smsDeliveredBroadcastReceiver = new SMSDeliveredBroadcastReceiver();

                String SMS_SENT = _GlobalResource.getString(R.string.SMS_SENT);
                String SMS_DELIVERED = _GlobalResource.getString(R.string.SMS_DELIVERED);

                _sentSMSPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
                _deliveredSMSPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

                //register BroadcastReceiver
                IntentFilter intentFilter = new IntentFilter(GeofenceTransitionsIntentService.ACTION_MyIntentService);
                intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
                registerReceiver(_userMovementBroadcastReceiver, intentFilter);
                registerReceiver(_smsSentBroadcastReceiver, new IntentFilter(SMS_SENT));
                registerReceiver(_smsDeliveredBroadcastReceiver, new IntentFilter(SMS_DELIVERED));
                initializeOnlinePresence();
                _mapRoot = (CustomFrameLayout) findViewById(R.id.mapCFL);

            }
            else{
                _helper.showNoInternetPrompt(MenuActivity.this);
            }


        }catch(Exception ex)
        {
            _helper.logger(ex,true);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(_constants.LOG_TAG,_GlobalResource.getString(R.string.GM_Ready));
        _googleMap = googleMap;
        _googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                _LL_MapStyleHolder.setVisibility(View.INVISIBLE);
            }
        });
        Enums.GoogleMapType mapType = Enums.GoogleMapType.MAP_TYPE_NORMAL;
        try{
           mapType = Enums.GoogleMapType.valueOf(_sessionManager.getMapStylePreference());
        }catch (Exception ex){
            Helper.logger(ex);
        }
        SetMapType(_googleMap, mapType);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                buildGoogleApiClient();
            }
        }
        else {
            buildGoogleApiClient();
        }
        if(mapType == Enums.GoogleMapType.MAP_TYPE_NORMAL){
            Integer mapsStyle = IsNight() ? R.raw.night_maps_style_darker: R.raw.maps_style;
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
        _googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(!_IsOnSearchMode && _infoLayout.getVisibility() != View.VISIBLE) {
                    //FrameSearchBarHolder.setVisibility(View.VISIBLE);
                    //Log.i(_constants.LOG_TAG,"==camera idle=="+ _googleMap.getCameraPosition().target);
                }

            }
        });
        _googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason ==REASON_GESTURE) {
                   // FrameSearchBarHolder.setVisibility(View.INVISIBLE);
                   // isMaptouched=true;
                } else if (reason ==REASON_API_ANIMATION) {
//                    Toast.makeText(MenuActivity.this, "The user tapped something on the map.",
//                            Toast.LENGTH_SHORT).show();
                } else if (reason ==REASON_DEVELOPER_ANIMATION) {
//                    Toast.makeText(MenuActivity.this, "The app moved the camera.",
//                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(_constants.LOG_TAG, _GlobalResource.getString(R.string.G_API_Building));
        if(_helper.isGooglePlayInstalled(_context)) {
            _googleAPI = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            _googleAPI.connect();
        }
        else
        {
            Toast.makeText(_context, _GlobalResource.getString(R.string.G_Play_Services_Not_Installed), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try
        {
            if(!_BOOL_IsGPSAcquired) {
                _BOOL_IsGPSAcquired=true;
                ShowGPSLoadingInfo(_GlobalResource.getString(R.string.GM_gps_acquired), false);

            }
            if(!_isUserLoggingOut) {
                if(!_sessionManager.isDriver())
                {
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
                        locationManager.removeUpdates(this);

                    }

                    //stop location updates
                    if (_googleAPI != null) {
                        LocationServices.FusedLocationApi.removeLocationUpdates(_googleAPI, this);
                    }
                }

            }
        }
        catch(Exception ex)
        {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
    private void ZoomAndAnimateMapCamera(LatLng LATLNG_var1, int INT_zoomLevel){
        _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LATLNG_var1,INT_zoomLevel));
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
    private void saveDestination(String destinationValue)
    {
        final HashMap<String, Object> currentDestination = new HashMap<>();
        currentDestination.put("currentDestination", destinationValue);

        _usersDBRef.child(_sessionManager.getUsername()).child("currentDestination").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null)
                {
                    _usersDBRef.child(_sessionManager.getUsername()).child("currentDestination").setValue(currentDestination.get("currentDestination"));
                }
                else
                {
                    _usersDBRef.child(_sessionManager.getUsername()).updateChildren(currentDestination);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void saveLocation(final double lat, final double lng)
    {
        final HashMap<String, Object> latitude = new HashMap<>();
        final HashMap<String, Object> longitude = new HashMap<>();
        final HashMap<String, Object> hashLastUpdated= new HashMap<>();
        final String lastUpdated = new Date().toString();
        hashLastUpdated.put("lastUpdated", lastUpdated);
        latitude.put("Latitude", lat);
        longitude.put("Longitude", lng);

        Map<String, Object> nodes = new HashMap<>();
        nodes.put(_sessionManager.getUsername() + "/Longitude", lng);
        nodes.put(_sessionManager.getUsername() + "/Latitude", lat);
        nodes.put(_sessionManager.getUsername() + "/lastUpdated", lastUpdated);
        _usersDBRef.updateChildren(nodes);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(_constants.LOG_TAG, "Google API Client is connected...");
        new mySQLDestinationProvider(_context, MenuActivity.this, "", _googleMap, _googleAPI).execute();
        _locationRequest = new LocationRequest();
        _locationRequest.setInterval(0);
        _locationRequest.setFastestInterval(0);
        _locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(_googleAPI, _locationRequest, this);
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, this);

        }
        _googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                    final String markerTitle = marker.getTitle();
            if(markerTitle!=null) {
                if (_terminalMarkerHashmap.containsKey(markerTitle)) {
                    if (_sessionManager.getIsDeveloper() && !_sessionManager.isGuest() && !_sessionManager.isDriver()) {
                        //if admin only:
                        AddPointDialog dialog = new AddPointDialog(MenuActivity.this, "Update", marker.getTitle());
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    } else {
                        for (Terminal terminal : _terminalList) {
                            if (terminal.Value.toLowerCase().equals(markerTitle)) {
                                TM_ClickedTerminal = terminal;
                            }

                        }
                        _SelectedTerminalMarkerTitle = TM_ClickedTerminal.getValue();
                        final Handler HND_Loc_DataFetchDelay = new Handler();
                        final Handler HND_Loc_DataFetchTooLong = new Handler();
                        final Terminal F_TM_ClickedTerminal = TM_ClickedTerminal;
                        HND_Loc_DataFetchDelay.removeCallbacksAndMessages(null);
                        HND_Loc_DataFetchTooLong.removeCallbacksAndMessages(null);
                        HND_Loc_DataFetchDelay.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                GetAndDisplayEloopETA(F_TM_ClickedTerminal);
                            }
                        }, 3000);

                        ShowInfoLayout(TM_ClickedTerminal.Description,
                                        _helper.getEmojiByUnicode(0x1F6BB) + " : Fetching Data..",
                                _helper.getEmojiByUnicode(0x1F68C) + " : Fetching Data..", R.drawable.ic_ecoloopstop_for_info, Enums.InfoLayoutType.INFO_STATION);
                        _terminalsDBRef.child(marker.getTitle()).runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData currentData) {

                                return Transaction.success(currentData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot DS_Terminals) {
                                long passengercount = 0;
                                passengercount = DS_Terminals.getChildrenCount();
                                _passengerCountInTerminal = (int) passengercount;
                                UpdateInfoPanelDetails(TM_ClickedTerminal.Description,
                                        _helper.getEmojiByUnicode(0x1F6BB) + " : " + _passengerCountInTerminal + " passenger(s) waiting", null);
                            }
                        });

                    }
                }
                //vehicle has been clicked instead
                else if (_driverMarkerHashmap.containsKey(markerTitle)) {
                    _SelectedTerminalMarkerTitle = null;
                    Users driverDetails = Helper.GetEloopDriver(Helper.GetEloopEntry(markerTitle));
                    ShowInfoLayout(Helper.GetEloopEntry(markerTitle).PlateNumber, (driverDetails != null ? driverDetails.firstName : "") + " "
                            + (driverDetails != null ? driverDetails.lastName : ""), null, R.drawable.eco_loop_for_info_transparent, Enums.InfoLayoutType.INFO_VEHICLE);
                } else if (markerTitle.equals("YOU")) {
                    _SelectedTerminalMarkerTitle = null;
                }
                //samm user marker has been clicked
                else {
                    _SelectedTerminalMarkerTitle = null;
                    String STR_IconGetterFlag = marker.getTitle();
                    if (Helper.IsPossibleAdminBasedOnFirebaseUserKey(markerTitle))
                        STR_IconGetterFlag = marker.getSnippet() == null ? STR_IconGetterFlag : marker.getSnippet();

                    final UserMarker UM_result = new UserMarker(STR_IconGetterFlag, _context);
                    Handler HD_FetchFBName = new Handler();

                    if (UM_result.UserType == Enums.UserType.SAMM_FACEBOOK) {
                        HD_FetchFBName.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                GetFacebookUsername(markerTitle, UM_result);
                            }
                        }, 1500);
                    }
                    ShowInfoLayout(UM_result.UserTitle, UM_result.UserType.toString(), null, UM_result.UserInfoLayoutIcon, Enums.InfoLayoutType.INFO_PERSON);
                }
            }
                return true;
            }
        });
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void GetAndDisplayEloopETA(final Terminal TM_CurrentDestination) {
        try {
            String res = "";
                if (TM_CurrentDestination != null) {
                    final List<Terminal> L_TM_DestList_Sorted = new ArrayList<Terminal>(MenuActivity._terminalList);
                Collections.sort(L_TM_DestList_Sorted, Terminal.DestinationComparators.ORDER_OF_ARRIVAL);
                _vehicle_destinationsDBRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {

                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot DS_Vehicle_Destinations) {
                        ValidateIfEloopIsWithinSameRoute(TM_CurrentDestination, DS_Vehicle_Destinations, L_TM_DestList_Sorted);
                    }
                });


            }
        } catch (Exception ex) {

            Helper.logger(ex,true);
        }
    }
    private void ValidateIfEloopIsWithinSameRoute(final Terminal TM_CurrentDest, final DataSnapshot DS_Vehicle_Destination, final List<Terminal> L_TM_DestList_Sorted){
        try{
            _DriversDatabaseReference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot DS_Drivers) {
                    if (DS_Vehicle_Destination.getChildren() != null && DS_Drivers.getChildren() != null) {
                        Boolean found = false, loopAwaiting = false;
                        int ctr = 0;
                        _IsAllLoopParked = true;
                        for (Terminal TM_Entry : L_TM_DestList_Sorted) {
                            if (Helper.IsSameRoute(TM_Entry, TM_CurrentDest) && (TM_Entry.OrderOfArrival == TM_CurrentDest.OrderOfArrival)) {
                                for (DataSnapshot v : DS_Vehicle_Destination.getChildren()) {
                                    String StationName = v.getKey().toString(), StationNameWithTblRouteId = TM_Entry.Value + "_" + TM_Entry.getTblRouteID();
                                    if (StationNameWithTblRouteId.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                        loopAwaiting = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? true : false;
                                        String loopId = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? Helper.CleanEloopName(v.child("Dwell").getValue().toString()) : Helper.CleanEloopName(v.child("LoopIds").getValue().toString());
                                        if (loopAwaiting && Helper.IsEloopWithinSameRouteID(DS_Drivers, TM_CurrentDest, loopId)) {
                                            loopAwaiting = true;
                                            _IsAllLoopParked = false;
                                            found = true;
                                            GetTimeRemainingFromGoogle(Integer.parseInt(loopId), TM_CurrentDest);
                                        } else continue;

                                    }

                                }
                                if (loopAwaiting)
                                    break;
                            } else if (Helper.IsSameRoute(TM_Entry, TM_CurrentDest) && (TM_Entry.OrderOfArrival == 1 || TM_CurrentDest.OrderOfArrival == 1)) {
                                for (Terminal dl2 : L_TM_DestList_Sorted) {
                                    for (DataSnapshot v : DS_Vehicle_Destination.getChildren()) {
                                        String StationName = v.getKey().toString(), StationNameWithTblRouteId = dl2.Value + "_" + TM_Entry.getTblRouteID();
                                        if (StationNameWithTblRouteId.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                            String loopId = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? Helper.CleanEloopName(v.child("Dwell").getValue().toString()) : Helper.CleanEloopName(v.child("LoopIds").getValue().toString());
                                            if ((!loopId.equals("") && !loopId.equals(",")) && !found) {
                                                List<String> temploopids = Arrays.asList(loopId.split(","));
                                                for (String tli : temploopids
                                                        ) {
                                                    if (!tli.equals(""))
                                                        _ListOfLoops.add(Integer.parseInt(tli));
                                                }
                                                Collections.sort(_ListOfLoops);
                                                if (_ListOfLoops.size() > 0 && Helper.IsEloopWithinSameRouteID(DS_Drivers, TM_CurrentDest, _ListOfLoops.get(0).toString())) {
                                                    _IsAllLoopParked = false;
                                                    found = true;
                                                    //VehicleDestinationDatabaseReference.removeEventListener(LoopArrivalEventListener);
                                                    //Jul-22
                                                    GetTimeRemainingFromGoogle(_ListOfLoops.get(0), TM_CurrentDest);
                                                    // Toast.makeText(_context, "if (order of arrival =0) hit!", Toast.LENGTH_LONG).show();

                                                }
                                                _ListOfLoops.clear();
                                                break;
                                            } else continue;
                                        } else continue;

                                    }
                                }
                                if (found)
                                    break;

                            } else if (Helper.IsSameRoute(TM_Entry, TM_CurrentDest) && (TM_Entry.OrderOfArrival < TM_CurrentDest.OrderOfArrival)) {
                                for (DataSnapshot v : DS_Vehicle_Destination.getChildren()) {
                                    String StationName = v.getKey().toString(), StationNameWithTblRouteId = TM_Entry.Value + "_" + TM_Entry.getTblRouteID();
                                    if (StationNameWithTblRouteId.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                        String loopId = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? Helper.CleanEloopName(v.child("Dwell").getValue().toString()) : Helper.CleanEloopName(v.child("LoopIds").getValue().toString());
                                        if ((!loopId.equals("") && !loopId.equals(",")) && !found) {
                                            List<String> temploopids = Arrays.asList(loopId.split(","));
                                            for (String tli : temploopids) {
                                                if (!tli.equals(""))
                                                    _ListOfLoops.add(Integer.parseInt(tli));
                                            }
                                            Collections.sort(_ListOfLoops);
                                            if (_ListOfLoops.size() > 0 && Helper.IsEloopWithinSameRouteID(DS_Drivers, TM_CurrentDest, _ListOfLoops.get(0).toString())) {
                                                found = true;
                                                _IsAllLoopParked = false;
                                                GetTimeRemainingFromGoogle(_ListOfLoops.get(0), TM_CurrentDest);
                                                //Toast.makeText(_context, "else if hit!", Toast.LENGTH_LONG).show();

                                                break;
                                            }
                                            _ListOfLoops.clear();
                                            break;
                                        } else continue;
                                    } else continue;

                                }
                                if (found)
                                    break;

                            } else continue;

                        }
                    }
                    if (_IsAllLoopParked) {
                        if(Helper.IsStringEqual(_SelectedTerminalMarkerTitle, TM_CurrentDest.getValue())) {
                            UpdateInfoPanelDetails(TM_CurrentDest.Description,
                                     _helper.getEmojiByUnicode(0x1F6BB)
                                            + " : " + _passengerCountInTerminal
                                            + " passenger(s) waiting",
                                    _helper.getEmojiByUnicode(0x1F68C)
                                            + " : No nearby E-loop found");
                        }
                    }

                }
            });
        }
        catch(Exception ex){

        }
    }

public void GetTimeRemainingFromGoogle(Integer INT_LoopID, final Terminal TM_Destination) {
    if (INT_LoopID != null) {
        _DriversDatabaseReference = _firebaseDB.getReference("drivers").child(INT_LoopID.toString());
        _DriversDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<Integer, Integer> destinationId_distance = new HashMap<>();
                String url = "https://maps.googleapis.com/maps/";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RetrofitMaps service = retrofit.create(RetrofitMaps.class);
                _BOOL_IsTerminalDataFetchOnGoing = true;
                _AssignedELoop = dataSnapshot.child("deviceid").getValue().toString();
                Call<Directions> call = service.getDistanceDuration("metric", TM_Destination.Lat + "," + TM_Destination.Lng, dataSnapshot.child("Lat").getValue() + "," + dataSnapshot.child("Lng").getValue(), "driving");
                call.enqueue(new Callback<Directions>() {
                    @Override
                    public void onResponse(Response<Directions> response, Retrofit retrofit) {
                        try {
                            for (int i = 0; i < response.body().getRoutes().size(); i++) {
                                String TimeofArrival = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();
                                String Distance = response.body().getRoutes().get(0).getLegs().get(0).getDistance().getText();
                                if(Helper.IsStringEqual(_SelectedTerminalMarkerTitle, TM_Destination.getValue())) {
                                    UpdateInfoPanelDetails(TM_Destination.Description, _helper.getEmojiByUnicode(0x1F6BB)
                                            + " : " + _passengerCountInTerminal + " passenger(s) waiting" ,
                                             _helper.getEmojiByUnicode(0x1F68C) + " : " + TimeofArrival + " (" + Distance + " away)");
                                }
                                _BOOL_IsTerminalDataFetchDone = true;
                            }
                        } catch (Exception ex) {
                            Log.d("onResponse", "There is an error");
                            Helper.logger(ex,true);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("onFailure", t.toString());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
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
            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements
        }
    }

    @Override
    public void onBackPressed() {
        if (_MainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            _MainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//                if (id == R.id.action_settings) {
//                    return true;
//                }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        try {
            int id = item.getItemId();
            FragmentManager fragment = getSupportFragmentManager();

            if (id == R.id.nav_logout) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }

                builder.setTitle(_GlobalResource.getString(R.string.USER_logout_dialog_title))
                        .setMessage(_GlobalResource.getString(R.string.USER_logout_confirm))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                PlayButtonClickSound();
                                try {
                                    FacebookSdk.sdkInitialize(MenuActivity.this);
                                    LoginManager.getInstance().logOut();
                                }
                                catch(Exception ex)
                                {
                                    _helper.logger(ex,true);

                                }

                                _sessionManager.logoutUser();
                                String username = _constants.GUEST_USERNAME_PREFIX + UUID.randomUUID().toString();
                                _sessionManager.CreateLoginSession(_constants.GUEST_FIRSTNAME, _constants.GUEST_LASTNAME, username, "", "", false, Constants.GUEST_USERTYPE);
                                finish();
                                startActivity(getIntent());
                                Toast.makeText(MenuActivity.this,_GlobalResource.getString(R.string.USER_logged_out), Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                PlayButtonClickSound();
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
            else if(id==R.id.nav_about){
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                fragment.beginTransaction().replace(R.id.content_frame, new AboutActivity()).commit();

            }
            else if (id==R.id.nav_login)
            {
                if(_sessionManager.getIsBeta() && !_sessionManager.getIsDeveloper())
                {
                    Toast.makeText(_context, _GlobalResource.getString(R.string.USER_login_not_available_in_beta), Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        LoginManager.getInstance().logOut();
                    }
                    catch(Exception ex)
                    {
                        _helper.logger(ex,true);

                    }

                    Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
            else if (id == R.id.nav_passengerpeakandlean)
            {
                _sessionManager.PassReportType(_constants.PASSENGER_REPORT_TYPE);
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                UpdateUI(Enums.UIType.APPBAR_MIN_HEIGHT);
                fragment.beginTransaction().replace(R.id.content_frame, new ReportsActivity()).commit();
            }
            else if (id == R.id.nav_ecolooppeakandlean)
            {
                _sessionManager.PassReportType(_constants.VEHICLE_REPORT_TYPE);
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                fragment.beginTransaction().replace(R.id.content_frame, new ReportsActivity()).commit();
            }
            else if(id == R.id.menu_home){
                UpdateUI(Enums.UIType.ADMIN_SHOW_MAPS_LINEARLAYOUT);
                Enums.GoogleMapType mapType = Enums.GoogleMapType.MAP_TYPE_NORMAL;
                try{
                    mapType = Enums.GoogleMapType.valueOf(_sessionManager.getMapStylePreference());
                }catch (Exception ex){
                    Helper.logger(ex);
                }
                SetMapType(_googleMap, mapType);
            }
            else if(id==R.id.menu_username) {
                if (!_sessionManager.isGuest()) {
                    UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                    fragment.beginTransaction().replace(R.id.content_frame, new UserProfileActivity()).commit();
                } else {
                    InfoDialog GuestInfo = new InfoDialog(MenuActivity.this, getResources().getString(R.string.guest_nav_drawer_message));
                    GuestInfo.show();
                }

            }
            else if (id==R.id.nav_superadminusers)
            {
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                SuperAdminUsersFragment superAdminUsersFragment = new SuperAdminUsersFragment();
                fragment.beginTransaction().replace(R.id.content_frame, superAdminUsersFragment).commit();
            }
            else if (id==R.id.nav_adminusers)
            {
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                AdminUsersFragment adminUsersFragment = new AdminUsersFragment();
                fragment.beginTransaction().replace(R.id.content_frame, adminUsersFragment).commit();
            }
            else if (id==R.id.nav_drivers)
            {
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                DriverUsersFragment driverUsersFragment = new DriverUsersFragment();
                fragment.beginTransaction().replace(R.id.content_frame, driverUsersFragment).commit();
            }

            else if(id==R.id.nav_lines)
            {
                UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                Intent addRouteIntent = new Intent(MenuActivity.this, ManageRoutesActivity.class);
                startActivity(addRouteIntent);
                Intent addLineIntent = new Intent(MenuActivity.this, ManageLinesActivity.class);
                startActivity(addLineIntent);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                finish();
            }
            else if (id == R.id.nav_vehicles)
            {
                try {
                    UpdateUI(Enums.UIType.ADMIN_HIDE_MAPS_LINEARLAYOUT);
                    fragment.beginTransaction().replace(R.id.content_frame, new ViewGPSFragment()).commit();
                }
                catch(Exception ex)
                {
                    Helper.logger(ex,true);
                }
            }

            PlayButtonClickSound();
            _MainDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);

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
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
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

//    private void updatePassengerCountForReport(String username, String terminal)
//    {
//        Log.i(LOG_TAG, "Updating passenger count for reports...");
//        new mySQLUpdateWaitingPassengerHistory(getApplicationContext(), this).execute(username, terminal);
//    }
    public void updatePassengerCountForReport(String terminal, long numberOfWaitingPassengers)
    {
        Log.i(LOG_TAG, "Updating passenger count for reports...");
        new mySQLUpdateWaitingPassengerHistory(getApplicationContext(), this).execute(terminal,  Long.toString(numberOfWaitingPassengers));
    }
    private void passengerMovement(final String destinationValue, final String movement)
    {
        Log.i(LOG_TAG, "Saving passenger movement to firebase...");
        final HashMap<String, Object> count = new HashMap<>();
        final HashMap<String, Object> hashmapCount = new HashMap<>();
        final String uid = _sessionManager.getUsername();

        _terminalsDBRef.child(destinationValue).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot == null || dataSnapshot.getValue() == null)
//                {
                if(movement.toLowerCase().equals("entered"))
                {
                    _terminalsDBRef.child(destinationValue).child(uid).setValue(true);
                    //updatePassengerCountForReport(_sessionManager.getUsername(), destinationValue);
                }

//                }
                else if(movement.toLowerCase().equals("exit")){
                    _terminalsDBRef.child(destinationValue).child(uid).removeValue();
                }
                else if (movement.toLowerCase().equals("entered"))
                {
                    //updatePassengerCountForReport(_sessionManager.getUsername(), destinationValue);
                }
//                _passengerCount.remove(destinationValue);
//                _passengerCount.put(destinationValue, dataSnapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    private void passengerMovement_withNoRunTransaction(final String destinationValue, final String movement)
    {
        Log.i(LOG_TAG, "Saving passenger movement to firebase...");
        final HashMap<String, Object> count = new HashMap<>();
        final HashMap<String, Object> hashmapCount = new HashMap<>();
        final String uid = _sessionManager.getUsername();

        _firebaseDB.getReference().child("terminals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    _terminalsDBRef.runTransaction(new Transaction.Handler()
                    {

                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            return null;
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
                }
                else
                {
                    _firebaseDB.getReference("terminals").runTransaction(new Transaction.Handler()
                    {

                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            try
                            {
                                HashMap<String, HashMap<String, Boolean>> test = new HashMap<>();
                                HashMap<String, Boolean> test2 = new HashMap<>();
                                test2.put("samm", true);
                                test.put("terminals", test2);
                                mutableData.child("terminals").child("CapitalOne").setValue(test2);
                                return Transaction.success(mutableData);
                            }
                            catch(Exception e) {
                                _helper.logger(e,true);

                            }
                            return null;

                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            DataSnapshot d = dataSnapshot;

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        _terminalsDBRef.child(destinationValue).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot == null || dataSnapshot.getValue() == null)
//                {


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    @Override
    public Drawable getDrawable(String arg0) {
        int id = 0;
        //string = _drawable

        if(arg0.equals("ic_walking.png")){
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
                                if(_googleMap !=null) {
                                    String TimeofArrival = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();
                                    MarkerOptions markerOpt = new MarkerOptions();
                                    LatLng latLng = new LatLng(Looploc.latitude, Looploc.longitude);
                                    markerOpt.position(latLng);
                                    markerOpt.title(TimeofArrival);
                                    markerOpt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ecoloop));
                                    markerOpt.anchor(0.5f, 0.5f);
                                    markerOpt.rotation(bearing);
                                    if(_vehicleMarker ==null){
                                        _vehicleMarker = _googleMap.addMarker(markerOpt);
                                    }
                                    _vehicleMarker.showInfoWindow();
                                    _vehicleMarker.setPosition(latLng);
                                    _TimeOfArrivalTextView.setText(deviceid + " - " + TimeofArrival.toString());
                                }
                            }
                            //_markeropt.title(response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText());
                        } catch (Exception e) {
                            _helper.logger(e,true);

                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d(_constants.LOG_TAG, t.toString());
                    }
                });
                //return _selectedPickUpPoint.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDuration().getText();
            }
        } catch(Exception ex){
            _helper.logger(ex,true);

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
            for(Terminal d: _terminalList)
            {
                if (d.GeofenceId.equals(geofenceRequestId))
                {
                    passengerMovement(d.Value, eventType);
                    Toast.makeText(context, "You " + eventType + " " +  d.Description, Toast.LENGTH_LONG).show();
                }

            }
        }

    }
    public class SentSMSBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        if (_smsMessageForGPS.equals(_constants.SMS_BEGIN)) {
                            _ProgressDialog.setMessage(_GlobalResource.getString(R.string.SMS_activating_gprs));
                            sendSMSMessage(_constants.SMS_GPRS, _GPSMobileNumber);
                        }
                        else if (_smsMessageForGPS.equals(_constants.SMS_GPRS)) {
                            if(_isGPSReconnect) {
                                _ReconnectGPSButton.setText(_GlobalResource.getString(R.string.SMS_button_reconnect));
                                _ReconnectGPSButton.setEnabled(true);
                            }
                            else {
                                _ProgressDialog.setMessage(_GlobalResource.getString(R.string.SMS_setting_apn));
                                sendSMSMessage(_smsAPN, _GPSMobileNumber);
                            }
                        }
                        else if (_smsMessageForGPS.equals(_smsAPN)) {
                            if(_isGPSReconnect) {
                                sendSMSMessage(_constants.SMS_GPRS, _GPSMobileNumber);
                            }
                            else {
                                _ProgressDialog.setMessage(_GlobalResource.getString(R.string.SMS_configuring_ip_and_port));
                                sendSMSMessage(_constants.SMS_ADMINIP, _GPSMobileNumber);
                            }

                        }
                        else if (_smsMessageForGPS.equals(_constants.SMS_ADMINIP)) {
                            _ProgressDialog.setMessage(_GlobalResource.getString(R.string.SMS_setting_automatic_location_updates));
                            sendSMSMessage(_constants.SMS_TIMEINTERVAL, _GPSMobileNumber);
                        }
                        else if (_smsMessageForGPS.equals(_constants.SMS_TIMEINTERVAL)) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MenuActivity.this);
                            alertDialogBuilder.setPositiveButton(_GlobalResource.getText(R.string.SMS_button_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            alertDialogBuilder.setTitle(_GlobalResource.getString(R.string.dialog_status_success));
                            alertDialogBuilder.setMessage(_GlobalResource.getString(R.string.SMS_successfully_added_GPS));
                            alertDialogBuilder.show();

                            _ProgressDialog.dismiss();

                            //new asyncAddTraccarGPS(getApplicationContext(), _ProgressDialog, MenuActivity.this, _dialog).execute("SAMM_"+ _gpsIMEI.substring(_gpsIMEI.length()-5, _gpsIMEI.length()), _gpsIMEI, _GPSMobileNumber, _gpsNetwork, _gpsPlateNumber, _gpsTblRoutesID.toString(), _gpsTblUsersID.toString());
                        }
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        if(_isGPSReconnect)
                        {
                            _ReconnectGPSButton.setEnabled(true);
                            _ReconnectGPSButton.setText(_GlobalResource.getString(R.string.SMS_button_reconnect));
                        }
                        else
                        {
                            _ProgressDialog.dismiss();
                        }
                        Toast.makeText(context, _GlobalResource.getString(R.string.SMS_error_encountered_in_adding_GPS), Toast.LENGTH_SHORT).show();

                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        if(_isGPSReconnect)
                        {
                            _ReconnectGPSButton.setEnabled(true);
                            _ReconnectGPSButton.setText(_GlobalResource.getString(R.string.SMS_button_reconnect));
                        }
                        else
                        {
                            _ProgressDialog.dismiss();
                        }
                        Toast.makeText(context, _GlobalResource.getString(R.string.SMS_error_encountered_in_adding_GPS), Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        if(_isGPSReconnect)
                        {
                            _ReconnectGPSButton.setEnabled(true);
                            _ReconnectGPSButton.setText(_GlobalResource.getString(R.string.SMS_button_reconnect));
                        }
                        else
                        {
                            _ProgressDialog.dismiss();
                        }
                        Toast.makeText(context, _GlobalResource.getString(R.string.SMS_error_encountered_in_adding_GPS), Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        if(_isGPSReconnect)
                        {
                            _ReconnectGPSButton.setEnabled(true);
                            _ReconnectGPSButton.setText(_GlobalResource.getString(R.string.SMS_button_reconnect));
                        }
                        else
                        {
                            _ProgressDialog.dismiss();
                        }
                        Toast.makeText(context, _GlobalResource.getString(R.string.SMS_error_encountered_in_adding_GPS), Toast.LENGTH_SHORT).show();
                        break;
                }


            }
            catch(Exception ex)
            {
                _helper.logger(ex,true);

            }

        }
    }
    public class SMSDeliveredBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getApplicationContext(), _GlobalResource.getString(R.string.SMS_status_delivered), Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getApplicationContext(), _GlobalResource.getString(R.string.SMS_status_not_delivered), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    private void initializeOnlinePresence() {
        // any time that connectionsRef's value is null, device is offline
        Log.i(LOG_TAG, "Initializing online presence...");
        String node="";
        if (_sessionManager.isDriver()) {
            node ="drivers/" + _sessionManager.getLastName();
        }
        else {
            node="users/" + _sessionManager.getUsername();
        }
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference userRef = database.getReference(node);
        final DatabaseReference myConnectionsRef = database.getReference(node + "/connections");

        // stores the timestamp of last online
        final DatabaseReference lastOnlineRef = database.getReference("/"+node + "/lastOnline");

        final DatabaseReference connectedRef = database.getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    // when this device disconnects, remove it
                    if(_sessionManager.isGuest())
                        userRef.onDisconnect().removeValue();
                    else
                    {
                        myConnectionsRef.onDisconnect().setValue(false);
                        // update the last online timestamp
                        lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }
                    // add this device to connections list
                    myConnectionsRef.setValue(true);

                    final DatabaseReference terminalDBRef = database.getReference("terminals");
                    terminalDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot:dataSnapshot.getChildren())
                            {
                                terminalDBRef.child(snapshot.getKey().toString()).child(_sessionManager.getUsername()).onDisconnect().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });
    }


    public void sendSMSMessage(String message, String phone) {
        try
        {

            Log.i(LOG_TAG, _GlobalResource.getString(R.string.SMS_sending_to_add_new_gps));
            this._smsMessageForGPS = message;
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                    Log.i(_constants.LOG_TAG,_GlobalResource.getString(R.string.SMS_status_sending_with_extra_white_space) + this._smsMessageForGPS + _GlobalResource.getString(R.string.ellipsis));
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
            else
            {
                Log.i(_constants.LOG_TAG,_GlobalResource.getString(R.string.SMS_status_sending_with_extra_white_space)  + this._smsMessageForGPS + _GlobalResource.getString(R.string.ellipsis));
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, this._smsMessageForGPS, _sentSMSPendingIntent, _deliveredSMSPendingIntent);
                Log.i(_constants.LOG_TAG, message + _GlobalResource.getString(R.string.SMS_status_sent_with_extra_white_space_prefix));
            }
        }
        catch(Exception ex)
        {
            _helper.logger(ex,true);

            _ProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(),_GlobalResource.getString(R.string.error_exception_with_concat) + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void sendSMSMessage(String message, String phone, Button btnReconnectGPS) {
        try
        {
            Log.i(LOG_TAG, _GlobalResource.getString(R.string.SMS_sending_to_reconnect_gps));
            this._smsMessageForGPS = message;
            this._GPSMobileNumber = phone;

            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    Log.i(_constants.LOG_TAG,_GlobalResource.getString(R.string.SMS_status_sending_with_extra_white_space)+ this._smsMessageForGPS + _GlobalResource.getString(R.string.ellipsis));
//                    Toast.makeText(getApplicationContext(), "sending " + this._smsMessageForGPS, Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
            else
            {
                Log.i(_constants.LOG_TAG,_GlobalResource.getString(R.string.SMS_status_sending_with_extra_white_space) + this._smsMessageForGPS + _GlobalResource.getString(R.string.ellipsis));
//                Toast.makeText(getApplicationContext(), "sending " + this._smsMessageForGPS, Toast.LENGTH_LONG).show();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, this._smsMessageForGPS, _sentSMSPendingIntent, _deliveredSMSPendingIntent);
                this._ReconnectGPSButton = btnReconnectGPS;

                Log.i(_constants.LOG_TAG, message + _GlobalResource.getString(R.string.SMS_status_sent_with_extra_white_space_prefix));
//                Toast.makeText(getApplicationContext(),this._smsMessageForGPS + " sent", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception ex)
        {
            _helper.logger(ex,true);
            _ProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(),_GlobalResource.getString(R.string.error_exception_with_concat) + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
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
                bitmap = BitmapFactory.decodeStream((InputStream) fetch(imageURL.toString()), null, options);
            } catch (OutOfMemoryError e){
                try{
                    options.inSampleSize = 2;
                    bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    return bitmap;
                }catch (Exception ex){

                }

            }catch (IOException exc){

            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result!=null) {
                _ProfilePictureImg.setImageBitmap(result);
            }
        }
        private InputStream fetch(String address) throws MalformedURLException,IOException {
            HttpGet httpRequest = new HttpGet(URI.create(address) );
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream instream = bufHttpEntity.getContent();
            return instream;
        }
    }


    public class AddPointDialog extends Dialog implements
            android.view.View.OnClickListener {
        View myView;
        String _action;
        Button btnAddPoints, btnDeletePoints;
        EditText editName;
        EditText editLat;
        EditText editLng;
        Spinner spinnerPrePosition, spinnerTerminalReference;
        String _destinationValueForEdit = "";
        public String TAG = "mead";


        public AddPointDialog(Activity activity, String action) {
            super(activity);
            this._action = action.toLowerCase();
        }
        public AddPointDialog(Activity activity, String action, String destinationValueforEdit) {
            super(activity);
            this._action = action.toLowerCase();
            this._destinationValueForEdit = destinationValueforEdit;
        }


        @Override
        public void onClick(View view) {
            PlayButtonClickSound();
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            try {
                requestWindowFeature(Window.FEATURE_NO_TITLE);

                setContentView(R.layout.dialog_add_point);
                btnAddPoints = (Button) findViewById(R.id.btnAddPoint);

                editName = (EditText) findViewById(R.id.terminalName);
                editLat = (EditText) findViewById(R.id.lat);
                editLng = (EditText) findViewById(R.id.lng);
                TextView txtAction = (TextView) findViewById(R.id.txtActionLabel);
                final TextView txtDestinationIDForEdit = (TextView) findViewById(R.id.txtDestinationIDForEdit);
                spinnerPrePosition = (Spinner) findViewById(R.id.spinner_preposition);
                spinnerTerminalReference = (Spinner) findViewById(R.id.spinner_terminalReference);
                Integer orderOfArrival = 0;
                final Integer destinationIDforEdit=0;

                ArrayAdapter<Terminal> terminalReferencesAdapter = new ArrayAdapter<Terminal>(getApplicationContext(), R.layout.spinner_item, MenuActivity._terminalList);

                spinnerTerminalReference.setAdapter(terminalReferencesAdapter);
                if(_action.equals("add"))
                {
                    btnAddPoints.setText("ADD");
                    txtAction.setText("ADD NEW PICKUP/DROPOFF POINT");
                    btnDeletePoints.setVisibility(View.GONE);
                }
                else {

                    btnAddPoints.setText("UPDATE");

                    for(Terminal d: _terminalList)
                    {
                        if (d.Value.equals(_destinationValueForEdit))
                        {
                            txtDestinationIDForEdit.setText(String.valueOf(d.ID));
                            editName.setText(d.Description);
                            editLat.setText(d.Lat.toString());
                            editLng.setText(d.Lng.toString());
                            orderOfArrival = d.OrderOfArrival;

                            break;
                        }
                    }
                    txtAction.setText("EDIT PICKUP/DROPOFF POINT");
                    int index = 0;
                    for(Terminal d: _terminalList)
                    {
                        if (d.OrderOfArrival == orderOfArrival + 1)
                        {
                            spinnerPrePosition.setSelection(0);
                            spinnerTerminalReference.setSelection(index);
                            break;
                        }else if (d.OrderOfArrival == orderOfArrival - 1)
                        {
                            spinnerPrePosition.setSelection(1);
                            spinnerTerminalReference.setSelection(index);
                            break;
                        }
                        index++;
                    }

                }
                final DialogInterface.OnClickListener dialog_deletepoint = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                PlayButtonClickSound();
                                deletePoint(Integer.parseInt(txtDestinationIDForEdit.getText().toString()));
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                PlayButtonClickSound();
                                //No button clicked
                                break;
                        }
                    }
                };
                btnDeletePoints.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialog_deletepoint)
                                    .setNegativeButton("No", dialog_deletepoint).show();

                        }
                        catch(Exception ex)
                        {
                            _helper.logger(ex,true);

                        }

                    }
                });
                btnAddPoints.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(_action.equals("add")) {
                            PlayButtonClickSound();
                            String name = editName.getText().toString();
                            String lat = editLat.getText().toString();
                            String lng = editLng.getText().toString();
                            String preposition = spinnerPrePosition.getSelectedItem().toString();
                            Terminal terminalReference = (Terminal) spinnerTerminalReference.getSelectedItem();
                            if (name.trim().length() == 0 || lat.trim().length() == 0 || lng.trim().length() == 0 || preposition.trim().length() == 0 || terminalReference == null) {
                                Toast.makeText(getApplicationContext(), _GlobalResource.getString(R.string.error_please_supply_all_fields), Toast.LENGTH_LONG).show();
                            } else {

                                savePoint(name, Double.parseDouble(lat), Double.parseDouble(lng), preposition, terminalReference);
                                AddPointDialog.this.dismiss();
                            }
                        }
                        else if(_action.equals("update"))
                        {
                            PlayButtonClickSound();
                            String name = editName.getText().toString();
                            String lat = editLat.getText().toString();
                            String lng = editLng.getText().toString();
                            String preposition = spinnerPrePosition.getSelectedItem().toString();
                            Integer destinationID = Integer.parseInt(txtDestinationIDForEdit.getText().toString());
                            Terminal terminalReference = (Terminal) spinnerTerminalReference.getSelectedItem();
                            if (name.trim().length() == 0 || lat.trim().length() == 0 || lng.trim().length() == 0 || preposition.trim().length() == 0 || terminalReference == null) {
                                Toast.makeText(getApplicationContext(), _GlobalResource.getString(R.string.error_please_supply_all_fields), Toast.LENGTH_LONG).show();
                            } else {

                                updatePoint(destinationID, name, Double.parseDouble(lat), Double.parseDouble(lng), preposition, terminalReference);
                                AddPointDialog.this.dismiss();
                            }
                        }

                    }
                });
            }catch(Exception e)
            {
                _helper.logger(e,true);

            }

            _LoaderDialog.dismiss();
        }

        private void savePoint(String name, Double lat, Double lng, String preposition, Terminal terminalReference)
        {
            _LoaderDialog = new LoaderDialog(MenuActivity.this, _GlobalResource.getString(R.string.GPS_adding),_GlobalResource.getString(R.string.GPS_updating_points_on_map));
            _LoaderDialog.show();
            new asyncAddPoints(getApplicationContext(), _LoaderDialog, MenuActivity.this, _googleMap, _googleAPI,_GlobalResource.getString(R.string.GPS_add), 0).execute(name, lat.toString(), lng.toString(), preposition, String.valueOf(terminalReference.ID));
        }
        private void updatePoint(Integer ID, String name, Double lat, Double lng, String preposition, Terminal terminalReference)
        {
            _LoaderDialog = new LoaderDialog(MenuActivity.this, _GlobalResource.getString(R.string.GPS_updating), _GlobalResource.getString(R.string.GPS_updating_points_on_map));
            _LoaderDialog.show();
            new asyncAddPoints(getApplicationContext(), _LoaderDialog, MenuActivity.this, _googleMap, _googleAPI, _GlobalResource.getString(R.string.GPS_update) , ID).execute(name, lat.toString(), lng.toString(), preposition, String.valueOf(terminalReference.ID));
        }
        private void deletePoint(Integer ID)
        {
            _LoaderDialog = new LoaderDialog(MenuActivity.this, _GlobalResource.getString(R.string.GPS_deleting), _GlobalResource.getString(R.string.GPS_updating_points_on_map));
            _LoaderDialog.show();
            new asyncAddPoints(getApplicationContext(), _LoaderDialog, MenuActivity.this, _googleMap, _googleAPI, _GlobalResource.getString(R.string.GPS_delete) , ID).execute();
        }
    }

    public class AddGPSDialog extends Dialog implements
            android.view.View.OnClickListener {

        Button btnAddGPS;
        EditText txtphoneNo, txtIMEI, txtPlateNumber;
        Spinner spinnerNetworks, spinnerRoutes, spinnerDrivers;


        public String TAG ="mead";

        PendingIntent sentPendingIntent;
        PendingIntent deliveredPendingIntent;
        public HashMap<String, Boolean> smsCommandsStatus = new HashMap<>();
        public final Activity _activity;
        public AddGPSDialog(Activity activity) {
            super(activity);
            // TODO Auto-generated constructor stub
            this._activity = activity;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_add_gps);
            ArrayList<String> networkList = new ArrayList<>();
            ArrayList<Users> driverAdapterList = new ArrayList<>();
            ArrayList<Routes> routesAdapterList = new ArrayList<>();

            driverAdapterList.add(new Users(0, _GlobalResource.getString(R.string.GPS_select_driver), "", "","","Driver", "", 1));
            routesAdapterList.add(new Routes(0, 0,_GlobalResource.getString(R.string.GPS_select_route)));

            driverAdapterList.addAll(MenuActivity._driverList);
            routesAdapterList.addAll(MenuActivity._routeList);


            ArrayAdapter<Users> driverListAdapter = new ArrayAdapter<Users>(getContext(), R.layout.spinner_item, driverAdapterList)
            {

                @Override
                public boolean isEnabled(int position)
                {
                    if (position == 0)
                        return false;
                    else
                        return true;
                }
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent)
                {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;

                    if(position==0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
                    }
                    return view;
                }
            };
            ArrayAdapter<Routes> routesListAdapter = new ArrayAdapter<Routes>(getContext(), R.layout.spinner_item, routesAdapterList)
            {
                @Override
                public boolean isEnabled(int position)
                {
                    if (position == 0)
                        return false;
                    else
                        return true;
                }
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent)
                {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(position==0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
                    }
                    return view;
                }
            };

            spinnerNetworks = (Spinner)findViewById(R.id.spinnerNetworks);
            spinnerRoutes = (Spinner) findViewById(id.spinnerRoutes);
            spinnerDrivers = (Spinner) findViewById(id.spinnerDrivers);


            networkList.add(_GlobalResource.getString(R.string.SMS_select_GPS_network));
            networkList.add(_GlobalResource.getString(R.string.NETWORK_GLOBE));
            networkList.add(_GlobalResource.getString(R.string.NETWORK_SMART));


            ArrayAdapter<String> networkProvidersAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, networkList){
                @Override
                public boolean isEnabled(int position)
                {
                    if (position == 0)
                        return false;
                    else
                        return true;
                }
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent)
                {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;

                    if(position==0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
                    }
                    return view;
                }

            };
            spinnerNetworks.setAdapter(networkProvidersAdapter);
            spinnerDrivers.setAdapter(driverListAdapter);
            spinnerRoutes.setAdapter(routesListAdapter);

            btnAddGPS = (Button) findViewById(R.id.btnAddGPS);
            txtphoneNo = (EditText) findViewById(R.id.GPSMobileNum);
            txtIMEI  = (EditText) findViewById(R.id.GPSIMEI);
            txtPlateNumber = (EditText) findViewById(id.plateNumber);
            btnAddGPS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try
                    {
                        PlayButtonClickSound();
                        _GPSMobileNumber = txtphoneNo.getText().toString().trim();
                        _gpsIMEI = txtIMEI.getText().toString().trim();
                        _gpsPlateNumber = txtPlateNumber.getText().toString().trim();
                        _gpsNetwork = spinnerNetworks.getSelectedItem().toString();
                        _gpsTblRoutesID = ((Routes)spinnerRoutes.getSelectedItem()).getID();
                        _gpsTblUsersID = ((Users)spinnerDrivers.getSelectedItem()).ID;
                        if(_GPSMobileNumber.trim().length() == 0 || _gpsIMEI.trim().length() == 0 || spinnerNetworks.getSelectedItem().toString().equals(_GlobalResource.getString(R.string.SMS_please_select_network_provider)))
                        {
                            Toast.makeText(getContext(), _GlobalResource.getString(R.string.error_please_supply_all_fields), Toast.LENGTH_LONG).show();
                        }
                        else {
                            if (spinnerNetworks.getSelectedItem().toString().equals("Globe"))
                                _smsAPN = _constants.SMS_APN_GLOBE;
                            else
                                _smsAPN = _constants.SMS_APN_SMART;
                            _ProgressDialog.show();

                            //TO DO: Call AsynTask class
                            //Add to MySQL SAMM Database
                            //Add to Traccar Server
                            //Send SMS to activate the GPS
                            new asyncAddTraccarGPS(getApplicationContext(), _LoaderDialog, MenuActivity.this, AddGPSDialog.this).execute("SAMM_"+ _gpsIMEI.substring(_gpsIMEI.length()-5, _gpsIMEI.length()), _gpsIMEI, _GPSMobileNumber, _gpsNetwork, _gpsPlateNumber, _gpsTblRoutesID.toString(), _gpsTblUsersID.toString());
                        }
                    }catch(Exception ex)
                    {
                        _helper.logger(ex,true);
                        Toast.makeText(getContext(), _GlobalResource.getString(R.string.error_an_error_occurred), Toast.LENGTH_LONG).show();
                    }
                }
            });


        }


        @Override
        public void onClick(View view) {

        }
    }

    public static Boolean IsNight(){
        Integer currentTime = Calendar.getInstance().getTime().getHours();
        return (currentTime >= 18 || currentTime <=5)? true: false;
    }
    public void BuildToolTip(String text, Activity activity, View view, int gravity, int highlightShape, Boolean hasOverlay){
        new SimpleTooltip.Builder(activity)
                .anchorView(view)
                .text(text)
                .gravity(gravity)
                .animated(true)
                .transparentOverlay(hasOverlay)
                .backgroundColor(Color.WHITE)
                .arrowColor(Color.WHITE)
                .highlightShape(highlightShape)
                .build()
                .show();
    }
    public void ShowGPSLoadingInfo(String STR_message, Boolean IsVisible){
        int Visibility = IsVisible ? View.VISIBLE : View.INVISIBLE;
        if(!IsVisible) {
            if(_BOOL_IsGoogleMapShownAndAppIsOnHomeScreen)
                MenuActivity._SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            if(_sessionManager.getIsAdmin())
                MenuActivity._AdminToolsFloatingMenu.setPadding(0,0,0,_helper.dpToPx(80,_context));
            final Handler HND_ShowPanel = new Handler();
            HND_ShowPanel.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UpdateUI(Enums.UIType.APPBAR_MIN_HEIGHT);
                    UpdateUI(Enums.UIType.MAIN);
                    _SL_AppBar_Top_TextView.stopShimmerAnimation();
                }
            }, 2000);
        }
        else {
            MenuActivity._BOOL_IsGPSAcquired=false;
            _AppBarLayout.height = _helper.dpToPx(20,_context);
            _AppBar.setLayoutParams(_AppBarLayout);
            _AppBar.setVisibility(Visibility);
            MenuActivity._SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
        _SL_AppBar_Top_TextView.startShimmerAnimation();
        _DestinationTextView.setVisibility(View.VISIBLE);
        _DestinationTextView.setText(STR_message!=null? STR_message.toUpperCase(): null);
        _DestinationTextView.setBackgroundResource(IsVisible ? R.color.colorElectronBlue : R.color.colorWebFlatGreen);

    }
    public void UpdateUI(Enums.UIType type){
        _LL_MapStyleHolder.setVisibility(View.INVISIBLE);
        if(_sessionManager != null && _sessionManager.getMainTutorialStatus() != null) {
            switch (type) {
                case MAIN:
                    if (!_sessionManager.getMainTutorialStatus() && _BOOL_IsGPSAcquired && _BOOL_IsGoogleMapShownAndAppIsOnHomeScreen) {
                        BuildToolTip(_GlobalResource.getString(R.string.ToolTip_show_menu_options), this, FAB_SammIcon, Gravity.END, OverlayView.HIGHLIGHT_SHAPE_OVAL, false);
                        BuildToolTip(_GlobalResource.getString(R.string.ToolTip_search_here), this, FrameSearchBarHolder, Gravity.TOP, OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR, false);
                        _sessionManager.TutorialStatus(Enums.UIType.MAIN, true);
                    }
                    break;
                case SHOWING_ROUTES:
                    if (!_sessionManager.getRouteTutorialStatus()) {
                        BuildToolTip(_GlobalResource.getString(R.string.ToolTip_search_again), this, Search_BackBtn, Gravity.END, OverlayView.HIGHLIGHT_SHAPE_OVAL, false);
                        BuildToolTip(_GlobalResource.getString(R.string.ToolTip_navigation_instructions), this, _RoutesPane, Gravity.TOP, OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR, false);
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
                    if(!_BOOL_IsGPSAcquired)
                        ShowGPSLoadingInfo(_GlobalResource.getString(R.string.GM_acquiring_gps), true);
                    if(_BOOL_IsGPSAcquired && _BOOL_IsGoogleMapShownAndAppIsOnHomeScreen) {
                        _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        _RoutesPane.setVisibility(View.VISIBLE);
                    }
                    break;
                case APPBAR_MIN_HEIGHT:
                    _AppBarLayout.height = 0;
                    _AppBar.setLayoutParams(_AppBarLayout);
                    break;
                default:
                    break;
            }
        }
    }
    public void ShowRouteTabsAndSlidingPanel(){
        FrameSearchBarHolder.setVisibility(View.INVISIBLE);
        _SL_Map_Fragment.stopShimmerAnimation();
        if(_sessionManager.isDriver()){
            _SlideUpPanelContainer.setTouchEnabled(true);
            _SlideUpPanelContainer.setPanelHeight(220);
            _RoutesPane.setVisibility(View.VISIBLE);
            _TimeOfArrivalTextView.setVisibility(View.VISIBLE);
            _StepsScroller.setVisibility(View.GONE);
        }
        else {
            _SlideUpPanelContainer.setTouchEnabled(true);
            _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            _AppBarLayout.height = Helper.dpToPx(60,_context);
            _AppBar.setLayoutParams(_AppBarLayout);
            _RouteTabLayout.setVisibility(View.VISIBLE);
            _RoutesPane.setVisibility(View.VISIBLE);
            _RouteStepsText.setVisibility(View.VISIBLE);
            FAB_SammIcon.setVisibility(View.INVISIBLE);
            Search_BackBtn.setVisibility(View.VISIBLE);
            _IsOnSearchMode = true;
        }
    }
    public void HideRouteTabsAndSlidingPanel(){
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
                _IsOnSearchMode=false;
                _selectedPickUpPoint = null;

                asyncPrepareRouteData.RemoveListenerFromLoop();
                _HasExitedInfoLayout = true;
                _SL_Vehicle_ETA.stopShimmerAnimation();
                _SL_Map_Fragment.startShimmerAnimation();
                Helper.InitializeSearchingRouteUI(true, false, null, null, null ,_context);
                _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }, 1000);
        //_SlideUpPanelContainer.setPanelHeight(_helper.dpToPx(60,_context));
        _SlideUpPanelContainer.setTouchEnabled(false);

    }
    public void SetMapType(GoogleMap gmap, Enums.GoogleMapType mapType){
        UnselectMapTypes();
        switch(mapType){
            case MAP_TYPE_NORMAL: gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Integer mapsStyle = IsNight() ? R.raw.night_maps_style_darker: R.raw.maps_style;
                boolean success = _googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, mapsStyle));

                if (!success) {
                    _helper.logger(_GlobalResource.getString(R.string.GM_Style_Parse_Failed));
                }
                _IB_MapType_Normal.setBackgroundResource(R.drawable.selected_border);
                break;
            case MAP_TYPE_HYBRID: gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                _IB_MapType_Hybrid.setBackgroundResource(R.drawable.selected_border);
                break;
            case MAP_TYPE_TERRAIN:   gmap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                _IB_MapType_Terrain.setBackgroundResource(R.drawable.selected_border);
                break;
            case MAP_TYPE_SATELLITE:gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                _IB_MapType_Satellite.setBackgroundResource(R.drawable.selected_border);
                break;
            default:gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
        for(ImageButton IB_entry: IB_MapStyleList){
            IB_entry.setBackgroundResource(0);
        }
    }

    //Public methods for showing Dialogs from fragments which are previously attached to FABs onclick events.

    public void ShowInfoLayout(String Title, String Description, String Description2, Integer Icon, Enums.InfoLayoutType InfoType){
        try{
        ClearInfoPanelDetails();ToggleMapTools();
        InfoPanelShow(Title, Description,Description2, Icon,InfoType);
        }
        catch(Exception ex){
            Toast.makeText(MenuActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void InitializeInfoPanel(){
        try {
            _infoTitleTV = (TextView) findViewById(R.id.TextView_InfoTitle);
            _infoDescriptionTV = (TextView) findViewById(R.id.TextView_InfoDesc);
            _infoDescriptionTV2 = (TextView) findViewById(id.TextView_InfoDesc2);
            _infoLayout = (LinearLayout) findViewById(R.id.Info_Layout);
            _infoPanelBtnClose = (ImageButton) findViewById(id.btnCloseInfoPanel);
            _infoTitleTV.setVisibility(View.INVISIBLE);
            _infoDescriptionTV.setVisibility(View.INVISIBLE);
            _infoDescriptionTV2.setVisibility(View.INVISIBLE);
            _infoImage = (ImageView) findViewById(id.ImageView_InfoImage);
            _infoPanelBtnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlayButtonClickSound();
                    InfoPanelHide();
                }
            });


        } catch (Exception ex) {
            Toast.makeText(MenuActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void InfoPanelShow(final String InfoTitle, final String InfoDescription, final String InfoDescription2,final Integer Icon, final Enums.InfoLayoutType infoType) {
            //final int imageResId = isStation ? R.drawable.ic_ecoloopstop_for_info : R.drawable.eco_loop_for_info_transparent;
            final String finalTitle =  InfoTitle;
            final Typeface finalTitleFont = GetFontStyle(infoType);
            _infoTitleTV.setTypeface(null);
            if (_infoLayout.getVisibility() == View.INVISIBLE) {
                if(!_IsOnSearchMode)
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
                                UpdateInfoPanelDetails(finalTitle,InfoDescription, InfoDescription2);
                                _infoImage.setImageResource(Icon);
                                _infoTitleTV.setTypeface(finalTitleFont);
                                _infoTitleTV.setVisibility(View.VISIBLE);
                                _infoDescriptionTV.setVisibility(View.VISIBLE);
                                _infoDescriptionTV.setTypeface(FONT_RUBIK_REGULAR);
                                _infoDescriptionTV2.setTypeface(FONT_RUBIK_REGULAR);
                                if(InfoDescription2!=null && !InfoDescription2.equals("")){
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
            } else if(_infoLayout.getVisibility() == View.VISIBLE) {
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
                                InfoPanelShow(InfoTitle, InfoDescription,InfoDescription2, Icon, infoType);
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
    public void UpdateInfoPanelDetails(String Title, String Description, String Description2){
        if(_InfoPanel_IsEditingEnabled) {
                _infoTitleTV.setText(Title);
            if(Description!=null)
                 _infoDescriptionTV.setText(Description);
            if(Description2!=null)
            _infoDescriptionTV2.setText(Description2);
        }
    }
    public void ClearInfoPanelDetails(){
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
        slide_down_bounce.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_up);
                _infoLayout.startAnimation(slide_up);
                if(!_IsOnSearchMode)
                    UpdateUI(Enums.UIType.HIDE_INFO);
                else{
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
    public static void buttonEffect(View button){
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
    private void InitializeFonts(){
        try{
            FONT_PLATE = Typeface.createFromAsset(_context.getAssets(),
                    _GlobalResource.getString(R.string.FONT_LICENSE_PLATE));
            FONT_STATION = Typeface.createFromAsset(_context.getAssets(),
                    _GlobalResource.getString(R.string.FONT_ROBOTO_MEDIUM));
            FONT_RUBIK_BOLD = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_Rubik_Bold));
            FONT_RUBIK_REGULAR = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_Rubik_Regular));
            FONT_RUBIK_MEDIUM = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_ROBOTO_MEDIUM));
            FONT_ROBOTO_CONDENDSED_BOLD = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_RobotoCondensed_Bold));
            FONT_RUBIK_BLACK = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_Rubik_Black));
        }catch (Exception ex){
          Helper.logger(ex);
        }
    }
    private void InitializeAnimations(){
        slide_down_bounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_bounce);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        slide_up_bounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up_bounce);
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

    }
    public void PlayButtonClickSound(){
        _buttonClick.start();
    }
    private void SearchBarClicked(){
        LoaderDialog pleasewait = new LoaderDialog(MenuActivity.this,"Please wait","..");
        pleasewait.show();
    }
        public void GetFacebookUsername(final String STR_FB_id, final UserMarker userMarkerCache){
            try{
                MenuActivity._usersDBRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        String STR_firstName= dataSnapshot.child(STR_FB_id).child("firstName").getValue() == null? null : dataSnapshot.child(STR_FB_id).child("firstName").getValue().toString();
                        String STR_lastName = dataSnapshot.child(STR_FB_id).child("lastName").getValue() == null? null: dataSnapshot.child(STR_FB_id).child("lastName").getValue().toString();
                        if(STR_firstName == null && STR_lastName == null)
                            userMarkerCache.UserTitle=null;
                        else
                            userMarkerCache.UserTitle=STR_firstName + " " + STR_lastName;
                        UpdateInfoPanelDetails(userMarkerCache.UserTitle,null,null);

                    }
                });

            }catch (Exception ex){
                Helper.logger(ex);
            }

        }

    public int GetUserNavigationMenuPicture(){
            int result = R.drawable.samm_user_icon_info_layout_sammer_registered;
            try{
                if(_sessionManager.isGuest())
                    result = R.drawable.samm_user_icon_info_layout_default;
                else if(_sessionManager.getIsAdmin())
                    result = R.drawable.samm_user_icon_info_layout_admin;
                else if(_sessionManager.getIsSuperAdmin())
                    result = R.drawable.samm_user_icon_info_layout_superadmin;

            }catch (Exception ex){
                Helper.logger(ex);
            }
            return result;
    }
    private Typeface GetFontStyle(Enums.InfoLayoutType layouttype){
        Typeface TF_result = FONT_STATION;
        try{
            switch (layouttype){
                case INFO_VEHICLE: TF_result = FONT_RUBIK_BOLD; break;
                case INFO_PERSON: TF_result = FONT_ROBOTO_CONDENDSED_BOLD; break;
                default: TF_result = FONT_ROBOTO_CONDENDSED_BOLD;
            }
        }catch (Exception ex){
            Helper.logger(ex);
        }
        return TF_result;
    }
    private void InitializeMapStyle(){
        _IB_MapType_Normal = (ImageButton) findViewById(id.IB_mapType_normal);
        _IB_MapType_Hybrid = (ImageButton) findViewById(id.IB_mapType_hybrid);
        _IB_MapType_Satellite = (ImageButton) findViewById(id.IB_mapType_satellite);
        _IB_MapType_Terrain = (ImageButton) findViewById(id.IB_mapType_terrain);
        _IB_MapType_Normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayButtonClickSound();
                SetMapType(_googleMap, Enums.GoogleMapType.MAP_TYPE_NORMAL);
                view.setBackgroundResource(R.drawable.selected_border);
            }
        });
        _IB_MapType_Terrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayButtonClickSound();
                SetMapType(_googleMap, Enums.GoogleMapType.MAP_TYPE_TERRAIN);
                view.setBackgroundResource(R.drawable.selected_border);
            }
        });
        _IB_MapType_Satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayButtonClickSound();
                SetMapType(_googleMap, Enums.GoogleMapType.MAP_TYPE_SATELLITE);
                view.setBackgroundResource(R.drawable.selected_border);
            }
        });
        _IB_MapType_Hybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayButtonClickSound();
                SetMapType(_googleMap, Enums.GoogleMapType.MAP_TYPE_HYBRID);
                view.setBackgroundResource(R.drawable.selected_border);
            }
        });
    }

    private void ToggleMapTools() {
        _LL_MapActions.setVisibility(_LL_MapActions.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
        _LL_MapStyleHolder.setVisibility(View.INVISIBLE);
    }
    public void InflateDriverRoutesPanel(){
        ViewStub _VS_RoutesPanel = (ViewStub) findViewById(id.ViewStub_RoutesPanel);
        _VS_RoutesPanel.inflate();
        ShimmerLayout SL_SelectYourRoute = (ShimmerLayout) findViewById(id.SL_SelectYourRoute);
        SL_SelectYourRoute.startShimmerAnimation();
        final LinearLayout LL_RoutesListDisplay = (LinearLayout) findViewById(id.LL_RoutesListDisplay);
        if(_routeList.size() >0) {
            for (final Routes routeEntry : _routeList) {
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(Helper.dpToPx(130,_context), Helper.dpToPx(130,_context));
                btnParams.setMargins(7,0,7,0);
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
                        for (Integer i = 0; LL_RoutesListDisplay.getChildCount()!=i;i++){
                            View v = LL_RoutesListDisplay.getChildAt(i);
                            v.setBackgroundResource(R.drawable.rounded_corner_route_panel);
                            ((Button)v).setTextColor(getApplication().getResources().getColor(R.color.colorBlack));
                        }
                        //then apply UI style to the selected
                        view.setBackgroundResource(R.drawable.selected_route);
                        ((Button)view).setTextColor(getApplication().getResources().getColor(R.color.colorWhite));
                        ((Button)view).setTypeface(FONT_RUBIK_BOLD);
                        _driversDBRef.child(_sessionManager.getKeyDeviceid()).child("routeIDs").setValue(String.valueOf(routeEntry.getID()));
                    }
                });
                LL_RoutesListDisplay.addView(BTN_routeButton);
            }
        }
    }
}









