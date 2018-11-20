package com.umandalmead.samm_v1;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.Chronometer;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners.AddVehicleMarkers;
import com.umandalmead.samm_v1.POJO.HTMLDirections.Directions;
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
import static com.umandalmead.samm_v1.Constants.LOG_TAG;
import static com.umandalmead.samm_v1.MenuActivity._GlobalResource;
import static com.umandalmead.samm_v1.MenuActivity._LL_Arrival_Info;
import static com.umandalmead.samm_v1.MenuActivity._RouteStepsText;
import static com.umandalmead.samm_v1.MenuActivity._TV_TimeofArrival;
import static com.umandalmead.samm_v1.MenuActivity._TV_Vehicle_Description;
import static com.umandalmead.samm_v1.MenuActivity._TV_Vehicle_Identifier;
import static com.umandalmead.samm_v1.MenuActivity._TimeOfArrivalTextView;
import static  com.umandalmead.samm_v1.MenuActivity._LoopArrivalProgress;
import static com.umandalmead.samm_v1.MenuActivity._currentLineIDSelected;
import static com.umandalmead.samm_v1.MenuActivity._googleMap;
import static com.umandalmead.samm_v1.MenuActivity._userCurrentLoc;

/**
 * Created by MeadRoseAnn on 01/07/2018.
 */


public class AnalyzeForBestRoutes extends AsyncTask<Void, Void, List<Terminal>> {
    public static Polyline _line;

    Context _context;
    Activity _activity;
    GoogleMap _map;
    String progressMessage;
    LatLng _currentLocation;
    List<Terminal> _possibleTerminals;
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
    private static DatabaseReference DriversDatabaseReference, VehicleDestinationDatabaseReference, _S_VehicleDestinationDatabaseReference;
    private String _loopIds = "";
    private List<Integer> _ListOfLoops = new ArrayList<Integer>();
    private String _AssignedELoop = "";
    private ValueEventListener LoopArrivalEventListener;
    private static ChildEventListener _SingleLoopChildListenerForSelectedTerminal;
    private Boolean _IsAllLoopParked = true;
    public static Polyline _redPolyLine, _magentaPolyLine;
    private Boolean _ISEloopIsWithinSameRoute = false;
    private LoaderDialog Loader;

    private static List<LatLng> listLatLng = new ArrayList<>();
    /**
     * This is the generic format in accessing data from mySQL
     *
     * @param context
     * @param activity
     */
    public AnalyzeForBestRoutes(Context context, Activity activity, GoogleMap map, LatLng currentLocation, List<Terminal> possibleTerminals, Terminal choseTerminal) {
        this._context = context;
        this._activity = activity;
        this._map = map;
        this.Loader = new LoaderDialog(_activity, null, progressMessage);
        this._currentLocation = currentLocation;
        this._possibleTerminals = possibleTerminals;
        this._SelectedTerminal = choseTerminal;

    }



    @Override
    protected void onPreExecute() {
        try {
            super.onPreExecute();
            Loader = new LoaderDialog(_activity, MenuActivity._GlobalResource.getString(R.string.dialog_title_analyzing_routes),MenuActivity._GlobalResource.getString(R.string.dialog_message_analyzing_routes));
            MenuActivity._buttonClick = MediaPlayer.create(_context, R.raw.button_click);
            Loader.show();

        } catch (Exception ex) {
            Helper.logger(ex,true);
        }


    }

    @Override
    protected List<Terminal> doInBackground(Void... voids) {
        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context)) {
            HashMap<Integer, Integer> destinationId_distance = new HashMap<>();
            String url = _GlobalResource.getString(R.string.GM_maps_url);
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
                    d.distanceFromCurrentLocation = helper.getDistanceFromLatLonInKm(
                            new LatLng(_currentLocation.latitude, _currentLocation.longitude),
                            new LatLng(d.getLat(), d.getLng()));
//                        destinationId_distance.put(dialog.ID, directions.getRoutes().get(1).getLegs().get(1).getDistance().getValue());
                    Log.i(LOG_TAG, "Success retrofit");
                } catch (MalformedURLException ex) {
                    helper.logger(ex,true);

                } catch (IOException ex) {
                    helper.logger(ex,true);
                } catch (Exception ex) {
                    helper.logger(ex,true);
                }

            }
            try {
                Collections.sort(_possibleTerminals);
            } catch (Exception ex) {
                Helper.logger(ex,true);
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
                Helper.logger(ex,true);
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


    @Override
    protected void onPostExecute(List<Terminal> L_TM_topTerminals) {
        try {
            new asyncPrepareRouteData(_activity,_context,L_TM_topTerminals,_SelectedTerminal, _googleMap, Loader).execute();
        } catch (Exception ex) {
            Helper.logger(ex,true);
        }

    }

    public void PlayButtonClickSound(){
        MenuActivity._buttonClick.start();
    }




}
