package com.umandalmead.samm_v1.Modules.GPS;

import android.app.Dialog;
import android.app.PendingIntent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
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

import com.umandalmead.samm_v1.Constants;
import com.umandalmead.samm_v1.EntityObjects.Lines;
import com.umandalmead.samm_v1.EntityObjects.Routes;
import com.umandalmead.samm_v1.EntityObjects.Users;
import com.umandalmead.samm_v1.ErrorDialog;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.ViewGPSFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MeadRoseAnn on 12/1/2018.
 */

public class AddGPSDialogFragment extends DialogFragment  {

    Button btnAddGPS;
    EditText txtphoneNo, txtIMEI, txtPlateNumber;
    Spinner spinnerNetworks, spinnerRoutes, spinnerDrivers, spinnerLines;

    private String _GPSMobileNumber;
    private String _gpsPlateNumber;
    private Integer _gpsTblUsersID;
    private Integer _gpsTblRoutesID;
    private Integer _gpsTblLineID;
    private String _gpsNetwork;
    private String _gpsIMEI;
    public String _smsAPN;
    public String _smsMessageForGPS;
    public Button _ReconnectGPSButton;
    public Boolean _isGPSReconnect = false;

//
//    private SentSMSBroadcastReceiver _smsSentBroadcastReceiver;
//    private SMSDeliveredBroadcastReceiver _smsDeliveredBroadcastReceiver;
//    private PendingIntent _sentSMSPendingIntent;
//    private PendingIntent _deliveredSMSPendingIntent;
    Constants _constants = new Constants();

    public String TAG ="mead";

    PendingIntent sentPendingIntent;
    PendingIntent deliveredPendingIntent;
    public HashMap<String, Boolean> smsCommandsStatus = new HashMap<>();

    public AddGPSDialogFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try
        {
            Dialog builder = new Dialog(MenuActivity._activity);
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View view = MenuActivity._activity.getLayoutInflater().inflate(R.layout.dialog_add_gps, new LinearLayout(MenuActivity._activity), false);
            builder.setContentView(view);


            ArrayList<String> networkList = new ArrayList<>();
            ArrayList<Users> driverAdapterList = new ArrayList<>();
            ArrayList<Routes> routesAdapterList = new ArrayList<>();
            ArrayList<Lines> linesAdapterList = new ArrayList<>();

            networkList.add(MenuActivity._GlobalResource.getString(R.string.GPS_select_network));
            driverAdapterList.add(new Users(0,MenuActivity._GlobalResource.getString(R.string.GPS_select_driver), "", "","","Driver", "", 1));
            routesAdapterList.add(new Routes(0, 0, MenuActivity._GlobalResource.getString(R.string.GPS_select_route), 0));
            linesAdapterList.add(new Lines(0, MenuActivity._GlobalResource.getString(R.string.GPS_select_line), 0, ""));

            networkList.add(MenuActivity._GlobalResource.getString(R.string.NETWORK_GLOBE));
            networkList.add(MenuActivity._GlobalResource.getString(R.string.NETWORK_SMART));
            driverAdapterList.addAll(MenuActivity._driverList);
            routesAdapterList.addAll(MenuActivity._routeList);
            linesAdapterList.addAll(MenuActivity._lineList);

            ArrayAdapter<Users> driverListAdapter = new ArrayAdapter<Users>(MenuActivity._context, R.layout.spinner_item, driverAdapterList)
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
            ArrayAdapter<Routes> routesListAdapter = new ArrayAdapter<Routes>(MenuActivity._context, R.layout.spinner_item, routesAdapterList)
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
            };


