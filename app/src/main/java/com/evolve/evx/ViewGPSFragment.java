package com.evolve.evx;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.evolve.evx.EntityObjects.GPS;
import com.evolve.evx.Modules.GPS.AddGPSDialogFragment;
import com.evolve.evx.Modules.GPS.GPSListViewCustomAdapter;
import com.evolve.evx.Modules.GPS.asyncGetGPSFromTraccar;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

import static com.evolve.evx.Constants.LOG_TAG;
import static com.evolve.evx.Constants.MY_PERMISSIONS_REQUEST_SEND_SMS;


public class ViewGPSFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String TAG = "mead";
    private String mParam1;
    private String mParam2;
    public ImageButton FAB_SammIcon;
    private  TextView ViewTitle;
    public static SwipeRefreshLayout _swipeRefreshGPS;
    public FloatingActionButton floatingActionButton_addGPS;
    public static AddGPSDialogFragment _addGPSDialogFragment;
    View myView;
    ArrayList<GPS> dataModels;
    public static LoaderDialog _LoaderDialog;
    private Constants _constants = new Constants();
    public NonScrollListView _gpsListview;

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
    public GifImageView _spinnerGif;
    public Boolean _isGPSReconnect = false;


    private SentSMSBroadcastReceiver _smsSentBroadcastReceiver;
    private SMSDeliveredBroadcastReceiver _smsDeliveredBroadcastReceiver;
    private PendingIntent _sentSMSPendingIntent;
    private PendingIntent _deliveredSMSPendingIntent;

    private static GPSListViewCustomAdapter adapter;

    public static ViewGPSFragment _viewGPSFragment;

    Activity _activity;

    public ViewGPSFragment() {


    }


    public static ViewGPSFragment newInstance(String param1, String param2) {
        ViewGPSFragment fragment = new ViewGPSFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        _activity = MenuActivity._activity;
        _addGPSDialogFragment = new AddGPSDialogFragment();
        _LoaderDialog = new LoaderDialog(getActivity(),null,null);
        _LoaderDialog.show();
        _LoaderDialog.dismiss();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_view_gps, container, false);
        try
        {
            InitializeToolbar("GPS Devices");
            _gpsListview = (NonScrollListView) myView.findViewById(R.id.gpslistview);
            _swipeRefreshGPS = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_refresh_gps);
            floatingActionButton_addGPS = myView.findViewById(R.id.floatingActionButton_addGPS);
            refreshGPSListView();


            floatingActionButton_addGPS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddGPSDialogFragment addGPSDialogFragment = new AddGPSDialogFragment();
                    addGPSDialogFragment.show(getFragmentManager() ,"AddGPSDialogFragment");
                }
            });
            _smsSentBroadcastReceiver = new SentSMSBroadcastReceiver();
            _smsDeliveredBroadcastReceiver = new SMSDeliveredBroadcastReceiver();

            String SMS_SENT = MenuActivity._GlobalResource.getString(R.string.SMS_SENT);
            String SMS_DELIVERED = MenuActivity._GlobalResource.getString(R.string.SMS_DELIVERED);

            _sentSMSPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SMS_SENT), 0);
            _deliveredSMSPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SMS_DELIVERED), 0);



            getActivity().registerReceiver(_smsSentBroadcastReceiver, new IntentFilter(SMS_SENT));
            getActivity().registerReceiver(_smsDeliveredBroadcastReceiver, new IntentFilter(SMS_DELIVERED));


        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }

        _viewGPSFragment = ViewGPSFragment.this;


        return myView;
    }

    public void refreshGPSListView()
    {
        _swipeRefreshGPS.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _swipeRefreshGPS.setRefreshing(true);
                FragmentManager fm = getActivity().getFragmentManager();
                new asyncGetGPSFromTraccar(getActivity(), _LoaderDialog, _gpsListview, fm, _swipeRefreshGPS, ViewGPSFragment.this).execute();
            }
        });
        _swipeRefreshGPS.post(new Runnable() {
            @Override
            public void run() {
                _swipeRefreshGPS.setRefreshing(true);
                FragmentManager fm = getActivity().getFragmentManager();
                new asyncGetGPSFromTraccar(getActivity(), _LoaderDialog, _gpsListview, fm, _swipeRefreshGPS, ViewGPSFragment.this).execute();
            }
        });
    }

    public void InitializeToolbar(String fragmentName){
        try {
            FAB_SammIcon = (ImageButton) myView.findViewById(R.id.SAMMLogoFAB);
            FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DrawerLayout drawerLayout = (DrawerLayout) ((MenuActivity) getActivity()).findViewById(R.id.drawer_layout);
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
            ViewTitle = (TextView) myView.findViewById(R.id.samm_toolbar_title);
            ViewTitle.setTypeface(MenuActivity.FONT_ROBOTO_CONDENDSED_BOLD);
            ViewTitle.setText(fragmentName);
        }catch (Exception ex){
            Helper.logger(ex);
        }
    }
    public void sendSMSMessage(String message, String phone) {
        try
        {
            this._smsMessageForGPS = message;
            this._GPSMobileNumber = phone;
            Log.i(LOG_TAG, MenuActivity._GlobalResource.getString(R.string.SMS_sending_to_add_new_gps));
            _smsMessageForGPS = message;
            if (ContextCompat.checkSelfPermission(MenuActivity._activity,android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                MenuActivity._GPSMobileNumber = phone;
                MenuActivity._smsMessageForGPS = message;
                MenuActivity._sentSMSPendingIntent = _sentSMSPendingIntent;
                MenuActivity._deliveredSMSPendingIntent = _deliveredSMSPendingIntent;
                Log.i(_constants.LOG_TAG,MenuActivity._GlobalResource.getString(R.string.SMS_status_sending_with_extra_white_space) + this._smsMessageForGPS + MenuActivity._GlobalResource.getString(R.string.ellipsis));
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
            else
            {
                Log.i(_constants.LOG_TAG,MenuActivity._GlobalResource.getString(R.string.SMS_status_sending_with_extra_white_space)  + this._smsMessageForGPS + MenuActivity._GlobalResource.getString(R.string.ellipsis));
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, this._smsMessageForGPS, _sentSMSPendingIntent, _deliveredSMSPendingIntent);
                Log.i(_constants.LOG_TAG, message + MenuActivity._GlobalResource.getString(R.string.SMS_status_sent_with_extra_white_space_prefix));
            }
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);

            _LoaderDialog.dismiss();
            Toast.makeText(getContext(),MenuActivity._GlobalResource.getString(R.string.error_exception_with_concat) + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void sendSMSMessage(String message, String phone, Button btnReconnectGPS, GifImageView spinnerGif) {
        try
        {
            Log.i(LOG_TAG, MenuActivity._GlobalResource.getString(R.string.SMS_sending_to_reconnect_gps));
            this._smsMessageForGPS = message;
            this._GPSMobileNumber = phone;



            this._ReconnectGPSButton = btnReconnectGPS;
            this._spinnerGif = spinnerGif;
            if (ContextCompat.checkSelfPermission(MenuActivity._activity,android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                MenuActivity._GPSMobileNumber = phone;
                MenuActivity._smsMessageForGPS = message;
                MenuActivity._sentSMSPendingIntent = _sentSMSPendingIntent;
                MenuActivity._deliveredSMSPendingIntent = _deliveredSMSPendingIntent;

                Log.i(_constants.LOG_TAG,MenuActivity._GlobalResource.getString(R.string.SMS_status_sending_with_extra_white_space)+ this._smsMessageForGPS + MenuActivity._GlobalResource.getString(R.string.ellipsis));
//                    Toast.makeText(getApplicationContext(), "sending " + this._smsMessageForGPS, Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
            else
            {
                Log.i(_constants.LOG_TAG,MenuActivity._GlobalResource.getString(R.string.SMS_status_sending_with_extra_white_space) + this._smsMessageForGPS + MenuActivity._GlobalResource.getString(R.string.ellipsis));
//                Toast.makeText(getApplicationContext(), "sending " + this._smsMessageForGPS, Toast.LENGTH_LONG).show();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, this._smsMessageForGPS, _sentSMSPendingIntent, _deliveredSMSPendingIntent);


                Log.i(_constants.LOG_TAG, message + MenuActivity._GlobalResource.getString(R.string.SMS_status_sent_with_extra_white_space_prefix));
//                Toast.makeText(getApplicationContext(),this._smsMessageForGPS + " sent", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
            _LoaderDialog.dismiss();
            Toast.makeText(getContext(),MenuActivity._GlobalResource.getString(R.string.error_exception_with_concat) + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public class SentSMSBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            try
            {
                ErrorDialog errorDialog;
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        if (_smsMessageForGPS.equals(_constants.SMS_BEGIN)) {

                            sendSMSMessage(_constants.SMS_GPRS, _GPSMobileNumber);
                        }
                        else if (_smsMessageForGPS.equals(_constants.SMS_GPRS)) {
//                            if(_isGPSReconnect) {
//                                _ReconnectGPSButton.setText(MenuActivity._GlobalResource.getString(R.string.SMS_button_reconnect));
//                                _ReconnectGPSButton.setEnabled(true);
//                            }
//                            else {


                                sendSMSMessage(_smsAPN, _GPSMobileNumber);
//                            }
                        }
                        else if (_smsMessageForGPS.equals(_smsAPN)) {
//                            if(_isGPSReconnect) {
//                                sendSMSMessage(_constants.SMS_GPRS, _GPSMobileNumber);
//                            }
//                            else {

                                sendSMSMessage(_constants.SMS_ADMINIP, _GPSMobileNumber);
//                            }

                        }
                        else if (_smsMessageForGPS.equals(_constants.SMS_ADMINIP)) {

                            sendSMSMessage(_constants.SMS_TIMEINTERVAL, _GPSMobileNumber);
                        }
                        else if (_smsMessageForGPS.equals(_constants.SMS_TIMEINTERVAL)) {
                            if (_isGPSReconnect)
                            {
                                _ReconnectGPSButton.setVisibility(View.VISIBLE);
                                _spinnerGif.setVisibility(View.GONE);
                                Toast.makeText(MenuActivity._context, "Done. Please wait for few minutes for the device to become online", Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                InfoDialog infoDialog = new InfoDialog(_activity, MenuActivity._GlobalResource.getString(R.string.SMS_successfully_added_GPS));
                                infoDialog.show();
                                _LoaderDialog.dismiss();
                            }



                            //new asyncAddTraccarGPS(getApplicationContext(), _loaderDialog, MenuActivity.this, _addGPSDialogFragment).execute("SAMM_"+ _gpsIMEI.substring(_gpsIMEI.length()-5, _gpsIMEI.length()), _gpsIMEI, _GPSMobileNumber, _gpsNetwork, _gpsPlateNumber, _gpsTblRoutesID.toString(), _gpsTblUsersID.toString());
                        }
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        if(_isGPSReconnect)
                        {
                            _ReconnectGPSButton.setVisibility(View.VISIBLE);
                            _spinnerGif.setVisibility(View.GONE);
                        }
                        else
                        {
                            _LoaderDialog.dismiss();
                        }
                        errorDialog = new ErrorDialog(_activity, MenuActivity._GlobalResource.getString(R.string.SMS_error_encountered_in_adding_GPS));
                        errorDialog.show();

                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        if(_isGPSReconnect)
                        {
                            _ReconnectGPSButton.setVisibility(View.VISIBLE);
                            _spinnerGif.setVisibility(View.GONE);
                        }
                        else
                        {
                            _LoaderDialog.dismiss();
                        }
                        errorDialog = new ErrorDialog(_activity, MenuActivity._GlobalResource.getString(R.string.SMS_No_Service));
                        errorDialog.show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        if(_isGPSReconnect)
                        {
                            _ReconnectGPSButton.setVisibility(View.VISIBLE);
                            _spinnerGif.setVisibility(View.GONE);
                        }
                        else
                        {
                            _LoaderDialog.dismiss();
                        }
                        errorDialog = new ErrorDialog(_activity, MenuActivity._GlobalResource.getString(R.string.SMS_error_encountered_in_adding_GPS));
                        errorDialog.show();
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        if(_isGPSReconnect)
                        {
                            _ReconnectGPSButton.setVisibility(View.VISIBLE);
                            _spinnerGif.setVisibility(View.GONE);
                        }
                        else
                        {
                            _LoaderDialog.dismiss();
                        }
                        errorDialog = new ErrorDialog(_activity, MenuActivity._GlobalResource.getString(R.string.SMS_error_encountered_in_adding_GPS));
                        errorDialog.show();
                        break;
                }


            }
            catch(Exception ex)
            {
                Helper.logger(ex,true);

            }

        }
    }
    public class SMSDeliveredBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getContext(), MenuActivity._GlobalResource.getString(R.string.SMS_status_delivered), Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getContext(), MenuActivity._GlobalResource.getString(R.string.SMS_status_not_delivered), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
