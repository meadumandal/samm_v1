package com.evolve.evx;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evolve.evx.EntityObjects.Lines;
import com.evolve.evx.EntityObjects.Terminal;

import java.util.List;

import static com.evolve.evx.MenuActivity._activity;
import static com.evolve.evx.MenuActivity._context;

/**
 * Created by MeadRoseAnn on 11/11/2018.
 */

public class CustomAdapterForValueDisplayPair extends ArrayAdapter{


    LayoutInflater mInflater;
    Integer mResource;
    List<?> items;
    Helper _helper;
    public CustomAdapterForValueDisplayPair(@NonNull Context context, int resource, @NonNull List<?> objects) {

        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
        _helper = new Helper(_activity, _context);
    }
    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        final View view = mInflater.inflate(mResource, parent, false);
        LinearLayout linearLayout = view.findViewById(R.id.value_display_list_item);
        TextView textview_value = (TextView) view.findViewById(R.id.textview_value);
        TextView textview_display = (TextView) view.findViewById(R.id.textview_display);

        linearLayout.setPadding(10,10,10,10);
        linearLayout.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorDarkGrey));
        textview_value.setTextColor(Color.WHITE);
        textview_display.setTextColor(Color.WHITE);
        textview_value.setTypeface(Helper.FONT_RUBIK_REGULAR);
        textview_display.setTypeface(Helper.FONT_RUBIK_REGULAR);
        textview_display.setPadding(5,5,5,5);
        textview_display.setTextSize(15);

        Object data = items.get(position);
        if (data instanceof Terminal)
        {
            Terminal terminalData = (Terminal) data;

            textview_value.setText(terminalData.getID().toString());
            textview_display.setText(terminalData.getDescription());
        }
        else if (data instanceof Lines)
        {
            Lines lineData = (Lines) data;

            textview_value.setText(lineData.getID().toString());
            textview_display.setText(lineData.getName());
        }
        if (position==0)
        {
            textview_display.setTextColor(Color.GRAY);
        }

        return view;
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {



        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
        }
        final View view = mInflater.inflate(mResource, parent, false);
        LinearLayout linearLayout = view.findViewById(R.id.value_display_list_item);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.bottomMargin = _helper.DpToPx(10);
        linearLayout.setLayoutParams(layoutParams);
        TextView textview_value = (TextView) view.findViewById(R.id.textview_value);
        TextView textview_display = (TextView) view.findViewById(R.id.textview_display);
        textview_value.setTextColor(Color.WHITE);
        textview_display.setTextColor(Color.WHITE);
//        textview_display.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorDarkGrey));

        textview_value.setTypeface(Helper.FONT_RUBIK_REGULAR);
        textview_display.setTypeface(Helper.FONT_RUBIK_REGULAR);

        Object data = items.get(position);
        if (data instanceof Terminal)
        {
            Terminal terminalData = (Terminal) data;

            textview_value.setText(terminalData.getID().toString());
            textview_display.setText(terminalData.getDescription());
        }
        else if (data instanceof Lines)
        {
            Lines lineData = (Lines) data;

            textview_value.setText(lineData.getID().toString());
            textview_display.setText(lineData.getName());
        }

        if (position == 0)
        {
            textview_display.setTextColor(Color.GRAY);
        }

        return view;
    }
    @Override
    public boolean isEnabled(int position)
    {
        if(position == 0)
            return false;
        else
            return true;
    }


}
