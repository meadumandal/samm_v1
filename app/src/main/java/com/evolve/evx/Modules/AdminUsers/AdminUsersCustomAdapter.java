package com.evolve.evx.Modules.AdminUsers;

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
import com.evolve.evx.R;

import java.util.ArrayList;

/**
 * Created by MeadRoseAnn on 7/29/2018.
 */

public class AdminUsersCustomAdapter extends ArrayAdapter<Users> implements View.OnClickListener {
    private ArrayList<Users> dataSet;
    Context mContext;
    String TAG = "mead";

    // View lookup cache
    private static class ViewHolder {
        TextView txtUsername;
        LinearLayout layoutAdminUser;
        ImageView iconPerson;
        ImageView iconAdd;

    }

    public AdminUsersCustomAdapter(ArrayList<Users> data, Context context) {
        super(context, R.layout.listview_adminusers, data);
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
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_adminusers, parent, false);

            viewHolder.txtUsername = (TextView) convertView.findViewById(R.id.username);
            viewHolder.iconAdd = (ImageView) convertView.findViewById(R.id.iconAdd);
            viewHolder.iconPerson = (ImageView) convertView.findViewById(R.id.iconPerson);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.txtUsername.setText(dataModel.username);
        if (dataModel.username.contains("Add new"))
        {
            viewHolder.iconAdd.setVisibility(View.VISIBLE);
            viewHolder.iconPerson.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.iconPerson.setVisibility(View.VISIBLE);
            viewHolder.iconAdd.setVisibility(View.GONE);
        }

        //viewHolder.layoutAdminUser.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGreen));


        // Return the completed view to render on screen
        return convertView;
    }
}