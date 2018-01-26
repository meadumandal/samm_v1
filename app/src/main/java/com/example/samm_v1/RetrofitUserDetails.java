package com.example.samm_v1;

import com.example.samm_v1.EntityObjects.User;
import com.example.samm_v1.POJO.Directions;
import com.example.samm_v1.POJO.UserPOJO;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by MeadRoseAnn on 11/15/2017.
 */

public interface RetrofitUserDetails {
    @GET("getUserDetails.php")
    Call<UserPOJO> getUserDetails(@Query("username") String username);
}
