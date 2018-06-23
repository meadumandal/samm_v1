package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;
import static com.umandalmead.samm_v1.MenuActivity._UserNameMenuItem;


/**
 * Created by MeadRoseAnn on 06/20/2018
 */


public class mySQLUpdateUserDetails extends AsyncTask<String, Void, String>{
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
    String _promptMessage, _newFirstName, _newLastName;
    SessionManager _sessionManager;
    private Constants _constants = new Constants();
    public mySQLUpdateUserDetails(Context context, Activity activity, ProgressDialog progressDialog, String promptMessage)
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
        String username = params[0];
        _newFirstName = params[1];
        _newLastName = params[2];

        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {
            if(_newFirstName.equals(_sessionManager.getFirstName()) && _newLastName.equals(_sessionManager.getLastName()))
            {
                return "";
            }
            try{
                String link = _constants.WEB_API_URL + "updateUserDetails.php?";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(link);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                postParameters.add(new BasicNameValuePair("username", username));
                postParameters.add(new BasicNameValuePair("firstName", _newFirstName));
                postParameters.add(new BasicNameValuePair("lastName", _newLastName));
                httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpClient.execute(httpPost);
                String strResponse = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(strResponse);

                _promptMessage += json.getString("Message") +  "\n";
            }
            catch(Exception ex)
            {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                Log.e(LOG_TAG, "StackTrace: " + sw.toString() + " | Message: " + ex.getMessage());
                _promptMessage += ex.getMessage() + "\n";

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
        _sessionManager.setFirstName(_newFirstName);
        _sessionManager.setLastName(_newLastName);
        _UserNameMenuItem.setTitle(_sessionManager.getFullName());

        if(_promptMessage.trim().length()>0)
        {
            InfoDialog dialog=new InfoDialog(this._activity, "Update profile is done with the following message/s:\n\n" + _promptMessage);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

    }
}