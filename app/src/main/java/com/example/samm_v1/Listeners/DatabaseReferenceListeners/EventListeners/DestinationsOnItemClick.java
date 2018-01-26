package com.example.samm_v1.Listeners.DatabaseReferenceListeners.EventListeners;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.samm_v1.AnalyzeForBestRoutes;
import com.example.samm_v1.EntityObjects.Destination;
import com.example.samm_v1.MenuActivity;
import com.example.samm_v1.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MeadRoseAnn on 1/17/2018.
 */

public class DestinationsOnItemClick implements AdapterView.OnItemClickListener {
    public MenuActivity _activity;
    public Context _context;
    public SessionManager _sessionManager;


    public DestinationsOnItemClick(MenuActivity activity, Context context)
    {
        this._activity = activity;
        this._context = context;
        this._sessionManager = new SessionManager(_context);
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        //hide keyboard on search ~
        InputMethodManager mgr = (InputMethodManager) (this._activity).getSystemService(Context.INPUT_METHOD_SERVICE);

        Destination chosenDestination = (Destination) adapterView.getItemAtPosition(i);
        saveDestination(chosenDestination.Value);

        (this._activity)._candidateTerminals = new ArrayList<>();
        for(Destination destination: (this._activity)._listDestinations)
        {
            if (destination.Direction.equals(chosenDestination.Direction))
            {
                if(destination.OrderOfArrival < chosenDestination.OrderOfArrival)
                    (this._activity)._candidateTerminals.add(destination);
            }
        }
        new AnalyzeForBestRoutes(_context, _activity,
                (this._activity)._map, (this._activity)._currentLocation,
                (this._activity).getSupportFragmentManager(),
                (this._activity)._candidateTerminals, chosenDestination)
                .execute();
    }
    private void saveDestination(String destinationValue)
    {
        final HashMap<String, Object> currentDestination = new HashMap<>();
        currentDestination.put("currentDestination", destinationValue);

        (this._activity)._userDatabaseReference
                .child(_sessionManager.getUsername())
                .child("currentDestination")
                .addListenerForSingleValueEvent(
                        new SaveCurrentDestination(this._activity, this._context, currentDestination));
    }
}
