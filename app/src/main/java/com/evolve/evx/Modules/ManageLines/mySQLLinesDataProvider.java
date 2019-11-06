package com.evolve.evx.Modules.ManageLines;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.evolve.evx.Constants;
import com.evolve.evx.EntityObjects.Lines;
import com.evolve.evx.Helper;
import com.evolve.evx.LoaderDialog;
import com.evolve.evx.MenuActivity;
import com.evolve.evx.NonScrollListView;
import com.evolve.evx.R;
import com.evolve.evx.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by MeadRoseAnn on 6/27/2018.
 */

public class mySQLLinesDataProvider extends AsyncTask<String,Void, ArrayList<Lines>> {
    Activity _activity;
    NonScrollListView _listViewToUpdate;
    LoaderDialog _loaderDialog;
    ManageLinesFragment _manageLinesFragment;
    FragmentManager _fragmentManager;
    SessionManager _sessionManager;

    public mySQLLinesDataProvider(Activity activity, @Nullable NonScrollListView ListViewToUpdate, ManageLinesFragment manageLinesFragment,
                                  FragmentManager fragmentManager)
    {
        this._activity = activity;
        this._listViewToUpdate = ListViewToUpdate;
        this._manageLinesFragment = manageLinesFragment;
        this._fragmentManager = fragmentManager;
        this._sessionManager = new SessionManager(_activity.getApplicationContext());
    }
    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            if (_listViewToUpdate != null)
            {
                _loaderDialog = new LoaderDialog(_activity, "LINES","Loading Lines...");
                _loaderDialog.setCancelable(false);
                _loaderDialog.show();
            }

        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }


    }
    @Override
    protected ArrayList<Lines> doInBackground(String... params) {
        Integer adminUserID = 0;
        if(params[0]!=null)
            adminUserID = Integer.parseInt(params[0]);

        Helper helper = new Helper();
        Constants constants = new Constants();
        ArrayList<Lines> listRoutes = new ArrayList<>();
        if (helper.isConnectedToInternet(this._activity))
        {
            try{

                String link = constants.WEB_API_URL + constants.LINE_API_FOLDER + "getLines.php?adminUserID=" + adminUserID;
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray = new JSONArray(jsonResponse);
                for(int index=0; index < jsonArray.length(); index++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(index);
                    int id  = jsonobject.getInt("ID");
                    String lineName  = jsonobject.getString("Name");
                    int Admin_User_ID = jsonobject.getInt("Admin_User_ID");
                    String adminUserName = jsonobject.getString("AdminUserName");
                    listRoutes.add(new Lines(id,lineName, Admin_User_ID, adminUserName));
                }
                return listRoutes;
            }
            catch(Exception ex)
            {
                Helper.logger(ex,true);
                return null;
            }
        }
        else
        {
            Toast.makeText(this._activity, MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline), Toast.LENGTH_LONG).show();
            return null;
        }
    }
    @Override
    protected void onPostExecute(ArrayList<Lines> listLines)
    {


        ArrayList<Lines> lineListCopy = new ArrayList<Lines>(listLines);
//        if(_sessionManager.getIsSuperAdmin())
//            lineListCopy.add(new Lines(0, "Add Line", 0,""));
        if(_listViewToUpdate != null)
        {
            ManageLinesFragment._customAdapter =new LineViewCustomAdapter(lineListCopy, _activity,_listViewToUpdate,_fragmentManager, ManageLinesFragment._swipeRefreshLines, _manageLinesFragment);
            _listViewToUpdate.setAdapter(ManageLinesFragment._customAdapter);
            ManageLinesFragment._swipeRefreshLines.setRefreshing(false);
            _loaderDialog.dismiss();
        }

        MenuActivity._lineList = listLines;



    }
}
