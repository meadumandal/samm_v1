package com.umandalmead.samm_v1.Modules.DriverUsers;


import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.NonScrollListView;
import com.umandalmead.samm_v1.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class DriverUsersFragment extends Fragment {
    View _myView;
    public ImageButton FAB_SammIcon;
    private  TextView ViewTitle;
    public SwipeRefreshLayout swipeRefreshDriverUsers;
    ProgressDialog progDialog;



    public DriverUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        progDialog = new ProgressDialog(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _myView =  inflater.inflate(R.layout.fragment_admin_users, container, false);
        try
        {
            InitializeToolbar("Drivers");

            final NonScrollListView driversUserListView = (NonScrollListView) _myView.findViewById(R.id.adminuserslistview);
            swipeRefreshDriverUsers = (SwipeRefreshLayout) _myView.findViewById(R.id.swipe_refresh_adminusers);

            swipeRefreshDriverUsers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshDriverUsers.setRefreshing(true);
                    FragmentManager fm = getActivity().getFragmentManager();

                    new mySQLGetDriverUsers(getActivity(), progDialog, driversUserListView, fm, swipeRefreshDriverUsers).execute();


                }
            });
            swipeRefreshDriverUsers.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshDriverUsers.setRefreshing(true);
                    FragmentManager fm = getActivity().getFragmentManager();
                    new mySQLGetDriverUsers(getActivity(), progDialog, driversUserListView, fm, swipeRefreshDriverUsers).execute();
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




        return _myView;
    }
    public void InitializeToolbar(String fragmentName){
        FAB_SammIcon = (ImageButton) _myView.findViewById(R.id.SAMMLogoFAB);
        FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) ((MenuActivity) getActivity()).findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        ViewTitle = (TextView) _myView.findViewById(R.id.samm_toolbar_title);
        ViewTitle.setText(fragmentName);
    }

}
