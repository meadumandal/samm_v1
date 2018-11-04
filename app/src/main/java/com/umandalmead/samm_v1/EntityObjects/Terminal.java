package com.umandalmead.samm_v1.EntityObjects;

import android.support.annotation.NonNull;

import com.google.android.gms.vision.text.Line;
import com.umandalmead.samm_v1.POJO.HTMLDirections.Directions;

import java.util.Comparator;

/**
 * Created by MeadRoseAnn on 11/14/2017.
 */

//MySQL Entity

public class Terminal implements Comparable<Terminal>{
    public int ID;
    public int tblRouteID;
    public String Value;
    public String Description;
    public int OrderOfArrival;
    public String Direction;
    public Double Lat;
    public Double Lng;
    public String GeofenceId;
    public Directions directionsFromCurrentLocation;
    public int DestinationPicture = -1;
    public String LineName;



    public Terminal()
    {

    }
    public Terminal(int ID,int tblRouteID, String Value, String Description, int OrderOfArrival, String Direction, Double Lat, Double Lng, String GeofenceId, Directions DirectionsFromCurrentLocation, int DestinationPicture, String LineName)
    {
        this.ID =ID;
        this.tblRouteID = tblRouteID;
        this.Value = Value;
        this.Description = Description;
        this.OrderOfArrival = OrderOfArrival;
        this.Direction = Direction;
        this.Lat = Lat;
        this.Lng = Lng;
        this.GeofenceId = GeofenceId;
        this.directionsFromCurrentLocation = DirectionsFromCurrentLocation;
        this.DestinationPicture = DestinationPicture;
        this.LineName = LineName;

    }


    //region SETTERS
    public void setID(int ID)
    {
        this.ID = ID;
    }
    public void setTblRouteID(int tblRouteID)
    {
        this.tblRouteID = tblRouteID;
    }
    public void setValue(String Value)
    {
        this.Value = Value;
    }
    public void setDescription(String Description)
    {
        this.Description = Description;
    }
    public void setOrderOfArrival(int OrderOfArrival)
    {
        this.OrderOfArrival = OrderOfArrival;
    }
    public void setDirection(String Direction)
    {
        this.Direction = Direction;
    }
    public void setLat(Double Lat)
    {
        this.Lat = Lat;
    }
    public void setLng(Double Lng)
    {
        this.Lng = Lng;
    }
    public void setGeofenceId(String GeofenceId)
    {
        this.GeofenceId = GeofenceId;
    }
    public void setDirectionsFromCurrentLocation(Directions DirectionsFromCurrentLocation)
    {
        this.directionsFromCurrentLocation = directionsFromCurrentLocation;
    }
    public void setDestinationPicture(int DestinationPicture)
    {
        this.DestinationPicture = DestinationPicture;
    }
    //endregion
    //region GETTERS
    public int getID(){return this.ID;}
    public int getTblRouteID(){return this.tblRouteID;}
    public String getValue(){ return this.Value; }
    public String getDescription(){return this.Description;}
    public int getOrderOfArrival(){return this.OrderOfArrival;}
    public String getDirection(){return this.Direction;}
    public Double getLat(){return this.Lat;}
    public Double getLng(){return this.Lng;}
    public String getGeofenceId(){return this.GeofenceId;}
    public Directions getDirectionsFromCurrentLocation(){return this.directionsFromCurrentLocation;}
    public int getDestinationPicture() {
        return DestinationPicture;
    }
    public String getLineName() {return this.LineName;}
    public void setLineName(String LineName){this.LineName = LineName;}
    //endregion


    @Override
    public String toString()
    {
        return Description;
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
