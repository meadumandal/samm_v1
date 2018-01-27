package com.example.samm_v1;

import android.view.Menu;
import android.widget.Toast;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by MeadRoseAnn on 10/22/2017.
 */

public class SAMM extends android.app.Application {

    private boolean IsOnline = true;
    public void onCreate()
    {
        setTheme(R.style.SplashTheme);
//        new com.example.samm_v1.AsyncTask(new com.example.samm_v1.AsyncTask.AsyncResponse(){
//
//            @Override
//            public void processFinish(Boolean output){
//                IsOnline = output;
//            }
//        }).execute();
        if(MenuActivity.isOnline()) {
            super.onCreate();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        else{
            Toast.makeText(this, "Unable to connect to server!", Toast.LENGTH_LONG).show();
        }
    }

}
