package com.umandalmead.samm_v1.EntityObjects;

/**
 * Created by MeadRoseAnn on 2/6/2018.
 */

public class PassengerCountReport {
    public int count;
    public int hour;
    //public String terminal;

    public PassengerCountReport(int count, int hour)//, String terminal)
    {
        this.count = count;
        this.hour = hour;
        //this.terminal = terminal;
    }
}
