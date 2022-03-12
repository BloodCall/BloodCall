package gr.gdschua.bloodapp.DatabaseAccess;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import gr.gdschua.bloodapp.Entities.User;


public class DAOUsers {


    public DAOUsers() {
    }

    public Task<Void> insertUser(User newUser, Uri userImage) {

        newUser.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (userImage != null) {
            FirebaseStorage.getInstance().getReference("UserImages").child(newUser.getId()).putFile(userImage);
        }


        return FirebaseDatabase.getInstance().getReference("Users")
                .child(newUser.getId())
                .setValue(newUser);
    }

    public Task<Void> updateUser(User newUser) {


        return FirebaseDatabase.getInstance().getReference("Users")
                .child(newUser.getId())
                .updateChildren(newUser.getAsMap());
    }


    public Task<DataSnapshot> getUser() {
        return FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).get();
    }

}
