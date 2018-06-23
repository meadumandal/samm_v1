package com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.MenuActivity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.umandalmead.samm_v1.SessionManager;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;

/**
 * Created by MeadRoseAnn on 1/17/2018.
 */

public class AddUserMarkers implements
        ChildEventListener{
    Activity _activity;
    SessionManager _sessionManager;
    public String TAG = "mead";

    public AddUserMarkers(Context context, Activity activity) {
        _activity = activity;
        _sessionManager = new SessionManager(context);

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        try
        {
            String username = dataSnapshot.getKey();
            if (!username.equals(((MenuActivity)this._activity)._sessionManager.getUsername()))
            {
                Marker marker;
                marker = (Marker) ((MenuActivity)this._activity)._userMarkerHashmap.get(username);
                if(marker !=null)
                {
                    marker.remove();
                    ((MenuActivity)this._activity)._userMarkerHashmap.remove(username);
                }
                Object Latitude = dataSnapshot.child("Latitude").getValue();
                Object Longitude = dataSnapshot.child("Longitude").getValue();
                double lat, lng;
                if(Latitude == null || Latitude.toString().equals("0"))
                    lat = 0.0;
                else
                    lat = (double) Latitude;
                if(Longitude == null || Longitude.toString().equals("0"))
                    lng = 0.0;
                else
                    lng = (double) Longitude;

                LatLng latLng = new LatLng(lat, lng);
                if (((MenuActivity)this._activity)._googleMap !=null)
                {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(username);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    marker = ((MenuActivity)this._activity)._googleMap.addMarker(markerOptions);
                    marker.showInfoWindow();
                    ((MenuActivity)this._activity)._userMarkerHashmap.put(username, marker);
                }
            }
        }
        catch(Exception ex)
        {
            Helper.logger(ex);
            Toast.makeText(this._activity, "Hello there!", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        String test = databaseError.getMessage();

    }
}