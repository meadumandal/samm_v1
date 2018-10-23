package com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.ui.IconGenerator;
import com.umandalmead.samm_v1.EntityObjects.Eloop;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.SessionManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;

/**
 * Created by MeadRoseAnn on 3/25/2018.
 */

public class AddVehicleMarkers implements ChildEventListener {

    Activity _activity;
    Context _context;
    SessionManager _sessionManager;

    Helper _helper = new Helper();
    Marker _vehicleAnimatedMarker;

    public AddVehicleMarkers(Context context, Activity activity) {
        _activity = activity;
        _sessionManager = new SessionManager(context);
        _context = context;


    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        //plotVehicleMarkers(dataSnapshot);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        plotVehicleMarkers(dataSnapshot);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        //plotVehicleMarkers(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        String test = databaseError.getMessage();

    }

    private void plotVehicleMarkers(DataSnapshot dataSnapshot)
    {
        try {
            final String deviceId = dataSnapshot.getKey();
            double lat, lng;

            Object Latitude = dataSnapshot.child("Lat").getValue();
            Object Longitude = dataSnapshot.child("Lng").getValue();
            String routeIDs = dataSnapshot.child("routeIDs").getValue().toString();
            if(MenuActivity._currentRoutesOfEachLoop.get(deviceId)==null)
                MenuActivity._currentRoutesOfEachLoop.put(deviceId, routeIDs);

            if (Latitude == null || Latitude.toString().equals("0"))
                lat = 0.0;
            else
                lat = Double.parseDouble(Latitude.toString());

            if (Longitude == null || Longitude.toString().equals("0"))
                lng = 0.0;
            else
                lng = Double.parseDouble(Longitude.toString());

            final LatLng latLng = new LatLng(lat, lng);
            if (deviceId.toString().equals(_sessionManager.getKeyDeviceid())) {
                //move map camera
                MenuActivity._googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                MenuActivity._googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
            final String vehicle_Plate = "test";//GetEloopEntry(deviceId);
            final Location prevLocation = new Location("");
            final Location currLocation = new Location("");
            prevLocation.setLatitude(Double.parseDouble(dataSnapshot.child("PrevLat").getValue().toString()));
            prevLocation.setLongitude(Double.parseDouble(dataSnapshot.child("PrevLng").getValue().toString()));
            currLocation.setLatitude(lat);
            currLocation.setLongitude(lng);
            final float bearing = (float) _helper.bearingBetweenLocations(prevLocation, currLocation);//prevLocation.bearingTo(currLocation);

            if (MenuActivity._googleMap != null) {
                if(MenuActivity._selectedPickUpPoint ==null) {
                    MenuActivity._markerAnimator = ValueAnimator.ofFloat(0, 1);
                    MenuActivity._markerAnimator.setDuration(2000);
                    MenuActivity._markerAnimator.setInterpolator(new LinearInterpolator());
                    MenuActivity._markerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            try {
                                MarkerOptions markerOptions = new MarkerOptions();
                                if (deviceId.toString().equals(_sessionManager.getKeyDeviceid()))
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ecoloopdriver));
                                else{
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ecoloop_blue));
                                    markerOptions.title(deviceId);
                                }


                                float v = valueAnimator.getAnimatedFraction();
                                double lng = v * currLocation.getLongitude() + (1 - v)
                                        * prevLocation.getLongitude();
                                double lat = v * currLocation.getLatitude() + (1 - v)
                                        * prevLocation.getLatitude();
                                LatLng newPos = new LatLng(lat, lng);
                                markerOptions.position(newPos);
                                if (bearing != 0.0) {
                                    markerOptions.anchor(0.5f, 0.5f);
                                    markerOptions.rotation(bearing);
                                    rotateMarker(markerOptions, bearing);
                                }
                                if (deviceId.toString().equals(_sessionManager.getKeyDeviceid())) {
                                    markerOptions.title("HEY!");
                                    markerOptions.snippet("It's you");
                                }

                                _vehicleAnimatedMarker = (Marker) MenuActivity._driverMarkerHashmap.get(deviceId);
                                if(_vehicleAnimatedMarker == null)
                                {
                                    _vehicleAnimatedMarker = MenuActivity._googleMap.addMarker(markerOptions);
                                    MenuActivity._driverMarkerHashmap.put(deviceId, _vehicleAnimatedMarker);
                                }
                                else
                                {
                                    _vehicleAnimatedMarker.remove();
                                    //if(_vehicleAnimatedMarker==null){
                                    _vehicleAnimatedMarker = MenuActivity._googleMap.addMarker(markerOptions);
                                    //}
                                    //else{
//                                        _vehicleAnimatedMarker.setPosition(newPos);
//                                    _vehicleAnimatedMarker.setRotation(bearing);
                                    //}
                                   // _vehicleAnimatedMarker.showInfoWindow();
                                    MenuActivity._driverMarkerHashmap.remove(deviceId);
                                    MenuActivity._driverMarkerHashmap.put(deviceId, _vehicleAnimatedMarker);
                                }
                                if (deviceId.toString().equals(_sessionManager.getKeyDeviceid())) {
                                    //_vehicleAnimatedMarker.showInfoWindow();
                                }
                               // _vehicleAnimatedMarker.showInfoWindow();
                            } catch (Exception ex) {
                                Helper.logger(ex,true);

                            }
                        }
                    });
                    MenuActivity._markerAnimator.start();
                }
                else
                {
                }
            }
        } catch (Exception ex) {
            Helper.logger(ex,true);
        }
    }
    private void rotateMarker(final MarkerOptions marker, final float toRotation) {
        if(!MenuActivity._isVehicleMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    MenuActivity._isVehicleMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.rotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        MenuActivity._isVehicleMarkerRotating = false;
                    }
                }
            });
        }
    }

}
