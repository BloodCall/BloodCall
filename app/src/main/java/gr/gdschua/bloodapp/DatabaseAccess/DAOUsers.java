package gr.gdschua.bloodapp.DatabaseAccess;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import gr.gdschua.bloodapp.Entities.User;


public class DAOUsers {


    public DAOUsers() {
    }

    public Task<Void> insertUser(User newUser, Bitmap userImage, Context context) throws IOException {

        newUser.setId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        if (userImage != null) {
            File outputFile = File.createTempFile("profPic" + Math.random(), ".png", context.getCacheDir());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            userImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            FirebaseStorage.getInstance().getReference("UserImages").child(newUser.getId()).putFile(Uri.fromFile(outputFile));
            outputFile.delete();
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
        return FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).get();
    }

    public Task<DataSnapshot> getUser(String uID) {
        return FirebaseDatabase.getInstance().getReference().child("Users").child(uID).get();
    }

}
