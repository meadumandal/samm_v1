package com.example.samm_v1;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.samm_v1.EntityObjects.Destination;
import com.example.samm_v1.POJO.Directions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

public class MenuActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        LocationListener {
            GoogleApiClient mGoogleApiClient;
            Marker mCurrLocationMarker;
            LocationRequest mLocationRequest;
            private GoogleMap mMap;
            FirebaseDatabase userDatabase;
            DatabaseReference userDatabaseReference;
            DatabaseReference _destinationDatabaseReference;
            SessionManager sessionManager;
            boolean isFirstLoad;
            LatLng origin;
            LatLng destination;
            LatLng currentLocation;
            Destination bestTerminal;
            List<Destination> possibleTerminals;
//            TextView showDistanceDuration;
            ArrayList<LatLng> MarkerPoints;
            Polyline line;
            Helper _helper;
            Context _context;
            List<Geofence> _geoFenceList;
            PendingIntent mGeofencePendingIntent;
            private Circle geoFenceLimits;
            private Marker geoFenceMarker;
            public List<Destination> listDestinations;
            HashMap hashmap_markerMap = new HashMap();

            protected static final String TAG = "Mead";
            public static float RADIUS = 50;
            protected static final int REQUEST_CHECK_SETTINGS = 0x1;
            public static final int MY_PERMISSION_REQUEST_LOCATION=99;
            MyBroadcastReceiver myBroadcastReceiver;




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


            @Override
            protected void onCreate(Bundle savedInstanceState) {
                Log.i(TAG, "onCreate");

//                showDistanceDuration = (TextView) findViewById(R.id.show_distance_time);
                MarkerPoints = new ArrayList<>();
                _helper = new Helper();
                _context = getApplicationContext();
                _geoFenceList = new ArrayList<>();


                displayLocationSettingsRequest(_context);
                super.onCreate(savedInstanceState);
                isFirstLoad = true;
                if(sessionManager==null)
                    sessionManager = new SessionManager(_context);
                if(!sessionManager.isLoggedIn())
                {
                    sessionManager.logoutUser();
                    Intent i = new Intent(MenuActivity.this, LoginActivity.class);
                    finish();
                    startActivity(i);
                }
                setContentView(R.layout.activity_menu);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                toolbar.setTitle("SAMM");

                if(userDatabase == null && userDatabaseReference ==null)
                {
                    userDatabase = FirebaseDatabase.getInstance();
                    userDatabaseReference = userDatabase.getReference("users");
                    _destinationDatabaseReference = userDatabase.getReference("destinations");
                }
                AutoCompleteTextView editDestinations = (AutoCompleteTextView) findViewById(R.id.edit_destinations);

                editDestinations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Destination chosenDestination = (Destination) adapterView.getItemAtPosition(i);
                        saveDestination(chosenDestination.Value);
                        possibleTerminals = new ArrayList<>();

                        //get best terminal by fewest passenger, by nearest from current location, by fewest stops
                        //dummy data for now.
                        possibleTerminals.add(listDestinations.get(0));
                        possibleTerminals.add(listDestinations.get(1));
                        possibleTerminals.add(listDestinations.get(2));
                        bestTerminal = possibleTerminals.get(2);


                        origin = currentLocation;
                        destination = new LatLng(bestTerminal.Lat, bestTerminal.Lng);
                        build_retrofit_and_get_response("walking");
                    }
                });

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    checkLocationPermission();
                }

                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(map);
                mapFragment.getMapAsync(this);


                userDatabaseReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        String username = dataSnapshot.child("username").getValue().toString();
                        if (!username.equals(sessionManager.getUsername()))
                        {
                            Marker marker;
                            marker = (Marker) hashmap_markerMap.get(username);
                            if(marker !=null)
                            {
                                marker.remove();
                                    hashmap_markerMap.remove(username);
                            }
                            Object Latitude = dataSnapshot.child("Latitude").getValue();
                            Object Longitude = dataSnapshot.child("Longitude").getValue();
                            double lat, lng;
                            if(Latitude == null || Latitude.toString().equals("0"))
                                lat = 0.0;
                            else
                                lat = (double) Latitude;
                            if(Longitude == null || Longitude.toString().equals("0"))
                                lng = 0.0;
                            else
                                lng = (double) Longitude;

                            LatLng latLng = new LatLng(lat, lng);
                            if (mMap!=null)
                            {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(username);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                marker = mMap.addMarker(markerOptions);
                                marker.showInfoWindow();
                                hashmap_markerMap.put(username, marker);
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

                myBroadcastReceiver = new MyBroadcastReceiver();

                //register BroadcastReceiver
                IntentFilter intentFilter = new IntentFilter(GeofenceTransitionsIntentService.ACTION_MyIntentService);
                intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

                registerReceiver(myBroadcastReceiver, intentFilter);

            }

            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.i(TAG,"onMapReady");
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                //Initialize Google Play Services
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);

