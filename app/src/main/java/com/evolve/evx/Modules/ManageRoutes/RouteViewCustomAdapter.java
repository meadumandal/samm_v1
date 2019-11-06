package com.evolve.evx.Modules.ManageRoutes;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.evolve.evx.Constants;
import com.evolve.evx.EntityObjects.Routes;
import com.evolve.evx.EntityObjects.Terminal;
import com.evolve.evx.Enums;
import com.evolve.evx.MenuActivity;
import com.evolve.evx.NonScrollListView;
import com.evolve.evx.R;
import com.evolve.evx.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eleazerarcilla on 20/06/2018.
 */

public class RouteViewCustomAdapter extends ArrayAdapter<Routes> implements View.OnClickListener{
    private NonScrollListView _RouteListView;
    private Context _context;
    private ArrayList<Routes> _TestData = new ArrayList<Routes>();
    private SwipeRefreshLayout _SwipeRefreshRoute;
    private FragmentManager _FragmentManager;
    private int lastPosition = -1;
    ManageRoutesFragment _manageRouesFragment;
    SessionManager _sessionManager;



    public RouteViewCustomAdapter(ArrayList<Routes> data, Context cont,NonScrollListView listView, FragmentManager fm,
                                  SwipeRefreshLayout swipeRefreshRoute, ManageRoutesFragment manageRoutesFragment){
        super(cont, R.layout.listview_viewroutes, data);
        this._context = cont;
        this._TestData = data;
        this._SwipeRefreshRoute = swipeRefreshRoute;
        this._RouteListView = listView;
        this._FragmentManager = fm;
        this._manageRouesFragment = manageRoutesFragment;
        this._sessionManager = new SessionManager(_context);
    }

    private static class ViewHolder {
        TextView textRouteName;
        LinearLayout layoutRouteItem;
        ImageButton dragger;
        Button viewRoute;
        ImageButton imgbtnShowMoreActions;
    }



    @Override
    public void onClick(View view) {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;
        try{
            ViewHolder viewHolder = new ViewHolder();
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_viewroutes, parent, false);
                viewHolder.textRouteName = (TextView) convertView.findViewById(R.id.routeName);
                viewHolder.layoutRouteItem = (LinearLayout) convertView.findViewById(R.id.routeitemLinearLayout);
//                viewHolder.dragger = (ImageButton) convertView.findViewById(R.id.routeDragger);
                viewHolder.viewRoute = (Button) convertView.findViewById(R.id.btnViewRoute);
                viewHolder.imgbtnShowMoreActions =(ImageButton) convertView.findViewById(R.id.imgbtnShowActions);
                result=convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result=convertView;
            }
            Animation animation = AnimationUtils.loadAnimation(_context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            final Routes route = _TestData.get(position);
            final PopupMenu popup = new PopupMenu(_context, convertView, Gravity.RIGHT);
            popup.getMenuInflater().inflate(R.menu.popup_route_actions, popup.getMenu());
            if (_sessionManager.getIsAdmin())
            {
                popup.getMenu().findItem(R.id.itemEdit).setVisible(true);
                popup.getMenu().findItem(R.id.itemDelete).setVisible(true);
            }
            else
            {
                popup.getMenu().findItem(R.id.itemEdit).setVisible(false);
                popup.getMenu().findItem(R.id.itemDelete).setVisible(false);
            }


            viewHolder.textRouteName.setText(route.getRouteName());
//            if(route.getRouteName().toLowerCase().contains("add route")) {
//                viewHolder.layoutRouteItem.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorWhite));
//                    convertView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (((ViewHolder) view.getTag()).textRouteName.getText().toString().toLowerCase().contains("add route"))
//                                _manageRouesFragment.ProcessSelectedRoute(Enums.ActionType.ADD,null, null);
//                        }
//                    });
//
//            }
//            else {
//                viewHolder.layoutRouteItem.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorSprayBlue));
                viewHolder.imgbtnShowMoreActions.setVisibility(View.VISIBLE);
//                viewHolder.viewRoute.setVisibility(View.VISIBLE);
//                viewHolder.dragger.setVisibility(View.GONE);
//            }

            viewHolder.imgbtnShowMoreActions.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    popup.show();
                }
            });
            MenuActivity.buttonEffect(viewHolder.viewRoute);
            viewHolder.viewRoute.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    MenuActivity._FragmentTitle = route.getRouteName();
                    try {
//                        PopulatePoints(route.getID());
                        Bundle bundle = new Bundle();
                        bundle.putInt("routeID", route.getID());
                        MenuActivity._manageStationsFragment.setArguments(bundle);
                        _FragmentManager.beginTransaction().replace(R.id.content_frame, MenuActivity._manageStationsFragment)
                                .addToBackStack(Constants.FRAGMENTNAME_MANAGESTATIONS)
                                .commit();


                    }catch(Exception ex){
                        Toast.makeText(_context,ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    try {
                        String clickedMenu = item.getTitle().toString();
                        Enums.ActionType action = Enums.ActionType.EDIT;
                        if (clickedMenu.equalsIgnoreCase("edit") || clickedMenu.equalsIgnoreCase("delete")) {
                            if (clickedMenu.equalsIgnoreCase("edit"))
                                action = Enums.ActionType.EDIT;
                            else
                                action = Enums.ActionType.DELETE;

                            _manageRouesFragment.ProcessSelectedRoute(action, route.getID(), route.getRouteName(), route.getTblLineID());

                        } else { //View Routes
                            MenuActivity._FragmentTitle = route.getRouteName();
                            Bundle bundle = new Bundle();
                            bundle.putInt("routeID", route.getID());
                            MenuActivity._manageStationsFragment.setArguments(bundle);
                            _FragmentManager.beginTransaction().replace(R.id.content_frame, MenuActivity._manageStationsFragment)
                                    .addToBackStack(Constants.FRAGMENTNAME_MANAGESTATIONS)
                                    .commit();
                        }

                    } catch (Exception ex) {
                        Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });


        }catch (Exception ex){
            throw ex;
        }
        return convertView;
    }
    public ArrayList<String> PopulatePoints(Integer routeID){
        ArrayList<String> result = new ArrayList<String>();
        List<Terminal> terminalListBasedOnRouteID = new ArrayList<>();
        for (Terminal entry: MenuActivity._terminalList) {
            if(entry.tblRouteID == routeID)
            {
                result.add(entry.Value);
                terminalListBasedOnRouteID.add(entry);
            }
        }
        MenuActivity._PointsArray =  terminalListBasedOnRouteID.toArray(new Terminal[terminalListBasedOnRouteID.size()]);
        MenuActivity._currentRouteIDSelected = routeID;
        result.add("Add Point");
        return  result;
    }
}
