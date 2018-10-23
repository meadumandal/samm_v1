package com.umandalmead.samm_v1;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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

public class mySQLRoutesDataProvider extends AsyncTask<Void,Void, ArrayList<Routes>> {
    Context _context;
    int _lineID = 0;
    LoaderDialog _loaderDialog;


    public mySQLRoutesDataProvider(Context context)
    {
        this._context = context;

    }

    @Override
    protected ArrayList<Routes> doInBackground(Void... voids) {

        Helper helper = new Helper();
        Constants constants = new Constants();
        ArrayList<Routes> listRoutes = new ArrayList<>();
        if (helper.isConnectedToInternet(this._context))
        {
            try{

                String link = constants.WEB_API_URL + constants.ROUTES_API_FOLDER + "getRoutes.php";
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
                        listRoutes.add(new Routes(id,tblLineID,routeName));

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
            Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();
            return null;
        }
    }
    @Override
    protected void onPostExecute(ArrayList<Routes> listRoutes)
    {
        if (_loaderDialog != null)
            _loaderDialog.dismiss();
        MenuActivity._routeList = listRoutes;
    }
}
