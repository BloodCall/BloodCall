package gr.gdschua.bloodapp.Utils;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
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

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        //not sure if this if actually works
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                Location userLocation = task.getResult();
                LatLng userLatLng = new LatLng(userLocation.getLatitude(),userLocation.getLongitude());
                LatLng hospitalLatLng = new LatLng(targetHospital.getLat(),targetHospital.getLon());
                if(calculateDistance(userLatLng,hospitalLatLng)<=20){
                    showNotification(targetHospital, targetAlert);
                }
            });
            return;
        }
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

    //Haversine formula
    private int calculateDistance(LatLng userLocation , LatLng hospitalLocation){

            double earthRadius = 6371;
            double latDistance = Math.toRadians(userLocation.latitude - hospitalLocation.latitude);
            double lngDistance = Math.toRadians(userLocation.longitude - hospitalLocation.longitude);

            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(userLocation.latitude)) * Math.cos(Math.toRadians(hospitalLocation.latitude))
                    * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return (int) (Math.round(earthRadius * c));
    }

}