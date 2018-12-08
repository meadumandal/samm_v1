package com.umandalmead.samm_v1.EntityObjects;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.ToDoubleFunction;

/**
 * Created by eleazerarcilla on 01/12/2018.
 */

public class VehicleProperties implements Comparable<VehicleProperties> {
    private Eloop EloopProperties;
    private Terminal  TerminalObject;
    private String LastEnteredTerminal;
    private Double TotalDistanceFromUser;
    private Integer OrderOfArrivalDifference;
    private String PossibleRouteIDs;
    private String EntryRouteID;
    private ArrayList<Terminal> TargetDestinationSpecimen;

    public VehicleProperties(){

    }
    public VehicleProperties(Eloop eloop, String terminal, Terminal terminalObject,  Double distance){
        this.EloopProperties = eloop;
        this.LastEnteredTerminal = terminal;
        this.TotalDistanceFromUser = distance;
    }
    public void setEloop(Eloop eloop){
        this.EloopProperties = eloop;
    }
    public void setTerminal(String terminal){
        this.LastEnteredTerminal = terminal;
    }
    public void setTargetDestinationSpecimen(ArrayList<Terminal> terminal){
        this.TargetDestinationSpecimen = terminal;
    }
    public void setTerminalObject(Terminal terminal){
        this.TerminalObject = terminal;
    }
    public void setDistance(Double distance){
        this.TotalDistanceFromUser = distance;
    }
    public void setOrderOfArrivalDifference(Integer difference){
        this.OrderOfArrivalDifference = difference;
    }
    public void setPossibleRouteIDs(String routeIDs){
        this.PossibleRouteIDs = routeIDs;
    }
    public void setEntryRouteID(String routeID){
        this.EntryRouteID = routeID;
    }
    public Eloop getEloop(){
        return this.EloopProperties;
    }
    public String getTerminal(){
        return this.LastEnteredTerminal;
    }
    public Double getDistance(){
       return this.TotalDistanceFromUser;
    }
    public Integer getOrderOfArrivalDifference(){
        return this.OrderOfArrivalDifference;
    }
    public String getPossibleRouteIDs(){
       return this.PossibleRouteIDs;
    }
    public String getEntryRouteID(){
        return this.EntryRouteID;
    }
    public Terminal getTerminalObject(){
        return this.TerminalObject;
    }
    public ArrayList<Terminal> getTargetDestinationSpecimen(){ return this.TargetDestinationSpecimen;
    }
    @Override
    public int compareTo(@NonNull VehicleProperties vehicleProperties) {
        return 0;
    }
    public static class VehicleComparators {
        public static Comparator<VehicleProperties> BASED_FROM_DISTANCE_ASC = new Comparator<VehicleProperties>() {
            @Override
            public int compare(VehicleProperties VP_1, VehicleProperties VP_2) {
                return Double.compare(VP_1.TotalDistanceFromUser, VP_2.TotalDistanceFromUser);
            }

        };
        public static Comparator<VehicleProperties> BASED_FROM_ORDEROFARRIVAL_ASC = new Comparator<VehicleProperties>() {
            @Override
            public int compare(VehicleProperties VP_1, VehicleProperties VP_2) {
                return VP_1.OrderOfArrivalDifference - VP_2.OrderOfArrivalDifference;
            }

        };
        public static Comparator<VehicleProperties> BASED_FROM_ROUTEID_ASC = new Comparator<VehicleProperties>() {
            @Override
            public int compare(VehicleProperties VP_1, VehicleProperties VP_2) {
                return VP_1.TerminalObject.getTblRouteID() - VP_2.TerminalObject.getTblRouteID();
            }

        };
    }
}
