package com.umandalmead.samm_v1;

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
            super.onCreate();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
