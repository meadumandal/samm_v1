package com.umandalmead.samm_v1;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
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
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.umandalmead.samm_v1.EntityObjects.Destination;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.AddUserMarkersListener;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.EventListeners.DestinationsOnItemClick;
import com.umandalmead.samm_v1.POJO.Directions;
import com.umandalmead.samm_v1.RouteTabs.Route1;
import com.umandalmead.samm_v1.RouteTabs.Route2;
import com.umandalmead.samm_v1.RouteTabs.Route3;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;


import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.umandalmead.samm_v1.R.id.map;

public class MenuActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        Route1.OnFragmentInteractionListener,
        Route2.OnFragmentInteractionListener,
        Route3.OnFragmentInteractionListener,
        Html.ImageGetter,
        LocationListener {
            GoogleApiClient _googleApiClient;
            Marker _currentLocationMarker;
            LocationRequest _locationRequest;
            public GoogleMap _map;
            FirebaseDatabase _firebaseDatabase;
            public DatabaseReference _userDatabaseReference;
            DatabaseReference _destinationDatabaseReference;
            public SessionManager _sessionManager;
            boolean _isFirstLoad;
            public LatLng _currentLocation;
            Destination _bestTerminal;
            public static List<Destination> _candidateTerminals;
            ArrayList<LatLng> _markerPoints;
            Polyline _polyLine;
            Helper _helper;
            DestinationsOnItemClick _DestinationsHelper;
            Context _context;
            List<Geofence> _geoFenceList;
            PendingIntent _geoFencePendingIntent;
            private Circle _geofenceCircleLimits;
            private Marker _geofenceMarker;
            public static List<Destination> _listDestinations;
            public HashMap<String, Marker> _destinationMarkers = new HashMap<>();

            public HashMap _hashmapMarkerMap = new HashMap();
            public HashMap _driverMarkers = new HashMap();
            MyBroadcastReceiver _broadcastReceiver;
            DatabaseReference _driverDatabaseReference;
            public static LevelListDrawable d = new LevelListDrawable();
            public  static Destination _chosenTerminal;
            private Boolean IsOnline = false;
             Marker _marker;
            public Boolean IsLoggingOut = false;
            public String fbImg;
            Marker _markerAnimate;
            private Boolean isMarkerRotating = false;
            public static final String DRIVERPREFIX="SAMM_";
            protected static final String TAG = "mead";
            public static float RADIUS = 50;
            protected static final int REQUEST_CHECK_SETTINGS = 0x1;
            public static final int MY_PERMISSION_REQUEST_LOCATION=99;
            public Destination _chosenDestination;

            //Declared as public so that they can be accessed on other context.

            public static LinearLayout RoutePane;
            public static SlidingUpPanelLayout SlideUpPanelContainer;
            public static TabLayout RouteTabLayout;
            public static WebView RouteStepsText;
            public static ImageView Slide_Expand;
            public static ImageView Slide_Collapse;
            public static ScrollView StepsScroller;
            public static ClearableAutoCompleteTextView editDestinations;
            public  static ProgressDialog CheckNetDialog;
            public  static View MainView;
            public static TextView TimeOfArrivalTextView;
            public static MenuItem UserNameMenuItem;
            public static NavigationView NavView;
            public static Menu menuNav;
            public static ImageView ProfilePictureImg;
            public static View NavHeaderView;
            public static TextView HeaderUserFullName;
            public static TextView HeaderUserEmail;
            public static AppBarLayout AppBar;
            public static Toolbar toolbar;
    public static LinearLayout SearchLinearLayout;
    public  static EditText CurrentLocation;
    public static LinearLayout MapsHolder_LinearLayout;
    public static LinearLayout AddGPSHolder_LinearLayout;
    public static Fragment GoogleMapSearchBar;


            public boolean checkLocationPermission()
            {
                Log.i(TAG, "Checking location permission (checkLocationPermission)");
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

                setTheme(R.style.SplashTheme);
                if (MenuActivity.isOnline()) {
                    Log.i(TAG, "onCreate");
                    _markerPoints = new ArrayList<>();
                    _helper = new Helper();
                    _DestinationsHelper = new DestinationsOnItemClick(MenuActivity.this,getApplicationContext());
                    _context = getApplicationContext();
                    _geoFenceList = new ArrayList<>();


                    displayLocationSettingsRequest(_context);
                    super.onCreate(savedInstanceState);
                    _isFirstLoad = true;
                    if (_sessionManager == null)
                        _sessionManager = new SessionManager(_context);
                    if (!_sessionManager.isLoggedIn()) {
                        _sessionManager.logoutUser();
                        Intent i = new Intent(MenuActivity.this, LoginActivity.class);
                        finish();
                        startActivity(i);
                    }
                    setContentView(R.layout.activity_menu);
                    toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    toolbar.setTitle("SAMM");

                    if (_firebaseDatabase == null && _userDatabaseReference == null) {
                        _firebaseDatabase = FirebaseDatabase.getInstance();
                        _userDatabaseReference = _firebaseDatabase.getReference("users");
                        _destinationDatabaseReference = _firebaseDatabase.getReference("destinations");
                    }
                    if (_driverDatabaseReference == null)
                        _driverDatabaseReference = _firebaseDatabase.getReference("drivers");

                    //Instantiate ~
                    editDestinations = (ClearableAutoCompleteTextView) findViewById(R.id.edit_destinations);
                    AppBar = (AppBarLayout) findViewById(R.id.appBarLayout);
                    RoutePane = (LinearLayout) findViewById(R.id.route_content);
                    SlideUpPanelContainer = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
                    RouteTabLayout = (TabLayout) findViewById(R.id.route_tablayout);
                    Slide_Collapse = (ImageView) findViewById(R.id.ev_panel_collapse);
                    Slide_Expand = (ImageView) findViewById(R.id.ev_panel_expand);
                    StepsScroller = (ScrollView) findViewById(R.id.step_scroll_view);
                    TimeOfArrivalTextView = (TextView) findViewById(R.id.toatextview);
                    NavView = (NavigationView) findViewById(R.id.nav_view);
                    menuNav = (Menu) NavView.getMenu();
                    UserNameMenuItem = menuNav.findItem(R.id.menu_username);
                    NavHeaderView = NavView.getHeaderView(0);
                    ProfilePictureImg = (ImageView) NavHeaderView.findViewById(R.id.imgLogo);
                    HeaderUserFullName = (TextView) NavHeaderView.findViewById(R.id.HeaderUserFullName);
                    HeaderUserEmail = (TextView) NavHeaderView.findViewById(R.id.HeaderUserEmail);
                    SearchLinearLayout = (LinearLayout) findViewById(R.id.searchlayoutcontainer);
                    CurrentLocation = (EditText) findViewById(R.id.tvcurrentlocation);

                    UserNameMenuItem.setTitle(_sessionManager.getFullName());
                    HeaderUserFullName.setText(_sessionManager.getFullName().toUpperCase());
                    HeaderUserEmail.setText(_sessionManager.getEmail());

                    ((EditText)findViewById(R.id.place_autocomplete_search_input)).setTextColor(Color.parseColor("#FFFFFF"));
                    ((EditText)findViewById(R.id.place_autocomplete_search_input)).setTextSize(14);
                    PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                            getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

                    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                        @Override
                        public void onPlaceSelected(Place place) {
                            double prevDistance = 0.0;
                            int ctr=0;
                            for (Destination dest: _listDestinations){
                                double tempDistance;
                                LatLng destLatLng = new LatLng(dest.Lat, dest.Lng);
                                LatLng searchLatLng = place.getLatLng();
                                tempDistance = _helper.getDistanceFromLatLonInKm(destLatLng,searchLatLng);
                                if(ctr==0){
                                    prevDistance = tempDistance;
                                    _chosenDestination = dest;
                                }else{
                                    if(tempDistance <= prevDistance){
                                        prevDistance = tempDistance;
                                        _chosenDestination = dest;
                                    }
                                }
                                ctr++;
                            }
                            //new DestinationsOnItemClick(getApplicationContext());
                           _DestinationsHelper.FindNearestStations(_chosenDestination);
                        }

                        @Override
                        public void onError(Status status) {
                            // TODO: Handle the error.
                            Log.i(TAG, "An error occurred: " + status);
                        }
                    });
                    autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    RouteTabLayout.setVisibility(View.GONE);
                                    RoutePane.setVisibility(View.GONE);
                                    AnalyzeForBestRoutes.clearLines();
                                    _chosenTerminal = null;
                                    CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)MenuActivity.AppBar.getLayoutParams();
                                    lp.height = 156;
                                    view.setVisibility(View.GONE);
                                }
                            });
                    if (_sessionManager.isDriver())
                    {
                        //Prepare UI for driver
                        LinearLayout searchContainer = (LinearLayout) findViewById(R.id.searchlayoutcontainer);
                        EditText tvcurrentlocation = (EditText) findViewById(R.id.tvcurrentlocation);

                        searchContainer.setVisibility(View.GONE);
                        tvcurrentlocation.setVisibility(View.GONE);
                        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appBarLayout);

                        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appbar.getLayoutParams();
                        lp.height = 150;

                        //show route tabs and slide up panel ~
