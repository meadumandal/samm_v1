package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
    ProgressDialog _progressDialog;
    String _promptMessage;
    SessionManager _sessionManager;
    private Constants _constants = new Constants();
    public Helper _helper = new Helper();

    private boolean _isSuccessful = false;
    public mySQLUpdateDestinationsOrder(Context context, Activity activity, ProgressDialog progressDialog, String promptMessage)
    {
        this._context = context;
        this._activity = activity;
        this._progressDialog = progressDialog;
        this._promptMessage = promptMessage;

        _sessionManager = new SessionManager(_context);
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
    @Override
    protected String doInBackground(String... params)
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
                Helper.logger(ex);
                _isSuccessful = false;


            }
        }
        else
        {
            Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();
            _progressDialog.hide();

        }
        return _promptMessage;
    }

    @Override
    protected void onPostExecute(String param)
    {


        _progressDialog.hide();

        try {

            if(_isSuccessful)
            {
                Toast.makeText(_context, "Updated destinations order", Toast.LENGTH_LONG).show();
                new mySQLDestinationProvider(_context, this._activity, "", _googleMap, _googleAPI).execute();
            }
            else
            {
                Toast.makeText(_context, "Error in updating destinations order", Toast.LENGTH_LONG).show();
            }


        }
        catch(Exception ex)
        {
            Helper.logger(ex);
        }


    }
}