package gr.gdschua.bloodapp.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import gr.gdschua.bloodapp.DatabaseAcess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;

public class HomeFragment extends Fragment {

    DAOUsers dao = new DAOUsers();
    TextView bloodTypeTV;
    TextView fullNameTextView;
    StorageReference mStorageReference;
    User currUser;
    de.hdodenhof.circleimageview.CircleImageView profilePicture;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bloodTypeTV = view.findViewById(R.id.bloodTypeTextView);
        fullNameTextView = view.findViewById(R.id.fullNameTextView);
        profilePicture = view.findViewById(R.id.profilePic);
        //this is how you retrieve data yo

        dao.getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    currUser = task.getResult().getValue(User.class);
                    bloodTypeTV.setText(currUser.getBloodType());
                    fullNameTextView.setText(currUser.getFullName());
                }else {
                    Toast.makeText(view.getContext(), "Failed to retrieve user info.", Toast.LENGTH_LONG).show();
                    Log.e("ERROR", "COULD NOT RETRIEVE USER INFO!");
                }
            }
        });

        mStorageReference = FirebaseStorage.getInstance().getReference().child("UserImages/" + FirebaseAuth.getInstance().getUid());


        try {
                final File localFile = File.createTempFile(FirebaseAuth.getInstance().getUid(), "jpg");


                mStorageReference.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            profilePicture.setImageBitmap(bitmap);
                        }else{
                            //need a way to load the default picture here

                            Log.e("ERROR","IMAGE NOT FOUND!");
                            //Toast.makeText(view.getContext(), "HERE NEED TO LOAD DEFAULT PIC?", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();

            }



        return view;
    }

}