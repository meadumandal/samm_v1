package com.umandalmead.samm_v1.Modules.TrackedPUVs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umandalmead.samm_v1.EntityObjects.Eloop;
import com.umandalmead.samm_v1.EntityObjects.GPS;
import com.umandalmead.samm_v1.EntityObjects.Lines;
import com.umandalmead.samm_v1.EntityObjects.Routes;
import com.umandalmead.samm_v1.EntityObjects.Users;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.LoaderDialog;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.NonScrollListView;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.SerializableRefreshLayoutComponents;
import com.umandalmead.samm_v1.ViewGPSFragment;
import com.umandalmead.samm_v1.asyncUpdateTraccarGPSandMySQLEloop;

import java.util.ArrayList;

/**
 * Created by MeadRoseAnn on 2/18/2018.
 */

public class EditGPSDialogFragment extends DialogFragment
{
    //private View pic;

    String _jsonDataModelSelectedGPS, _jsonDataModelSelectedEloop;
    GPS _dataModelSelectedGPS;
    Eloop _dataModelSelectedEloop;

    SwipeRefreshLayout _swipeRefresh;
    FragmentManager _fragmentManager;
    NonScrollListView _gpsListView;

    public EditGPSDialogFragment()
    {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog builder = new Dialog(getActivity());
        try
        {

            View view = getActivity().getLayoutInflater().inflate(R.layout.fragmentdialog_edit_gps, new LinearLayout(getActivity()), false);

            final EditText GPSIMEI = (EditText) view.findViewById(R.id.GPSIMEI);
            final EditText GPSPhone = (EditText) view.findViewById(R.id.GPSMobileNum);
            final EditText GPSPlateNumber = (EditText) view.findViewById(R.id.plateNumber);
            final Spinner GPSNetwork = (Spinner) view.findViewById(R.id.spinnerNetworks);
            final Spinner GPSDriver = (Spinner) view.findViewById(R.id.spinnerDrivers);
            final Spinner GPSRoute = (Spinner) view.findViewById(R.id.spinnerRoutes);
            final Spinner spinnerLines  = view.findViewById(R.id.spinnerLines);

            Button btnUpdate = (Button) view.findViewById(R.id.btnUpdateGPS);
            Button btnDelete = (Button) view.findViewById(R.id.btnDeleteGPS);

            ArrayList<String> networkProviders = new ArrayList<>();
            ArrayList<Users> driverAdapterList = new ArrayList<>();
            ArrayList<Routes> routesAdapterList = new ArrayList<>();
            ArrayList<Lines> linesAdapterList = new ArrayList<>();

            driverAdapterList.add(new Users(0, "Select a driver for this PUV", "", "","","Driver", "", 1));
            routesAdapterList.add(new Routes(0, 0, "Select a route for this PUV", 0));

            driverAdapterList.addAll(MenuActivity._driverList);
            routesAdapterList.addAll(MenuActivity._routeList);
            networkProviders.add("Select the network of the GPS of this PUV");
            networkProviders.add("Globe");
            networkProviders.add("Smart");
            linesAdapterList.add(new Lines(0, MenuActivity._GlobalResource.getString(R.string.GPS_select_line), 0, ""));
            linesAdapterList.addAll(MenuActivity._lineList);

            ArrayAdapter<Users> driverListAdapter = new ArrayAdapter<Users>(getContext(), R.layout.spinner_item, driverAdapterList)
            {

                @Override
                public boolean isEnabled(int position)
                {
                    if (position == 0)
                        return false;
                    else
                        return true;
                }
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent)
                {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;

                    if(position==0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
                    }
                    return view;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    TextView tv = (TextView) super.getView(position, convertView, parent);

                    if(position == 0)
                        tv.setTextColor(Color.GRAY);
                    else
                        tv.setTextColor(Color.BLACK);

                    // Return the view
                    return tv;
                }
            };
            ArrayAdapter<Routes> routesListAdapter = new ArrayAdapter<Routes>(getContext(), R.layout.spinner_item, routesAdapterList)
            {
                @Override
                public boolean isEnabled(int position)
                {
                    if (position == 0)
                        return false;
                    else
                        return true;
                }
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent)
                {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(position==0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
                    }
                    return view;
                }
                @Override
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    TextView tv = (TextView) super.getView(position, convertView, parent);

                    if(position == 0)
                        tv.setTextColor(Color.GRAY);
                    else
                        tv.setTextColor(Color.BLACK);

                    // Return the view
                    return tv;
                }
            };
            ArrayAdapter<String> networkProvidersAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, networkProviders){
                @Override
                public boolean isEnabled(int position)
                {
                    if (position == 0)
                        return false;
                    else
                        return true;
                }
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent)
                {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;

                    if(position==0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
                    }
                    return view;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    TextView tv = (TextView) super.getView(position, convertView, parent);

                    if(position == 0)
                        tv.setTextColor(Color.RED);
                    else
                        tv.setTextColor(Color.BLACK);

                    // Return the view
                    return tv;
                }


            };


            ArrayAdapter<Lines> linesListAdapter = new ArrayAdapter<Lines>(getContext(), R.layout.spinner_item, linesAdapterList)
            {

                @Override
                public boolean isEnabled(int position)
                {
                    if (position == 0)
                        return false;
                    else
                        return true;
                }
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent)
                {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;

                    if(position==0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
                    }
                    return view;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    TextView tv = (TextView) super.getView(position, convertView, parent);

                    if(position == 0)
                        tv.setTextColor(Color.RED);
                    else
                        tv.setTextColor(Color.BLACK);

                    // Return the view
                    return tv;
                }
            };

            spinnerLines.setAdapter(linesListAdapter);
            GPSNetwork.setAdapter(networkProvidersAdapter);
            GPSDriver.setAdapter(driverListAdapter);
            GPSRoute.setAdapter(routesListAdapter);


