package gr.gdschua.bloodapp.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import gr.gdschua.bloodapp.Activities.NoInternetActivity;

public class NetworkChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, final Intent intent) {
        try {
            if (isOnline(context)) {
                Intent local = new Intent();
                local.setAction("gr.gdschua.bloodapp.online");
                context.sendBroadcast(local);

            } else {
                Intent launchIntent = new Intent(context, NoInternetActivity.class);
                context.startActivity(launchIntent);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
