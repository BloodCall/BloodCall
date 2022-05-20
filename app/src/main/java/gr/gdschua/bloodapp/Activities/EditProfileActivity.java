package gr.gdschua.bloodapp.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.BitmapResizer;

public class EditProfileActivity extends AppCompatActivity {

    de.hdodenhof.circleimageview.CircleImageView profilePic;
    Bitmap profilePictureBitmap;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        profilePic = findViewById(R.id.profilePic);
        EditText fName = findViewById(R.id.hospSignupName);
        EditText lName = findViewById(R.id.hospSignupAddr);
        EditText email = findViewById(R.id.hospSignupEmail);
        Spinner bloodTypeSpinner = findViewById(R.id.bloodtype_spinner);
        Spinner posNegSpinner = findViewById(R.id.bloodtype_spinner_pos_neg);
        Button updateBtn = findViewById(R.id.updateBtn);
        Button changePwBtn = findViewById(R.id.changePasswordBtn);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final ActivityResultLauncher<Intent> launchGalleryActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK) {
                        return;
                    }
                    Intent data = result.getData();
                    try {
                        profilePictureBitmap = BitmapResizer.processBitmap(Objects.requireNonNull(data).getData(), 400, getApplicationContext());
                        profilePic.setImageBitmap(profilePictureBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        new DAOUsers().getUser().addOnSuccessListener(dataSnapshot -> {
            User currUser = dataSnapshot.getValue(User.class);
            String[] nameSpl = currUser.getFullName().split("\\s+");
            fName.setText(nameSpl[0]);
            lName.setText(nameSpl[1]);
            email.setText(currUser.getEmail());
            String[] bldSpl = currUser.getBloodType().split("((?=[+-])|(?<=[+-]))");
            bloodTypeSpinner.setSelection(((ArrayAdapter<String>)bloodTypeSpinner.getAdapter()).getPosition(bldSpl[0]));
            posNegSpinner.setSelection(((ArrayAdapter<String>)posNegSpinner.getAdapter()).getPosition(bldSpl[1]));


            profilePic.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK).setType("image/*").putExtra("outputX", 240).putExtra("outputY", 240).putExtra("aspectX", 1).putExtra("aspectY", 1).putExtra("scale", true);
                launchGalleryActivity.launch(intent);
            });

            updateBtn.setOnClickListener(v -> {

                Thread t = new Thread(() -> {
                    FirebaseMessaging.getInstance().subscribeToTopic(currUser.getTopic());
                    currUser.updateSelf();
                });
                if (!email.getText().toString().equals(currUser.getEmail())) {
                    FirebaseAuth.getInstance().getCurrentUser().updateEmail(email.getText().toString());
                }
                FirebaseMessaging.getInstance().unsubscribeFromTopic(currUser.getTopic());
                currUser.setEmail(email.getText().toString());
                currUser.setFullName(fName.getText() + " " + lName.getText());
                currUser.setBloodType(bloodTypeSpinner.getSelectedItem().toString().trim() + posNegSpinner.getSelectedItem().toString().trim());
                t.start();
                if (profilePictureBitmap != null) {
                    File outputFile = null;
                    try {
                        outputFile = File.createTempFile("profPic" + Math.random(), ".png", getCacheDir());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    profilePictureBitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(outputFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FirebaseStorage.getInstance().getReference("UserImages").child(currUser.getId()).putFile(Uri.fromFile(outputFile));
                }
                finish();
            });
        });

        findViewById(R.id.changePasswordBtn).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),ChangePasswordActivity.class);
            startActivity(intent);
        });


        try {
            File localFile = File.createTempFile(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()), "jpg");
            FirebaseStorage.getInstance().getReference().child("UserImages/" + FirebaseAuth.getInstance().getUid()).getFile(localFile).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    profilePic.setImageBitmap(bitmap);
                    localFile.delete();
                } else {
                    Log.e("ERROR", "IMAGE NOT FOUND!");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}