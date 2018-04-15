package com.umandalmead.samm_v1.EntityObjects;

/**
 * Created by MeadRoseAnn on 4/15/2018.
 */

public class SummaryReport {
    public int deviceId;
    public String deviceName;
    public String plateNumber;
    public double distance;
    public double averageSpeed;
    public double maxSpeed;
    public double spentFuel;
    public int engineHours;

    public SummaryReport(int deviceId, String deviceName, String plateNumber, double distance, double averageSpeed, double maxSpeed, double spentFuel, int engineHours)
    {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.plateNumber = plateNumber;
        this.distance = distance;
        this.averageSpeed = averageSpeed;
        this.maxSpeed = maxSpeed;
        this.spentFuel = spentFuel;
        this.engineHours = engineHours;
    }
}
