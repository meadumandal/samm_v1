package com.umandalmead.samm_v1;

/**
 * Created by MeadRoseAnn on 12/3/2017.
 */
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = "Mead";
    public FirebaseDatabase firebaseDatabase;
    public DatabaseReference destinationDatabaseReference;
    public static final String ACTION_MyIntentService = "com.example.samm_v1.RESPONSE";
    public static final String EXTRA_KEY_IN = "EXTRA_IN";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    public static final String KEY_EVENT_TYPE = "EVENT_TYPE";
    public static final String KEY_GEOFENCEREQUESTID = "GEOFENCEREQUESTID";
    String extraIn;
    String eventType;
    String geofenceRequestId;

    private static final String SUPER = GeofenceTransitionsIntentService.class.getSimpleName();

    public GeofenceTransitionsIntentService()  {
        super("GeofenceTransitionsIntentService");
        Log.i(TAG, "GeofenceTransitionIntentService");
        if(firebaseDatabase == null && destinationDatabaseReference ==null)
        {
            firebaseDatabase = FirebaseDatabase.getInstance();
            destinationDatabaseReference = firebaseDatabase.getReference("destinations");
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "OnHandleIntent");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Goefencing Error " + geofencingEvent.getErrorCode());
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.i(TAG, "Entered");
            eventType = "Entered";
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i(TAG, "Exit");
            eventType = "Exit";
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Log.i(TAG, "Dwell");
            eventType = "Dwell";
        } else {
            Log.i(TAG, "Error");
        }
        try
        {
//            if(!eventType.toLowerCase().equals("entered"))
//            {
                geofenceRequestId = geofencingEvent.getTriggeringGeofences().get(0).getRequestId().toString();

                //extraOut = "Hey it's working!";
                Intent intentResponse = new Intent();
                intentResponse.setAction(ACTION_MyIntentService);
                intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                intentResponse.putExtra(KEY_EVENT_TYPE, eventType);
                intentResponse.putExtra(KEY_GEOFENCEREQUESTID, geofenceRequestId);
                sendBroadcast(intentResponse);
//            }

        }
        catch(Exception e)
        {

        }

    }

    public void showNotification(String text, String bigText) {
        // 1. Create a NotificationManager
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // 2. Create a PendingIntent for AllGeofencesActivity
        Intent intent = new Intent(this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 3. Create and send a notification
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(text)
                .setContentText(text)
                .setContentIntent(pendingNotificationIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(0, notification);
    }

}