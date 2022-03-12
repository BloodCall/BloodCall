package gr.gdschua.bloodapp.Utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import gr.gdschua.bloodapp.Activities.NotificationInfoActivity;
import gr.gdschua.bloodapp.Entities.Alert;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.R;

public class NotificationReceiverSvc extends FirebaseMessagingService {

    Gson gson = new Gson();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("FCM", "Refreshed token: " + s);
    }


    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        Alert targetAlert = gson.fromJson(remoteMessage.getData().get("alert"), Alert.class);
        Hospital targetHospital = gson.fromJson(remoteMessage.getData().get("hospital"), Hospital.class);
        showNotification(targetHospital, targetAlert);
    }


    public PendingIntent buildIntent(Hospital hospital, Alert alert) {
        Intent intent = new Intent(this, NotificationInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle b = new Bundle();
        b.putDouble("lon", hospital.getLon());
        b.putDouble("lat", hospital.getLat());
        b.putString("timestamp", alert.getDateCreated());
        b.putString("hospname", hospital.getName());
        b.putString("hospaddr", hospital.getAddress(this));
        intent.putExtras(b);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, alert.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, alert.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        return pendingIntent;
    }


    public void showNotification(Hospital hospital, Alert alert) {
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(getApplicationContext(), "emerg_notif_chan")
                .setAutoCancel(true)
                .setContentText(String.format(getString(R.string.notif_text_template), hospital.getName())) //just to cover all android versions/oem skins
                .setSmallIcon(R.drawable.ic_blood_drop)
                .setContentTitle(getResources().getString(R.string.notif_title))
                .setContentIntent(buildIntent(hospital, alert))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(String.format(getString(R.string.notif_text_template), hospital.getName())));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("emerg_notif_chan", getResources().getString(R.string.notif_channel_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(alert.getId(), builder.build());
    }


}