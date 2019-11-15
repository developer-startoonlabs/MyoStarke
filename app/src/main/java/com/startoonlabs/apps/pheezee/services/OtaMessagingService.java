package com.startoonlabs.apps.pheezee.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.activities.OnStartActivity;
import com.startoonlabs.apps.pheezee.activities.PatientsView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

public class OtaMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "1";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();

        Map map = remoteMessage.getData();
        editor.putString("firmware_update",Objects.requireNonNull(map.get("downloadlink")).toString());
        editor.apply();
        showNotifications(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody(),Objects.requireNonNull(map.get("downloadlink")).toString(), this);
    }


    public void showNotifications(String app, String message,String downloadLink, Context context){
            Intent intent = new Intent(context, PatientsView.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            Log.i("downloadLink1", downloadLink);
            intent.putExtra("downloadLink", downloadLink);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.pheezee_logos_final_square_round)
                    .setContentTitle(app)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//                    .setAutoCancel(true)
//                    .setContentIntent(pendingIntent);
            createNotificationChannel(context);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify('1', builder.build());
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
