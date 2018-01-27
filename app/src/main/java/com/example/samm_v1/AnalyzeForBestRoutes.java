package com.example.samm_v1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
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

import static com.example.samm_v1.MenuActivity.RoutePane;
import static com.example.samm_v1.MenuActivity.RouteStepsText;
import static com.example.samm_v1.MenuActivity.RouteTabLayout;
import static com.example.samm_v1.MenuActivity.SlideUpPanelContainer;
import static com.example.samm_v1.MenuActivity.StepsScroller;
import static com.example.samm_v1.MenuActivity.TimeOfArrivalTextView;


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
    List<Destination> _topTerminals;
    final String TAG = "mead";
    public  static Polyline _line;
    List<String> _AllSteps = new ArrayList<String>();
    Destination _SelectedDestination;
    List<String> _AllPoints = new ArrayList<String>();
    List<Polyline> _AllPoly = new ArrayList<Polyline>();
    List<String> _AllTotalTime = new ArrayList<String>();
    List<List<String>> _AllDirectionsSteps = new ArrayList<List<String>>();
    List<List<String>> _AllTerminalPoints = new ArrayList<List<String>>();
    List<Polyline> polyLines = new ArrayList<>();
    /**
     *This is the generic format in accessing data from mySQL
     * @param context
     * @param activity
     */
    public AnalyzeForBestRoutes(Context context, Activity activity, GoogleMap map, LatLng currentLocation, FragmentManager supportFragmentManager, List<Destination> possibleTerminals, Destination choseDestination)
    {
        this._context = context;
        this._activity = activity;
        this._map = map;
        this._supportFragmentManager = supportFragmentManager;
        progDialog = new ProgressDialog(this._activity);
        progDialog.setMessage(progressMessage);
        this._currentLocation = currentLocation;
        this._possibleTerminals = possibleTerminals;
        this._SelectedDestination = choseDestination;

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
            _topTerminals = topTerminals;
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
    public static void clearLines()
    {
        if(_line!=null)
            _line.remove();
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
        polyLines.add(_line);
    }




    @Override
    protected void onPostExecute(List<Destination> topTerminals)
    {
        //"topTerminals" contains the top 3 nearest terminal from user's CURRENT location

        try
        {
            int ctr=0;
            if (_line!=null){
                _line.setVisible(false);
            }
            for(Destination terminal: topTerminals)
            {
                String TotalTime ="";
                List<String> DirectionSteps = new ArrayList<String>();
                for (int i = 0; i < terminal.directionsFromCurrentLocation.getRoutes().size(); i++) {
                    TotalTime = terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getDuration().getText();
                    String encodedString = terminal.directionsFromCurrentLocation.getRoutes().get(0).getOverviewPolyline().getPoints();
                    //drawLines(encodedString);
                    _AllPoints.add(encodedString);
                    _AllTotalTime.add(TotalTime);
                }
                for(int x = 0; x<terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getInstructions().size(); x++){
                    String Instructions =  terminal.directionsFromCurrentLocation.getRoutes().get(0).getLegs().get(0).getInstructions().get(x).getSteps().toString();
                    DirectionSteps.add(Instructions);
                }
                _AllTerminalPoints.add(_AllPoints);
                _AllDirectionsSteps.add(DirectionSteps);
                ctr++;
            }
            createRouteTabs(_AllTotalTime,_AllDirectionsSteps, _topTerminals, _AllTerminalPoints, ctr);
            progDialog.dismiss();
//            //show route tabs and slide up panel ~
            RouteTabLayout.setVisibility(View.VISIBLE);
            RoutePane.setVisibility(View.VISIBLE);
            SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            TimeOfArrivalTextView.setVisibility(View.VISIBLE);

            RouteStepsText = (WebView) this._activity.findViewById(R.id.route_steps);
            final ViewPager viewPager = (ViewPager) this._activity.findViewById(R.id.routepager);
            viewPager.setCurrentItem(0);
            RouteStepsText.loadDataWithBaseURL("file:///android_res/",SelectedTabInstructions(_AllDirectionsSteps.get(0), _AllTotalTime.get(0), _topTerminals.get(0)), "text/html; charset=utf-8", "UTF-8", null);
            SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            MenuActivity._ChosenDestination = _topTerminals.get(0);
            StepsScroller.scrollTo(0,0);
            clearLines();
            drawLines(_AllTerminalPoints.get(0).get(0));


        }
        catch(Exception e)
        {
            progDialog.dismiss();
            Toast.makeText(this._context, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

    }
    public void createRouteTabs(final List<String> TotalTimeList, final List<List<String>> DirectionStepsList, final List<Destination> AllPossibleTerminals,final List<List<String>> TerminalPointsList, final int ctr){
        //For Route Tabs
        try{
            if(AllPossibleTerminals.size() == 0 || AllPossibleTerminals == null)
                throw new Exception("Unable to find route for this destination.");
            final TabLayout RouteTabs = (TabLayout) this._activity.findViewById(R.id.route_tablayout);
            RouteTabs.removeAllTabs();
            for (Destination entry:AllPossibleTerminals) {
                RouteTabs.addTab(RouteTabs.newTab().setText(entry.Description));
            }
            RouteTabs.setTabGravity(TabLayout.GRAVITY_FILL);

            final ViewPager viewPager = (ViewPager) this._activity.findViewById(R.id.routepager);
            final PagerAdapter adapter = new PagerAdapter(_supportFragmentManager, RouteTabs.getTabCount());
            viewPager.setAdapter(adapter);
            viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(RouteTabs));

            RouteTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                    RouteStepsText.loadDataWithBaseURL("file:///android_res/",SelectedTabInstructions(DirectionStepsList.get(tab.getPosition()), TotalTimeList.get(tab.getPosition()), AllPossibleTerminals.get(tab.getPosition())), "text/html; charset=utf-8", "UTF-8", null);
                    SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    MenuActivity._ChosenDestination = AllPossibleTerminals.get(tab.getPosition());
                    StepsScroller.scrollTo(0,0);
                    clearLines();
                    drawLines(TerminalPointsList.get(tab.getPosition()).get(tab.getPosition()));
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            TimeOfArrivalTextView.setVisibility(View.VISIBLE);

            RouteStepsText = (WebView) this._activity.findViewById(R.id.route_steps);
            viewPager.setCurrentItem(0);
            RouteStepsText.loadDataWithBaseURL("file:///android_res/",SelectedTabInstructions(DirectionStepsList.get(0), TotalTimeList.get(0), AllPossibleTerminals.get(0)), "text/html; charset=utf-8", "UTF-8", null);
            SlideUpPanelContainer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            MenuActivity._ChosenDestination = AllPossibleTerminals.get(0);
            StepsScroller.scrollTo(0,0);
            clearLines();
            drawLines(TerminalPointsList.get(0).get(0));
        }catch(Exception e){
            progDialog.dismiss();
            Toast.makeText(this._context, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

    }

    public String SelectedTabInstructions(List<String> StepsList, String TT, Destination Terminal){
        String Step =
                "<hr/><h3 style='padding-left:5%;'>Suggested Actions</h3><body style='margin: 0; padding: 0'><table style='padding-left:5%; padding-right:2%;'><tr><td width='20%'><img style='height:60%; border-radius:50%;' src= 'drawable/ic_walking.png'></td>" +
                "<td style='padding-left:7%;'><medium style='background:#2196F3; color:white;border-radius:10%; padding: 7px;'>WALK</medium></td></tr>"+
                "<tr><td width='20%' style='text-align:center'><small>"+CleanTotalTime(TT)+"</small></td><td></td></tr>";

        if(StepsList!=null){
            for(int x=0; x < StepsList.size(); x++){
                Step +="<tr><td></td><td>"+ (x+1)+". " + CleanDirectionStep(StepsList.get(x))  + ".</td><tr>";
                if((x+1) == StepsList.size()){
                    Step += "<tr><td></td><td>"+(x+2)+". " +GenerateFinalStep(_SelectedDestination, Terminal);
                }
            }
        }
        return Step + "</table></body>";
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
    public String CleanTotalTime(String str){
        if(str!=null){
            if(str.contains("hours"))
            {
                str = str.replace("hours","h");
            }
            if(str.contains("mins"))
            {
                str = str.replace("mins","min");
            }
        }

        return str;
    }
    public String GenerateFinalStep(Destination end, Destination start){
        int dist = end.OrderOfArrival - start.OrderOfArrival;
        return "Alight after <b>" + dist + " stop"+(dist > 1 ? "s":"")+"</b>.</td><tr>";
    }



}
