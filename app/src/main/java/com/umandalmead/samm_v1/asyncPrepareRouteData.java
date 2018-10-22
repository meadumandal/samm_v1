package com.umandalmead.samm_v1;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.POJO.HTMLDirections.Directions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.google.android.gms.maps.model.JointType.ROUND;
import static com.umandalmead.samm_v1.MenuActivity._BOOL_IsGPSAcquired;
import static com.umandalmead.samm_v1.MenuActivity._GlobalResource;
import static com.umandalmead.samm_v1.MenuActivity._RouteStepsText;
import static com.umandalmead.samm_v1.MenuActivity._SlideUpPanelContainer;
import static com.umandalmead.samm_v1.MenuActivity._googleMap;
import static com.umandalmead.samm_v1.MenuActivity._userCurrentLoc;

/**
 * Created by eleazerarcilla on 21/10/2018.
 */

public class asyncPrepareRouteData extends AsyncTask<Void,Void,Void>{
    protected Activity _activity;
    protected Context _context;
    LoaderDialog _dialog;
    private List<Terminal> _L_Terminals;
    private List<String> _L_STR_TotalTimeList = new ArrayList<String>();
    private List<List<String>> _L_L_STR_DirectionStepsList = new ArrayList<List<String>>();
    private List<Terminal> _L_TM_AllPossibleTerminals = new ArrayList<Terminal>();
    private List<List<String>> _L_L_STR_TerminalPointsList = new ArrayList<List<String>>();
    private List<String> _L_AllPoints = new ArrayList<String>();
    private Terminal _chosenTerminal;
    private GoogleMap _map;
    public static Polyline _redPolyLine, _magentaPolyLine;
    private static List<LatLng> listLatLng = new ArrayList<>();
    private  LoaderDialog _loader;
    private static DatabaseReference DriversDatabaseReference, VehicleDestinationDatabaseReference, _S_VehicleDestinationDatabaseReference;
    private FirebaseDatabase FB;
    private static ChildEventListener _SingleLoopChildListenerForSelectedTerminal;
    private String _AssignedELoop = "";
    private Boolean _IsAllLoopParked = true;
    private List<Integer> _ListOfLoops = new ArrayList<Integer>();

