package com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.umandalmead.samm_v1.Constants;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.EntityObjects.FirebaseEntities.VehicleDestination;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.SessionManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;

/**
 * Created by MeadRoseAnn on 4/16/2018.
 */

public class Vehicle_DestinationsListener implements ChildEventListener {
    private SessionManager _sessionManager;
    private Context _context;
    private Constants _constants = new Constants();
    private DatabaseReference _terminalsDBRef;
    public String _dwelledTerminal = "";
    public String _leftTerminal = "";
    public Vehicle_DestinationsListener(Context context, DatabaseReference terminalsDBRef)
    {
        this._context = context;
        this._sessionManager = new SessionManager(this._context);
        this._terminalsDBRef = terminalsDBRef;
    }
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        try
        {
            String message = "";
            VehicleDestination vehicleDestinationNode = dataSnapshot.getValue(VehicleDestination.class);
            List<String> dwellingLoopIds = Arrays.asList(vehicleDestinationNode.Dwell.split(","));
            List<String> leftLoopIds = Arrays.asList(vehicleDestinationNode.LoopIds.split(","));
            if (dwellingLoopIds.contains(_sessionManager.getKeyDeviceid())){
                if (!this._dwelledTerminal.toLowerCase().equals(dataSnapshot.getKey()))
                {
                    this._dwelledTerminal = dataSnapshot.getKey();
                    MenuActivity._TimeOfArrivalTextView.setText("You are at " + this._dwelledTerminal + ". ");
                    _terminalsDBRef.child(this._dwelledTerminal).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long passengerCount = dataSnapshot.getChildrenCount();
                            MenuActivity._TimeOfArrivalTextView.setText(MenuActivity._TimeOfArrivalTextView.getText() + "There are " + passengerCount + " waiting passengers here.");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
            if(leftLoopIds.contains(_sessionManager.getKeyDeviceid()))
            {

                if(!this._leftTerminal.toLowerCase().equals(dataSnapshot.getKey()))
                {
                    this._leftTerminal = dataSnapshot.getKey();
                    int currentOrderOfArrival = vehicleDestinationNode.OrderOfArrival;
                    for(Terminal terminal:MenuActivity._terminalList)
                    {
                        if(terminal.OrderOfArrival == currentOrderOfArrival + 1)
                        {
                            MenuActivity._TimeOfArrivalTextView.setText( "Now you are approaching " + terminal.Value);
                            _terminalsDBRef.child(terminal.Value).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long passengerCount = dataSnapshot.getChildrenCount();
                                    MenuActivity._TimeOfArrivalTextView.setText(MenuActivity._TimeOfArrivalTextView.getText() +  ". There are + " + passengerCount + " waiting passengers there");
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }


            }
        } catch(Exception ex)
        {
            Helper.logger(ex);
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
}
