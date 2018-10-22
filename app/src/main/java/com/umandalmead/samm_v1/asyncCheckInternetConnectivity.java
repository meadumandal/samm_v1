package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.umandalmead.samm_v1.LoaderDialog;

import java.io.IOException;

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
        if(!IsOnline){
            _helper.showNoInternetPrompt(_actvity);
        }
        _loader.dismiss();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}
