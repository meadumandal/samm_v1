package com.evolve.evx;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.evolve.evx.EntityObjects.Routes;
import com.evolve.evx.EntityObjects.Terminal;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private Place _place;
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
                Double distanceFromPreviousStation = jsonObject.getDouble("distanceFromPreviousStation");
                Integer isMainTerminal = jsonObject.getInt("isMainTerminal");
                String routeName = jsonObject.getString("routeName");
                Integer LineID = jsonObject.getInt("LineID");
                String LineName = jsonObject.getString("LineName");
                Integer IsActive = jsonObject.getInt("IsActive");
                possiblePickUpPoints.add(new Terminal(ID, tblRouteID,Value, Description, OrderOfArrival,Direction,  Lat, Lng,"",null,0, LineName, isMainTerminal.toString(),routeName,LineID, distanceFromPreviousStation));
            }

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
}
