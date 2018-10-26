package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.umandalmead.samm_v1.Adapters.RouteViewCustomAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.umandalmead.samm_v1.MenuActivity._GlobalResource;
import static com.umandalmead.samm_v1.MenuActivity._googleAPI;
import static com.umandalmead.samm_v1.MenuActivity._googleMap;

/**
 * Created by MeadRoseAnn on 7/22/2018.
 */

public class mySQLDeleteDestinationOrRoute extends AsyncTask<String, Void, String> {
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
    public Helper _helper = new Helper();
    public String _typeOfItemToDelete;
    Boolean _isSuccessful;

    public mySQLDeleteDestinationOrRoute(Context context, Activity activity, LoaderDialog loaderDialog, String promptMessage, AlertDialog.Builder alertDialog, String typeOfItemToDelete)
    {
        this._context = context;
        this._activity = activity;
        this._LoaderDialog = loaderDialog;
        this._promptMessage = promptMessage;
        this._alertDialogBuilder = alertDialog;
        this._typeOfItemToDelete = typeOfItemToDelete;

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
            String destinationID = params[0];
            if (_helper.isConnectedToInternet(this._context))
            {

                try{
                    String link = "";
                    if(_typeOfItemToDelete.equals("Destination"))
                        link = _constants.WEB_API_URL + _constants.DESTINATIONS_API_FOLDER + _constants.DESTINATIONS_API_DELETE_FILE_WITH_PENDING_QUERYSTRING+"destinationid="+destinationID;
                    else
                        link = _constants.WEB_API_URL + _constants.ROUTES_API_FOLDER + _constants.ROUTES_API_DELETE_FILE_WITH_PENDING_QUERYSTRING+"routeID="+destinationID;
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
                            return json.get("msg").toString();
                        }
                        else
                        {
                            _isSuccessful = false;
                            return json.get("msg").toString();
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
                String STR_DefaultErrorMessage = _GlobalResource.getString(R.string.Error_looks_like_your_offline);
                _isSuccessful = false;
                Toast.makeText(this._context, STR_DefaultErrorMessage, Toast.LENGTH_LONG).show();
                _LoaderDialog.hide();
                return STR_DefaultErrorMessage;

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


        _LoaderDialog.hide();

        try
        {

            this._alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            this._alertDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    // RefreshList();
                }
            });

            if(_isSuccessful)
                this._alertDialogBuilder.setTitle("Success");
            else
                this._alertDialogBuilder.setTitle("Error");

            this._alertDialogBuilder.setMessage(param);


            new mySQLDestinationProvider(_context, this._activity, "", _googleMap, _googleAPI, _LoaderDialog).execute();



            this._alertDialogBuilder.show();



        }
        catch(Exception ex)
        {

            Helper.logger(ex,true);

        }


    }
}