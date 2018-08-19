package com.umandalmead.samm_v1.Modules.DriverUsers;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.umandalmead.samm_v1.Adapters.adminUsersListViewCustomerAdapter;
import com.umandalmead.samm_v1.Constants;
import com.umandalmead.samm_v1.EntityObjects.Users;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.NonScrollListView;
import com.umandalmead.samm_v1.SerializableRefreshLayoutComponents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * Created by MeadRoseAnn on 08/19/2018.
 */


public class mySQLGetDriverUsers extends AsyncTask<Void, Void, JSONArray>{
    public Context _context;
    public Activity _activity;
    public ProgressDialog _progDialog;
    public NonScrollListView _listView;
    public ArrayList<Users> _dataModels;
    public FragmentManager _fragmentManager;

    public SwipeRefreshLayout _swipeRefreshDriverUsers;
    public adminUsersListViewCustomerAdapter customAdapter;

    private Constants _constants = new Constants();



    public mySQLGetDriverUsers(Context context,
                              ProgressDialog progDialog,
                              NonScrollListView listView,
                              FragmentManager fm,
                              SwipeRefreshLayout swipeRefreshDriverUsers)
    {
        Log.i(_constants.LOG_TAG, "mySQLGetDriverUsers");
        this._context = context;
        this._progDialog = progDialog;
        this._listView = listView;
        this._fragmentManager = fm;
        this._swipeRefreshDriverUsers = swipeRefreshDriverUsers;

    }



    /**
     *

     * @return A hashmap with column names and values
     */
    @Override
    protected JSONArray doInBackground(Void... voids) {
        Log.i(_constants.LOG_TAG, "mySQLGetDriverUsers doInBackground");
        try {

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context)) {
                String link = _constants.WEB_API_URL + _constants.USERS_API_FOLDER + "getUsersByType.php?usertype=driver";
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray;

                try {

                    return new JSONArray(jsonResponse);
                } catch (Exception ex) {
                    _progDialog.dismiss();
                    Helper.logger(ex);
                    return null;
                }
            } else {
                _progDialog.dismiss();
                return null;
            }
        } catch (Exception ex) {
            Helper.logger(ex);
            _progDialog.dismiss();
            return null;

        }

    }

    @Override
    protected void onPostExecute(JSONArray jsonArray)
    {
        try
        {
            _dataModels= new ArrayList<>();
            Integer i = 0;
            for(i=0;i<jsonArray.length();i++)
            {
                JSONObject json =  (JSONObject)jsonArray.get(i);

                int ID = json.getInt("ID");
                String username = json.getString("username");
                String emailAddress = json.getString("emailAddress");
                String firstName = json.getString("firstName");
                String lastName = json.getString("lastName");
                String userType = json.getString("userType");
                String password = json.getString("password");
                int IsActive = json.getInt("IsActive");

                _dataModels.add(new Users(ID, username, emailAddress, firstName, lastName, userType, password, IsActive));
            }

            _dataModels.add(new Users(0,"Add new driver", "","","","","",1));

            customAdapter =new adminUsersListViewCustomerAdapter(_dataModels, _context);
            _listView.setAdapter(customAdapter);

            _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        Boolean isAdd;
                        if (_dataModels.get(position).username.substring(0, 7).equals("Add new"))
                            isAdd = true;
                        else
                            isAdd = false;
                        Gson gson = new Gson();
                        String json = gson.toJson(_dataModels.get(position));
                        Bundle bundle = new Bundle();
                        bundle.putString("datamodel", json);
                        SerializableRefreshLayoutComponents swipeRefreshLayoutSerializable = new SerializableRefreshLayoutComponents(_swipeRefreshDriverUsers, _fragmentManager, _listView);
                        bundle.putSerializable("swipeRefreshLayoutSerializable", swipeRefreshLayoutSerializable);
                        bundle.putBoolean("isAdd", isAdd);

                        EditDriverUserDialogFragment editDriverUserDialog = new EditDriverUserDialogFragment();
                        editDriverUserDialog.setArguments(bundle);
                        editDriverUserDialog.show(_fragmentManager ,"EditDriverUserDialog");
                    }
                    catch(Exception ex)
                    {
                        Helper.logger(ex);
                    }

                }
            });

            _swipeRefreshDriverUsers.setRefreshing(false);
            _progDialog.dismiss();

        }
        catch(Exception ex)
        {

            Helper.logger(ex);

        }

    }





}
