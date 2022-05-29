package gr.gdschua.bloodapp.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gr.gdschua.bloodapp.DatabaseAccess.DAOEvents;
import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.Entities.Event;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.MyDialogCloseListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapsFragment extends Fragment implements MyDialogCloseListener {

    private Context thisContext;
    private static final String TAG = "MapsFragment";
    private final DAOHospitals daoHospitals = new DAOHospitals();
    private final DAOEvents daoEvents = new DAOEvents();
    final GoogleMap.OnMarkerClickListener MarkerClickListener = marker -> {
        MarkerInfoFragment myMarkerInfoFragment = new MarkerInfoFragment();
        if (Objects.equals(marker.getSnippet(), "Hospital")) {
            Hospital hospital = (Hospital) marker.getTag();

            Bundle bundle = new Bundle();
            assert hospital != null;
            ArrayList<String> accepts = new ArrayList<>(hospital.getAccepts());

            bundle.putString("name", hospital.getName());
            bundle.putString("address", hospital.getAddress());
            bundle.putString("email", hospital.getEmail());
            bundle.putDouble("lat", hospital.getLat());
            bundle.putDouble("lon", hospital.getLon());
            bundle.putStringArrayList("accepts",accepts);
            myMarkerInfoFragment.setArguments(bundle);
            myMarkerInfoFragment.show(requireActivity().getSupportFragmentManager(), "My Fragment");

        } else if (Objects.equals(marker.getSnippet(), "Event")) {
            Event event = (Event) marker.getTag();

            Bundle bundle = new Bundle();
            assert event != null;
            bundle.putString("name", event.getName());
            bundle.putString("date", event.getDate());
            bundle.putString("address", event.getAddress(getActivity()));
            bundle.putDouble("lat", event.getLat());
            bundle.putDouble("lon", event.getLon());
            daoHospitals.getUser(event.getOwner()).addOnCompleteListener(task -> {
                Hospital ownerHosp = task.getResult().getValue(Hospital.class);
                assert ownerHosp != null;
                bundle.putString("email", ownerHosp.getEmail());
                bundle.putString("organizer", ownerHosp.getName());
                myMarkerInfoFragment.setArguments(bundle);
                myMarkerInfoFragment.show(requireActivity().getSupportFragmentManager(), "My Fragment");
            });
            return true;

        }

        return true;
    };
    private GoogleMap map;
    private final ActivityResultLauncher<String> locationRequest = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            handleLocation();
        } else {
            Toast.makeText(thisContext, getString(R.string.maps_loc_err), Toast.LENGTH_LONG).show();
        }
    });
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            map = googleMap;
            if (ContextCompat.checkSelfPermission(thisContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                handleLocation();
            }

            googleMap.setOnMarkerClickListener(MarkerClickListener);

            MapDialogFragment mapDialogFragment = new MapDialogFragment();
            mapDialogFragment.show(requireActivity().getSupportFragmentManager(), "My Fragment");
            MyDialogCloseListener closeListener = dialog -> placeMarkers();

            mapDialogFragment.DismissListener(closeListener);

            FloatingActionButton floatingActionButton = getView().findViewById(R.id.emergenciesButton);

            floatingActionButton.setOnClickListener(view1 -> {
                CurrentEmergenciesFragment currentEmergenciesFragment =  CurrentEmergenciesFragment.newInstance();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(getParentFragment().getId(),currentEmergenciesFragment).addToBackStack(null).commit();


            });
        }
    };

    @SuppressLint("MissingPermission")
    private void handleLocation() {
        map.setMyLocationEnabled(true);
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(thisContext);
        mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
            //Move the camera to the user's location and zoom in!
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
        });
    }

    private void placeMarkers() {
        ArrayList<Event> events = daoEvents.getAllEvents();
        ArrayList<Hospital> hospitals = daoHospitals.getAllHospitals();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String regularBlood = preferences.getString("regular","null");
        String platelets = preferences.getString("platelets","null");
        String plasma = preferences.getString("plasma","null");

        if (events.size() > 0) {
            for (int i = 0; i < events.size(); i++) {
                LatLng eventLatLong = new LatLng(events.get(i).getLat(), events.get(i).getLon());
                Objects.requireNonNull(map.addMarker(new MarkerOptions().position(eventLatLong).title(events.get(i).getName()).snippet("Event").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))).setTag(events.get(i));
            }
        }
        if (hospitals.size() > 0) {
            for (int i = 0; i < hospitals.size(); i++) {
                LatLng hospitalLatLong = new LatLng(hospitals.get(i).getLat(), hospitals.get(i).getLon());
                List<String> accepts = hospitals.get(i).getAccepts();

                if( accepts != null){

                    for(int j=0; j< accepts.size();j++){

                        if(accepts.get(j).equals(regularBlood) || accepts.get(j).equals(platelets) || accepts.get(j).equals(plasma) ){
                            Objects.requireNonNull(map.addMarker(new MarkerOptions().position(hospitalLatLong).title(hospitals.get(i).getName()).snippet("Hospital")))
                                    .setTag(hospitals.get(i));
                        }

                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_maps, container, false);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // here the part where I get my selected date from the saved variable in the intent and the displaying it.
                Bundle bundle = data.getExtras();
                String resultDate = bundle.getString("selectedDate", "error");
                Toast.makeText(getContext(), "COCK " + resultDate, Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {

    }
}