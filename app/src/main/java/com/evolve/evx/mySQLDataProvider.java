package com.evolve.evx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class mySQLDataProvider extends AsyncTask<HashMap<String, String>, Void, HashMap<String, Object>>{
    Context _context;
    Activity _activity;
    LoaderDialog _LoaderDialog;
    String progressMessage;

    /**
     *This is the generic format in accessing data from mySQL
     * @param context
     * @param activity
     */
    public mySQLDataProvider(Context context, Activity activity)
    {
        this._context = context;
        this._activity = activity;
        _LoaderDialog = new LoaderDialog(this._activity, "",progressMessage);
    }
    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            _LoaderDialog.setCancelable(false);
            _LoaderDialog.show();
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }


    }

    /**
     *
     * @param arg0 Place the API URL in a Hashmap, then add the parameters to the Hashmap (if any).
     *             The key of the URL in the Hashmap should be "URL"
     * @return A hashmap with column names and values
     */
    @Override
    protected HashMap<String, Object> doInBackground(HashMap<String,String>... arg0)
    {
        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String link = "";
                String data = "";
                Set set = arg0[0].entrySet();
                Iterator iterator = set.iterator();
                while(iterator.hasNext())
                {
                    Map.Entry entry = (Map.Entry)iterator.next();
                    if (entry.getKey().toString()=="URL")
                    {
                        link = entry.getValue().toString();
                    }
                    else
                    {
                        if (data != "")
                        {
                            data +="&";
                        }
                        data += URLEncoder.encode(entry.getKey().toString(), "UTF-8") + "=" +
                                URLEncoder.encode(entry.getValue().toString(), "UTF-8");
                    }
                }

                //String link = "http://meadumandal.website/sammAPI/signIn.php?";
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;
                return helper.jsonToMap(reader.readLine());
            }
            catch(Exception ex)
            {
                HashMap<String, Object> returnValues = new HashMap<String,Object>();
                returnValues.put("IsValid", 0);
                returnValues.put("Message", MenuActivity._GlobalResource.getString(R.string.error_contacting_server));

                Helper.logger(ex,true);

                return returnValues;
            }
        }
        else
        {
            HashMap<String, Object> returnValues = new HashMap<String,Object>();
            returnValues.put("IsValid", 0);
            returnValues.put("Message", MenuActivity._GlobalResource.getString(R.string.error_not_connected));
            return returnValues;
        }

    }

    @Override
    protected void onPostExecute(HashMap<String, Object> result)
    {
        try
        {
            SessionManager sessionManager = new SessionManager(this._context);
            if(result.get("IsValid").toString().equals("1"))
            {
                String firstName = result.get("FirstName").toString();
                String lastName = result.get("LastName").toString();
                String userName = result.get("Username").toString();
                String email = result.get("EmailAddress").toString();
                boolean isDriver = Boolean.valueOf(result.get("IsDriver").toString());

                sessionManager.CreateLoginSession(firstName,lastName,userName,0,email,"", false,"");

                Intent i = new Intent(this._context, MenuActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this._context.startActivity(i);

            }
            else
            {
                sessionManager.CreateLoginSession(false, result.get("Message").toString());
                Toast.makeText(this._context, result.get("Message").toString(), Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
            _LoaderDialog.dismiss();
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }

    }


}
