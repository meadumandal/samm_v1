package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.umandalmead.samm_v1.EntityObjects.Routes;
import com.umandalmead.samm_v1.EntityObjects.Terminal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eleazerarcilla on 21/10/2018.
 */

public class asyncProcessSelectedDestination extends AsyncTask<Void, Void, List<Terminal>>{
    Context _context;
    MenuActivity _activity;
    LoaderDialog _LoaderDialog;
    String progressMessage;
    public static String TAG="mead";
    private Constants _constants = new Constants();
    private Terminal _ChosenTerminal;
    private List<Terminal> _TerminalList;
    private  Place _place;
    private Boolean _IsPossibleTerminalsEmpty, _IsOnline = false;
    private HashMap<Routes, Integer> _numberOfStationsPerRoute = new HashMap<>();
    private ArrayList<Integer> _routeIDsThatServeTheChosenDropOffPoint = new ArrayList<>();
    Helper _helper;


    public asyncProcessSelectedDestination(Activity activity, Context context, List<Terminal> terminalList, Place place){
        this._activity = (MenuActivity) activity;
        this._context = context;
        this._TerminalList = terminalList;
        this._LoaderDialog = new LoaderDialog(_activity,"Searching","Please wait...");
        this._place = place;
        this._helper = new Helper(_activity,_context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _LoaderDialog.show();
        new asyncCheckInternetConnectivity(_activity);
    }

    @Override
    protected List<Terminal> doInBackground(Void... voids) {
        try {
            List<Terminal> possiblePickUpPoints = new ArrayList<>();

            double prevDistance = 0.0;
            int ctr = 0;
            if(_TerminalList.size()>0) {
                for (Terminal dest : _TerminalList) {
                    double tempDistance;
                    LatLng destLatLng = new LatLng(dest.Lat, dest.Lng);
                    LatLng searchLatLng = _place.getLatLng();
                    tempDistance = _helper.getDistanceFromLatLonInKm(destLatLng, searchLatLng);
                    if (ctr == 0) {
                        prevDistance = tempDistance;
                        _ChosenTerminal = dest;
                    } else {
                        if (tempDistance <= prevDistance) {
                            prevDistance = tempDistance;
                            _ChosenTerminal = dest;
                        }
                    }
                    ctr++;
                }
            }
                String link = Constants.WEB_API_URL
                    + Constants.DESTINATIONS_API_FOLDER
                    + Constants.DESTINATIONS_API_GET_POSSIBLE_STATIONS
                    + "destinationID=" + _ChosenTerminal.ID.toString();
            URL url = new URL(link);
            URLConnection urlConn = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String jsonResponse = bufferedReader.readLine();
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for(int index = 0; index < jsonArray.length(); index++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                Integer ID = jsonObject.getInt("ID");
                Integer tblRouteID = jsonObject.getInt("tblRouteID");
                String Value = jsonObject.getString("Value");
                String Description = jsonObject.getString("Description");
                Integer OrderOfArrival = jsonObject.getInt("OrderOfArrival");
                String Direction = jsonObject.getString("Direction");
                Double Lat = jsonObject.getDouble("Lat");
                Double Lng = jsonObject.getDouble("Lng");
                Integer isMainTerminal = jsonObject.getInt("isMainTerminal");
                Integer IsActive = jsonObject.getInt("IsActive");
                possiblePickUpPoints.add(new Terminal(ID, tblRouteID,Value, Description, OrderOfArrival,Direction, Lat, Lng,"",null,0, "", isMainTerminal.toString(),""));
            }

//            for(Routes route: _activity._routeList)
//            {
//                Integer iStationCount = 0;
//                for(Terminal terminal:_activity._terminalList)
//                {
//                    if (terminal.tblRouteID == route.getID())
//                    {
//                        iStationCount++;
//                        if (terminal.getValue().equals(_ChosenTerminal.getValue()))
//                        {
//                            if (!_routeIDsThatServeTheChosenDropOffPoint.contains(route.getID()))
//                                _routeIDsThatServeTheChosenDropOffPoint.add(route.getID());
//                        }
//                    }
//
//                }
//                _numberOfStationsPerRoute.put(route, iStationCount);
//            }
//            HashMap<Terminal, Integer> halfwayIndexOfEachRoute = new HashMap<>();
//            List<Terminal> possiblePickUpPoints = new ArrayList<>();
//
//            for(Terminal terminal: _activity._terminalList)
//            {
//                if (_routeIDsThatServeTheChosenDropOffPoint.contains(terminal.getTblRouteID()))
//                {
//                    int stationGap = Math.abs(terminal.getOrderOfArrival() - _ChosenTerminal.getOrderOfArrival());
//                    for(Map.Entry<Routes, Integer> numberOfStation: _numberOfStationsPerRoute.entrySet())
//                    {
//                        if (numberOfStation.getKey().getID() == terminal.tblRouteID)
//                        {
//                            if (stationGap<=numberOfStation.getValue()/2)
//                            {
//                                possiblePickUpPoints.add(terminal);
//                                break;
//                            }
//
//                        }
//                    }
//                }
//            }
            return possiblePickUpPoints;
        }catch (Exception ex){
            Helper.logger(ex);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Terminal> possibleTerminals) {
             _LoaderDialog.dismiss();
            if (possibleTerminals.size()>0) {
                new AnalyzeForBestRoutes(_context, _activity,
                        MenuActivity._googleMap, MenuActivity._userCurrentLoc,
                        possibleTerminals, _ChosenTerminal)
                        .execute();
            } else {
                Toast.makeText(_context, "Unable to process, data returned was empty!", Toast.LENGTH_SHORT).show();
            }

    }
//    public List<Terminal> FindPossiblePickUpPoints(Terminal dropOffPoint) {
//
//        List<Terminal> possiblePickUpPoints = new ArrayList<>();
//        if(_helper.isOnline(_activity,_activity))
//        {
//            // ArrayList<Terminal> dropOffPointList = GetAllDestinationRegardlessOfTheirTableRouteIds(dropOffPoint);
//            Terminal chosenTerminal =dropOffPoint;
//            //saveDestination(chosenTerminal.Value);
//
//
//            for(Terminal terminal : _activity._terminalList)
//            {
//                if (terminal.Direction.equals(chosenTerminal.Direction) && terminal.tblRouteID == chosenTerminal.tblRouteID)
//                {
//                    if(terminal.OrderOfArrival < chosenTerminal.OrderOfArrival)
//                        possiblePickUpPoints.add(terminal);
//                }
//            }
//            return possiblePickUpPoints;
//        }
//        else
//        {
//            return possiblePickUpPoints;
//        }
//
//    }
}
