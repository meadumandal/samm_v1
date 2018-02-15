package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class asyncAddPoints extends AsyncTask<String, Void, String>{
    Context _context;
    Activity _activity;
    public static String TAG = "mead";


    public asyncAddPoints(Context context)
    {
        Log.i(TAG, "asyncAddPoints");
        this._context = context;


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

    /**
     *

     * @return A hashmap with column names and values
     */
    @Override
    protected String doInBackground(String... params)
    {

        Log.i(TAG, "asyncAddPoints doInBackground");
        try{
            return "";

        }
        catch(Exception e)
        {
            Log.e(TAG, e.getLocalizedMessage() + e.getMessage());
            return e.getMessage();

        }


    }

    @Override
    protected void onPostExecute(String s)
    {
        Toast.makeText(_context, s, Toast.LENGTH_LONG).show();
    }





}
