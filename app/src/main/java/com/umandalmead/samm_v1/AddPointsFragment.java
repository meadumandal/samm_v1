package com.umandalmead.samm_v1;


import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.umandalmead.samm_v1.Adapters.PointsViewCustomAdapter;
import com.umandalmead.samm_v1.Adapters.RouteViewCustomAdapter;
import com.umandalmead.samm_v1.Adapters.listViewCustomAdapter;
import com.umandalmead.samm_v1.EntityObjects.Terminal;
import com.umandalmead.samm_v1.POJO.Duration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPointsFragment extends Fragment {

    private View myView;
    private ImageButton FAB_SammIcon;
    private  TextView ViewTitle;
    private SwipeRefreshLayout swipeRefreshPoints;
    private NonScrollListView PointScrollListView;
    private PointsViewCustomAdapter customAdapter;
    private String _FragmentTitle;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = inflater.inflate(R.layout.activity_addpoints, container, false);
        PointScrollListView = (NonScrollListView) myView.findViewById(R.id.pointslistview);
        try
        {
            InitializeToolbar(MenuActivity._FragmentTitle + " Routes");
            SessionManager sessionManager = new SessionManager(myView.getContext());
            final NonScrollListView routeListview = (NonScrollListView) myView.findViewById(R.id.routelistview);

            swipeRefreshPoints = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_refresh_points);
            swipeRefreshPoints.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    InitializeView(routeListview);
                }
            });
            swipeRefreshPoints.post(new Runnable() {
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
        FAB_SammIcon.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fragment = getActivity().getSupportFragmentManager();
                fragment.beginTransaction().replace(R.id.content_frame, new AddRouteFragment()).commit();
            }
        });
        ViewTitle = (TextView) myView.findViewById(R.id.samm_toolbar_title);
        ViewTitle.setText(fragmentName);
    }
    public ArrayList<String> Populate(){
        ArrayList<String> result = new ArrayList<String>();
        for (Terminal entry: MenuActivity._terminalList) {
            result.add(entry.Value);
        }
        result.add("Add Point");
        return  result;
    }
    public void InitializeView(NonScrollListView NSRouteListView){
        swipeRefreshPoints.setRefreshing(true);
        FragmentManager fm = getActivity().getFragmentManager();
        customAdapter =new PointsViewCustomAdapter(Populate(), getActivity(),NSRouteListView,fm, swipeRefreshPoints);
        PointScrollListView.setAdapter(customAdapter);
        PointScrollListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

//                        MenuActivity.AddPointDialog dialog= new MenuActivity.AddPointDialog(AddPointsFragment.this,"ADD"); // AddPointDialog(getActivity(), "ADD");//MenuActivity.AddPointDialog(, "ADD");
//                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        dialog.show();
//                    Toast.makeText(getActivity(),"Clicked: " + position + " Text: " + ScrollListView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
//                    android.support.v4.app.FragmentManager fragment = getActivity().getSupportFragmentManager();
//                    MenuActivity._FragmentTitle = ScrollListView.getItemAtPosition(position).toString();
//                    fragment.beginTransaction().replace(R.id.content_frame, new AddPointsFragment()).commit();
                }
                catch(Exception ex)
                {
                    Helper.logger(ex);
                }

            }
        });

        swipeRefreshPoints.setRefreshing(false);
    }

}

