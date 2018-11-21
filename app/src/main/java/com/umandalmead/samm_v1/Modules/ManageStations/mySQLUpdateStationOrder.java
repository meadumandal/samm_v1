package com.umandalmead.samm_v1.Modules.ManageStations;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.umandalmead.samm_v1.Constants;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.InfoDialog;
import com.umandalmead.samm_v1.LoaderDialog;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.SessionManager;

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

import static com.umandalmead.samm_v1.MenuActivity._googleAPI;
import static com.umandalmead.samm_v1.MenuActivity._googleMap;


/**
 * Created by MeadRoseAnn on 7/10/2018.
 */


public class mySQLUpdateStationOrder extends AsyncTask<String, Void, Void> {
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
    public Helper _helper;
    AlertDialog.Builder _alertDialogBuilder;
    String _updatedStationsDataInJSONFormat;

    private boolean _isSuccessful = true;
    public mySQLUpdateStationOrder(Context context,
                                   Activity activity,
                                   LoaderDialog loaderDialog,
                                   AlertDialog.Builder alertDialogBuilder)
    {
        this._context = context;
        this._activity = activity;
        this._LoaderDialog = loaderDialog;
        this._alertDialogBuilder = alertDialogBuilder;
        this._helper = new Helper(_activity, _context);
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
    protected Void doInBackground(String... params)
    {
        try
        {

            String pointsArray = params[0];
            String tblRouteID = params[1];

            if (_helper.isConnectedToInternet(this._context))
            {
                String link = _constants.WEB_API_URL + _constants.DESTINATIONS_API_FOLDER + _constants.SAVE_DESTINATIONS_ORDER_WITH_PENDING_QUERYSTRNG;
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(link);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                postParameters.add(new BasicNameValuePair("pointsArray", pointsArray));
                postParameters.add(new BasicNameValuePair("tblRouteID", tblRouteID));
                httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpClient.execute(httpPost);
                String strResponse = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(strResponse);
                JSONArray jsonArray = json.getJSONArray("queryResults");
                for(int i = 0; i<jsonArray.length(); i++)
                {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    if (!json.getBoolean("status"))
                    {
                        _isSuccessful=false;
                        break;
                    }
                }
                if (json.getBoolean("status") == false)
                    _isSuccessful = false;

                _promptMessage = json.getString("message");
                if (_isSuccessful) {
                    _promptMessage = MenuActivity._GlobalResource.getString(R.string.info_station_order_save_success);
                    _updatedStationsDataInJSONFormat = json.getString("stationList");
                }

            }
            else
            {
                _promptMessage = MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline);
            }
        }
        catch(Exception ex)
        {
            _promptMessage = ex.getMessage();
            Helper.logger(ex,true);
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
            _helper.UpdateStationsData(MenuActivity._manageStationsFragment, _updatedStationsDataInJSONFormat);
            _helper.UpdateStationMarkersOnTheMap(MenuActivity._terminalList, MenuActivity._googleMap, MenuActivity._googleAPI);
        }


    }
}