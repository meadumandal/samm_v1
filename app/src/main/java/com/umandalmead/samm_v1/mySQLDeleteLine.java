package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static com.umandalmead.samm_v1.MenuActivity._googleAPI;
import static com.umandalmead.samm_v1.MenuActivity._googleMap;

/**
 * Created by MeadRoseAnn on 7/22/2018.
 */

public class mySQLDeleteLine extends AsyncTask<String, Void, String> {
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
    public Helper _helper = new Helper();

    Boolean _isSuccessful;

    public mySQLDeleteLine(Context context, Activity activity, LoaderDialog loaderDialog, AlertDialog.Builder alertDialog)
    {
        this._context = context;
        this._activity = activity;
        this._LoaderDialog = loaderDialog;
        this._alertDialogBuilder = alertDialog;
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
    protected String doInBackground(String... params)
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
                        if ((Boolean)json.get("status")==true)
                        {

                            _isSuccessful = true;
                            return json.get("message").toString();
                        }
                        else
                        {
                            _isSuccessful = false;
                            return json.get("message").toString();
                        }
                    }
                    catch(Exception ex){
                        _isSuccessful = false;
                        Helper.logger(ex,true);

                        return MenuActivity._GlobalResource.getString(R.string.error_encountered_with_colon)+ex.getMessage()+". Please re-try";
                    }
                }
                catch(Exception ex)
                {
                    _isSuccessful = false;
                    Helper.logger(ex,true);
                    return MenuActivity._GlobalResource.getString(R.string.error_encountered_with_colon)+ex.getMessage()+". Please re-try";
                }
            }
            else
            {
                _isSuccessful = false;
                Toast.makeText(this._context, MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline), Toast.LENGTH_LONG).show();
                _LoaderDialog.hide();
                return MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline);

            }
        }
        catch(Exception ex)
        {
            _isSuccessful = false;
            Helper.logger(ex,true);
            return MenuActivity._GlobalResource.getString(R.string.error_encountered_with_colon)+ex.getMessage()+". Please re-try";
        }
    }

    @Override
    protected void onPostExecute(String param)
    {
        try
        {
            _LoaderDialog.hide();
            InfoDialog dialog=new InfoDialog(this._activity, _isSuccessful==true? "Successfully deleted" : "Error encountered");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            ManageLinesActivity._swipeRefreshLines.setRefreshing(true);
            new mySQLLinesDataProvider(_activity, ManageLinesActivity._lineListView).execute();



        }
        catch(Exception ex)
        {

            Helper.logger(ex,true);

        }


    }
}