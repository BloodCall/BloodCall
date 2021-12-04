package gr.gdschua.bloodapp.DatabaseAcess;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gr.gdschua.bloodapp.Activities.SignupActivity;
import gr.gdschua.bloodapp.Entities.User;


public class DAOUsers {


    public DAOUsers() {
    }

    public Task<Void> insertUser(User newUser){

        if(newUser==null) {
            Log.e("FirebaseUser","Did not receive user info");
        }

       return FirebaseDatabase.getInstance().getReference("Users")
               .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
               .setValue(newUser);
    }
}
