package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;
import com.umandalmead.samm_v1.Adapters.listViewCustomAdapter;
import com.umandalmead.samm_v1.EntityObjects.Eloop;
import com.umandalmead.samm_v1.EntityObjects.GPS;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class asyncGetGPSFromTraccar extends AsyncTask<Void, Void, JSONArray>{
    public Context _context;
    public Activity _activity;
    public ProgressDialog _progDialog;
    public NonScrollListView _listView;
    public ArrayList<GPS> _dataModels;
    public FragmentManager _fragmentManager;
    public SwipeRefreshLayout _swipeRefreshGPS;
    public listViewCustomAdapter customAdapter;

    private Constants _constants = new Constants();


    public asyncGetGPSFromTraccar(Context context,
                                  ProgressDialog progDialog,
                                  NonScrollListView listView,
                                  FragmentManager fm,
                                  SwipeRefreshLayout swipeRefreshGPS)
    {
            Log.i(_constants.LOG_TAG, "asyncGetGPSFromTraccar");
        this._context = context;
        this._progDialog = progDialog;
        this._listView = listView;
        this._fragmentManager = fm;
        this._swipeRefreshGPS = swipeRefreshGPS;
        new mySQLGetEloopList(_context).execute();
    }



    /**
     *

     * @return A hashmap with column names and values
     */
    @Override
    protected JSONArray doInBackground(Void... voids) {
            Log.i(_constants.LOG_TAG, "asyncGetGPSFromTraccar doInBackground");
            try {

                Helper helper = new Helper();
                if (helper.isConnectedToInternet(this._context)) {


                    String link = _constants.WEB_API_URL + _constants.DEVICES_API_FOLDER + "getDevices.php";
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

                String GPSName = json.get("name").toString();
                String GPSIMEI = json.get("uniqueId").toString();
                String GPSPhone = json.get("contact").toString();
                String GPSNetwork = json.get("model").toString();
                Integer ID = Integer.parseInt(json.get("id").toString());
                String Status = json.get("status").toString();

                _dataModels.add(new GPS(ID, GPSName, GPSIMEI, GPSPhone, GPSNetwork, Status));

            }
            customAdapter =new listViewCustomAdapter(_dataModels, _context);
            _listView.setAdapter(customAdapter);

            _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        Gson gson = new Gson();
                        GPS selectedGPS = _dataModels.get(position);
                        Eloop selectedEloop = new Eloop();
                        for(Eloop e:MenuActivity._eloopList)
                        {
                            if (e.DeviceId == selectedGPS.getID())
                            {
                                selectedEloop = e;
                            }
                        }
                        String jsonSelectedGPS = gson.toJson(selectedGPS);
                        String jsonSelectedEloop = gson.toJson(selectedEloop);

                        Bundle bundle = new Bundle();
                        bundle.putString("selectedGPS", jsonSelectedGPS);
                        bundle.putString("selectedEloop", jsonSelectedEloop);
                        SerializableRefreshLayoutComponents swipeRefreshLayoutSerializable = new SerializableRefreshLayoutComponents(_swipeRefreshGPS, _fragmentManager, _listView);
                        bundle.putSerializable("swipeRefreshLayoutSerializable", swipeRefreshLayoutSerializable);
                        EditGPSDialogFragment editGPSDialog = new EditGPSDialogFragment();
                        editGPSDialog.setArguments(bundle);
                        editGPSDialog.show(_fragmentManager ,"EditGPSDialogFragment");

                    }
                    catch(Exception ex)
                    {
                        Toast.makeText(_context, "Error occured in fetching data", Toast.LENGTH_LONG).show();
                        Helper.logger(ex);
                    }

                }
            });

            _swipeRefreshGPS.setRefreshing(false);
            _progDialog.dismiss();

        }
        catch(Exception ex)
        {

            Helper.logger(ex);

        }

    }





}
