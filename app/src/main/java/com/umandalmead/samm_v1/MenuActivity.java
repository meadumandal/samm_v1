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
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
import com.umandalmead.samm_v1.EntityObjects.Users;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.AddPassengerCountLabel;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.AddUserMarkers;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.AddVehicleMarkers;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.Vehicle_DestinationsListener;
import com.umandalmead.samm_v1.Modules.AdminUsers.AdminUsersFragment;
import com.umandalmead.samm_v1.Modules.DriverUsers.DriverUsersFragment;
import com.umandalmead.samm_v1.Modules.DriverUsers.mySQLGetDriverUsers;
import com.umandalmead.samm_v1.POJO.HTMLDirections.Directions;
import com.umandalmead.samm_v1.POJO.HTMLDirections.Setting;
import com.umandalmead.samm_v1.POJO.HTMLDirections.Settings;
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
import java.sql.Time;
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
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;
import static com.umandalmead.samm_v1.Constants.MY_PERMISSIONS_REQUEST_SEND_SMS;
import static com.umandalmead.samm_v1.Constants.MY_PERMISSION_REQUEST_LOCATION;
import android.graphics.PorterDuff;


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
        public DatabaseReference _vehicle_destinationsDBRef, _DriversDatabaseReference;

        //Put here all global Collection Variables
        public static List<Terminal> _possiblePickUpPointList;
        public static List<Terminal> _terminalList;
        public static HashMap<String, Marker> _terminalMarkerHashmap = new HashMap<>();
        public HashMap _userMarkerHashmap = new HashMap();
        public HashMap<String, Long> _passengerCount = new HashMap<>();
        public static List<Eloop> _eloopList;
        public static ArrayList<Routes> _routeList;
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
        public static ImageView _Slide_Collapse;
        public static ScrollView _StepsScroller;
        public static ClearableAutoCompleteTextView _TerminalsAutoCompleteTextView;
        public static MenuItem UserNameMenuItem;
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
        public static LinearLayout _MapsHolderLinearLayout;
        public FloatingActionButton _AddGPSFloatingButton, _AddPointFloatingButton, _ViewGPSFloatingButton, _AddRouteFloatingButton;
        public FloatingActionMenu _AdminToolsFloatingMenu;
        public Button _ReconnectGPSButton;
        public static ImageView FAB_SammIcon;
        public static FrameLayout FrameSearchBarHolder;
        public static ImageView Search_BackBtn;
        public  static TextView _DestinationTextView;
        public static CardView _RoutesContainer_CardView;
        public static ProgressBar _LoopArrivalProgress;
        public static String _FragmentTitle, _SelectedTerminalMarkerTitle, _ProcessedTerminalTitle;
        public static MediaPlayer _buttonClick;
        private TextView _infoTitleTV, _infoDescriptionTV;
        private LinearLayout _infoLayout;
        private ImageButton _infoPanelBtnClose;
        private ImageView _infoImage;
        private Animation  slide_down, slide_down_bounce, slide_up, slide_up_bounce;


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
        public Boolean _isUserLoggingOut = false;
        public String _facebookImg;
        public static Boolean _isVehicleMarkerRotating = false;
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
        public static Boolean _IsOnSearchMode = false, _BOOL_IsTerminalDataFetchDone = false, _BOOL_IsTerminalDataFetchOnGoing = false;
        public static Terminal[] _PointsArray;
        public static Typeface FONT_PLATE,FONT_STATION;
        public static int _currentRouteIDSelected;
        public static Boolean _HasExitedInfoLayout = false;
        public static CustomFrameLayout _mapRoot;
        public static Integer _DestinationTblRouteID, _RouteTabSelectedIndex=0;
        public AddGPSDialog _dialog;
        public LoaderDialog _LoaderDialog;



        public  boolean _IsAllLoopParked,_InfoPanel_IsEditingEnabled=true;
        private String _loopIds = "";
        private List<Integer> _ListOfLoops = new ArrayList<Integer>();
        private String _AssignedELoop = "";
        private int _passengerCountInTerminal =0;



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
            _constants = new Constants();
            if (_helper.isOnline()) {
                _isAppFirstLoad = true;

                displayLocationSettingsRequest(_context);
                //region EloopList
                new mySQLGetEloopList(_context).execute();
                //endregion
                new mySQLRoutesDataProvider(_context).execute();

                new mySQLGetDriverUsers(_context).execute();



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
                    _DriversDatabaseReference = _firebaseDB.getReference("drivers");
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
                _ProfilePictureImg = (CircleImageView) _NavHeaderView.findViewById(R.id.imgLogo);
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
                _NavView.getMenu().findItem(id.nav_adminusers).setVisible(_sessionManager.getIsAdmin());
                _NavView.getMenu().findItem(R.id.nav_routes).setVisible(_sessionManager.getIsAdmin());
                _NavView.getMenu().findItem(R.id.nav_vehicles).setVisible(_sessionManager.getIsAdmin());
                _NavView.getMenu().findItem(R.id.nav_login).setVisible(_sessionManager.isGuest());
                FAB_SammIcon = (ImageView) findViewById(R.id.SAMMLogoFAB);
                FrameSearchBarHolder = (FrameLayout) findViewById(R.id.FrameSearchBarHolder);
                Search_BackBtn = (ImageView) findViewById(id.Search_BackBtn);
                _DestinationTextView = (TextView) findViewById(id.DestinationTV);
                _RoutesContainer_CardView = (CardView) findViewById(id.Routes_CardView);
                _LoopArrivalProgress = (ProgressBar) findViewById(R.id.progressBarLoopArrival);
                InitializeInfoPanel();
                InitializeFonts();
                InitializeAnimations();
                MenuActivity.buttonEffect(Search_BackBtn);

                //disable adjusting app dimensions when soft keyboard is shown
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                _buttonClick = MediaPlayer.create(this, R.raw.button_click);

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
                        _dialog =new AddGPSDialog(MenuActivity.this);
                        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        _dialog.show();
                        PlayButtonClickSound();
                    }
                });
                FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.openDrawer(Gravity.LEFT);
                        PlayButtonClickSound();
                    }
                });
                Search_BackBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                     HideRouteTabsAndSlidingPanel();
                        PlayButtonClickSound();
                    }
                });

                _AddPointFloatingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddPointDialog dialog=new AddPointDialog(MenuActivity.this, "ADD");
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        PlayButtonClickSound();
                    }
                });
                _AddRouteFloatingButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Intent addRouteIntent = new Intent(MenuActivity.this, ManageRoutesActivity.class);
                        startActivity(addRouteIntent);
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                        finish();
                        PlayButtonClickSound();
                    }
                });


                _ViewGPSFloatingButton.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View view) {
                        PlayButtonClickSound();
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

                final EditText placeAutoCompleteFragmentInstance = (EditText) findViewById(id.place_autocomplete_search_input);

                placeAutoCompleteFragmentInstance.setTextColor(Color.parseColor( "#FFFFFF"));
                placeAutoCompleteFragmentInstance.setTextSize(14);


                AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(Place.TYPE_COUNTRY)
                        .setCountry("PH")
                        .build();
                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                autocompleteFragment.setFilter(autocompleteFilter);
                //set bounds to search within bounds only~
                //autocompleteFragment.setBoundsBias(new LatLngBounds(new LatLng(14.427248, 120.996781), new LatLng(14.413897, 121.077285)));
//                placeAutoCompleteFragmentInstance.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        view.setVisibility(View.INVISIBLE);
//
//                    }
//                });
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    //GO-2R
                    public void onPlaceSelected(Place place) {
                        try {
                            _DestinationTextView.setText(_constants.DESTINATION_PREFIX + place.getName().toString());
                            double prevDistance = 0.0;
                            int ctr = 0;
                            for (Terminal dest : _terminalList) {
                                double tempDistance;
                                LatLng destLatLng = new LatLng(dest.Lat, dest.Lng);
                                LatLng searchLatLng = place.getLatLng();
                                tempDistance = _helper.getDistanceFromLatLonInKm(destLatLng, searchLatLng);
                                if (ctr == 0) {
                                    prevDistance = tempDistance;
                                    _chosenDropOffPoint = dest;
                                } else {
                                    if (tempDistance <= prevDistance) {
                                        prevDistance = tempDistance;
                                        _chosenDropOffPoint = dest;
                                    }
                                }
                                ctr++;
                            }
                            //new DestinationsOnItemClick(getApplicationContext());
                            _helper.FindNearestPickUpPoints(_chosenDropOffPoint);

                        }
                        catch (Exception ex) {
                            Log.i(_constants.LOG_TAG, ex.toString());
                        }
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
                                PlayButtonClickSound();
                              HideRouteTabsAndSlidingPanel();
                            }
                        });
                FrameSearchBarHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlayButtonClickSound();
                        UpdateUI(Enums.UIType.SHOWING_INFO);
                        Toast.makeText(getApplicationContext(), "Non-Facebook username!", Toast.LENGTH_LONG).show();
                    }
                });

                _LoaderDialog= new LoaderDialog(this, "Adding Vehicle GPS","Initializing...");
                _LoaderDialog.setCancelable(false);

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
                _mapRoot = (CustomFrameLayout) findViewById(R.id.mapCFL);
            }
            else
            {
                Log.w(_constants.LOG_TAG, "Device is not online");
                _helper.showNoInternetPrompt(this);
            }
            UpdateUI(Enums.UIType.MAIN);

        }catch(Exception ex)
        {
            _helper.logger(ex);
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
                _helper.logger("Style parsing failed");
            }
        }
        _driversDBRef.addChildEventListener(new AddVehicleMarkers(getApplicationContext(), this));
        _googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        _googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(!_IsOnSearchMode && _infoLayout.getVisibility() != View.VISIBLE) {
                    FrameSearchBarHolder.setVisibility(View.VISIBLE);
                    //Log.i(_constants.LOG_TAG,"==camera idle=="+ _googleMap.getCameraPosition().target);
                }

            }
        });
        _googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                if (reason ==REASON_GESTURE) {
                    FrameSearchBarHolder.setVisibility(View.INVISIBLE);
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
        try
        {
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

                        //move map camera with 2s delay
                        final Handler HND_CameraZoomDelay = new Handler();
                        HND_CameraZoomDelay.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ZoomAndAnimateMapCamera(_userCurrentLoc, 10);
                            }
                        }, 800);

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
        _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LATLNG_var1,INT_zoomLevel));
        _googleMap.animateCamera(CameraUpdateFactory.zoomOut());
        _googleMap.animateCamera(CameraUpdateFactory.zoomTo(16), 3000, null);
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
                String markerTitle = marker.getTitle();

                if(_terminalMarkerHashmap.containsKey(markerTitle))
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
                        Terminal TM_ClickedTerminal = new Terminal();
                        for(Terminal terminal:_terminalList)
                        {
                            if (terminal.Value.toLowerCase().equals(marker.getTitle().toLowerCase()))
                            {
                                TM_ClickedTerminal = terminal;
                            }

                        }
                        _SelectedTerminalMarkerTitle = TM_ClickedTerminal.getValue();
                        _passengerCountInTerminal = (int) passengercount;
                        ShowInfoLayout(TM_ClickedTerminal.Description, "\nFetching Data.." , true);
                        //_InfoPanel_IsEditingEnabled = false;
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
//                        HND_Loc_DataFetchTooLong.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(!_BOOL_IsTerminalDataFetchDone && !_BOOL_IsTerminalDataFetchOnGoing)
//                                    UpdateInfoPanelDetails(F_TM_ClickedTerminal.Description, "Data fetch taking longer than usual...");
//                            }
//                        }, 10000);

                    }
                }
                //vehicle has been clicked instead
                else if(_driverMarkerHashmap.containsKey(markerTitle)){
                    ShowInfoLayout(Helper.GetEloopEntry(markerTitle), "\nDescription not yet available" , false);
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

            Helper.logger(ex);
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
                            if (AnalyzeForBestRoutes.IsSameRoute(TM_Entry, TM_CurrentDest) && (TM_Entry.OrderOfArrival == TM_CurrentDest.OrderOfArrival)) {
                                for (DataSnapshot v : DS_Vehicle_Destination.getChildren()) {
                                    String StationName = v.getKey().toString(), StationNameWithTblRouteId = TM_Entry.Value + "_" + TM_Entry.getTblRouteID();
                                    if (StationNameWithTblRouteId.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                        loopAwaiting = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? true : false;
                                        String loopId = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? AnalyzeForBestRoutes.CleanEloopName(v.child("Dwell").getValue().toString()) : AnalyzeForBestRoutes.CleanEloopName(v.child("LoopIds").getValue().toString());
                                        if (loopAwaiting && AnalyzeForBestRoutes.IsEloopWithinSameRouteID(DS_Drivers, TM_CurrentDest, loopId)) {
                                            loopAwaiting = true;
                                            _IsAllLoopParked = false;
                                            found = true;
                                            GetTimeRemainingFromGoogle(Integer.parseInt(loopId), TM_CurrentDest);
                                        } else continue;

                                    }

                                }
                                if (loopAwaiting)
                                    break;
                            } else if (AnalyzeForBestRoutes.IsSameRoute(TM_Entry, TM_CurrentDest) && (TM_Entry.OrderOfArrival == 1 || TM_CurrentDest.OrderOfArrival == 1)) {
                                for (Terminal dl2 : L_TM_DestList_Sorted) {
                                    for (DataSnapshot v : DS_Vehicle_Destination.getChildren()) {
                                        String StationName = v.getKey().toString(), StationNameWithTblRouteId = dl2.Value + "_" + TM_Entry.getTblRouteID();
                                        if (StationNameWithTblRouteId.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                            String loopId = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? AnalyzeForBestRoutes.CleanEloopName(v.child("Dwell").getValue().toString()) : AnalyzeForBestRoutes.CleanEloopName(v.child("LoopIds").getValue().toString());
                                            if ((!loopId.equals("") && !loopId.equals(",")) && !found) {
                                                List<String> temploopids = Arrays.asList(loopId.split(","));
                                                for (String tli : temploopids
                                                        ) {
                                                    if (!tli.equals(""))
                                                        _ListOfLoops.add(Integer.parseInt(tli));
                                                }
                                                Collections.sort(_ListOfLoops);
                                                if (_ListOfLoops.size() > 0 && AnalyzeForBestRoutes.IsEloopWithinSameRouteID(DS_Drivers, TM_CurrentDest, _ListOfLoops.get(0).toString())) {
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

                            } else if (AnalyzeForBestRoutes.IsSameRoute(TM_Entry, TM_CurrentDest) && (TM_Entry.OrderOfArrival < TM_CurrentDest.OrderOfArrival)) {
                                for (DataSnapshot v : DS_Vehicle_Destination.getChildren()) {
                                    String StationName = v.getKey().toString(), StationNameWithTblRouteId = TM_Entry.Value + "_" + TM_Entry.getTblRouteID();
                                    if (StationNameWithTblRouteId.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                        String loopId = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? AnalyzeForBestRoutes.CleanEloopName(v.child("Dwell").getValue().toString()) : AnalyzeForBestRoutes.CleanEloopName(v.child("LoopIds").getValue().toString());
                                        if ((!loopId.equals("") && !loopId.equals(",")) && !found) {
                                            List<String> temploopids = Arrays.asList(loopId.split(","));
                                            for (String tli : temploopids) {
                                                if (!tli.equals(""))
                                                    _ListOfLoops.add(Integer.parseInt(tli));
                                            }
                                            Collections.sort(_ListOfLoops);
                                            if (_ListOfLoops.size() > 0 && AnalyzeForBestRoutes.IsEloopWithinSameRouteID(DS_Drivers, TM_CurrentDest, _ListOfLoops.get(0).toString())) {
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
                            UpdateInfoPanelDetails(TM_CurrentDest.Description, "\n" + _helper.getEmojiByUnicode(0x1F6BB) + " : " + _passengerCountInTerminal + " passenger(s) waiting\n" + _helper.getEmojiByUnicode(0x1F68C) + " : No nearby E-loop found");
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
                                    UpdateInfoPanelDetails(TM_Destination.Description, "\n" + _helper.getEmojiByUnicode(0x1F6BB) + " : " + _passengerCountInTerminal + " passenger(s) waiting\n" + _helper.getEmojiByUnicode(0x1F68C) + " : " + TimeofArrival + " (" + Distance + " away)");
                                }
                                _BOOL_IsTerminalDataFetchDone = true;
                            }
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
                                PlayButtonClickSound();
                                try {
                                    FacebookSdk.sdkInitialize(MenuActivity.this);
                                    LoginManager.getInstance().logOut();
                                }
                                catch(Exception ex)
                                {
                                    _helper.logger(ex);

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
                                PlayButtonClickSound();
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
                        _helper.logger(ex);

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
            else if(id==R.id.menu_username) {
                if (!_sessionManager.isGuest()) {
                    _MapsHolderLinearLayout = (LinearLayout) findViewById(R.id.mapsLinearLayout);
                    _MapsHolderLinearLayout.setVisibility(View.GONE);
                    fragment.beginTransaction().replace(R.id.content_frame, new UserProfileActivity()).commit();
                } else {
                    InfoDialog GuestInfo = new InfoDialog(MenuActivity.this, getResources().getString(R.string.guest_nav_drawer_message));
                    GuestInfo.show();
                }

            }
            else if (id==R.id.nav_adminusers)
            {

                _MapsHolderLinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                _MapsHolderLinearLayout.setVisibility(View.GONE);
                appBarLayoutParam.height = _constants.APPBAR_MIN_HEIGHT;
                AdminUsersFragment adminUsersFragment = new AdminUsersFragment();
                fragment.beginTransaction().replace(R.id.content_frame, adminUsersFragment).commit();
            }
            else if (id==R.id.nav_drivers)
            {
                _MapsHolderLinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                _MapsHolderLinearLayout.setVisibility(View.GONE);
                appBarLayoutParam.height = _constants.APPBAR_MIN_HEIGHT;
                DriverUsersFragment driverUsersFragment = new DriverUsersFragment();
                fragment.beginTransaction().replace(R.id.content_frame, driverUsersFragment).commit();
            }
            else if(id==R.id.nav_routes)
            {
                Intent addRouteIntent = new Intent(MenuActivity.this, ManageRoutesActivity.class);
                startActivity(addRouteIntent);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                finish();
            }
            else if (id == R.id.nav_vehicles)
            {
                try {
                    _MapsHolderLinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                    _MapsHolderLinearLayout.setVisibility(View.GONE);
                    fragment.beginTransaction().replace(R.id.content_frame, new ViewGPSFragment()).commit();
                }
                catch(Exception ex)
                {
                    Helper.logger(ex);
                }
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            PlayButtonClickSound();

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
                                _helper.logger(e);

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
                            _helper.logger(e);

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
            _helper.logger(ex);

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
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MenuActivity.this);
                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                            alertDialogBuilder.setTitle("Success");
                            alertDialogBuilder.setMessage("Successfully added GPS! It might take up to 10minutes before the GPS appears on the map.");
                            alertDialogBuilder.show();

                            _ProgressDialog.dismiss();

                            //new asyncAddTraccarGPS(getApplicationContext(), _ProgressDialog, MenuActivity.this, _dialog).execute("SAMM_"+ _gpsIMEI.substring(_gpsIMEI.length()-5, _gpsIMEI.length()), _gpsIMEI, _GPSMobileNumber, _gpsNetwork, _gpsPlateNumber, _gpsTblRoutesID.toString(), _gpsTblUsersID.toString());
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
                _helper.logger(ex);

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

                    Log.i(_constants.LOG_TAG,"Sending " + this._smsMessageForGPS + "...");
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
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
            _helper.logger(ex);

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
                    Log.i(_constants.LOG_TAG,"Sending " + this._smsMessageForGPS + "...");
//                    Toast.makeText(getApplicationContext(), "sending " + this._smsMessageForGPS, Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
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
            _helper.logger(ex);
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
                            _helper.logger(ex);

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
                                Toast.makeText(getApplicationContext(), "Please supply all fields", Toast.LENGTH_LONG).show();
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
                _helper.logger(e);

            }

            _LoaderDialog.dismiss();
        }

        private void savePoint(String name, Double lat, Double lng, String preposition, Terminal terminalReference)
        {
            _LoaderDialog = new LoaderDialog(MenuActivity.this, "Adding Pickup/Dropoff Point","Please wait as we set up the points on the map");
            _LoaderDialog.show();
            new asyncAddPoints(getApplicationContext(), _LoaderDialog, MenuActivity.this, _googleMap, _googleAPI,"Add", 0).execute(name, lat.toString(), lng.toString(), preposition, String.valueOf(terminalReference.ID));
        }
        private void updatePoint(Integer ID, String name, Double lat, Double lng, String preposition, Terminal terminalReference)
        {
            _LoaderDialog = new LoaderDialog(MenuActivity.this, "Updating Pickup/Dropoff Point", "Please wait as we update the points on the map");
            _LoaderDialog.show();
            new asyncAddPoints(getApplicationContext(), _LoaderDialog, MenuActivity.this, _googleMap, _googleAPI, "Update", ID).execute(name, lat.toString(), lng.toString(), preposition, String.valueOf(terminalReference.ID));
        }
        private void deletePoint(Integer ID)
        {
            _LoaderDialog = new LoaderDialog(MenuActivity.this, "Deleting Pickup/Dropoff Point", "Please wait as we update the points on the map");
            _LoaderDialog.show();
            new asyncAddPoints(getApplicationContext(), _LoaderDialog, MenuActivity.this, _googleMap, _googleAPI, "Delete", ID).execute();
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

            driverAdapterList.add(new Users(0, "Select a driver for this PUV", "", "","","Driver", "", 1));
            routesAdapterList.add(new Routes(0, "Select a route for this PUV"));
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


            networkList.add("Select the network of the GPS of this PUV");
            networkList.add("Globe");
            networkList.add("Smart");


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
                        if(_GPSMobileNumber.trim().length() == 0 || _gpsIMEI.trim().length() == 0 || spinnerNetworks.getSelectedItem().toString().equals("Select GSM SIM Network Provicer"))
                        {
                            Toast.makeText(getContext(), "Please supply all fields", Toast.LENGTH_LONG).show();
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
                        _helper.logger(ex);
                        Toast.makeText(getContext(), "Error occured", Toast.LENGTH_LONG).show();
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
    public void UpdateUI(Enums.UIType type){
        if(_sessionManager.getMainTutorialStatus() != null) {
            switch (type) {
                case MAIN:
                    if (!_sessionManager.getMainTutorialStatus()) {
                        BuildToolTip("Tap to show menu options", this, FAB_SammIcon, Gravity.END, OverlayView.HIGHLIGHT_SHAPE_OVAL, false);
                        BuildToolTip("Search here", this, FrameSearchBarHolder, Gravity.BOTTOM, OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR, false);
                        _sessionManager.TutorialStatus(Enums.UIType.MAIN, true);
                    }
                    break;
                case SHOWING_ROUTES:
                    if (!_sessionManager.getRouteTutorialStatus()) {
                       // ShowRouteTabsAndSlidingPanel();
                        BuildToolTip("Tap to search again", this, Search_BackBtn, Gravity.END, OverlayView.HIGHLIGHT_SHAPE_OVAL, false);
                        BuildToolTip("Pull up to show navigation instructions", this, _RoutesContainer_CardView, Gravity.TOP, OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR, false);
                        _sessionManager.TutorialStatus(Enums.UIType.SHOWING_ROUTES, true);
                        _HasExitedInfoLayout = false;
                    }
                    break;
                case SHOWING_INFO:
                    FAB_SammIcon.setVisibility(View.INVISIBLE);
                    FrameSearchBarHolder.setVisibility(View.INVISIBLE);
                    break;
                case HIDE_SEARCH_FRAGMENT_ON_SEARCH:
                    FAB_SammIcon.setVisibility(View.INVISIBLE);
                    FrameSearchBarHolder.setVisibility(View.INVISIBLE);
                    break;
                case HIDE_INFO:
                    FAB_SammIcon.setVisibility(View.VISIBLE);
                    FrameSearchBarHolder.setVisibility(View.VISIBLE);
                    _infoLayout.setVisibility(View.INVISIBLE);
                    _infoPanelBtnClose.setVisibility(View.VISIBLE);
                    break;
                case HIDE_INFO_SEARCH:
                    Search_BackBtn.setVisibility(View.VISIBLE);
                    _infoLayout.setVisibility(View.INVISIBLE);
                    _infoPanelBtnClose.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }
    public void ShowRouteTabsAndSlidingPanel(){
        _RouteTabLayout.setVisibility(View.VISIBLE);
        _RoutesPane.setVisibility(View.VISIBLE);
       // _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        _TimeOfArrivalTextView.setVisibility(View.VISIBLE);
        _StepsScroller.scrollTo(0, 0);
        _AppBar.setVisibility(View.VISIBLE);
        FAB_SammIcon.setVisibility(View.INVISIBLE);
        Search_BackBtn.setVisibility(View.VISIBLE);
        FrameSearchBarHolder.setVisibility(View.INVISIBLE);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.routepager);
        viewPager.setCurrentItem(0);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)MenuActivity._AppBar.getLayoutParams();
        lp.height = 130;
        _AppBar.setLayoutParams(lp);
        //Get nearest loop time of arrival~
        _LoopArrivalProgress.setVisibility(View.VISIBLE);
        _IsOnSearchMode=true;
        if(_markerAnimator!=null)
            _markerAnimator.start();
        final Handler HND_ShowPanel = new Handler();
        HND_ShowPanel.postDelayed(new Runnable() {
            @Override
            public void run() {
                _SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        }, 1000);
    }
    public void HideRouteTabsAndSlidingPanel(){
        _RouteTabLayout.setVisibility(View.INVISIBLE);
        _IsOnSearchMode=false;
        _RoutesPane.setVisibility(View.INVISIBLE);
        AnalyzeForBestRoutes.clearLines();
        _selectedPickUpPoint = null;
        Search_BackBtn.setVisibility(View.INVISIBLE);
        FrameSearchBarHolder.setVisibility(View.VISIBLE);
        FAB_SammIcon.setVisibility(View.VISIBLE);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)MenuActivity._AppBar.getLayoutParams();
        lp.height = 0;
        _AppBar.setLayoutParams(lp);
        AnalyzeForBestRoutes.RemoveListenerFromLoop();
        _HasExitedInfoLayout = true;
        AnalyzeForBestRoutes.InitializeSearchingRouteUI(false, false, "" );
    }
    public void SetMapType(GoogleMap gmap, Enums.GoogleMapType mapType){
        switch(mapType){
            case MAP_TYPE_NORMAL: gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Integer mapsStyle = IsNight() ? R.raw.night_maps_style: R.raw.maps_style;
                boolean success = _googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, mapsStyle));

                if (!success) {
                    _helper.logger("Style parsing failed");
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

    public void ShowInfoLayout(String Title, String Description, Boolean IsStation){
        try{
        ClearInfoPanelDetails();
        InfoPanelShow(Title, Description, IsStation);
        }
        catch(Exception ex){
            Toast.makeText(MenuActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void InitializeInfoPanel(){
        try {
            _infoTitleTV = (TextView) findViewById(R.id.TextView_InfoTitle);
            _infoDescriptionTV = (TextView) findViewById(R.id.TextView_InfoDesc);
            _infoLayout = (LinearLayout) findViewById(R.id.Info_Layout);
            _infoPanelBtnClose = (ImageButton) findViewById(id.btnCloseInfoPanel);
            _infoTitleTV.setVisibility(View.INVISIBLE);
            _infoDescriptionTV.setVisibility(View.INVISIBLE);
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
    public void InfoPanelShow(final String InfoTitle, final String InfoDescription,final Boolean isStation) {
            final int imageResId = isStation ? R.drawable.ic_ecoloopstop_for_info : R.drawable.eco_loop_for_info_transparent;
            final String finalTitle =  InfoTitle.toUpperCase();
            final Typeface finalTitleFont = isStation ? FONT_STATION: FONT_PLATE;
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
                                UpdateInfoPanelDetails(finalTitle,InfoDescription);
                                _infoImage.setImageResource(imageResId);
                                _infoTitleTV.setTypeface(finalTitleFont);
                                _infoTitleTV.setVisibility(View.VISIBLE);
                                _infoDescriptionTV.setVisibility(View.VISIBLE);
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
                                InfoPanelShow(InfoTitle, InfoDescription, isStation);
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
    public void UpdateInfoPanelDetails(String Title, String Description){
        if(_InfoPanel_IsEditingEnabled) {
            _infoTitleTV.setText(Title);
            _infoDescriptionTV.setText(Description);
        }
    }
    public void ClearInfoPanelDetails(){
        _infoImage.setImageResource(0);
        _infoTitleTV.setText(null);
        _infoDescriptionTV.setText(null);
    }
    public void InfoPanelHide() {
        Animation slide_down_bounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_bounce);
        _infoLayout.startAnimation(slide_down_bounce);
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
                    "font/Trender.ttf");
            FONT_STATION = Typeface.createFromAsset(_context.getAssets(),
                    "font/Trender.ttf");
        }catch (Exception ex){
            Log.i(_constants.LOG_TAG, "Exception: " + ex.getMessage());
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


}









