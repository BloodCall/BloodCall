package gr.gdschua.bloodapp.DatabaseAccess;


import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gr.gdschua.bloodapp.Entities.Hospital;

public class DAOHospitals {
    Map<String,Object> hospitalsMap = new HashMap<>();

    public DAOHospitals() {
        populateHospitalMap();
    }

    public Task<Void> insertUser(Hospital newUser){

        return FirebaseDatabase.getInstance().getReference("Hospitals")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(newUser);
    }



    public Task<DataSnapshot> getUser(){
        return FirebaseDatabase.getInstance().getReference().child("Hospitals").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get();
    }

    public Task<DataSnapshot> getUser(String ID){
        return FirebaseDatabase.getInstance().getReference().child("Hospitals").child(ID).get();
    }



    private void populateHospitalMap(){


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Hospitals");
        ref.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot



                        //hospitalsMap = (Map<String, Object>) dataSnapshot.getValue();
                        if(dataSnapshot.exists()) {
                            hospitalsMap.putAll((Map<String, Object>) dataSnapshot.getValue());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

    //Maybe make static
    //Maybe in the future add a parameter to limit how many hospitals it should return or the general location?
    public ArrayList<Hospital> getAllHospitals(){


        ArrayList<Hospital> allHospitalsList = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : hospitalsMap.entrySet()){

            //Get user map
            Map  singleHospital =  (Map)entry.getValue();
            //Get phone field and append to list
            allHospitalsList.add(mapToHospital(singleHospital));
        }
        return allHospitalsList;
    }
    public static Hospital mapToHospital(Map singleHospital){
        return new Hospital(singleHospital.get("name").toString(),singleHospital.get("email").toString(),(double)singleHospital.get("lat"),(double)singleHospital.get("lon"));
    }

}