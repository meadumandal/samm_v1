package com.evolve.evx.EntityObjects;

import android.content.Context;

import com.evolve.evx.Enums;
import com.evolve.evx.Helper;
import com.evolve.evx.R;
import com.evolve.evx.SessionManager;

/**
 * Created by eleazerarcilla on 26/10/2018.
 */

public class UserMarker {
    public Enums.UserType UserType;
    public String UserTitle;
    public int UserIcon, UserInfoLayoutIcon;
    private SessionManager _sessionManager;
    public UserMarker(String STR_UserType, Context context){
        try {
            if (STR_UserType != null) {
                Enums.UserType UserType = Enums.UserType.valueOf(STR_UserType);
                this._sessionManager = new SessionManager(context);
                switch (UserType) {
                    case EVX_SUPERADMIN:
                        this.UserTitle = "EVX Super Administrator";
                        this.UserIcon = R.drawable.evxuser_admin_mapicon;
                        this.UserType = Enums.UserType.EVX_SUPERADMIN;
                        this.UserInfoLayoutIcon = R.drawable.evxuser_admin;
                        break;
                    case EVX_DRIVER:
                        this.UserTitle = "EVX DRIVER";
                        this.UserIcon = R.drawable.evxuser_registered_mapicon;
                        this.UserType = Enums.UserType.EVX_DEFAULT_REGISTERED;
                        this.UserInfoLayoutIcon = R.drawable.evxuser_registered;
                        break;
                    case EVX_FACEBOOK:
                        this.UserTitle = "Please wait...";
                        this.UserIcon = R.drawable.evxuser_fb_mapicon;
                        this.UserType = Enums.UserType.EVX_FACEBOOK;
                        this.UserInfoLayoutIcon = R.drawable.evxuse_fb;
                        break;
                    case EVX_DEFAULT_REGISTERED:
                        this.UserTitle = "EVX User";
                        this.UserIcon = R.drawable.evxuser_registered_mapicon;
                        this.UserType = Enums.UserType.EVX_DEFAULT_REGISTERED;
                        this.UserInfoLayoutIcon = R.drawable.evxuser_registered;
                        break;
                    case EVX_DEFAULT:
                        this.UserTitle = "EVX Guest";
                        this.UserIcon = R.drawable.evxuser_default_mapicon;
                        this.UserType = Enums.UserType.EVX_DEFAULT;
                        this.UserInfoLayoutIcon = R.drawable.evxuser_default;
                        break;
                    case EVX_ADMINISTRATOR:
                        this.UserTitle = "EVX Administrator";
                        this.UserIcon = R.drawable.evxuser_admin_mapicon;
                        this.UserType = Enums.UserType.EVX_ADMINISTRATOR;
                        this.UserInfoLayoutIcon = R.drawable.evxuser_admin;
                        break;
                }
//                if (marker.contains("guest")) {
//                    this.UserTitle = "SAMMER";
//                    this.UserIcon = R.drawable.samm_user_map_icon;
//                    this.UserType = Enums.UserType.SAMM_DEFAULT;
//                    this.UserInfoLayoutIcon = R.drawable.samm_user_icon_info_layout_default;
//                } else if (marker.matches("\\d+")) {
//                    this.UserTitle = "Please wait...";
//                    this.UserIcon = R.drawable.samm_user_map_icon_fb;
//                    this.UserType = Enums.UserType.SAMM_FACEBOOK;
//                    this.UserInfoLayoutIcon = R.drawable.samm_user_icon_info_layout_facebook;
//                } else {
//                    //if email address is present
//                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(marker).matches()) {
//                        this.UserTitle = "SAMMER";
//                        this.UserIcon = R.drawable.samm_user_map_icon_samm_registered;
//                        this.UserType = Enums.UserType.SAMM_DEFAULT_REGISTERED;
//                        this.UserInfoLayoutIcon = R.drawable.samm_user_icon_info_layout_sammer_registered;
//                    } else {
//                        //admin
//                        this.UserTitle = marker;
//                        this.UserIcon = R.drawable.samm_user_map_icon_admin;
//                        this.UserType = Enums.UserType.SAMM_ADMINISTRATOR;
//                        this.UserInfoLayoutIcon = R.drawable.samm_user_icon_info_layout_admin;
//                    }
//                }
//            } else {
//                //admin catch
//                this.UserTitle = marker;
//                this.UserIcon = R.drawable.samm_user_map_icon_admin;
//                this.UserType = Enums.UserType.SAMM_ADMINISTRATOR;
//                this.UserInfoLayoutIcon = R.drawable.samm_user_icon_info_layout_admin;
//            }
            }
        }catch (Exception ex){
            Helper.logger(ex);
        }
    }
}
