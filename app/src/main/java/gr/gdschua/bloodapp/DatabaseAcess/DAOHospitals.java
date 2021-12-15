package gr.gdschua.bloodapp.DatabaseAcess;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import gr.gdschua.bloodapp.Entities.Hospital;

public class DAOHospitals {

    public DAOHospitals() {
    }

    public Task<Void> insertUser(Hospital newUser){

        return FirebaseDatabase.getInstance().getReference("Hospitals")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(newUser);
    }



    public Task<DataSnapshot> getUser(){
        return FirebaseDatabase.getInstance().getReference().child("Hospitals").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get();
    }



    //Maybe make static
    //Maybe in the future add a parameter to limit how many hospitals it should return or the general location?
    public ArrayList<Hospital> getAllHospitals(){

        ArrayList<Hospital> allHospitals = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Hospitals");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot

                        Map<String,Object>  hospitalsMap = (Map<String, Object>) dataSnapshot.getValue();



                        int i = 0; //for debug
                        //iterate through each user, ignoring their UID
                        for (Map.Entry<String, Object> entry : hospitalsMap.entrySet()){

                            //Get user map
                            Map  singleHospital =  (Map)entry.getValue();
                            //Get phone field and append to list
                            allHospitals.add(mapToHospital(singleHospital));

                            Log.i("DEBUG", allHospitals.get(i).getName());
                            i++;
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        return allHospitals;
    }

    public static Hospital mapToHospital(Map singleHospital){
        return new Hospital(singleHospital.get("name").toString(),singleHospital.get("email").toString(),(double)singleHospital.get("lat"),(double)singleHospital.get("lon"));
    }

}
