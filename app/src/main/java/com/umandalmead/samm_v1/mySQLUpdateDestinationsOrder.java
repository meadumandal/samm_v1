package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.IDNA;
import android.os.AsyncTask;
import android.util.Log;
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;
import static com.umandalmead.samm_v1.MenuActivity._UserNameMenuItem;
import static com.umandalmead.samm_v1.MenuActivity._googleAPI;
import static com.umandalmead.samm_v1.MenuActivity._googleMap;


/**
 * Created by MeadRoseAnn on 7/10/2018.
 */


public class mySQLUpdateDestinationsOrder extends AsyncTask<String, Void, String> {
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
    LoaderDialog _LoaderDialog;
    String _promptMessage;
    SessionManager _sessionManager;
    private Constants _constants = new Constants();
    public Helper _helper = new Helper();
    AlertDialog.Builder _alertDialogBuilder;

    private boolean _isSuccessful = false;
    public mySQLUpdateDestinationsOrder(Context context, Activity activity, LoaderDialog loaderDialog, String promptMessage, AlertDialog.Builder alertDialogBuilder)
    {
        this._context = context;
        this._activity = activity;
        this._LoaderDialog = loaderDialog;
        this._promptMessage = promptMessage;
        this._alertDialogBuilder = alertDialogBuilder;

        _sessionManager = new SessionManager(_context);
    }

    @Override
    protected void onPreExecute()
    {
        try
        {

            super.onPreExecute();
            _LoaderDialog.show();
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

            String pointsArray = params[0];
            String tblRouteID = params[1];

            if (_helper.isConnectedToInternet(this._context))
            {

                try{
                    String link = _constants.WEB_API_URL + _constants.DESTINATIONS_API_FOLDER + "saveDestinationsOrder.php?";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(link);
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                    postParameters.add(new BasicNameValuePair("pointsArray", pointsArray));
                    postParameters.add(new BasicNameValuePair("tblRouteID", tblRouteID));
                    httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                    HttpResponse response = httpClient.execute(httpPost);
                    String strResponse = EntityUtils.toString(response.getEntity());
                    JSONArray jsonArray = new JSONArray(strResponse);
                    for(int i = 0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        if (!Boolean.valueOf(jsonObj.getString("IsValid")))
                        {
                            _isSuccessful = false;
                            break;
                        }
                    }
                    _isSuccessful = true;
                }
                catch(Exception ex)
                {
                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    Helper.logger(ex,true);
                    _isSuccessful = false;


                }
            }
            else
            {
                Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();
                _LoaderDialog.hide();

            }
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }

        return _promptMessage;
    }

    @Override
    protected void onPostExecute(String param)
    {


        _LoaderDialog.hide();
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

        try {

            if(_isSuccessful)
            {

                this._alertDialogBuilder.setTitle("Success");

                this._alertDialogBuilder.setMessage("We have successfully updated the order of points!");


                new mySQLDestinationProvider(_context, this._activity, "", _googleMap, _googleAPI).execute();
            }
            else {
                this._alertDialogBuilder.setTitle("Error");
                this._alertDialogBuilder.setMessage("Error in updating points");
            }
            this._alertDialogBuilder.show();

        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }


    }
}