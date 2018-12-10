package com.umandalmead.samm_v1.Modules.DriverUsers;


import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.media.tv.TvInputService;
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

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.LoaderDialog;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.NonScrollListView;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.SerializableRefreshLayoutComponents;
import com.umandalmead.samm_v1.SessionManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class DriverUsersFragment extends Fragment {
    View _myView;
    public ImageButton FAB_SammIcon;
    private  TextView ViewTitle;
    public SwipeRefreshLayout swipeRefreshDriverUsers;
    LoaderDialog _LoaderDialog;
    SessionManager _sessionManager = new SessionManager(MenuActivity._context);



    public DriverUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        _LoaderDialog = new LoaderDialog(getActivity(), null, null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _myView =  inflater.inflate(R.layout.fragment_driver_users, container, false);
        try
        {
            InitializeToolbar(MenuActivity._GlobalResource.getString(R.string.title_drivers_fragment));

            final NonScrollListView driversUserListView = (NonScrollListView) _myView.findViewById(R.id.driveruserslistview);
            swipeRefreshDriverUsers = (SwipeRefreshLayout) _myView.findViewById(R.id.swipe_refresh_driverusers);
            FloatingActionButton fab_addDriver = _myView.findViewById(R.id.floatingActionButton_addDriver);

            swipeRefreshDriverUsers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshDriverUsers.setRefreshing(true);
                    FragmentManager fm = getActivity().getFragmentManager();

                    new mySQLGetDriverUsers(getActivity(), _LoaderDialog, driversUserListView, fm, swipeRefreshDriverUsers).execute(_sessionManager.getUserID());


                }
            });
            swipeRefreshDriverUsers.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshDriverUsers.setRefreshing(true);
                    FragmentManager fm = getActivity().getFragmentManager();

                    new mySQLGetDriverUsers(getActivity(), _LoaderDialog, driversUserListView, fm, swipeRefreshDriverUsers).execute(_sessionManager.getUserID());
                }
            });
            _LoaderDialog = new LoaderDialog(getActivity(), MenuActivity._GlobalResource.getString(R.string.dialog_please_wait), MenuActivity._GlobalResource.getString(R.string.dialog_please_wait_with_ellipsis));
            _LoaderDialog.show();
            fab_addDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    FragmentManager fm = getActivity().getFragmentManager();
                    SerializableRefreshLayoutComponents swipeRefreshLayoutSerializable = new SerializableRefreshLayoutComponents(
                            swipeRefreshDriverUsers,fm , driversUserListView);
                    bundle.putSerializable("swipeRefreshLayoutSerializable", swipeRefreshLayoutSerializable);
                    bundle.putBoolean("isAdd", true);

                    EditDriverUserDialogFragment editDriverUserDialog = new EditDriverUserDialogFragment();
                    editDriverUserDialog.setArguments(bundle);
                    editDriverUserDialog.show(fm ,"EditDriverUserDialog");
                }
            });

        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
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
