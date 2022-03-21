package gr.gdschua.bloodapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Objects;

import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.BitmapResizer;

@SuppressWarnings("ALL")
public class SignupActivity extends AppCompatActivity {


    private final int PICK_IMAGE_REQUEST = 0;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Spinner bloodTypeSpinner;
    Spinner posNegSpinner;
    de.hdodenhof.circleimageview.CircleImageView profilePicButton;
    Button registerButton;
    EditText fName;
    EditText lName;
    Bitmap profilePicture;
    EditText email;
    ProgressBar progressBar;
    EditText password;
    final DAOUsers daoUser = new DAOUsers();
    final ActivityResultLauncher<Intent> launchGalleryActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() != Activity.RESULT_OK) {
                        return;
                    }
                    Intent data = result.getData();
                    try {
                        profilePicture = BitmapResizer.processBitmap(Objects.requireNonNull(data).getData(), 400, SignupActivity.this);
                        profilePicButton.setImageBitmap(profilePicture);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        bloodTypeSpinner = findViewById(R.id.bloodtype_spinner);
        posNegSpinner = findViewById(R.id.bloodtype_spinner_pos_neg);
        profilePicButton = findViewById(R.id.profilePic);
        registerButton = findViewById(R.id.hospSignupBtn);
        fName = findViewById(R.id.hospSignupName);
        lName = findViewById(R.id.hospSignupAddr);
        email = findViewById(R.id.hospSignupEmail);
        password = findViewById(R.id.hospSignupPass);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


        profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK).setType("image/*").putExtra("outputX", 240).putExtra("outputY", 240).putExtra("aspectX", 1).putExtra("aspectY", 1).putExtra("scale", true);
                launchGalleryActivity.launch(intent);
            }
        });

    }

    private void registerUser() {
        String fullName = fName.getText().toString().trim() + " " + lName.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String bloodType = bloodTypeSpinner.getSelectedItem().toString().trim() + posNegSpinner.getSelectedItem().toString().trim();

        if (fName.getText().toString().trim().isEmpty()) {
            fName.setError(getResources().getString(R.string.fname_error));
            fName.requestFocus();
            return;
        }

        if (lName.getText().toString().trim().isEmpty()) {
            lName.setError(getResources().getString(R.string.lname_error));
            lName.requestFocus();
            return;
        }

        if (userEmail.isEmpty()) {
            email.setError(getResources().getString(R.string.email_req_error));
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError(getResources().getString(R.string.email_inv_error));
            email.requestFocus();
            return;
        }

        if (userPassword.isEmpty()) {
            password.setError(getResources().getString(R.string.pass_req_error));
            password.requestFocus();
            return;
        }

        if (userPassword.length() < 6) {
            password.setError(getResources().getString(R.string.pass_inv_error));
            password.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User newUser = new User(fullName, userEmail, bloodType);
                            newUser.setNotifications(true);
                            try {
                                daoUser.insertUser(newUser, profilePicture, getApplicationContext()).addOnSuccessListener(suc -> {
                                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.succ_reg), Toast.LENGTH_LONG).show();
                                    Intent goToHome = new Intent(SignupActivity.this, MainActivity.class);
                                    FirebaseMessaging.getInstance().subscribeToTopic(newUser.getTopic()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            startActivity(goToHome);
                                            finish();
                                        }
                                    });
                                }).addOnFailureListener(fail -> {
                                    Toast.makeText(SignupActivity.this, getResources().getString(R.string.fail_reg) + fail.getMessage(), Toast.LENGTH_LONG).show();
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, getResources().getString(R.string.fail_reg), Toast.LENGTH_LONG).show();
                            Log.w("error", "signInWithCustomToken:failure", task.getException());
                        }
                    }
                });

    }


}