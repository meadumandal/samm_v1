package com.example.samm_v1.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by MeadRoseAnn on 10/22/2017.
 */
//Firebase Entity

public class UserPOJO {
    @SerializedName("username")
    public String username;

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("emailAddress")
    public String emailAddress;




    public String getUsername() { return this.username; }

    public String getFirstName() { return this.firstName; }

    public String getLastName() { return this.lastName; }

    public String getEmailAddress(){ return this.emailAddress; }

    public void setUsername(String username){this.username = username;}

    public void setFirstName(String firstName){this.firstName = firstName;}

    public void setLastName(String lastName){this.lastName = lastName;}

    public void setEmailAddress(String emailAddress){this.emailAddress = emailAddress;}


}
