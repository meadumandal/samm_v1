package com.evolve.evx.Listeners.DatabaseReferenceListeners;

import android.app.Activity;
import android.content.Context;

import com.evolve.evx.EntityObjects.UserMarker;
import com.evolve.evx.Helper;
import com.evolve.evx.MenuActivity;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.evolve.evx.SessionManager;

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
                Boolean IsUserOnline = false;
                try
                {
                    IsUserOnline = Boolean.valueOf(dataSnapshot.child("connections").getValue().toString());
                }
                catch(Exception ex){
                    IsUserOnline = false;
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

                    if(marker !=null)
                    {
                        if(IsUserOnline)
                             marker.setPosition(latLng);
                        else {
                            marker.remove();
                            ((MenuActivity)this._activity)._userMarkerHashmap.remove(username);
                        }

                    }else{
                        String STR_UserType = dataSnapshot.child("UserType").getValue()!=null ?
                                dataSnapshot.child("UserType").getValue().toString(): null;
                        if(IsUserOnline) {
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(username);
                            if(STR_UserType!=null) {
                                markerOptions.snippet(STR_UserType);
                                UserMarker UM_user = Helper.GetUserMarkerDetails(STR_UserType, _activity.getApplicationContext());
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(UM_user.UserIcon));
                            }
                            marker = ((MenuActivity) this._activity)._googleMap.addMarker(markerOptions);
                            ((MenuActivity) this._activity)._userMarkerHashmap.put(username, marker);
                        }
                    }
                }
            }
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
            //Toast.makeText(this._activity, "Hello there!", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        try {
            String username = dataSnapshot.getKey();
            if (!username.equals(((MenuActivity) this._activity)._sessionManager.getUsername())) {
                Marker marker;
                marker = (Marker) ((MenuActivity) this._activity)._userMarkerHashmap.get(username);
                if (((MenuActivity)this._activity)._googleMap !=null) {
                    if (marker != null) {
                        marker.remove();
                        ((MenuActivity)this._activity)._userMarkerHashmap.remove(username);
                    }
                }

            }
        }catch (Exception ex){
            Helper.logger(ex);
        }

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        String test = databaseError.getMessage();

    }

}
