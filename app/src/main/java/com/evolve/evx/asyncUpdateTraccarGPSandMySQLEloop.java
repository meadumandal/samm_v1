package com.evolve.evx;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.evolve.evx.EntityObjects.Eloop;
import com.evolve.evx.EntityObjects.GPS;
import com.evolve.evx.Modules.GPS.EditGPSDialogFragment;
import com.evolve.evx.Modules.GPS.asyncGetGPSFromTraccar;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class asyncUpdateTraccarGPSandMySQLEloop extends AsyncTask<Void, Void, String>{
    Context _context;
    Activity _activity;
    LoaderDialog _LoaderDialog;
    AlertDialog.Builder _alertDialogBuilder;
    String progressMessage;
    public static String TAG="mead";
    String _putData;
    GPS _dataModelGPS;
    Eloop _dataModelEloop;
    DialogFragment _dialogFragment;
    private Constants _constants = new Constants();
    EditGPSDialogFragment _dialog;
    SwipeRefreshLayout _swipeRefreshLayout;
    NonScrollListView _gpsListView;


    public asyncUpdateTraccarGPSandMySQLEloop(Context context, LoaderDialog loaderDialog, Activity activity, EditGPSDialogFragment dialog, GPS dataModelGPS, Eloop dataModelEloop,SwipeRefreshLayout swipeRefreshLayout, NonScrollListView GPSListView)
    {
        Log.i(TAG, "asyncUpdateTraccarGPSandMySQLEloop");
        this._context = context;
        this._LoaderDialog = loaderDialog;
        this._activity = activity;
        this._dataModelGPS = dataModelGPS;
        this._dataModelEloop = dataModelEloop;
        this._dialog = dialog;
        this._swipeRefreshLayout = swipeRefreshLayout;
        this._gpsListView = GPSListView;



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
    protected String doInBackground(Void... voids)
    {
        Log.i(TAG, "asyncUpdateTraccarGPSandMySQLEloop doInBackground");
        String returnString;
        try{
           //api parameters
            String id= _dataModelGPS.getID().toString();
            String uniqueId = _dataModelGPS.getGPSIMEI().toString();
            String name = _dataModelGPS.getGPSName();
            String phone = _dataModelGPS.getGPSPhone();
            String networkProvider = _dataModelGPS.getGPSNetworkProvider();
            String plateNumber = _dataModelEloop.PlateNumber;
            String tblRoutesID = String.valueOf(_dataModelEloop.tblRoutesID);
            String tblUsersID = String.valueOf(_dataModelEloop.tblUsersID);
            String tblLineID = String.valueOf(_dataModelEloop.tblLinesID);

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context))
            {
                String link = _constants.WEB_API_URL + _constants.DEVICES_API_FOLDER +  "updateDevice.php";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(link);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                postParameters.add(new BasicNameValuePair("deviceID", id));
                postParameters.add(new BasicNameValuePair("name", name));
                postParameters.add(new BasicNameValuePair("uniqueId", uniqueId));
                postParameters.add(new BasicNameValuePair("phoneNo", phone));
                postParameters.add(new BasicNameValuePair("model", networkProvider));
                postParameters.add(new BasicNameValuePair("plateNumber", plateNumber));
                postParameters.add(new BasicNameValuePair("tblRoutesID", tblRoutesID));
                postParameters.add(new BasicNameValuePair("tblUsersID", tblUsersID));
                postParameters.add(new BasicNameValuePair("tblLineID", tblLineID));
                httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpClient.execute(httpPost);
                String strResponse = EntityUtils.toString(response.getEntity());
                JSONObject json;

                json = new JSONObject(strResponse);
                if (json.getBoolean("status"))
                {
                    _LoaderDialog.dismiss();
                    returnString= "Success";
                }
                else
                {
                    _LoaderDialog.dismiss();
                    returnString= json.getString("message");
                }
            }
            else
            {
                _LoaderDialog.dismiss();
                returnString=  "Looks like you're offline";
            }

        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
            returnString = ex.getMessage();
            _dialog.dismiss();

        }
        return returnString;
    }

    @Override
    protected void onPostExecute(String returnMessage)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this._activity);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        try
        {
            String message = returnMessage;

            if(message.equals("Success"))
            {
                InfoDialog infoDialog = new InfoDialog(_activity, MenuActivity._GlobalResource.getString(R.string.GPS_update_success));
                _dialog.dismiss();
                _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        _swipeRefreshLayout.setRefreshing(true);
                        FragmentManager fm = _activity.getFragmentManager();
                        new asyncGetGPSFromTraccar(_activity, _LoaderDialog, _gpsListView, fm,_swipeRefreshLayout, ViewGPSFragment._viewGPSFragment).execute();

                    }
                });
                _swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        _swipeRefreshLayout.setRefreshing(true);
                        FragmentManager fm = _activity.getFragmentManager();
                        new asyncGetGPSFromTraccar(_activity, _LoaderDialog, _gpsListView, fm,_swipeRefreshLayout, ViewGPSFragment._viewGPSFragment).execute();
                    }
                });
                infoDialog.show();
//              new mySQLSignUp(_activity, _activity).execute(gpsname, "EVX", deviceId, "sammdriver@yahoo.com");
            }
            else
            {
                ErrorDialog errorDialog = new ErrorDialog(_activity, MenuActivity._GlobalResource.getString(R.string.dialog_status_error) + ": " + message);
                errorDialog.show();
                _dialog.dismiss();
            }

//        Toast.makeText(_activity, s, Toast.LENGTH_LONG).show();
        }
        catch(Exception ex)
        {
            ErrorDialog errorDialog = new ErrorDialog(_activity, MenuActivity._GlobalResource.getString(R.string.dialog_status_error));
            errorDialog.show();
            _dialog.dismiss();
            Helper.logger(ex,true);

        }

    }





}
