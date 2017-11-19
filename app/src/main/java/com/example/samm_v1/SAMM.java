package com.example.samm_v1;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by MeadRoseAnn on 10/22/2017.
 */

public class SAMM extends android.app.Application{
    @Override
    public void onCreate()
    {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
