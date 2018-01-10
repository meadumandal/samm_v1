package com.example.samm_v1.EntityObjects;

import android.support.annotation.NonNull;

import com.example.samm_v1.POJO.Directions;
import com.google.android.gms.location.Geofence;

import java.util.Comparator;

/**
 * Created by MeadRoseAnn on 11/14/2017.
 */

//MySQL Entity

public class Destination implements Comparable<Destination>{
    public int ID;
    public String Value;
    public String Description;
    public int OrderOfArrival;
    public String Direction;
    public Double Lat;
    public Double Lng;
    public String GeofenceId;
    public Directions directionsFromCurrentLocation;


    public Destination()
    {

    }
    public Destination(int ID, String Value, String Description, int OrderOfArrival, String Direction, Double Lat, Double Lng, String GeofenceId, Directions DirectionsFromCurrentLocation)
    {
        this.ID =ID;
        this.Value = Value;
        this.Description = Description;
        this.OrderOfArrival = OrderOfArrival;
        this.Direction = Direction;
        this.Lat = Lat;
        this.Lng = Lng;
        this.GeofenceId = GeofenceId;
        this.directionsFromCurrentLocation = DirectionsFromCurrentLocation;

    }

    @Override
    public String toString()
    {
        return Description;
    }

    @Override
    public int compareTo(@NonNull Destination destination) {
        //directions.getRoutes().get(1).getLegs().get(1).getDistance().getValue()
        if (directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDistance().getValue() > destination.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDistance().getValue()) {
            return 1;
        }
        else if (directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDistance().getValue() < destination.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0  ).getDistance().getValue()) {
            return -1;
        }
        else {
            return 0;
        }

    }
}
