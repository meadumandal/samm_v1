package com.umandalmead.samm_v1.EntityObjects;

import android.text.TextUtils;
import android.widget.Toast;

import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.InvalidUserDetailsException;
import com.umandalmead.samm_v1.R;

import static com.facebook.FacebookSdk.getApplicationContext;

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

    public static void validateRegistrationDetails(Users user) {

        if (user.username != null && (user.username.trim().isEmpty() || Helper.CheckForSpecialCharacters(user.username))) {

                throw new IllegalArgumentException("Username must not be empty and must not contain special characters");

        }
        if (user.emailAddress!=null && TextUtils.isEmpty(user.emailAddress.trim())) {

                throw new IllegalArgumentException("Email address should not be empty");

        }
        if(user.password!=null && TextUtils.isEmpty(user.password.trim()))
        {

                throw new IllegalArgumentException("Password should not be empty");

        }

        if(user.password!=null && user.password.trim().length() < 6)
        {

                throw new IllegalArgumentException("Password should be composed of 6 characters");

        }

        if (user.password!=null && user.confirmPassword!=null)
            if(!user.password.equals(user.confirmPassword))
            {

                    throw new IllegalArgumentException("Passwords do not match!");

            }
        if(user.firstName!=null && TextUtils.isEmpty(user.firstName.trim()))
        {
                throw new IllegalArgumentException("First name is required");

        }

        if(user.lastName !=null && TextUtils.isEmpty(user.lastName.trim()))
        {

                throw new IllegalArgumentException("Last name is required");

        }


    }

    @Override
    public String toString() {  return username; }



}
