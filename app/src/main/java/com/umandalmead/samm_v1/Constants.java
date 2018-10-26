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
    public static final String WEB_API_URL = "http://samm.website/sammAPI/";
    public static final String DESTINATIONS_API_FOLDER = "destinations/";
    public static final String DESTINATIONS_API_FILE = "getDestinations.php?";
    public static final String DESTINATIONS_API_DELETE_FILE_WITH_PENDING_QUERYSTRING = "deleteDestination.php?";
    public static final String SAVE_DESTINATIONS_ORDER_WITH_PENDING_QUERYSTRNG = "saveDestinationsOrder.php?";
    public static final String DEVICES_API_FOLDER = "devices/";
    public static final String ELOOPS_API_FOLDER = "eloops/";
    public static final String ELOOPS_API_FILE = "getEloops.php";
    public static final String POINTS_API_FOLDER = "points/";
    public static final String REPORTGENERATORS_API_FOLDER = "reportgenerators/";
    public static final String REPORTS_API_FOLDER = "reports/";
    public static final String ROUTES_API_FOLDER = "routes/";
    public static final String LINE_API_FOLDER = "lines/";
    public static final String ROUTES_API_DELETE_FILE_WITH_PENDING_QUERYSTRING = "deleteRoute.php?";
    public static final String PASSENGERQUEUINGHISTORY_API_FILE_WITH_PENDING_QUERYSTRING = "getPassengerQueueingHistory.php?";
    public static final String TERMINALBUSIESTTIME_API_FILE_WITH_PENDING_QUERYSTRING ="getTerminalBusiestTimes.php?";
    public static final String ROUTE_UPDATE_API_FILE = "updateRoute.php";
    public static final String ADD_ROUTES_API_FILE = "addRoute.php";
    public static final String GET_ROUTES_API_FILE = "getRoutes.php";
    public static final String SETTINGS_API_FOLDER = "settings/";
    public static final String USERS_API_FOLDER = "users/";
    public static final String USERS_UPDATE_API_FILE_WITH_PENDING_QUERYSTRING = "updateUserDetails.php?";
    public static final String USERS_API_DELETE_FILE_WITH_PENDING_QUERYSTRING = "deleteUser.php?";
    public static final String SIGNUP_API_FILE_WITH_PENDING_QUERYSTRING = "signUp.php?";
    public static final String UPDATE_PASSENGER_COUNTER_WITH_PENDING_QUERYSTRING = "updatePassengerCounter.php?";
    public static final String ADMIN_API_FOLDER = "users/admin/";
    public static final String DRIVER_API_FOLDER = "users/driver/";
    public static final String GUEST_USERNAME_PREFIX = "guestuser";
    public static final String GUEST_FIRSTNAME = "Guest";
    public static final String GUEST_LASTNAME = "User";
    public static final String APP_TITLE = "SAMM";
    public static final String DRIVER_EMAILADDRESS="driver@samm.com";
    public static final String DRIVER_PASSWORD = "sammdriver";
    public static final String ADMIN_EMAILADDRESS = "admin@samm.com";
    public static final String ADMIN_PASSWORD = "sammadmin";
    public static final String ADMIN_USERTYPE = "Administrator";
    public static final String DRIVER_USERTYPE = "Driver";
    public static final String PASSENGER_USERTYPE = "Passenger";
    public static final String SUPERADMIN_USERTYPE = "SuperAdministrator";
    public static final String GUEST_USERTYPE = "GUEST";

    public static final String PASSENGER_REPORT_TYPE = "passenger";
    public static final String VEHICLE_REPORT_TYPE = "ecoloop";
    public static final int APPBAR_MIN_HEIGHT = 0;
    public static final String GOOGLE_API_URL = "https://maps.googleapis.com/maps/";
    public static final String DESTINATION_PREFIX = "Going to: ";
    public static final String ROUTES_BASEURI= "file:///android_res/";
    public static final String ROUTES_MIMETYPE = "text/html; charset=utf-8";
    public static final String ROUTES_ENCODING = "UTF-8";

    public static final String VEHICLE_ALREADY_WAITING_CONTAINS = "already";
    public static final String VEHICLE_SEARCHING_CONTAINS = "searching";
    public static final String VEHICLE_REMAINING_TIME_SUFFIX = " away";

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

