package com.example.samm_v1;

import com.example.samm_v1.POJO.Example;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by MeadRoseAnn on 11/15/2017.
 */

public interface RetrofitMaps {
    @GET("api/directions/json?key=AIzaSyA5hO5foH5lUUrBygspvTtNH5mwg-EB1o4")
    Call<Example> getDistanceDuration(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);
}
