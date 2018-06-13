package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class mySQLSignUp extends AsyncTask<String, Void, Void>{
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
    private Constants _constants = new Constants();
    public mySQLSignUp(Context context, Activity activity)
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
        catch(Exception ex)
        {
            Helper.logger(ex);
        }


    }
    @Override
    protected Void doInBackground(String... params)
    {
        String username = params[0];
        String firstName = params[1];
        String lastName = params[2];
        String emailAddress = params[3];
        String data = "";
        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {

            try{
                String link = _constants.WEB_API_URL + "signUp.php?";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(link);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                postParameters.add(new BasicNameValuePair("username", username));
                postParameters.add(new BasicNameValuePair("firstName", firstName));
                postParameters.add(new BasicNameValuePair("lastName", lastName));
                postParameters.add(new BasicNameValuePair("emailAddress", emailAddress));
                httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpClient.execute(httpPost);
                String strResponse = EntityUtils.toString(response.getEntity());


//                String link = _constants.WEB_API_URL + "signUp.php?";
//                data += "username=" + URLEncoder.encode(username, "UTF-8")
//                        + "&firstName=" + URLEncoder.encode(firstName, "UTF-8")
//                        + "&lastName=" + URLEncoder.encode(lastName, "UTF-8")
//                        + "&emailAddress=" + URLEncoder.encode(emailAddress, "UTF-8");
//                URL url = new URL(link);
//                URLConnection conn = url.openConnection();
//
//
//                conn.setDoOutput(true);
//
//                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//                wr.write(data);
//                wr.flush();
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String jsonResponse = reader.readLine();
//                JSONObject json = new JSONObject(jsonResponse);
                JSONObject json = new JSONObject(strResponse);

            }
            catch(Exception ex)
            {
                StringWriter sw = new StringWriter();
                ex.printStackTrace(new PrintWriter(sw));
                Log.e(LOG_TAG, "StackTrace: " + sw.toString() + " | Message: " + ex.getMessage());

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
