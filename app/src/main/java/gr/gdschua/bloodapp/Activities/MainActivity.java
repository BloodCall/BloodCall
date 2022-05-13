package gr.gdschua.bloodapp.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.NetworkChangeReceiver;
import gr.gdschua.bloodapp.databinding.ActivityMainHospitalBinding;
import gr.gdschua.bloodapp.databinding.ActivityMainUserBinding;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHECK_SETTINGS = 500;
    private static final long LOCATION_UPDATE_INTERVAL = 1000;
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL = 500;

    final BroadcastReceiver broadcastReceiver = new NetworkChangeReceiver();
    final DAOUsers Udao = new DAOUsers();
    User currUser;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetwork();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Udao.getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    currUser = task.getResult().getValue(User.class);
                    if (task.getResult().getValue() == null) {
                        InflateHospital();
                    } else {
                        InflateUser();
                    }
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);
        enableLocationSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_licences) {
            startActivity(new Intent(this, OssLicensesMenuActivity.class));
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.settings_toolbar));
            return true;
        }
        if (item.getItemId() == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.about));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController;
        if (findViewById(R.id.nav_host_fragment_content_user) == null) {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_hosp);
        } else {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user);
        }
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    protected void unregisterNetwork() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    protected void InflateHospital() {
        ActivityMainHospitalBinding binding = ActivityMainHospitalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_hosp);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.



    }

    protected void InflateUser() {
        ActivityMainUserBinding binding = ActivityMainUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_user);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation2);
        NavigationUI.setupWithNavController(bottomNav, navController);

    }

    protected void enableLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(LOCATION_UPDATE_INTERVAL)
                .setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        LocationServices
                .getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(this, (LocationSettingsResponse response) -> {
                    // startUpdatingLocation(...);
                })
                .addOnFailureListener(this, ex -> {
                    if (ex instanceof ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(MainActivity.this, REQUEST_CODE_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_CHECK_SETTINGS == requestCode) {
            if (Activity.RESULT_OK != resultCode) {
                new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                        .setTitle(R.string.enable_location_title)
                        .setMessage(R.string.enable_location_text)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                enableLocationSettings();
                            }
                        }).show().setCancelable(false);
            }
        }
    }
}

