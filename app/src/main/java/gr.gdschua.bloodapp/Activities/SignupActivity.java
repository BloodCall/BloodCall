package gr.gdschua.bloodapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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


import gr.gdschua.bloodapp.DatabaseAcess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;

public class SignupActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

    Spinner bloodTypeSpinner;
    Spinner posNegSpinner;
    de.hdodenhof.circleimageview.CircleImageView profilePicButton;
    Button registerButton;
    EditText fName;
    EditText lName;
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
        profilePicButton=findViewById(R.id.profile_picture_button);
        registerButton=findViewById(R.id.register_button_details);
        fName=findViewById(R.id.first_name_box);
        lName=findViewById(R.id.last_name_box);
        email=findViewById(R.id.email_signup_box);
        password=findViewById(R.id.password_signup_box);
        progressBar=findViewById(R.id.progressBar);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


        profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                try {

                    i.putExtra("return-data", true);
                    startActivityForResult(
                            Intent.createChooser(i, "Select Picture"), 0);
                }catch (ActivityNotFoundException ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode == Activity.RESULT_OK){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                profilePicButton.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
        //user creation works
        //real time database does not work
        mAuth.createUserWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            User newUser = new User(fullName,userEmail,bloodType);
                                daoUser.insertUser(newUser).addOnSuccessListener(suc->{
                                    Toast.makeText(SignupActivity.this,"Succesfully registered",Toast.LENGTH_LONG).show();
                                }).addOnFailureListener(fail->{
                                    Toast.makeText(SignupActivity.this,"Failed to register "+fail.getMessage(),Toast.LENGTH_LONG).show();
                                });

                                Intent goToLogin = new Intent(SignupActivity.this,LoginActivity.class);
                                startActivity(goToLogin);
                        }else{
                            Toast.makeText(SignupActivity.this,"Failed to register",Toast.LENGTH_LONG).show();
                            Log.w("error", "signInWithCustomToken:failure", task.getException());
                        }
                    }
                });
        progressBar.setVisibility(View.GONE);
    }
}