package gr.gdschua.bloodapp.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import gr.gdschua.bloodapp.Activities.HospitalActivities.HospitalSettingsActivity;
import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.Hospital;
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
    Hospital currHosp;
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
        Udao.getUser().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currUser = task.getResult().getValue(User.class);
                if (task.getResult().getValue() == null) {
                    InflateHospital();
                } else {
                    InflateUser();
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);
        enableLocationSettings();
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

        DAOHospitals daoHosp = new DAOHospitals();

        daoHosp.getUser().addOnCompleteListener(task -> {
            currHosp = task.getResult().getValue(Hospital.class);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Set<String> don_types = preferences.getStringSet("donation_type", new HashSet<>(Arrays.asList("0", "1", "2")));
            currHosp.setAccepts(new ArrayList<>(don_types));
            currHosp.updateSelf();
        });



        findViewById(R.id.settings_btn_hosp).setOnClickListener(v -> {
            Intent moveToSettings = new Intent(getApplicationContext(), HospitalSettingsActivity.class);
            startActivity(moveToSettings);
        });

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

        findViewById(R.id.settings_btn_user).setOnClickListener(v -> {
            Intent moveToSettings = new Intent(getApplicationContext(), UserSettingsActivity.class);
            startActivity(moveToSettings);
        });

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
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> enableLocationSettings()).show().setCancelable(false);
            }
        }
    }
}

