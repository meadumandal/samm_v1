package com.umandalmead.samm_v1.POJO.HTMLDirections;

/**
 * Created by MeadRoseAnn on 11/15/2017.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Directions {

    @SerializedName("routes")
    @Expose
    private List<Route> routes = new ArrayList<>();


    /**
     *
     * @return
     * The routes
     */
    public List<Route> getRoutes() {
        return routes;
    }

    /**
     *
     * @param routes
     * The routes
     */
    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

}