package com.umandalmead.samm_v1;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.umandalmead.samm_v1.EntityObjects.Eloop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MeadRoseAnn on 4/15/2018.
 */

public class mySQLGetEloopList extends AsyncTask<Void,Void, List<Eloop>> {
    Context _context;


    public mySQLGetEloopList(Context context)
    {
        this._context = context;

    }
    @Override
    protected List<Eloop> doInBackground(Void... voids) {

        Helper helper = new Helper();
        Constants constants = new Constants();
        List<Eloop> listEloops = new ArrayList<>();
        if (helper.isConnectedToInternet(this._context))
        {
            try{

                String link = constants.WEB_API_URL + constants.ELOOPS_API_FOLDER + "getEloops.php";
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray = new JSONArray(jsonResponse);
                for(int index=0; index < jsonArray.length(); index++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(index);
                    int id       = jsonobject.getInt("ID");
                    int deviceId       = jsonobject.getInt("DeviceId");
                    String deviceName = jsonobject.getString("DeviceName");
                    String plateNumber    = jsonobject.getString("PlateNumber");
                    int tblUsersID = jsonobject.getInt("tblUsersID");
                    int tblRoutesID = jsonobject.getInt("tblRoutesID");
                    int IsActive = jsonobject.getInt("IsActive");
                    listEloops.add(new Eloop(id,deviceId, deviceName, plateNumber, tblUsersID, tblRoutesID, IsActive));
                }
                return listEloops;
            }
            catch(Exception ex)
            {
                Helper.logger(ex,true);
                return null;
            }
        }
        else
        {
            Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();
            return null;
        }
    }
    @Override
    protected void onPostExecute(List<Eloop> listEloops)
    {
        MenuActivity._eloopList = listEloops;
    }
}
