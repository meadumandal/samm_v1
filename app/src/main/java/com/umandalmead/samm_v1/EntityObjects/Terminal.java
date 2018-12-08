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
    public Integer ID;
    public int tblRouteID;
    public String Value;
    public String Description;
    public int OrderOfArrival;
    public String Direction;
    public Double Lat;
    public Double Lng;
    public Double distanceFromPreviousStation;

    public String GeofenceId;
    public Directions directionsFromCurrentLocation;
    public double distanceFromCurrentLocation;
    public int DestinationPicture = -1;
    public String LineName;
    public String isMainTerminal;
    public String routeName;
    public Integer LineID;



    public Terminal()
    {

    }
    public Terminal(int ID,
                    int tblRouteID,
                    String Value,
                    String Description,
                    int OrderOfArrival,
                    String Direction,
                    Double Lat,
                    Double Lng,
                    String GeofenceId,
                    Directions DirectionsFromCurrentLocation,
                    double DistanceFromCurrentLocation,
                    String LineName,
                    String isMainTerminal,
                    String routeName,
                    Integer lineID,
                    Double distanceFromPreviousStation)
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
        this.distanceFromCurrentLocation = DistanceFromCurrentLocation;
        this.distanceFromPreviousStation = distanceFromPreviousStation;

        this.LineName = LineName;
        this.isMainTerminal = isMainTerminal;
        this.routeName = routeName;
        this.LineID =lineID;

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
    public void setIsMainTerminal(String isMainTerminal){
        this.isMainTerminal = isMainTerminal;
    }
    public String getIsMainTerminal()
    {
        return this.isMainTerminal;
    }
    //endregion
    //region GETTERS
    public Integer getID(){return this.ID;}
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
    public String getRouteName(){return this.routeName;}
    public void setRouteName(String routeName){this.routeName = routeName;}
    public Integer getLineID() {return this.LineID;}
    public void setLineID(Integer lineID) {this.LineID = lineID;}
    public double getDistanceFromCurrentLocation(){return this.distanceFromCurrentLocation;}
    public void setDistanceFromCurrentLocation(double distanceFromCurrentLocation){this.distanceFromCurrentLocation = distanceFromCurrentLocation;}
    public void setDistanceFromPreviousStation(double distanceFromPreviousStation){this.distanceFromPreviousStation = distanceFromPreviousStation;}
    public double getDistanceFromPreviousStation(){return this.distanceFromPreviousStation;}
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
        if (distanceFromCurrentLocation > terminal.distanceFromCurrentLocation) {
            return 1;
        }
        else if (distanceFromCurrentLocation < terminal.distanceFromCurrentLocation) {
            return -1;
        }
        else {
            return 0;
        }

    }
    public static class DestinationComparators{
        public static Comparator<Terminal> ORDER_OF_ARRIVAL_DESC = new Comparator<Terminal>() {
            @Override
            public int compare(Terminal dest1, Terminal dest2) {
                return dest2.OrderOfArrival - dest1.OrderOfArrival;
            }
        };
        public static Comparator<Terminal> ORDER_OF_ARRIVAL_ASC = new Comparator<Terminal>() {
            @Override
            public int compare(Terminal dest1, Terminal dest2) {
                return dest1.OrderOfArrival - dest2.OrderOfArrival;
            }
        };
        public static Comparator<Terminal> ORDER_BY_ROUTEID_ASC = new Comparator<Terminal>() {
            @Override
            public int compare(Terminal dest1, Terminal dest2) {
                return dest1.tblRouteID - dest2.tblRouteID;
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
