package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.umandalmead.samm_v1.Adapters.DestinationAdapter;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */

public class mySQLDestinationProvider extends AsyncTask<Void,Void, List<Terminal>>{
    Context _context;
    Activity _activity;
    GoogleMap _googleMap;
    GoogleApiClient mGoogleApiClient;
    PendingIntent mGeofencePendingIntent;
    protected static final String TAG = "Mead";
    public static float RADIUS = 10;
    public static final int LOITERINGDELAY = 10000;
    private GeofencingApi mGeofenceApi;
    private List<Terminal> mTerminals;
    ProgressDialog _progDialog;
    private Constants _constants = new Constants();
    Helper _helper = new Helper();
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
        _progDialog = null;

    }
    public mySQLDestinationProvider(Context context, Activity activity, String progressMessage, GoogleMap map, GoogleApiClient googleApiClient, ProgressDialog progDialog)
    {
        Log.i(TAG, "Called mySQLDestinationProvider");
        this.mGeofenceApi = LocationServices.GeofencingApi;
        this._context = context;
        this._activity = activity;
        this._googleMap = map;
        this.mGoogleApiClient = googleApiClient;
        _progDialog = progDialog;

    }

    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            if (_progDialog != null)
            {
                _progDialog.setMax(100);
                _progDialog.setMessage("The app is initializing, please wait...");
                _progDialog.setTitle("Initializing Data");
                _progDialog.setIndeterminate(false);
                _progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                _progDialog.setCancelable(false);
                _progDialog.show();
            }

        }
        catch(Exception ex)
        {
            Helper.logger(ex);
        }


    }
    @Override
    protected List<Terminal> doInBackground(Void... voids)
    {
        Helper helper = new Helper();
        List<Terminal> listTerminals = new ArrayList<>();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String link = _constants.WEB_API_URL + _constants.DESTINATIONS_API_FOLDER + "getDestinations.php?";
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray = new JSONArray(jsonResponse);
                for(int index=0; index < jsonArray.length(); index++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(index);
                    int ID       = jsonobject.getInt("ID");
                    int tblRouteID = jsonobject.getInt("tblRouteID");
                    String Value       = jsonobject.getString("Value");
                    String Description    = jsonobject.getString("Description");
                    int OrderOfArrival  = jsonobject.getInt("OrderOfArrival");
                    String Direction = jsonobject.getString("Direction");
                    double Lat  = jsonobject.getDouble("Lat");
                    double Lng = jsonobject.getDouble("Lng");
                    listTerminals.add(new Terminal(ID, tblRouteID, Value, Description, OrderOfArrival, Direction,Lat,Lng, "", null, GetDrawableID(Description)));
                }
                return listTerminals;
            }
            catch(Exception ex)
            {
                Helper.logger(ex);
                return null;
            }
        }
        else
        {
            Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();
            return null;
        }

    }

    private int GetDrawableID(String DestinationName){
        int drawableID = 0;
        if(DestinationName != null){
            DestinationName = DestinationName.toLowerCase();
            if(DestinationName.contains("hospital")){
               drawableID= R.drawable.ic_dest_hosp;
            }
            if(DestinationName.contains("hotel") || DestinationName.contains("bellevue") || DestinationName.contains("vivere")){
                drawableID = R.drawable.ic_dest_hotel;
            }
            if(DestinationName.contains("mall") || DestinationName.contains("festival")){
                drawableID = R.drawable.ic_dest_mall;
            }
            if(DestinationName.contains("fastbytes")){
                drawableID = R.drawable.ic_dest_food;
            }



        }
        return  drawableID == 0? R.drawable.ic_dest_bldg : drawableID ;
    }
    @Override
    protected void onPostExecute(List<Terminal> terminals)
    {
        try
        {
            Log.i(TAG,"mySQLDestinationProvider(onPostExecute)");
            List<String> strDestinations = new ArrayList<>();
            MenuActivity._terminalList = terminals;
            //((MenuActivity)this._activity)._terminalList = terminals;
            for (Terminal d: terminals)
            {
                if(!strDestinations.contains(d.Description))
                    strDestinations.add(d.Description);
            }

            DestinationAdapter adapter = null;
            ArrayList<Terminal> terminalArrayList = new ArrayList<>(terminals);
            //adapter =  new DestinationAdapter((MenuActivity)this._activity, terminalArrayList);
            //ClearableAutoCompleteTextView editDestination = (ClearableAutoCompleteTextView) (this._activity).findViewById(R.id.edit_destinations);
            //ArrayAdapter<Terminal> adapter = new ArrayAdapter<>(this._context, R.layout.list_item, terminals);
            //editDestination.setThreshold(1);
            //editDestination.setAdapter(adapter);
            //editDestination.setDropDownAnchor(MenuActivity._AppBar.getId());
            _googleMap.clear();
            MenuActivity._terminalMarkerHashmap = new HashMap<>();

            for (Terminal terminal : terminals)
            {
                if(terminal.Lat >0 && terminal.Lng >0)
                {
                    double lat = terminal.Lat;
                    double lng = terminal.Lng;
                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);

                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ecoloopstop));
                    markerOptions.snippet("0 passenger/s waiting");
                    markerOptions.title(terminal.Value);
                    Marker marker = _googleMap.addMarker(markerOptions);

                    marker.showInfoWindow();

                    MenuActivity._terminalMarkerHashmap.put(terminal.Value, marker);
                }
            }

            startGeofence(terminals);
        }
        catch(Exception ex)
        {
            Helper.logger(ex);
        }


        if (_progDialog != null)
            _progDialog.dismiss();
    }

    // Start Geofence creation process
    private void startGeofence(List<Terminal> listTerminals) {


        Log.i(TAG, "startGeofence()");
        mTerminals = listTerminals;
        List<Geofence> geofences = createGeoFence(listTerminals);
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
    public List<Geofence> createGeoFence(List<Terminal> listTerminals)
    {
        Log.i(TAG, "createGeoFence");
        int i = 0;
        String geofenceRequestId = "";
        List<Geofence> geofenceList = new ArrayList<>();
        if (listTerminals != null)
            for (Terminal terminal : listTerminals) {
                if(terminal.Lat>0 && terminal.Lng>0) {
                    try
                    {
                        geofenceRequestId = UUID.randomUUID().toString();
                        geofenceList.add(new Geofence.Builder()
                                .setRequestId(geofenceRequestId)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
//                                .setLoiteringDelay(5000)
                                .setCircularRegion(terminal.Lat, terminal.Lng, RADIUS)
                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .build());

                        listTerminals.get(i).GeofenceId = geofenceRequestId;
                    }
                    catch(Exception ex)
                    {
                        Helper.logger(ex);
                    }
                }
                i++;

//                if(i==1)
//                    break;
                //drawGeofence(new LatLng(terminal.Lat, terminal.Lng));
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
                            drawGeofence(mTerminals);
                        } else {
                            _helper.logger("Registering geofence failed: " + status.getStatusMessage() +
                                            " : " + status.getStatusCode());

                        }
                    }
                });
            }
            catch (Exception ex)
            {
                Helper.logger(ex);
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
    private void drawGeofence(List<Terminal> terminals) {
        Log.d(TAG, "drawGeofence()");
        for(Terminal d: terminals)
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
