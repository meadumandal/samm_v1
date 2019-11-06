package com.evolve.evx.Modules.ManageLines;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.evolve.evx.Constants;
import com.evolve.evx.Helper;
import com.evolve.evx.InfoDialog;
import com.evolve.evx.LoaderDialog;
import com.evolve.evx.MenuActivity;
import com.evolve.evx.R;
import com.evolve.evx.SessionManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static com.evolve.evx.MenuActivity._manageStationsFragment;

/**
 * Created by MeadRoseAnn on 7/22/2018.
 */

public class mySQLDeleteLine extends AsyncTask<String, Void, Void> {
    /**
     *
     * Describe what this method does
     * @param context
     * @param _activity
     * @param progressMessage Message that will appear in UI while processing
     * @param map
     */
    Context _context;
    Activity _activity;
    LoaderDialog _LoaderDialog;
    AlertDialog.Builder _alertDialogBuilder;

    SessionManager _sessionManager;
    private Constants _constants = new Constants();
    public Helper _helper;
    Boolean _isSuccessful = false;
    ManageLinesFragment _manageLinesFragment;
    FragmentManager _fragmentManager;
    String _updatedLinesDataInJSONFormat, _updatedStationsDataInJSONFormat;
    String _promptMessage;


    public mySQLDeleteLine(Context context, Activity activity, LoaderDialog loaderDialog, AlertDialog.Builder alertDialog,
                           ManageLinesFragment manageLinesFragment, FragmentManager fragmentManager)
    {
        this._context = context;
        this._activity = activity;
        this._LoaderDialog = loaderDialog;
        this._alertDialogBuilder = alertDialog;
        _sessionManager = new SessionManager(_context);
        this._manageLinesFragment = manageLinesFragment;
        this._fragmentManager = fragmentManager;
        this._helper = new Helper(activity, context);
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
            String lineID = params[0];
            if (_helper.isConnectedToInternet(this._context))
            {

                try{
                    String link = "";
                    link = _constants.WEB_API_URL + _constants.LINE_API_FOLDER + "deleteLine.php?lineID="+lineID;
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String jsonResponse = reader.readLine();
                    JSONObject json;

                    try
                    {
                        json = new JSONObject(jsonResponse);
                        if(json.getBoolean("status") == true)
                        {
                            _isSuccessful = true;
                            _promptMessage = MenuActivity._GlobalResource.getString(R.string.info_delete_line_success);
                            _updatedLinesDataInJSONFormat = json.getString("lineList");
                            _updatedStationsDataInJSONFormat = json.getString("stationList");


                        }
                        else
                        {
                            _isSuccessful = false;
                            _promptMessage = json.getString("message") +  "\n";
                        }
                    }
                    catch(Exception ex){
                        _isSuccessful = false;
                        Helper.logger(ex,true);

                        _promptMessage = MenuActivity._GlobalResource.getString(R.string.error_encountered_with_colon)+ex.getMessage()+". Please re-try";
                    }
                }
                catch(Exception ex)
                {
                    _isSuccessful = false;
                    Helper.logger(ex,true);
                    _promptMessage = MenuActivity._GlobalResource.getString(R.string.error_encountered_with_colon)+ex.getMessage()+". Please re-try";
                }
            }
            else
            {
                _isSuccessful = false;
                Toast.makeText(this._context, MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline), Toast.LENGTH_LONG).show();
                _LoaderDialog.hide();
                _promptMessage = MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline);

            }

        }
        catch(Exception ex)
        {
            _isSuccessful = false;
            Helper.logger(ex,true);
            _promptMessage =  MenuActivity._GlobalResource.getString(R.string.error_encountered_with_colon)+ex.getMessage()+". Please re-try";
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

            ManageLinesFragment._swipeRefreshLines.setRefreshing(true);
            _helper.UpdateLinesData(_manageLinesFragment, _updatedLinesDataInJSONFormat);
            _helper.UpdateStationsData(_manageStationsFragment, _updatedStationsDataInJSONFormat);
            _helper.UpdateStationMarkersOnTheMap(MenuActivity._terminalList, MenuActivity._googleMap, MenuActivity._googleAPI);
            ManageLinesFragment._swipeRefreshLines.setRefreshing(false);
        }


    }
}