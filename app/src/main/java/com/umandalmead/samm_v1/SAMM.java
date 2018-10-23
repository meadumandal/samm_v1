package com.umandalmead.samm_v1;

import android.widget.Toast;
import com.google.firebase.database.FirebaseDatabase;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by MeadRoseAnn on 10/22/2017.
 */

@ReportsCrashes(
        mailTo = "meadumandal@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_error)
public class SAMM extends android.app.Application {

    private boolean IsOnline = true;
    public void onCreate()
    {
        ACRA.init(this);
        setTheme(R.style.SplashTheme);
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }

}
