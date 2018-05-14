package com.umandalmead.samm_v1.EntityObjects;

/**
 * Created by MeadRoseAnn on 4/15/2018.
 */

public class Eloop {
    public int ID;
    public int DeviceId;
    public String DeviceName;
    public String PlateNumber;


    public Eloop(int ID, int DeviceId,String DeviceName, String PlateNumber)
    {
        this.ID = ID;
        this.DeviceId = DeviceId;
        this.DeviceName = DeviceName;
        this.PlateNumber = PlateNumber;
    }
}
