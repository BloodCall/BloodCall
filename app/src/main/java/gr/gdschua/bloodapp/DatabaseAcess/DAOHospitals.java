package gr.gdschua.bloodapp.DatabaseAcess;


import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

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


}
