package com.evolve.evx.Modules.DriverUsers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evolve.evx.EntityObjects.Users;
import com.evolve.evx.Helper;
import com.evolve.evx.R;

import java.util.ArrayList;

/**
 * Created by MeadRoseAnn on 12/2/2018.
 */

public class DriverUsersCustomAdapter extends ArrayAdapter<Users> implements View.OnClickListener {
    private ArrayList<Users> dataSet;
    Context mContext;
    String TAG = "mead";
    private Helper _helper = new Helper();

    // View lookup cache
    private static class ViewHolder {
        TextView txtName,txtUsername, txtPlateNumber;
        LinearLayout layoutAdminUser;
        ImageView iconPerson;


    }

    public DriverUsersCustomAdapter(ArrayList<Users> data, Context context) {
        super(context, R.layout.listview_driverusers, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

    }

    private int lastPosition = -1;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Users dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        final DriverUsersCustomAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new DriverUsersCustomAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_driverusers, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.txtName);
            viewHolder.txtUsername = convertView.findViewById(R.id.username);
            viewHolder.txtPlateNumber = convertView.findViewById(R.id.txtPlateNumber);
            viewHolder.iconPerson = convertView.findViewById(R.id.iconPerson);

            viewHolder.txtName.setTypeface(Helper.FONT_RUBIK_REGULAR);
            viewHolder.txtUsername.setTypeface(Helper.FONT_RUBIK_REGULAR);
            viewHolder.txtPlateNumber.setTypeface(Helper.FONT_RUBIK_REGULAR);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DriverUsersCustomAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        String assignedVehiclePlateNumber = _helper.getPlateNumberBasedOnUserID(dataModel.ID);

        viewHolder.txtName.setText(dataModel.firstName + " " + dataModel.lastName);
        viewHolder.txtPlateNumber.setText(
                _helper.getLineNameBasedOnLineID(dataModel.tblLineID)
                + (assignedVehiclePlateNumber.isEmpty() ? "" : ": "+ assignedVehiclePlateNumber));
        viewHolder.txtUsername.setText(dataModel.username);


        //viewHolder.layoutAdminUser.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGreen));


        // Return the completed view to render on screen
        return convertView;
    }
}