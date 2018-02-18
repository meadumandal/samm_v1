package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.umandalmead.samm_v1.Adapters.listViewCustomAdapter;
import com.umandalmead.samm_v1.EntityObjects.GPS;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class asyncGetGPSFromTraccar extends AsyncTask<Void, Void, JSONArray>{
    Context _context;
    Activity _activity;
    ProgressDialog _progDialog;
    NonScrollListView _listView;
    ArrayList<GPS> _dataModels;
    public static String TAG="mead";


    public asyncGetGPSFromTraccar(Context context, ProgressDialog progDialog, ArrayList<GPS> dataModels, NonScrollListView listView)
    {
        Log.i(TAG, "asyncGetGPSFromTraccar");
        this._context = context;
        this._progDialog = progDialog;

        this._dataModels = dataModels;
        this._listView = listView;

    }



    /**
     *

     * @return A hashmap with column names and values
     */
    @Override
    protected JSONArray doInBackground(Void... voids) {
            Log.i(TAG, "asyncGetGPSFromTraccar doInBackground");
            try {

                Helper helper = new Helper();
                if (helper.isConnectedToInternet(this._context)) {

                    String link = "http://meadumandal.website/sammAPI/getDevices.php";
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String jsonResponse = reader.readLine();
                    JSONArray jsonArray;

                    try {

                        return new JSONArray(jsonResponse);
                    } catch (Exception e) {
                        _progDialog.dismiss();
                        Log.e(TAG, e.getMessage());
                        return null;
                    }
                } else {
                    _progDialog.dismiss();
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage() + e.getMessage());
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
                String GPSPhone = json.get("phone").toString();
                String GPSNetwork = json.get("model").toString();
                Integer ID = Integer.parseInt(json.get("id").toString());
                _dataModels.add(new GPS(ID, GPSName, GPSIMEI, GPSPhone, GPSNetwork));
            }
            _listView.setAdapter(new listViewCustomAdapter(_dataModels,getApplicationContext()));
            _progDialog.dismiss();
            _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    GPS dataModel= _dataModels.get(position);
//                    MenuActivity.AddGPSDialog dialog=new MenuActivity.AddGPSDialog(_context);
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    dialog.show();
                }
            });
        }
        catch(Exception e)
        {

            Log.e(TAG, e.getMessage());

        }

    }





}
