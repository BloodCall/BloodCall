package gr.gdschua.bloodapp.DatabaseAcess;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gr.gdschua.bloodapp.Entities.User;


public class DAOUsers {

    private DatabaseReference databaseReference;

    public DAOUsers() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        this.databaseReference = db.getReference().child("Users");
    }

    public Task<Void> insertUser(User newUser){

        if(newUser==null) {
            //throw exception
        }

       return databaseReference.push().setValue(newUser);
    }
}