//                        RouteTabLayout.setVisibility(View.VISIBLE);
                        RoutePane.setVisibility(View.VISIBLE);

                        SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//                        SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                        TimeOfArrivalTextView.setVisibility(View.VISIBLE);
                        TextView sammDriver = (TextView) findViewById(R.id.sammdriver);
                        sammDriver.setVisibility(View.VISIBLE);
                        TimeOfArrivalTextView.setText("You are approaching FASTBYTES terminal, there are NO PASSENGER WAITING");


                    }

                    RouteTabLayout.setMinimumWidth(150);
                    fbImg = "http://graph.facebook.com/"+_sessionManager.getUsername()+"/picture?type=large";
                    try{
                        FetchFBDPTask dptask = new FetchFBDPTask ();
                        dptask.execute();
                    }catch(Exception ex){
                        Toast.makeText(getApplicationContext(), "Non-Facebook username!", Toast.LENGTH_LONG).show();
                    }

                    SlideUpPanelContainer.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                        @Override
                        public void onPanelSlide(View panel, float slideOffset) {

                        }

                        @Override
                        public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                            if (previousState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                                Slide_Expand.setVisibility(View.GONE);
                                Slide_Collapse.setVisibility(View.VISIBLE);
                            }
                            if (previousState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                                Slide_Expand.setVisibility(View.VISIBLE);
                                Slide_Collapse.setVisibility(View.GONE);
                            }
                        }
                    });

                    editDestinations.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDestinations.showDropDown();
                          editDestinations.setCursorVisible(true);
                        }
                    });


                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    drawer.setDrawerListener(toggle);
                    toggle.syncState();

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.setNavigationItemSelectedListener(this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkLocationPermission();
                    }

                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(map);
                    mapFragment.getMapAsync(this);


                    _userDatabaseReference.addChildEventListener(new AddUserMarkersListener(getApplicationContext(), this));

                    _driverDatabaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            try {
                                final String deviceId = dataSnapshot.getKey();


//                                Marker marker;
//                                marker = (Marker) _driverMarkers.get(deviceId);
//                                if (marker != null) {
//                                    marker.remove();
//                                    _driverMarkers.remove(deviceId);
//                                }
                                Object Latitude = dataSnapshot.child("Lat").getValue();
                                Object Longitude = dataSnapshot.child("Lng").getValue();
                                double lat, lng;
                                if (Latitude == null || Latitude.toString().equals("0"))
                                    lat = 0.0;
                                else
                                    lat = Double.parseDouble(Latitude.toString());
                                if (Longitude == null || Longitude.toString().equals("0"))
                                    lng = 0.0;
                                else
                                    lng = Double.parseDouble(Longitude.toString());
                                final LatLng latLng = new LatLng(lat, lng);
                                if (deviceId.toString().equals(_sessionManager.getUsername().replace(DRIVERPREFIX, "")))
                                {

                                    //move map camera
                                    _map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                                    _map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                                }

                                final Location prevLocation = new Location("");
                                final Location currLocation = new Location("");
                                prevLocation.setLatitude(Double.parseDouble(dataSnapshot.child("PrevLat").getValue().toString()));
                                prevLocation.setLongitude(Double.parseDouble(dataSnapshot.child("PrevLng").getValue().toString()));
                                currLocation.setLatitude(lat);
                                currLocation.setLongitude(lng);
                                final float bearing =  (float)bearingBetweenLocations(prevLocation,currLocation);//prevLocation.bearingTo(currLocation);

                                if (_map != null) {
                                    if (_chosenTerminal == null) {
                                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                                        valueAnimator.setDuration(2000);
                                        valueAnimator.setInterpolator(new LinearInterpolator());
                                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                                                _markerAnimate = (Marker) _driverMarkers.get(deviceId);
                                                final MarkerOptions markerOptions = new MarkerOptions();
                                                markerOptions.position(latLng);
//                                                markerOptions.title(deviceId);
                                                if (deviceId.toString().equals(_sessionManager.getUsername().replace(DRIVERPREFIX, "")))
                                                {
                                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ecoloopdriver));


                                                }
                                                else {
                                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ecoloop));
                                                }

                                                float  v = valueAnimator.getAnimatedFraction();
                                                double lng = v * currLocation.getLongitude() + (1 - v)
                                                        * prevLocation.getLongitude();
                                                double lat = v * currLocation.getLatitude() + (1 - v)
                                                        * prevLocation.getLatitude();
                                                LatLng newPos = new LatLng(lat, lng);
                                                if(_markerAnimate==null) {
                                                    _markerAnimate = _map.addMarker(markerOptions);
                                                }
                                                if(bearing!=0.0){
                                                    _markerAnimate.setPosition(newPos);
                                                    _markerAnimate.setAnchor(0.5f, 0.5f);
                                                    _markerAnimate.setRotation(bearing);
                                                    rotateMarker(_markerAnimate, bearing);
                                                }
                                                if (deviceId.toString().equals(_sessionManager.getUsername().replace(DRIVERPREFIX, "")))
                                                {
                                                    _markerAnimate.setTitle("HEY!");
                                                    _markerAnimate.setSnippet("It's you");
                                                    _markerAnimate.showInfoWindow();
                                                }
                                                _driverMarkers.put(deviceId, _markerAnimate);

                                            }
                                            });
                                             valueAnimator.start();

                                    } else {
                                        //ShowLoopTimeofArrival(latLng, bearing, deviceId);
                                    }
                                }
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            String test = databaseError.getMessage();

                        }
                    });

                    _destinationDatabaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            try {
                                Marker terminalEntered = _destinationMarkers.get(dataSnapshot.getKey());
                                terminalEntered.showInfoWindow();
                                terminalEntered.setSnippet(String.valueOf(dataSnapshot.getChildrenCount()) + " passenger/s waiting");
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            try {
                                Marker terminalEntered = _destinationMarkers.get(dataSnapshot.getKey());
                                terminalEntered.showInfoWindow();
                                terminalEntered.setSnippet(String.valueOf(dataSnapshot.getChildrenCount()) + " passenger/s waiting");
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            try {
                                Marker terminalEntered = _destinationMarkers.get(dataSnapshot.getKey());
                                terminalEntered.showInfoWindow();
                                terminalEntered.setSnippet(String.valueOf(dataSnapshot.getChildrenCount()) + " passenger/s waiting");
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    _broadcastReceiver = new MyBroadcastReceiver();

                    //register BroadcastReceiver
                    IntentFilter intentFilter = new IntentFilter(GeofenceTransitionsIntentService.ACTION_MyIntentService);
                    intentFilter.addCategory(Intent.CATEGORY_DEFAULT);


                    registerReceiver(_broadcastReceiver, intentFilter);
                    initialiseOnlinePresence();


                }
            }

    private double bearingBetweenLocations(Location PrevLoc,Location CurrLoc) {

        double PI = 3.14159;
        double lat1 = PrevLoc.getLatitude() * PI / 180;
        double long1 = PrevLoc.getLongitude() * PI / 180;
        double lat2 = CurrLoc.getLatitude() * PI / 180;
        double long2 = CurrLoc.getLongitude() * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }
    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.i(TAG,"onMapReady");
                _map = googleMap;
                _map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                //Initialize Google Play Services
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        buildGoogleApiClient();
                        _map.setMyLocationEnabled(true);

//                        createGeoFence(14.42576,121.03898);
                    }
                }
                else {
                    buildGoogleApiClient();

                    _map.setMyLocationEnabled(true);
                }
            }




            protected synchronized void buildGoogleApiClient() {
                if(_helper.isGooglePlayInstalled(_context)) {
                    _googleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                    _googleApiClient.connect();
                }
                else
                {
                    Toast.makeText(_context, "Please install google play service", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLocationChanged(Location location) {

                if(!IsLoggingOut) {
                    if(!_sessionManager.isDriver())
                    {
                        if (_currentLocationMarker != null) {
                            _currentLocationMarker.remove();
                        }
                        //Place current location marker
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        _currentLocation = new LatLng(lat, lng);

                        saveLocation(lat, lng);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(_currentLocation);
//                markerOptions.title(_sessionManager.getUsername());

                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                        _currentLocationMarker = _map.addMarker(markerOptions);

                        if (_isFirstLoad) {
                            _isFirstLoad = false;
                            //move map camera
                            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(_currentLocation, 16));
                            _map.animateCamera(CameraUpdateFactory.newLatLngZoom(_currentLocation, 16));
                        }

                        //stop location updates
                        if (_googleApiClient != null) {
                            LocationServices.FusedLocationApi.removeLocationUpdates(_googleApiClient, this);
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


                _userDatabaseReference.child(_sessionManager.getUsername()).child("currentDestination").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null)
                        {
                            _userDatabaseReference.child(_sessionManager.getUsername()).child("currentDestination").setValue(currentDestination.get("currentDestination"));
                        }
                        else
                        {
                            _userDatabaseReference.child(_sessionManager.getUsername()).updateChildren(currentDestination);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            private void saveLocation(double lat, double lng)
            {
                final HashMap<String, Object> latitude = new HashMap<>();
                final HashMap<String, Object> longitude = new HashMap<>();
                final HashMap<String, Object> hashLastUpdated= new HashMap<>();
                hashLastUpdated.put("lastUpdated", new Date().toString());
                latitude.put("Latitude", lat);
                longitude.put("Longitude", lng);
                _userDatabaseReference.child(_sessionManager.getUsername()).child("Longitude")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()==null)
                                {
                                    _userDatabaseReference.child(_sessionManager.getUsername()).child("Longitude").setValue(longitude);
                                }
                                else
                                {
                                    _userDatabaseReference.child(_sessionManager.getUsername()).updateChildren(longitude);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                _userDatabaseReference.child(_sessionManager.getUsername()).child("Latitude")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()==null)
                                {
                                    _userDatabaseReference.child(_sessionManager.getUsername()).child("Latitude").setValue(latitude);
                                }
                                else
                                {
                                    _userDatabaseReference.child(_sessionManager.getUsername()).updateChildren(latitude);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                _userDatabaseReference.child(_sessionManager.getUsername()).child("lastUpdated")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()==null)
                                {
                                    _userDatabaseReference.child(_sessionManager.getUsername()).child("lastUpdated").setValue(hashLastUpdated);
                                }
                                else
                                {
                                    _userDatabaseReference.child(_sessionManager.getUsername()).updateChildren(hashLastUpdated);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                new mySQLDestinationProvider(_context, MenuActivity.this, "", _map, _googleApiClient).execute();
                _locationRequest = new LocationRequest();
                _locationRequest.setInterval(0);
                _locationRequest.setFastestInterval(0);
                _locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(_googleApiClient, _locationRequest, this);
                    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, this);

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
                        // If request is cancelled, the result arrays are empty.
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                            // Permission was granted.
                            if (ContextCompat.checkSelfPermission(this,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {

                                if (_googleApiClient == null) {
                                    buildGoogleApiClient();

                                }
                                _map.setMyLocationEnabled(true);
                            }

                        } else {

                            // Permission denied, Disable the functionality that depends on this permission.
                            Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                        }
                        return;
                    }

                    // other 'case' lines to check for other permissions this app might request.
                    //You can add here other case statements according to your requirement.
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

                    if (id == R.id.nav_share) {
                        startActivity(new Intent(MenuActivity.this, MapsActivity.class));
                    } else if (id == R.id.nav_logout) {
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
                                        LoginManager.getInstance().logOut();
                                        _sessionManager.logoutUser();
                                        IsLoggingOut = true;
                                        Intent i = new Intent(MenuActivity.this, LoginActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
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
                        MapsHolder_LinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                        MapsHolder_LinearLayout.setVisibility(View.GONE);

                        fragment.beginTransaction().replace(R.id.content_frame, new AboutActivity()).commit();
//                        startActivity(new Intent(MenuActivity.this, AboutActivity.class));

                    }
                    else if (id == R.id.nav_passengerpeakandlean)
                    {
                        _sessionManager.PassReportType("passenger");
//                    fragment.beginTransaction().replace(R.id.content_frame, new ReportsActivity()).commit();

                        Intent i = new Intent(MenuActivity.this, ReportsActivity.class);
                        startActivity(i);
                    }
                    else if (id == R.id.nav_ecolooppeakandlean)
                    {
                        _sessionManager.PassReportType("ecoloop");
                        Intent i = new Intent(MenuActivity.this, ReportsActivity.class);
                        startActivity(i);
//                    fragment.beginTransaction().replace(R.id.content_frame, new ReportsActivity()).commit();
                    } else if (id == R.id.nav_addGPS)
                    {
                        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) AppBar.getLayoutParams();
                        lp.height = 156;
                        MapsHolder_LinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                        MapsHolder_LinearLayout.setVisibility(View.GONE);
                        fragment.beginTransaction().replace(R.id.content_frame, new AddGPSFragment()).commit();
                        editDestinations.setVisibility(View.GONE);
                        CurrentLocation.setVisibility(View.GONE);
                    }
                    else if(id == R.id.menu_home){
                        AddGPSHolder_LinearLayout = (LinearLayout) findViewById(R.id.addGPSLinearLayout);
                        AddGPSHolder_LinearLayout.setVisibility(View.GONE);
                        MapsHolder_LinearLayout = (LinearLayout)findViewById(R.id.mapsLinearLayout);
                        MapsHolder_LinearLayout.setVisibility(View.VISIBLE);
                        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) AppBar.getLayoutParams();
                        lp.height = 235;
                        editDestinations.setVisibility(View.VISIBLE);
                        CurrentLocation.setVisibility(View.VISIBLE);

                    }


                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
                catch(Exception ex)
                {
                    Toast.makeText(MenuActivity.this,ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                return true;
            }

            private void displayLocationSettingsRequest(Context context) {


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
                                Log.i(TAG, "All location settings are satisfied.");
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the result
                                    // in onActivityResult().
                                    status.startResolutionForResult(MenuActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                                break;
                        }
                    }
                });
            }
            public List<Geofence> createGeoFence()
            {
                String geofenceRequestId = "";
                List<Geofence> geofenceList = new ArrayList<>();


                        geofenceRequestId = UUID.randomUUID().toString();
                        geofenceList.add(new Geofence.Builder()
                                .setRequestId(geofenceRequestId)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                                .setCircularRegion(14.42576,121.03898, 50)
                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .build());


                return geofenceList;
            }
            private GeofencingRequest createGeofenceRequest(List<Geofence> geofence)
            {
                GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
                builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
                builder.addGeofences(geofence);
                return builder.build();
            }
            private PendingIntent createGeofencePendingIntent()
            {
                Log.i(TAG, "createGeofencePendingIntent()");
                Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
//                startService(intent);
                return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            }

            private void drawGeofence(LatLng latLng) {
                Log.d(TAG, "drawGeofence()");

                if ( _geofenceCircleLimits != null )
                    _geofenceCircleLimits.remove();

                CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
                        .strokeColor(Color.argb(50, 70,70,70))
                        .fillColor( Color.argb(100, 150,150,150) )
                        .radius( 200 );
                _geofenceCircleLimits = _map.addCircle( circleOptions );
            }

            // Create a marker for the geofence creation
            private void markerForGeofence(LatLng latLng) {
                Log.i(TAG, "markerForGeofence("+latLng+")");
                String title = latLng.latitude + ", " + latLng.longitude;
                // Define marker options
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .title(title);
                if ( _map !=null ) {
                    // Remove last _geofenceMarker
                    if (_geofenceMarker != null)
                        _geofenceMarker.remove();

                    _geofenceMarker = _map.addMarker(markerOptions);
                }
            }
            // Start Geofence creation process
            private void startGeofence() {
                Log.i(TAG, "startGeofence()");
                List<Geofence> geofence = createGeoFence();
                GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
                addGeofence( geofenceRequest );
            }
            // Add the created GeofenceRequest to the device's monitoring list
            private void addGeofence(GeofencingRequest request) {

                Log.d(TAG, "addGeofence");
                if (checkPermission())
                try
                {
                    LocationServices.GeofencingApi.addGeofences(
                            _googleApiClient,
                            request,
                            createGeofencePendingIntent()
                    ).setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "Saving Geofence");


                            } else {
                                Log.e(TAG, "Registering geofence failed: " + status.getStatusMessage() +
                                        " : " + status.getStatusCode());
                            }
                        }
                    });
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.getMessage());
                }

            }
            // Check for permission to access Location
            private boolean checkPermission() {


                Log.d(TAG, "checkPermission()");
                // Ask for permission if it wasn't granted yet
                return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED );
            }

            private void updatePassengerCounter(String username, String terminal)
            {
                new mySQLUpdatePassengerCounter(getApplicationContext(), this).execute(username, terminal);

            }
            private void passengerMovement(final String destinationValue, final String movement)
            {
                final HashMap<String, Object> count = new HashMap<>();
//                final DatabaseReference destinationDatabaseReference = _firebaseDatabase.getReference("destinations");
                final HashMap<String, Object> hashmapCount = new HashMap<>();
                final String uid = _sessionManager.getUsername();
                _destinationDatabaseReference.child(destinationValue).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot == null || dataSnapshot.getValue() == null)
                        {
                            if(movement.toLowerCase().equals("entered"))
                            {
                                _destinationDatabaseReference.child(destinationValue).child(uid).setValue(true);
                                updatePassengerCounter(_sessionManager.getUsername(), destinationValue);
                            }

                        }
                        else if(movement.toLowerCase().equals("exit")){
                                _destinationDatabaseReference.child(destinationValue).child(uid).removeValue();
                        }
                        else if (movement.toLowerCase().equals("entered"))
                        {
                            updatePassengerCounter(_sessionManager.getUsername(), destinationValue);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//                _destinationDatabaseReference.child(destinationValue).child("WaitingPassenger").runTransaction(new Transaction.Handler() {
//                    @Override
//                    public Transaction.Result doTransaction(MutableData currentData) {
//                        if(currentData.getValue() == null) {
//                            if(movement.toLowerCase().equals("entered"))
//                            {
//                                currentData.setValue(1);
//                                new mySQLSignUp(_context, MenuActivity.this).execute(_sessionManager.getUsername(),destinationValue);
//
//                            }
//
//
//                        } else {
//                            if(movement.toLowerCase().equals("entered"))
//                            {
//                                currentData.setValue((Long) currentData.getValue() + 1);
//                                new mySQLSignUp(_context, MenuActivity.this).execute(_sessionManager.getUsername(),destinationValue);
//                            }
//                            if(movement.toLowerCase().equals("exit"))
//                            {
//                                currentData.setValue((Long) currentData.getValue() - 1);
//                                new mySQLSignUp(_context, MenuActivity.this).execute(_sessionManager.getUsername(),"");
//                            }
//
//                        }
//                        return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
//                    }
//                    @Override
//                    public void onComplete(DatabaseError firebaseError, boolean committed, DataSnapshot currentData) {
//                        //This method will be called once with the results of the transaction.
//                    }
//                });

//                _destinationDatabaseReference.child(destinationValue).child("WaitingPassenger")
//                        .addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                Log.i(TAG, "Updating counter for " + destinationValue + " " + movement);
//                                if(dataSnapshot.getValue()==null)
//                                {
//                                    if(movement.toLowerCase().equals("entered"))
//                                    {
//
//                                        _destinationDatabaseReference.child(destinationValue).child("WaitingPassenger").setValue(1);
//                                        _destinationDatabaseReference.child(destinationValue).removeEventListener(this);
//
//                                        new mySQLSignUp(_context, MenuActivity.this).execute(_sessionManager.getUsername(),destinationValue);
//
//                                    }'
//
//                                }
//                                else
//                                {
//                                    long count = (long) dataSnapshot.getValue();
//                                    if(movement.toLowerCase().equals("entered"))
//                                    {
//                                        count++;
//                                        hashmapCount.put("WaitingPassenger", count);
//                                        _destinationDatabaseReference.child(destinationValue).updateChildren(hashmapCount);
//                                        _destinationDatabaseReference.child(destinationValue).removeEventListener(this);
//                                        new mySQLSignUp(_context, MenuActivity.this).execute(_sessionManager.getUsername(),destinationValue);
//                                    }
//                                    if(movement.toLowerCase().equals("exit"))
//                                    {
//                                        count--;
//                                        hashmapCount.put("WaitingPassenger", count);
//                                        _destinationDatabaseReference.child(destinationValue).updateChildren(hashmapCount);
//                                        _destinationDatabaseReference.child(destinationValue).removeEventListener(this);
//                                        new mySQLSignUp(_context, MenuActivity.this).execute(_sessionManager.getUsername(),"");
//                                    }
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//
//
//                        });
//
//

            }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    @Override
    public Drawable getDrawable(String arg0) {
        int id = 0;
        //string = d

        if(arg0.equals("ic_walking.png")){
            id = R.drawable.ic_walking;
        }

        Drawable empty = getResources().getDrawable(id);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

        return d;
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
        try {
            if (_chosenTerminal != null) {
                HashMap<Integer, Integer> destinationId_distance = new HashMap<>();
                String url = "https://maps.googleapis.com/maps/";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RetrofitMaps service = retrofit.create(RetrofitMaps.class);
                Call<Directions> call = service.getDistanceDuration("metric", _chosenTerminal.Lat + "," + _chosenTerminal.Lng, Looploc.latitude + "," + Looploc.longitude, "driving");
                call.enqueue(new Callback<Directions>() {
                    @Override
                    public void onResponse(Response<Directions> response, Retrofit retrofit) {
                        try {
                            for (int i = 0; i < response.body().getRoutes().size(); i++) {
                                if(_map!=null) {
                                    String TimeofArrival = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();
                                    MarkerOptions markerOpt = new MarkerOptions();
                                    LatLng latLng = new LatLng(Looploc.latitude, Looploc.longitude);
                                    markerOpt.position(latLng);
                                    markerOpt.title(TimeofArrival);
                                    markerOpt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ecoloop));
                                    markerOpt.anchor(0.5f, 0.5f);
                                    markerOpt.rotation(bearing);
                                    if(_marker==null){
                                        _marker = _map.addMarker(markerOpt);
                                    }
                                    _marker.showInfoWindow();
                                    _marker.setPosition(latLng);
                                    TimeOfArrivalTextView.setText(deviceid + " - " + TimeofArrival.toString());
                                }
                            }
                            //_markeropt.title(response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText());
                        } catch (Exception e) {
                            Log.d("onResponse", "There is an error");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d("onFailure", t.toString());
                    }
                });
                //return _chosenTerminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDuration().getText();
            }
        } catch(Exception ex){
            Toast.makeText(MenuActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
        }

    }

    public String CleanDirectionStep(String str){
        if(str!=null){
            if(str.contains("onto"))
            {
                str = str.replace("onto","on to");
            }
            if(str.contains("<div style=\"font-size:0.9em\">"))
            {
                str = str.replace("<div style=\"font-size:0.9em\">"," ");
            }
            if(str.contains("</div>"))
            {
                str = str.replace("</div>","");
            }
        }

        return str;
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String eventType = intent.getStringExtra(GeofenceTransitionsIntentService.KEY_EVENT_TYPE);
                    String geofenceRequestId = intent.getStringExtra(GeofenceTransitionsIntentService.KEY_GEOFENCEREQUESTID);
                    for(Destination d: _listDestinations)
                    {
                        if (d.GeofenceId.equals(geofenceRequestId))
                        {
                            passengerMovement(d.Value, eventType);
                            Toast.makeText(context, "You " + eventType + " " +  d.Description, Toast.LENGTH_LONG).show();
                        }

                    }
                }

            }
    private void initialiseOnlinePresence() {
        // any time that connectionsRef's value is null, device is offline
        if (!_sessionManager.isDriver())
        {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myConnectionsRef = database.getReference("users/"+ _sessionManager.getUsername() + "/connections");

            // stores the timestamp of last online
            final DatabaseReference lastOnlineRef = database.getReference("/users/" + _sessionManager.getUsername()+ "/lastOnline");

            final DatabaseReference connectedRef = database.getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        // when this device disconnects, remove it
                        myConnectionsRef.onDisconnect().setValue(false);
                        // update the last online timestamp
                        lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);

                        // add this device to connections list
                        myConnectionsRef.setValue(true);

                        final DatabaseReference destinationReference = database.getReference("destinations");
                        destinationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                {
                                    destinationReference.child(snapshot.getKey().toString()).child(_sessionManager.getUsername()).onDisconnect().removeValue();
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

    }

    private class FetchFBDPTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            URL imageURL = null;
            try {
                imageURL = new URL(fbImg);
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
                ProfilePictureImg.setImageBitmap(result);
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


}









