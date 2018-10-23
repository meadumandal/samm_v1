package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class asyncAddTraccarGPS extends AsyncTask<String, Void, String>{
    Context _context;
    Activity _activity;
    LoaderDialog _LoaderDialog;
    String progressMessage, _GPSMobileNumber;

    private Constants _constants = new Constants();
    MenuActivity.AddGPSDialog _dialog;


    public asyncAddTraccarGPS(Context context, LoaderDialog loaderDialog, Activity activity, MenuActivity.AddGPSDialog dialog)
    {
        Log.i(_constants.LOG_TAG, "asyncAddTraccarGPS");
        this._context = context;
        this._LoaderDialog = loaderDialog;
        this._activity = activity;
        this._dialog = dialog;
    }
    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            _LoaderDialog.show();

        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }
    }

    /**
     *

     * @return A hashmap with column names and values
     */
    @Override
    protected String doInBackground(String... params)
    {
        Log.i(_constants.LOG_TAG, "asyncAddTraccarGPS doInBackground");
        try{
            String name = params[0];
            String uniqueId = params[1];
            String phoneNo = params[2];
            String network = params[3];
            String plateNumber = params[4];
            String tblRoutesID = params[5];
            String tblUsersID = params[6];
            String returnString  ="";
            Integer deviceId = 0;
            _GPSMobileNumber = phoneNo;

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {


                String link = _constants.WEB_API_URL + _constants.DEVICES_API_FOLDER + "createDevices.php?name="+name+"&uniqueId="+uniqueId
                        +"&phoneNo="+phoneNo + "&model=" + network+ "&plateNumber=" + plateNumber + "&tblRoutesID=" + tblRoutesID + "&tblUsersID=" + tblUsersID;
                URL url = new URL(link);
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
                    _LoaderDialog.dismiss();
                    Helper.logger(ex,true);
                    returnString= "Error encountered upon adding GPS. Please re-try";
                }
            }
            else
            {
                _LoaderDialog.dismiss();
                returnString=  "Looks like you're offline";
            }
            return returnString;
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
            return ex.getMessage();

        }

    }

    @Override
    protected void onPostExecute(String returnMessage)
    {
        try
        {
            if(returnMessage.equals("Success"))
            {
                _dialog.dismiss();
                _LoaderDialog = new LoaderDialog(_activity,"","Starting to configure GPS");
                ((MenuActivity)_activity).sendSMSMessage(_constants.SMS_BEGIN, _GPSMobileNumber);

            }
            else
            {
                AlertDialog.Builder errorAlertDialogBuilder = new AlertDialog.Builder(this._activity);
                errorAlertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                errorAlertDialogBuilder.setTitle("Error encountered");
                errorAlertDialogBuilder.setMessage(returnMessage);
                errorAlertDialogBuilder.show();
                _LoaderDialog.dismiss();
            }
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }
    }
}