package com.example.samm_v1;

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
    private static final String PREF_NAME = "UserPref";

    public static final String KEY_FNAME = "FirstName";
    public static final String KEY_LNAME = "LastName";
    public static final String KEY_USERNAME = "Username";
    public static final String KEY_EMAIL = "Email";

    public static final String KEY_ISDRIVER = "IsDriver";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String ERROR_MSG = "ErrorMsg";




    public SessionManager(Context context)
    {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        prefEditor = pref.edit();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void CreateLoginSession(String firstname, String lastname, String username, String email, boolean isDriver)
    {
        prefEditor.putString(KEY_FNAME, firstname);
        prefEditor.putString(KEY_LNAME, lastname);
        prefEditor.putString(KEY_USERNAME, username);
        prefEditor.putString(KEY_EMAIL, email);
        prefEditor.putBoolean(KEY_ISDRIVER, isDriver);
        prefEditor.putBoolean(IS_LOGIN, true);

        prefEditor.commit();
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
    public String getUsername()
    {
        return pref.getString(KEY_USERNAME,null);
    }
    public boolean isLoggedIn()
    {
        return pref.getBoolean(IS_LOGIN, false) && firebaseAuth.getCurrentUser()!=null;
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