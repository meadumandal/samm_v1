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

    public static final String KEY_ISlOGIN = "IsLoggedIn";

    public static final String KEY_ISFACEBOOKACCOUNT ="IsFacebook";
    public static final String KEY_ERRORMSG = "ErrorMsg";
    public static final String KEY_REPORTTYPE = "ReportType";
    public static final String KEY_ISBETA = "IsBeta";
    public static final String KEY_ISDEVELOPER = "IsDeveloper";

    public static final String KEY_ISSUPERADMIN = "IsSuperAdmin";
    public static final String KEY_ISADMIN = "IsAdmin";
    public static final String KEY_ISPASSENGER = "IsPassenger";
    public static final String KEY_ISDRIVER = "IsDriver";
    public static final String KEY_ISGUEST = "IsGuest";

    public static final String KEY_ISMAINTOOLTIPSHOWN = "IsMainToolTopShown";
    public static final String KEY_ISROUTETOOLTIPSHOWN = "IsRouteToolTipShown";
    public static final String KEY_CURRENTMAPSTYLE = "CurrentMapStyle";

    public SessionManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        prefEditor = pref.edit();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void CreateLoginSession(String firstname, String lastname, String username, String email, String deviceId, boolean isFacebook, String userType)
    {
        prefEditor.putString(KEY_FNAME, firstname);
        prefEditor.putString(KEY_LNAME, lastname);
        prefEditor.putString(KEY_USERNAME, username);
        prefEditor.putString(KEY_EMAIL, email);
        prefEditor.putBoolean(KEY_ISlOGIN, !userType.equals(Constants.GUEST_USERTYPE));
        prefEditor.putString(KEY_DEVICEID, deviceId);
        prefEditor.putBoolean(KEY_ISFACEBOOKACCOUNT, isFacebook);
        prefEditor.putBoolean(KEY_ISDRIVER, userType.equals(Constants.DRIVER_USERTYPE));
        prefEditor.putBoolean(KEY_ISADMIN, userType.equals(Constants.ADMIN_USERTYPE));
        prefEditor.putBoolean(KEY_ISSUPERADMIN, userType.equals(Constants.SUPERADMIN_USERTYPE));
        prefEditor.putBoolean(KEY_ISGUEST, userType.equals(Constants.GUEST_USERTYPE));
        prefEditor.putBoolean(KEY_ISPASSENGER, userType.equals(Constants.PASSENGER_USERTYPE));
        prefEditor.commit();
    }
    public Boolean getMainTutorialStatus()
    {
        try {
            return pref.getBoolean(KEY_ISMAINTOOLTIPSHOWN, false);
        }catch (Exception e){
            return false;
        }
    }
    public Boolean getRouteTutorialStatus() {return pref.getBoolean(KEY_ISROUTETOOLTIPSHOWN, false);}
    public void TutorialStatus(Enums.UIType type, Boolean isShown){
        switch(type){
            case MAIN:prefEditor.putBoolean(KEY_ISMAINTOOLTIPSHOWN, isShown); break;
            case SHOWING_ROUTES: prefEditor.putBoolean(KEY_ISROUTETOOLTIPSHOWN, isShown); break;
        }
        prefEditor.commit();
    }
    public String getMapStylePreference() {return pref.getString(KEY_CURRENTMAPSTYLE, null);}
    public void SetMapStylePreference(Enums.GoogleMapType type){
        prefEditor.putString(KEY_CURRENTMAPSTYLE, type.toString());
        prefEditor.commit();
    }
    public void PassReportType(String reportType)
    {
        prefEditor.putString(KEY_REPORTTYPE, reportType);
        prefEditor.commit();
    }
    public String GetReportType()
    {
        return pref.getString(KEY_REPORTTYPE,null);
    }
    public void CreateLoginSession(boolean isLoggedIn, String errorMsg)
    {
        prefEditor.putBoolean(KEY_ISlOGIN, isLoggedIn);
        prefEditor.putString(KEY_ERRORMSG, errorMsg);
    }
    public HashMap<String,Object> getUserDetails()
    {
        HashMap<String, Object> user = new HashMap<String,Object>();
        user.put(KEY_FNAME, pref.getString(KEY_FNAME,null));
        user.put(KEY_LNAME, pref.getString(KEY_LNAME,null));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME,null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL,null));
        user.put(KEY_ISDRIVER, pref.getBoolean(KEY_ISDRIVER, false));
        user.put(KEY_ISlOGIN, pref.getString(KEY_FNAME,null));
        return user;
    }
    public Boolean getIsBeta()
    {
        return pref.getBoolean(KEY_ISBETA, false);
    }
    public void setIsBeta(Boolean IsBeta)
    {
        prefEditor.putBoolean(KEY_ISBETA, IsBeta);
        prefEditor.commit();
    }

    public Boolean getIsDeveloper()
    {
        return pref.getBoolean(KEY_ISDEVELOPER, false);
    }
    public void setIsDeveloper(Boolean IsDeveloper)
    {
        prefEditor.putBoolean(KEY_ISDEVELOPER, IsDeveloper);
        prefEditor.commit();
    }
    public Boolean getIsAdmin()
    {
        return pref.getBoolean(KEY_ISADMIN, false);
    }
    public void setIsAdmin(Boolean IsAdmin)
    {
        prefEditor.putBoolean(KEY_ISADMIN, IsAdmin);
        prefEditor.commit();
    }
    public Boolean getIsSuperAdmin()
    {
        return pref.getBoolean(KEY_ISSUPERADMIN, false);
    }
    public void setKeyIssuperadmin(Boolean IsSuperAdmin)
    {
        prefEditor.putBoolean(KEY_ISSUPERADMIN, IsSuperAdmin);
        prefEditor.commit();
    }
    public Boolean getIsPassenger()
    {
        return pref.getBoolean(KEY_ISPASSENGER, false);
    }
    public void setKeyIspassenger(Boolean IsPassenger)
    {
        prefEditor.putBoolean(KEY_ISSUPERADMIN, IsPassenger);
        prefEditor.commit();
    }
    public String getUsername()
    {
        return pref.getString(KEY_USERNAME,null);
    }
    public String getFullName(){ return pref.getString(KEY_FNAME,null) + " " + pref.getString(KEY_LNAME,null); }
    public String getFirstName() { return pref.getString(KEY_FNAME,null); }
    public String getLastName()
    {
        return pref.getString(KEY_LNAME,null);
    }
    public String getEmail()
    {
        return pref.getString(KEY_EMAIL,null);
    }
    public void setFirstName(String firstName)
    {
        prefEditor.remove(KEY_FNAME);
        prefEditor.putString(KEY_FNAME, firstName);
        prefEditor.commit();
    }
    public void setLastName(String lastName)
    {
        prefEditor.remove(KEY_LNAME);
        prefEditor.putString(KEY_LNAME, lastName);
        prefEditor.commit();
    }
    public boolean isLoggedIn()
    {
        return pref.getBoolean(KEY_ISlOGIN, false) && firebaseAuth.getCurrentUser()!=null;
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
        return pref.getBoolean(KEY_ISGUEST, false);

    }
    public boolean isFacebook()
    {
        return pref.getBoolean(KEY_ISFACEBOOKACCOUNT, false);
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
