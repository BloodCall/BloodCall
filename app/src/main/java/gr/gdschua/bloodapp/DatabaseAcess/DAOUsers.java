package gr.gdschua.bloodapp.DatabaseAcess;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;

import gr.gdschua.bloodapp.Entities.User;



public class DAOUsers {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    public DAOUsers() {
    }

    public Task<Void> insertUser(User newUser, Uri userImage){

       FirebaseStorage.getInstance().getReference("UserImages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).putFile(userImage);
       return FirebaseDatabase.getInstance().getReference("Users")
               .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
               .setValue(newUser);
    }
}
