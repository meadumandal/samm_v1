package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class AddGPSFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    Button sendBtn;
    EditText txtphoneNo;
    EditText txtIMEI;
    Spinner networkProvider;
    String phoneNo;
    String GPSIMEI;
    String apn;
    View myView;

    public String _message;
    android.os.Handler mHandler;
    public static String TAG ="mead";
    public ProgressDialog progDialog;
    PendingIntent sentPendingIntent;
    PendingIntent deliveredPendingIntent;
    public HashMap<String, Boolean> smsCommandsStatus = new HashMap<>();
//    MyBroadcastReceiver _broadcastReceiver;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_add_gps);
        myView = inflater.inflate(R.layout.fragment_add_gps, container, false);
        networkProvider = (Spinner) myView.findViewById(R.id.spinnerNetworkProviders);

        ArrayList<String> networkProviders = new ArrayList<>();
        networkProviders.add("Select GSM SIM Network Provicer");
        networkProviders.add("Globe");
        networkProviders.add("Smart");


        ArrayAdapter<String> networkProvidersAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, networkProviders){
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
                    tv.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
                }
                return view;
            }

        };
        networkProvider.setAdapter(networkProvidersAdapter);





        sendBtn = (Button) myView.findViewById(R.id.btnAddGPS);
        txtphoneNo = (EditText) myView.findViewById(R.id.GPSMobileNum);
        txtIMEI  = (EditText) myView.findViewById(R.id.GPSIMEI);
        progDialog = new ProgressDialog(getActivity());
        progDialog.setTitle("Adding GPS");
        progDialog.setMessage("Initializing...");
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(false);


        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

        sentPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SMS_SENT), 0);
        deliveredPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SMS_DELIVERED), 0);

        // For when the SMS has been sent
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try
                {

                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
//                            addGPSActivityContext.smsCommandsStatus.put(addGPSActivityContext._message, true);
                            if (_message.equals("begin123456")) {
                                progDialog.setMessage("Activating GPRS");
                                sendSMSMessage("gprs123456");
                            }
                            else if (_message.equals("gprs123456")) {
                                progDialog.setMessage("Setting APN");
                                sendSMSMessage("apn123456 " + apn);
                            }
                            else if (_message.equals("apn123456 " + apn)) {
                                progDialog.setMessage("Configuring IP and Port");
                                sendSMSMessage("adminip123456 server.traccar.org 5002");
                            }
                            else if (_message.equals("adminip123456 server.traccar.org 5002")) {
                                progDialog.setMessage("Setting automatic location updates");
                                sendSMSMessage("t005s***n123456");
                            }
                            else if (_message.equals("t005s***n123456")) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                                alertDialogBuilder.setTitle("Success");
                                alertDialogBuilder.setMessage("Successfully added GPS! It might take up to 10minutes before the GPS appears on the map.");
//                                alertDialogBuilder.show();


                                new addGPStoTraccar(getActivity(), progDialog).execute("SAMM_"+GPSIMEI.substring(GPSIMEI.length()-5, GPSIMEI.length()), GPSIMEI);
//                                progDialog.dismiss();
//                                Toast.makeText(context, "Successfully added GPS! It might take up to 10minutes before the GPS appears on the map.", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            progDialog.dismiss();
                            Toast.makeText(context, "Error encountered in adding GPS: Please check your signal", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            progDialog.dismiss();
                            Toast.makeText(context, "Error encountered in adding GPS: Please check your signal", Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            progDialog.dismiss();
                            Toast.makeText(context, "Error encountered in adding GPS: Please check your signal", Toast.LENGTH_SHORT).show();
                            break;

                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            progDialog.dismiss();
                            Toast.makeText(context, "Error encountered in adding GPS: Please check your signal", Toast.LENGTH_SHORT).show();
                            break;
                    }


                }
                catch(Exception ex)
                {
                    Log.e(TAG, ex.getMessage());
                }

            }
        }, new IntentFilter(SMS_SENT));

//        _broadcastReceiver = new MyBroadcastReceiver();
//
//        //register BroadcastReceiver
//        IntentFilter intentFilter = new IntentFilter(GeofenceTransitionsIntentService.ACTION_MyIntentService);
//        IntentFilter intentFilter = new IntentFilter(addGPStoTraccar.class());
//        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
//
//        getActivity().registerReceiver(_broadcastReceiver, intentFilter);

// For when the SMS has been delivered
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));


        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                phoneNo = txtphoneNo.getText().toString();
                GPSIMEI = txtIMEI.getText().toString();
                if(phoneNo.trim().length() == 0 || GPSIMEI.trim().length() == 0 || networkProvider.getSelectedItem().toString().equals("Select GSM SIM Network Provicer"))
                {
                    Toast.makeText(getContext(), "Please supply all fields", Toast.LENGTH_LONG).show();
                }
                else {
                    smsCommandsStatus.put("begin123456", false);
                    smsCommandsStatus.put("gprs123456", false);
                    if (networkProvider.getSelectedItem().toString().equals("Globe"))
                        apn = "http.globe.com.ph";
                    else
                        apn = "internet";
                    smsCommandsStatus.put("apn123456 " + apn, false);
                    smsCommandsStatus.put("adminip123456 server.traccar.org 5002", false);
                    smsCommandsStatus.put("t005s***n123456", false);

                    //Configure thru SMS
                    progDialog.show();
                    sendSMSMessage("begin123456");
                }
            }
        });
//        mHandler = new android.os.Handler(Looper.getMainLooper())
//        {
//            @Override
//            public void handleMessage(Message message) {
//                // This is where you do your work in the UI thread.
//                // Your worker tells you in the message what to do.
//
//            }
//        };
//        void workerThread(){
//            // And this is how you call it from the worker thread:
//            new addGPStoTraccar(getActivity(), progDialog).execute("SAMM_"+GPSIMEI.substring(GPSIMEI.length()-5, GPSIMEI.length()), GPSIMEI);
//            Message message = mHandler.obtainMessage(command, parameter);
//            message.sendToTarget();
//        };



        return myView;
    }
    public void showToast(@NonNull String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
    public void sendSMSMessage(String message) {
        try
        {
            this._message = message;


            if (ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.SEND_SMS)) {

                } else {

                    Log.i(TAG,"sending " + this._message);
//                    Toast.makeText(getApplicationContext(), "sending " + this._message, Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            }
            else
            {
                Log.i(TAG,"sending " + this._message);
//                Toast.makeText(getApplicationContext(), "sending " + this._message, Toast.LENGTH_LONG).show();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, this._message, sentPendingIntent, deliveredPendingIntent);

                Log.i(TAG, message + " sent");
//                Toast.makeText(getApplicationContext(),this._message + " sent", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, this._message, sentPendingIntent, deliveredPendingIntent);

                    Log.i(TAG, this._message + " sent");
                    Toast.makeText(getContext(), this._message + " sent", Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "SMS failed, please try again.");
                    Toast.makeText(getContext(), "SMS Failed", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }
//    public class MyBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String eventType = intent.getStringExtra(GeofenceTransitionsIntentService.KEY_EVENT_TYPE);
//            String geofenceRequestId = intent.getStringExtra(GeofenceTransitionsIntentService.KEY_GEOFENCEREQUESTID);
//            for(Destination d: _listDestinations)
//            {
//                if (d.GeofenceId.equals(geofenceRequestId))
//                {
//                    passengerMovement(d.Value, eventType);
//                    Toast.makeText(context, "You " + eventType + " " +  d.Description, Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }
//
//    }



}


