package gr.gdschua.bloodapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import gr.gdschua.bloodapp.R;

    public class HospitalAuthActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_hospital_auth);

            findViewById(R.id.HospSignupBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HospitalAuthActivity.this, HospitalSignUpActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            findViewById(R.id.HospLoginBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

