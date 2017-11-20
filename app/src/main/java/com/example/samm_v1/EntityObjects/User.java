package com.example.samm_v1.EntityObjects;

import java.util.Date;

/**
 * Created by MeadRoseAnn on 10/22/2017.
 */
//Firebase Entity

public class User {
    public String username;
    public String firstName;
    public String lastName;
    public String emailAddress;
    public double Latitude;
    public double Longitude;
    public Date lastUpdated;
    public String currentDestination;
    public User()
    {

    }
    public User(String username, String firstName, String lastName, String emailAddress)
    {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.Latitude = 0.0;
        this.Longitude = 0.0;
        this.lastUpdated = new Date();
        this.currentDestination = "";
    }
}
