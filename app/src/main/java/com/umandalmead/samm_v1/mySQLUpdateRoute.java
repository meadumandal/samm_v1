package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
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
 * Created by MeadRoseAnn on 06/30/2018
 */


public class mySQLUpdateRoute extends AsyncTask<String, Void, String>{
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
    ManageRoutesActivity.AddRouteDialog _addRouteDialog;

    private Constants _constants = new Constants();
    public mySQLUpdateRoute(Context context, Activity activity, LoaderDialog loaderDialog, ManageRoutesActivity.AddRouteDialog addRouteDialog,  String promptMessage)
    {
        this._context = context;
        this._activity = activity;
        this._LoaderDialog = loaderDialog;
        this._promptMessage = promptMessage;
        this._addRouteDialog = addRouteDialog;

        _sessionManager = new SessionManager(_context);
    }

    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            _LoaderDialog = new LoaderDialog(_activity,MenuActivity._GlobalResource.getString(R.string.dialog_please_wait), MenuActivity._GlobalResource.getString(R.string.dialog_updating_routes));
            _LoaderDialog.setCancelable(false);
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
        String routeID;
        String newRouteName;
        routeID = params[0];
        newRouteName = params[1];


        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String link = _constants.WEB_API_URL + _constants.ROUTES_API_FOLDER + _constants.ROUTE_UPDATE_API_FILE ;
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(link);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                postParameters.add(new BasicNameValuePair("routeID", routeID));
                postParameters.add(new BasicNameValuePair("newRouteName", newRouteName));
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
                Helper.logger(ex,true);
                _promptMessage += ex.getMessage() + "\n";

            }
        }
        else
        {
            Toast.makeText(this._context, MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline), Toast.LENGTH_LONG).show();
            _LoaderDialog.hide();

        }
        return newRouteName;
    }

    @Override
    protected void onPostExecute(String param)
    {


        _LoaderDialog.hide();

        if(_promptMessage.trim().length()>0)
        {
            InfoDialog dialog=new InfoDialog(this._activity, _promptMessage);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            _addRouteDialog.hide();
        }

    }
}