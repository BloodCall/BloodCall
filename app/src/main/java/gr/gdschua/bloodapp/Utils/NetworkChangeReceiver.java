package gr.gdschua.bloodapp.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import gr.gdschua.bloodapp.Activities.LauncherActivity;
import gr.gdschua.bloodapp.Activities.NoInternetActivity;

public class NetworkChangeReceiver extends BroadcastReceiver {

    Boolean triggered;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        try{
            if(isOnline(context) && triggered){
                NoInternetActivity.FinishTask();
            }else{
                triggered=true;
                Intent launchIntent=new Intent(context, NoInternetActivity.class);
                context.startActivity(launchIntent);
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    public boolean isOnline(Context context){
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return (networkInfo!=null && networkInfo.isConnected());
        }catch(NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }
}
