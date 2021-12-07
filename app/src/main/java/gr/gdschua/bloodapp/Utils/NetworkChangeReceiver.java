package gr.gdschua.bloodapp.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        try{
            if(isOnline(context)){
                Toast.makeText(context,"Online",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context,"Offline",Toast.LENGTH_LONG).show();
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
