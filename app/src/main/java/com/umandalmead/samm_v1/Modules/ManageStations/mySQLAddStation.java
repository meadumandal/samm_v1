package com.umandalmead.samm_v1.Modules.ManageStations;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;

import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.umandalmead.samm_v1.Constants;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.ErrorDialog;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.InfoDialog;
import com.umandalmead.samm_v1.LoaderDialog;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import static com.umandalmead.samm_v1.Constants.DESTINATIONS_API_FOLDER;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class mySQLAddStation extends AsyncTask<String, Void, Void>{
    Context _context;

    LoaderDialog _Loader;
    Activity _activity;
    GoogleApiClient _googleAPIClient;
    GoogleMap _map;
    Integer _destinationIDForEdit;
    String _action;
    public static String TAG = "mead";
    private Constants _constants = new Constants();
    ManageStationsFragment _manageStationsFragment;
    Integer _routeID;
    Helper _helper;
    String _promptMessage, _updatedStationsDataInJSONFormat;
    Boolean _isSuccessful = false;


    public mySQLAddStation(Context context,
                           LoaderDialog loaderDialog,
                           Activity activity,
                           GoogleMap map,
                           GoogleApiClient apiClient,
                           Integer destinationIDForEdit,
                           ManageStationsFragment manageStationsFragment) {
        Log.i(_constants.LOG_TAG, "mySQLAddStation");
        this._context = context;
        this._Loader = loaderDialog;
        this._activity = activity;
        this._map = map;
        this._googleAPIClient = apiClient;
        this._destinationIDForEdit = destinationIDForEdit;
        this._manageStationsFragment = manageStationsFragment;
        this._helper = new Helper(activity,context);
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
    protected Void doInBackground(String... params)
    {

        Log.i(_constants.LOG_TAG, "mySQLAddStation doInBackground");
        try{
            String name="";
            String lat="";
            String lng="";
            String tblRouteID = "";
            _routeID = 0;
            name = params[0];
            lat = params[1];
            lng = params[2];
            tblRouteID = params[3];
            _routeID = Integer.parseInt(tblRouteID);


            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {
                String link = "";
                link = _constants.WEB_API_URL + DESTINATIONS_API_FOLDER + "addDestination.php?value=" +name
                        + "&lat=" + lat
                        + "&lng=" + lng
                        + "&tblRouteID=" +tblRouteID.toString()
                        + "&direction=CW";

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONObject json = new JSONObject(jsonResponse);
                if(json.getBoolean("status") == true)
                {
                    _isSuccessful = true;
                    _promptMessage = MenuActivity._GlobalResource.getString(R.string.dialog_point_added);
                    _updatedStationsDataInJSONFormat = json.getString("stationList");

                }
                else
                {
                    _isSuccessful = false;
                    _promptMessage = json.getString("message") +  "\n";
                }
            }
            else
            {

                _promptMessage = MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline);
            }
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
            _promptMessage = ex.getMessage();

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v)
    {
        _Loader.hide();
        InfoDialog dialog=new InfoDialog(this._activity, _promptMessage);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        if(_isSuccessful)
        {
            //Station List View has no SwipeRefreshLayout
            _helper.UpdateStationsData(_manageStationsFragment, _updatedStationsDataInJSONFormat);
            _helper.UpdateStationMarkersOnTheMap(MenuActivity._terminalList, MenuActivity._googleMap, MenuActivity._googleAPI);
        }
    }

    public void RefreshList(){

      //  ((MenuActivity)this._activity).RefreshStationPoints();
    }



}
