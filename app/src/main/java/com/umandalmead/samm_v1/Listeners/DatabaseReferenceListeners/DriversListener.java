package com.umandalmead.samm_v1.Listeners.DatabaseReferenceListeners;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.umandalmead.samm_v1.Constants;
import com.umandalmead.samm_v1.EntityObjects.FirebaseEntities.Drivers;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.SessionManager;

/**
 * Created by MeadRoseAnn on 4/16/2018.
 */

public class DriversListener implements ValueEventListener {
    private SessionManager _sessionManager;
    private Context _context;
    private Constants _constants = new Constants();
    private DatabaseReference _terminalsDBRef;
    public String _dwelledTerminal = "";
    public String _leftTerminal = "";
    Helper _helper = new Helper();
    public DriversListener(Context context, DatabaseReference terminalsDBRef)
    {
        this._context = context;
        this._sessionManager = new SessionManager(this._context);
        this._terminalsDBRef = terminalsDBRef;

    }



    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        try
        {
            displayNumberOfWaitingPassengers(dataSnapshot);
        } catch(Exception ex)
        {
            Helper.logger(ex,true);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void displayNumberOfWaitingPassengers(DataSnapshot dataSnapshot)
    {
        try
        {
            Drivers driverNode = dataSnapshot.getValue(Drivers.class);

            String routeIDsOfTheEloop = driverNode.routeIDs;
            String[] tempRouteIDs = routeIDsOfTheEloop.split(",");
            Integer length = 0;
            for(String id:tempRouteIDs)
            {
                if (!id.isEmpty())
                    length++;
            }
            Integer[] routeIDs = new Integer[length];
            Integer ctr = 0;
            for(String routeID: tempRouteIDs)
            {
                if (!routeID.isEmpty())
                {
                    routeIDs[ctr] = Integer.parseInt(routeID);
                    ctr++;
                }
            }
            Integer minRouteID = _helper.getMin(routeIDs);
            if (!driverNode.EnteredStation.isEmpty()){
                final Terminal terminalObject = _helper.GetTerminalFromValueAndRouteID(driverNode.EnteredStation,minRouteID);
                if (!driverNode.IsDwelling.isEmpty() ? Boolean.valueOf(driverNode.IsDwelling) : false)
                {
                    _terminalsDBRef.child(terminalObject.getValue()).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            long passengerCount = dataSnapshot.getChildrenCount();
                            MenuActivity._TimeOfArrivalTextView.setText("YOU'RE AT: "
                                    + terminalObject.getDescription() + ".\n"
                                    + "WAITING PASSENGER: " +passengerCount);
                        }
                    });
                }
                else
                {
                    final Terminal nextTerminal = _helper.GetNextTerminalBasedOnCurrentTerminal(terminalObject);

                    _terminalsDBRef.child(nextTerminal.getValue()).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            return null;
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            long passengerCount = dataSnapshot.getChildrenCount();
                            MenuActivity._TimeOfArrivalTextView.setText("APPROACHING: " + nextTerminal.getDescription() + ".\n"
                                    + "WAITING PASSENGER: " + passengerCount);
                        }
                    });

                }
            }
            else
            {
                MenuActivity._TimeOfArrivalTextView.setText("Parked");
            }
        }
        catch(Exception ex)
        {
            _helper.logger(ex);
        }



//        if (dwellingLoopIds.contains(_sessionManager.getKeyDeviceid()) && Integer.parseInt(vehicleDestinationNode.tblRouteID) == minRouteID){
//            if (!this._dwelledTerminal.toLowerCase().equals(dataSnapshot.getKey()))
//            {
//                this._dwelledTerminal = dataSnapshot.getKey();
//                MenuActivity._TimeOfArrivalTextView.setBackgroundResource(R.drawable.pill_shaped_eloop_status);
//                MenuActivity._TimeOfArrivalTextView.setText("You've reached: " + this._dwelledTerminal + ". ");
//                _terminalsDBRef.child(this._dwelledTerminal).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        long passengerCount = dataSnapshot.getChildrenCount();
//                        MenuActivity._TimeOfArrivalTextView.setText(MenuActivity._TimeOfArrivalTextView.getText() + "There are " + passengerCount + " waiting passengers here.");
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//        }
//        else if(leftLoopIds.contains(_sessionManager.getKeyDeviceid()))
//        {
//
//            if(!this._leftTerminal.toLowerCase().equals(dataSnapshot.getKey()))
//            {
//                this._leftTerminal = dataSnapshot.getKey();
//
//                Terminal leftTerminal = _helper.GetTerminalUsingVehicleDestinationsNode(this._leftTerminal);
//
//                if (leftTerminal.tblRouteID == minRouteID)
//                {
//                    int orderOfArrivalOfTheStation = Integer.parseInt(vehicleDestinationNode.OrderOfArrival);
//
//                    for(Terminal terminal:MenuActivity._terminalList)
//                    {
//                        if(terminal.OrderOfArrival == orderOfArrivalOfTheStation + 1  && terminal.tblRouteID == minRouteID)
//                        {
//                            MenuActivity._TimeOfArrivalTextView.setText( "Approaching: " + terminal.Value);
//                            _terminalsDBRef.child(terminal.Value).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    long passengerCount = dataSnapshot.getChildrenCount();
//                                    MenuActivity._TimeOfArrivalTextView.setText(MenuActivity._TimeOfArrivalTextView.getText() +  ". There are + " + passengerCount + " waiting passengers there");
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//                    }
//                }
//
//            }


//        }
    }
}
