package com.umandalmead.samm_v1.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.umandalmead.samm_v1.EntityObjects.GPS;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.R;

import java.util.ArrayList;

/**
 * Created by MeadRoseAnn on 2/17/2018.
 */

public class GPSListViewCustomAdapter extends ArrayAdapter<GPS> implements View.OnClickListener {
    private ArrayList<GPS> dataSet;
    Context mContext;
    String TAG = "mead";
    Helper _helper;

    // View lookup cache
    private static class ViewHolder {
        TextView txtGPSName;
        TextView txtGPSStatus;
        LinearLayout layoutGPSItem;
        Button btnReconnectGPS;
    }

    public GPSListViewCustomAdapter(ArrayList<GPS> data, Context context) {
        super(context, R.layout.listview_viewgps, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {
//
//        int position=(Integer) v.getTag();
//        Object object= getItem(position);
//        GPS dataModel=(GPS)object;
//
//        switch (v.getId())
//        {
//            case R.id.gpsname:
//                Toast.makeText(this.mContext,"Clicked", Toast.LENGTH_LONG).show();
//                break;
//        }
    }

    private int lastPosition = -1;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        GPS dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_viewgps, parent, false);
            viewHolder.txtGPSName = (TextView) convertView.findViewById(R.id.gpsname);
            viewHolder.txtGPSStatus = (TextView) convertView.findViewById(R.id.gpsstatus);
            viewHolder.layoutGPSItem = (LinearLayout) convertView.findViewById(R.id.gpsitemLinearLayout);
            viewHolder.btnReconnectGPS = (Button) convertView.findViewById(R.id.btnReconnectGPS);
            viewHolder.txtGPSName.setTypeface(MenuActivity.FONT_RUBIK_REGULAR);
            viewHolder.txtGPSStatus.setTypeface(MenuActivity.FONT_RUBIK_BOLD);
            viewHolder.btnReconnectGPS.setTypeface(MenuActivity.FONT_RUBIK_BOLD);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.btnReconnectGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.btnReconnectGPS.setText("Reconnecting..");
                viewHolder.btnReconnectGPS.setEnabled(false);
                View parentRow = (View) view.getParent();
                ListView listView = (ListView) parentRow.getParent().getParent();
                final int position = listView.getPositionForView(parentRow);
                Log.i(TAG, String.valueOf(position));
                GPS gps = dataSet.get(position);
                String apn;
                if(gps.getGPSNetworkProvider().toLowerCase().equals("globe"))
                    apn = "http.globe.com.ph";
                else
                    apn = "internet";
                ((MenuActivity)mContext)._smsAPN = apn;
                ((MenuActivity)mContext)._isGPSReconnect = true;
                ((MenuActivity)mContext).sendSMSMessage("apn123456 "+apn, gps.getGPSPhone(), viewHolder.btnReconnectGPS);


            }
        });

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtGPSName.setText(dataModel.getGPSName());
        viewHolder.txtGPSStatus.setText(dataModel.getStatus() != null? dataModel.getStatus().toUpperCase():"N/A");
        if(dataModel.getStatus().toLowerCase().equals("online"))

            viewHolder.layoutGPSItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGreen));
        else
            viewHolder.layoutGPSItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorRed));

        // Return the completed view to render on screen
        return convertView;
    }
}
