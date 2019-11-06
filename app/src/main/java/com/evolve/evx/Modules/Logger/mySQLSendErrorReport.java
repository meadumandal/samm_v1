package com.evolve.evx.Modules.Logger;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.evolve.evx.Constants;
import com.evolve.evx.Helper;
import com.evolve.evx.MenuActivity;
import com.evolve.evx.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MeadRoseAnn on 10/22/2018.
 */

public class mySQLSendErrorReport extends AsyncTask<String, Void, Void> {
    Context _context;
    Activity _activity;
    Helper _helper = new Helper();
    Constants _constants = new Constants();
    Boolean _isSuccessful;
    public mySQLSendErrorReport()
    {

    }
    @Override
    protected void onPreExecute()
    {

    }
    @Override
    protected Void doInBackground(String... params)
    {
        try
        {
            String errorMessage = params[0];
            if (_helper.isConnectedToInternet(this._context))
            {

                try{
                    String link = "";
                    link = _constants.WEB_API_URL  + "sendErrorReport.php";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(link);
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                    postParameters.add(new BasicNameValuePair("errorMessage", errorMessage));
                    httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                    HttpResponse response = httpClient.execute(httpPost);
                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String jsonResponse = reader.readLine();
                    JSONObject json;
                }
                catch(Exception ex){
                    _isSuccessful = false;
                    Helper.logger(ex);
                }
            }
            else
            {
                _isSuccessful = false;
                Toast.makeText(this._context, MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline), Toast.LENGTH_LONG).show();

            }
        }
        catch(Exception ex)
        {
            _isSuccessful = false;
            Helper.logger(ex,true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void param)
    {

    }

}
