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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.umandalmead.samm_v1.EntityObjects.Routes;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.AddPassengerCountLabel;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.AddUserMarkers;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.AddVehicleMarkers;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.Vehicle_DestinationsListener;
import com.umandalmead.samm_v1.POJO.Directions;
import com.umandalmead.samm_v1.POJO.Setting;
import com.umandalmead.samm_v1.POJO.Settings;
import com.umandalmead.samm_v1.R.id;
import com.umandalmead.samm_v1.RouteTabs.Route1;
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
import java.io.PrintWriter;
import java.io.StringWriter;
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

import io.github.douglasjunior.androidSimpleTooltip.OverlayView;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

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
        public FirebaseDatabase _firebaseDB;
        public DatabaseReference _usersDBRef;
        public static DatabaseReference _terminalsDBRef;
        public DatabaseReference _driversDBRef;
        public DatabaseReference _vehicle_destinationsDBRef;

        //Put here all global Collection Variables
        public static List<Terminal> _possiblePickUpPointList;
        public static List<Terminal> _terminalList;
        public static HashMap<String, Marker> _terminalMarkerHashmap = new HashMap<>();
        public HashMap _userMarkerHashmap = new HashMap();
        public HashMap<String, Long> _passengerCount = new HashMap<>();
        public static List<Eloop> _eloopList;
        public static ArrayList<Routes> _routeList;

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
        public static ImageView _Slide_Expand;
        public static ImageView _Slide_Collapse;
        public static ScrollView _StepsScroller;
        public static ClearableAutoCompleteTextView _TerminalsAutoCompleteTextView;
        public static MenuItem UserNameMenuItem;
        public static TextView _TimeOfArrivalTextView;
        public static MenuItem _UserNameMenuItem;
        public static NavigationView _NavView;
        public static Menu _MenuNav;
        public static ImageView _ProfilePictureImg;
        public static View _NavHeaderView;
        public static TextView _HeaderUserFullName;
        public static TextView _HeaderUserEmail;
        public static AppBarLayout _AppBar;
        public static Toolbar _Toolbar;
        public static LinearLayout _SearchLinearLayout;
        public  static EditText _CurrentLocationEditText;
        public static LinearLayout _MapsHolderLinearLayout;
        public FloatingActionButton _AddGPSFloatingButton, _AddPointFloatingButton, _ViewGPSFloatingButton, _AddRouteFloatingButton;
        public FloatingActionMenu _AdminToolsFloatingMenu;
        public Button _ReconnectGPSButton;
        public ProgressDialog _ProgressDialog;
        public static ImageView FAB_SammIcon;
        public static FrameLayout FrameSearchBarHolder;
        public static ImageView Search_BackBtn;
        public  static TextView _DestinationTextView;
        public static CardView _RoutesContainer_CardView;
        public static ProgressBar _LoopArrivalProgress;
        public static String _FragmentTitle;


        //Put here other global variables
        public GoogleApiClient _googleAPI;
        public Helper _helper;
        public Context _context;
        public LatLng _userCurrentLoc;
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
        public Boolean _isUserLoggingOut = false;
        public String _facebookImg;
        public static Boolean _isVehicleMarkerRotating = false;
        public String _smsMessageForGPS;
        private String _GPSMobileNumber;
        public String _smsAPN;
        public Boolean _isGPSReconnect = false;
        private String _gpsIMEI;
        public Constants _constants;
        public Boolean terminalsNodeExists = false;



        public  boolean _IsAllLoopParked;
        private String _loopIds = "";
        private List<Integer> _ListOfLoops = new ArrayList<Integer>();
        private String _AssignedELoop = "";
        private int _passengerCountInTerminal =0;


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
            _constants = new Constants();
            if (_helper.isOnline()) {
                _isAppFirstLoad = true;

                displayLocationSettingsRequest(_context);
                //region EloopList
                new mySQLGetEloopList(_context).execute();
                //endregion
                new mySQLRoutesDataProvider(_context).execute();


                if (_sessionManager == null)
                    _sessionManager = new SessionManager(_context);
                if (!_sessionManager.isLoggedIn()) {
                    String username = _constants.GUEST_USERNAME_PREFIX + UUID.randomUUID().toString();
                    _sessionManager.CreateLoginSession(_constants.GUEST_FIRSTNAME, _constants.GUEST_LASTNAME, username, "", false, true, "", false);
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
                                        if (setting.getName().toLowerCase().equals(_sessionManager.IS_BETA.toLowerCase()))
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
                            Helper.logger(ex);
                        }
                    }
                    @Override
                    public void onFailure(Throwable t) {
                        Log.d(_constants.LOG_TAG, t.toString());
                    }
                });
                //_Toolbar = (Toolbar) findViewById(R.id.toolbar);
                //setSupportActionBar(_Toolbar);
                //_Toolbar.setTitle(_constants.APP_TITLE);

                if (_firebaseDB == null || _usersDBRef == null || _vehicle_destinationsDBRef == null) {
                    _firebaseDB = FirebaseDatabase.getInstance();
                    _usersDBRef = _firebaseDB.getReference("users");
                    _terminalsDBRef = _firebaseDB.getReference("terminals");
                    _vehicle_destinationsDBRef = _firebaseDB.getReference("vehicle_destinations");
                }
                if (_driversDBRef == null)
                    _driversDBRef = _firebaseDB.getReference("drivers");

                //Instantiate UI objects~
                //_TerminalsAutoCompleteTextView = (ClearableAutoCompleteTextView) findViewById(id.txtDestinationIDForEdit);
                _AppBar = (AppBarLayout) findViewById(R.id.appBarLayout);
                _RoutesPane = (LinearLayout) findViewById(R.id.route_content);
                _SlideUpPanelContainer = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
                _RouteTabLayout = (TabLayout) findViewById(R.id.route_tablayout);
                _Slide_Collapse = (ImageView) findViewById(R.id.ev_panel_collapse);
                _StepsScroller = (ScrollView) findViewById(R.id.step_scroll_view);
                _TimeOfArrivalTextView = (TextView) findViewById(R.id.toatextview);
                _NavView = (NavigationView) findViewById(R.id.nav_view);
                _MenuNav = (Menu) _NavView.getMenu();
                UserNameMenuItem = _MenuNav.findItem(R.id.menu_username);
                _UserNameMenuItem = _MenuNav.findItem(R.id.menu_username);
                _NavHeaderView = _NavView.getHeaderView(0);
                _ProfilePictureImg = (ImageView) _NavHeaderView.findViewById(R.id.imgLogo);
                _HeaderUserFullName = (TextView) _NavHeaderView.findViewById(R.id.HeaderUserFullName);
                _HeaderUserEmail = (TextView) _NavHeaderView.findViewById(R.id.HeaderUserEmail);
                //_SearchLinearLayout = (LinearLayout) findViewById(R.id.searchlayoutcontainer);
                //_CurrentLocationEditText = (EditText) findViewById(R.id.tvcurrentlocation);
                _UserNameMenuItem.setTitle(_sessionManager.getFullName());
                _HeaderUserFullName.setText(_sessionManager.getFullName().toUpperCase());
                _HeaderUserEmail.setText(_sessionManager.getEmail());
                _AddGPSFloatingButton = (FloatingActionButton) findViewById(R.id.subFloatingAddGPS);
                _AddPointFloatingButton = (FloatingActionButton) findViewById(R.id.subFloatingAddPoint);
                _ViewGPSFloatingButton = (FloatingActionButton) findViewById(R.id.subFloatingViewGPS);
                _AdminToolsFloatingMenu = (FloatingActionMenu) findViewById(R.id.AdminFloatingActionMenu);
                _AddRouteFloatingButton = (FloatingActionButton) findViewById(id.subFloatingAddRoute);
                _NavView.getMenu().findItem(R.id.nav_logout).setVisible(!_sessionManager.isGuest());
                _NavView.getMenu().findItem(R.id.nav_passengerpeakandlean).setVisible(!_sessionManager.isGuest() && !_sessionManager.isDriver());
                _NavView.getMenu().findItem(R.id.nav_ecolooppeakandlean).setVisible(_sessionManager.getIsAdmin());
                _NavView.getMenu().findItem(R.id.nav_login).setVisible(_sessionManager.isGuest());
                FAB_SammIcon = (ImageView) findViewById(R.id.SAMMLogoFAB);
                FrameSearchBarHolder = (FrameLayout) findViewById(R.id.FrameSearchBarHolder);
                Search_BackBtn = (ImageView) findViewById(id.Search_BackBtn);
                _DestinationTextView = (TextView) findViewById(id.DestinationTV);
                _RoutesContainer_CardView = (CardView) findViewById(id.Routes_CardView);
                _LoopArrivalProgress = (ProgressBar) findViewById(R.id.progressBarLoopArrival);


                if(_sessionManager.getIsBeta() && !_sessionManager.getIsDeveloper() && !_sessionManager.getIsAdmin())
                {
                    //(findViewById(R.id.tvcurrentlocation)).setVisibility(View.GONE);
                    //(findViewById(R.id.searchlayoutcontainer)).setVisibility(View.GONE);
                }
                if (_sessionManager.getIsAdmin())
                    _AdminToolsFloatingMenu.setVisibility(View.VISIBLE);
                else
                    _AdminToolsFloatingMenu.setVisibility(View.GONE);

                _AddGPSFloatingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddGPSDialog dialog=new AddGPSDialog(MenuActivity.this);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                });
                FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.openDrawer(Gravity.LEFT);
                    }
                });
                Search_BackBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                     HideRouteTabsAndSlidingPanel();
                    }
                });

                _AddPointFloatingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddPointDialog dialog=new AddPointDialog(MenuActivity.this, "ADD");
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                });
                _AddRouteFloatingButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        _MapsHolderLinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                        _MapsHolderLinearLayout.setVisibility(View.GONE);

                        FragmentManager fragment = getSupportFragmentManager();
                        fragment.beginTransaction().replace(R.id.content_frame, new AddRouteFragment()).commit();
                    }
                });


                _ViewGPSFloatingButton.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View view) {
                        try {
                            _MapsHolderLinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                            _MapsHolderLinearLayout.setVisibility(View.GONE);
                            FragmentManager fragment = getSupportFragmentManager();
                            fragment.beginTransaction().replace(R.id.content_frame, new ViewGPSFragment()).commit();
                        }
                        catch(Exception ex)
                        {
                            Helper.logger(ex);
                        }

                    }
                });

                ((EditText)findViewById(R.id.place_autocomplete_search_input)).setTextColor(Color.parseColor( "#FFFFFF"));
                ((EditText)findViewById(R.id.place_autocomplete_search_input)).setTextSize(14);

                AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(Place.TYPE_COUNTRY)
                        .setCountry("PH")
                        .build();
                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                autocompleteFragment.setFilter(autocompleteFilter);
                //set bounds to search within bounds only~
                //autocompleteFragment.setBoundsBias(new LatLngBounds(new LatLng(14.427248, 120.996781), new LatLng(14.413897, 121.077285)));

                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    //GO-2R
                    public void onPlaceSelected(Place place) {
                        _DestinationTextView.setText(_constants.DESTINATION_PREFIX + place.getName().toString());
                        double prevDistance = 0.0;
                        int ctr=0;
                        for (Terminal dest: _terminalList){
                            double tempDistance;
                            LatLng destLatLng = new LatLng(dest.Lat, dest.Lng);
                            LatLng searchLatLng = place.getLatLng();
                            tempDistance = _helper.getDistanceFromLatLonInKm(destLatLng,searchLatLng);
                            if(ctr==0){
                                prevDistance = tempDistance;
                                _chosenDropOffPoint = dest;
                            }else{
                                if(tempDistance <= prevDistance){
                                    prevDistance = tempDistance;
                                    _chosenDropOffPoint = dest;
                                }
                            }
                            ctr++;
                        }
                        //new DestinationsOnItemClick(getApplicationContext());
                        _helper.FindNearestPickUpPoints(_chosenDropOffPoint);

                    }

                    @Override
                    public void onError(Status status) {
                        // TODO: Handle the error.
                        Log.i(_constants.LOG_TAG, "An error occurred: " + status);
                    }
                });
                autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                              HideRouteTabsAndSlidingPanel();
                            }
                        });
                FrameSearchBarHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Non-Facebook username!", Toast.LENGTH_LONG).show();
                    }
                });

                _ProgressDialog = new ProgressDialog(this);
                _ProgressDialog.setTitle("Adding Vehicle GPS");
                _ProgressDialog.setMessage("Initializing...");
                _ProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                _ProgressDialog.setCancelable(false);

                if (_sessionManager.isGuest() || _sessionManager.isDriver() || _sessionManager.getIsAdmin())
                {


                    //LinearLayout searchContainer = (LinearLayout) findViewById(R.id.searchlayoutcontainer);
                    //EditText tvcurrentlocation = (EditText) findViewById(R.id.tvcurrentlocation);

                    //searchContainer.setVisibility(View.GONE);
                    //tvcurrentlocation.setVisibility(View.GONE);
                    AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appBarLayout);

                    //CoordinatorLayout.LayoutParams appBarLayoutParam = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
                    //appBarLayoutParam.height = _constants.APPBAR_MIN_HEIGHT;
                }
                if (_sessionManager.isDriver())
                {
                    _vehicle_destinationsDBRef.addChildEventListener(new Vehicle_DestinationsListener(getApplicationContext(), _terminalsDBRef));
                    _RoutesPane.setVisibility(View.VISIBLE);
                    _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    _TimeOfArrivalTextView.setVisibility(View.VISIBLE);
                    _TimeOfArrivalTextView.setText("You are approaching FASTBYTES terminal, there are NO PASSENGER WAITING");
                }

               // _RouteTabLayout.setMinimumWidth(150);
                _facebookImg = "http://graph.facebook.com/" + _sessionManager.getUsername().trim() + "/picture?type=large";
                try {
                    FetchFBDPTask dptask = new FetchFBDPTask();
                    dptask.execute();
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Non-Facebook username!", Toast.LENGTH_LONG).show();
                }

                _SlideUpPanelContainer.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {

                    }

                    @Override
                    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                        if (previousState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                            _Slide_Expand.setVisibility(View.GONE);
                            _Slide_Collapse.setVisibility(View.VISIBLE);
                        }
                        if (previousState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                            _Slide_Expand.setVisibility(View.VISIBLE);
                            _Slide_Collapse.setVisibility(View.GONE);
                        }
                    }
                });

