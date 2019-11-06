package com.evolve.evx.Modules.DriverUsers;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.evolve.evx.Constants;

import com.evolve.evx.Helper;
import com.evolve.evx.InfoDialog;
import com.evolve.evx.LoaderDialog;
import com.evolve.evx.NonScrollListView;
import com.evolve.evx.SessionManager;

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

import static com.evolve.evx.MenuActivity._UserNameMenuItem;

/**
 * Created by MeadRoseAnn on 08/20/2018.
 */

public class mySQLUpdateDriverUserDetails extends AsyncTask<String, Void, String> {
    Context _context;
    Activity _activity;
    LoaderDialog _LoaderDialog;
    String _promptMessage;
    Boolean _status;
    String newusername,newfirstname, newlastname, newpassword, newTblLineID;
    Integer userID;
    SessionManager _sessionManager;
    EditDriverUserDialogFragment _editDialog;
    SwipeRefreshLayout _swipeRefreshLayout;
    NonScrollListView _adminUserListView;
    String _action;
    private Constants _constants = new Constants();
    Helper _helper = new Helper();
    public mySQLUpdateDriverUserDetails(Context context,
                                        Activity activity,
                                        LoaderDialog loaderDialog,
                                        String promptMessage,
                                        EditDriverUserDialogFragment editDialog,
                                        SwipeRefreshLayout swipeRefreshLayout,
                                        NonScrollListView adminUserListView,
                                        String action)
    {
        this._context = context;
        this._activity = activity;
        this._LoaderDialog = loaderDialog;
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
            Helper.logger(ex,true);
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
        newTblLineID = params[5];


        JSONObject json = new JSONObject();

        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                if (this._action.equals("Add"))
                {

                    String link = _constants.WEB_API_URL + _constants.DRIVER_API_FOLDER + "add.php?";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(link);
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                    postParameters.add(new BasicNameValuePair("username", newusername));
                    postParameters.add(new BasicNameValuePair("firstname", newfirstname));
                    postParameters.add(new BasicNameValuePair("lastname", newlastname));
                    postParameters.add(new BasicNameValuePair("emailaddress", Constants.DRIVER_EMAILADDRESS));
                    postParameters.add(new BasicNameValuePair("password", newpassword));
                    postParameters.add(new BasicNameValuePair("usertype", "Driver"));
                    postParameters.add(new BasicNameValuePair("tblLineID", newTblLineID));
                    httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                    HttpResponse response = httpClient.execute(httpPost);
                    String strResponse = EntityUtils.toString(response.getEntity());
                    json = new JSONObject(strResponse);
                }
                else if (this._action.equals("Delete"))
                {
                    String link = _constants.WEB_API_URL + _constants.DRIVER_API_FOLDER + "delete.php?";
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
                    String link = _constants.WEB_API_URL + _constants.DRIVER_API_FOLDER + "edit.php?";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(link);
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                    postParameters.add(new BasicNameValuePair("userID", userID.toString()));
                    postParameters.add(new BasicNameValuePair("newusername", newusername));
                    postParameters.add(new BasicNameValuePair("newfirstname", newfirstname));
                    postParameters.add(new BasicNameValuePair("newlastname", newlastname));
                    postParameters.add(new BasicNameValuePair("newlineid", newTblLineID));
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

                _promptMessage += ex.getMessage() + "\n";


            }
        }
        else
        {
            Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();
            _LoaderDialog.hide();

        }
        return _promptMessage;
    }

    @Override
    protected void onPostExecute(String param)
    {

        try
        {
            _LoaderDialog.hide();
            _UserNameMenuItem.setTitle(_sessionManager.getFullName().toUpperCase());

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
                        new mySQLGetDriverUsers(_activity, _LoaderDialog, _adminUserListView, fm,_swipeRefreshLayout).execute(_sessionManager.getUserID());

                    }
                });
                _swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        _swipeRefreshLayout.setRefreshing(true);
                        FragmentManager fm = _activity.getFragmentManager();
                        new mySQLGetDriverUsers(_activity, _LoaderDialog, _adminUserListView, fm,_swipeRefreshLayout).execute(_sessionManager.getUserID());
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
            Helper.logger(ex,true);
        }


    }
}


