package com.umandalmead.samm_v1.Modules.ManageStations;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.umandalmead.samm_v1.Constants;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.LoaderDialog;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.NoticeDialog;
import com.umandalmead.samm_v1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */

public class mySQLStationProvider extends AsyncTask<Integer,Void, List<Terminal>>{
    Context _context;
    Activity _activity;
    GoogleMap _googleMap;
    GoogleApiClient _googleApiClient;
    LoaderDialog _loader;
    PendingIntent mGeofencePendingIntent;

    public static float RADIUS = 10;
    public static final int LOITERINGDELAY = 10000;
    private GeofencingApi mGeofenceApi;
    private List<Terminal> mTerminals;
    private Constants _constants = new Constants();
    Helper _helper;
    /**
     *
     * This gets destination value, description, order of arrival, lat and lng
     * @param context
     * @param activity
     * @param progressMessage Message that will appear in UI while processing
     * @param map Pass the main map of the app, so the asynctask will be able to pin the location of destinations
     */
    public mySQLStationProvider(Context context, Activity activity, String progressMessage, GoogleMap map, GoogleApiClient googleApiClient)
    {
        Log.i(LOG_TAG, "Called mySQLStationProvider");
        this.mGeofenceApi = LocationServices.GeofencingApi;
        this._context = context;
        this._activity = activity;
        this._googleMap = map;
        this._googleApiClient = googleApiClient;
        this._helper = new Helper(activity, context);
        _loader = null;

    }
    public mySQLStationProvider(Context context, Activity activity, String progressMessage, GoogleMap map, GoogleApiClient googleApiClient, LoaderDialog loaderDialog)
    {
        Log.i(LOG_TAG, "Called mySQLStationProvider");
        this.mGeofenceApi = LocationServices.GeofencingApi;
        this._context = context;
        this._activity = activity;
        this._googleMap = map;
        this._googleApiClient = googleApiClient;
        _loader = loaderDialog;

    }

    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            if (_loader == null)
            {
                _loader = new LoaderDialog(_activity, MenuActivity._GlobalResource.getString(R.string.dialog_title_initializing_destinations) ,MenuActivity._GlobalResource.getString(R.string.dialog_message_initializing_destinations));
                _loader.setCancelable(false);
                _loader.show();
            }

        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }


    }
    @Override
    protected List<Terminal> doInBackground(Integer... params)
    {
        Helper helper = new Helper();
        List<Terminal> listTerminals = new ArrayList<>();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String link = _constants.WEB_API_URL + _constants.DESTINATIONS_API_FOLDER + _constants.DESTINATIONS_API_FILE;
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
                    double distanceFromPreviousStation = jsonobject.getDouble("distanceFromPreviousStation");
                    String LineName = jsonobject.getString("LineName");
                    String isMainTerminal = jsonobject.getString("isMainTerminal");
                    String routeName = jsonobject.getString("routeName");
                    Integer lineID = jsonobject.getInt("LineID");




                    listTerminals.add(new Terminal(ID, tblRouteID, Value, Description, OrderOfArrival, Direction,Lat,Lng, "", null, GetDrawableID(Description), LineName,isMainTerminal,routeName, lineID, distanceFromPreviousStation));
                }
                return listTerminals;
            }
            catch(Exception ex)
            {
                Helper.logger(ex,true);
                return null;
            }
        }
        else
        {
            Toast.makeText(this._context, MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline), Toast.LENGTH_LONG).show();
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
            Log.i(LOG_TAG,"mySQLStationProvider(onPostExecute)");
//            List<String> strDestinations = new ArrayList<>();
            HashMap<String, Terminal> distinctTerminals = new HashMap<>();

            if(terminals == null){
                //IsPersitent|IsMaintenance|Title|Details|URL
                NoticeDialog ND_TerminalEmpty = new NoticeDialog(_activity,"Empty Data","False|False|Empty Data|Internal error.\nNo terminal/station found. Searching for target destination has been disabled.");
                ND_TerminalEmpty.show();
                ND_TerminalEmpty.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        MenuActivity._SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    }
                });
            }
            else {
                MenuActivity._terminalList = terminals;
            }
            //((MenuActivity)this._activity)._terminalList = terminals;
//            for (Terminal d: terminals)
//            {
//                if(!strDestinations.contains(d.Description))
//                    strDestinations.add(d.Description);
//            }
//
//            StationCustomAdapter adapter = null;
//            ArrayList<Terminal> terminalArrayList = new ArrayList<>(terminals);

            for (Terminal d: terminals)
            {
                if (!distinctTerminals.containsKey(d.getValue()))
                {
                    distinctTerminals.put(d.getValue(), d);
                }
            }
            MenuActivity._distinctTerminalList = distinctTerminals;
            _helper.UpdateStationMarkersOnTheMap(terminals, _googleMap, _googleApiClient);
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }


        if (_loader != null)
            _loader.dismiss();
    }


}
