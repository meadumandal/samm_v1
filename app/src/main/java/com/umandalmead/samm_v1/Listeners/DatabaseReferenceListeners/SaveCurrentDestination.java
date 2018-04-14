package com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners;

import android.content.Context;

import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by MeadRoseAnn on 1/17/2018.
 */

public class SaveCurrentDestination implements ValueEventListener {
    private MenuActivity _activity;
    private Context _context;
    private SessionManager _sessionManager;
    private HashMap<String, Object> _currentDestination;

    public SaveCurrentDestination(MenuActivity activity, Context context, HashMap<String, Object> currentDestination) {
        this._activity = activity;
        this._context = context;
        this._currentDestination = currentDestination;
        this._sessionManager = new SessionManager(context);


    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(dataSnapshot.getValue()==null)
        {
            (this._activity)._usersDBRef.child(_sessionManager.getUsername()).child("currentDestination").setValue(_currentDestination.get("currentDestination"));
        }
        else
        {
            (this._activity)._usersDBRef.child(_sessionManager.getUsername()).updateChildren(_currentDestination);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
