package gr.gdschua.bloodapp.Utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

import gr.gdschua.bloodapp.Activities.MainActivity;
import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;

public class NotificationReceiverSvc extends FirebaseMessagingService {

    DAOHospitals daoHospitals = new DAOHospitals();
    DAOUsers daoUsers = new DAOUsers();
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("FCM", "Refreshed token: " + s);
    }


    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            daoHospitals.getUser(remoteMessage.getData().get("owner")).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()){
                        Hospital hospital = task.getResult().getValue(Hospital.class);
                        showNotification("Urgent need for "+remoteMessage.getData().get("bloodType") + " blood!"
                                , "Your blood is needed right now at "+hospital.getName()
                                ,remoteMessage.getData().get("bloodType"));
                    }
                }
            });
        }
    }


    private RemoteViews getCustomDesign(String title, String message) {
        RemoteViews remoteViews = new RemoteViews(
        getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon, R.drawable.ic_blood_drop);
        return remoteViews;
    }

    public void showNotification(String title, String message,String bloodTypeNeeded) {
        Intent intent = new Intent(this, MainActivity.class);
        String channel_id = "notification_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        daoUsers.getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getValue(User.class).getBloodType().equals(bloodTypeNeeded)){
                        NotificationCompat.Builder builder = new NotificationCompat
                                .Builder(getApplicationContext(), channel_id)
                                .setSmallIcon(R.drawable.ic_blood_drop)
                                .setAutoCancel(true)
                                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                .setOnlyAlertOnce(true)
                                .setContentIntent(pendingIntent);

                        builder = builder.setContent(getCustomDesign(title, message));
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "bloodcall", NotificationManager.IMPORTANCE_HIGH);
                            notificationManager.createNotificationChannel(notificationChannel);
                        }

                        notificationManager.notify(0, builder.build());
                    }
                }else{
                    Log.e("ERROR USER","Something went very wrong");
                }
            }
        });
    }
}
