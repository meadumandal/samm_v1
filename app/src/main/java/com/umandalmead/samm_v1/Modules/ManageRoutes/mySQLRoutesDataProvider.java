package com.umandalmead.samm_v1.Modules.ManageRoutes;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.umandalmead.samm_v1.Constants;
import com.umandalmead.samm_v1.EntityObjects.Routes;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.LoaderDialog;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.NonScrollListView;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.SessionManager;

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

public class mySQLRoutesDataProvider extends AsyncTask<String,Void, ArrayList<Routes>> {
    Activity _activity;
    Context _context;
    int _lineID = 0;
    LoaderDialog _loaderDialog;
    NonScrollListView _listViewToUpdate;
    ManageRoutesFragment _manageRoutesFragment;
    FragmentManager _fragmentManager;


    public mySQLRoutesDataProvider(Activity activity, Context context, NonScrollListView listViewToUpdate,
                                   ManageRoutesFragment manageRoutesFragment, FragmentManager fragmentManager)
    {
        this._context = context;
        this._activity = activity;
        this._listViewToUpdate = listViewToUpdate;
        this._manageRoutesFragment = manageRoutesFragment;
        this._fragmentManager = fragmentManager;
    }
    public mySQLRoutesDataProvider(Activity activity,Context context)
    {
        this._context = context;
        this._activity = activity;
    }
    @Override
    protected void onPreExecute()
    {
        try
        {
            SessionManager s = new SessionManager(_context);

            super.onPreExecute();
            //if activity is not null, this means that the call came from ManageRoutes
            if (this._activity != null){
                _loaderDialog = new LoaderDialog(_activity, "ROUTES","Loading Routes...");
                _loaderDialog.setCancelable(false);
                _loaderDialog.show();
                _manageRoutesFragment._swipeRefreshRoute.setRefreshing(true);
            }

        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }


    }
    @Override
    protected ArrayList<Routes> doInBackground(String... params) {
        if (params.length>0)
            _lineID = Integer.parseInt(params[0]);

        Helper helper = new Helper();
        Constants constants = new Constants();
        ArrayList<Routes> listRoutes = new ArrayList<>();
        if (helper.isConnectedToInternet(this._context))
        {
            try{

                String link = constants.WEB_API_URL + constants.ROUTES_API_FOLDER + constants.GET_ROUTES_API_FILE;
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray = new JSONArray(jsonResponse);
                for(int index=0; index < jsonArray.length(); index++) {

                    JSONObject jsonobject = jsonArray.getJSONObject(index);

                    int id       = jsonobject.getInt("ID");
                    int tblLineID = jsonobject.getInt("tblLineID");
                    String routeName  = jsonobject.getString("routeName");
                    int noOfStations = jsonobject.getInt("noOfStations");
                        listRoutes.add(new Routes(id,tblLineID,routeName, noOfStations));

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
    protected void onPostExecute(ArrayList<Routes> listRoutes)
    {

        MenuActivity._routeList = listRoutes;

        //if activity is not null, this means that the call came from ManageRoutes
        if(this._listViewToUpdate!=null)
        {
            ArrayList<Routes> routeListCopy = new ArrayList<Routes>();
            if (_lineID!=0)
                for(Routes route:listRoutes)
                {
                    if (route.getTblLineID() == _lineID)
                        routeListCopy.add(route);
                }
            else
                routeListCopy = new ArrayList<Routes>(listRoutes);


            
//            routeListCopy.add(new Routes(0,0,  "Add Route"));
            ManageRoutesFragment.customAdapter =new RouteViewCustomAdapter(routeListCopy, _activity ,_listViewToUpdate,_fragmentManager, ManageRoutesFragment._swipeRefreshRoute, _manageRoutesFragment);
            _listViewToUpdate.setAdapter(ManageRoutesFragment.customAdapter);
            ManageRoutesFragment._swipeRefreshRoute.setRefreshing(false);
        }


        if (_loaderDialog!=null)
                _loaderDialog.dismiss();
        MenuActivity._routeList = listRoutes;
        SessionManager session = new SessionManager(_context);
        if(session.isDriver()){
            ((MenuActivity)mySQLRoutesDataProvider.this._activity).InflateDriverRoutesPanel();
        }
    }
}
