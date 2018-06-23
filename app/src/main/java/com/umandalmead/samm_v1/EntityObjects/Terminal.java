package com.umandalmead.samm_v1.EntityObjects;

import android.support.annotation.NonNull;

import com.umandalmead.samm_v1.POJO.Directions;

import java.util.Comparator;

/**
 * Created by MeadRoseAnn on 11/14/2017.
 */

//MySQL Entity

public class Terminal implements Comparable<Terminal>{
    public int ID;
    public String Value;
    public String Description;
    public int OrderOfArrival;
    public String Direction;
    public Double Lat;
    public Double Lng;
    public String GeofenceId;
    public Directions directionsFromCurrentLocation;
    public int DestinationPicture = -1;


    public Terminal()
    {

    }
    public Terminal(int ID, String Value, String Description, int OrderOfArrival, String Direction, Double Lat, Double Lng, String GeofenceId, Directions DirectionsFromCurrentLocation, int DestinationPicture)
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
        this.DestinationPicture = DestinationPicture;

    }

    @Override
    public String toString()
    {
        return Description;
    }

    public int getDestinationPicture() {
        return DestinationPicture;
    }
//    @Override
//    public int compareTo(@NonNull Terminal terminal) {
//        //directions.getRoutes().get(1).getLegs().get(1).getDistance().getValue()
//        if (this.OrderOfArrival > terminal.OrderOfArrival) {
//            return -1;
//        }
//        else if (this.OrderOfArrival < terminal.OrderOfArrival) {
//            return 1;
//        }
//        else {
//            return 0;
//        }
//
//    }
    @Override
    public int compareTo(@NonNull Terminal terminal) {
       // return DestinationComparators.DEFAULT.compare(this, terminal);
        //directions.getRoutes().get(1).getLegs().get(1).getDistance().getValue()
        if (directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDistance().getValue() > terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDistance().getValue()) {
            return 1;
        }
        else if (directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDistance().getValue() < terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0  ).getDistance().getValue()) {
            return -1;
        }
        else {
            return 0;
        }

    }
    public static class DestinationComparators{
        public static Comparator<Terminal> ORDER_OF_ARRIVAL = new Comparator<Terminal>() {
            @Override
            public int compare(Terminal dest1, Terminal dest2) {
                return dest2.OrderOfArrival - dest1.OrderOfArrival;
            }
        };
//        public static Comparator<Terminal> DEFAULT = new Comparator<Terminal>() {
//            @Override
//            public int compare(Terminal destination, Terminal t1) {
//                if (directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDistance().getValue() > destination.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDistance().getValue()) {
//                    return 1;
//                }
//                else if (directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDistance().getValue() < destination.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0  ).getDistance().getValue()) {
//                    return -1;
//                }
//                else {
//                    return 0;
//                }
//            }
//        };
    }
}