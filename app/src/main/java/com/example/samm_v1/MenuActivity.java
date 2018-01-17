package com.example.samm_v1;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samm_v1.EntityObjects.Destination;
import com.example.samm_v1.POJO.Directions;
import com.example.samm_v1.POJO.Route;
import com.example.samm_v1.POJO.Steps;
import com.example.samm_v1.RouteTabs.Route1;
import com.example.samm_v1.RouteTabs.Route2;
import com.example.samm_v1.RouteTabs.Route3;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.example.samm_v1.R.id.map;
import static com.example.samm_v1.R.id.route_tablayout;
import static com.example.samm_v1.R.id.visible;

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
            private GoogleMap _map;
            FirebaseDatabase _firebaseDatabase;
            DatabaseReference _userDatabaseReference;
            DatabaseReference _destinationDatabaseReference;
            SessionManager _sessionManager;
            boolean _isFirstLoad;
            LatLng _currentLocation;
            Destination _bestTerminal;
            List<Destination> _candidateTerminals;
            ArrayList<LatLng> _markerPoints;
            Polyline _polyLine;
            Helper _helper;
            Context _context;
            List<Geofence> _geoFenceList;
            PendingIntent _geoFencePendingIntent;
            private Circle _geofenceCircleLimits;
            private Marker _geofenceMarker;
            public List<Destination> _listDestinations;
            HashMap _hashmapMarkerMap = new HashMap();
            HashMap _driverMarkers = new HashMap();
            MyBroadcastReceiver _broadcastReceiver;
            DatabaseReference _driverDatabaseReference;
            public static LevelListDrawable d = new LevelListDrawable();
            public  static Destination _ChosenDestination;
    Marker _marker;


            protected static final String TAG = "mead";
            public static float RADIUS = 50;
            protected static final int REQUEST_CHECK_SETTINGS = 0x1;
            public static final int MY_PERMISSION_REQUEST_LOCATION=99;

            //Declared as public so that they can be accessed on other context.
            public static AutoCompleteTextView EditDestinationsPH;
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
                if (isOnline()) {
                    Log.i(TAG, "onCreate");
                    _markerPoints = new ArrayList<>();
                    _helper = new Helper();
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
                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                    RoutePane = (LinearLayout) findViewById(R.id.route_content);
                    SlideUpPanelContainer = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
                    RouteTabLayout = (TabLayout) findViewById(R.id.route_tablayout);
                    Slide_Collapse = (ImageView) findViewById(R.id.ev_panel_collapse);
                    Slide_Expand = (ImageView) findViewById(R.id.ev_panel_expand);
                    StepsScroller = (ScrollView) findViewById(R.id.step_scroll_view);
                    TimeOfArrivalTextView = (TextView) findViewById(R.id.toatextview);

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
                        }
                    });
                    editDestinations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            //hide keyboard on search ~
                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.hideSoftInputFromWindow(editDestinations.getWindowToken(), 0);

                            Destination chosenDestination = (Destination) adapterView.getItemAtPosition(i);
                            _ChosenDestination = chosenDestination;
                            saveDestination(chosenDestination.Value);

                            _candidateTerminals = new ArrayList<>();
                            for (Destination destination : _listDestinations) {
                                if (destination.Direction.equals(chosenDestination.Direction)) {
                                    if (destination.OrderOfArrival < chosenDestination.OrderOfArrival)
                                        _candidateTerminals.add(destination);
                                }
                            }
                            new AnalyzeForBestRoutes(_context, MenuActivity.this, _map, _currentLocation, getSupportFragmentManager(), _candidateTerminals, chosenDestination).execute();
                        }
                    });

