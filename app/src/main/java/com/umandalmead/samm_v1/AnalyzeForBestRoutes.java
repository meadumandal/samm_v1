package com.umandalmead.samm_v1;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.Toast;


import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.umandalmead.samm_v1.POJO.Directions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.net.MalformedURLException;
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
import static com.umandalmead.samm_v1.MenuActivity._RouteStepsText;
import static com.umandalmead.samm_v1.MenuActivity._TimeOfArrivalTextView;
import static  com.umandalmead.samm_v1.MenuActivity._LoopArrivalProgress;

/**
 * Created by MeadRoseAnn on 01/07/2018.
 */


public class AnalyzeForBestRoutes extends AsyncTask<Void, Void, List<Terminal>> {
    public static Polyline _line;
    final String TAG = "mead";
    Context _context;
    Activity _activity;
    ProgressDialog progDialog;
    GoogleMap _map;
    String progressMessage;
    LatLng _currentLocation;
    List<Terminal> _possibleTerminals;
    FragmentManager _supportFragmentManager;
    List<Terminal> _topTerminals;
    List<String> _AllSteps = new ArrayList<String>();
    Terminal _SelectedTerminal;
    List<String> _AllPoints = new ArrayList<String>();
    List<Polyline> _AllPoly = new ArrayList<Polyline>();
    List<String> _AllTotalTime = new ArrayList<String>();
    List<List<String>> _AllDirectionsSteps = new ArrayList<List<String>>();
    List<List<String>> _AllTerminalPoints = new ArrayList<List<String>>();
    List<Polyline> polyLines = new ArrayList<>();
    private FirebaseDatabase FB;
    private static DatabaseReference VehicleDestinationDatabaseReference, _S_VehicleDestinationDatabaseReference;
    private String _loopIds = "";
    private List<Integer> _ListOfLoops = new ArrayList<Integer>();
    private String _AssignedELoop = "";
    private ValueEventListener LoopArrivalEventListener;
    private static ChildEventListener _SingleLoopChildListenerForSelectedTerminal;
    private Boolean _IsAllLoopParked = true;
    private Boolean _IsLoopResultsFound = false;
    public static Polyline _redPolyLine, _magentaPolyLine;

    private static List<LatLng> listLatLng = new ArrayList<>();
    /**
     * This is the generic format in accessing data from mySQL
     *
     * @param context
     * @param activity
     */
    public AnalyzeForBestRoutes(Context context, Activity activity, GoogleMap map, LatLng currentLocation, FragmentManager supportFragmentManager, List<Terminal> possibleTerminals, Terminal choseTerminal) {
        this._context = context;
        this._activity = activity;
        this._map = map;
        this._supportFragmentManager = supportFragmentManager;
        progDialog = new ProgressDialog(this._activity);
        progDialog.setMessage(progressMessage);
        this._currentLocation = currentLocation;
        this._possibleTerminals = possibleTerminals;
        this._SelectedTerminal = choseTerminal;

    }

    public static void clearLines() {
        if (_redPolyLine !=null &&  _magentaPolyLine != null && !listLatLng.isEmpty()) {
            _redPolyLine.remove();
            _magentaPolyLine.remove();
            listLatLng.clear();
        }
    }

    @Override
    protected void onPreExecute() {
        try {
            super.onPreExecute();
            progDialog.setMax(100);
            progDialog.setMessage("Please wait as we search for the best route");
            progDialog.setTitle("Analyzing Routes");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(false);
            progDialog.show();
            MenuActivity._buttonClick = MediaPlayer.create(_context, R.raw.button_click);

        } catch (Exception ex) {
            Helper.logger(ex);
        }


    }

