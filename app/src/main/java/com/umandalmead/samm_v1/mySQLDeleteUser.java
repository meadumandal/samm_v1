package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.umandalmead.samm_v1.EntityObjects.GPS;
import com.umandalmead.samm_v1.EntityObjects.Users;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by MeadRoseAnn on 7/30/2018.
 */

public class mySQLDeleteUser extends AsyncTask<String, Void, String>{
    Context _context;
    Activity _activity;
    LoaderDialog _LoaderDialog;
    AlertDialog.Builder _alertDialogBuilder;
    String progressMessage;
    public static String TAG="mead";
    String _putData;
    Users _dataModel;
    DialogFragment _dialogFragment;
    private Constants _constants = new Constants();


    public mySQLDeleteUser(Context context, LoaderDialog loaderDialog, Activity activity, Users dataModel)
    {
        Log.i(TAG, "mySQLDeleteUser");
        this._context = context;
        this._LoaderDialog = loaderDialog;
        this._activity = activity;
        this._dataModel = dataModel;



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
            Helper.logger(ex,true);

        }


    }

    /**
     *

     * @return A hashmap with column names and values
     */
    @Override
    protected String doInBackground(String... params)
    {

        Log.i(TAG, "mySQLDeleteUser doInBackground");
        try{
            String username=params[0];

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {
                URL url = new URL(_constants.WEB_API_URL + _constants.USERS_API_FOLDER+ _constants.USERS_API_DELETE_FILE_WITH_PENDING_QUERYSTRING+"username="+username);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONObject json;
            }
            else
            {
                _LoaderDialog.dismiss();
                return "Looks like you're offline";
            }
            _LoaderDialog.dismiss();
            return "GPS deleted!";
        }
        catch(Exception ex)
        {
            _LoaderDialog.dismiss();
            Helper.logger(ex,true);
            return ex.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String returnMessage)
    {
        try
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this._activity);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            alertDialogBuilder.setTitle(MenuActivity._GlobalResource.getString(R.string.dialog_status_success));
            alertDialogBuilder.setMessage(MenuActivity._GlobalResource.getString(R.string.dialog_succesfully_removed_user));
            alertDialogBuilder.show();

        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }
    }


}
