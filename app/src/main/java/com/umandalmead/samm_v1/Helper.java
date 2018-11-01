package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.umandalmead.samm_v1.EntityObjects.Eloop;
import com.umandalmead.samm_v1.EntityObjects.FirebaseEntities.User;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.EntityObjects.UserMarker;
import com.umandalmead.samm_v1.EntityObjects.Users;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.SaveCurrentDestination;
import com.umandalmead.samm_v1.Modules.Logger.mySQLSendErrorReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;
import static com.umandalmead.samm_v1.MenuActivity._AppBar;
import static com.umandalmead.samm_v1.MenuActivity._DestinationTextView;
import static com.umandalmead.samm_v1.MenuActivity._LL_Arrival_Info;
import static com.umandalmead.samm_v1.MenuActivity._LoopArrivalProgress;
import static com.umandalmead.samm_v1.MenuActivity._RouteTabLayout;
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
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(ToRadians(First.latitude)) * Math.cos(ToRadians(Second.latitude)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
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
                _TimeOfArrivalTextView.setText(Html.fromHtml(STR_HTMLMessage));
                //MenuActivity._SlideUpPanelContainer.setPanelHeight(dpToPx(60,context));
            }
            else if(BOOL_IsResultNegative){
                _LL_Arrival_Info.setVisibility(View.INVISIBLE);
                _TimeOfArrivalTextView.setVisibility(View.VISIBLE);
                _TimeOfArrivalTextView.setBackgroundResource(R.drawable.pill_shaped_eloop_status_error);
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
    public Boolean FindNearestPickUpPoints(Terminal dropOffPoint, Activity activity, Context context) {
        if(isOnline(activity,context))
        {
           // ArrayList<Terminal> dropOffPointList = GetAllDestinationRegardlessOfTheirTableRouteIds(dropOffPoint);
            Terminal chosenTerminal =dropOffPoint;
            saveDestination(chosenTerminal.Value);

            (this._activity)._possiblePickUpPointList = new ArrayList<>();
            for(Terminal terminal : (this._activity)._terminalList)
            {
                if (terminal.Direction.equals(chosenTerminal.Direction) && terminal.tblRouteID == chosenTerminal.tblRouteID)
                {
                    if(terminal.OrderOfArrival < chosenTerminal.OrderOfArrival)
                        (this._activity)._possiblePickUpPointList.add(terminal);
                }
            }
            return (this._activity)._possiblePickUpPointList.size() <= 0;
        }
        else
        {
            return false;
        }

    }
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
        return _result;
    }
    public String getEmojiByUnicode(int unicode)
    {
        return new String(Character.toChars(unicode));
    }
    public static Boolean CheckForSpecialCharacters(String text){
        Boolean result = false;
        Pattern p = Pattern.compile("^[^<>%$]*$");
        Matcher m = p.matcher(text);
        if (!m.matches())
        {
            result = true;
        }
        return  result;
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
    public static UserMarker GetUserMarkerDetails(String entry, Context context){
            UserMarker UM_result = new UserMarker(entry,context);
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


}

