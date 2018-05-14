package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.SaveCurrentDestination;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;

/**
 * Created by MeadRoseAnn on 10/8/2017.
 */

public class Helper {
    /**
     * This converts a jsonString to HashMap
     * @param jsonString
     * @return
     * @throws JSONException
     */

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private String _smsMessage;
    private String _smsPhoneNo;

    public MenuActivity _activity;
    public Context _context;
    public SessionManager _sessionManager;

    public Helper(Activity activity, Context context)
    {
        this._activity = (MenuActivity) activity;
        this._context = context;
        this._sessionManager = new SessionManager(_context);
    }
    public Helper()
    {

    }

    public HashMap<String, Object> jsonToMap(String jsonString) throws JSONException
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        JSONObject jObject = new JSONObject(jsonString);
        Iterator<?> keys = jObject.keys();
        while(keys.hasNext())
        {
            String key = (String)keys.next();
            Object value = jObject.get(key);
            map.put(key,value);
        }
        return map;
    }

    /**
     * This checks if the connection service (WIFI or Mobile Data) is available.
     * This also checks if the device has internet connection by trying to get the IPAddress of google.com
     * @param context
     * @return
     */
    public boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityMngr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityMngr.getActiveNetworkInfo();
        if (activeNetwork != null && isInternetAvailable())
            return true;
        else
            return false;
    }

    /**
     * This checks if the device has internet connection by trying to get the IPAddress of google.com
     * @return true if there is an internet connection. Otherwise, false
     */
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception ex) {
            Helper.logger(ex);
            return false;
        }
    }

    /**
     * This method checks if the device has google play services installed before building Google API Client
     * @return true if google play services is installed
     */
    public Boolean isGooglePlayInstalled(Context context)
    {
        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return(resp == ConnectionResult.SUCCESS);
    }

    public void showNoInternetPrompt(Activity activity)
    {
        NoInternetDialog dialog=new NoInternetDialog(activity);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
    }
    public double getDistanceFromLatLonInKm(LatLng First, LatLng Second) {
        double R = 6371; // Radius of the earth in km
        double dLat = ToRadians(Second.latitude-First.latitude);  // deg2rad below
        double dLon = ToRadians(Second.longitude-First.longitude);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(ToRadians(First.latitude)) * Math.cos(ToRadians(Second.latitude)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }

    public double ToRadians(double deg) {
        return deg * (Math.PI/180);
    }
    public double bearingBetweenLocations(Location PrevLoc, Location CurrLoc) {

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

    /**
     * This method gets the nearest "PICK-UP" points based on the chosen DROP-OFF point
     * @param dropOffPoint This is the chosen drop-off point
     */
    public void FindNearestPickUpPoints(Terminal dropOffPoint) {

        if(MenuActivity.isOnline())
        {
            Terminal chosenTerminal =dropOffPoint;
            saveDestination(chosenTerminal.Value);

            (this._activity)._possiblePickUpPointList = new ArrayList<>();
            for(Terminal terminal : (this._activity)._terminalList)
            {
                if (terminal.Direction.equals(chosenTerminal.Direction))
                {
                    if(terminal.OrderOfArrival < chosenTerminal.OrderOfArrival)
                        (this._activity)._possiblePickUpPointList.add(terminal);
                }
            }
            new AnalyzeForBestRoutes(_context, _activity,
                    (this._activity)._googleMap, (this._activity)._userCurrentLoc,
                    (this._activity).getSupportFragmentManager(),
                    (this._activity)._possiblePickUpPointList, chosenTerminal)
                    .execute();
        }
        else
        {
            showNoInternetPrompt(this._activity);
        }

    }
    private void saveDestination(String destinationValue)
    {
        final HashMap<String, Object> currentDestination = new HashMap<>();
        currentDestination.put("currentDestination", destinationValue);

        (this._activity)._usersDBRef
                .child(_sessionManager.getUsername())
                .child("currentDestination")
                .addListenerForSingleValueEvent(
                        new SaveCurrentDestination(this._activity, this._context, currentDestination));
    }
    public int dpToPx(float dp) {
        DisplayMetrics metrics = _context.getResources().getDisplayMetrics();
        float fpixels = metrics.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        return pixels;
    }
    public static void logger(Exception ex)
    {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        Log.e(LOG_TAG, "StackTrace: " + sw.toString() + " | Message: " + ex.getMessage());
    }

}
