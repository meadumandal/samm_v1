package com.umandalmead.samm_v1;

/**
 * Created by MeadRoseAnn on 3/25/2018.
 */

public class Constants {
    //Put here all Constant Variables
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static final int MY_PERMISSION_REQUEST_LOCATION=99;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    public static final String LOG_TAG ="Custom_Log";
    public static final String WEB_API_URL = "http://meadumandal.website/sammAPI/";
    public static final String GUEST_USERNAME_PREFIX = "guestuser";
    public static final String GUEST_FIRSTNAME = "Guest";
    public static final String GUEST_LASTNAME = "User";
    public static final String APP_TITLE = "SAMM";
    public static final String DRIVER_EMAILADDRESS="sammdriver@yahoo.com";
    public static final String ADMIN_EMAILADDRESS = "admin@yahoo.com";
    public static final String PASSENGER_REPORT_TYPE = "passenger";
    public static final String VEHICLE_REPORT_TYPE = "ecoloop";
    public static final int APPBAR_MIN_HEIGHT = 156;
    public static final int APPBAR_MAX_HEIGHT = 235;
    public static final String GOOGLE_API_URL = "https://maps.googleapis.com/maps/";
    public static final String DESTINATION_PREFIX = "Going to: ";

    public static final String GPS_PASSWORD = "123456";
    public static final String GLOBE_APN = "http.globe.com.ph";
    public static final String SMART_APN = "internet";
    public static final String TRACCAR_SERVER = "http://demo4.traccar.org/";
    public static final String TRACCAR_USERNAME="meadumandal@yahoo.com";
    public static final String TRACCAR_PASSWORD="password";
    public static final String TRACCAR_PORT = "5002";

    public static final String SMS_BEGIN = "begin" + GPS_PASSWORD;
    public static final String SMS_GPRS = "gprs" + GPS_PASSWORD;
    public static final String SMS_APN_GLOBE  = "apn" + GPS_PASSWORD + " " + GLOBE_APN;
    public static final String SMS_APN_SMART  = "apn" + GPS_PASSWORD + " " + SMART_APN;
    public static final String SMS_ADMINIP = "adminip" + GPS_PASSWORD + " " + TRACCAR_SERVER + " " + TRACCAR_PORT;
    public static final String SMS_TIMEINTERVAL = "t050s***n" + GPS_PASSWORD;
}
