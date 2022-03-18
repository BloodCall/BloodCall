package gr.gdschua.bloodapp.Activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.IOException;

import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.LevelHandler;
import gr.gdschua.bloodapp.Utils.QrEncoder;

public class HomeFragment extends Fragment {

    DAOUsers Udao = new DAOUsers();
    private Boolean reqResult=false;
    DAOHospitals Hdao = new DAOHospitals();
    TextView bloodTypeTV;
    TextView fullNameTextView;
    TextView emailTextView;
    StorageReference mStorageReference;
    User currUser;
    Boolean showPermsDialog=true;
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

    final ActivityResultLauncher<String> bgLocationRequest = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result-> {
        reqResult=result;
        showPermsDialog=!reqResult;
        pushN.setChecked(reqResult);
        currUser.setNotificationsB(reqResult);
        if(!currUser.notifFirstTime){
            currUser.setXp(currUser.getXp()+10);
            currUser.notifFirstTime=true;
            Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.notif_first_time, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        Udao.updateUser(currUser);
    });
    final ActivityResultLauncher<String> locationRequest = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            bgLocationRequest.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        else {
            reqResult = result;
            pushN.setChecked(reqResult);
            showPermsDialog=!reqResult;
            currUser.setNotificationsB(reqResult);
            if(!currUser.notifFirstTime){
                currUser.setXp(currUser.getXp()+10);
                currUser.notifFirstTime=true;
                Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.notif_first_time, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            Udao.updateUser(currUser);
        }
    });



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bloodTypeTV = view.findViewById(R.id.bloodTypeTextView);
        QrEncoder qrEncoder= new QrEncoder(requireContext());
        fullNameTextView = view.findViewById(R.id.hosp_fullNameTextView);
        ProgressBar progressBar= view.findViewById(R.id.lvlProgressBar);
        TextView lvlTV= view.findViewById(R.id.lvlTV);
        TextView nextLvlTV= view.findViewById(R.id.nextLvlTV);
        ImageView qrIV =  view.findViewById(R.id.qrImageView);
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
                        if (!reqResult) {
                            handleBgLoc();
                        }
                        FirebaseMessaging.getInstance().subscribeToTopic(currUser.getTopic());
                    }
                    try {
                        qrIV.setImageBitmap(qrEncoder.encodeAsBitmap("BLCL:"+currUser.getId()));
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    bloodTypeTV.setText(currUser.getBloodType());
                    fullNameTextView.setText(currUser.getFullName());
                    //kitsaros gia to mail
                    emailTextView.setText(currUser.getEmail());
                    mStorageReference = FirebaseStorage.getInstance().getReference().child("UserImages/" + FirebaseAuth.getInstance().getUid());
                    progressBar.setProgress(LevelHandler.getLvlCompletionPercentage(currUser.getXp(),LevelHandler.getLevel(currUser.getXp())),true);
                    ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress()).setDuration(1000).start();
                    lvlTV.setText(getResources().getString(R.string.curr_lvl_text,LevelHandler.getLevel(currUser.getXp())));
                    nextLvlTV.setText(getResources().getString(R.string.new_lvl_xp,(LevelHandler.getLvlXpCap(LevelHandler.getLevel(currUser.getXp()))-currUser.getXp()),LevelHandler.getLevel(currUser.getXp())+1));
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
                            handleBgLoc();
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

    private void handleBgLoc(){
        if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) && showPermsDialog){
            new AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
                    .setTitle(R.string.bg_loc_title)
                    .setMessage(R.string.bg_loc_text)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            locationRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            pushN.setChecked(false);
                            dialog.dismiss();
                            showPermsDialog=true;
                        }
                    }).show();
        }
        else {
            reqResult=true;
            currUser.setNotificationsB(true);
            Udao.updateUser(currUser);
        }
        showPermsDialog=false;
    }
}