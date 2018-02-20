package com.umandalmead.samm_v1;

import com.umandalmead.samm_v1.POJO.UserPOJO;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by MeadRoseAnn on 11/15/2017.
 */

public interface RetrofitUserDetails {
    @GET("getUserDetails.php")
    Call<UserPOJO> getUserDetails(@Query("username") String username, @Query("emailAddress") String emailAddress);
}
