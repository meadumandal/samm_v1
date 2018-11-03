package com.umandalmead.samm_v1.POJO.HTMLDirections;

import com.google.gson.annotations.SerializedName;

/**
 * Created by MeadRoseAnn on 2/24/2018.
 */

public class Setting {
    @SerializedName("ID")
    public int ID;

    @SerializedName("Name")
    public String Name;

    @SerializedName("Value")
    public String Value;

    @SerializedName("Description")
    public String Description;

    public int getID()
    {
        return this.ID;
    }
    public String getName()
    {
        return this.Name;
    }
    public String getValue()
    {
        return this.Value;
    }
    public String getDescription()
    {
        return this.Description;
    }


    public void setID(int ID)
    {
        this.ID = ID;
    }
    public void setName(String Name)
    {
        this.Name =Name;
    }
    public void setValue(String Value)
    {
        this.Value = Value;
    }
    public void setDescription(String Description)
    {
        this.Description = Description;
    }

}
