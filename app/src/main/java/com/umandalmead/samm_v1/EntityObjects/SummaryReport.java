package com.umandalmead.samm_v1.EntityObjects;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by MeadRoseAnn on 4/15/2018.
 */

public class SummaryReport implements Comparable<SummaryReport>{
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

    @Override
    public int compareTo(@NonNull SummaryReport summaryReport) {
        return 0;
    }

    public static class SummaryReportComparator {
        public static Comparator<SummaryReport> DISTANCE_TRAVELED = new Comparator<SummaryReport>() {
            @Override
            public int compare(SummaryReport SR_DistanceTraveled_1, SummaryReport SR_DistanceTraveled_2) {
                return  (int) Math.round(SR_DistanceTraveled_2.distance) - (int) Math.round(SR_DistanceTraveled_1.distance);
            }
        };
    }
}