            ArrayAdapter<Lines> linesListAdapter = new ArrayAdapter<Lines>(MenuActivity._context, R.layout.spinner_item, linesAdapterList)
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
            ArrayAdapter<String> networkProvidersAdapter = new ArrayAdapter<String>(MenuActivity._context, R.layout.spinner_item, networkList){
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


            spinnerNetworks = (Spinner) view.findViewById(R.id.spinnerNetworks);
            spinnerRoutes = (Spinner) view.findViewById(R.id.spinnerRoutes);
            spinnerDrivers = (Spinner) view.findViewById(R.id.spinnerDrivers);
            spinnerLines = view.findViewById(R.id.spinnerLines);


            spinnerNetworks.setAdapter(networkProvidersAdapter);
            spinnerDrivers.setAdapter(driverListAdapter);
            spinnerRoutes.setAdapter(routesListAdapter);
            spinnerLines.setAdapter(linesListAdapter);

            btnAddGPS = (Button) view.findViewById(R.id.btnAddGPS);
            txtphoneNo = (EditText) view.findViewById(R.id.GPSMobileNum);
            txtIMEI  = (EditText) view.findViewById(R.id.GPSIMEI);
            txtPlateNumber = (EditText) view.findViewById(R.id.plateNumber);
            btnAddGPS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try
                    {
                        ViewGPSFragment._LoaderDialog.setMessage(MenuActivity._GlobalResource.getString(R.string.GPS_Initialize));
                        ViewGPSFragment._LoaderDialog.show();
                        _GPSMobileNumber = txtphoneNo.getText().toString().trim();
                        _gpsIMEI = txtIMEI.getText().toString().trim();
                        _gpsPlateNumber = txtPlateNumber.getText().toString().trim();
                        _gpsNetwork = spinnerNetworks.getSelectedItem().toString();
                        _gpsTblRoutesID = ((Routes)spinnerRoutes.getSelectedItem()).getID();
                        _gpsTblUsersID = ((Users)spinnerDrivers.getSelectedItem()).ID;
                        _gpsTblLineID = ((Lines)spinnerLines.getSelectedItem()).getID();
                        if(_GPSMobileNumber.trim().length() == 0
                                || _gpsIMEI.trim().length() == 0
                                || spinnerNetworks.getSelectedItem().toString().equals(MenuActivity._GlobalResource.getString(R.string.GPS_select_network))
                                || _gpsTblLineID == 0
                                )
                        {
                            ErrorDialog errorDialog = new ErrorDialog(MenuActivity._activity, MenuActivity._GlobalResource.getString(R.string.error_please_supply_required_fields));
                            errorDialog.show();
                            ViewGPSFragment._LoaderDialog.dismiss();
                        }
                        else {
                            if (spinnerNetworks.getSelectedItem().toString().equals("Globe"))
                                _smsAPN = _constants.SMS_APN_GLOBE;
                            else
                                _smsAPN = _constants.SMS_APN_SMART;

                            //TO DO: Call AsynTask class
                            //Add to MySQL SAMM Database
                            //Add to Traccar Server
                            //Send SMS to activate the GPS
                            new asyncAddTraccarGPS(MenuActivity._context,
                                    ViewGPSFragment._LoaderDialog,
                                    MenuActivity._activity,
                                    AddGPSDialogFragment.this,
                                    ViewGPSFragment._viewGPSFragment).execute(
                                    "SAMM_"+ _gpsIMEI.substring(_gpsIMEI.length()-5, _gpsIMEI.length()),
                                    _gpsIMEI,
                                    _GPSMobileNumber,
                                    _gpsNetwork,
                                    _gpsPlateNumber,
                                    _gpsTblRoutesID.toString(),
                                    _gpsTblUsersID.toString(),
                                    _gpsTblLineID.toString());
                        }
                    }catch(Exception ex)
                    {
                        ViewGPSFragment._LoaderDialog.dismiss();

                        Helper.logger(ex,true);
                        Toast.makeText(getContext(),
                                MenuActivity._GlobalResource.getString(R.string.error_an_error_occurred), Toast.LENGTH_LONG).show();
                    }
                }
            });

//        _smsSentBroadcastReceiver = new SentSMSBroadcastReceiver();
//        _smsDeliveredBroadcastReceiver = new SMSDeliveredBroadcastReceiver();
//
//        String SMS_SENT = MenuActivity._GlobalResource.getString(R.string.SMS_SENT);
//        String SMS_DELIVERED = MenuActivity._GlobalResource.getString(R.string.SMS_DELIVERED);
//
//        _sentSMSPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SMS_SENT), 0);
//        _deliveredSMSPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SMS_DELIVERED), 0);
//
//        //register BroadcastReceiver
//        IntentFilter intentFilter = new IntentFilter(GeofenceTransitionsIntentService.ACTION_MyIntentService);
//        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
//
//        getActivity().registerReceiver(_smsSentBroadcastReceiver, new IntentFilter(SMS_SENT));
//        getActivity().registerReceiver(_smsDeliveredBroadcastReceiver, new IntentFilter(SMS_DELIVERED));
            return builder;
        }
        catch(Exception ex)
        {
            Toast.makeText(getContext(), "Error occured", Toast.LENGTH_LONG).show();
            return null;
        }

    }
