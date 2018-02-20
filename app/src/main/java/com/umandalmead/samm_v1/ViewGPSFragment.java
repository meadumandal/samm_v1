package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.umandalmead.samm_v1.Adapters.listViewCustomAdapter;
import com.umandalmead.samm_v1.EntityObjects.GPS;

import java.util.ArrayList;
import android.app.DialogFragment;


import static android.R.attr.fragment;
import static com.facebook.FacebookSdk.getApplicationContext;


public class ViewGPSFragment extends android.support.v4.app.Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String TAG = "mead";
    private String mParam1;
    private String mParam2;
    View myView;
    ArrayList<GPS> dataModels;
    ProgressDialog progDialog;

    private static listViewCustomAdapter adapter;

    Activity _activity;

    public ViewGPSFragment( ) {


    }


    public static ViewGPSFragment newInstance(String param1, String param2) {
        ViewGPSFragment fragment = new ViewGPSFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        progDialog = new ProgressDialog(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_view_gps, container, false);
        try
        {
            NonScrollListView gpsListview = (NonScrollListView) myView.findViewById(R.id.gpslistview);
            progDialog.setTitle("Please wait");
            progDialog.setMessage("Please wait");
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.show();
            FragmentManager fm = getActivity().getFragmentManager();
            new asyncGetGPSFromTraccar(getActivity(), progDialog, gpsListview, fm).execute();
        }
        catch(Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }




        return myView;
    }


}
