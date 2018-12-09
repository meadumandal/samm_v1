package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.EntityObjects.VehicleProperties;
import com.umandalmead.samm_v1.POJO.HTMLDirections.Directions;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.umandalmead.samm_v1.MenuActivity._GlobalResource;

/**
 * Created by eleazerarcilla on 02/12/2018.
 */

public class ArrivalHelper {
    private FirebaseDatabase FB;
    private DatabaseReference DriversDatabaseReference,
            VehicleDestinationDatabaseReference,
            _S_VehicleDestinationDatabaseReference;
    private static ChildEventListener _SingleLoopChildListenerForSelectedTerminal;
    private Activity _activity;
    private Context _context;
    private Helper _helper;
    private Boolean _BOOL_IsFromSearch = false;
    public ArrivalHelper(Activity activity, Context context){
        this._activity = activity;
        this._context = context;
        this._helper = new Helper(_activity,_context);
    }
    public void GetGPSDetailsFromFirebase(final Terminal TM_NearestFromUser, Boolean IsFromSearch){
        try{
            if(TM_NearestFromUser != null){
                GetAllTerminalGroupedByRoutes();
                if(MenuActivity._HM_GroupedTerminals !=null){
                    GetAllGPSDataFromFirebase(TM_NearestFromUser);
                    _BOOL_IsFromSearch = IsFromSearch;
                }
            }
        }catch (Exception ex){
            Helper.logger(ex);
        }
    }
    public void GetAllTerminalGroupedByRoutes(){
        HashMap<Integer, ArrayList<Terminal>> HM_TM_TerminalMasterList = new  HashMap<Integer, ArrayList<Terminal>>() ;
        try{
            if(MenuActivity._HM_GroupedTerminals==null) {
                for (Terminal T_entry : MenuActivity._terminalList) {
                    ArrayList<Terminal> AL_TerminalList = HM_TM_TerminalMasterList.get(T_entry.tblRouteID);
                    if (AL_TerminalList == null) {
                        AL_TerminalList = new ArrayList<Terminal>();
                        AL_TerminalList.add(T_entry);
                        HM_TM_TerminalMasterList.put(T_entry.tblRouteID, AL_TerminalList);
                    } else {
                        AL_TerminalList.add(T_entry);
                    }
                }
                MenuActivity._HM_GroupedTerminals = HM_TM_TerminalMasterList;
            }
        }catch (Exception ex){
            Helper.logger(ex);
        }
    }
    public void GetAllGPSDataFromFirebase(final Terminal TM_NearestFromUser){
        try{
            FB = FirebaseDatabase.getInstance();
            DriversDatabaseReference = FB.getReference("drivers");
            DriversDatabaseReference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot DS_Vehicle_Destinantions) {
                    ArrayList<VehicleProperties> AL_VehicleProperties = new ArrayList<>();
                    ArrayList<Terminal> T_UserLocationSpecimen = new ArrayList<Terminal>();
                    if(DS_Vehicle_Destinantions.getChildren()!=null){
                        T_UserLocationSpecimen = getTerminalArrayListObject(TM_NearestFromUser.getValue());
                        for (DataSnapshot DS_Driver_Entry: DS_Vehicle_Destinantions.getChildren()){
                            String STR_LastEnteredStation = DS_Driver_Entry.child("EnteredStation").getValue() != null ?
                                    DS_Driver_Entry.child("EnteredStation").getValue().toString() : null;
                            if(STR_LastEnteredStation!=null && !STR_LastEnteredStation.equalsIgnoreCase("")) {
                                VehicleProperties VP_Entry = new VehicleProperties();
                                VP_Entry.setTargetDestinationSpecimen(getTerminalArrayListObject(STR_LastEnteredStation));
                                VP_Entry.setEloop(Helper.GetEloopEntry(DS_Driver_Entry.child("deviceid").getValue().toString()));
                                VP_Entry.setPossibleRouteIDs(DS_Driver_Entry.child("routeIDs").getValue().toString());
                                AL_VehicleProperties.add(VP_Entry);
                            }
                        }
                        ArrayList<VehicleProperties> AL_CandidateVehicles = new ArrayList<VehicleProperties>();
                        if(T_UserLocationSpecimen!=null && T_UserLocationSpecimen.size() > 0
                                && AL_VehicleProperties!=null && AL_VehicleProperties.size() > 0) {

                            for (Terminal T_Entry : T_UserLocationSpecimen) {
                                for (VehicleProperties VP_Entry : AL_VehicleProperties) {
                                    Integer ctr =0;
                                    while(ctr<VP_Entry.getTargetDestinationSpecimen().size()){
                                        if(T_Entry.getTblRouteID()==VP_Entry.getTargetDestinationSpecimen().get(ctr).getTblRouteID()
                                                && ValidateOrderOfArrival(T_Entry,VP_Entry.getTargetDestinationSpecimen().get(ctr)) && VP_Entry
                                                .getPossibleRouteIDs().contains(String.valueOf(T_Entry.getTblRouteID()))){
                                            VehicleProperties VP_Entry2 = new VehicleProperties();
                                            VP_Entry2.setEloop(VP_Entry.getEloop());
                                            VP_Entry2.setOrderOfArrivalDifference(_helper.OrderOfArrivalDifference(T_Entry, VP_Entry.getTargetDestinationSpecimen().get(ctr)));
                                            VP_Entry2.setTerminalObject(VP_Entry.getTargetDestinationSpecimen().get(ctr));
                                            VP_Entry2.setDistance(getAggregatedDistanceFromUserLocation(T_Entry, VP_Entry.getTargetDestinationSpecimen().get(ctr)));
                                            AL_CandidateVehicles.add(VP_Entry2);
                                        }
                                        ctr++;
                                    }
                                }
                            }
                        }
                       Collections.sort(AL_CandidateVehicles, VehicleProperties.VehicleComparators.BASED_FROM_DISTANCE_ASC);
                        ProcessVehicleResults(AL_CandidateVehicles, TM_NearestFromUser,T_UserLocationSpecimen);
                    }
                }
            });
        }catch (Exception ex){
            Helper.logger(ex);
        }
    }
    public void ProcessVehicleResults(ArrayList<VehicleProperties> AL_VehicleProperties, Terminal TM_NearestFromUser, ArrayList<Terminal> AL_UserTerminalSpecimen){
        try{
            if(AL_VehicleProperties.size() > 0) {
                VehicleProperties VP_SelectedVehicle =  AL_VehicleProperties.get(0);
                if (VP_SelectedVehicle.getOrderOfArrivalDifference() == 0) {
                    if (_BOOL_IsFromSearch)
                        Helper.InitializeSearchingRouteUI(true, false, _GlobalResource.getString(R.string.VEHICLE_already_waiting), null, null, _context);
                    else {
                        if (Helper.IsStringEqual(MenuActivity._SelectedTerminalMarkerTitle, TM_NearestFromUser.getValue())) {
                            ((MenuActivity) _activity).UpdateInfoPanelForTimeofArrival(TM_NearestFromUser.LineName + "-" + TM_NearestFromUser.Description, null,
                                    _helper.getEmojiByUnicode(0x1F68C) + " : " + _GlobalResource.getString(R.string.VEHICLE_already_waiting));
                        }
                    }

                } else {
                    GetTimeRemainingFromGoogle(VP_SelectedVehicle.getEloop().DeviceId, TM_NearestFromUser);
                    if (_BOOL_IsFromSearch)
                        AttachListenerToLoopV2(VP_SelectedVehicle.getEloop().DeviceId, AL_UserTerminalSpecimen);
                }
            }else{
                if (_BOOL_IsFromSearch)
                    Helper.InitializeSearchingRouteUI(true, true, "Unfortunately, all E-loops are parked (or offline)",null,null,_context);
                else{
                    if (Helper.IsStringEqual(MenuActivity._SelectedTerminalMarkerTitle, TM_NearestFromUser.getValue())) {
                        ((MenuActivity) _activity).UpdateInfoPanelForTimeofArrival(TM_NearestFromUser.LineName + "-" + TM_NearestFromUser.Description, null,
                                _helper.getEmojiByUnicode(0x1F68C) + " : " + "No nearby E-loops found.");
                    }
                }
            }

        }catch (Exception ex){
             Helper.logger(ex);
        }
    }
    public void RemoveListenerFromLoop(){
        if(_S_VehicleDestinationDatabaseReference!=null) {
            _S_VehicleDestinationDatabaseReference.removeEventListener(_SingleLoopChildListenerForSelectedTerminal);
            _S_VehicleDestinationDatabaseReference=null;
            _SingleLoopChildListenerForSelectedTerminal=null;
        }
    }
    private void AttachListenerToLoopV2(final Integer INT_LoopID, final ArrayList<Terminal> AL_UserTerminalSpecimen){
        try {
            FB = FirebaseDatabase.getInstance();
            _S_VehicleDestinationDatabaseReference = FB.getReference("drivers").child(INT_LoopID.toString());
            _SingleLoopChildListenerForSelectedTerminal = _S_VehicleDestinationDatabaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getKey().toUpperCase().equals("ROUTEIDS")) {
                        String Firebase_routeIDs = dataSnapshot.getValue().toString();
                        ArrayList<String> AL_Firebase_routeIDs = new ArrayList<String>();
                        ArrayList<String> AL_RouteIds = new ArrayList<String>();
                        if(AL_UserTerminalSpecimen!= null && AL_UserTerminalSpecimen.size()>0) {
                            for (Terminal T_entry : AL_UserTerminalSpecimen) {
                                AL_RouteIds.add(String.valueOf(T_entry.getTblRouteID()));
                            }
                        }
                        if(Firebase_routeIDs!=null && !Firebase_routeIDs.trim().equalsIgnoreCase("")){
                            String[] STR_temp= Firebase_routeIDs.split(",");
                            AL_Firebase_routeIDs = new ArrayList<String>(Arrays.asList(STR_temp));
                        }
                        AL_Firebase_routeIDs.retainAll(AL_RouteIds);
                        if(AL_UserTerminalSpecimen.size() > 0) {
                            if (AL_Firebase_routeIDs.size() == 0) {
                                _S_VehicleDestinationDatabaseReference.removeEventListener(this);
                                //Toast.makeText(_context, "Listener removed!", Toast.LENGTH_SHORT).show();
                                GetGPSDetailsFromFirebase(AL_UserTerminalSpecimen.get(0), true);
                            } else if (!MenuActivity._HasExitedInfoLayout && AL_Firebase_routeIDs.size() != 0) {
                                GetTimeRemainingFromGoogle(INT_LoopID, AL_UserTerminalSpecimen.get(0));
                                //Toast.makeText(_context, "Listener attached! RouteID:" + routeId + " FirebaseRouteID: " + Firebase_routeIDs, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception ex){
            Helper.logger(ex);
        }

    }
    public void GetTimeRemainingFromGoogle(final Integer INT_LoopID, final Terminal TM_Destination) {

        if (INT_LoopID != null) {
            FB = FirebaseDatabase.getInstance();
            DriversDatabaseReference = FB.getReference("drivers").child(INT_LoopID.toString()); //database.getReference("users/"+ _sessionManager.getUsername() + "/connections");
            DriversDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<Integer, Integer> destinationId_distance = new HashMap<>();
                    String url = "https://maps.googleapis.com/maps/";
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitMaps service = retrofit.create(RetrofitMaps.class);
                    Call<Directions> call = service.getDistanceDuration("metric", TM_Destination.Lat + "," + TM_Destination.Lng, dataSnapshot.child("Lat").getValue() + "," + dataSnapshot.child("Lng").getValue(), "driving");
                    call.enqueue(new Callback<Directions>() {
                        @Override
                        public void onResponse(Response<Directions> response, Retrofit retrofit) {
                            try {
                                for (int i = 0; i < response.body().getRoutes().size(); i++) {
                                    String TimeofArrival = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();
                                    String Distance = response.body().getRoutes().get(0).getLegs().get(0).getDistance().getText();
                                    //Helper.InitializeSearchingRouteUI(true,false, "Filinvest E-loop",Distance, TimeofArrival.toString(),_context);
                                    if(_BOOL_IsFromSearch) {
                                        Helper.InitializeSearchingRouteUI(true, false, Helper.GetEloopEntry(INT_LoopID.toString()).PlateNumber, Distance, TimeofArrival.toString(), _context);
                                    }
                                    else{
                                        if(Helper.IsStringEqual(MenuActivity._SelectedTerminalMarkerTitle, TM_Destination.getValue())) {
                                            ((MenuActivity)_activity).UpdateInfoPanelForTimeofArrival(TM_Destination.LineName + "-" + TM_Destination.Description, null,
                                                    _helper.getEmojiByUnicode(0x1F68C) + " : " + TimeofArrival + " (" + Distance + " away)");
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                if(_BOOL_IsFromSearch)
                                    Helper.InitializeSearchingRouteUI(true,true,"Data Error!",null,null,_context);
                                Log.d("onResponse", "There is an error");
                                Helper.logger(ex);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.d("onFailure", t.toString());
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    public static ArrayList<Terminal> getTerminalArrayListObject(String STR_StationName) {
        ArrayList<Terminal> AL_result = new ArrayList<Terminal>();
        try {
            if (STR_StationName != null && !STR_StationName.equalsIgnoreCase("")) {
                for (Terminal T_entry : MenuActivity._terminalList) {
                    if (T_entry.getValue().equalsIgnoreCase(STR_StationName))
                        AL_result.add(T_entry);
                }
                Collections.sort(AL_result, Terminal.DestinationComparators.ORDER_BY_ROUTEID_ASC);
            }
        } catch (Exception ex) {
            Helper.logger(ex);
        }
        return AL_result;
    }
    public double getAggregatedDistanceFromUserLocation(Terminal TM_UserLocation, Terminal TM_VehicleLocation){
        double D_result = 0.0;
        try{
            ArrayList<Terminal> AL_PickedRouteBasedFromUserLocation = MenuActivity._HM_GroupedTerminals.get(TM_UserLocation.getTblRouteID());
            if(AL_PickedRouteBasedFromUserLocation.size() > 0){
                Collections.sort(AL_PickedRouteBasedFromUserLocation, Terminal.DestinationComparators.ORDER_OF_ARRIVAL_DESC);
                Terminal T_MainTerminal = GetStationTerminalFromRoute(TM_UserLocation.getTblRouteID());
               for (Terminal T_Entry:AL_PickedRouteBasedFromUserLocation){
                   if(ValidateOrderOfArrival(TM_UserLocation, T_Entry)
                           && T_Entry.getIsMainTerminal()!="1"){
                       if(T_Entry.getOrderOfArrival()==TM_VehicleLocation.getOrderOfArrival())
                           break;
                       if(T_Entry.equals(T_MainTerminal))
                           break;
                       D_result += T_Entry.getDistanceFromPreviousStation();

                   }
               }

            }
        }catch (Exception ex){
            Helper.logger(ex);
        }
        return D_result;
    }
    public Terminal GetStationTerminalFromRoute(int INT_RouteID){
        Terminal T_result = new Terminal();
        try{
            if(MenuActivity._HM_GroupedTerminals != null && MenuActivity._HM_GroupedTerminals.size() > 0){
                ArrayList<Terminal> AL_Terminals = MenuActivity._HM_GroupedTerminals.get(INT_RouteID);
                for (Terminal T_entry: AL_Terminals){
                    if(T_entry.getIsMainTerminal() != null && T_entry.getIsMainTerminal().equalsIgnoreCase("1"))
                        return T_entry;
                }
            }
            else{
                GetAllTerminalGroupedByRoutes();
                GetStationTerminalFromRoute(INT_RouteID);
            }
        }catch(Exception ex){
            Helper.logger(ex);
        }
        return  T_result;
    }
    public Boolean ValidateOrderOfArrival(Terminal T_EntryLocation, Terminal T_VehicleLastStation){
        Boolean BOOL_result = false;
        try{
            if(T_EntryLocation.getOrderOfArrival() == 1)
                return true;
            if(T_EntryLocation.getOrderOfArrival() >= T_VehicleLastStation.getOrderOfArrival())
               return true;

        }catch (Exception ex){

        }
        return BOOL_result;
    }
}
