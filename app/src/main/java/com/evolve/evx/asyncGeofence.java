package com.evolve.evx;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.evolve.evx.EntityObjects.Terminal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.evolve.evx.Constants.LOG_TAG;
import static com.evolve.evx.MenuActivity._terminalsDBRef;

/**
 * Created by MeadRoseAnn on 12/11/2018.
 */

public class asyncGeofence extends AsyncTask<LatLng, Void, Terminal> {
    public Helper _helper = new Helper();
    SessionManager _sessionManager;
    Context _context;
    LatLng _userLoc;

    public asyncGeofence(Context context)
    {
        this._context = context;
        _sessionManager = new SessionManager(_context);
    }
    @Override
    protected void onPreExecute()
    {

    }
    @Override
    protected Terminal doInBackground(LatLng... latLngs) {
        List<Terminal> enteredStations = new ArrayList<>();
        Terminal returnTerminal = null;
        try
        {
            LatLng userLoc = latLngs[0];
            _userLoc = userLoc;
            for(Map.Entry<String, Terminal> terminal: MenuActivity._distinctTerminalList.entrySet())
            {
                LatLng terminalLoc = new LatLng(terminal.getValue().getLat(), terminal.getValue().getLng());
                Double distanceFromCurrentLocation = _helper.getDistanceFromLatLonInKm(userLoc, terminalLoc)*1000;
                if (distanceFromCurrentLocation <= Constants.GEOFENCE_RADIUS_FOR_USER)
                {
                    Terminal t = terminal.getValue();
                    t.distanceFromCurrentLocation = distanceFromCurrentLocation;
                    enteredStations.add(t);
                }
            }
            if (enteredStations.size()>0)
            {
                Collections.sort(enteredStations, Terminal.DestinationComparators.ORDER_BY_DISTANCEFROMUSER_ASC);
                return enteredStations.get(0);
            }

        }
        catch(Exception ex)
        {
            _helper.logger(ex);
        }
        return returnTerminal;
    }
    @Override
    protected void onPostExecute(Terminal terminal)
    {

        if (terminal!=null)
        {

            if (!_sessionManager.getKeyEnteredStation().isEmpty()
                    && !_sessionManager.getKeyEnteredStation().equalsIgnoreCase(terminal.getValue())) {

                passengerMovement(_sessionManager.getKeyEnteredStation(), "exit");
                passengerMovement(terminal.getValue(), "entered");
//                Toast.makeText(_context, "You entered " + terminal.getDescription(), Toast.LENGTH_LONG).show();
                _sessionManager.setKeyEnteredStation(terminal.getValue());
            }
            else if (_sessionManager.getKeyEnteredStation().isEmpty())
            {
                passengerMovement(terminal.getValue(), "entered");
//                Toast.makeText(_context, "You entered " + terminal.getDescription(), Toast.LENGTH_LONG).show();
                _sessionManager.setKeyEnteredStation(terminal.getValue());
            }
            else {
                passengerMovement(terminal.getValue(), "entered");
            }


        }
        else if (terminal == null && !_sessionManager.getKeyEnteredStation().isEmpty())
        {
            passengerMovement(_sessionManager.getKeyEnteredStation(), "exit");
//            Toast.makeText(_context, "You exited " + _sessionManager.getKeyEnteredStation(), Toast.LENGTH_LONG).show();
            _sessionManager.setKeyEnteredStation("");
        }
        initializeOnlinePresence();
    }
    private void passengerMovement(final String destinationValue, final String movement)
    {
        Log.i(LOG_TAG, "Saving passenger movement to firebase...");
        final HashMap<String, Object> count = new HashMap<>();
        final HashMap<String, Object> hashmapCount = new HashMap<>();
        final String uid = _sessionManager.getUsername();

//        _terminalsDBRef.child(destinationValue).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
////                if(dataSnapshot == null || dataSnapshot.getValue() == null)
////                {
        if(movement.toLowerCase().equals("entered"))
        {
            _terminalsDBRef.child(destinationValue).child(uid).setValue(true);
            //updatePassengerCountForReport(_sessionManager.getUsername(), destinationValue);
        }

//                }
        else if(movement.toLowerCase().equals("exit")){
            _terminalsDBRef.child(destinationValue).child(uid).removeValue();
        }
        else if (movement.toLowerCase().equals("entered"))
        {
            //updatePassengerCountForReport(_sessionManager.getUsername(), destinationValue);
        }
//                _passengerCount.remove(destinationValue);
//                _passengerCount.put(destinationValue, dataSnapshot.getChildrenCount());

//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }

    private void initializeOnlinePresence() {
        // any time that connectionsRef's value is null, device is offline
        Log.i(LOG_TAG, "Initializing online presence for geofence...");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();


        final DatabaseReference terminalsDBRef = database.getReference("terminals/" + _sessionManager.getKeyEnteredStation() + "/" + _sessionManager.getUsername());
        final DatabaseReference connectedRef = database.getReference(".info/connected");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    terminalsDBRef.onDisconnect().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });
    }
}
