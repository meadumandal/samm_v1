package com.evolve.evx;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.evolve.evx.EntityObjects.Terminal;
import com.evolve.evx.POJO.HTMLDirections.Directions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.evolve.evx.Constants.LOG_TAG;
import static com.evolve.evx.MenuActivity._GlobalResource;
import static com.evolve.evx.MenuActivity._googleMap;

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
            Loader.show();

        } catch (Exception ex) {
            Helper.logger(ex,true);
        }


    }

    @Override
    protected List<Terminal> doInBackground(Void... voids) {
        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context) && MenuActivity._BOOL_IsGPSAcquired) {
            HashMap<Integer, Integer> destinationId_distance = new HashMap<>();
            String url = _GlobalResource.getString(R.string.GM_maps_url);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitMaps service = retrofit.create(RetrofitMaps.class);
            List<Terminal> topTerminals = new ArrayList<>();
            for (Terminal d : _possibleTerminals) {
                try {
                    Call<Directions> call = service.getDistanceDuration("metric", _currentLocation.latitude + "," + _currentLocation.longitude, d.Lat + "," + d.Lng, "walking");
                    Directions directions = call.execute().body();
                    d.directionsFromCurrentLocation = directions;
                    d.distanceFromCurrentLocation = helper.getDistanceFromLatLonInKm(
                            new LatLng(_currentLocation.latitude, _currentLocation.longitude),
                            new LatLng(d.getLat(), d.getLng()));
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
                    if(_possibleTerminals.get(counter).directionsFromCurrentLocation.getRoutes().size() > 0)
                    topTerminals.add(_possibleTerminals.get(counter));
                }
            } catch (Exception ex) {
                HashMap<String, Object> returnValues = new HashMap<String, Object>();
                returnValues.put("IsValid", 0);
                returnValues.put("Message", "Please check your Internet connection and ensure GPS services are turned on.");
                Helper.logger(ex,true);
                return null;
            }
            _topTerminals = topTerminals;
            return topTerminals;

        } else {
            HashMap<String, Object> returnValues = new HashMap<String, Object>();
            returnValues.put("IsValid", 0);
            returnValues.put("Message", "Please check your Internet connection and ensure GPS services are turned on.");
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



}
