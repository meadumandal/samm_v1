package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import static com.umandalmead.samm_v1.Constants.DESTINATIONS_API_FOLDER;
import static com.umandalmead.samm_v1.Constants.DESTINATION_PREFIX;
import static com.umandalmead.samm_v1.Constants.LOG_TAG;
import static com.umandalmead.samm_v1.Constants.POINTS_API_FOLDER;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class asyncAddPoints extends AsyncTask<String, Void, String>{
    Context _context;

    LoaderDialog _Loader;
    Activity _activity;
    GoogleApiClient _googleAPIClient;
    GoogleMap _map;
    Integer _destinationIDForEdit;
    String _action;
    public static String TAG = "mead";
    private Constants _constants = new Constants();


    public asyncAddPoints(Context context, LoaderDialog loaderDialog, Activity activity, GoogleMap map, GoogleApiClient apiClient, String action, Integer destinationIDForEdit) {
        Log.i(_constants.LOG_TAG, "asyncAddPoints");
        this._context = context;
        this._Loader = loaderDialog;
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

        Log.i(_constants.LOG_TAG, "asyncAddPoints doInBackground");
        try{
            String name="";
            String lat="";
            String lng="";
            String tblRouteID = "";

            if(_action.equals("add") || _action.equals("update"))
            {
                name = params[0];
                lat = params[1];
                lng = params[2];
                tblRouteID = params[3];

            }


            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {
                String link = "";
                if(_action.equals("add"))

                    link = _constants.WEB_API_URL + DESTINATIONS_API_FOLDER + "addDestination.php?value=" +name
                            + "&lat=" + lat
                            + "&lng=" + lng
                            + "&tblRouteID=" +tblRouteID.toString()
                            + "&direction=CW";
                else if (_action.equals("update"))
                    link = _constants.WEB_API_URL + DESTINATIONS_API_FOLDER +  "updateDestination.php?id="+_destinationIDForEdit.toString()
                            + "&name=" +name
                            + "&lat=" + lat
                            + "&lng=" + lng
                            + "&tblRouteID=" +tblRouteID.toString()
                            + "&direction=CW";


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
                catch(Exception ex){

                    Helper.logger(ex);

                    return "Error encountered : "+ex.getMessage()+". Please re-try";
                }
            }
            else
            {

                return  "Looks like you're offline";
            }
        }
        catch(Exception ex)
        {
            Helper.logger(ex);
            return ex.getMessage();

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
            alertDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                   // RefreshList();
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
                new mySQLDestinationProvider(_context, this._activity, "", _map, _googleAPIClient, _Loader).execute();

            }
            else
            {
                alertDialogBuilder.setTitle("Error");
                alertDialogBuilder.setMessage(s);
            }
            alertDialogBuilder.show();

        _Loader.hide();

        }
        catch(Exception ex)
        {

            Helper.logger(ex);

        }
    }

    public void RefreshList(){

      //  ((MenuActivity)this._activity).RefreshStationPoints();
    }



}
