package com.example.samm_v1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.samm_v1.EntityObjects.Destination;
import com.example.samm_v1.POJO.Directions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

import static com.example.samm_v1.MenuActivity.RouteStepsText;
import static com.example.samm_v1.MenuActivity.RouteTabLayout;
import static com.example.samm_v1.MenuActivity.SlideUpPanelContainer;



/**
 * Created by MeadRoseAnn on 01/07/2018.
 */


public class AnalyzeForBestRoutes extends AsyncTask<Void, Void, List<Destination>>{
    Context _context;
    Activity _activity;
    ProgressDialog progDialog;
    GoogleMap _map;
    String progressMessage;
    LatLng _currentLocation;
    List<Destination> _possibleTerminals;
    FragmentManager _supportFragmentManager;
    final String TAG = "mead";
    Polyline _line;

    /**
     *This is the generic format in accessing data from mySQL
     * @param context
     * @param activity
     */
    public AnalyzeForBestRoutes(Context context, Activity activity, GoogleMap map, LatLng currentLocation, FragmentManager supportFragmentManager, List<Destination> possibleTerminals)
    {
        this._context = context;
        this._activity = activity;
        this._map = map;
        this._supportFragmentManager = supportFragmentManager;
        progDialog = new ProgressDialog(this._activity);
        progDialog.setMessage(progressMessage);
        this._currentLocation = currentLocation;
        this._possibleTerminals = possibleTerminals;

    }
    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            progDialog.setMax(100);
            progDialog.setMessage("Please wait as we search for the best route");
            progDialog.setTitle("Analyzing Routes");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(false);
            progDialog.show();

        }
        catch(Exception e)
        {
            Toast.makeText(this._context, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }


    @Override
    protected List<Destination> doInBackground(Void... voids)
    {
        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {
            HashMap<Integer, Integer> destinationId_distance = new HashMap<>();
            String url = "https://maps.googleapis.com/maps/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitMaps service = retrofit.create(RetrofitMaps.class);
            List<Destination> topTerminals = new ArrayList<>();
            for(Destination d: _possibleTerminals) {
                    Call<Directions> call = service.getDistanceDuration("metric", _currentLocation.latitude + "," + _currentLocation.longitude, d.Lat + "," + d.Lng, "walking");
                    try {
                        Directions directions = call.execute().body();
                        d.directionsFromCurrentLocation = directions;
//                        destinationId_distance.put(d.ID, directions.getRoutes().get(1).getLegs().get(1).getDistance().getValue());
                        Log.i(TAG, "Success retrofit");
                    } catch (MalformedURLException ex) {
                        Log.e(TAG, ex.getMessage());
                    } catch (IOException ex) {
                        Log.e(TAG, ex.getMessage());
                    } catch (Exception ex)
                    {
                        Log.e(TAG, ex.getMessage());
                    }

            }
            try
            {
                Collections.sort(_possibleTerminals);
            }
            catch (Exception e)
            {
                Log.e(TAG, e.getMessage());
            }

            Integer counter =0;
            Integer limit = 0;
            if(_possibleTerminals.size() > 3)
                limit = 3;
            else
                limit = _possibleTerminals.size();

            try
            {
                for(counter = 0; counter < limit; counter++)
                {
                    topTerminals.add(_possibleTerminals.get(counter));
                }
            }
            catch(Exception e)
            {
                HashMap<String, Object> returnValues = new HashMap<String,Object>();
                returnValues.put("IsValid", 0);
                returnValues.put("Message", e.getMessage());
                return null;
            }
            return topTerminals;

        }
        else
        {
            HashMap<String, Object> returnValues = new HashMap<String,Object>();
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

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }
    private void drawLines(String points)
    {
        List<LatLng> list = decodePoly(points);
        _line = this._map.addPolyline(new PolylineOptions()
                .addAll(list)
                .width(5f)
                .color(Color.RED)
                .geodesic(true)
        );
    }
    @Override
    protected void onPostExecute(List<Destination> topTerminals)
    {
        //"topTerminals" contains the top 3 nearest terminal from user's CURRENT location

        try
        {

            for(Destination terminal: topTerminals)
            {
                String TotalDistance = "";
                String TotalTime = "";
                List<String> DirectionSteps = new ArrayList<String>();
                for (int i = 0; i < terminal.directionsFromCurrentLocation.getRoutes().size(); i++) {
                    TotalDistance = terminal.directionsFromCurrentLocation.getRoutes().get(i).getLegs().get(i).getDistance().getText();
                    TotalTime = terminal.directionsFromCurrentLocation.getRoutes().get(i).getLegs().get(i).getDuration().getText();
//                                showDistanceDuration.setText("Distance:" + distance + ", Duration:" + time);
                    String encodedString = terminal.directionsFromCurrentLocation.getRoutes().get(0).getOverviewPolyline().getPoints();
                    drawLines(encodedString);
                }
                for(int x = 0; x<terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getInstructions().size(); x++){
                    String Instructions =  terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getInstructions().get(x).getSteps().toString();
                    DirectionSteps.add(Instructions);
                }
                createRouteTabs(TotalDistance, TotalTime, DirectionSteps);

            }

            progDialog.dismiss();
            //show route tabs and slide up panel ~

            RouteTabLayout.setVisibility(View.VISIBLE);
            SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
        catch(Exception e)
        {
            Toast.makeText(this._context, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

    }
    public void createRouteTabs(String TD, String TT, List<String> Steps){
        //For Route Tabs
        final TabLayout RouteTabs = (TabLayout) this._activity.findViewById(R.id.route_tablayout);
        RouteTabs.removeAllTabs();
        RouteTabs.addTab(RouteTabs.newTab().setText(TT));
        RouteTabs.addTab(RouteTabs.newTab().setText("Route 2"));
        RouteTabs.addTab(RouteTabs.newTab().setText("Route 3"));
        RouteTabs.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) this._activity.findViewById(R.id.routepager);
        final PagerAdapter adapter = new PagerAdapter(_supportFragmentManager, RouteTabs.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(RouteTabs));

        RouteTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        String _Steps = "";
        for(int x=0; x < Steps.size(); x++){
            _Steps += (x+1)+". " + CleanDirectionStep(Steps.get(x))  + ". </br>";
        }

        RouteStepsText = (WebView) this._activity.findViewById(R.id.route_steps);
        //Check version for HTML render compatibility
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            RouteStepsText.loadData(_Steps, "text/html; charset=utf-8", "UTF-8");
        } else {
            RouteStepsText.loadData(_Steps, "text/html; charset=utf-8", "UTF-8");
        }


    }

    public String CleanDirectionStep(String str){
        if(str!=null){
            if(str.contains("onto"))
            {
                str = str.replace("onto","on to");
            }
            if(str.contains("<div style=\"font-size:0.9em\">"))
            {
                str = str.replace("<div style=\"font-size:0.9em\">"," ");
            }
            if(str.contains("</div>"))
            {
                str = str.replace("</div>","");
            }
        }

        return str;
    }



}
