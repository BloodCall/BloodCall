package gr.gdschua.bloodapp.DatabaseAccess;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import gr.gdschua.bloodapp.Entities.Hospital;

public class DAOHospitals {
    final Map<String, Object> hospitalsMap = new HashMap<>();

    public DAOHospitals() {
        populateHospitalMap();
    }

    public static Hospital mapToHospital(Map singleHospital) {
        return new Hospital(Objects.requireNonNull(singleHospital.get("name")).toString(), Objects.requireNonNull(singleHospital.get("email")).toString(),
                (double) singleHospital.get("lat"), (double) singleHospital.get("lon"), Objects.requireNonNull(singleHospital.get("address")).toString() ,
                (List<String>) singleHospital.get("accepts"));
    }

    public Task<Void> insertUser(Hospital newUser) {

        newUser.setId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        return FirebaseDatabase.getInstance().getReference("Hospitals")
                .child(newUser.getId())
                .setValue(newUser);
    }

    public Task<DataSnapshot> getUser() {
        return FirebaseDatabase.getInstance().getReference().child("Hospitals").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).get();
    }

    public Task<DataSnapshot> getUser(String ID) {
        return FirebaseDatabase.getInstance().getReference().child("Hospitals").child(ID).get();
    }

    private void populateHospitalMap() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Hospitals");
        ref.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot


                        //hospitalsMap = (Map<String, Object>) dataSnapshot.getValue();
                        if (dataSnapshot.exists()) {
                            hospitalsMap.putAll((Map<String, Object>) Objects.requireNonNull(dataSnapshot.getValue()));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

    //Maybe make static
    //Maybe in the future add a parameter to limit how many hospitals it should return or the general location?
    public ArrayList<Hospital> getAllHospitals() {


        ArrayList<Hospital> allHospitalsList = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : hospitalsMap.entrySet()) {

            //Get user map
            Map singleHospital = (Map) entry.getValue();
            //Get phone field and append to list
            allHospitalsList.add(mapToHospital(singleHospital));
        }
        return allHospitalsList;
    }

    public Task<Void> updateUser(Hospital hospital) {


        return FirebaseDatabase.getInstance().getReference("Hospitals")
                .child(hospital.getId())
                .updateChildren(hospital.getAsMap());
    }

}