package com.example.samm_v1.EntityObjects;

import com.google.android.gms.location.Geofence;

/**
 * Created by MeadRoseAnn on 11/14/2017.
 */

//MySQL Entity

public class Destination {
    public int ID;
    public String Value;
    public String Description;
    public int OrderOfArrival;
    public String Direction;
    public Double Lat;
    public Double Lng;
    public String GeofenceId;

    public Destination()
    {

    }
    public Destination(int ID, String Value, String Description, int OrderOfArrival, String Direction, Double Lat, Double Lng, String GeofenceId)
    {
        this.ID =ID;
        this.Value = Value;
        this.Description = Description;
        this.OrderOfArrival = OrderOfArrival;
        this.Direction = Direction;
        this.Lat = Lat;
        this.Lng = Lng;
        this.GeofenceId = GeofenceId;
    }

    @Override
    public String toString()
    {
        return Description;
    }
}
