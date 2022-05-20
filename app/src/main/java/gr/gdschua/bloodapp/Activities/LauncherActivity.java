package gr.gdschua.bloodapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import gr.gdschua.bloodapp.Activities.HospitalActivities.HospitalSignUpActivity;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.NetworkChangeReceiver;

public class LauncherActivity extends AppCompatActivity {


    final BroadcastReceiver broadcastReceiver = new NetworkChangeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);


        findViewById(R.id.login_button).setOnClickListener(v -> {
            Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.signup_button).setOnClickListener(v -> {
            Intent intent = new Intent(LauncherActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.hospital_text).setOnClickListener(v -> {
            Intent intent = new Intent(LauncherActivity.this, HospitalSignUpActivity.class);
            startActivity(intent);
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetwork();
    }


    protected void unregisterNetwork() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


}