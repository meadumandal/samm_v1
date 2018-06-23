package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
    ProgressDialog _progDialog;
    AlertDialog.Builder _alertDialogBuilder;
    String progressMessage;
    public static String TAG="mead";
    String _putData;
    GPS _dataModel;
    DialogFragment _dialogFragment;
    private Constants _constants = new Constants();


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

        Log.i(TAG, "asyncDeleteTraccarGPS doInBackground");
        try{


            //api parameters
            String id=_dataModel.getID().toString();

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {
                URL url = new URL(_constants.WEB_API_URL + "deleteDevice.php?id="+id);
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
        catch(Exception ex)
        {
            _progDialog.dismiss();
            Helper.logger(ex);
            return ex.getMessage();
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
        catch(Exception ex)
        {
            Helper.logger(ex);
        }
    }





}