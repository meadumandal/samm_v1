package com.evolve.evx.EntityObjects;

/**
 * Created by MeadRoseAnn on 11/11/2018.
 */

public class VehicleGeofenceHistory {
    public String StationName;
    public String PlateNumber;
    public Integer NumberOfTrips;

    public VehicleGeofenceHistory(String StationName, String PlateNumber, Integer NumberOfTrips)
    {
        this.StationName = StationName;
        this.PlateNumber = PlateNumber;
        this.NumberOfTrips = NumberOfTrips;
    }
}
