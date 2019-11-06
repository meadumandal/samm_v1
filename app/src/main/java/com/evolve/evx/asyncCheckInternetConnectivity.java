package com.evolve.evx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

/**
 * Created by eleazerarcilla on 21/10/2018.
 */

public class asyncCheckInternetConnectivity extends AsyncTask<Void, Void, Boolean> {
    private Activity _actvity;
    private Context _context;
    private LoaderDialog _loader;
    private Helper _helper;
    public  asyncCheckInternetConnectivity(Activity activity){
        this._actvity = activity;
        this._helper = new Helper();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _loader = new LoaderDialog(_actvity,"Connecting","Please wait while we check your internet connectivity...");
        _loader.show();
    }

    @Override
    protected void onPostExecute(Boolean IsOnline) {
        super.onPostExecute(IsOnline);
        try {
            if (!IsOnline) {
                _helper.showNoInternetPrompt(_actvity);
                _loader.dismiss();
            } else {
                _loader.dismiss();
                this._actvity.startActivity(new Intent(this._actvity, LoginActivity.class));
            }
        }catch (Exception ex){
            _loader.dismiss();
            Helper.logger(ex);
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean IsConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) _actvity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            IsConnected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        } else {
            IsConnected = false;
        }

        return IsConnected;
    }
}
