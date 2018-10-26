package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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


/**
 * Created by MeadRoseAnn on 06/30/2018
 */


public class mySQLAddRoute extends AsyncTask<String, Void, String>{
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
    public mySQLAddRoute(Context context, Activity activity, LoaderDialog loaderDialog, ManageRoutesActivity.AddRouteDialog addRouteDialog, String promptMessage)
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
            _LoaderDialog = new LoaderDialog(_activity, MenuActivity._GlobalResource.getString(R.string.dialog_please_wait),MenuActivity._GlobalResource.getString(R.string.dialog_adding_route));
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
        String routeName;

        routeName = params[0];


        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String link = _constants.WEB_API_URL + _constants.ROUTES_API_FOLDER + _constants.ADD_ROUTES_API_FILE;
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(link);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                postParameters.add(new BasicNameValuePair("routeName", routeName));
                httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpClient.execute(httpPost);
                String strResponse = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(strResponse);
                if(json.getString("status") == "1")
                {
                    _promptMessage = MenuActivity._GlobalResource.getString(R.string.info_add_route_success);
                }
                else
                {
                    _promptMessage = json.getString("msg") +  "\n";
                }


            }
            catch(Exception ex)
            {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                Helper.logger(ex,true);
                _promptMessage = ex.getMessage() + "\n";

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
        if(_promptMessage.trim().length()>0)
        {
            _LoaderDialog.hide();
            InfoDialog dialog=new InfoDialog(this._activity, _promptMessage);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            _addRouteDialog.hide();
        }

    }
}