    @Override
    protected List<Terminal> doInBackground(Void... voids) {
        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context)) {
            HashMap<Integer, Integer> destinationId_distance = new HashMap<>();
            String url = "https://maps.googleapis.com/maps/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitMaps service = retrofit.create(RetrofitMaps.class);
            List<Terminal> topTerminals = new ArrayList<>();
            for (Terminal d : _possibleTerminals) {
                Call<Directions> call = service.getDistanceDuration("metric", _currentLocation.latitude + "," + _currentLocation.longitude, d.Lat + "," + d.Lng, "walking");
                try {
                    Directions directions = call.execute().body();
                    d.directionsFromCurrentLocation = directions;
//                        destinationId_distance.put(dialog.ID, directions.getRoutes().get(1).getLegs().get(1).getDistance().getValue());
                    Log.i(TAG, "Success retrofit");
                } catch (MalformedURLException ex) {
                    Log.e(TAG, ex.getMessage());
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }

            }
            try {
                Collections.sort(_possibleTerminals);
            } catch (Exception ex) {
                Helper.logger(ex);
            }

            Integer counter = 0;
            Integer limit = 0;
            if (_possibleTerminals.size() > 3)
                limit = 3;
            else
                limit = _possibleTerminals.size();

            try {
                for (counter = 0; counter < limit; counter++) {
                    topTerminals.add(_possibleTerminals.get(counter));
                }
            } catch (Exception ex) {
                HashMap<String, Object> returnValues = new HashMap<String, Object>();
                returnValues.put("IsValid", 0);
                returnValues.put("Message", ex.getMessage());
                Helper.logger(ex);
                return null;
            }
            _topTerminals = topTerminals;
            return topTerminals;

        } else {
            HashMap<String, Object> returnValues = new HashMap<String, Object>();
            returnValues.put("IsValid", 0);
            returnValues.put("Message", "You are not connected to the internet. Please check your connection and try again.");
            return null;
        }

    }

    private List<LatLng> decodePoly(String encoded) {
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

    private void drawLines(String points) {
        PolylineOptions magentaPolyOptions = new PolylineOptions();
        List<LatLng> list = decodePoly(points);
        magentaPolyOptions.width(10);
        magentaPolyOptions.color(Color.MAGENTA);
        magentaPolyOptions.startCap(new SquareCap());
        magentaPolyOptions.endCap(new SquareCap());
        magentaPolyOptions.jointType(ROUND);
        _magentaPolyLine = this._map.addPolyline(magentaPolyOptions);

        PolylineOptions redPolyOptions = new PolylineOptions();
        redPolyOptions.width(10);
        redPolyOptions.color(Color.RED);
        redPolyOptions.startCap(new SquareCap());
        redPolyOptions.endCap(new SquareCap());
        redPolyOptions.jointType(ROUND);
        _redPolyLine = this._map.addPolyline(redPolyOptions);
        listLatLng.addAll(list);
        animatePolyLine();
//        _line = this._map.addPolyline(new PolylineOptions()
//                .addAll(list)
//                .width(5f)
//                .color(Color.RED)
//                .geodesic(true)
//
//        );
        //polyLines.add(_line);
    }
    private void animatePolyLine() {

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {

                List<LatLng> latLngList = _magentaPolyLine.getPoints();
                int initialPointSize = latLngList.size();
                int animatedValue = (int) animator.getAnimatedValue();
                int newPoints = (animatedValue * listLatLng.size()) / 100;

                if (initialPointSize < newPoints ) {
                    latLngList.addAll(listLatLng.subList(initialPointSize, newPoints));
                    _magentaPolyLine.setPoints(latLngList);
                }


            }
        });

        animator.addListener(polyLineAnimationListener);
        animator.start();

    }
    private void addMarker(LatLng destination) {

        MarkerOptions options = new MarkerOptions();
        options.position(destination);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        this._map.addMarker(options);

    }
    Animator.AnimatorListener polyLineAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
            addMarker(listLatLng.get(listLatLng.size()-1));
        }

        @Override
        public void onAnimationEnd(Animator animator) {

            List<LatLng> _redLatLng = _magentaPolyLine.getPoints();
            List<LatLng> _pinkLatLng = _redPolyLine.getPoints();

            _pinkLatLng.clear();
            _pinkLatLng.addAll(_redLatLng);
            _redLatLng.clear();

            _magentaPolyLine.setPoints(_redLatLng);
            _redPolyLine.setPoints(_pinkLatLng);

            _magentaPolyLine.setZIndex(2);

            animator.start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {


        }
    };

    @Override
    protected void onPostExecute(List<Terminal> topTerminals) {
        //"topTerminals" contains the top 3 nearest terminal from user's CURRENT location

        try {
            int ctr = 0;
            if (_line != null) {
                _line.setVisible(false);
            }
            for (Terminal terminal : topTerminals) {
                String TotalTime = "";
                List<String> DirectionSteps = new ArrayList<String>();
                for (int i = 0; i < terminal.directionsFromCurrentLocation.getRoutes().size(); i++) {
                    TotalTime = terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDuration().getText();
                    String encodedString = terminal.directionsFromCurrentLocation.getRoutes().get(0).getOverviewPolyline().getPoints();
                    //drawLines(encodedString);
                    _AllPoints.add(encodedString);
                    _AllTotalTime.add(TotalTime);
                }
                for (int x = 0; x < terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getInstructions().size(); x++) {
                    String Instructions = terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getInstructions().get(x).getSteps().toString();
                    DirectionSteps.add(Instructions);
                }
                _AllTerminalPoints.add(_AllPoints);
                _AllDirectionsSteps.add(DirectionSteps);
                ctr++;
            }
            createRouteTabs(_AllTotalTime, _AllDirectionsSteps, _topTerminals, _AllTerminalPoints, ctr);
            progDialog.dismiss();

            ((MenuActivity)this._activity).ShowRouteTabsAndSlidingPanel();
            _RouteStepsText.loadDataWithBaseURL("file:///android_res/", SelectedTabInstructions(_AllDirectionsSteps.get(0), _AllTotalTime.get(0), _topTerminals.get(0)), "text/html; charset=utf-8", "UTF-8", null);
            MenuActivity._selectedPickUpPoint = _topTerminals.get(0);
            drawLines(_AllTerminalPoints.get(0).get(0));


        } catch (Exception ex) {
            progDialog.dismiss();
            Helper.logger(ex);
        }

    }

    public void createRouteTabs(final List<String> TotalTimeList, final List<List<String>> DirectionStepsList, final List<Terminal> AllPossibleTerminals, final List<List<String>> TerminalPointsList, final int ctr) {
        //For Routes Tabs
        try {
            if (AllPossibleTerminals.size() == 0 || AllPossibleTerminals == null)
                throw new Exception("Unable to find route for this destination.");
            final TabLayout RouteTabs = (TabLayout) this._activity.findViewById(R.id.route_tablayout);
            RouteTabs.removeAllTabs();
            for (Terminal entry : AllPossibleTerminals) {
                RouteTabs.addTab(RouteTabs.newTab().setText(entry.Description));
            }
            RouteTabs.setTabGravity(TabLayout.GRAVITY_FILL);

            final ViewPager viewPager = (ViewPager) this._activity.findViewById(R.id.routepager);
            final PagerAdapter adapter = new PagerAdapter(_supportFragmentManager, RouteTabs.getTabCount());
            viewPager.setAdapter(adapter);
            viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(RouteTabs));


            RouteTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    ((MenuActivity)AnalyzeForBestRoutes.this._activity).UpdateUI(Enums.UIType.SHOWING_ROUTES);
                    viewPager.setCurrentItem(tab.getPosition());
                    _RouteStepsText.loadDataWithBaseURL("file:///android_res/", SelectedTabInstructions(DirectionStepsList.get(tab.getPosition()), TotalTimeList.get(tab.getPosition()), AllPossibleTerminals.get(tab.getPosition())), "text/html; charset=utf-8", "UTF-8", null);
                    MenuActivity._selectedPickUpPoint = AllPossibleTerminals.get(tab.getPosition());
                    clearLines();
                    drawLines(TerminalPointsList.get(tab.getPosition()).get(tab.getPosition()));
                    GetArrivalTimeOfLoopBasedOnSelectedStation(AllPossibleTerminals.get(tab.getPosition()));
                    _TimeOfArrivalTextView.setText("Searching for nearest E-loop...");
                    _TimeOfArrivalTextView.setBackgroundResource(R.drawable.pill_shaped_eloop_status);
                    _LoopArrivalProgress.setVisibility(View.VISIBLE);
                    RemoveListenerFromLoop();
                    PlayButtonClickSound();

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            ((MenuActivity)this._activity).UpdateUI(Enums.UIType.SHOWING_ROUTES);
            drawLines(TerminalPointsList.get(0).get(0));
            MenuActivity._selectedPickUpPoint = AllPossibleTerminals.get(0);
            GetArrivalTimeOfLoopBasedOnSelectedStation(AllPossibleTerminals.get(0));
            _RouteStepsText = (WebView) this._activity.findViewById(R.id.route_steps);
            _RouteStepsText.loadDataWithBaseURL("file:///android_res/", SelectedTabInstructions(DirectionStepsList.get(0), TotalTimeList.get(0), AllPossibleTerminals.get(0)), "text/html; charset=utf-8", "UTF-8", null);


        } catch (Exception ex) {
            progDialog.dismiss();
            Helper.logger(ex);
        }

    }

    public void GetArrivalTimeOfLoopBasedOnSelectedStation(final Terminal currentDest) {
        try {
            String res = "";
            if (currentDest != null) {
                final List<Terminal> DestList = MenuActivity._terminalList;

                Collections.sort(DestList, Terminal.DestinationComparators.ORDER_OF_ARRIVAL);
                FB = FirebaseDatabase.getInstance();
                VehicleDestinationDatabaseReference = FB.getReference("vehicle_destinations");
                VehicleDestinationDatabaseReference.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        //if (currentData.getValue()!=null)
                            return Transaction.success(currentData);

                        //return Transaction.abort();
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getChildren() != null) {
                            Boolean found = false, loopAwaiting = false;
                            int ctr = 0;
                            _IsAllLoopParked = true;
                            for (Terminal dl : DestList) {
                                if (IsSameRoute(dl, currentDest) && (dl.OrderOfArrival == currentDest.OrderOfArrival)) {
                                    for (DataSnapshot v : dataSnapshot.getChildren()) {
                                        String StationName = v.getKey().toString(), StationNameWithTblRouteId = dl.Value+"_"+dl.getTblRouteID();
                                        if (StationNameWithTblRouteId.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                            loopAwaiting = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(","))  ? true : false;
                                            String loopId = (!v.child("Dwell").getValue().toString().equals("") && !v.child("Dwell").getValue().toString().equals(",")) ? CleanEloopName(v.child("Dwell").getValue().toString()) : CleanEloopName(v.child("LoopIds").getValue().toString());
                                            if (loopAwaiting) {
                                                _TimeOfArrivalTextView.setText(Html.fromHtml("An E-loop is already waiting!"));
                                                _LoopArrivalProgress.setVisibility(View.GONE);
                                                _IsLoopResultsFound = true;
                                                loopAwaiting = true;
                                                _IsAllLoopParked = false;
                                                AttachListenerToLoop(Integer.parseInt(loopId), currentDest);
                                                break;
                                            } else continue;

                                        }

                                    }
                                    if (loopAwaiting)
                                        break;
                                }
                                else if (IsSameRoute(dl, currentDest) && (dl.OrderOfArrival == 1 || currentDest.OrderOfArrival == 1)) {
                                    for (Terminal dl2 : DestList) {
                                        for (DataSnapshot v : dataSnapshot.getChildren()) {
                                            String StationName = v.getKey().toString(), StationNameWithTblRouteId = dl2.Value+"_"+dl.getTblRouteID();
                                            if (StationNameWithTblRouteId.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                                _loopIds = v.child("LoopIds").getValue().toString();
                                                if ((!_loopIds.equals("") && !_loopIds.equals(",")) && !found) {
                                                    _IsAllLoopParked = false;
                                                    found = true;
                                                    List<String> temploopids = Arrays.asList(_loopIds.split(","));
                                                    for (String tli : temploopids
                                                            ) {
                                                        if(!tli.equals(""))
                                                            _ListOfLoops.add(Integer.parseInt(tli));
                                                    }
                                                    Collections.sort(_ListOfLoops);
                                                    if (_ListOfLoops.size() > 0) {
                                                        //VehicleDestinationDatabaseReference.removeEventListener(LoopArrivalEventListener);
                                                        //Jul-22
                                                        GetTimeRemainingFromGoogle(_ListOfLoops.get(0), currentDest);
                                                        Toast.makeText(_context, "if (order of arrival =0) hit!", Toast.LENGTH_LONG).show();
                                                        _IsLoopResultsFound = true;
                                                        AttachListenerToLoop(_ListOfLoops.get(0), currentDest);
                                                        // LoopArrivalProgress.setVisibility(View.INVISIBLE);
                                                    }
                                                    _ListOfLoops.clear();
                                                    break;
                                                } else continue;
                                            } else continue;

                                        }
                                    }
                                    if (found)
                                        break;

                                }
                                else if (IsSameRoute(dl, currentDest) && (dl.OrderOfArrival < currentDest.OrderOfArrival)) {
                                    for (DataSnapshot v : dataSnapshot.getChildren()) {
                                        String StationName = v.getKey().toString(), StationNameWithTblRouteId = dl.Value+"_"+dl.getTblRouteID();
                                        if (StationNameWithTblRouteId.equals(StationName) && Integer.parseInt(v.child("OrderOfArrival").getValue().toString()) != 0) {
                                            _loopIds = v.child("LoopIds").getValue().toString();
                                            if(_loopIds.equals("")){
                                                _loopIds = v.child("Dwell").getValue().toString();
                                            }
                                            if ((!_loopIds.equals("") && !_loopIds.equals(",")) && !found) {
                                                _IsAllLoopParked = false;
                                                found = true;
                                                List<String> temploopids = Arrays.asList(_loopIds.split(","));
                                                for (String tli : temploopids) {
                                                    if(!tli.equals(""))
                                                        _ListOfLoops.add(Integer.parseInt(tli));
                                                }
                                                Collections.sort(_ListOfLoops);
                                                if (_ListOfLoops.size() > 0) {
                                                    // VehicleDestinationDatabaseReference.removeEventListener(LoopArrivalEventListener);
                                                    //Jul 22
                                                    GetTimeRemainingFromGoogle(_ListOfLoops.get(0), currentDest);
                                                    Toast.makeText(_context, "else if hit!", Toast.LENGTH_LONG).show();
                                                    _IsLoopResultsFound = true;
                                                    AttachListenerToLoop(_ListOfLoops.get(0), currentDest);
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
                            _TimeOfArrivalTextView.setBackgroundResource(R.drawable.pill_shaped_eloop_status_error);
                            _TimeOfArrivalTextView.setText(Html.fromHtml("Unfortunately, all E-loops are parked (or offline)"));
                            _LoopArrivalProgress.setVisibility(View.GONE);
                            _IsLoopResultsFound = true;
                        }
                    }
                });

//                if (!_IsLoopResultsFound)
//                {
//                    _TimeOfArrivalTextView.setText(Html.fromHtml("It seems like all e-loops are offline!"));
//                    _LoopArrivalProgress.setVisibility(View.GONE);
//                }

            }
        } catch (Exception ex) {

            Helper.logger(ex);
        }
    }

    public void GetTimeRemainingFromGoogle(Integer LoopId, final Terminal dest) {
        if (LoopId != null) {
            FB = FirebaseDatabase.getInstance();
            VehicleDestinationDatabaseReference = FB.getReference("drivers").child(LoopId.toString()); //database.getReference("users/"+ _sessionManager.getUsername() + "/connections");
            VehicleDestinationDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    Call<Directions> call = service.getDistanceDuration("metric", dest.Lat + "," + dest.Lng, dataSnapshot.child("Lat").getValue() + "," + dataSnapshot.child("Lng").getValue(), "driving");
                    call.enqueue(new Callback<Directions>() {
                        @Override
                        public void onResponse(Response<Directions> response, Retrofit retrofit) {
                            try {
                                for (int i = 0; i < response.body().getRoutes().size(); i++) {
                                    String TimeofArrival = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();
                                    String Distance = response.body().getRoutes().get(0).getLegs().get(0).getDistance().getText();
                                    _TimeOfArrivalTextView.setText(Html.fromHtml("<i>E-Loop " + Helper.GetEloopEntry(_AssignedELoop)+ " (" + Distance + " away) will arrive within: </i><b>" + TimeofArrival.toString() + ".</b>"));
                                    _TimeOfArrivalTextView.setBackgroundResource(R.drawable.pill_shaped_eloop_status);
                                }

                                _LoopArrivalProgress.setVisibility(View.GONE);
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

    public String SelectedTabInstructions(List<String> StepsList, String TT, Terminal Terminal) {
        String Step =
                "<hr/><h3 style='padding-left:5%;'>Suggested Actions</h3><body style='margin: 0; padding: 0'><table style='padding-left:5%; padding-right:2%;'><tr><td width='20%'><userImg style='height:60%; border-radius:50%;' src= 'drawable/ic_walking.png'></td>" +
//                        "<td style='padding-left:7%;'><medium style='background:#2196F3; color:white;border-radius:10%; padding: 7px;'>WALK</medium></td></tr>" +
                        "<td style='padding-left:7%;'><b>Walk your way to " + Terminal.Value + " Terminal </b></td></tr>" +
                        "<tr><td width='20%' style='text-align:center'><small>" + CleanTotalTime(TT) + "</small></td><td></td></tr>";

        if (StepsList != null) {
            for (int x = 0; x < StepsList.size(); x++) {
                Step += "<tr><td></td><td>" + (x + 1) + ". " + CleanDirectionStep(StepsList.get(x)) + ".</td><tr>";
                if ((x + 1) == StepsList.size()) {
                    Step += "<tr><td></td><td>" + (x + 2) + ". " + GenerateFinalStep(_SelectedTerminal, Terminal);
                }
            }
        }
        return Step + "</table></body>";
    }

    public String CleanDirectionStep(String str) {
        if (str != null) {
            if (str.contains("onto")) {
                str = str.replace("onto", "on to");
            }
            if (str.contains("<div style=\"font-size:0.9em\">")) {
                str = str.replace("<div style=\"font-size:0.9em\">", " ");
            }
            if (str.contains("</div>")) {
                str = str.replace("</div>", "");
            }
        }

        return str;
    }

    public String CleanTotalTime(String str) {
        if (str != null) {
            if (str.contains("hours")) {
                str = str.replace("hours", "h");
            }
            if (str.contains("mins")) {
                str = str.replace("mins", "min");
            }
        }

        return str;
    }

    public String GenerateFinalStep(Terminal end, Terminal start) {
        int dist = end.OrderOfArrival - start.OrderOfArrival;
        return "Ride the e-loop and alight after <b>" + dist + " stop" + (dist > 1 ? "s" : "") + "</b>.</td><tr>";
    }
    private Boolean IsSameRoute(Terminal term1, Terminal term2){
        Boolean result = false;
        if(term1.getTblRouteID() == term2.getTblRouteID())
            result =true;
        return result;
    }
    private void AttachListenerToLoop(final Integer LoopID, final Terminal currentDestination){
       // if(currentDestination != null)// && currentDestination.tblRouteID){
        FB = FirebaseDatabase.getInstance();
        _S_VehicleDestinationDatabaseReference = FB.getReference("drivers").child(LoopID.toString());//.child("routeIDs");
        if(_S_VehicleDestinationDatabaseReference !=null){
            Toast.makeText(this._context, "Listener Attached to: " + LoopID.toString(), Toast.LENGTH_LONG).show();
        }
        _SingleLoopChildListenerForSelectedTerminal = _S_VehicleDestinationDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().toUpperCase().equals("ROUTEIDS")) {
                    String Firebase_routeIDs = dataSnapshot.getValue().toString();
                    String routeId = String.valueOf(currentDestination.getTblRouteID());
                    if (!Firebase_routeIDs.contains(routeId)) {
                        _S_VehicleDestinationDatabaseReference.removeEventListener(this);
                        Toast.makeText(_context, "Listener removed!", Toast.LENGTH_SHORT).show();
                        GetArrivalTimeOfLoopBasedOnSelectedStation(currentDestination);
                    } else {
                        GetTimeRemainingFromGoogle(LoopID, currentDestination);
                        Toast.makeText(_context, "Listener attached! RouteID:" + routeId + " FirebaseRouteID: " + Firebase_routeIDs, Toast.LENGTH_SHORT).show();
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

        }


    private String GetRouteIDFromDestination(Terminal Destination){
        String result = null;
        try{
            result = Destination.getValue().split("_")[1];
        }
        catch (Exception ex){
            Toast.makeText(_context, "Error in getting Route ID", Toast.LENGTH_SHORT).show();
        }
        return result;

    }
    public static void RemoveListenerFromLoop(){
        if(_S_VehicleDestinationDatabaseReference!=null) {
            _S_VehicleDestinationDatabaseReference.removeEventListener(_SingleLoopChildListenerForSelectedTerminal);
        }
        //Toast.makeText(_context, "Listener removed!", Toast.LENGTH_SHORT).show();
    }
    private String CleanEloopName(String eloop){
        String result = "";
        try{
            String[] loops = eloop.split(",");
            result = loops[0];
        }catch(Exception ex){

        }
        return result;
    }
    public void PlayButtonClickSound(){
        MenuActivity._buttonClick.start();
    }


}
