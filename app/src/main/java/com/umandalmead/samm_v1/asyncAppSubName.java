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
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class asyncAppSubName extends AsyncTask<Void, Void, String>{
    Context _context;
    Activity _activity;
    ProgressDialog _progDialog;
    AlertDialog.Builder _alertDialogBuilder;
    String progressMessage;
    public static String TAG="mead";
    String _putData;
    GPS _dataModel;
    DialogFragment _dialogFragment;


    public asyncAppSubName(Context context, ProgressDialog progDialog, Activity activity, GPS dataModel)
    {
        Log.i(TAG, "asyncUpdateTraccarGPS");
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

        Log.i(TAG, "asyncUpdateTraccarGPS doInBackground");
        try{
            String returnString;
            Integer deviceId = 0;

            //api parameters
            String id=_dataModel.getID().toString();
            String uniqueId = _dataModel.getGPSIMEI().toString();
            String name = _dataModel.getGPSName();
            String phone = _dataModel.getGPSPhone();
            String networkProvider = _dataModel.getGPSNetworkProvider();


            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {
                String link="http://meadumandal.website/sammAPI/updateDevice.php?id="+id+"&name="+name+"&uniqueId="+uniqueId
                        +"&phone="+phone+"&model="+networkProvider;
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
                        returnString= "Error encountered upon updating GPS. Please re-try";
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
        catch(Exception e)
        {
            Log.e(TAG, e.getLocalizedMessage() + e.getMessage());
            return e.getMessage();

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
                alertDialogBuilder.setMessage("Successfully updated GPS!");
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
        catch(Exception e)
        {

            Log.e(TAG, e.getMessage());

        }

    }





}
