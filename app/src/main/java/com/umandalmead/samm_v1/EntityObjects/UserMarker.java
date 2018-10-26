package com.umandalmead.samm_v1.EntityObjects;

import android.content.Context;

import com.umandalmead.samm_v1.Constants;
import com.umandalmead.samm_v1.Enums;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.SessionManager;

import static com.umandalmead.samm_v1.ReportsActivity._sessionManager;

/**
 * Created by eleazerarcilla on 26/10/2018.
 */

public class UserMarker {
    public Enums.UserType UserType;
    public String UserTitle;
    public int UserIcon, UserInfoLayoutIcon;
    private SessionManager _sessionManager;
    public UserMarker(String markerTitle, Context context){
        this._sessionManager = new SessionManager(context);
        if(markerTitle.contains("guest")) {
            this.UserTitle="SAMMER";
            this.UserIcon = R.drawable.samm_user_map_icon;
            this.UserType = Enums.UserType.SAMM_DEFAULT;
            this.UserInfoLayoutIcon = R.drawable.samm_user_icon_info_layout_default;
        }
        else if(markerTitle.matches("\\d+")) {
            this.UserTitle= "Please wait...";
            this.UserIcon = R.drawable.samm_user_map_icon_fb;
            this.UserType = Enums.UserType.SAMM_FACEBOOK;
            this.UserInfoLayoutIcon = R.drawable.samm_user_icon_info_layout_facebook;
        }
        else{
            this.UserTitle=markerTitle;
            this.UserIcon = R.drawable.samm_user_map_icon_admin;
            this.UserType = Enums.UserType.SAMM_ADMINISTRATOR;
            this.UserInfoLayoutIcon = R.drawable.samm_user_icon_info_layout_admin;
        }
    }
}
