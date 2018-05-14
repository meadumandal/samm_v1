package com.umandalmead.samm_v1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.umandalmead.samm_v1.EntityObjects.GPS;

import java.util.ArrayList;

/**
 * Created by MeadRoseAnn on 2/18/2018.
 */

public class EditGPSDialogFragment extends DialogFragment
{
    //private View pic;
    String TAG="mead";
    String _datamodeljson;
    GPS _datamodel;

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

            final EditText GPSPhone = (EditText) view.findViewById(R.id.GPSMobileNum);
            final EditText GPSIMEI = (EditText) view.findViewById(R.id.GPSIMEI);
            final Spinner GPSNetwork = (Spinner) view.findViewById(R.id.spinnerNetworkProviders);
            Button btnUpdate = (Button) view.findViewById(R.id.btnUpdateGPS);
            Button btnDelete = (Button) view.findViewById(R.id.btnDeleteGPS);
            ArrayList<String> networkProviders = new ArrayList<>();
            networkProviders.add("Select GSM SIM Network Provider");
            networkProviders.add("Globe");
            networkProviders.add("Smart");

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProgressDialog progDialog = new ProgressDialog(getActivity());
                    progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progDialog.setTitle("Updating GPS");
                    progDialog.setMessage("Please wait...");
                    progDialog.setCancelable(false);
                    progDialog.show();
                    String IMEI = GPSIMEI.getText().toString();
                    String Phone = GPSPhone.getText().toString();
                    String networkProvider = GPSNetwork.getSelectedItem().toString();
                    _datamodel.setGPSIMEI(IMEI);
                    _datamodel.setGPSPhone(Phone);
                    _datamodel.setGPSNetworkProvider(networkProvider);

                    new asyncUpdateTraccarGPS(getActivity(), progDialog, getActivity(), _datamodel).execute();


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
                                        ProgressDialog progDialog = new ProgressDialog(getActivity());
                                        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        progDialog.setTitle("Deleting GPS");
                                        progDialog.setMessage("Please wait...");
                                        progDialog.setCancelable(false);
                                        progDialog.show();

                                        new asyncDeleteTraccarGPS(getActivity(), progDialog, getActivity(), _datamodel).execute();
                                    }
                                    catch(Exception ex)
                                    {
                                        Helper.logger(ex);
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

            };
            GPSNetwork.setAdapter(networkProvidersAdapter);

            if(getArguments()!=null)
            {
                Gson gson = new Gson();
                _datamodeljson = getArguments().getString("datamodel");
                _datamodel =  gson.fromJson(_datamodeljson, GPS.class);

                GPSPhone.setText(_datamodel.getGPSPhone());
                GPSIMEI.setText(_datamodel.getGPSIMEI());
                if (_datamodel.getGPSNetworkProvider().toLowerCase().equals("globe"))
                    GPSNetwork.setSelection(1);
                else
                    GPSNetwork.setSelection(2);
            }


//            // Retrieve layout elements
//            TextView title = (TextView) view.findViewById(R.id.text_title);
//
//            // Set values
//            title.setText("Not perfect yet");

            // Build dialog

            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setContentView(view);

        }
        catch(Exception ex)
        {
            Helper.logger(ex);
        }
        return builder;

    }
}