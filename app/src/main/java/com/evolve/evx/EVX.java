package com.evolve.evx;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by MeadRoseAnn on 10/22/2017.
 */

public class EVX extends android.app.Application {

    private boolean IsOnline = true;
    public void onCreate()
    {

        setTheme(R.style.SplashTheme);
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }

}
