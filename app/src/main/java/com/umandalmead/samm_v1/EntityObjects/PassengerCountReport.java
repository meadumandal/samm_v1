package com.umandalmead.samm_v1.EntityObjects;

/**
 * Created by MeadRoseAnn on 2/6/2018.
 */

public class PassengerCountReport {
    public int count;
    public int hour;

    public PassengerCountReport(int count, int hour)
    {
        this.count = count;
        this.hour = hour;
    }
}
