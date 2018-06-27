package com.umandalmead.samm_v1.Adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.umandalmead.samm_v1.AddPointsFragment;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.NonScrollListView;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.MenuActivity.AddPointDialog;

import java.util.ArrayList;

/**
 * Created by eleazerarcilla on 20/06/2018.
 */

public class PointsViewCustomAdapter extends ArrayAdapter<String> implements View.OnClickListener {
    private NonScrollListView _PointListView;
    private Context _context;
    private ArrayList<String> _TestData = new ArrayList<String>();
    private SwipeRefreshLayout _SwipeRefreshPoint;
    private FragmentManager _FragmentManager;
    private int lastPosition = -1;
    private static class PointViewHolder {
        TextView textPointName;
        LinearLayout layoutPointItem;
        ImageButton pointDragger;
        Button editButton;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;
        try{
            PointViewHolder viewHolder = new PointViewHolder();
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_viewpoints, parent, false);
                viewHolder.textPointName = (TextView) convertView.findViewById(R.id.pointName);
                viewHolder.layoutPointItem = (LinearLayout) convertView.findViewById(R.id.pointsItemLinearLayout);
                viewHolder.pointDragger = (ImageButton) convertView.findViewById(R.id.pointDragger);
                viewHolder.editButton = (Button) convertView.findViewById(R.id.btnEditPoint);
                result=convertView;

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (PointViewHolder) convertView.getTag();
                result=convertView;
            }

            Animation animation = AnimationUtils.loadAnimation(_context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            result.startAnimation(animation);
            lastPosition = position;

            final String PointValue = _TestData.get(position);
            viewHolder.textPointName.setText(PointValue);
            if(PointValue.toLowerCase().contains("add point")) {
                viewHolder.layoutPointItem.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorWhite));
                viewHolder.pointDragger.setImageResource(R.drawable.ic_add_black_24dp);
                if(PointValue.toLowerCase().contains("add point")) {
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((MenuActivity) _context).AddNewStationPoint("ADD");
                        }
                    });
                }
            }
            else {
                viewHolder.layoutPointItem.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorGreen));
                viewHolder.pointDragger.setImageResource(R.drawable.ic_reorder_black_24dp);
                viewHolder.editButton.setVisibility(View.VISIBLE);
                viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MenuActivity)_context).ModifyStationPoint("UPDATE",PointValue);
                    }
                });
            }
        }catch (Exception ex){
            throw ex;
        }
        return convertView;
    }

    public PointsViewCustomAdapter(ArrayList<String> data, Context cont, NonScrollListView listView, FragmentManager fm,
                                  SwipeRefreshLayout swipeRefreshPoints){
        super(cont, R.layout.listview_viewpoints, data);
        this._context = cont;
        this._TestData = data;
        this._SwipeRefreshPoint = swipeRefreshPoints;
        this._PointListView = listView;
        this._FragmentManager = fm;
    }


    @Override
    public void onClick(View view) {
    }
}
