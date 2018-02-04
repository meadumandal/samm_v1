package com.umandalmead.samm_v1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.umandalmead.samm_v1.EntityObjects.Destination;
import com.umandalmead.samm_v1.R;

import java.util.ArrayList;

/**
 * Created by eleazerarcilla on 03/02/2018.
 */

public class DestinationAdapter extends ArrayAdapter<Destination>{
    ArrayList<Destination> _destination, _tempDestination, _suggestions;

    public DestinationAdapter(Context context, ArrayList<Destination> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this._destination = objects;
        this._tempDestination = new ArrayList<Destination>(objects);
        this._suggestions = new ArrayList<Destination>(objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Destination dest = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.destination_list, parent, false);
        }
        TextView txtDest = (TextView) convertView.findViewById(R.id.destinationNameTV);
        ImageView IVDest = (ImageView) convertView.findViewById(R.id.destinationImageView);
        if (txtDest != null)
            txtDest.setText(dest.toString());
        if (IVDest != null && dest.getDestinationPicture() != -1)
            IVDest.setImageResource(dest.getDestinationPicture());
         //colors
        if (position % 2 == 0)
            convertView.setBackgroundColor(getContext().getColor(R.color.colorWhite));
        else
            convertView.setBackgroundColor(getContext().getColor(R.color.colorLightGrey));

        return convertView;
    }


    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Destination dest = (Destination) resultValue;
            return dest.toString();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                _suggestions.clear();
                for (Destination _destentry : _tempDestination) {
                    if (_destentry.toString().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        _suggestions.add(_destentry);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = _suggestions;
                filterResults.count = _suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Destination> c = (ArrayList<Destination>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Destination cust : c) {
                    add(cust);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
