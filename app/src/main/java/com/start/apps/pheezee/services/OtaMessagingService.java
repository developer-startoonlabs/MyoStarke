package com.start.apps.pheezee.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import start.apps.pheezee.R;
import com.start.apps.pheezee.activities.PatientsView;
import com.start.apps.pheezee.repository.MqttSyncRepository;

import org.json.JSONException;
import org.json.JSONObject;

import static com.start.apps.pheezee.activities.MonitorActivity.IS_SESSION_SCEDULED_ON;

public class OtaMessagingService extends FirebaseMessagingService {
    public static final String CHANNEL_ID = "1";
    SharedPreferences preferences;
    MqttSyncRepository repository;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        repository = new MqttSyncRepository(getApplication());
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn",false);
        if(isLoggedIn) {
                try {
                    JSONObject object_remote_message = new JSONObject(remoteMessage.getData());
                    if (object_remote_message.has("type") && object_remote_message.getString("type").equalsIgnoreCase("1")) {
                        if(object_remote_message.has("patientid") && !IS_SESSION_SCEDULED_ON.equalsIgnoreCase(remoteMessage.getData().get("patientid")) ) {
                            showNotifications(object_remote_message.getString("title"), remoteMessage.getData().get("patientid"), object_remote_message.getString("patientname"), this);
                            insetTheSceduledSessionDetailsInBackground(object_remote_message);
                        }else {
                            repository.sceduledSessionNotSaved(object_remote_message.getString("patientid"));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

    }

    private void insetTheSceduledSessionDetailsInBackground(JSONObject object) {
        repository.insetTheSceduledSessionDetails(object);
    }


    public void showNotifications(String app, String patientid, String patientname, Context context){
        String  message = "Session Sceduled for "+patientname+" with id "+patientid;
        Intent i = new Intent(context, PatientsView.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                i, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.pheezee_logos_final_square_round)
                .setContentTitle(app)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(intent)
                .setAutoCancel(true);

        createNotificationChannel(context);
        NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        try {
            notificationManager.notify(Integer.parseInt(patientid), builder.build());
        }catch(NumberFormatException e){
            notificationManager.notify(0, builder.build());
        }
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
