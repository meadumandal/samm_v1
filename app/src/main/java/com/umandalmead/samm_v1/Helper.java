package com.umandalmead.samm_v1;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Property;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.os.Handler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umandalmead.samm_v1.EntityObjects.Eloop;
import com.umandalmead.samm_v1.EntityObjects.GPS;
import com.umandalmead.samm_v1.EntityObjects.Lines;
import com.umandalmead.samm_v1.EntityObjects.Routes;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.EntityObjects.UserMarker;
import com.umandalmead.samm_v1.EntityObjects.Users;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.SaveCurrentDestination;
import com.umandalmead.samm_v1.Modules.ManageLines.LineViewCustomAdapter;
import com.umandalmead.samm_v1.Modules.ManageLines.ManageLinesFragment;
import com.umandalmead.samm_v1.Modules.ManageRoutes.ManageRoutesFragment;
import com.umandalmead.samm_v1.Modules.ManageRoutes.RouteViewCustomAdapter;
import com.umandalmead.samm_v1.Modules.ManageStations.ManageStationsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;
import static com.umandalmead.samm_v1.MenuActivity._HasExitedInfoLayout;
import static com.umandalmead.samm_v1.MenuActivity._LL_Arrival_Info;
import static com.umandalmead.samm_v1.MenuActivity._LoopArrivalProgress;
import static com.umandalmead.samm_v1.MenuActivity._TV_TimeofArrival;
import static com.umandalmead.samm_v1.MenuActivity._TV_Vehicle_Description;
import static com.umandalmead.samm_v1.MenuActivity._TV_Vehicle_Identifier;
import static com.umandalmead.samm_v1.MenuActivity._TimeOfArrivalTextView;

/**
 * Created by MeadRoseAnn on 10/8/2017.
 */

public class Helper {
    /**
     * This converts a jsonString to HashMap
     * @param jsonString
     * @return
     * @throws JSONException
     */

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private String _smsMessage;
    private String _smsPhoneNo;

    public MenuActivity _activity;
    public Context _context;
    public SessionManager _sessionManager;
    private static Resources _GlobalResource;
    public static Typeface FONT_PLATE,FONT_STATION, FONT_RUBIK_REGULAR, FONT_RUBIK_BOLD, FONT_RUBIK_MEDIUM, FONT_ROBOTO_CONDENDSED_BOLD, FONT_RUBIK_BLACK;
    Integer _counter = 0;




    public Helper(Activity activity, Context context)
    {
        this._activity = (MenuActivity) activity;
        this._context = context;
        this._sessionManager = new SessionManager(_context);
        this._GlobalResource = _context.getResources();
        InitializeFonts(_context);
    }
    public Helper()
    {

    }

    public HashMap<String, Object> jsonToMap(String jsonString) throws JSONException
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        JSONObject jObject = new JSONObject(jsonString);
        Iterator<?> keys = jObject.keys();
        while(keys.hasNext())
        {
            String key = (String)keys.next();
            Object value = jObject.get(key);
            map.put(key,value);
        }
        return map;
    }

    /**
     * This checks if the connection service (WIFI or Mobile Data) is available.
     * This also checks if the device has internet connection by trying to get the IPAddress of google.com
     * @param context
     * @return
     */
    public boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityMngr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityMngr.getActiveNetworkInfo();
        if (activeNetwork != null && isInternetAvailable())
            return true;
        else
            return false;
    }

    /**
     * This checks if the device has internet connection by trying to get the IPAddress of google.com
     * @return true if there is an internet connection. Otherwise, false
     */
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return !ipAddr.equals("");

        } catch (Exception ex) {
            Helper.logger(ex,true);
            return false;
        }
    }

    /**
     * This method checks if the device has google play services installed before building Google API Client
     * @return true if google play services is installed
     */
    public Boolean isGooglePlayInstalled(Context context)
    {
        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return(resp == ConnectionResult.SUCCESS);
    }

    public void showNoInternetPrompt(Activity activity)
    {
        NoInternetDialog dialog=new NoInternetDialog(activity);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
    }
    public double getDistanceFromLatLonInKm(LatLng First, LatLng Second) {
        double R = 6371; // Radius of the earth in km
        double dLat = ToRadians(Second.latitude-First.latitude);  // deg2rad below
        double dLon = ToRadians(Second.longitude-First.longitude);
        double sinLat = Math.sin(dLat/2);
        double sinLon = Math.sin(dLon/2);

        double a =
                sinLat * sinLat +
                        Math.cos(ToRadians(First.latitude)) * Math.cos(ToRadians(Second.latitude)) *
                                sinLon * sinLon
            ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }

    public double ToRadians(double deg) {
        return deg * (Math.PI/180);
    }
    public double bearingBetweenLocations(Location PrevLoc, Location CurrLoc) {

        double PI = 3.14159;
        double lat1 = PrevLoc.getLatitude() * PI / 180;
        double long1 = PrevLoc.getLongitude() * PI / 180;
        double lat2 = CurrLoc.getLatitude() * PI / 180;
        double long2 = CurrLoc.getLongitude() * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }
