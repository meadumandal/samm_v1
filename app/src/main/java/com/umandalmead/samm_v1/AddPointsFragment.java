package com.umandalmead.samm_v1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AddPointsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPointsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View myView;
    Button btnAddPoints;
    EditText editName;
    EditText editLocation;

    public AddPointsFragment() {
        // Required empty public constructor


    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPointsFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        btnAddPoints = (Button) myView.findViewById(R.id.btnAddPoint);
        editName = (EditText) myView.findViewById(R.id.terminalName);
        editLocation = (EditText) myView.findViewById(R.id.location);

        btnAddPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString();
                String location = editLocation.getText().toString();
                if (name.trim().length() == 0 || location.trim().length() == 0)
                {
                    Toast.makeText(getActivity(), "Please supply all fields", Toast.LENGTH_LONG).show();
                }
                else
                {
                    addPoint(name, location);
                }

            }
        });
        // Inflate the layout for this fragment
        myView= inflater.inflate(R.layout.fragment_add_points, container, false);
        return myView;

    }
    public void addPoint(String name, String location)
    {
        
    }


}
