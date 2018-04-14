package com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.SessionManager;

/**
 * Created by MeadRoseAnn on 3/25/2018.
 */

public class AddPassengerCountLabel implements ChildEventListener {

    Activity _activity;
    Context _context;
    SessionManager _sessionManager;
    Helper _helper = new Helper();

    public AddPassengerCountLabel(Context context, Activity activity) {
        _activity = activity;
        _sessionManager = new SessionManager(context);
        _context = context;
    }
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        try {
            Marker terminalEntered = MenuActivity._terminalMarkerHashmap.get(dataSnapshot.getKey());
            if (terminalEntered != null) {
                terminalEntered.showInfoWindow();
                terminalEntered.setSnippet(String.valueOf(dataSnapshot.getChildrenCount()) + " passenger/s waiting");
            }
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        try {
            Marker terminalEntered = MenuActivity._terminalMarkerHashmap.get(dataSnapshot.getKey());
            if (terminalEntered != null)
            {
                terminalEntered.showInfoWindow();
                terminalEntered.setSnippet(String.valueOf(dataSnapshot.getChildrenCount()) + " passenger/s waiting");
            }

        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        try {
            Marker terminalEntered = MenuActivity._terminalMarkerHashmap.get(dataSnapshot.getKey());
            if (terminalEntered != null) {
                terminalEntered.showInfoWindow();
                terminalEntered.setSnippet(String.valueOf(dataSnapshot.getChildrenCount()) + " passenger/s waiting");
            }
        } catch (Exception ex) {
            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
