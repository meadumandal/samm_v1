package com.umandalmead.samm_v1.Adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umandalmead.samm_v1.EntityObjects.GPS;
import com.umandalmead.samm_v1.R;

import java.util.ArrayList;

/**
 * Created by MeadRoseAnn on 2/17/2018.
 */

public class listViewCustomAdapter extends ArrayAdapter<GPS> implements View.OnClickListener {
    private ArrayList<GPS> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtGPSName;
        TextView txtGPSIMEI;
        TextView txtGPSPhone;
        TextView txtGPSNetwork;
    }

    public listViewCustomAdapter(ArrayList<GPS> data, Context context) {
        super(context, R.layout.listview_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        GPS dataModel=(GPS)object;

        switch (v.getId())
        {
            case R.id.gpsname:
                Toast.makeText(this.mContext,"Clicked", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        GPS dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
            viewHolder.txtGPSName = (TextView) convertView.findViewById(R.id.gpsname);
            viewHolder.txtGPSIMEI = (TextView) convertView.findViewById(R.id.gpsimei);
            viewHolder.txtGPSPhone = (TextView) convertView.findViewById(R.id.gpsphone);
            viewHolder.txtGPSNetwork = (TextView) convertView.findViewById(R.id.gpsnetwork);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtGPSName.setText(dataModel.getGPSName());
        viewHolder.txtGPSIMEI.setText(dataModel.getGPSIMEI());
        viewHolder.txtGPSPhone.setText(dataModel.getGPSPhone());
        viewHolder.txtGPSNetwork.setText(dataModel.getGPSNetworkProvider());

        viewHolder.txtGPSPhone.setOnClickListener(this);

        // Return the completed view to render on screen
        return convertView;
    }
}
