package com.evolve.evx.EntityObjects;

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
    String Status;

    public GPS(Integer ID,String gpsName, String gpsIMEI, String gpsPhone, String gpsNetworkProvider, String status, String plateNo)
    {
        this.ID = ID;
        this.GPSName = gpsName;
        this.GPSIMEI = gpsIMEI;
        this.GPSPhone = gpsPhone;
        this.GPSNetworkProvider = gpsNetworkProvider;
        this.Status = status;
        this.GPSPlateNo = plateNo;
    }

    public void setStatus (String status)
    {
        this.Status = status;
    }
    public void setGPSName(String GPSName)
    {
        this.GPSName = GPSName;
    }
    public void setGPSIMEI(String GPSIMEI)
    {
        this.GPSIMEI = GPSIMEI;
    }
    public void setGPSPhone(String GPSPhone)
    {
        this.GPSPhone = GPSPhone;
    }
    public void setGPSNetworkProvider(String GPSNetworkProvider) { this.GPSNetworkProvider = GPSNetworkProvider; }
    public void setGPSPlateNo(String GPSPlateNo){ this.GPSPlateNo = GPSPlateNo; }

    public String getStatus()
    {
        return this.Status;
    }
    public String getGPSName()
    {
        return this.GPSName.trim();
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
    public String getGPSPlateNo(){return this.GPSPlateNo;}
    public Integer getID()
    {
        return this.ID;
    }



}