//                        createGeoFence(14.42576,121.03898);
                    }
                }
                else {
                    buildGoogleApiClient();

                    mMap.setMyLocationEnabled(true);
                }




            }

            private void build_retrofit_and_get_response(String type)
            {
                final ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(MenuActivity.this);
                progressDialog.setMax(100);
                progressDialog.setMessage("Please wait as we search for the best route");
                progressDialog.setTitle("Analyzing Routes");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();

                String url = "https://maps.googleapis.com/maps/";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RetrofitMaps service = retrofit.create(RetrofitMaps.class);

                Call<Directions> call = service.getDistanceDuration("metric", origin.latitude + "," + origin.longitude,destination.latitude + "," + destination.longitude, type);

                call.enqueue(new Callback<Directions>() {
                    @Override
                    public void onResponse(Response<Directions> response, Retrofit retrofit) {

                        try {
                            //Remove previous line from map
//                            if (line != null) {
//                                line.remove();
//                            }
                            // This loop will go through all the results and add marker on each location.
                            for (int i = 0; i < response.body().getRoutes().size(); i++) {
                                String distance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                                String time = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
//                                showDistanceDuration.setText("Distance:" + distance + ", Duration:" + time);
                                String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                                List<LatLng> list = decodePoly(encodedString);
                                line = mMap.addPolyline(new PolylineOptions()
                                        .addAll(list)
                                        .width(5f)
                                        .color(Color.RED)
                                        .geodesic(true)
                                );
                            }
                        } catch (Exception e) {
                            Log.d("onResponse", "There is an error");
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        progressDialog.dismiss();
                        Log.d("onFailure", t.toString());
                    }
                });
            }

            private List<LatLng> decodePoly(String encoded) {
                List<LatLng> poly = new ArrayList<>();
                int index = 0, len = encoded.length();
                int lat = 0, lng = 0;

                while (index < len) {
                    int b, shift = 0, result = 0;
                    do {
                        b = encoded.charAt(index++) - 63;
                        result |= (b & 0x1f) << shift;
                        shift += 5;
                    } while (b >= 0x20);
                    int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                    lat += dlat;

                    shift = 0;
                    result = 0;
                    do {
                        b = encoded.charAt(index++) - 63;
                        result |= (b & 0x1f) << shift;
                        shift += 5;
                    } while (b >= 0x20);
                    int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                    lng += dlng;

                    LatLng p = new LatLng( (((double) lat / 1E5)),
                            (((double) lng / 1E5) ));
                    poly.add(p);
                }

                return poly;
            }
            protected synchronized void buildGoogleApiClient() {
                if(_helper.isGooglePlayInstalled(_context)) {
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
                    mGoogleApiClient.connect();
                }
                else
                {
                    Toast.makeText(_context, "Please install google play service", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLocationChanged(Location location) {
//                Toast.makeText(this, "Location updated", Toast.LENGTH_LONG).show();
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                //Place current location marker
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                currentLocation = new LatLng(lat, lng);

                saveLocation(lat, lng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(currentLocation);
                markerOptions.title(sessionManager.getUsername());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                mCurrLocationMarker = mMap.addMarker(markerOptions);
                mCurrLocationMarker.showInfoWindow();

                if(isFirstLoad) {
                    isFirstLoad = false;
                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
                }
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                //stop location updates
                if (mGoogleApiClient != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
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


                userDatabaseReference.child(sessionManager.getUsername()).child("currentDestination").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null)
                        {
                            userDatabaseReference.child(sessionManager.getUsername()).child("currentDestination").setValue(currentDestination.get("currentDestination"));
                        }
                        else
                        {
                            userDatabaseReference.child(sessionManager.getUsername()).updateChildren(currentDestination);
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
                userDatabaseReference.child(sessionManager.getUsername()).child("Longitude")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()==null)
                                {
                                    userDatabaseReference.child(sessionManager.getUsername()).child("Longitude").setValue(longitude);
                                }
                                else
                                {
                                    userDatabaseReference.child(sessionManager.getUsername()).updateChildren(longitude);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                userDatabaseReference.child(sessionManager.getUsername()).child("Latitude")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()==null)
                                {
                                    userDatabaseReference.child(sessionManager.getUsername()).push().setValue(latitude);
                                }
                                else
                                {
                                    userDatabaseReference.child(sessionManager.getUsername()).updateChildren(latitude);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                userDatabaseReference.child(sessionManager.getUsername()).child("lastUpdated")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()==null)
                                {
                                    userDatabaseReference.child(sessionManager.getUsername()).child("lastUpdated").setValue(hashLastUpdated);
                                }
                                else
                                {
                                    userDatabaseReference.child(sessionManager.getUsername()).updateChildren(hashLastUpdated);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                new mySQLDestinationProvider(_context, MenuActivity.this, "", mMap, mGoogleApiClient).execute();
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(0);
                mLocationRequest.setFastestInterval(0);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, this);

                }
//                try{
//                    startGeofence();
//                } catch (Exception e) {
//                    // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
//                    Log.e(TAG, e.getMessage());
//                }


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

                                if (mGoogleApiClient == null) {
                                    buildGoogleApiClient();

                                }
                                mMap.setMyLocationEnabled(true);
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
                if (id == R.id.action_settings) {
                    return true;
                }

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
                    sessionManager.logoutUser();
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

                if ( geoFenceLimits != null )
                    geoFenceLimits.remove();

                CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
                        .strokeColor(Color.argb(50, 70,70,70))
                        .fillColor( Color.argb(100, 150,150,150) )
                        .radius( 200 );
                geoFenceLimits = mMap.addCircle( circleOptions );
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
                if ( mMap!=null ) {
                    // Remove last geoFenceMarker
                    if (geoFenceMarker != null)
                        geoFenceMarker.remove();

                    geoFenceMarker = mMap.addMarker(markerOptions);
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
                            mGoogleApiClient,
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
//                final DatabaseReference destinationDatabaseReference = userDatabase.getReference("destinations");
                final HashMap<String, Object> hashmapCount = new HashMap<>();
                _destinationDatabaseReference.child(destinationValue).child("WaitingPassenger").runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if(currentData.getValue() == null) {
                            if(movement.toLowerCase().equals("entered"))
                            {
                                currentData.setValue(1);
                                new mySQLUpdatePassengerMovement(_context, MenuActivity.this).execute(sessionManager.getUsername(),destinationValue);

                            }


                        } else {
                            if(movement.toLowerCase().equals("entered"))
                            {
                                currentData.setValue((Long) currentData.getValue() + 1);
                                new mySQLUpdatePassengerMovement(_context, MenuActivity.this).execute(sessionManager.getUsername(),destinationValue);
                            }
                            if(movement.toLowerCase().equals("exit"))
                            {
                                currentData.setValue((Long) currentData.getValue() - 1);
                                new mySQLUpdatePassengerMovement(_context, MenuActivity.this).execute(sessionManager.getUsername(),"");
                            }

                        }
                        return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                    }
                    @Override
                    public void onComplete(DatabaseError firebaseError, boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                    }
                });

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
//                                        new mySQLUpdatePassengerMovement(_context, MenuActivity.this).execute(sessionManager.getUsername(),destinationValue);
//
//                                    }
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
//                                        new mySQLUpdatePassengerMovement(_context, MenuActivity.this).execute(sessionManager.getUsername(),destinationValue);
//                                    }
//                                    if(movement.toLowerCase().equals("exit"))
//                                    {
//                                        count--;
//                                        hashmapCount.put("WaitingPassenger", count);
//                                        _destinationDatabaseReference.child(destinationValue).updateChildren(hashmapCount);
//                                        _destinationDatabaseReference.child(destinationValue).removeEventListener(this);
//                                        new mySQLUpdatePassengerMovement(_context, MenuActivity.this).execute(sessionManager.getUsername(),"");
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

            public class MyBroadcastReceiver extends BroadcastReceiver {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String eventType = intent.getStringExtra(GeofenceTransitionsIntentService.KEY_EVENT_TYPE);
                    String geofenceRequestId = intent.getStringExtra(GeofenceTransitionsIntentService.KEY_GEOFENCEREQUESTID);
                    for(Destination d:listDestinations)
                    {
                        if (d.GeofenceId.equals(geofenceRequestId))
                        {
                            passengerMovement(d.Value, eventType);
                            Toast.makeText(context, "You " + eventType + " " +  d.Description, Toast.LENGTH_LONG).show();
                        }

                    }
                }

            }


        }




