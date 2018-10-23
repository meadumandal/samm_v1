package com.umandalmead.samm_v1.EntityObjects;

/**
 * Created by MeadRoseAnn on 10/17/2018.
 */

public class Lines {
    int ID;
    String Name;
    int Admin_User_ID;
    String AdminUserName;

    public Lines(int ID, String Name, int Admin_User_ID, String AdminUserName)
    {
        this.ID = ID;
        this.Name = Name;
        this.Admin_User_ID = Admin_User_ID;
        this.AdminUserName = AdminUserName;
    }

    public int getID() {
        return this.ID;
    }
    public String getName() {
        return this.Name;
    }
    public int getAdmin_User_ID()
    {
        return this.Admin_User_ID;
    }
    public String getAdminUserName() {return this.AdminUserName;}

    public void setID(int ID)
    {
        this.ID = ID;
    }
    public void setName(String name)
    {
        this.Name = name;
    }
    public void setAdmin_User_ID(int admin_user_id)
    {
        this.Admin_User_ID = admin_user_id;
    }
    public void setAdminUserName(String adminUserName){this.AdminUserName = adminUserName;}


}
