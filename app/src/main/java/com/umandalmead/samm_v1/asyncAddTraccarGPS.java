package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class asyncAddTraccarGPS extends AsyncTask<String, Void, String>{
    Context _context;
    Activity _activity;
    ProgressDialog _progDialog;
    AlertDialog.Builder _alertDialogBuilder;
    String progressMessage;
    public static String TAG="mead";
    JSONObject postData;
    private Constants _constants = new Constants();


    public asyncAddTraccarGPS(Context context, ProgressDialog progDialog, Activity activity)
    {
        Log.i(TAG, "asyncAddTraccarGPS");
        this._context = context;
        this._progDialog = progDialog;
        this._activity = activity;


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
    protected String doInBackground(String... params)
    {

        Log.i(TAG, "asyncAddTraccarGPS doInBackground");
        try{
            String name = params[0];
            String uniqueId = params[1];
            String phoneNo = params[2];
            String returnString  ="";
            Integer deviceId = 0;

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {

                String link = _constants.WEB_API_URL + _constants.DEVICES_API_FOLDER + "createDevices.php?name="+name+"&uniqueId="+uniqueId+"&phoneNo="+phoneNo;
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONObject json;

                try
                {
                    json = new JSONObject(jsonResponse);
                    deviceId=Integer.parseInt(json.get("id").toString());
                    if (deviceId>0)
                    {

                        _progDialog.dismiss();
                        returnString= "Success";
                    }
                    else
                    {
                        _progDialog.dismiss();
                        returnString= "Error encountered upon adding GPS. Please re-try";
                    }
                }
                catch(Exception ex){
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
                    else
                    {
                        Helper.logger(ex);
                        errorMessage = ex.getMessage();
                    }

                    _progDialog.dismiss();
                    returnString= "Error encountered upon adding GPS: "+errorMessage+". Please re-try";
                }
            }
            else
            {
                _progDialog.dismiss();
                returnString=  "Looks like you're offline";
            }
            return returnString + "/" + name + "/" +deviceId.toString();
        }
        catch(Exception ex)
        {
            Helper.logger(ex);
            return ex.getMessage();

        }

    }

    @Override
    protected void onPostExecute(String returnMessage)
    {
        try
        {
            String[] parts = returnMessage.split("/");
            String message = parts[0];
            String gpsname = parts[1];
            String deviceId = parts[2];
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this._activity);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            if(message.equals("Success"))
            {

                alertDialogBuilder.setTitle("Success");
                alertDialogBuilder.setMessage("Successfully added GPS! It might take up to 10minutes before the GPS appears on the map.");
                new mySQLSignUp(_context, _activity).execute(gpsname, "SAMM", deviceId, _constants.DRIVER_EMAILADDRESS);
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

            Helper.logger(ex);

        }

    }





}
