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


public class addGPStoTraccar extends AsyncTask<String, Void, String>{
    Context _context;
    Activity _activity;
    ProgressDialog _progDialog;
    AlertDialog.Builder _alertDialogBuilder;
    String progressMessage;
    public static String TAG="mead";
    JSONObject postData;


    public addGPStoTraccar(Context context, ProgressDialog progDialog)
    {
        Log.i(TAG, "addGPStoTraccar");
        this._context = context;
        this._progDialog = progDialog;


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

        Log.i(TAG, "addGPStoTraccar doInBackground");
        try{
            String name = params[0];
            String uniqueId = params[1];

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {

                String link = "http://meadumandal.website/sammAPI/createDevices.php?name="+name+"&uniqueId="+uniqueId;
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONObject json;

                try
                {
                    json = new JSONObject(jsonResponse);
                    if (Integer.parseInt(json.get("id").toString())>0)
                    {
                        _progDialog.dismiss();
                        return "Success";
                    }
                    else
                    {
                        _progDialog.dismiss();
                        return "Error encountered upon adding GPS. Please re-try";
                    }
                }
                catch(Exception e){
                    _progDialog.dismiss();
                    String errorMessage = jsonResponse.toString();
                    if(errorMessage.substring(0,15).equals("Duplicate entry"))
                    {
                        errorMessage="GPS IMEI already exists.";
                    }
                    else if(errorMessage.substring(0,15).equals("User device lim"))
                    {
                        errorMessage="Server exceeded maximum number of GPS!";
                    }
                    Log.e(TAG, e.getLocalizedMessage() +":"+ e.getMessage());
                    _progDialog.dismiss();
                    return "Error encountered upon adding GPS: "+errorMessage+". Please re-try";
                }
            }
            else
            {
                _progDialog.dismiss();
                return  "Looks like you're offline";
            }
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