//                _TerminalsAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        _TerminalsAutoCompleteTextView.showDropDown();
//                        _TerminalsAutoCompleteTextView.setCursorVisible(true);
//                    }
//                });


//                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                        this, drawer, _Toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//                drawer.setDrawerListener(toggle);
//                toggle.syncState();

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

                String SMS_SENT = "SMS_SENT";
                String SMS_DELIVERED = "SMS_DELIVERED";

                _sentSMSPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
                _deliveredSMSPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

                //register BroadcastReceiver
                IntentFilter intentFilter = new IntentFilter(GeofenceTransitionsIntentService.ACTION_MyIntentService);
                intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
                registerReceiver(_userMovementBroadcastReceiver, intentFilter);
                registerReceiver(_smsSentBroadcastReceiver, new IntentFilter(SMS_SENT));
                registerReceiver(_smsDeliveredBroadcastReceiver, new IntentFilter(SMS_DELIVERED));
                initializeOnlinePresence();
                CustomFrameLayout mapRoot = (CustomFrameLayout) findViewById(R.id.mapCFL);
                mapRoot.setOnDragListener(new View.OnDragListener(){
                    @Override
                    public boolean onDrag(View view, DragEvent dragEvent) {
                        _googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        return true;
                    }
                });
            }
            else
            {
                Log.w(_constants.LOG_TAG, "Device is not online");
                _helper.showNoInternetPrompt(this);
            }
            UpdateUI(Enums.UIType.MAIN);

        }catch(Exception ex)
        {
            Log.e(_constants.LOG_TAG, ex.getMessage());
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(_constants.LOG_TAG,"Google Map is Ready...");
        _googleMap = googleMap;
        Enums.GoogleMapType mapType = Enums.GoogleMapType.MAP_TYPE_NORMAL;
        try{
           mapType = Enums.GoogleMapType.valueOf(_sessionManager.getMapStylePreference());
        }catch (Exception ex){
        }
        SetMapType(_googleMap, mapType);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                buildGoogleApiClient();
                _googleMap.setMyLocationEnabled(true);
                _googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }
        else {
            buildGoogleApiClient();
            _googleMap.setMyLocationEnabled(true);
        }
        if(mapType == Enums.GoogleMapType.MAP_TYPE_NORMAL){
            Integer mapsStyle = IsNight() ? R.raw.night_maps_style: R.raw.maps_style;
            boolean success = _googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, mapsStyle));

            if (!success) {
                Log.e(Constants.LOG_TAG, "Style parsing failed.");
            }
        }

        _driversDBRef.addChildEventListener(new AddVehicleMarkers(getApplicationContext(), this));
        _googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(_constants.LOG_TAG, "Building Google API Client...");
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
            Toast.makeText(_context, "Please install google play service", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
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
                markerOptions.position(_userCurrentLoc);

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                _userCurrentLocMarker = _googleMap.addMarker(markerOptions);

                if (_isAppFirstLoad) {
                    _isAppFirstLoad = false;
                    //move map camera
                    _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(_userCurrentLoc, 16));
                    _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(_userCurrentLoc, 16));
                }

                //stop location updates
                if (_googleAPI != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(_googleAPI, this);
                }
            }

        }

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
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, this);

        }
        _googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(_terminalMarkerHashmap.containsKey(marker.getTitle()))
                {
                    if (_sessionManager.getIsDeveloper() && !_sessionManager.isGuest() && !_sessionManager.isDriver())
                    {
                        //if admin only:
                        AddPointDialog dialog=new AddPointDialog(MenuActivity.this, "Update", marker.getTitle());
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                    else
                    {
                        long passengercount = 0;
                        if(_passengerCount.containsKey(marker.getTitle())) {
                            passengercount = _passengerCount.get(marker.getTitle());
                        }
                        Terminal clickedTerminal = new Terminal();
                        for(Terminal terminal:_terminalList)
                        {
                            if (terminal.Value.toLowerCase().equals(marker.getTitle().toLowerCase()))
                            {
                                clickedTerminal = terminal;
                            }

                        }
                        _passengerCountInTerminal = (int) passengercount;
                        marker.setSnippet("Fetching Data...");
                        marker.showInfoWindow();
                        GetAndDisplayEloopETA(clickedTerminal, marker);
                    }
                }


               //marker.setSnippet(_helper.getEmojiByUnicode(0x1F6BB) +" : " + String.valueOf(passengercount) + " | " + _helper.getEmojiByUnicode(0x1F68C) + " : 2 mins");

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
    public void GetAndDisplayEloopETA(final Terminal currentDest, final Marker marker) {
        try {
            String res = "";
            if (currentDest != null) {
                final List<Terminal> DestList = MenuActivity._terminalList;
                Collections.sort(DestList, Terminal.DestinationComparators.ORDER_OF_ARRIVAL);
                _vehicle_destinationsDBRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {

                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildren() != null) {
                            Boolean found = false, loopAwaiting = false;
                            int ctr = 0;
                            _IsAllLoopParked = true;
                            for (Terminal dl : DestList) {
                                if (dl.OrderOfArrival == currentDest.OrderOfArrival) {
                                    for (DataSnapshot v : dataSnapshot.getChildren()) {
                                        String StationName = v.getKey().toString();
                                        if (dl.Value.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                            loopAwaiting = !v.child("Dwell").getValue().toString().equals("") ? true : false;
                                            if (loopAwaiting) {
                                                marker.setSnippet(_helper.getEmojiByUnicode(0x1F6BB) +" : " + _passengerCountInTerminal + "    " + _helper.getEmojiByUnicode(0x1F68C) + " : waiting");
                                                marker.hideInfoWindow();
                                                marker.showInfoWindow();
                                                loopAwaiting = true;
                                                break;
                                            } else continue;

                                        }

                                    }
                                    if (loopAwaiting)
                                        break;
                                } else if (dl.OrderOfArrival == 1 || currentDest.OrderOfArrival == 1) {
                                    for (Terminal dl2 : DestList) {
                                        for (DataSnapshot v : dataSnapshot.getChildren()) {
                                            String StationName = v.getKey().toString();
                                            if (dl2.Value.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {

                                                if (!_loopIds.equals("") && !found) {
                                                    _IsAllLoopParked = false;
                                                    found = true;
                                                    List<String> temploopids = Arrays.asList(_loopIds.split(","));
                                                    for (String tli : temploopids
                                                            ) {
                                                        _ListOfLoops.add(Integer.parseInt(tli));
                                                    }
                                                    Collections.sort(_ListOfLoops);
                                                    if (_ListOfLoops.size() > 0) {
                                                        //VehicleDestinationDatabaseReference.removeEventListener(LoopArrivalEventListener);
                                                        GetTimeRemainingFromGoogle(_ListOfLoops.get(0), currentDest, marker);
                                                        Toast.makeText(_context, "if (order of arrival =0) hit!", Toast.LENGTH_LONG).show();
                                                        // LoopArrivalProgress.setVisibility(View.INVISIBLE);
                                                    }
                                                    _ListOfLoops.clear();
                                                    break;
                                                } else continue;
                                            } else continue;

                                        }
                                    }
                                    if (found)
                                        break;

                                } else if (dl.OrderOfArrival < currentDest.OrderOfArrival) {
                                    for (DataSnapshot v : dataSnapshot.getChildren()) {
                                        String StationName = v.getKey().toString();
                                        if (dl.Value.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                            _loopIds = v.child("LoopIds").getValue().toString();
                                            if(_loopIds.equals("")){
                                                _loopIds = v.child("Dwell").getValue().toString();
                                            }
                                            if (!_loopIds.equals("") && !found) {
                                                _IsAllLoopParked = false;
                                                found = true;
                                                List<String> temploopids = Arrays.asList(_loopIds.split(","));
                                                for (String tli : temploopids) {
                                                    _ListOfLoops.add(Integer.parseInt(tli));
                                                }
                                                Collections.sort(_ListOfLoops);
                                                if (_ListOfLoops.size() > 0) {
                                                    // VehicleDestinationDatabaseReference.removeEventListener(LoopArrivalEventListener);
                                                    GetTimeRemainingFromGoogle(_ListOfLoops.get(0), currentDest, marker);
                                                    Toast.makeText(_context, "else if hit!", Toast.LENGTH_LONG).show();
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
                            marker.setSnippet(_helper.getEmojiByUnicode(0x1F6BB) +" : " + _passengerCountInTerminal + "    " + _helper.getEmojiByUnicode(0x1F68C) + " : N/A");
                            marker.hideInfoWindow();
                            marker.showInfoWindow();

                        }
                    }
                });


            }
        } catch (Exception ex) {

            Helper.logger(ex);
        }
    }

    public void GetTimeRemainingFromGoogle(Integer LoopId, final Terminal dest, final Marker marker) {
        if (LoopId != null) {

            _vehicle_destinationsDBRef = _firebaseDB.getReference("drivers").child(LoopId.toString()); //database.getReference("users/"+ _sessionManager.getUsername() + "/connections");
            _vehicle_destinationsDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<Integer, Integer> destinationId_distance = new HashMap<>();
                    String url = "https://maps.googleapis.com/maps/";
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitMaps service = retrofit.create(RetrofitMaps.class);
                    _AssignedELoop = dataSnapshot.child("deviceid").getValue().toString();
                    Call<Directions> call = service.getDistanceDuration("metric", dest.Lat + "," + dest.Lng, dataSnapshot.child("Lat").getValue() + "," + dataSnapshot.child("Lng").getValue(), "driving");
                    call.enqueue(new Callback<Directions>() {
                        @Override
                        public void onResponse(Response<Directions> response, Retrofit retrofit) {
                            try {
                                for (int i = 0; i < response.body().getRoutes().size(); i++) {
                                    String TimeofArrival = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();
                                    marker.setSnippet(_helper.getEmojiByUnicode(0x1F6BB) +" : " + _passengerCountInTerminal + "    " + _helper.getEmojiByUnicode(0x1F68C) + " : " + TimeofArrival.toString());
                                    marker.setSnippet(TimeofArrival.toString());
                                    marker.hideInfoWindow();
                                    marker.showInfoWindow();

                                }

                                _LoopArrivalProgress.setVisibility(View.GONE);
                            } catch (Exception ex) {
                                Log.d("onResponse", "There is an error");
                                Helper.logger(ex);
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
                    Log.e(_constants.LOG_TAG, "Sending of SMS failed");
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
            CoordinatorLayout.LayoutParams appBarLayoutParam = (CoordinatorLayout.LayoutParams) _AppBar.getLayoutParams();

            if (id == R.id.nav_logout) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }

                builder.setTitle("Log out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    LoginManager.getInstance().logOut();
                                }
                                catch(Exception ex)
                                {
                                    Log.e(_constants.LOG_TAG, ex.getMessage());
                                }

                                _sessionManager.logoutUser();
                                String username = _constants.GUEST_USERNAME_PREFIX + UUID.randomUUID().toString();
                                _sessionManager.CreateLoginSession(_constants.GUEST_FIRSTNAME, _constants.GUEST_LASTNAME, username, "", false, true, "", false);
                                finish();
                                startActivity(getIntent());
                                Toast.makeText(MenuActivity.this,"You've been logged out.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
            else if(id==R.id.nav_about){
                _MapsHolderLinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                _MapsHolderLinearLayout.setVisibility(View.GONE);

                fragment.beginTransaction().replace(R.id.content_frame, new AboutActivity()).commit();

            }
            else if (id==R.id.nav_login)
            {
                if(_sessionManager.getIsBeta() && !_sessionManager.getIsDeveloper())
                {
                    Toast.makeText(_context, "Login not available in Beta version", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        LoginManager.getInstance().logOut();
                    }
                    catch(Exception ex)
                    {
                        Log.e(_constants.LOG_TAG, ex.getMessage());
                    }

                    Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
            else if (id == R.id.nav_passengerpeakandlean)
            {
                _sessionManager.PassReportType(_constants.PASSENGER_REPORT_TYPE);
                _MapsHolderLinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                _MapsHolderLinearLayout.setVisibility(View.GONE);
                appBarLayoutParam.height = _constants.APPBAR_MIN_HEIGHT;
                //_SearchLinearLayout.setVisibility(View.GONE);
                //_TerminalsAutoCompleteTextView.setVisibility(View.GONE);
                //_CurrentLocationEditText.setVisibility(View.GONE);

                fragment.beginTransaction().replace(R.id.content_frame, new ReportsActivity()).commit();
            }
            else if (id == R.id.nav_ecolooppeakandlean)
            {
                _sessionManager.PassReportType(_constants.VEHICLE_REPORT_TYPE);
                _MapsHolderLinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                _MapsHolderLinearLayout.setVisibility(View.GONE);
                appBarLayoutParam.height = _constants.APPBAR_MIN_HEIGHT;
                //_SearchLinearLayout.setVisibility(View.GONE);
                //_TerminalsAutoCompleteTextView.setVisibility(View.GONE);
                //_CurrentLocationEditText.setVisibility(View.GONE);
                fragment.beginTransaction().replace(R.id.content_frame, new ReportsActivity()).commit();
            }
            else if(id == R.id.menu_home){
                _MapsHolderLinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                _MapsHolderLinearLayout.setVisibility(View.VISIBLE);

                if(!_sessionManager.isGuest() && !_sessionManager.getIsAdmin())
                {
                    appBarLayoutParam.height = _constants.APPBAR_MAX_HEIGHT;
                    //_SearchLinearLayout.setVisibility(View.VISIBLE);
                    //_TerminalsAutoCompleteTextView.setVisibility(View.VISIBLE);
                    //_CurrentLocationEditText.setVisibility(View.VISIBLE);
                }
            }
            else if(id==R.id.nav_map_normal){
                SetMapType(_googleMap, Enums.GoogleMapType.MAP_TYPE_NORMAL);
            }
            else if(id==R.id.nav_map_hybrid){
                SetMapType(_googleMap, Enums.GoogleMapType.MAP_TYPE_HYBRID);
            }
            else if(id==R.id.nav_map_satellite){
                SetMapType(_googleMap, Enums.GoogleMapType.MAP_TYPE_SATELLITE);
            }
            else if(id==R.id.nav_map_terrain){
                SetMapType(_googleMap, Enums.GoogleMapType.MAP_TYPE_TERRAIN);
            }
            else if(id==R.id.menu_username)
            {
//                Intent SignUpForm = new Intent(MenuActivity.this, UserProfileActivity.class);
//                startActivity(SignUpForm);
//                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                drawer.closeDrawer(GravityCompat.START);


                _MapsHolderLinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                _MapsHolderLinearLayout.setVisibility(View.GONE);

                //_SearchLinearLayout.setVisibility(View.GONE);
                //_TerminalsAutoCompleteTextView.setVisibility(View.GONE);
                //_CurrentLocationEditText.setVisibility(View.GONE);
                fragment.beginTransaction().replace(R.id.content_frame, new UserProfileActivity()).commit();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        catch(Exception ex)
        {
            Helper.logger(ex);

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

        _terminalsDBRef.child(destinationValue).addListenerForSingleValueEvent(new ValueEventListener() {
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
                _passengerCount.remove(destinationValue);
                _passengerCount.put(destinationValue, dataSnapshot.getChildrenCount());

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
                                Log.e(LOG_TAG, e.getMessage());
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


    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
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
                Call<Directions> call = service.getDistanceDuration("metric", _selectedPickUpPoint.Lat + "," + _selectedPickUpPoint.Lng, Looploc.latitude + "," + Looploc.longitude, "driving");
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
                            Log.e(LOG_TAG, e.getMessage());
                            e.printStackTrace();
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
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            Log.e(LOG_TAG, "StackTrace: " + sw.toString() + " | Message: " + ex.getMessage());
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
                            _ProgressDialog.setMessage("Activating GPRS");
                            sendSMSMessage(_constants.SMS_GPRS, _GPSMobileNumber);
                        }
                        else if (_smsMessageForGPS.equals(_constants.SMS_GPRS)) {
                            if(_isGPSReconnect) {
                                _ReconnectGPSButton.setText("Reconnect");
                                _ReconnectGPSButton.setEnabled(true);
                            }
                            else {
                                _ProgressDialog.setMessage("Setting APN");
                                sendSMSMessage(_smsAPN, _GPSMobileNumber);
                            }
                        }
                        else if (_smsMessageForGPS.equals(_smsAPN)) {
                            if(_isGPSReconnect) {
                                sendSMSMessage(_constants.SMS_GPRS, _GPSMobileNumber);
                            }
                            else {
                                _ProgressDialog.setMessage("Configuring IP and Port");
                                sendSMSMessage(_constants.SMS_ADMINIP, _GPSMobileNumber);
                            }

                        }
                        else if (_smsMessageForGPS.equals(_constants.SMS_ADMINIP)) {
                            _ProgressDialog.setMessage("Setting automatic location updates");
                            sendSMSMessage(_constants.SMS_TIMEINTERVAL, _GPSMobileNumber);
                        }
                        else if (_smsMessageForGPS.equals(_constants.SMS_TIMEINTERVAL)) {
                            _ProgressDialog.setMessage("Successfully configured GPS. Now adding GPS to server...");
                            new asyncAddTraccarGPS(getApplicationContext(), _ProgressDialog, MenuActivity.this).execute("SAMM_"+ _gpsIMEI.substring(_gpsIMEI.length()-5, _gpsIMEI.length()), _gpsIMEI, _GPSMobileNumber);
                        }
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        if(_isGPSReconnect)
                        {
                            _ReconnectGPSButton.setEnabled(true);
                            _ReconnectGPSButton.setText("Reconnect");
                        }
                        else
                        {
                            _ProgressDialog.dismiss();
                        }
                        Toast.makeText(context, "Error encountered in adding GPS: Please check your signal", Toast.LENGTH_SHORT).show();

                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        if(_isGPSReconnect)
                        {
                            _ReconnectGPSButton.setEnabled(true);
                            _ReconnectGPSButton.setText("Reconnect");
                        }
                        else
                        {
                            _ProgressDialog.dismiss();
                        }
                        Toast.makeText(context, "Error encountered in adding GPS: Please check your signal", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        if(_isGPSReconnect)
                        {
                            _ReconnectGPSButton.setEnabled(true);
                            _ReconnectGPSButton.setText("Reconnect");
                        }
                        else
                        {
                            _ProgressDialog.dismiss();
                        }
                        Toast.makeText(context, "Error encountered in adding GPS: Please check your signal", Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        if(_isGPSReconnect)
                        {
                            _ReconnectGPSButton.setEnabled(true);
                            _ReconnectGPSButton.setText("Reconnect");
                        }
                        else
                        {
                            _ProgressDialog.dismiss();
                        }
                        Toast.makeText(context, "Error encountered in adding GPS: Please check your signal", Toast.LENGTH_SHORT).show();
                        break;
                }


            }
            catch(Exception ex)
            {
                Log.e(_constants.LOG_TAG, ex.getMessage());
            }

        }
    }
    public class SMSDeliveredBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getApplicationContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getApplicationContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
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
            Log.i(LOG_TAG, "Sending SMS Messages to add new GPS...");
            this._smsMessageForGPS = message;
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)) {

                } else {

                    Log.i(_constants.LOG_TAG,"Sending " + this._smsMessageForGPS + "...");
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            }
            else
            {
                Log.i(_constants.LOG_TAG,"Sending " + this._smsMessageForGPS + "...");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, this._smsMessageForGPS, _sentSMSPendingIntent, _deliveredSMSPendingIntent);
                Log.i(_constants.LOG_TAG, message + " sent");
            }
        }
        catch(Exception ex)
        {
            Log.e(_constants.LOG_TAG, ex.getMessage());
            _ProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Error encountered" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void sendSMSMessage(String message, String phone, Button btnReconnectGPS) {
        try
        {
            Log.i(LOG_TAG, "Sending SMS Messages to reconnect GPS...");
            this._smsMessageForGPS = message;
            this._GPSMobileNumber = phone;

            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)) {

                } else {

                    Log.i(_constants.LOG_TAG,"Sending " + this._smsMessageForGPS + "...");
//                    Toast.makeText(getApplicationContext(), "sending " + this._smsMessageForGPS, Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            }
            else
            {
                Log.i(_constants.LOG_TAG,"Sending " + this._smsMessageForGPS + "...");
//                Toast.makeText(getApplicationContext(), "sending " + this._smsMessageForGPS, Toast.LENGTH_LONG).show();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, this._smsMessageForGPS, _sentSMSPendingIntent, _deliveredSMSPendingIntent);
                this._ReconnectGPSButton = btnReconnectGPS;

                Log.i(_constants.LOG_TAG, message + " sent");
//                Toast.makeText(getApplicationContext(),this._smsMessageForGPS + " sent", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception ex)
        {
            Log.e(_constants.LOG_TAG, ex.getMessage());
            _ProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Error encountered" + ex.getMessage(), Toast.LENGTH_LONG).show();
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

        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            try {
                requestWindowFeature(Window.FEATURE_NO_TITLE);

                setContentView(R.layout.dialog_add_point);
                btnAddPoints = (Button) findViewById(R.id.btnAddPoint);
                btnDeletePoints = (Button) findViewById(R.id.btnDeletePoint);
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
                                deletePoint(Integer.parseInt(txtDestinationIDForEdit.getText().toString()));
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
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
                            Log.e(TAG,ex.getMessage());
                        }

                    }
                });
                btnAddPoints.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(_action.equals("add")) {
                            String name = editName.getText().toString();
                            String lat = editLat.getText().toString();
                            String lng = editLng.getText().toString();
                            String preposition = spinnerPrePosition.getSelectedItem().toString();
                            Terminal terminalReference = (Terminal) spinnerTerminalReference.getSelectedItem();
                            if (name.trim().length() == 0 || lat.trim().length() == 0 || lng.trim().length() == 0 || preposition.trim().length() == 0 || terminalReference == null) {
                                Toast.makeText(getApplicationContext(), "Please supply all fields", Toast.LENGTH_LONG).show();
                            } else {

                                savePoint(name, Double.parseDouble(lat), Double.parseDouble(lng), preposition, terminalReference);
                                AddPointDialog.this.dismiss();
                            }
                        }
                        else if(_action.equals("update"))
                        {
                            String name = editName.getText().toString();
                            String lat = editLat.getText().toString();
                            String lng = editLng.getText().toString();
                            String preposition = spinnerPrePosition.getSelectedItem().toString();
                            Integer destinationID = Integer.parseInt(txtDestinationIDForEdit.getText().toString());
                            Terminal terminalReference = (Terminal) spinnerTerminalReference.getSelectedItem();
                            if (name.trim().length() == 0 || lat.trim().length() == 0 || lng.trim().length() == 0 || preposition.trim().length() == 0 || terminalReference == null) {
                                Toast.makeText(getApplicationContext(), "Please supply all fields", Toast.LENGTH_LONG).show();
                            } else {

                                updatePoint(destinationID, name, Double.parseDouble(lat), Double.parseDouble(lng), preposition, terminalReference);
                                AddPointDialog.this.dismiss();
                            }
                        }

                    }
                });
            }catch(Exception e)
            {
                Log.e(TAG, e.getMessage());
            }

            _ProgressDialog.dismiss();
        }

        private void savePoint(String name, Double lat, Double lng, String preposition, Terminal terminalReference)
        {
            _ProgressDialog = new ProgressDialog(MenuActivity.this);
            _ProgressDialog.setTitle("Adding Pickup/Dropoff Point");
            _ProgressDialog.setMessage("Please wait as we set up the points on the map");
            _ProgressDialog.show();
            new asyncAddPoints(getApplicationContext(), _ProgressDialog, MenuActivity.this, _googleMap, _googleAPI,"Add", 0).execute(name, lat.toString(), lng.toString(), preposition, String.valueOf(terminalReference.ID));
        }
        private void updatePoint(Integer ID, String name, Double lat, Double lng, String preposition, Terminal terminalReference)
        {
            _ProgressDialog = new ProgressDialog(MenuActivity.this);
            _ProgressDialog.setTitle("Updating Pickup/Dropoff Point");
            _ProgressDialog.setMessage("Please wait as we update the points on the map");
            _ProgressDialog.show();
            new asyncAddPoints(getApplicationContext(), _ProgressDialog, MenuActivity.this, _googleMap, _googleAPI, "Update", ID).execute(name, lat.toString(), lng.toString(), preposition, String.valueOf(terminalReference.ID));
        }
        private void deletePoint(Integer ID)
        {
            _ProgressDialog = new ProgressDialog(MenuActivity.this);
            _ProgressDialog.setTitle("Deleting Pickup/Dropoff Point");
            _ProgressDialog.setMessage("Please wait as we update the points on the map");
            _ProgressDialog.show();
            new asyncAddPoints(getApplicationContext(), _ProgressDialog, MenuActivity.this, _googleMap, _googleAPI, "Delete", ID).execute();
        }
    }

    public class AddGPSDialog extends Dialog implements
            android.view.View.OnClickListener {
        Button sendBtn;
        EditText txtphoneNo;
        EditText txtIMEI;
        Spinner networkProvider;


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

            networkProvider = (Spinner)findViewById(R.id.spinnerNetworkProviders);

            ArrayList<String> networkProviders = new ArrayList<>();
            networkProviders.add("Select GSM SIM Network Provider");
            networkProviders.add("Globe");
            networkProviders.add("Smart");


            ArrayAdapter<String> networkProvidersAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, networkProviders){
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
            networkProvider.setAdapter(networkProvidersAdapter);

            sendBtn = (Button) findViewById(R.id.btnAddGPS);
            txtphoneNo = (EditText) findViewById(R.id.GPSMobileNum);
            txtIMEI  = (EditText) findViewById(R.id.GPSIMEI);



            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _GPSMobileNumber = txtphoneNo.getText().toString().trim();
                    _gpsIMEI = txtIMEI.getText().toString().trim();
                    if(_GPSMobileNumber.trim().length() == 0 || _gpsIMEI.trim().length() == 0 || networkProvider.getSelectedItem().toString().equals("Select GSM SIM Network Provicer"))
                    {
                        Toast.makeText(getContext(), "Please supply all fields", Toast.LENGTH_LONG).show();
                    }
                    else {
                        smsCommandsStatus.put(_constants.SMS_BEGIN, false);
                        smsCommandsStatus.put(_constants.SMS_GPRS, false);
                        if (networkProvider.getSelectedItem().toString().equals("Globe"))
                            _smsAPN = _constants.SMS_APN_GLOBE;
                        else
                            _smsAPN = _constants.SMS_APN_SMART;
                        smsCommandsStatus.put(_smsAPN, false);
                        smsCommandsStatus.put(_constants.SMS_ADMINIP, false);
                        smsCommandsStatus.put(_constants.SMS_TIMEINTERVAL, false);

                        //Configure thru SMS
                        _ProgressDialog.show();

                        sendSMSMessage(_constants.SMS_BEGIN, _GPSMobileNumber);
                    }
                    dismiss();

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
    public void UpdateUI(Enums.UIType type){
       switch(type){
           case MAIN:
               if(!_sessionManager.getMainTutorialStatus()){
                   BuildToolTip("Tap to show menu options",this, FAB_SammIcon, Gravity.END, OverlayView.HIGHLIGHT_SHAPE_OVAL, false );
                   BuildToolTip("Search here",this, FrameSearchBarHolder, Gravity.BOTTOM ,OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR, false  );
                   _sessionManager.TutorialStatus(Enums.UIType.MAIN, true);
               }
               break;
           case SHOWING_ROUTES:
               if(!_sessionManager.getRouteTutorialStatus()){
                   ShowRouteTabsAndSlidingPanel();
                   BuildToolTip("Tap to search again",this, Search_BackBtn, Gravity.END, OverlayView.HIGHLIGHT_SHAPE_OVAL, false );
                   BuildToolTip("Pull up to show navigation instructions",this, _RoutesContainer_CardView, Gravity.TOP ,OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR ,false );
                   _sessionManager.TutorialStatus(Enums.UIType.SHOWING_ROUTES, true);
               }
               break;
           default: break;
       }
    }
    public void ShowRouteTabsAndSlidingPanel(){
        _RouteTabLayout.setVisibility(View.VISIBLE);
        _RoutesPane.setVisibility(View.VISIBLE);
        _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        _TimeOfArrivalTextView.setVisibility(View.VISIBLE);
        _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        _StepsScroller.scrollTo(0, 0);
        AnalyzeForBestRoutes.clearLines();
        _TimeOfArrivalTextView.setVisibility(View.VISIBLE);
        _AppBar.setVisibility(View.VISIBLE);
        FAB_SammIcon.setVisibility(View.GONE);
        Search_BackBtn.setVisibility(View.VISIBLE);
        FrameSearchBarHolder.setVisibility(View.GONE);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.routepager);
        viewPager.setCurrentItem(0);
        _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        _StepsScroller.scrollTo(0, 0);
        AnalyzeForBestRoutes.clearLines();
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)MenuActivity._AppBar.getLayoutParams();
        lp.height = 130;
        _AppBar.setLayoutParams(lp);
        //Get nearest loop time of arrival~
        _LoopArrivalProgress.setVisibility(View.VISIBLE);
        if(_markerAnimator!=null)
            _markerAnimator.start();
    }
    public void HideRouteTabsAndSlidingPanel(){
        _RouteTabLayout.setVisibility(View.GONE);
        _RoutesPane.setVisibility(View.GONE);
        AnalyzeForBestRoutes.clearLines();
        _selectedPickUpPoint = null;
        Search_BackBtn.setVisibility(View.GONE);
        FrameSearchBarHolder.setVisibility(View.VISIBLE);
        FAB_SammIcon.setVisibility(View.VISIBLE);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)MenuActivity._AppBar.getLayoutParams();
        lp.height = 0;
        _AppBar.setLayoutParams(lp);
    }
    public void SetMapType(GoogleMap gmap, Enums.GoogleMapType mapType){
        switch(mapType){
            case MAP_TYPE_NORMAL: gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Integer mapsStyle = IsNight() ? R.raw.night_maps_style: R.raw.maps_style;
                boolean success = _googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, mapsStyle));

                if (!success) {
                    Log.e(Constants.LOG_TAG, "Style parsing failed.");
                }
                _NavView.getMenu().findItem(id.nav_map_normal).setIcon(R.drawable.ic_check_black_24dp);
                break;
            case MAP_TYPE_HYBRID: gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                _NavView.getMenu().findItem(id.nav_map_hybrid).setIcon(R.drawable.ic_check_black_24dp);
                break;
            case MAP_TYPE_TERRAIN:   gmap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                _NavView.getMenu().findItem(id.nav_map_terrain).setIcon(R.drawable.ic_check_black_24dp);
                break;
            case MAP_TYPE_SATELLITE:gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                _NavView.getMenu().findItem(id.nav_map_satellite).setIcon(R.drawable.ic_check_black_24dp);
                break;
            default:gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);  _NavView.getMenu().findItem(id.nav_map_normal).setIcon(R.drawable.ic_check_black_24dp);

        }
        UnselectMapTypes(mapType);
        _sessionManager.SetMapStylePreference(mapType);

    }
    public void UnselectMapTypes(Enums.GoogleMapType SelectedMapType) {
        List<MenuItem> MenuItems = new ArrayList<MenuItem>();
        MenuItems.add(_NavView.getMenu().findItem(id.nav_map_hybrid));
        MenuItems.add(_NavView.getMenu().findItem(id.nav_map_terrain));
        MenuItems.add(_NavView.getMenu().findItem(id.nav_map_satellite));
        MenuItems.add(_NavView.getMenu().findItem(id.nav_map_normal));
        for (MenuItem item : MenuItems) {
            if (!String.valueOf(SelectedMapType).contains(item.getTitle().toString().toUpperCase())) {
                item.setIcon(null);
            }
        }
    }

    //Public methods for showing Dialogs from fragments which are previously attached to FABs onclick events.
    public void AddNewStationPoint(String DialogTitle){
            AddPointDialog dialog = new AddPointDialog(MenuActivity.this, DialogTitle);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
    }
    public void ModifyStationPoint(String DialogTitle, String DestinationToBeEdited){
            AddPointDialog dialog=new AddPointDialog(MenuActivity.this, "Update", DestinationToBeEdited);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

    }
    public void RefreshStationPoints(){
        FragmentManager fragment = getSupportFragmentManager();
        fragment.beginTransaction().replace(R.id.content_frame, new AddPointsFragment()).commit();
    }

}









