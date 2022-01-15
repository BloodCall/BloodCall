package gr.gdschua.bloodapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.NetworkChangeReceiver;
import gr.gdschua.bloodapp.databinding.ActivityMainHospitalBinding;
import gr.gdschua.bloodapp.databinding.ActivityMainUserBinding;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    BroadcastReceiver broadcastReceiver = new NetworkChangeReceiver();
    DAOUsers Udao = new DAOUsers();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetwork();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Udao.getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                        if(task.getResult().getValue()==null){
                            InflateHospital();
                        }
                        else {
                            InflateUser();
                        }
                }
            }
        });


        IntentFilter filter=new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver,filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController=null;
        if(findViewById(R.id.nav_host_fragment_content_user)==null){
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_hosp);
        }else{
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user);
        }
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    protected void unregisterNetwork(){
        try{
            unregisterReceiver(broadcastReceiver);
        }catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }


    protected void InflateHospital(){
        ActivityMainHospitalBinding binding = ActivityMainHospitalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainHosp.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_hospital_home,R.id.nav_hosp_map,R.id.nav_hosp_addEvent,R.id.nav_hosp_alert)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_hosp);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                if (id==R.id.sign_out){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent=new Intent(MainActivity.this,LauncherActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.signed_out), Toast.LENGTH_SHORT).show();
                }
                NavigationUI.onNavDestinationSelected(menuItem,navController);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    protected void InflateUser(){
        ActivityMainUserBinding binding = ActivityMainUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainUser.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_map)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_user);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                if (id==R.id.sign_out){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent=new Intent(MainActivity.this,LauncherActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.signed_out), Toast.LENGTH_SHORT).show();
                }
                NavigationUI.onNavDestinationSelected(menuItem,navController);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

}