//    public static boolean isOnline() {
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
//            int     exitValue = ipProcess.waitFor();
//            return (exitValue == 0);
//        }
//        catch (IOException e)          { e.printStackTrace(); }
//        catch (InterruptedException e) { e.printStackTrace(); }
//
//        return false;
//    }

    /**
     * This method gets the nearest "PICK-UP" points based on the chosen DROP-OFF point
     * @param dropOffPoint This is the chosen drop-off point
     */

    public static ArrayList<Terminal> GetAllDestinationRegardlessOfTheirTableRouteIds(Terminal dropOffPoint){
        ArrayList<Terminal> result = new ArrayList<Terminal>();
        for (Terminal dest:MenuActivity._terminalList) {
            if(dropOffPoint.getValue().equals(dest.getValue())){
                result.add(dest);
            }
        }
        return result;
    }

    public static Boolean IsSameRoute(Terminal TM_Terminal_1, Terminal TM_Terminal_2){
        Boolean BOOL_LOC_Result = false;
        if(TM_Terminal_1.getTblRouteID() == TM_Terminal_2.getTblRouteID())
            BOOL_LOC_Result =true;
        return BOOL_LOC_Result;
    }
    public static Boolean IsEloopWithinSameRouteID(final DataSnapshot DS_Drivers, final Terminal TM_CurrentDest, final String STR_LoopID){
        Boolean BOOL_Result = false;
        try{
            for (DataSnapshot DS_Entry: DS_Drivers.getChildren()) {
                if(DS_Entry.getKey().equals(STR_LoopID)) {
                    String S_LoopTblRoutes = DS_Entry.child("routeIDs").getValue().toString();
                    Integer INT_CurrentDestRouteID = TM_CurrentDest.getTblRouteID();
                    if (!S_LoopTblRoutes.equals("") && S_LoopTblRoutes.contains(INT_CurrentDestRouteID.toString())) {
                        BOOL_Result = true;
                        break;
                    }
                }
            }
        }catch (Exception ex){
            logger(ex);

        }
        return BOOL_Result;
    }
    public static String CleanEloopName(String STR_EloopName){
        String STR_LOC_Result = "";
        try{
            String[] ARR_STR_LOC_Loops = STR_EloopName.split(",");
            for (String entry: ARR_STR_LOC_Loops) {
                if(!entry.equals("")){
                    return entry;
                }
            }
            return STR_LOC_Result;

        }catch(Exception ex){
            logger(ex);

        }
        return STR_LOC_Result;
    }
    public static void InitializeSearchingRouteUI(Boolean BOOL_IsSearchingDone, Boolean BOOL_IsResultNegative, String STR_HTMLMessage,String STR_Distance, String STR_TimeRemaining,Context context){

        if(BOOL_IsSearchingDone) {
            _LoopArrivalProgress.setVisibility(View.INVISIBLE);
            if(!BOOL_IsResultNegative && STR_HTMLMessage != null && STR_Distance!= null && STR_TimeRemaining!=null){
                _LL_Arrival_Info.setVisibility(View.VISIBLE);
                MenuActivity._SL_Vehicle_ETA.startShimmerAnimation();
                _TV_Vehicle_Identifier.setText(STR_HTMLMessage);
                _TV_Vehicle_Description.setText(STR_Distance+ Constants.VEHICLE_REMAINING_TIME_SUFFIX);
                _TV_TimeofArrival.setTypeface(MenuActivity.FONT_RUBIK_REGULAR);
                _TV_TimeofArrival.setText(STR_TimeRemaining);
                _TimeOfArrivalTextView.setVisibility(View.INVISIBLE);
            }
            else if(STR_HTMLMessage !=null && STR_HTMLMessage.toLowerCase().contains(Constants.VEHICLE_ALREADY_WAITING_CONTAINS)){
                _LL_Arrival_Info.setVisibility(View.INVISIBLE);
                _TimeOfArrivalTextView.setVisibility(View.VISIBLE);
                _TimeOfArrivalTextView.setBackgroundResource(R.drawable.pill_shaped_eloop_status);
                _TimeOfArrivalTextView.setTypeface(MenuActivity.FONT_RUBIK_BOLD);
                _TimeOfArrivalTextView.setText(Html.fromHtml(STR_HTMLMessage));
                //MenuActivity._SlideUpPanelContainer.setPanelHeight(dpToPx(60,context));
            }
            else if(BOOL_IsResultNegative){
                _LL_Arrival_Info.setVisibility(View.INVISIBLE);
                _TimeOfArrivalTextView.setVisibility(View.VISIBLE);
                _TimeOfArrivalTextView.setBackgroundResource(R.drawable.pill_shaped_eloop_status_error);
                _TimeOfArrivalTextView.setTypeface(MenuActivity.FONT_RUBIK_REGULAR);
                _TimeOfArrivalTextView.setText(Html.fromHtml(STR_HTMLMessage));
                //MenuActivity._SlideUpPanelContainer.setPanelHeight(dpToPx(60,context));
            }
            else{
                _LL_Arrival_Info.setVisibility(View.INVISIBLE);
                _TimeOfArrivalTextView.setBackgroundResource(0);
                _TimeOfArrivalTextView.setText(null);
                MenuActivity._SlideUpPanelContainer.setPanelHeight(dpToPx(60,context));
            }
        }
        else {
            _LoopArrivalProgress.setVisibility(View.VISIBLE);
            if(STR_HTMLMessage != null && STR_HTMLMessage.toLowerCase().contains(Constants.VEHICLE_SEARCHING_CONTAINS)){
                _LL_Arrival_Info.setVisibility(View.INVISIBLE);
                _TimeOfArrivalTextView.setVisibility(View.VISIBLE);
                _TimeOfArrivalTextView.setBackgroundResource(R.drawable.pill_shaped_eloop_status);
                _TimeOfArrivalTextView.setTypeface(MenuActivity.FONT_RUBIK_REGULAR);
                _TimeOfArrivalTextView.setText(Html.fromHtml(STR_HTMLMessage));
                //MenuActivity._SlideUpPanelContainer.setPanelHeight(dpToPx(60,context));

            }
            else{
                _LL_Arrival_Info.setVisibility(View.INVISIBLE);
                _TimeOfArrivalTextView.setBackgroundResource(0);
                _TimeOfArrivalTextView.setText(null);
                //MenuActivity._SlideUpPanelContainer.setPanelHeight(dpToPx(60,context));

            }
        }


    }
    public static List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
