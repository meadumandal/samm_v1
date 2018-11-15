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
import org.json.JSONObject;

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
    LoaderDialog _LoaderDialog;
    String _promptMessage, _newFirstName, _newLastName;
    SessionManager _sessionManager;
    private Constants _constants = new Constants();
    public mySQLUpdateUserDetails(Context context, Activity activity, LoaderDialog loaderDialog, String promptMessage)
    {
        this._context = context;
        this._activity = activity;
        this._LoaderDialog = loaderDialog;
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
            Helper.logger(ex,true);
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
                String link = _constants.WEB_API_URL + _constants.USERS_API_FOLDER + _constants.USERS_UPDATE_API_FILE_WITH_PENDING_QUERYSTRING;
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
                helper.logger(ex,true);
                _promptMessage += ex.getMessage() + "\n";

            }
        }
        else
        {
            Toast.makeText(this._context, MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline), Toast.LENGTH_LONG).show();
            _LoaderDialog.hide();

        }
        return _promptMessage;
    }

    @Override
    protected void onPostExecute(String param)
    {
        _LoaderDialog.hide();
        _sessionManager.setFirstName(_newFirstName);
        _sessionManager.setLastName(_newLastName);
        _UserNameMenuItem.setTitle(_sessionManager.getFullName().toUpperCase());
        MenuActivity._HeaderUserFullName.setText(_sessionManager.getFullName().toUpperCase());

        if(_promptMessage.trim().length()>0)
        {
            InfoDialog dialog=new InfoDialog(this._activity, MenuActivity._GlobalResource.getString(R.string.dialog_user_update_success_with_message) + _promptMessage);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

    }
}