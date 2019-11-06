package com.evolve.evx.EntityObjects;

import android.text.TextUtils;

import com.evolve.evx.Helper;

/**
 * Created by MeadRoseAnn on 7/29/2018.
 */

public class Users {
    public Integer ID;
    public String username;
    public String emailAddress;
    public String firstName;
    public String lastName;
    public String userType;
    public String password;
    public String confirmPassword;
    public Integer tblLineID;
    public int IsActive;


    public Users(Integer ID, String username, String emailAddress, String firstName, String lastName, String userType, String password, int IsActive)
    {
        this.ID = ID;
        this.username = username;
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.password = password;
        this.IsActive = IsActive;
    }

    public Users(String username, String emailAddress, String firstName, String lastName, String password, String confirmPassword)
    {
        this.username = username;
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
    public Users(Integer ID, String username, String emailAddress, String firstName, String lastName, String userType, String password, int IsActive, int tblLineID)
    {
        this.ID = ID;
        this.username = username;
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.password = password;
        this.IsActive = IsActive;
        this.tblLineID = tblLineID;
    }


    public static void validateRegistrationDetails(Users user) {
        if(TextUtils.isEmpty(user.firstName.trim()) || Helper.HasSpecialCharacters(user.firstName.trim()))
        {
            throw new IllegalArgumentException("First name is required and must not contain special characters");

        }

        if(TextUtils.isEmpty(user.lastName.trim()) || Helper.HasSpecialCharacters(user.lastName.trim()))
        {

            throw new IllegalArgumentException("Last name is required and must not contain special characters");

        }
        if (user.username.trim().isEmpty() || Helper.HasSpecialCharacters(user.username) || user.username.contains(" ")) {

                throw new IllegalArgumentException("Username must not be empty and must not contain special characters and spaces");

        }
        if (TextUtils.isEmpty(user.emailAddress.trim())) {

                throw new IllegalArgumentException("Email address should not be empty");

        }
        if (user.password !=null)
        {
            if(TextUtils.isEmpty(user.password.trim()))
            {

                throw new IllegalArgumentException("Password should not be empty");

            }

            if(user.password.trim().length() < 6)
            {

                throw new IllegalArgumentException("Password should be composed of 6 characters");

            }

        }

        if (user.confirmPassword!=null)
            if(!user.password.equals(user.confirmPassword))
            {

                    throw new IllegalArgumentException("Passwords do not match!");

            }



    }

    @Override
    public String toString() {  return username; }



}
