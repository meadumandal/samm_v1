package com.umandalmead.samm_v1;


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

import com.umandalmead.samm_v1.Adapters.RouteViewCustomAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddRouteFragment extends Fragment  {

    private View myView;
    private ImageButton FAB_SammIcon;
    private  TextView ViewTitle;
    private SwipeRefreshLayout swipeRefreshRoute;
    public NonScrollListView ScrollListView;
    private RouteViewCustomAdapter customAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        myView = inflater.inflate(R.layout.activity_addroute, container, false);
        ScrollListView = (NonScrollListView) myView.findViewById(R.id.routelistview);
        try
        {
            InitializeToolbar("Manage Routes");
            SessionManager sessionManager = new SessionManager(myView.getContext());
            final NonScrollListView routeListview = (NonScrollListView) myView.findViewById(R.id.routelistview);
            swipeRefreshRoute = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_refresh_routes);
            swipeRefreshRoute.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                   InitializeView(routeListview);
                }
            });
            swipeRefreshRoute.post(new Runnable() {
                @Override
                public void run() {
                    InitializeView(routeListview);
                }
            });

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
//    public ArrayList<Routes> Populate(){
//        ArrayList<String> result = new ArrayList<String>();
//        result.add("Northgate");
//        result.add("Northgate-Expanded");
//        result.add("Palms");
//        result.add("RITM");
//        result.add("Add Routes");
//        return  result;
//    }
    public void InitializeView(NonScrollListView NSRouteListView){
        swipeRefreshRoute.setRefreshing(true);
        FragmentManager fm = getActivity().getFragmentManager();
        new mySQLRoutesDataProvider(getContext()).execute();
        customAdapter =new RouteViewCustomAdapter(MenuActivity._routeList, getActivity(),NSRouteListView,fm, swipeRefreshRoute);
        ScrollListView.setAdapter(customAdapter);

//        ScrollListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                try {
//                    Toast.makeText(getActivity(),"Clicked: " + position + " Text: " + ScrollListView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
//                    android.support.v4.app.FragmentManager fragment = getActivity().getSupportFragmentManager();
//                    MenuActivity._FragmentTitle = ScrollListView.getItemAtPosition(position).toString();
//                    fragment.beginTransaction().replace(R.id.content_frame, new AddPointsFragment()).commit();
//                }
//                catch(Exception ex)
//                {
//                    Helper.logger(ex);
//                }
//
//            }
//        });

        swipeRefreshRoute.setRefreshing(false);
    }
}