//    public Boolean FindPossiblePickUpPoints(Terminal dropOffPoint, Activity activity, Context context) {
//        if(isOnline(activity,context))
//        {
//           // ArrayList<Terminal> dropOffPointList = GetAllDestinationRegardlessOfTheirTableRouteIds(dropOffPoint);
//            Terminal chosenTerminal =dropOffPoint;
//            saveDestination(chosenTerminal.Value);
//
//            (this._activity)._possiblePickUpPointList = new ArrayList<>();
//            for(Terminal terminal : (this._activity)._terminalList)
//            {
//                if (terminal.Direction.equals(chosenTerminal.Direction) && terminal.tblRouteID == chosenTerminal.tblRouteID)
//                {
//                    if(terminal.OrderOfArrival < chosenTerminal.OrderOfArrival)
//                        (this._activity)._possiblePickUpPointList.add(terminal);
//                }
//            }
//            return (this._activity)._possiblePickUpPointList.size() <= 0;
//        }
//        else
//        {
//            return false;
//        }
//
//    }
    public static Users GetEloopDriver(Eloop eloopData){
        try {
            if(MenuActivity._driverList.size() > 0) {
                for (Users driver : MenuActivity._driverList) {
                    if (driver.ID == eloopData.tblUsersID) {
                        return driver;
                    }
                }
            }
        }catch (Exception ex){
            Helper.logger(ex);
        }
        return null;
    }
    public static boolean isOnline(Activity activity, Context context) {
        Boolean IsConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            IsConnected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        } else {
            IsConnected = false;
        }
        return IsConnected;
    }
    private void saveDestination(String destinationValue)
    {
        final HashMap<String, Object> currentDestination = new HashMap<>();
        currentDestination.put("currentDestination", destinationValue);

        (this._activity)._usersDBRef
                .child(_sessionManager.getUsername())
                .child("currentDestination")
                .addListenerForSingleValueEvent(
                        new SaveCurrentDestination(this._activity, this._context, currentDestination));
    }
    public String GenerateNavigationDrawerTooltip(){
        String STR_result = "Guest";
        if(_sessionManager!=null){
            if(_sessionManager.isDriver())
                STR_result = "Driver";
            else if(_sessionManager.getIsAdmin())
                STR_result ="Admin";
            else if(_sessionManager.getIsSuperAdmin())
                STR_result="Super Admin";
            else if(_sessionManager.isLoggedIn()|| _sessionManager.isFacebook())
                STR_result = "User";

        }
        return  STR_result;
    }
    public static int dpToPx(float dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float fpixels = metrics.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        return pixels;
    }
    public static void logger(Exception ex)
    {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        Log.e(LOG_TAG, "StackTrace: " + sw.toString() + " | Message: " + ex.getMessage());
    }
    public static void logger(Exception ex, Boolean sendErrorReport)
    {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        Log.e(LOG_TAG, "StackTrace: " + sw.toString() + " | Message: " + ex.getMessage());
        //new mySQLSendErrorReport().execute(ex.getMessage());
    }
    public static void logger(String ex)
    {
        StringWriter sw = new StringWriter();

        Log.e(LOG_TAG, ex);
    }
     public static Eloop GetEloopEntry(String vehicle_ID){
        Eloop _result = new Eloop();
        for (Eloop e: MenuActivity._eloopList) {
            if(e.DeviceId == Integer.parseInt(vehicle_ID)){
                _result = e;
                break;
            }
        }
        if(_result.PlateNumber==null)
            _result.PlateNumber="Filinvest E-loop";
        if(_result.DriverName==null || (_result.DriverName!=null && _result.DriverName.trim().equals("")))
            _result.DriverName="No description available";
        return _result;
    }
    public String getEmojiByUnicode(int unicode)
    {
        return new String(Character.toChars(unicode));
    }
    public static Boolean HasSpecialCharacters(String text){

        return !Pattern.matches("[a-zA-Z ]+", text);

    }
    // Method for getting the minimum value
    public static int getMin(Integer[] inputArray){
        int minValue = inputArray[0];
        for(int i=1;i<inputArray.length;i++){
            if(inputArray[i] < minValue){
                minValue = inputArray[i];
            }
        }
        return minValue;
    }
    public static Boolean IsStringEqual(String STR_Entry_1, String STR_Entry_2){
        if(STR_Entry_1==null || STR_Entry_2 == null)
            return false;
        return STR_Entry_1.toLowerCase().equals(STR_Entry_2.toLowerCase());
    }

    public Terminal GetTerminalFromValue(String value)
    {

        String[] arrayString = value.split("_");
        String terminalValue = arrayString[0];
        int terminalRouteID = Integer.parseInt(arrayString[1]);
        for(Terminal terminal: MenuActivity._terminalList)
        {
            if (terminal.getValue().equalsIgnoreCase(terminalValue) && terminal.getTblRouteID() == terminalRouteID)
                return terminal;

        }
        return null;
    }

    public static UserMarker GetUserMarkerDetails(String STR_UserType, Context context){
            UserMarker UM_result = new UserMarker(STR_UserType,context);
        return UM_result;
    }
    public static Boolean IsPossibleAdminBasedOnFirebaseUserKey(String STR_FirebaseUserKey){
        if(STR_FirebaseUserKey.matches("\\d+") || STR_FirebaseUserKey.contains("guest")){
          return false;
        }else {
            return true;
        }
    }
    public static void InitializeFonts(Context _context){
        try{
            FONT_PLATE = Typeface.createFromAsset(_context.getAssets(),
                    _GlobalResource.getString(R.string.FONT_LICENSE_PLATE));
            FONT_STATION = Typeface.createFromAsset(_context.getAssets(),
                    _GlobalResource.getString(R.string.FONT_ROBOTO_MEDIUM));
            FONT_RUBIK_BOLD = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_Rubik_Bold));
            FONT_RUBIK_REGULAR = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_Rubik_Regular));
            FONT_RUBIK_MEDIUM = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_ROBOTO_MEDIUM));
            FONT_ROBOTO_CONDENDSED_BOLD = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_RobotoCondensed_Bold));
            FONT_RUBIK_BLACK = Typeface.createFromAsset(_context.getAssets(), _GlobalResource.getString(R.string.FONT_Rubik_Black));
        }catch (Exception ex){
            Helper.logger(ex);
        }
    }

    /**
     * This returns a list of Station "Values" based on RouteID
     * Can be used for filtering the list of stations that an admin user can view on the Manage Stations Module
     * @param routeID
     * @return an array of Terminal
     */
    public Terminal[] FilterStationsForManageStationsModule(Integer routeID){
        List<Terminal> terminalListBasedOnRouteID = new ArrayList<>();
        for (Terminal entry: MenuActivity._terminalList) {
            if(entry.tblRouteID == routeID)
            {
                terminalListBasedOnRouteID.add(entry);
            }
        }
        MenuActivity._PointsArray =  terminalListBasedOnRouteID.toArray(new Terminal[terminalListBasedOnRouteID.size()]);
        terminalListBasedOnRouteID.toArray(new Terminal[terminalListBasedOnRouteID.size()]);
        MenuActivity._currentRouteIDSelected = routeID;
        return terminalListBasedOnRouteID.toArray(new Terminal[terminalListBasedOnRouteID.size()]);

    }
    /**
     * This updates the station markers on the map
     * Call this when user adds, edits or deletes a station
     * @param StationList
     * @param googleMap
     */

    public void UpdateStationMarkersOnTheMap(List<Terminal> StationList, GoogleMap googleMap, GoogleApiClient googleApiClient)
    {
        googleMap.clear();
        MenuActivity._terminalMarkerHashmap = new HashMap<>();

        for (Terminal station : StationList)
        {
            if(station.Lat >0 && station.Lng >0)
            {
                double lat = station.Lat;
                double lng = station.Lng;
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ecoloopstop));
                //markerOptions.snippet("0 passenger/s waiting");

                markerOptions.title(station.ID.toString()+"-"+ station.getValue());
                Marker marker = googleMap.addMarker(markerOptions);
               // dropMarker(marker,googleMap);
                // marker.showInfoWindow();

                MenuActivity._terminalMarkerHashmap.put(station.Value, marker);
            }
        }
