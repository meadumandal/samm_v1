package com.example.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;

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

        } catch (Exception e) {
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


}
