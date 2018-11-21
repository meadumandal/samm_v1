package com.umandalmead.samm_v1;

import com.umandalmead.samm_v1.POJO.HTMLDirections.Settings;
import com.umandalmead.samm_v1.POJO.UserPOJO;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by MeadRoseAnn on 11/15/2017.
 */

public interface RetrofitDatabase {
    @GET("users/getUserDetails.php")
    Call<UserPOJO> getUserDetails(@Query("username") String username, @Query("emailAddress") String emailAddress);

    @GET("settings/getSettings.php")
    Call<Settings> getSettings();
}
