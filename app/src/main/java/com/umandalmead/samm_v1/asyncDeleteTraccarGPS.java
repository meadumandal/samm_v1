package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.umandalmead.samm_v1.EntityObjects.GPS;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import static com.squareup.okhttp.internal.Internal.logger;
import static com.umandalmead.samm_v1.Constants.LOG_TAG;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class asyncDeleteTraccarGPS extends AsyncTask<Void, Void, String>{
    Context _context;
    Activity _activity;
    LoaderDialog _LoaderDialog;
    AlertDialog.Builder _alertDialogBuilder;
    String progressMessage;
    public static String TAG="mead";
    String _putData;
    GPS _dataModel;
    DialogFragment _dialogFragment;
    private Constants _constants = new Constants();
    SwipeRefreshLayout _swipeRefreshLayout;
    NonScrollListView _gpsListView;


    public asyncDeleteTraccarGPS(Context context, LoaderDialog loaderDialog, Activity activity, EditGPSDialogFragment dialog, GPS dataModel, SwipeRefreshLayout swipeRefreshLayout, NonScrollListView gpsListView)
    {
        Log.i(TAG, "asyncDeleteTraccarGPS");
        this._context = context;
        this._LoaderDialog = loaderDialog;
        this._activity = activity;
        this._dataModel = dataModel;
        this._swipeRefreshLayout = swipeRefreshLayout;
        this._gpsListView = gpsListView;
        this._dialogFragment = dialog;

    }
    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();

        }
        catch(Exception ex)
        {
            Helper.logger(ex);

        }


    }

    /**
     *

     * @return A hashmap with column names and values
     */
    @Override
    protected String doInBackground(Void... voids)
    {
        String returnString;
        Log.i(TAG, "asyncDeleteTraccarGPS doInBackground");
        try{

            //api parameters
            String id=_dataModel.getID().toString();

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {
                URL url = new URL(_constants.WEB_API_URL + _constants.DEVICES_API_FOLDER+ "deleteDevice.php?id="+id);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONObject json;

                try
                {
                    json = new JSONObject(jsonResponse);
                    Boolean status = json.getBoolean("status");
                    String message = json.getString("message");
                    if(status)
                    {
                        returnString= "Success";
                    }
                    else
                    {
                        returnString= message;
                    }
                }
                catch(Exception ex){

                    Helper.logger(ex);
                    returnString= "Error encountered upon deleting GPS. Please re-try";
                }


            }
            else
            {

                returnString =  "Looks like you're offline";
            }

        }
        catch(Exception ex)
        {

            Helper.logger(ex);
            returnString =  ex.getMessage();
        }
        _LoaderDialog.dismiss();
        return returnString;
    }

    @Override
    protected void onPostExecute(String returnMessage)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this._activity);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        try
        {
            String message = returnMessage;

            if(message.equals("Success"))
            {
                alertDialogBuilder.setTitle("Success");
                alertDialogBuilder.setMessage("Successfully deleted GPS!");
                _dialogFragment.dismiss();
                _LoaderDialog.dismiss();
                _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        _swipeRefreshLayout.setRefreshing(true);
                        FragmentManager fm = _activity.getFragmentManager();
                        new asyncGetGPSFromTraccar(_activity, _LoaderDialog, _gpsListView, fm,_swipeRefreshLayout).execute();

                    }
                });
                _swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        _swipeRefreshLayout.setRefreshing(true);
                        FragmentManager fm = _activity.getFragmentManager();
                        new asyncGetGPSFromTraccar(_activity, _LoaderDialog, _gpsListView, fm,_swipeRefreshLayout).execute();
                    }
                });
//              new mySQLSignUp(_context, _activity).execute(gpsname, "SAMM", deviceId, "sammdriver@yahoo.com");
            }
            else
            {
                alertDialogBuilder.setTitle("Error");
                alertDialogBuilder.setMessage(message);
            }
            alertDialogBuilder.show();
//        Toast.makeText(_context, s, Toast.LENGTH_LONG).show();
        }
        catch(Exception ex)
        {
            alertDialogBuilder.setTitle("Error");
            alertDialogBuilder.setMessage(ex.getMessage());
            Helper.logger(ex);

        }

    }





}
