package com.example.samm_v1;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.samm_v1.EntityObjects.Destination;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */

public class mySQLDestinationProvider extends AsyncTask<Void,Void, List<Destination>>{
    Context _context;
    Activity _activity;
    GoogleMap _googleMap;
    GoogleApiClient mGoogleApiClient;
    PendingIntent mGeofencePendingIntent;





    protected static final String TAG = "Mead";
    public static float RADIUS = 20;
    public static final int LOITERINGDELAY = 10000;
    private GeofencingApi mGeofenceApi;
    private List<Destination> mDestinations;




    /**
     *
     * This gets destination value, description, order of arrival, lat and lng
     * @param context
     * @param activity
     * @param progressMessage Message that will appear in UI while processing
     * @param map Pass the main map of the app, so the asynctask will be able to pin the location of destinations
     */
    public mySQLDestinationProvider(Context context, Activity activity, String progressMessage, GoogleMap map, GoogleApiClient googleApiClient)
    {

        Log.i(TAG, "Called mySQLDestinationProvider");
        this.mGeofenceApi = LocationServices.GeofencingApi;
        this._context = context;
        this._activity = activity;
        this._googleMap = map;
        this.mGoogleApiClient = googleApiClient;

    }

    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
        }
        catch(Exception e)
        {
            Toast.makeText(this._context, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
    @Override
    protected List<Destination> doInBackground(Void... voids)
    {
        Helper helper = new Helper();
        List<Destination> listDestinations = new ArrayList<>();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String link = "http://meadumandal.website/sammAPI/getDestinations.php?";
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray = new JSONArray(jsonResponse);
                for(int index=0; index < jsonArray.length(); index++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(index);
                    int ID       = jsonobject.getInt("ID");
                    String Value       = jsonobject.getString("Value");
                    String Description    = jsonobject.getString("Description");
                    int OrderOfArrival  = jsonobject.getInt("OrderOfArrival");
                    String Direction = jsonobject.getString("Direction");
                    double Lat  = jsonobject.getDouble("Lat");
                    double Lng = jsonobject.getDouble("Lng");
                    listDestinations.add(new Destination(ID, Value, Description, OrderOfArrival, Direction,Lat,Lng, "", null));
                }
                return listDestinations;
            }
            catch(Exception e)
            {
                Toast.makeText(this._context,e.getMessage(), Toast.LENGTH_LONG).show();
                return null;
            }
        }
        else
        {
            Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();
            return null;
        }

    }

    @Override
    protected void onPostExecute(List<Destination> destinations)
    {
        Log.i(TAG,"mySQLDestinationProvider(onPostExecute)");
        List<String> strDestinations = new ArrayList<>();
        ((MenuActivity)this._activity)._listDestinations = destinations;
        for (Destination d:destinations)
        {
            if(!strDestinations.contains(d.Description))
                strDestinations.add(d.Description);
        }


        AutoCompleteTextView editDestination = (AutoCompleteTextView) (this._activity).findViewById(R.id.edit_destinations);
        ArrayAdapter<Destination> adapter = new ArrayAdapter<>(this._context, R.layout.list_item, destinations);
        editDestination.setThreshold(1);
        editDestination.setAdapter(adapter);

        for (Destination destination:destinations)
        {
            if(destination.Lat >0 && destination.Lng >0)
            {
                double lat = destination.Lat;
                double lng = destination.Lng;
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ecoloopstop));
                markerOptions.snippet("0 passenger/s waiting");
                markerOptions.title(destination.Value);
                Marker marker = _googleMap.addMarker(markerOptions);
                marker.showInfoWindow();

                ((MenuActivity)this._activity)._destinationMarkers.put(destination.Value, marker);
            }
        }
        startGeofence(destinations);
    }

    // Start Geofence creation process
    private void startGeofence(List<Destination> listDestinations) {


        Log.i(TAG, "startGeofence()");
        mDestinations = listDestinations;
        List<Geofence> geofences = createGeoFence(listDestinations);
        GeofencingRequest geofenceRequest = createGeofenceRequest(geofences);
        addGeofence( geofenceRequest );
    }
    private GeofencingRequest createGeofenceRequest(List<Geofence> geofence)
    {
        Log.i(TAG, "createGeofenceRequest()");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofence);
        return builder.build();
    }
    public List<Geofence> createGeoFence(List<Destination> listDestinations)
    {
        Log.i(TAG, "createGeoFence");
        int i = 0;
        String geofenceRequestId = "";
        List<Geofence> geofenceList = new ArrayList<>();
        if (listDestinations != null)
            for (Destination destination:listDestinations) {
                if(destination.Lat>0 && destination.Lng>0) {
                    try
                    {
                        geofenceRequestId = UUID.randomUUID().toString();
                        geofenceList.add(new Geofence.Builder()
                                .setRequestId(geofenceRequestId)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
//                                .setLoiteringDelay(5000)
                                .setCircularRegion(destination.Lat, destination.Lng, RADIUS)
                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .build());

                        listDestinations.get(i).GeofenceId = geofenceRequestId;
                    }
                    catch(Exception e)
                    {
                        Log.e(TAG, e.getMessage());
                    }
                }
                i++;

//                if(i==1)
//                    break;
                //drawGeofence(new LatLng(destination.Lat, destination.Lng));
            }

        return geofenceList;
    }
    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request) {

        Log.d(TAG, "addGeofence");
        if (checkPermission())
            try
            {
                PendingIntent pendingIntent = createGeofencePendingIntent();

                LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, pendingIntent);
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        request,
                        pendingIntent
                ).setResultCallback(new ResultCallback<com.google.android.gms.common.api.Status>() {

                    @Override
                    public void onResult(com.google.android.gms.common.api.Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Success Saving Geofence");
                            drawGeofence(mDestinations);
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
        return (ContextCompat.checkSelfPermission(this._activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }
    private PendingIntent createGeofencePendingIntent()
    {
        Log.i(TAG, "createGeofencePendingIntent()");
        Intent intent = new Intent(this._activity, GeofenceTransitionsIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this._activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        this._context.startService(intent);
        return pendingIntent;


    }
    private void drawGeofence(List<Destination> destinations) {
        Log.d(TAG, "drawGeofence()");
        for(Destination d:destinations)
        {
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(d.Lat, d.Lng))
                    .strokeColor(Color.argb(50, 70,70,70))
                    .fillColor( Color.argb(100, 150,150,150) )
                    .radius( RADIUS );
            _googleMap.addCircle( circleOptions );
        }

    }



}
