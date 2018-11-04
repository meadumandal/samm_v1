package com.umandalmead.samm_v1.POJO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MeadRoseAnn on 10/22/2017.
 */
//Firebase Entity

public class UserPOJO {
    @SerializedName("username")
    public String username;

    @SerializedName("emailAddress")
    public String emailAddress;

    @SerializedName("firstName")
    public String firstName;

    @SerializedName("lastName")
    public String lastName;

    @SerializedName("deviceId")
    public String deviceId;

    @SerializedName("userType")
    public String userType;

    @SerializedName("authType")
    public String authType;

    @SerializedName("password")
    public String password;

    @SerializedName("IsActive")
    public String IsActive;

    @SerializedName("UserID")
    public Integer UserID;

    public String getUsername() { return this.username; }

    public Integer getUserID() { return this.UserID; }

    public String getFirstName() { return this.firstName; }

    public String getLastName() { return this.lastName; }

    public String getEmailAddress(){ return this.emailAddress; }

    public String getDeviceId() {return this.deviceId;}

    public String getUserType(){ return this.userType; }

    public String getAuthType(){ return this.authType; }

    public String getPassword(){ return this.password; }

    public String getIsActive() { return this.IsActive; }

    public void setUsername(String username){this.username = username;}

    public void setUserID(Integer userID) {this.UserID = userID;}

    public void setFirstName(String firstName){this.firstName = firstName;}

    public void setLastName(String lastName){this.lastName = lastName;}

    public void setEmailAddress(String emailAddress){this.emailAddress = emailAddress;}

    public void setDeviceId(String deviceId) {this.deviceId = deviceId;}

    public void setUserType(String userType) {this.userType = userType;}

    public void setAuthType(String authType) {this.authType = authType;}

    public void setPassword(String password) {this.password = password;}

    public void setIsActive(String IsActive) {this.IsActive = IsActive;}
}