//        startGeofence(StationList, googleMap, googleApiClient);
    }
    // Start Geofence creation process
    private void startGeofence(List<Terminal> listTerminals, GoogleMap googleMap, GoogleApiClient googleApiClient) {


        Log.i(LOG_TAG, "startGeofence()");
        List<Geofence> geofences = createGeoFence(listTerminals);
        GeofencingRequest geofenceRequest = createGeofenceRequest(geofences);
        addGeofence(geofenceRequest, googleMap, googleApiClient, listTerminals);
    }
    private GeofencingRequest createGeofenceRequest(List<Geofence> geofence)
    {
        Log.i(LOG_TAG, "createGeofenceRequest()");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofence);
        return builder.build();
    }
    public List<Geofence> createGeoFence(List<Terminal> listTerminals)
    {
        Log.i(LOG_TAG, "createGeoFence");
        int i = 0;

        String geofenceRequestId = "";
        List<Geofence> geofenceList = new ArrayList<>();
        if (listTerminals != null)
            for (Terminal terminal : listTerminals) {
                if(terminal.Lat>0 && terminal.Lng>0) {
                    try
                    {
                        geofenceRequestId = UUID.randomUUID().toString();
                        geofenceList.add(new Geofence.Builder()
                                .setRequestId(geofenceRequestId)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
//                                .setLoiteringDelay(5000)
                                .setCircularRegion(terminal.Lat, terminal.Lng, Constants.GEOFENCE_RADIUS)
                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .build());

                        listTerminals.get(i).GeofenceId = geofenceRequestId;
                    }
                    catch(Exception ex)
                    {
                        ErrorDialog errorDialog = new ErrorDialog(_activity, MenuActivity._GlobalResource.getString(R.string.error_generic));
                        errorDialog.show();
                        logger(ex,true);
                    }
                }
                i++;

//                if(i==1)
//                    break;
                //drawGeofence(new LatLng(terminal.Lat, terminal.Lng));
            }

        return geofenceList;
    }
    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request,  final GoogleMap googleMap, GoogleApiClient googleApiClient,final List<Terminal> terminals) {

        Log.d(LOG_TAG, "addGeofence");
        if (checkPermission())
            try
            {
                PendingIntent pendingIntent = createGeofencePendingIntent();

                LocationServices.GeofencingApi.removeGeofences(googleApiClient, pendingIntent);
                LocationServices.GeofencingApi.addGeofences(
                        googleApiClient,
                        request,
                        pendingIntent
                ).setResultCallback(new ResultCallback<com.google.android.gms.common.api.Status>() {

                    @Override
                    public void onResult(com.google.android.gms.common.api.Status status) {
                        if (status.isSuccess()) {
                            Log.i(LOG_TAG, "Success Saving Geofence");
                            drawGeofence(terminals, googleMap);
                        } else {
                            logger("Registering geofence failed: " + status.getStatusMessage() +
                                    " : " + status.getStatusCode());

                        }
                    }
                });
            }
            catch (Exception ex)
            {
                ErrorDialog errorDialog = new ErrorDialog(_activity, MenuActivity._GlobalResource.getString(R.string.error_generic));
                errorDialog.show();
                logger(ex);
            }



    }
    // Check for permission to access Location
    private boolean checkPermission() {
        try
        {
            Log.d(LOG_TAG, "checkPermission()");
            // Ask for permission if it wasn't granted yet
            return (ContextCompat.checkSelfPermission(this._activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED );
        }
        catch(Exception ex)
        {
            logger(ex);
            return false;
        }
    }
    private PendingIntent createGeofencePendingIntent()
    {
        Log.i(LOG_TAG, "createGeofencePendingIntent()");
        Intent intent = new Intent(this._activity, GeofenceTransitionsIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this._activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        this._activity.startService(intent);
        return pendingIntent;


    }
    private void drawGeofence(List<Terminal> terminals, GoogleMap googleMap) {
        Log.d(LOG_TAG, "drawGeofence()");
        for(Terminal d: terminals)
        {
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(d.Lat, d.Lng))
                    .strokeColor(Color.argb(50, 70,70,70))
                    .fillColor( Color.argb(100, 150,150,150) )
                    .radius( Constants.GEOFENCE_RADIUS );
            googleMap.addCircle( circleOptions );
        }

    }
    public void UpdateRoutesData(ManageRoutesFragment manageRoutesFragment, String updatedRoutesDataInJSONFormat)
    {

        try
        {
            Type listTypeOfRoute = new TypeToken<List<Routes>>(){}.getType();
            String routesList = (updatedRoutesDataInJSONFormat);
            Gson gson = new Gson();
            MenuActivity._routeList = gson.fromJson(routesList, listTypeOfRoute);

            ArrayList<Routes> routesByLineID = new ArrayList<Routes>();
            if (MenuActivity._currentLineIDSelected!=0)
                for(Routes route:MenuActivity._routeList)
                {
                    if (route.getTblLineID() == MenuActivity._currentLineIDSelected)
                        routesByLineID.add(route);
                }
            else
                routesByLineID = new ArrayList<Routes>(MenuActivity._routeList);

            RouteViewCustomAdapter customAdapter =new RouteViewCustomAdapter(routesByLineID,
                    _activity,
                    manageRoutesFragment._routesListView,
                    manageRoutesFragment.getFragmentManager(),
                    manageRoutesFragment._swipeRefreshRoute,
                    manageRoutesFragment);
            MenuActivity._manageRoutesFragment._routesListView.setAdapter(customAdapter);
        }
        catch(Exception ex)
        {
            logger(ex);
        }



    }
    public void UpdateStationsData(ManageStationsFragment manageStationsFragment, String updatedStationsDataInJSONFormat)
    {

        Type listTypeOfTerminal = new TypeToken<List<Terminal>>(){}.getType();
        String stationList = (updatedStationsDataInJSONFormat);
        Gson gson = new Gson();

        MenuActivity._terminalList = gson.fromJson(stationList, listTypeOfTerminal);

        FilterStationsForManageStationsModule(MenuActivity._currentRouteIDSelected);
        ArrayAdapter adp = new ArrayAdapter(_context, R.layout.listview_viewpoints, MenuActivity._PointsArray);
        MenuActivity._manageStationsFragment.setListAdapter(adp);

    }
    public void UpdateLinesData(ManageLinesFragment manageLinesFragment, String updatedLinesDataInJSONFormat)
    {

        Type listTypeOfLines = new TypeToken<List<Lines>>(){}.getType();
        String lineList = (updatedLinesDataInJSONFormat);
        Gson gson = new Gson();
        MenuActivity._lineList = gson.fromJson(lineList, listTypeOfLines);

        LineViewCustomAdapter customAdapter =new LineViewCustomAdapter(MenuActivity._lineList,
                _activity,
                manageLinesFragment._lineListView,
                manageLinesFragment.getFragmentManager(),
                manageLinesFragment._swipeRefreshLines,
                manageLinesFragment);
        MenuActivity._manageLinesFragment._lineListView.setAdapter(customAdapter);


    }

    public Integer getNoOfStationsByRouteID(Integer routeID)
    {
        for(Routes route:MenuActivity._routeList)
        {
            if (route.getID() == routeID)
            {
                return route.getNoOfStations();
            }
        }
        return 0;
    }
    public Integer DpToPx(Integer dp)
    {
        Resources r = _context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics());
    }
    public void slideUp(View view, int slideUpDistance){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,-slideUpDistance);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }
    public void slideDown(View view, int slideDownDistance){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,slideDownDistance);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
    }
    public String getLineNameBasedOnLineID(Integer lineID)
    {
        for(Lines line:MenuActivity._lineList)
        {
            if (line.getID() == lineID)
            {
                return line.getName();
            }
        }
        return "";
    }
    public String getPlateNumberBasedOnUserID(Integer UserID)
    {
        for(Eloop eloop:MenuActivity._eloopList)
        {
            if (eloop.tblUsersID == UserID)
            {
                return eloop.PlateNumber;
            }
        }
        return "";
    }
    public void DisconnectPreviousUser()
    {
        final SessionManager sessionManager = new SessionManager(MenuActivity._context);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference terminalDBRef = database.getReference("terminals");
        String node="users/" + sessionManager.getUsername();
        final DatabaseReference userRef = database.getReference(node);
        final DatabaseReference myConnectionsRef = database.getReference(node + "/connections");
        final DatabaseReference lastOnlineRef = database.getReference("/"+node + "/lastOnline");
        if (sessionManager.isGuest())
            userRef.removeValue();
        else
        {
            lastOnlineRef.onDisconnect().setValue(DateFormat.getDateTimeInstance().format(new Date()));
            myConnectionsRef.setValue(false);
        }
        terminalDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    terminalDBRef.child(snapshot.getKey().toString()).child(sessionManager.getUsername()).onDisconnect().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public Integer OrderOfArrivalDifference(Terminal TM_UserLoc, Terminal TM_2){
        Integer result = 0;
        try{
            return TM_UserLoc.OrderOfArrival - TM_2.OrderOfArrival;
        }catch (Exception ex){
            Helper.logger(ex);
        }
        return result;
    }

    public void dropMarker(final Marker marker, GoogleMap map) {
        final LatLng finalPosition = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);

        Projection projection = map.getProjection();
        Point startPoint = projection.toScreenLocation(finalPosition);
        startPoint.y = 0;
        final LatLng startLatLng = projection.fromScreenLocation(startPoint);
        final Interpolator interpolator = new MarkerInterpolator(0.11, 4.6);
        final android.os.Handler handler = new android.os.Handler();
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {

                float t = interpolator.getInterpolation(fraction);
                double lng = t * finalPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * finalPosition.latitude + (1 - t) * startLatLng.latitude;
                return new LatLng(lat, lng);
            }
        };

        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator =  ObjectAnimator.ofObject(marker,property,typeEvaluator,finalPosition);
        // (marker, property,typeEvaluator,finalPosition);// ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition);
        animator.setDuration(400);
        animator.start();
    }




}

