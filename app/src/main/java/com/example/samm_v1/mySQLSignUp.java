package com.example.samm_v1;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.samm_v1.EntityObjects.Destination;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.BroadcastReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class mySQLSignUp extends AsyncTask<String, Void, Void>{
    /**
     *
     * This updates the movement of passengers in mySQL Database
     * @param context
     * @param _activity
     * @param progressMessage Message that will appear in UI while processing
     * @param map Pass the main map of the app, so the asynctask will be able to pin the location of destinations
     */
    Context _context;
    Activity _activity;
    public mySQLSignUp(Context context, Activity activity)
    {
        this._context = context;
        this._activity = activity;

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
    protected Void doInBackground(String... params)
    {
        String username = params[0];
        String firstName = params[1];
        String lastName = params[2];
        String emailAddress = params[3];
        String data = "";
        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String link = "http://meadumandal.website/sammAPI/signUp.php?";
                data += "username=" + URLEncoder.encode(username, "UTF-8")
                        + "&firstName=" + URLEncoder.encode(firstName, "UTF-8")
                        + "&lastName=" + URLEncoder.encode(lastName, "UTF-8")
                        + "&emailAddress=" + URLEncoder.encode(emailAddress, "UTF-8");
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONObject json = new JSONObject(jsonResponse);

            }
            catch(Exception e)
            {
                Toast.makeText(this._context,e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }
        else
        {
            Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void param)
    {

    }
}
