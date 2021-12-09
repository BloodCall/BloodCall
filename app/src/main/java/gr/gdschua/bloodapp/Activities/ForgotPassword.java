package gr.gdschua.bloodapp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import gr.gdschua.bloodapp.R;

public class ForgotPassword extends AppCompatActivity {

    EditText emailET;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        emailET = findViewById(R.id.forgotPassEmail);


        findViewById(R.id.resetPassButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailET.getText().toString().trim());
            }
        });
    }
}
