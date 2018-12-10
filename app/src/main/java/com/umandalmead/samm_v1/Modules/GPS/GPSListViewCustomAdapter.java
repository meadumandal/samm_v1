package com.umandalmead.samm_v1.Modules.GPS;

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

import com.umandalmead.samm_v1.Constants;
import com.umandalmead.samm_v1.EntityObjects.GPS;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.ViewGPSFragment;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by MeadRoseAnn on 2/17/2018.
 */

public class GPSListViewCustomAdapter extends ArrayAdapter<GPS> implements View.OnClickListener {
    private ArrayList<GPS> dataSet;
    Context mContext;
    String TAG = "mead";
    Helper _helper;
    ViewGPSFragment _viewGPSFragment;

    // View lookup cache
    private static class ViewHolder {
        TextView txtPlateNumber;
        TextView txtGPSName;
        TextView txtGPSStatus;
        LinearLayout layoutGPSItem;
        Button btnReconnectGPS;
        GifImageView spinnerGIF;

    }

    public GPSListViewCustomAdapter(ArrayList<GPS> data, Context context, ViewGPSFragment fragment) {
        super(context, R.layout.listview_viewgps, data);
        this.dataSet = data;
        this.mContext=context;
        this._viewGPSFragment = fragment;
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
            viewHolder.txtPlateNumber = convertView.findViewById(R.id.platenumber);
            viewHolder.txtGPSName = (TextView) convertView.findViewById(R.id.gpsname);
            viewHolder.txtGPSStatus = (TextView) convertView.findViewById(R.id.gpsstatus);
            viewHolder.layoutGPSItem = (LinearLayout) convertView.findViewById(R.id.gpsitemLinearLayout);
            viewHolder.btnReconnectGPS = (Button) convertView.findViewById(R.id.btnReconnectGPS);
            viewHolder.spinnerGIF = convertView.findViewById(R.id.spinnergif);


            viewHolder.txtPlateNumber.setTypeface(Helper.FONT_RUBIK_REGULAR);
            viewHolder.txtGPSName.setTypeface(Helper.FONT_RUBIK_REGULAR);
            viewHolder.txtGPSStatus.setTypeface(Helper.FONT_RUBIK_REGULAR);
            viewHolder.btnReconnectGPS.setTypeface(Helper.FONT_RUBIK_REGULAR);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.btnReconnectGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    ShowSpinner(viewHolder);
                    View parentRow = (View) view.getParent();
                    ListView listView = (ListView) parentRow.getParent().getParent().getParent();
                    final int position = listView.getPositionForView(parentRow);
                    Log.i(TAG, String.valueOf(position));
                    GPS gps = dataSet.get(position);
                    String apn;
                    if(gps.getGPSNetworkProvider().toLowerCase().equals("globe"))
                        apn = "http.globe.com.ph";
                    else
                        apn = "internet";
                    _viewGPSFragment._smsAPN = "apn" + Constants.GPS_PASSWORD + " " + apn;
                    _viewGPSFragment._isGPSReconnect = true;
                    _viewGPSFragment.sendSMSMessage(Constants.SMS_BEGIN, gps.getGPSPhone(), viewHolder.btnReconnectGPS, viewHolder.spinnerGIF);


            }
        });

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.txtPlateNumber.setText(dataModel.getGPSPlateNo());
        viewHolder.txtGPSName.setText(dataModel.getGPSName());
        viewHolder.txtGPSStatus.setText(dataModel.getStatus() != null? dataModel.getStatus().toUpperCase():"N/A");
        if(dataModel.getStatus().toLowerCase().equals("online"))

            viewHolder.layoutGPSItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGreen));
        else
            viewHolder.layoutGPSItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorOrange));

        // Return the completed view to render on screen
        return convertView;
    }
    private void ShowSpinner(ViewHolder viewHolder)
    {
        viewHolder.btnReconnectGPS.setVisibility(View.GONE);
        viewHolder.spinnerGIF.setVisibility(View.VISIBLE);
    }
}
