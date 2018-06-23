package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umandalmead.samm_v1.Adapters.listViewCustomAdapter;
import com.umandalmead.samm_v1.EntityObjects.GPS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import static com.umandalmead.samm_v1.Constants.LOG_TAG;


public class ViewGPSFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String TAG = "mead";
    private String mParam1;
    private String mParam2;
    public ImageButton FAB_SammIcon;
    private  TextView ViewTitle;
    public SwipeRefreshLayout swipeRefreshGPS;
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
            InitializeToolbar("GPS");
            final NonScrollListView gpsListview = (NonScrollListView) myView.findViewById(R.id.gpslistview);
            swipeRefreshGPS = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_refresh_gps);
            swipeRefreshGPS.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshGPS.setRefreshing(true);
                    FragmentManager fm = getActivity().getFragmentManager();
                    new asyncGetGPSFromTraccar(getActivity(), progDialog, gpsListview, fm,swipeRefreshGPS).execute();
                }
            });
            swipeRefreshGPS.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshGPS.setRefreshing(true);
                    FragmentManager fm = getActivity().getFragmentManager();
                    new asyncGetGPSFromTraccar(getActivity(), progDialog, gpsListview, fm, swipeRefreshGPS).execute();
                }
            });
            progDialog.setTitle("Please wait");
            progDialog.setMessage("Please wait");
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.show();


        }
        catch(Exception ex)
        {
            Helper.logger(ex);
        }




        return myView;
    }

    public void InitializeToolbar(String fragmentName){
        FAB_SammIcon = (ImageButton) myView.findViewById(R.id.SAMMLogoFAB);
        FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) ((MenuActivity) getActivity()).findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        ViewTitle = (TextView) myView.findViewById(R.id.samm_toolbar_title);
        ViewTitle.setText(fragmentName);
    }
}
