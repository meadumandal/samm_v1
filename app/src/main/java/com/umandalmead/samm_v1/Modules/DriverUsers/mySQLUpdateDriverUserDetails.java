package com.umandalmead.samm_v1.Modules.DriverUsers;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.umandalmead.samm_v1.Constants;

import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.InfoDialog;
import com.umandalmead.samm_v1.NonScrollListView;
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

import static com.umandalmead.samm_v1.Constants.LOG_TAG;
import static com.umandalmead.samm_v1.MenuActivity._UserNameMenuItem;

/**
 * Created by MeadRoseAnn on 08/20/2018.
 */

public class mySQLUpdateDriverUserDetails extends AsyncTask<String, Void, String> {
    Context _context;
    Activity _activity;
    ProgressDialog _progressDialog;
    String _promptMessage;
    Boolean _status;
    String newusername,newfirstname, newlastname, newpassword;
    Integer userID;
    SessionManager _sessionManager;
    EditDriverUserDialogFragment _editDialog;
    SwipeRefreshLayout _swipeRefreshLayout;
    NonScrollListView _adminUserListView;
    String _action;
    private Constants _constants = new Constants();
    public mySQLUpdateDriverUserDetails(Context context, Activity activity, ProgressDialog progressDialog, String promptMessage, EditDriverUserDialogFragment editDialog, SwipeRefreshLayout swipeRefreshLayout, NonScrollListView adminUserListView, String action)
    {
        this._context = context;
        this._activity = activity;
        this._progressDialog = progressDialog;
        this._promptMessage = promptMessage;
        this._editDialog = editDialog;
        this._swipeRefreshLayout = swipeRefreshLayout;
        this._adminUserListView = adminUserListView;
        _sessionManager = new SessionManager(_context);
        this._action = action;
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
        userID = Integer.parseInt(params[0]);
        newusername = params[1];
        newfirstname = params[2];
        newlastname = params[3];
        newpassword = params[4];

        JSONObject json = new JSONObject();

        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                if (this._action.equals("Add"))
                {

                    String link = _constants.WEB_API_URL + _constants.USERS_API_FOLDER + "add.php?";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(link);
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                    postParameters.add(new BasicNameValuePair("username", newusername));
                    postParameters.add(new BasicNameValuePair("firstname", newfirstname));
                    postParameters.add(new BasicNameValuePair("lastname", newlastname));
                    postParameters.add(new BasicNameValuePair("emailaddress", Constants.DRIVER_EMAILADDRESS));
                    postParameters.add(new BasicNameValuePair("password", newpassword));
                    postParameters.add(new BasicNameValuePair("usertype", "Driver"));
                    httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                    HttpResponse response = httpClient.execute(httpPost);
                    String strResponse = EntityUtils.toString(response.getEntity());
                    json = new JSONObject(strResponse);
                }
                else if (this._action.equals("Delete"))
                {
                    String link = _constants.WEB_API_URL + _constants.USERS_API_FOLDER + "delete.php?";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(link);
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                    postParameters.add(new BasicNameValuePair("userID", userID.toString()));
                    httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                    HttpResponse response = httpClient.execute(httpPost);
                    String strResponse = EntityUtils.toString(response.getEntity());
                    json = new JSONObject(strResponse);
                }
                else if(this._action.equals("Edit"))
                {
                    String link = _constants.WEB_API_URL + _constants.USERS_API_FOLDER + "edit.php?";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(link);
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                    postParameters.add(new BasicNameValuePair("userID", userID.toString()));
                    postParameters.add(new BasicNameValuePair("newusername", newusername));
                    postParameters.add(new BasicNameValuePair("newfirstname", newfirstname));
                    postParameters.add(new BasicNameValuePair("newlastname", newlastname));
                    if (newpassword != null)
                        postParameters.add(new BasicNameValuePair("newpassword", newpassword));
                    httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                    HttpResponse response = httpClient.execute(httpPost);
                    String strResponse = EntityUtils.toString(response.getEntity());
                    json = new JSONObject(strResponse);


                }
                if(json.getString("status").equals("1"))
                    _status = true;
                else
                    _status = false;
                _promptMessage += json.getString("message") +  "\n";

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

        try
        {
            _progressDialog.hide();
            _UserNameMenuItem.setTitle(_sessionManager.getFullName());

            if(_status)
            {
                InfoDialog dialog=new InfoDialog(this._activity, "Driver user details are updated with the following message/s:\n\n" + _promptMessage);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                _editDialog.dismiss();
                _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        _swipeRefreshLayout.setRefreshing(true);
                        FragmentManager fm = _activity.getFragmentManager();
                        new mySQLGetDriverUsers(_activity, _progressDialog, _adminUserListView, fm,_swipeRefreshLayout).execute();

                    }
                });
                _swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        _swipeRefreshLayout.setRefreshing(true);
                        FragmentManager fm = _activity.getFragmentManager();
                        new mySQLGetDriverUsers(_activity, _progressDialog, _adminUserListView, fm,_swipeRefreshLayout).execute();
                    }
                });
            }
            else
            {
                Toast.makeText(_context,_promptMessage,Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception ex)
        {
            Helper.logger(ex);
        }


    }
}

