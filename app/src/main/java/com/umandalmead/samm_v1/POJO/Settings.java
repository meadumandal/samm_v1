package com.umandalmead.samm_v1.POJO;

import android.content.pm.PackageManager;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by MeadRoseAnn on 2/24/2018.
 */

public class Settings {

    @SerializedName("settings")
    List<Setting> setting;

    public List<Setting> getSetting()
    {
        return this.setting;
    }
    public void setSetting(List<Setting> setting)
    {
        this.setting = setting;
    }

}
