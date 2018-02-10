package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class mySQLUpdatePassengerCounter extends AsyncTask<String, Void, Void>{
    /**
     *
     * This updates the movement of passengers in mySQL Database
     * @param context
     * @param _activity
     * @param progressMessage Message that will appear in UI while processing
     * @param map Pass the main map of the app, so the asynctask will be able to pin the location of destinations
     */
    Context _context;
    Activity _activity;
    public mySQLUpdatePassengerCounter(Context context, Activity activity)
    {
        this._context = context;
        this._activity = activity;

    }

    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
        }
        catch(Exception e)
        {
            Toast.makeText(this._context, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
    @Override
    protected Void doInBackground(String... params)
    {
        String username = params[0];
        String terminal = params[1];

        String data = "";
        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String link = "http://meadumandal.website/sammAPI/updatePassengerCounter.php?";
                data += "username=" + URLEncoder.encode(username, "UTF-8")
                        + "&terminal=" + URLEncoder.encode(terminal, "UTF-8");
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONObject json = new JSONObject(jsonResponse);

            }
            catch(Exception e)
            {
                Toast.makeText(this._context,e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }
        else
        {
            Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void param)
    {

    }
}
