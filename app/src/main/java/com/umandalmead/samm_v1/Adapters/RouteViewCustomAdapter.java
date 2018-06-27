package com.umandalmead.samm_v1.Adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umandalmead.samm_v1.AddPointsFragment;
import com.umandalmead.samm_v1.EntityObjects.Routes;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.NonScrollListView;
import com.umandalmead.samm_v1.R;

import java.util.ArrayList;

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

    public RouteViewCustomAdapter(ArrayList<Routes> data, Context cont,NonScrollListView listView, FragmentManager fm,
                                  SwipeRefreshLayout swipeRefreshRoute){
        super(cont, R.layout.listview_viewroutes, data);
        this._context = cont;
        this._TestData = data;
        this._SwipeRefreshRoute = swipeRefreshRoute;
        this._RouteListView = listView;
        this._FragmentManager = fm;
    }

    private static class ViewHolder {
        TextView textRouteName;
        LinearLayout layoutRouteItem;
        ImageButton dragger;
        Button viewRoute;
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
                viewHolder.dragger = (ImageButton) convertView.findViewById(R.id.routeDragger);
                viewHolder.viewRoute = (Button) convertView.findViewById(R.id.btnViewRoute);
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

            viewHolder.textRouteName.setText(route.getRouteName());
            if(route.getRouteName().toLowerCase().contains("add route")) {
                viewHolder.layoutRouteItem.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorWhite));
            }
            else {
                viewHolder.layoutRouteItem.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorSprayBlue));
                viewHolder.viewRoute.setVisibility(View.VISIBLE);
                viewHolder.dragger.setVisibility(View.GONE);
            }

            viewHolder.viewRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MenuActivity._FragmentTitle = route.getRouteName();
                    ((MenuActivity)_context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new AddPointsFragment())
                            .commit();

                }
            });


        }catch (Exception ex){
            throw ex;
        }
        return convertView;
    }
}
