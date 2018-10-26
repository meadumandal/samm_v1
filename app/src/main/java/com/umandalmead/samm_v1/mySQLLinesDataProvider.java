package com.umandalmead.samm_v1;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.umandalmead.samm_v1.Adapters.LineViewCustomAdapter;
import com.umandalmead.samm_v1.EntityObjects.Lines;
import com.umandalmead.samm_v1.EntityObjects.Routes;

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

public class mySQLLinesDataProvider extends AsyncTask<Void,Void, ArrayList<Lines>> {
    Context _context;
    NonScrollListView _listViewToUpdate;
    LoaderDialog _loaderDialog;


    public mySQLLinesDataProvider(Context context, NonScrollListView ListViewToUpdate)
    {
        this._context = context;
        this._listViewToUpdate = ListViewToUpdate;

    }
    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            _loaderDialog = new LoaderDialog((ManageLinesActivity)_context, "LINES","Loading Lines...");
            _loaderDialog.setCancelable(false);
            _loaderDialog.show();
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }


    }
    @Override
    protected ArrayList<Lines> doInBackground(Void... voids) {

        Helper helper = new Helper();
        Constants constants = new Constants();
        ArrayList<Lines> listRoutes = new ArrayList<>();
        if (helper.isConnectedToInternet(this._context))
        {
            try{

                String link = constants.WEB_API_URL + constants.LINE_API_FOLDER + "getLines.php";
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
            Toast.makeText(this._context, MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline), Toast.LENGTH_LONG).show();
            return null;
        }
    }
    @Override
    protected void onPostExecute(ArrayList<Lines> listLines)
    {

        FragmentManager fm = ((ManageLinesActivity)_context).getFragmentManager();
        ArrayList<Lines> lineListCopy = new ArrayList<Lines>(listLines);
        lineListCopy.add(new Lines(0, "Add Line", 0,""));
        ManageLinesActivity._customAdapter =new LineViewCustomAdapter(lineListCopy, _context ,_listViewToUpdate,fm, ManageLinesActivity._swipeRefreshLines);
        _listViewToUpdate.setAdapter(ManageLinesActivity._customAdapter);
        ManageLinesActivity._swipeRefreshLines.setRefreshing(false);
        MenuActivity._lineList = listLines;
        _loaderDialog.dismiss();
    }
}
