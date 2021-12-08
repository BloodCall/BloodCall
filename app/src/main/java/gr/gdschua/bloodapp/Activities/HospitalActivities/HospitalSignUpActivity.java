package gr.gdschua.bloodapp.Activities.HospitalActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import gr.gdschua.bloodapp.DatabaseAcess.DAOHospitals;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.R;

    public class HospitalSignUpActivity extends AppCompatActivity {

        private DAOHospitals dao=new DAOHospitals();
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_hospital_sign_up);

            findViewById(R.id.HospSignUpBtn2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText addr=findViewById(R.id.HospSignupAddr);
                    EditText name=findViewById(R.id.hospSignupName);
                    EditText email=findViewById(R.id.HospSignupEmail);
                    EditText password=findViewById(R.id.HospSignupPass);

                    mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){

                                        Hospital newUser= new Hospital(name.getText().toString(),addr.getText().toString(),email.getText().toString());
                                        dao.insertUser(newUser).addOnSuccessListener(suc->{
                                            Toast.makeText(HospitalSignUpActivity.this,"Succesfully registered",Toast.LENGTH_LONG).show();
                                        }).addOnFailureListener(fail->{
                                            Toast.makeText(HospitalSignUpActivity.this,"Failed to register "+fail.getMessage(),Toast.LENGTH_LONG).show();
                                        });
                                        Intent goToLogin = new Intent(HospitalSignUpActivity.this, HospitalAuthActivity.class);
                                        startActivity(goToLogin);
                                        finish();
                                    }else{
                                        Toast.makeText(HospitalSignUpActivity.this,"Failed to register",Toast.LENGTH_LONG).show();
                                        Log.w("error", "signInWithCustomToken:failure", task.getException());
                                    }
                                }
                            });
                }
            });
        }

    }
