package com.umandalmead.samm_v1.EntityObjects;

/**
 * Created by MeadRoseAnn on 6/27/2018.
 */

public class Routes {
    int ID;
    String routeName;
    public Routes(int ID, String routeName)
    {
        this.ID = ID;
        this.routeName = routeName;
    }
    public void setID(int ID)
    {
        this.ID = ID;
    }
    public int getID()
    {
        return this.ID;
    }
    public void setRouteName(String routeName)
    {
        this.routeName = routeName;
    }
    public String getRouteName()
    {
        return this.routeName;
    }
    @Override
    public String toString()
    {
        return this.routeName;
    }
}
