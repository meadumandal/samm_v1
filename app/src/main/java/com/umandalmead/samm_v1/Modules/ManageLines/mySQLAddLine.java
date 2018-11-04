package com.umandalmead.samm_v1.Modules.ManageLines;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

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
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by MeadRoseAnn on 06/30/2018
 */


public class mySQLAddLine extends AsyncTask<String, Void, Void>{
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
    String _updatedLinesDataUsinJSONFormat;
    Boolean _isSuccessful = false;
    Helper _helper;

    private Constants _constants = new Constants();
    public mySQLAddLine(Context context, Activity activity, LoaderDialog loaderDialog,
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
        this._helper = new Helper(_activity,_context);
        _sessionManager = new SessionManager(_context);
    }

    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            _LoaderDialog = new LoaderDialog(_activity, "Please wait...","Adding Line...");
            _LoaderDialog.setCancelable(false);
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
        String lineName;
        String adminUserID;

        lineName = params[0];
        adminUserID = params[1];


        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String link = _constants.WEB_API_URL + _constants.LINE_API_FOLDER + "addLine.php";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(link);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                postParameters.add(new BasicNameValuePair("lineName", lineName));
                postParameters.add(new BasicNameValuePair("adminUserID", adminUserID));
                httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpClient.execute(httpPost);
                String strResponse = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(strResponse);
                if(json.getBoolean("status") == true)
                {
                    _isSuccessful = true;
                    _promptMessage = MenuActivity._GlobalResource.getString(R.string.info_add_new_line_success);
                    _updatedLinesDataUsinJSONFormat = json.getString("lineList");

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
                _promptMessage = ex.getMessage() + "\n";

            }
        }
        else
        {
            Toast.makeText(this._context, MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline), Toast.LENGTH_LONG).show();
            _LoaderDialog.hide();

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
        _addLineDialog.hide();
        if(_isSuccessful)
        {

            _manageLinesFragment._swipeRefreshLines.setRefreshing(true);
            _helper.UpdateLinesData(_manageLinesFragment, _updatedLinesDataUsinJSONFormat);
            _manageLinesFragment._swipeRefreshLines.setRefreshing(false);
        }

    }
}