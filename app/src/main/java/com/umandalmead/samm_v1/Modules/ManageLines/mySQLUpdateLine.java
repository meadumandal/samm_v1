package com.umandalmead.samm_v1.Modules.ManageLines;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umandalmead.samm_v1.Constants;
import com.umandalmead.samm_v1.EntityObjects.Lines;
import com.umandalmead.samm_v1.EntityObjects.Routes;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.InfoDialog;
import com.umandalmead.samm_v1.LoaderDialog;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.Modules.ManageRoutes.ManageRoutesFragment;
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
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.umandalmead.samm_v1.MenuActivity._manageStationsFragment;


/**
 * Created by MeadRoseAnn on 06/30/2018
 */


public class mySQLUpdateLine extends AsyncTask<String, Void, String>{
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
    ManageLinesFragment.AddLineDialog _addLineDialog;
    ManageLinesFragment _manageLinesFragment;
    FragmentManager _fragmentManager;
    String _updatedLinesDataInJSONFormat, _updatedStationDataInJSONFormat;
    Boolean _isSuccessful = false;
    Helper _helper;

    private Constants _constants = new Constants();
    public mySQLUpdateLine(Context context, Activity activity, LoaderDialog loaderDialog,
                           ManageLinesFragment.AddLineDialog addLineDialog, String promptMessage,
                           ManageLinesFragment manageLinesFragment, FragmentManager fragmentManager)
    {
        this._context = context;
        this._activity = activity;
        this._LoaderDialog = loaderDialog;
        this._promptMessage = promptMessage;
        this._addLineDialog = addLineDialog;
        this._manageLinesFragment = manageLinesFragment;
        this._fragmentManager = fragmentManager;
        this._helper = new Helper(_activity, _context);
        _sessionManager = new SessionManager(_context);
    }

    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            _LoaderDialog = new LoaderDialog(_activity,"Please wait...", "Updating Line...");
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
        String lineID;
        String newLineName;
        String newAdminUserID;
        lineID = params[0];
        newLineName = params[1];
        newAdminUserID = params[2];


        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String link = _constants.WEB_API_URL + _constants.LINE_API_FOLDER + "updateLine.php";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(link);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                postParameters.add(new BasicNameValuePair("lineID", lineID));
                postParameters.add(new BasicNameValuePair("newLineName", newLineName));
                postParameters.add(new BasicNameValuePair("newAdminUserID", newAdminUserID));
                httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpClient.execute(httpPost);
                String strResponse = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(strResponse);
                if(json.getBoolean("status") == true)
                {
                    _isSuccessful = true;
                    _promptMessage = MenuActivity._GlobalResource.getString(R.string.info_update_line_success);
                    _updatedLinesDataInJSONFormat = json.getString("lineList");
                    _updatedStationDataInJSONFormat = json.getString("stationList");


                }
                else
                {
                    _isSuccessful = false;
                    _promptMessage = json.getString("message") +  "\n";
                }

            }
            catch(Exception ex)
            {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                Helper.logger(ex,true);
                _promptMessage += "An error is encountered" + "\n";

            }
        }
        else
        {
            Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();
            _LoaderDialog.hide();

        }
        return newLineName;
    }

    @Override
    protected void onPostExecute(String param)
    {

        _LoaderDialog.hide();
        InfoDialog dialog=new InfoDialog(this._activity, _promptMessage);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        _addLineDialog.hide();
        if(_isSuccessful)
        {

            ManageLinesFragment._swipeRefreshLines.setRefreshing(true);
            _helper.UpdateLinesData(_manageLinesFragment, _updatedLinesDataInJSONFormat);
            _helper.UpdateStationsData(_manageStationsFragment, _updatedStationDataInJSONFormat);
            _helper.UpdateStationMarkersOnTheMap(MenuActivity._terminalList, MenuActivity._googleMap, MenuActivity._googleAPI);
            ManageLinesFragment._swipeRefreshLines.setRefreshing(false);
        }
    }
}
