package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.gson.Gson;
import com.umandalmead.samm_v1.EntityObjects.GPS;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class asyncDeleteTraccarGPS extends AsyncTask<Void, Void, String>{
    Context _context;
    Activity _activity;
    ProgressDialog _progDialog;
    AlertDialog.Builder _alertDialogBuilder;
    String progressMessage;
    public static String TAG="mead";
    String _putData;
    GPS _dataModel;
    DialogFragment _dialogFragment;


    public asyncDeleteTraccarGPS(Context context, ProgressDialog progDialog, Activity activity, GPS dataModel)
    {
        Log.i(TAG, "asyncDeleteTraccarGPS");
        this._context = context;
        this._progDialog = progDialog;
        this._activity = activity;
        this._dataModel = dataModel;



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
    protected String doInBackground(Void... voids)
    {

        Log.i(TAG, "asyncDeleteTraccarGPS doInBackground");
        try{


            //api parameters
            String id=_dataModel.getID().toString();

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {
                String link="http://meadumandal.website/sammAPI/deleteDevice.php?id="+id;
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONObject json;


            }
            else
            {
                _progDialog.dismiss();
                return "Looks like you're offline";
            }
            _progDialog.dismiss();
            return "GPS deleted!";
        }
        catch(Exception e)
        {
            _progDialog.dismiss();
            Log.e(TAG, e.getLocalizedMessage() + e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String returnMessage)
    {
        try
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this._activity);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            alertDialogBuilder.setTitle("Success");
            alertDialogBuilder.setMessage("Successfully deleted GPS!");
            alertDialogBuilder.show();

        }
        catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
    }





}
