package com.umandalmead.samm_v1.EntityObjects;

/**
 * Created by MeadRoseAnn on 6/27/2018.
 */

public class Routes {
    int ID;
    int tblLineID;
    String routeName;
    int noOfStations;
    public Routes(int ID, int tblLineID, String routeName, int noOfStations)
    {
        this.ID = ID;
        this.tblLineID = tblLineID;
        this.routeName = routeName;
        this.noOfStations = noOfStations;
    }
    public void setID(int ID)
    {
        this.ID = ID;
    }
    public int getID()
    {
        return this.ID;
    }
    public void setTblLineID(int tblLineID){this.tblLineID = tblLineID;}
    public int getTblLineID(){return this.tblLineID;}
    public void setRouteName(String routeName)
    {
        this.routeName = routeName;
    }
    public String getRouteName()
    {
        return this.routeName;
    }
    public int getNoOfStations(){ return this.noOfStations;}
    public void setNoOfStations(int noOfStations) {this.noOfStations = noOfStations;}

    @Override
    public String toString()
    {
        return this.routeName;
    }
}