//    public void sendSMSMessage(String message, String phone) {
//        try
//        {
//
//            Log.i(LOG_TAG, MenuActivity._GlobalResource.getString(R.string.SMS_sending_to_add_new_gps));
//            this._smsMessageForGPS = message;
//            if (ContextCompat.checkSelfPermission(MenuActivity._activity,android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//
//                Log.i(_constants.LOG_TAG,MenuActivity._GlobalResource.getString(R.string.SMS_status_sending_with_extra_white_space) + this._smsMessageForGPS + MenuActivity._GlobalResource.getString(R.string.ellipsis));
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{android.Manifest.permission.SEND_SMS},
//                        MY_PERMISSIONS_REQUEST_SEND_SMS);
//            }
//            else
//            {
//                Log.i(_constants.LOG_TAG,MenuActivity._GlobalResource.getString(R.string.SMS_status_sending_with_extra_white_space)  + this._smsMessageForGPS + MenuActivity._GlobalResource.getString(R.string.ellipsis));
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(phone, null, this._smsMessageForGPS, _sentSMSPendingIntent, _deliveredSMSPendingIntent);
//                Log.i(_constants.LOG_TAG, message + MenuActivity._GlobalResource.getString(R.string.SMS_status_sent_with_extra_white_space_prefix));
//            }
//        }
//        catch(Exception ex)
//        {
//            Helper.logger(ex,true);
//
//            _LoaderDialog.dismiss();
//            Toast.makeText(getContext(),MenuActivity._GlobalResource.getString(R.string.error_exception_with_concat) + ex.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    public void sendSMSMessage(String message, String phone, Button btnReconnectGPS) {
//        try
//        {
//            Log.i(LOG_TAG, MenuActivity._GlobalResource.getString(R.string.SMS_sending_to_reconnect_gps));
//            this._smsMessageForGPS = message;
//            this._GPSMobileNumber = phone;
//
//            if (ContextCompat.checkSelfPermission(MenuActivity._activity,android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//                Log.i(_constants.LOG_TAG,MenuActivity._GlobalResource.getString(R.string.SMS_status_sending_with_extra_white_space)+ this._smsMessageForGPS + MenuActivity._GlobalResource.getString(R.string.ellipsis));
////                    Toast.makeText(getApplicationContext(), "sending " + this._smsMessageForGPS, Toast.LENGTH_LONG).show();
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{android.Manifest.permission.SEND_SMS},
//                        MY_PERMISSIONS_REQUEST_SEND_SMS);
//            }
//            else
//            {
//                Log.i(_constants.LOG_TAG,MenuActivity._GlobalResource.getString(R.string.SMS_status_sending_with_extra_white_space) + this._smsMessageForGPS + MenuActivity._GlobalResource.getString(R.string.ellipsis));
////                Toast.makeText(getApplicationContext(), "sending " + this._smsMessageForGPS, Toast.LENGTH_LONG).show();
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(phone, null, this._smsMessageForGPS, _sentSMSPendingIntent, _deliveredSMSPendingIntent);
//                this._ReconnectGPSButton = btnReconnectGPS;
//
//                Log.i(_constants.LOG_TAG, message + MenuActivity._GlobalResource.getString(R.string.SMS_status_sent_with_extra_white_space_prefix));
////                Toast.makeText(getApplicationContext(),this._smsMessageForGPS + " sent", Toast.LENGTH_LONG).show();
//            }
//        }
//        catch(Exception ex)
//        {
//            Helper.logger(ex,true);
//            _LoaderDialog.dismiss();
//            Toast.makeText(getContext(),MenuActivity._GlobalResource.getString(R.string.error_exception_with_concat) + ex.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    public class SentSMSBroadcastReceiver extends BroadcastReceiver
//    {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try
//            {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        if (_smsMessageForGPS.equals(_constants.SMS_BEGIN)) {
//                            _LoaderDialog = new LoaderDialog(getActivity(),
//                                    MenuActivity._GlobalResource.getString(R.string.SMS_configuring_gps_via_sms),
//                                    MenuActivity._GlobalResource.getString(R.string.SMS_activating_gprs));
//                            sendSMSMessage(_constants.SMS_GPRS, _GPSMobileNumber);
//                        }
//                        else if (_smsMessageForGPS.equals(_constants.SMS_GPRS)) {
//                            if(_isGPSReconnect) {
//                                _ReconnectGPSButton.setText(MenuActivity._GlobalResource.getString(R.string.SMS_button_reconnect));
//                                _ReconnectGPSButton.setEnabled(true);
//                            }
//                            else {
//                                _LoaderDialog = new LoaderDialog(getActivity(),
//                                        MenuActivity._GlobalResource.getString(R.string.SMS_configuring_gps_via_sms),
//                                        MenuActivity._GlobalResource.getString(R.string.SMS_setting_apn));
//
//                                sendSMSMessage(_smsAPN, _GPSMobileNumber);
//                            }
//                        }
//                        else if (_smsMessageForGPS.equals(_smsAPN)) {
//                            if(_isGPSReconnect) {
//                                sendSMSMessage(_constants.SMS_GPRS, _GPSMobileNumber);
//                            }
//                            else {
//                                _LoaderDialog = new LoaderDialog(getActivity(),
//                                        MenuActivity._GlobalResource.getString(R.string.SMS_configuring_gps_via_sms),
//                                        MenuActivity._GlobalResource.getString(R.string.SMS_configuring_ip_and_port));
//                                sendSMSMessage(_constants.SMS_ADMINIP, _GPSMobileNumber);
//                            }
//
//                        }
//                        else if (_smsMessageForGPS.equals(_constants.SMS_ADMINIP)) {
//                            _LoaderDialog = new LoaderDialog(getActivity(),
//                                    MenuActivity._GlobalResource.getString(R.string.SMS_configuring_gps_via_sms),
//                                    MenuActivity._GlobalResource.getString(R.string.SMS_setting_automatic_location_updates));
//                            sendSMSMessage(_constants.SMS_TIMEINTERVAL, _GPSMobileNumber);
//                        }
//                        else if (_smsMessageForGPS.equals(_constants.SMS_TIMEINTERVAL)) {
//                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//                            alertDialogBuilder.setPositiveButton(MenuActivity._GlobalResource.getText(R.string.SMS_button_ok), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                }
//                            });
//                            alertDialogBuilder.setTitle(MenuActivity._GlobalResource.getString(R.string.dialog_status_success));
//                            alertDialogBuilder.setMessage(MenuActivity._GlobalResource.getString(R.string.SMS_successfully_added_GPS));
//                            alertDialogBuilder.show();
//
//                            _LoaderDialog.dismiss();
//
//                            //new asyncAddTraccarGPS(getApplicationContext(), _loaderDialog, MenuActivity.this, _addGPSDialogFragment).execute("SAMM_"+ _gpsIMEI.substring(_gpsIMEI.length()-5, _gpsIMEI.length()), _gpsIMEI, _GPSMobileNumber, _gpsNetwork, _gpsPlateNumber, _gpsTblRoutesID.toString(), _gpsTblUsersID.toString());
//                        }
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        if(_isGPSReconnect)
//                        {
//                            _ReconnectGPSButton.setEnabled(true);
//                            _ReconnectGPSButton.setText(MenuActivity._GlobalResource.getString(R.string.SMS_button_reconnect));
//                        }
//                        else
//                        {
//                            _LoaderDialog.dismiss();
//                        }
//                        Toast.makeText(context, MenuActivity._GlobalResource.getString(R.string.SMS_error_encountered_in_adding_GPS), Toast.LENGTH_SHORT).show();
//
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        if(_isGPSReconnect)
//                        {
//                            _ReconnectGPSButton.setEnabled(true);
//                            _ReconnectGPSButton.setText(MenuActivity._GlobalResource.getString(R.string.SMS_button_reconnect));
//                        }
//                        else
//                        {
//                            _LoaderDialog.dismiss();
//                        }
//                        Toast.makeText(context, MenuActivity._GlobalResource.getString(R.string.SMS_error_encountered_in_adding_GPS), Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        if(_isGPSReconnect)
//                        {
//                            _ReconnectGPSButton.setEnabled(true);
//                            _ReconnectGPSButton.setText(MenuActivity._GlobalResource.getString(R.string.SMS_button_reconnect));
//                        }
//                        else
//                        {
//                            _LoaderDialog.dismiss();
//                        }
//                        Toast.makeText(context, MenuActivity._GlobalResource.getString(R.string.SMS_error_encountered_in_adding_GPS), Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        if(_isGPSReconnect)
//                        {
//                            _ReconnectGPSButton.setEnabled(true);
//                            _ReconnectGPSButton.setText(MenuActivity._GlobalResource.getString(R.string.SMS_button_reconnect));
//                        }
//                        else
//                        {
//                            _LoaderDialog.dismiss();
//                        }
//                        Toast.makeText(context, MenuActivity._GlobalResource.getString(R.string.SMS_error_encountered_in_adding_GPS), Toast.LENGTH_SHORT).show();
//                        break;
//                }
//
//
//            }
//            catch(Exception ex)
//            {
//                Helper.logger(ex,true);
//
//            }
//
//        }
//    }
//    public class SMSDeliveredBroadcastReceiver extends BroadcastReceiver
//    {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            switch (getResultCode()) {
//                case Activity.RESULT_OK:
//                    Toast.makeText(getContext(), MenuActivity._GlobalResource.getString(R.string.SMS_status_delivered), Toast.LENGTH_SHORT).show();
//                    break;
//                case Activity.RESULT_CANCELED:
//                    Toast.makeText(getContext(), MenuActivity._GlobalResource.getString(R.string.SMS_status_not_delivered), Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    }
}

