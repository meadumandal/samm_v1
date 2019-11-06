package com.evolve.evx.Modules.ManageStations;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.widget.Toast;

import com.evolve.evx.Constants;
import com.evolve.evx.Helper;
import com.evolve.evx.InfoDialog;
import com.evolve.evx.LoaderDialog;
import com.evolve.evx.MenuActivity;
import com.evolve.evx.NonScrollListView;
import com.evolve.evx.R;
import com.evolve.evx.SessionManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static com.evolve.evx.MenuActivity._GlobalResource;

/**
 * Created by MeadRoseAnn on 11/3/2018.
 */

public class mySQLDeleteStation extends AsyncTask<String, Void, Void> {
    /**
     *
     * This updates the movement of passengers in mySQL Database
     * @param context
     * @param _activity
     * @param progressMessage Message that will appear in UI while processing
     * @param map Pass the main map of the app, so the asynctask will be able to pin the location of destinations
     */
    Context _context;
    Activity _activity;
    ProgressDialog _progressDialog;
    LoaderDialog _LoaderDialog;
    AlertDialog.Builder _alertDialogBuilder;
    String _promptMessage;
    SessionManager _sessionManager;
    private Constants _constants = new Constants();
    public Helper _helper;
    public String _typeOfItemToDelete;
    Boolean _isSuccessful = false;
    NonScrollListView _routesListView;
    ManageStationsFragment _manageStationsFragment;
    String _updatedStationsDataInJSONFormat;



    public mySQLDeleteStation(Context context,
                              Activity activity,
                              LoaderDialog loaderDialog,
                              AlertDialog.Builder alertDialog,
                              ManageStationsFragment manageStationsFragment)
    {
        this._context = context;
        this._activity = activity;
        this._LoaderDialog = loaderDialog;
        this._alertDialogBuilder = alertDialog;
        this._helper = new Helper(_activity, _context);
        this._manageStationsFragment = manageStationsFragment;
        _sessionManager = new SessionManager(_context);
    }

    @Override
    protected void onPreExecute()
    {
        _LoaderDialog.show();
        try
        {
            super.onPreExecute();
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }


    }
    @Override
    protected Void doInBackground(String... params)
    {
        try
        {
            String destinationID = params[0];
            if (_helper.isConnectedToInternet(this._context))
            {

                String link = _constants.WEB_API_URL + _constants.DESTINATIONS_API_FOLDER + _constants.DESTINATIONS_API_DELETE_FILE_WITH_PENDING_QUERYSTRING+"destinationid="+destinationID;

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();

                JSONObject json = new JSONObject(jsonResponse);
                if(json.getBoolean("status") == true)
                {
                    _isSuccessful = true;
                    _promptMessage = MenuActivity._GlobalResource.getString(R.string.dialog_point_deleted);
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
                String STR_DefaultErrorMessage = _GlobalResource.getString(R.string.Error_looks_like_your_offline);
                _isSuccessful = false;
                Toast.makeText(this._context, STR_DefaultErrorMessage, Toast.LENGTH_LONG).show();
                _LoaderDialog.hide();
                _promptMessage = STR_DefaultErrorMessage;

            }
        }
        catch(Exception ex)
        {
            _isSuccessful = false;
            Helper.logger(ex,true);
            _promptMessage = MenuActivity._GlobalResource.getString(R.string.error_encountered_with_colon)+ex.getMessage()+". Please re-try";
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v)
    {

        _LoaderDialog.hide();
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
}
