package com.umandalmead.samm_v1.EntityObjects;

/**
 * Created by MeadRoseAnn on 4/15/2018.
 */

public class Eloop {
    public int ID;
    public int DeviceId;
    public String PlateNumber;

    public Eloop(int ID, int DeviceId, String PlateNumber)
    {
        this.ID = ID;
        this.DeviceId = DeviceId;
        this.PlateNumber = PlateNumber;
    }
}
