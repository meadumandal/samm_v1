package com.umandalmead.samm_v1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

/**
 * Created by MeadRoseAnn on 10/8/2017.
 */

public class SessionManager {
    SharedPreferences pref;
    Editor prefEditor;
    Context _context;
    int PRIVATE_MODE = 0;
    FirebaseAuth firebaseAuth;
    private Constants _constants = new Constants();
    private static final String PREF_NAME = "UserPref";

    public static final String KEY_FNAME = "FirstName";
    public static final String KEY_LNAME = "LastName";
    public static final String KEY_USERNAME = "Username";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_DEVICEID = "DeviceId";

    public static final String KEY_ISDRIVER = "IsDriver";
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String IS_GUEST = "IsGuest";
    public static final String ERROR_MSG = "ErrorMsg";
    public static final String REPORT_TYPE = "ReportType";
    public static final String IS_BETA = "IsBeta";
    public static final String IS_DEVELOPER = "IsDeveloper";
    public static final String IS_ADMIN = "IsAdmin";
    public static final String  IS_MAINTOOLTIP_SHOWN = "IsMainToolTopShown";
    public static final String  IS_ROUTETOOLTIP_SHOWN = "IsRouteToolTipShown";
    public static final String CURRENT_MAPS_STYLE = "CurrentMapStyle";
    public SessionManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        prefEditor = pref.edit();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void CreateLoginSession(String firstname, String lastname, String username, String email, boolean isDriver, boolean isGuest, String deviceId)
    {
        prefEditor.putString(KEY_FNAME, firstname);
        prefEditor.putString(KEY_LNAME, lastname);
        prefEditor.putString(KEY_USERNAME, username);
        prefEditor.putString(KEY_EMAIL, email);
        prefEditor.putBoolean(KEY_ISDRIVER, isDriver);
        prefEditor.putBoolean(IS_LOGIN, !isGuest);
        prefEditor.putBoolean(IS_GUEST, isGuest);
        prefEditor.putString(KEY_DEVICEID, deviceId);
        setIsAdmin(email.toLowerCase().equals(_constants.ADMIN_EMAILADDRESS));
        prefEditor.commit();
    }
    public Boolean getMainTutorialStatus()
    {
        return pref.getBoolean(IS_MAINTOOLTIP_SHOWN,false);
    }
    public Boolean getRouteTutorialStatus() {return pref.getBoolean(IS_ROUTETOOLTIP_SHOWN, false);}
    public void TutorialStatus(Enums.UIType type, Boolean isShown){
        switch(type){
            case MAIN:prefEditor.putBoolean(IS_MAINTOOLTIP_SHOWN, isShown); break;
            case SHOWING_ROUTES: prefEditor.putBoolean(IS_ROUTETOOLTIP_SHOWN, isShown); break;
        }
        prefEditor.commit();
    }
    public String getMapStylePreference() {return pref.getString(CURRENT_MAPS_STYLE, null);}
    public void SetMapStylePreference(Enums.GoogleMapType type){
        prefEditor.putString(CURRENT_MAPS_STYLE, type.toString());
        prefEditor.commit();
    }
    public void PassReportType(String reportType)
    {
        prefEditor.putString(REPORT_TYPE, reportType);
        prefEditor.commit();
    }
    public String GetReportType()
    {
        return pref.getString(REPORT_TYPE,null);
    }
    public void CreateLoginSession(boolean isLoggedIn, String errorMsg)
    {
        prefEditor.putBoolean(IS_LOGIN, isLoggedIn);
        prefEditor.putString(ERROR_MSG, errorMsg);
    }
    public HashMap<String,Object> getUserDetails()
    {
        HashMap<String, Object> user = new HashMap<String,Object>();
        user.put(KEY_FNAME, pref.getString(KEY_FNAME,null));
        user.put(KEY_LNAME, pref.getString(KEY_LNAME,null));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME,null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL,null));
        user.put(KEY_ISDRIVER, pref.getBoolean(KEY_ISDRIVER, false));
        user.put(IS_LOGIN, pref.getString(KEY_FNAME,null));
        return user;
    }
    public Boolean getIsBeta()
    {
        return pref.getBoolean(IS_BETA, false);
    }
    public void setIsBeta(Boolean IsBeta)
    {
        prefEditor.putBoolean(IS_BETA, IsBeta);
        prefEditor.commit();
    }

    public Boolean getIsDeveloper()
    {
        return pref.getBoolean(IS_DEVELOPER, false);
    }
    public void setIsDeveloper(Boolean IsDeveloper)
    {
        prefEditor.putBoolean(IS_DEVELOPER, IsDeveloper);
        prefEditor.commit();
    }
    public Boolean getIsAdmin()
    {
        return pref.getBoolean(IS_ADMIN, false);
    }
    public void setIsAdmin(Boolean IsAdmin)
    {
        prefEditor.putBoolean(IS_ADMIN, IsAdmin);
        prefEditor.commit();
    }
    public String getUsername()
    {
        return pref.getString(KEY_USERNAME,null);
    }
    public String getFullName()
    {
        return pref.getString(KEY_FNAME,null) + " " + pref.getString(KEY_LNAME,null);
    }
    public String getLastName()
    {
        return pref.getString(KEY_LNAME,null);
    }
    public String getEmail()
    {
        return pref.getString(KEY_EMAIL,null);
    }
    public boolean isLoggedIn()
    {
        return pref.getBoolean(IS_LOGIN, false) && firebaseAuth.getCurrentUser()!=null;
    }
    public boolean isDriver()
    {
        return pref.getBoolean(KEY_ISDRIVER,false);
    }
    public String getKeyDeviceid()
    {
        return pref.getString(KEY_DEVICEID, "");
    }
    public boolean isGuest()
    {
        return pref.getBoolean(IS_GUEST, false);

    }



    public void checkLogin()
    {
        if (!this.isLoggedIn())
        {
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }
    public void logoutUser(){

        prefEditor.clear();
        prefEditor.commit();
        firebaseAuth.signOut();
    }

}
