package com.evolve.evx.Modules.ManageLines;


import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evolve.evx.Constants;
import com.evolve.evx.EntityObjects.Lines;
import com.evolve.evx.Enums;
import com.evolve.evx.Helper;

import com.evolve.evx.MenuActivity;
import com.evolve.evx.NonScrollListView;
import com.evolve.evx.R;
import com.evolve.evx.SessionManager;

import java.util.ArrayList;

/**
 * Created by eleazerarcilla on 20/06/2018.
 */

public class LineViewCustomAdapter extends ArrayAdapter<Lines> implements View.OnClickListener{
    private NonScrollListView _LineListView;
    private ManageLinesFragment _manageLinesFragment;
    private Context _context;
    private ArrayList<Lines> _lines = new ArrayList<Lines>();
    private SwipeRefreshLayout _SwipeRefreshLine;
    private FragmentManager     _FragmentManager;
    private int lastPosition = -1;
    Helper _helper = new Helper();
    SessionManager _sessionManager;



    public LineViewCustomAdapter(ArrayList<Lines> data, Context cont,NonScrollListView listView, FragmentManager fm,
                                  SwipeRefreshLayout swipeRefreshRoute, ManageLinesFragment manageLinesFragment){
        super(cont, R.layout.listview_viewlines, data);
        this._context = cont;
        this._lines = data;
        this._SwipeRefreshLine = swipeRefreshRoute;
        this._LineListView = listView;
        this._FragmentManager = fm;
        this._manageLinesFragment = manageLinesFragment;
        this._sessionManager = new SessionManager(_context);

    }

    private static class ViewHolder {
        TextView textLineName;
        TextView textAdminUsername;
        LinearLayout layoutLineItem;
        ImageButton dragger;
        RelativeLayout layoutForAddIcon;
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
                convertView = inflater.inflate(R.layout.listview_viewlines, parent, false);

                viewHolder.textLineName = (TextView) convertView.findViewById(R.id.lineName);
                viewHolder.textAdminUsername = (TextView) convertView.findViewById(R.id.adminUserName);
                viewHolder.layoutLineItem = (LinearLayout) convertView.findViewById(R.id.lineitemLinearLayout);
                viewHolder.dragger = (ImageButton) convertView.findViewById(R.id.lineDragger);
                viewHolder.layoutForAddIcon = convertView.findViewById(R.id.layoutForAddIcon);

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

            final Lines line = _lines.get(position);

            final PopupMenu popup = new PopupMenu(_context, convertView, Gravity.RIGHT);



            popup.getMenuInflater().inflate(R.menu.popup_line_actions, popup.getMenu());
            Menu popupMenu = popup.getMenu();
            if(_sessionManager.getIsSuperAdmin())
            {
                popupMenu.findItem(R.id.itemEdit).setVisible(true);
                popupMenu.findItem(R.id.itemDelete).setVisible(true);
            }
            else
            {
                popupMenu.findItem(R.id.itemEdit).setVisible(false);
                popupMenu.findItem(R.id.itemDelete).setVisible(false);
            }
            viewHolder.textLineName.setText(line.getName());
            viewHolder.textAdminUsername.setText("Managed by: " + line.getAdminUserName());


//            if(line.getName().toLowerCase().contains("add line")) {
//                viewHolder.layoutLineItem.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorWhite));
//                convertView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        //Mysterious bug hacky solution: Double check if the clicked view is the "ADD LINE" row
//                        if (((ViewHolder) view.getTag()).textLineName.getText().toString().toLowerCase().contains("add line"))
//                            _manageLinesFragment.ProcessSelectedLine(Enums.ActionType.ADD,null, null, null);
//                    }
//                });
//
//            }
//            else {
//                viewHolder.layoutLineItem.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorSprayBlue));
                viewHolder.imgbtnShowMoreActions.setVisibility(View.VISIBLE);
                viewHolder.layoutForAddIcon.setVisibility(View.GONE);
                viewHolder.dragger.setVisibility(View.GONE);
//            }

            viewHolder.imgbtnShowMoreActions.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    popup.show();
                }
            });


            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getTitle().toString().equalsIgnoreCase("view routes"))
                    {
                        MenuActivity._FragmentTitle =line.getName();
                        MenuActivity._FragmentTitle =line.getName();
                        MenuActivity._FragmentTitle =line.getName();
                        MenuActivity._currentLineIDSelected = line.getID();
                        try {

                            Bundle bundle = new Bundle();
                            bundle.putInt("lineID", line.getID());
                            MenuActivity._manageRoutesFragment.setArguments(bundle);
                            _FragmentManager.beginTransaction().replace(R.id.content_frame,MenuActivity._manageRoutesFragment)
                                    .addToBackStack(Constants.FRAGMENTNAME_MANAGEROUTES)
                                    .commit();

                        }catch(Exception ex){
                            Toast.makeText(_context,ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Enums.ActionType action = item.getTitle().toString().equalsIgnoreCase("edit") ? Enums.ActionType.EDIT : Enums.ActionType.DELETE;
                        try {
                            _manageLinesFragment.ProcessSelectedLine(action, line.getID(), line.getName(), line.getAdmin_User_ID());

                        }catch (Exception ex){
                            Toast.makeText(_context, ex.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                    return true;
                }
            });


        }catch (Exception ex){
            _helper.logger(ex);
        }
        return convertView;
    }

}
