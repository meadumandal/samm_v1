package com.evolve.evx.EntityObjects;

/**
 * Created by MeadRoseAnn on 4/15/2018.
 */

public class Eloop {
    public int ID;
    public int DeviceId;
    public String DeviceName;
    public String PlateNumber;
    public int tblUsersID;
    public int tblRoutesID;
    public int IsActive;
    public String DriverName;
    public int tblLinesID;

    public Eloop()
    {

    }
    public Eloop(int ID,
                 int DeviceId,
                 String DeviceName,
                 String PlateNumber,
                 int tblUsersID,
                 int tblRoutesID,
                 int IsActive,
                 String driverName,
                 int tblLinesID)
    {
        this.ID = ID;
        this.DeviceId = DeviceId;
        this.DeviceName = DeviceName;
        this.PlateNumber = PlateNumber;
        this.tblUsersID = tblUsersID;
        this.tblRoutesID = tblRoutesID;
        this.IsActive = IsActive;
        this.DriverName = driverName;
        this.tblLinesID = tblLinesID;
    }
}
