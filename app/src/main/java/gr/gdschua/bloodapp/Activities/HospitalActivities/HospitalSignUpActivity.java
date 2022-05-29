package gr.gdschua.bloodapp.Activities.HospitalActivities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Locale;

import gr.gdschua.bloodapp.Activities.MainActivity;
import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;
import gr.gdschua.bloodapp.Entities.Hospital;
import gr.gdschua.bloodapp.R;

public class HospitalSignUpActivity extends AppCompatActivity {

    private final DAOHospitals dao = new DAOHospitals();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_sign_up);
        double lat, lon;

        findViewById(R.id.hospSignupBtn).setOnClickListener(v -> {
            EditText addr = findViewById(R.id.hospSignupAddr);
            EditText name = findViewById(R.id.hospSignupName);
            EditText email = findViewById(R.id.hospSignupEmail);
            EditText password = findViewById(R.id.hospSignupPass);


            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(task -> {
                        boolean successful = true;
                        if (task.isSuccessful()) {

                            Geocoder geocoder = new Geocoder(HospitalSignUpActivity.this, Locale.getDefault());
                            try {
                                List<Address> latLonList = geocoder.getFromLocationName(addr.getText().toString(), 1);
                                Hospital newUser = new Hospital(name.getText().toString(), email.getText().toString(), latLonList.get(0).getLatitude(), latLonList.get(0).getLongitude(), addr.getText().toString(), null);

                                dao.insertUser(newUser).addOnSuccessListener(suc -> {
                                    Toast.makeText(HospitalSignUpActivity.this, getResources().getString(R.string.succ_reg), Toast.LENGTH_LONG).show();
                                }).addOnFailureListener(fail -> {
                                    Toast.makeText(HospitalSignUpActivity.this, getResources().getString(R.string.fail_reg) + fail.getMessage(), Toast.LENGTH_LONG).show();
                                });

                                Intent goToLogin = new Intent(HospitalSignUpActivity.this, MainActivity.class);
                                startActivity(goToLogin);
                                finish();
                            } catch (Exception e) {
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                currentUser.delete();
                                Toast.makeText(HospitalSignUpActivity.this, getString(R.string.hosp_s_u_addr_err), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(HospitalSignUpActivity.this, getResources().getString(R.string.fail_reg), Toast.LENGTH_LONG).show();
                            Log.w("error", "signInWithCustomToken:failure", task.getException());
                        }
                    });

        });
    }

}
