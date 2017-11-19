package com.example.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.samm_v1.EntityObjects.Destination;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */

public class mySQLDestinationProvider extends AsyncTask<Void,Void, List<Destination>>{
    Context _context;
    Activity _activity;
    GoogleMap _googleMap;

    /**
     * This gets destination value, description, order of arrival, lat and lng
     * @param context
     * @param activity
     * @param progressMessage Message that will appear in UI while processing
     * @param map Pass the main map of the app, so the asynctask will be able to pin the location of destinations
     */
    public mySQLDestinationProvider(Context context, Activity activity, String progressMessage, GoogleMap map)
    {
        this._context = context;
        this._activity = activity;
        this._googleMap = map;

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
                    listDestinations.add(new Destination(ID, Value, Description, OrderOfArrival, Direction,Lat,Lng));
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
        List<String> strDestinations = new ArrayList<>();
        ((MenuActivity)this._activity).listDestinations = destinations;
        for (Destination d:destinations)
        {
            if(!strDestinations.contains(d.Description))
                strDestinations.add(d.Description);
        }


        AutoCompleteTextView editDestination = (AutoCompleteTextView) (this._activity).findViewById(R.id.edit_destinations);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this._context, R.layout.list_item, strDestinations);
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
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus_stop));
                _googleMap.addMarker(markerOptions);
            }
        }
    }


}