    public asyncPrepareRouteData(Activity activity, Context context, List<Terminal> L_TM_topTerminals, Terminal terminal, GoogleMap googlemap,LoaderDialog loaderDialog){
        this._activity = activity;
        this._context = context;
        this._L_Terminals = L_TM_topTerminals;
        this._chosenTerminal = terminal;
        this._map = googlemap;
        this._loader = loaderDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<Object> Objects = new ArrayList<Object>();
        try{
           // long startTime = System.currentTimeMillis();
            int ctr = 0;
            for (Terminal terminal : _L_Terminals) {
                String TotalTime = "";
                List<String> DirectionSteps = new ArrayList<String>();
                for (int i = 0; i < terminal.directionsFromCurrentLocation.getRoutes().size(); i++) {
                    TotalTime = terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDuration().getText();
                    String encodedString = terminal.directionsFromCurrentLocation.getRoutes().get(0).getOverviewPolyline().getPoints();
                    //drawLines(encodedString);
                    _L_AllPoints.add(encodedString);
                    _L_STR_TotalTimeList.add(TotalTime);
                }
                for (int x = 0; x < terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getInstructions().size(); x++) {
                    String Instructions = terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getInstructions().get(x).getSteps().toString();
                    DirectionSteps.add(Instructions);
                }
                _L_L_STR_TerminalPointsList.add(_L_AllPoints);
                _L_L_STR_DirectionStepsList.add(DirectionSteps);
                ctr++;
            }

        }catch (Exception ex){
            Helper.logger(ex);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        createRouteTabs(_L_STR_TotalTimeList,_L_L_STR_DirectionStepsList,_L_Terminals,_L_L_STR_TerminalPointsList);

    }
    public void createRouteTabs(final List<String> L_STR_TotalTimeList, final List<List<String>> L_L_STR_DirectionStepsList, final List<Terminal> L_TM_AllPossibleTerminals, final List<List<String>> L_L_STR_TerminalPointsList) {
        try {
            ((MenuActivity)_activity).ShowRouteTabsAndSlidingPanel();
            MenuActivity._selectedPickUpPoint = _L_Terminals.get(0);
            if (L_TM_AllPossibleTerminals.size() == 0 || L_TM_AllPossibleTerminals == null)
                throw new Exception("Unable to find route for this destination.");

            MenuActivity.RouteTabs.removeAllTabs();
            for (Terminal entry : L_TM_AllPossibleTerminals) {
                MenuActivity.RouteTabs.addTab( MenuActivity.RouteTabs.newTab().setText(entry.Description));
            }
            MenuActivity.RouteTabs.setTabGravity(TabLayout.GRAVITY_FILL);

            MenuActivity.RouteTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    MenuActivity.viewPager.setCurrentItem(tab.getPosition());
                    MenuActivity._RouteTabSelectedIndex = tab.getPosition();

                    new asyncGenerateDirectionSteps(_activity,_activity,_chosenTerminal, L_L_STR_DirectionStepsList.get(MenuActivity._RouteTabSelectedIndex),L_STR_TotalTimeList.get(MenuActivity._RouteTabSelectedIndex), L_TM_AllPossibleTerminals.get(MenuActivity._RouteTabSelectedIndex),_loader).execute();
                    MenuActivity._selectedPickUpPoint = L_TM_AllPossibleTerminals.get(tab.getPosition());
                    GetArrivalTimeOfLoopBasedOnSelectedStation(L_TM_AllPossibleTerminals.get(tab.getPosition()));
                    RemoveListenerFromLoop();
                    clearLines();
                    PlayButtonClickSound();
                    //final Handler HND_UpdateUI = new Handler();
//                    HND_UpdateUI.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    }, 1000);
                    ((MenuActivity)asyncPrepareRouteData.this._activity).UpdateUI(Enums.UIType.SHOWING_ROUTES);
                    drawLines(L_L_STR_TerminalPointsList.get(tab.getPosition()).get(tab.getPosition()));
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            clearLines();
//            final Handler HND_UpdateUI = new Handler();
//            HND_UpdateUI.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            }, 1000);
            ((MenuActivity)asyncPrepareRouteData.this._activity).UpdateUI(Enums.UIType.SHOWING_ROUTES);
            MenuActivity._selectedPickUpPoint = L_TM_AllPossibleTerminals.get(0);
            new asyncGenerateDirectionSteps(_activity,_activity,_chosenTerminal, L_L_STR_DirectionStepsList.get(MenuActivity._RouteTabSelectedIndex),L_STR_TotalTimeList.get(MenuActivity._RouteTabSelectedIndex), L_TM_AllPossibleTerminals.get(MenuActivity._RouteTabSelectedIndex),_loader).execute();
            GetArrivalTimeOfLoopBasedOnSelectedStation(L_TM_AllPossibleTerminals.get(0));
            drawLines(L_L_STR_TerminalPointsList.get(0).get(0));
        } catch (Exception ex) {
            //Loader.dismiss();
            Helper.logger(ex);
        }

    }

    public void PlayButtonClickSound(){
        MenuActivity._buttonClick.start();
    }
    public static void clearLines() {
        if (_redPolyLine != null && _redPolyLine.getPoints().size() >0) {
            _redPolyLine.remove();
        }
        if(_magentaPolyLine != null && _magentaPolyLine.getPoints().size() >0) {
            _magentaPolyLine.remove();
        }
        if(!listLatLng.isEmpty()) {
            listLatLng.clear();
        }

    }
    private void drawLines(String points) {
        int width = _activity.getResources().getDisplayMetrics().widthPixels;
        int height = _activity.getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.20);
        List<LatLng> list = Helper.decodePoly(points);
        listLatLng.addAll(list);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : listLatLng) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width,height,padding);
        _map.animateCamera(mCameraUpdate);
        MenuActivity._IsPolyLineDrawn=false;
        _map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(!MenuActivity._IsPolyLineDrawn){
                    animatePolyLine();
                    MenuActivity._IsPolyLineDrawn=true;
                }
            }
        });

    }
    private void animatePolyLine() {
        PolylineOptions magentaPolyOptions = new PolylineOptions();
        magentaPolyOptions.width(5);
        magentaPolyOptions.color(Color.MAGENTA);
        magentaPolyOptions.startCap(new SquareCap());
        magentaPolyOptions.endCap(new SquareCap());
        magentaPolyOptions.jointType(ROUND);
        magentaPolyOptions.addAll(listLatLng);
        _magentaPolyLine = this._map.addPolyline(magentaPolyOptions);

        PolylineOptions redPolyOptions = new PolylineOptions();
        redPolyOptions.width(5);
        redPolyOptions.color(Color.RED);
        redPolyOptions.startCap(new SquareCap());
        redPolyOptions.endCap(new SquareCap());
        redPolyOptions.jointType(ROUND);
        _redPolyLine = this._map.addPolyline(redPolyOptions);

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                List<LatLng> points = _magentaPolyLine.getPoints();
                int percentValue = (int) animator.getAnimatedValue();
                int size = points.size();
                int newPoints = (int) (size * (percentValue / 100.0f));
                List<LatLng> p = points.subList(0, newPoints);
                _redPolyLine.setPoints(p);

            }
        });
        animator.start();
    }
    public void GetArrivalTimeOfLoopBasedOnSelectedStation(final Terminal TM_CurrentDest) {
        try {
            if (TM_CurrentDest != null) {
                Helper.InitializeSearchingRouteUI(false, false,"Searching for nearest E-loop...", null,null);
                final List<Terminal> LTM_DestList_Sorted = MenuActivity._terminalList;
                Collections.sort(LTM_DestList_Sorted, Terminal.DestinationComparators.ORDER_OF_ARRIVAL);
                FB = FirebaseDatabase.getInstance();
                VehicleDestinationDatabaseReference = FB.getReference("vehicle_destinations");
                VehicleDestinationDatabaseReference.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot DS_Vehicle_Destinantions) {
                        ValidateIfEloopIsWithinSameRoute(TM_CurrentDest, DS_Vehicle_Destinantions, LTM_DestList_Sorted);

                    }
                });

            }
        } catch (Exception ex) {

            Helper.logger(ex);
        }
    }
    private void ValidateIfEloopIsWithinSameRoute(final Terminal TM_CurrentDest, final DataSnapshot DS_Vehicle_Destination, final List<Terminal> L_TM_DestList_Sorted){
        try{

            FB = FirebaseDatabase.getInstance();
            DriversDatabaseReference = FB.getReference("drivers");
            DriversDatabaseReference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot DS_Drivers) {
                    if (DS_Vehicle_Destination.getChildren() != null && DS_Drivers.getChildren() != null) {
                        Boolean found = false, loopAwaiting = false;
                        int ctr = 0;
                        _IsAllLoopParked = true;
                        for (Terminal TM_Entry : L_TM_DestList_Sorted) {
                            if (Helper.IsSameRoute(TM_Entry, TM_CurrentDest) && (TM_Entry.OrderOfArrival == TM_CurrentDest.OrderOfArrival)) {
                                for (DataSnapshot v : DS_Vehicle_Destination.getChildren()) {
                                    String StationName = v.getKey().toString(), StationNameWithTblRouteId = TM_Entry.Value + "_" + TM_Entry.getTblRouteID();
                                    if (StationNameWithTblRouteId.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                        loopAwaiting = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? true : false;
                                        String loopId = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? Helper.CleanEloopName(v.child("Dwell").getValue().toString()) : Helper.CleanEloopName(v.child("LoopIds").getValue().toString());
                                        if (loopAwaiting && Helper.IsEloopWithinSameRouteID(DS_Drivers, TM_CurrentDest, loopId)) {
                                            Helper.InitializeSearchingRouteUI(true, false, _GlobalResource.getString(R.string.VEHICLE_already_waiting), null, null);
                                            loopAwaiting = true;
                                            _IsAllLoopParked = false;
                                            found = true;
                                            GetTimeRemainingFromGoogle(Integer.parseInt(loopId), TM_CurrentDest);
                                            AttachListenerToLoop(Integer.parseInt(loopId), TM_CurrentDest);
                                        } else continue;

                                    }

                                }
                                if (loopAwaiting)
                                    break;
                            } else if (Helper.IsSameRoute(TM_Entry, TM_CurrentDest) && (TM_Entry.OrderOfArrival == 1 || TM_CurrentDest.OrderOfArrival == 1)) {
                                for (Terminal dl2 : L_TM_DestList_Sorted) {
                                    for (DataSnapshot v : DS_Vehicle_Destination.getChildren()) {
                                        String StationName = v.getKey().toString(), StationNameWithTblRouteId = dl2.Value + "_" + TM_Entry.getTblRouteID();
                                        if (StationNameWithTblRouteId.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                            String loopId = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? Helper.CleanEloopName(v.child("Dwell").getValue().toString()) : Helper.CleanEloopName(v.child("LoopIds").getValue().toString());
                                            if ((!loopId.equals("") && !loopId.equals(",")) && !found) {
                                                List<String> temploopids = Arrays.asList(loopId.split(","));
                                                for (String tli : temploopids
                                                        ) {
                                                    if (!tli.equals(""))
                                                        _ListOfLoops.add(Integer.parseInt(tli));
                                                }
                                                Collections.sort(_ListOfLoops);
                                                if (_ListOfLoops.size() > 0 && Helper.IsEloopWithinSameRouteID(DS_Drivers, TM_CurrentDest, _ListOfLoops.get(0).toString())) {
                                                    _IsAllLoopParked = false;
                                                    found = true;
                                                    //VehicleDestinationDatabaseReference.removeEventListener(LoopArrivalEventListener);
                                                    //Jul-22
                                                     GetTimeRemainingFromGoogle(_ListOfLoops.get(0), TM_CurrentDest);
                                                    // Toast.makeText(_context, "if (order of arrival =0) hit!", Toast.LENGTH_LONG).show();

                                                    AttachListenerToLoop(_ListOfLoops.get(0), TM_CurrentDest);
                                                }
                                                _ListOfLoops.clear();
                                                break;
                                            } else continue;
                                        } else continue;

                                    }
                                }
                                if (found)
                                    break;

                            } else if (Helper.IsSameRoute(TM_Entry, TM_CurrentDest) && (TM_Entry.OrderOfArrival < TM_CurrentDest.OrderOfArrival)) {
                                for (DataSnapshot v : DS_Vehicle_Destination.getChildren()) {
                                    String StationName = v.getKey().toString(), StationNameWithTblRouteId = TM_Entry.Value + "_" + TM_Entry.getTblRouteID();
                                    if (StationNameWithTblRouteId.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                        String loopId = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? Helper.CleanEloopName(v.child("Dwell").getValue().toString()) : Helper.CleanEloopName(v.child("LoopIds").getValue().toString());
                                        if ((!loopId.equals("") && !loopId.equals(",")) && !found) {
                                            List<String> temploopids = Arrays.asList(loopId.split(","));
                                            for (String tli : temploopids) {
                                                if (!tli.equals(""))
                                                    _ListOfLoops.add(Integer.parseInt(tli));
                                            }
                                            Collections.sort(_ListOfLoops);
                                            if (_ListOfLoops.size() > 0 && Helper.IsEloopWithinSameRouteID(DS_Drivers, TM_CurrentDest, _ListOfLoops.get(0).toString())) {
                                                found = true;
                                                _IsAllLoopParked = false;
                                                GetTimeRemainingFromGoogle(_ListOfLoops.get(0), TM_CurrentDest);
                                                AttachListenerToLoop(_ListOfLoops.get(0), TM_CurrentDest);
                                                break;
                                            }
                                            _ListOfLoops.clear();
                                            break;
                                        } else continue;
                                    } else continue;

                                }
                                if (found)
                                    break;

                            } else continue;

                        }
                    }
                    if (_IsAllLoopParked) {
                        Helper.InitializeSearchingRouteUI(true, true, "Unfortunately, all E-loops are parked (or offline)",null,null);

                    }

                }
            });
        }
        catch(Exception ex){

        }
    }
    public void GetTimeRemainingFromGoogle(Integer INT_LoopID, final Terminal TM_Destination) {

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
                    _AssignedELoop = dataSnapshot.child("deviceid").getValue().toString();
                    Call<Directions> call = service.getDistanceDuration("metric", TM_Destination.Lat + "," + TM_Destination.Lng, dataSnapshot.child("Lat").getValue() + "," + dataSnapshot.child("Lng").getValue(), "driving");
                    call.enqueue(new Callback<Directions>() {
                        @Override
                        public void onResponse(Response<Directions> response, Retrofit retrofit) {
                            try {
                                for (int i = 0; i < response.body().getRoutes().size(); i++) {
                                    String TimeofArrival = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();
                                    String Distance = response.body().getRoutes().get(0).getLegs().get(0).getDistance().getText();
                                    Helper.InitializeSearchingRouteUI(true,false, Helper.GetEloopEntry(_AssignedELoop).PlateNumber,Distance, TimeofArrival.toString());
                                }
                            } catch (Exception ex) {
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
    private void AttachListenerToLoop(final Integer INT_LoopID, final Terminal TM_CurrentDestination){
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
                        String routeId = String.valueOf(TM_CurrentDestination.getTblRouteID());
                        if (!Firebase_routeIDs.contains(routeId)) {
                            _S_VehicleDestinationDatabaseReference.removeEventListener(this);
                            //Toast.makeText(_context, "Listener removed!", Toast.LENGTH_SHORT).show();
                            GetArrivalTimeOfLoopBasedOnSelectedStation(TM_CurrentDestination);
                        } else if (!MenuActivity._HasExitedInfoLayout) {
                            GetTimeRemainingFromGoogle(INT_LoopID, TM_CurrentDestination);
                            //Toast.makeText(_context, "Listener attached! RouteID:" + routeId + " FirebaseRouteID: " + Firebase_routeIDs, Toast.LENGTH_SHORT).show();
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
    public static void RemoveListenerFromLoop(){
        if(_S_VehicleDestinationDatabaseReference!=null) {
            _S_VehicleDestinationDatabaseReference.removeEventListener(_SingleLoopChildListenerForSelectedTerminal);
            _S_VehicleDestinationDatabaseReference=null;
            _SingleLoopChildListenerForSelectedTerminal=null;
        }
        //Toast.makeText(_context, "Listener removed!", Toast.LENGTH_SHORT).show();
    }

}
