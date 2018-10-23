package com.umandalmead.samm_v1.Modules.AdminUsers;


import android.app.FragmentManager;
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
import com.umandalmead.samm_v1.LoaderDialog;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.NonScrollListView;
import com.umandalmead.samm_v1.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminUsersFragment extends Fragment {
    View _myView;
    public ImageButton FAB_SammIcon;
    private  TextView ViewTitle;
    public SwipeRefreshLayout swipeRefreshAdminUsers;
    LoaderDialog _LoaderDialog;



    public AdminUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        _LoaderDialog = new LoaderDialog(getActivity(), null,null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _myView =  inflater.inflate(R.layout.fragment_admin_users, container, false);
        try
        {
            InitializeToolbar("Admin Users");

            final NonScrollListView adminUsersListView = (NonScrollListView) _myView.findViewById(R.id.adminuserslistview);
            swipeRefreshAdminUsers = (SwipeRefreshLayout) _myView.findViewById(R.id.swipe_refresh_adminusers);

            swipeRefreshAdminUsers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshAdminUsers.setRefreshing(true);
                    FragmentManager fm = getActivity().getFragmentManager();
                    new mySQLGetAdminUsers(getActivity(), _LoaderDialog, adminUsersListView, fm,swipeRefreshAdminUsers).execute();
                }
            });
            swipeRefreshAdminUsers.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshAdminUsers.setRefreshing(true);
                    FragmentManager fm = getActivity().getFragmentManager();
                    new mySQLGetAdminUsers(getActivity(), _LoaderDialog, adminUsersListView, fm,swipeRefreshAdminUsers).execute();
                }
            });
            _LoaderDialog = new LoaderDialog(getActivity(),"Please wait", "Please wait");
            _LoaderDialog.show();


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
