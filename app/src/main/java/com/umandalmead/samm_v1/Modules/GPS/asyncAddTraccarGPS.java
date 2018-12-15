package com.umandalmead.samm_v1.Modules.GPS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.umandalmead.samm_v1.Constants;
import com.umandalmead.samm_v1.ErrorDialog;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.LoaderDialog;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.ViewGPSFragment;

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
    String progressMessage, _GPSMobileNumber, _GPSNetwork;
    ViewGPSFragment _viewGPSFragment;
    AddGPSDialogFragment _dialog;

    private Constants _constants = new Constants();



    public asyncAddTraccarGPS(Context context, LoaderDialog loaderDialog, Activity activity, AddGPSDialogFragment dialog, ViewGPSFragment viewGPSFragment)
    {
        Log.i(_constants.LOG_TAG, "asyncAddTraccarGPS");
        this._context = context;
        this._LoaderDialog = loaderDialog;
        this._activity = activity;
        this._dialog = dialog;
        this._viewGPSFragment = viewGPSFragment;

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
            String tblLineID = params[7];
            String returnString  ="";
            Integer deviceId = 0;
            _GPSMobileNumber = phoneNo;
            _GPSNetwork = network;

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {


                String link = _constants.WEB_API_URL + _constants.DEVICES_API_FOLDER
                        + "createDevices.php?name="+name
                        +"&uniqueId="+uniqueId
                        +"&phoneNo="+phoneNo
                        + "&model=" + network
                        + "&plateNumber=" + plateNumber
                        + "&tblRoutesID=" + tblRoutesID
                        + "&tblUsersID=" + tblUsersID
                        + "&tblLineID="+tblLineID;
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
                    returnString= MenuActivity._GlobalResource.getString(R.string.error_encountered_upon_adding_gps);
                }
            }
            else
            {
                _LoaderDialog.dismiss();
                returnString=  MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline);
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
                _LoaderDialog.setMessage("Starting to configure GPS");
                String apn;
                if(_GPSNetwork.toLowerCase().equals("globe"))
                    apn = "http.globe.com.ph";
                else
                    apn = "internet";
                _viewGPSFragment._smsAPN = "apn" + Constants.GPS_PASSWORD + " " + apn;
                _viewGPSFragment.sendSMSMessage(_constants.SMS_BEGIN, _GPSMobileNumber);
                _viewGPSFragment.refreshGPSListView();

            }
            else
            {
                ErrorDialog errorDialog = new ErrorDialog(_activity, returnMessage);
                errorDialog.show();
                _LoaderDialog.dismiss();
            }
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }
    }
}