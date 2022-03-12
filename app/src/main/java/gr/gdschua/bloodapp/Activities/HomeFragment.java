package gr.gdschua.bloodapp.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;

public class HomeFragment extends Fragment {

    DAOUsers Udao = new DAOUsers();
    DAOHospitals Hdao = new DAOHospitals();
    TextView bloodTypeTV;
    TextView fullNameTextView;
    TextView emailTextView;
    StorageReference mStorageReference;
    User currUser;
    de.hdodenhof.circleimageview.CircleImageView profilePicture;
    SwitchMaterial pushN;

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
        fullNameTextView = view.findViewById(R.id.hosp_fullNameTextView);
        pushN = view.findViewById(R.id.pushNotifSwitch);
        //Kitsaros gia to email.
        emailTextView = view.findViewById(R.id.hosp_emailTextView);
        profilePicture = view.findViewById(R.id.profilePic);

        Udao.getUser().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    currUser = task.getResult().getValue(User.class);
                    pushN.setChecked(currUser.getNotificationsB());
                    if (currUser.getNotificationsB()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(currUser.getTopic());
                    }
                    bloodTypeTV.setText(currUser.getBloodType());
                    fullNameTextView.setText(currUser.getFullName());
                    //kitsaros gia to mail
                    emailTextView.setText(currUser.getEmail());
                    mStorageReference = FirebaseStorage.getInstance().getReference().child("UserImages/" + FirebaseAuth.getInstance().getUid());
                    try {
                        File localFile = File.createTempFile(FirebaseAuth.getInstance().getUid(), "jpg");


                        mStorageReference.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    profilePicture.setImageBitmap(bitmap);
                                    localFile.delete();
                                } else {
                                    Log.e("ERROR", "IMAGE NOT FOUND!");
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        pushN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseMessaging.getInstance().subscribeToTopic(currUser.getTopic()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            currUser.setNotificationsB(true);
                            Udao.updateUser(currUser);
                        }
                    });
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(currUser.getTopic()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            currUser.setNotificationsB(false);
                            Udao.updateUser(currUser);
                        }
                    });
                }
            }
        });


        return view;
    }

}