//            driverAdapterList.add(new Users(0, "Select a driver for this PUV", "", "","","Driver", "", 1));
//            routesAdapterList.add(new Routes(0, "Select a route for this PUV"));


            Bundle argumentsBundle = getArguments();
            if(argumentsBundle!=null)
            {
                Gson gson = new Gson();
                _jsonDataModelSelectedGPS = getArguments().getString("selectedGPS");
                _jsonDataModelSelectedEloop = getArguments().getString("selectedEloop");
                _dataModelSelectedGPS =  gson.fromJson(_jsonDataModelSelectedGPS, GPS.class);
                _dataModelSelectedEloop = gson.fromJson(_jsonDataModelSelectedEloop, Eloop.class);
                int userPositionInSpinner = 0, routePositionInSpinner = 0, linePositionInSpinner = 0;
                for(Users user:driverAdapterList)
                {
                    if (_dataModelSelectedEloop.tblUsersID == user.ID)
                        break;
                    else
                        userPositionInSpinner+=1;

                }

                for (Routes route:routesAdapterList)
                {
                    if(_dataModelSelectedEloop.tblRoutesID == route.getID())
                        break;
                    else
                        routePositionInSpinner+=1;
                }
                for (Lines lines:linesAdapterList)
                {
                    if(_dataModelSelectedEloop.tblLinesID == lines.getID())
                        break;
                    else
                        linePositionInSpinner+=1;
                }
                if(userPositionInSpinner >= driverAdapterList.size())
                {
                    userPositionInSpinner = 0;
                }
                if (routePositionInSpinner >= routesAdapterList.size())
                {
                    routePositionInSpinner = 0;
                }
                if (linePositionInSpinner>=linesAdapterList.size())
                {
                    linePositionInSpinner = 0;
                }

                GPSPhone.setText(_dataModelSelectedGPS.getGPSPhone());
                GPSIMEI.setText(_dataModelSelectedGPS.getGPSIMEI());
                GPSPlateNumber.setText(_dataModelSelectedEloop.PlateNumber);
                GPSDriver.setSelection(userPositionInSpinner);
                GPSRoute.setSelection(routePositionInSpinner);
                spinnerLines.setSelection(linePositionInSpinner);


                if (_dataModelSelectedGPS.getGPSNetworkProvider().toLowerCase().equals("globe"))
                    GPSNetwork.setSelection(1);
                else
                    GPSNetwork.setSelection(2);
                SerializableRefreshLayoutComponents swipeRefreshLayoutSerializable =(SerializableRefreshLayoutComponents) argumentsBundle.getSerializable("swipeRefreshLayoutSerializable");
                _swipeRefresh = swipeRefreshLayoutSerializable._swipeRefreshLayoutSerializable;
                _fragmentManager = swipeRefreshLayoutSerializable._fragmentManager;
                _gpsListView = swipeRefreshLayoutSerializable._listView;
            }

            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setContentView(view);





            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoaderDialog GPSUpdateLoader = new LoaderDialog(getActivity(), "Updating GPS", "Please wait...");
                    GPSUpdateLoader.setCancelable(false);
                    GPSUpdateLoader.show();
                    String IMEI = GPSIMEI.getText().toString();
                    String Phone = GPSPhone.getText().toString();
                    String PlateNumber = GPSPlateNumber.getText().toString();
                    int tblRouteID = ((Routes)GPSRoute.getSelectedItem()).getID();
                    int tblUsersID = ((Users)GPSDriver.getSelectedItem()).ID;
                    String networkProvider = GPSNetwork.getSelectedItem().toString();
                    _dataModelSelectedGPS.setGPSIMEI(IMEI);
                    _dataModelSelectedGPS.setGPSPhone(Phone);
                    _dataModelSelectedGPS.setGPSNetworkProvider(networkProvider);
                    _dataModelSelectedGPS.setGPSName("SAMM_"+ IMEI.substring(IMEI.length()-5, IMEI.length()));
                    _dataModelSelectedEloop.PlateNumber = PlateNumber;
                    _dataModelSelectedEloop.tblRoutesID = tblRouteID;
                    _dataModelSelectedEloop.tblUsersID = tblUsersID;
                    _dataModelSelectedEloop.DeviceName= "SAMM_"+ IMEI.substring(IMEI.length()-5, IMEI.length());

                    new asyncUpdateTraccarGPSandMySQLEloop(getActivity(), GPSUpdateLoader, getActivity(), EditGPSDialogFragment.this, _dataModelSelectedGPS, _dataModelSelectedEloop, _swipeRefresh, _gpsListView).execute();


                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());


                    builder.setTitle("Delete GPS")
                            .setMessage("Are you sure you want to delete this GPS?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        LoaderDialog progDialog= new LoaderDialog(getActivity(),"Deleting GPS","Please wait...");
                                        progDialog.setCancelable(false);
                                        progDialog.show();

                                        new asyncDeleteTraccarGPS(getActivity(),
                                                progDialog,
                                                getActivity(),
                                                EditGPSDialogFragment.this,
                                                _dataModelSelectedGPS,
                                                _swipeRefresh,
                                                _gpsListView,
                                                ViewGPSFragment._viewGPSFragment).execute();
                                    }
                                    catch(Exception ex)
                                    {
                                        Helper.logger(ex,true);
                                    }


                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();




                }
            });
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }
        return builder;

    }
}