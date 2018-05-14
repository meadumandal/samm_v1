package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;

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

    ProgressDialog _progDialog;
    Activity _activity;
    GoogleApiClient _googleAPIClient;
    GoogleMap _map;
    Integer _destinationIDForEdit;
    String _action;
    public static String TAG = "mead";
    private Constants _constants = new Constants();


    public asyncAddPoints(Context context, ProgressDialog progDialog, Activity activity, GoogleMap map, GoogleApiClient apiClient, String action, Integer destinationIDForEdit) {
        Log.i(_constants.LOG_TAG, "asyncAddPoints");
        this._context = context;
        this._progDialog = progDialog;
        this._activity = activity;
        this._map = map;
        this._googleAPIClient = apiClient;
        this._action = action.toLowerCase();
        this._destinationIDForEdit = destinationIDForEdit;
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

        Log.i(_constants.LOG_TAG, "asyncAddPoints doInBackground");
        try{
            String name="";
            String lat="";
            String lng="";
            String preposition="";
            String terminalreferenceid="";
            if(_action.equals("add") || _action.equals("update"))
            {
                name = params[0];
                lat = params[1];
                lng = params[2];
                preposition = params[3];
                terminalreferenceid = params[4];
            }


            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {
                String link = "";
                if(_action.equals("add"))

                    link = _constants.WEB_API_URL + "_AddPointFloatingButton.php?name=" +name
                            + "&lat=" + lat
                            + "&lng=" + lng
                            + "&preposition=" + preposition
                            + "&terminalreferenceid="+terminalreferenceid;
                else if (_action.equals("update"))
                    link = _constants.WEB_API_URL + "updatePoint.php?id="+_destinationIDForEdit.toString()
                            + "&name=" +name
                            + "&lat=" + lat
                            + "&lng=" + lng
                            + "&preposition=" + preposition
                            + "&terminalreferenceid="+terminalreferenceid;
                else
                    link = _constants.WEB_API_URL + "removePoint.php?destinationid="+_destinationIDForEdit.toString();
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONObject json;

                try
                {
                    json = new JSONObject(jsonResponse);
                    if ((Boolean)json.get("status")==true)
                    {

                        return "Success";
                    }
                    else
                    {

                        return json.get("msg").toString();
                    }
                }
                catch(Exception e){


                    Log.e(_constants.LOG_TAG, e.getLocalizedMessage() +":"+ e.getMessage());

                    return "Error encountered : "+e.getMessage()+". Please re-try";
                }
            }
            else
            {

                return  "Looks like you're offline";
            }
        }
        catch(Exception e)
        {
            Log.e(_constants.LOG_TAG, e.getLocalizedMessage() + e.getMessage());
            return e.getMessage();

        }

    }

    @Override
    protected void onPostExecute(String s)
    {
        try
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this._activity);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            if(s.equals("Success"))
            {
                alertDialogBuilder.setTitle("Success");
                if(_action.equals("add"))
                    alertDialogBuilder.setMessage("We have successfully added the point on the map!");
                else if(_action.equals("update"))
                    alertDialogBuilder.setMessage("We have successfully updated the point on the map!");
                else
                    alertDialogBuilder.setMessage("We have successfully deleted the point from the map!");
                new mySQLDestinationProvider(_context, this._activity, "", _map, _googleAPIClient, _progDialog).execute();
            }
            else
            {
                alertDialogBuilder.setTitle("Error");
                alertDialogBuilder.setMessage(s);
            }
            alertDialogBuilder.show();
//        Toast.makeText(_context, s, Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {

            Log.e(_constants.LOG_TAG, e.getMessage());

        }
    }





}
