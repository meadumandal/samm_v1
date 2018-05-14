package com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.SessionManager;
import com.umandalmead.samm_v1.mySQLUpdateWaitingPassengerHistory;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;

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
                LatLng markerPosition = terminalEntered.getPosition();
                terminalEntered.remove();
                MenuActivity._terminalMarkerHashmap.remove(dataSnapshot.getKey());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(markerPosition);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ecoloopstop));
                markerOptions.snippet(dataSnapshot.getChildrenCount() + " passenger/s waiting");
                markerOptions.title(dataSnapshot.getKey());
                Marker newTerminalMarker = MenuActivity._googleMap.addMarker(markerOptions);
                newTerminalMarker.showInfoWindow();

                ((MenuActivity)this._activity)._terminalMarkerHashmap.put(dataSnapshot.getKey(), newTerminalMarker);
                Log.i(LOG_TAG, "Updating waiting passenger history for reports...");

                new mySQLUpdateWaitingPassengerHistory(_context, _activity).execute(dataSnapshot.getKey(),  Long.toString(dataSnapshot.getChildrenCount()));
            }
        } catch (Exception ex) {
            Helper.logger(ex);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        try {
            Marker terminalEntered = MenuActivity._terminalMarkerHashmap.get(dataSnapshot.getKey());
            if (terminalEntered != null)
            {

                LatLng markerPosition = terminalEntered.getPosition();
                terminalEntered.remove();
                MenuActivity._terminalMarkerHashmap.remove(dataSnapshot.getKey());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(markerPosition);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ecoloopstop));
                markerOptions.snippet(dataSnapshot.getChildrenCount() + " passenger/s waiting");
                markerOptions.title(dataSnapshot.getKey());
                Marker newTerminalMarker = MenuActivity._googleMap.addMarker(markerOptions);
                newTerminalMarker.showInfoWindow();

                ((MenuActivity)this._activity)._terminalMarkerHashmap.put(dataSnapshot.getKey(), newTerminalMarker);
                Log.i(LOG_TAG, "Updating passenger count for reports...");

                new mySQLUpdateWaitingPassengerHistory(_context, _activity).execute(dataSnapshot.getKey(),  Long.toString(dataSnapshot.getChildrenCount()));


            }

        } catch (Exception ex) {
            Helper.logger(ex);
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
            Helper.logger(ex);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
