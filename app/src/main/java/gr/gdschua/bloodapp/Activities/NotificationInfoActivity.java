package gr.gdschua.bloodapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import gr.gdschua.bloodapp.R;

public class NotificationInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_info);

        TextView dateTv = findViewById(R.id.DateTV);
        TextView hospNameTv = findViewById(R.id.HospNameTV);
        TextView addrTv = findViewById(R.id.AddrTV);

        Button navBtn = findViewById(R.id.navBtn);
        Button closeBtn = findViewById(R.id.closeBtn);

        Bundle launchIntent = getIntent().getExtras();

        dateTv.setText(launchIntent.getString("timestamp"));
        hospNameTv.setText(launchIntent.getString("hospname"));
        addrTv.setText(launchIntent.getString("hospaddr"));

        Double lat = launchIntent.getDouble("lat");
        Double lon = launchIntent.getDouble("lon");

        closeBtn.setOnClickListener(v -> finish());

        final MapView mapView = (MapView) findViewById(R.id.mapView);

        navBtn.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            }
        });

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(googleMap -> {
            LatLng coordinates = new LatLng(lat, lon);
            googleMap.addMarker(new MarkerOptions().position(coordinates));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
            googleMap.setOnMarkerClickListener(marker -> true);
            mapView.onResume();
        });
    }
}