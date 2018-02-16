package com.umandalmead.samm_v1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.umandalmead.samm_v1.EntityObjects.Destination;



public class AddPointsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View myView;
    Button btnAddPoints;
    EditText editName;
    EditText editLat;
    EditText editLng;
    Spinner spinnerPrePosition, spinnerTerminalReference;
    public static String TAG = "mead";

    public AddPointsFragment() {


    }

    public static AddPointsFragment newInstance(String param1, String param2) {
        AddPointsFragment fragment = new AddPointsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            myView = inflater.inflate(R.layout.fragment_add_points, container, false);
            btnAddPoints = (Button) myView.findViewById(R.id.btnAddPoint);
            editName = (EditText) myView.findViewById(R.id.terminalName);
            editLat = (EditText) myView.findViewById(R.id.lat);
            editLng = (EditText) myView.findViewById(R.id.lng);

            spinnerPrePosition = (Spinner) myView.findViewById(R.id.spinner_preposition);
            spinnerTerminalReference = (Spinner) myView.findViewById(R.id.spinner_terminalReference);
            ArrayAdapter<Destination> terminalReferencesAdapter = new ArrayAdapter<Destination>(getActivity(), R.layout.spinner_item, MenuActivity._listDestinations);
            spinnerTerminalReference.setAdapter(terminalReferencesAdapter);


            btnAddPoints.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = editName.getText().toString();
                    String lat = editLat.getText().toString();
                    String lng = editLng.getText().toString();
                    String preposition = spinnerPrePosition.getSelectedItem().toString();
                    Destination terminalReference = (Destination) spinnerTerminalReference.getSelectedItem();
//                    if (name.trim().length() == 0 || location.trim().length() == 0)
//                    {
//                        Toast.makeText(getActivity(), "Please supply all fields", Toast.LENGTH_LONG).show();
//                    }
//                    else
//                    {
//                        addPoint(name, location);
//                    }

                }
            });
            // Inflate the layout for this fragment
            myView = inflater.inflate(R.layout.fragment_add_points, container, false);
        }
        catch(Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }
        return myView;

    }
    public void addPoint(String name, String location)
    {
        
    }


}
