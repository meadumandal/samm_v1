package com.umandalmead.samm_v1.EntityObjects;

/**
 * Created by MeadRoseAnn on 2/17/2018.
 */

public class GPS {
    String GPSName;
    String GPSIMEI;
    String GPSPhone;
    String GPSNetworkProvider;
    String GPSPlateNo;
    Integer ID;

    public GPS(Integer ID,String gpsName, String gpsIMEI, String gpsPhone, String gpsNetworkProvider)
    {
        this.ID = ID;
        this.GPSName = gpsName;
        this.GPSIMEI = gpsIMEI;
        this.GPSPhone = gpsPhone;
        this.GPSNetworkProvider = gpsNetworkProvider;


    }
    public String getGPSName()
    {
        return this.GPSName;
    }
    public String getGPSIMEI()
    {
        return this.GPSIMEI;
    }
    public String getGPSPhone()
    {
        return this.GPSPhone;
    }
    public String getGPSNetworkProvider()
    {
        return this.GPSNetworkProvider;
    }

}