//                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//                fab.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
//                    }
//                });

                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    drawer.setDrawerListener(toggle);
                    toggle.getDrawerArrowDrawable().setColor(getColor(R.color.colorWhite));
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


                    _userDatabaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            String username = dataSnapshot.child("username").getValue().toString();
                            if (!username.equals(_sessionManager.getUsername())) {
                                Marker marker;
                                marker = (Marker) _hashmapMarkerMap.get(username);
                                if (marker != null) {
                                    marker.remove();
                                    _hashmapMarkerMap.remove(username);
                                }
                                Object Latitude = dataSnapshot.child("Latitude").getValue();
                                Object Longitude = dataSnapshot.child("Longitude").getValue();
                                double lat, lng;
                                if (Latitude == null || Latitude.toString().equals("0"))
                                    lat = 0.0;
                                else
                                    lat = (double) Latitude;
                                if (Longitude == null || Longitude.toString().equals("0"))
                                    lng = 0.0;
                                else
                                    lng = (double) Longitude;

                                LatLng latLng = new LatLng(lat, lng);
                                if (_map != null) {
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(latLng);
                                    markerOptions.title(username);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                    marker = _map.addMarker(markerOptions);
                                    marker.showInfoWindow();
                                    _hashmapMarkerMap.put(username, marker);
                                }
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

                    _driverDatabaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            try {
                                String deviceId = dataSnapshot.getKey();

                                Marker marker;
                                marker = (Marker) _driverMarkers.get(deviceId);
                                if (marker != null) {
                                    marker.remove();
                                    _driverMarkers.remove(deviceId);
                                }
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

                                LatLng latLng = new LatLng(lat, lng);
                                if (_map != null) {
                                    if(_ChosenDestination==null){
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(latLng);
                                        markerOptions.title(deviceId);
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus));
                                    marker = _map.addMarker(markerOptions);
                                    marker.showInfoWindow();
                                    _driverMarkers.put(deviceId, marker);
                                    }
                                    else{
                                        ShowLoopTimeofArrival(latLng, deviceId);
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

                    _broadcastReceiver = new MyBroadcastReceiver();

                    //register BroadcastReceiver
                    IntentFilter intentFilter = new IntentFilter(GeofenceTransitionsIntentService.ACTION_MyIntentService);
                    intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

                    registerReceiver(_broadcastReceiver, intentFilter);
                    initialiseOnlinePresence();
                }
                else{
                    HideNetCheckerDialog(getApplicationContext());
                    //Snackbar.make(findViewById(R.id.content), "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
                markerOptions.title(_sessionManager.getUsername());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                _currentLocationMarker = _map.addMarker(markerOptions);
                _currentLocationMarker.showInfoWindow();

                if(_isFirstLoad) {
                    _isFirstLoad = false;
                    //move map camera
                    _map.moveCamera(CameraUpdateFactory.newLatLngZoom(_currentLocation, 16));
                    _map.animateCamera(CameraUpdateFactory.newLatLngZoom(_currentLocation, 16));
                }
//                _map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                //stop location updates
                if (_googleApiClient != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(_googleApiClient, this);
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
                                    _userDatabaseReference.child(_sessionManager.getUsername()).push().setValue(latitude);
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
                int id = item.getItemId();
                android.app.FragmentManager fragment = getFragmentManager();

                if (id == R.id.nav_share) {

                    // fragment.beginTransaction().replace(R.id.content_frame, new MapsActivity()).commit();
                    startActivity(new Intent(MenuActivity.this, MapsActivity.class));
                } else if (id == R.id.nav_logout) {
                    _sessionManager.logoutUser();
                    Intent i = new Intent(MenuActivity.this, LoginActivity.class);

                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(i);

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
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
                                _destinationDatabaseReference.child(destinationValue).child(uid).setValue(true);
                        }
                        else if(movement.toLowerCase().equals("exit")){
                                _destinationDatabaseReference.child(destinationValue).child(uid).removeValue();
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
//                                new mySQLUpdatePassengerMovement(_context, MenuActivity.this).execute(_sessionManager.getUsername(),destinationValue);
//
//                            }
//
//
//                        } else {
//                            if(movement.toLowerCase().equals("entered"))
//                            {
//                                currentData.setValue((Long) currentData.getValue() + 1);
//                                new mySQLUpdatePassengerMovement(_context, MenuActivity.this).execute(_sessionManager.getUsername(),destinationValue);
//                            }
//                            if(movement.toLowerCase().equals("exit"))
//                            {
//                                currentData.setValue((Long) currentData.getValue() - 1);
//                                new mySQLUpdatePassengerMovement(_context, MenuActivity.this).execute(_sessionManager.getUsername(),"");
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
//                                        new mySQLUpdatePassengerMovement(_context, MenuActivity.this).execute(_sessionManager.getUsername(),destinationValue);
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
//                                        new mySQLUpdatePassengerMovement(_context, MenuActivity.this).execute(_sessionManager.getUsername(),destinationValue);
//                                    }
//                                    if(movement.toLowerCase().equals("exit"))
//                                    {
//                                        count--;
//                                        hashmapCount.put("WaitingPassenger", count);
//                                        _destinationDatabaseReference.child(destinationValue).updateChildren(hashmapCount);
//                                        _destinationDatabaseReference.child(destinationValue).removeEventListener(this);
//                                        new mySQLUpdatePassengerMovement(_context, MenuActivity.this).execute(_sessionManager.getUsername(),"");
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

    public void ShowLoopTimeofArrival(final LatLng Looploc, final String deviceid) {
        try {
            if (_ChosenDestination != null) {
                HashMap<Integer, Integer> destinationId_distance = new HashMap<>();
                String url = "https://maps.googleapis.com/maps/";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RetrofitMaps service = retrofit.create(RetrofitMaps.class);
                Call<Directions> call = service.getDistanceDuration("metric", _ChosenDestination.Lat + "," + _ChosenDestination.Lng, Looploc.latitude + "," + Looploc.longitude, "driving");
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
                                    markerOpt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus));
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
                //return _ChosenDestination.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDuration().getText();
            }
            } catch(Exception ex){
                Toast.makeText(MenuActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
            }

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




