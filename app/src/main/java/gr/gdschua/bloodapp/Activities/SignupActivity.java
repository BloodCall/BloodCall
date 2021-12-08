package gr.gdschua.bloodapp.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import java.io.File;

import gr.gdschua.bloodapp.DatabaseAcess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.BitmapResizer;
import gr.gdschua.bloodapp.Utils.CacheClearer;

public class SignupActivity extends AppCompatActivity {


    Spinner bloodTypeSpinner;
    Spinner posNegSpinner;
    de.hdodenhof.circleimageview.CircleImageView profilePicButton;
    Button registerButton;
    Button backButton;
    EditText fName;
    EditText lName;
    private final int PICK_IMAGE_REQUEST = 0;
    Uri profilePicture;
    EditText email;
    ProgressBar progressBar;
    EditText password;
    DAOUsers daoUser = new DAOUsers();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.setTitle("Your Details");

        bloodTypeSpinner=findViewById(R.id.bloodtype_spinner);
        posNegSpinner=findViewById(R.id.bloodtype_spinner_pos_neg);
        profilePicButton=findViewById(R.id.profilePic);
        registerButton=findViewById(R.id.register_button_details);
        backButton = findViewById(R.id.back_button_details);
        fName=findViewById(R.id.first_name_box);
        lName=findViewById(R.id.last_name_box);
        email=findViewById(R.id.email_signup_box);
        password=findViewById(R.id.password_signup_box);
        progressBar=findViewById(R.id.progressBar);


        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this,LauncherActivity.class );
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


        profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK).setType("image/*").putExtra("outputX", 240).putExtra("outputY", 240).putExtra("aspectX", 1).putExtra("aspectY",1).putExtra("scale",true);
                launchGalleryActivity.launch(intent);
            }
        });
    }


    ActivityResultLauncher<Intent> launchGalleryActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() != Activity.RESULT_OK) {
                        return;
                    }
                    Intent data = result.getData();
                    try {
                        profilePicture=BitmapResizer.processBitmap(data.getData(),400,SignupActivity.this,profilePicButton);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


    private void registerUser(){
        String fullName = fName.getText().toString().trim() + " " + lName.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String bloodType = bloodTypeSpinner.getSelectedItem().toString().trim() + posNegSpinner.getSelectedItem().toString().trim();

        if(fName.getText().toString().trim().isEmpty()){
            fName.setError("Name is required");
            fName.requestFocus();
            return;
        }

        if(lName.getText().toString().trim().isEmpty()){
            lName.setError("Last name is required");
            lName.requestFocus();
            return;
        }

        if(userEmail.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            email.setError("Please provide a valid email address");
            email.requestFocus();
            return;
        }

        if(userPassword.isEmpty()){
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        if (userPassword.length()<6){
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            User newUser = new User(fullName,userEmail,bloodType);
                            daoUser.insertUser(newUser,profilePicture).addOnSuccessListener(suc->{
                                Toast.makeText(SignupActivity.this,"Succesfully registered",Toast.LENGTH_LONG).show();
                            }).addOnFailureListener(fail->{
                                Toast.makeText(SignupActivity.this,"Failed to register "+fail.getMessage(),Toast.LENGTH_LONG).show();
                            });
                            CacheClearer.deleteCache(SignupActivity.this);
                            Intent goToLogin = new Intent(SignupActivity.this,LoginActivity.class);
                            startActivity(goToLogin);
                            finish();
                        }else{
                            Toast.makeText(SignupActivity.this,"Failed to register",Toast.LENGTH_LONG).show();
                            Log.w("error", "signInWithCustomToken:failure", task.getException());
                        }
                    }
                });
        progressBar.setVisibility(View.GONE);
    }
}