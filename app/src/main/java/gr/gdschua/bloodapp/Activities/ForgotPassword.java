package gr.gdschua.bloodapp.Activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


        findViewById(R.id.resetPassButton).setOnClickListener(v -> {
            String email = emailET.getText().toString().trim();

            if (email.isEmpty()) {
                emailET.setError(getResources().getString(R.string.email_req_error));
                emailET.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailET.setError(getResources().getString(R.string.email_inv_error));
                emailET.requestFocus();
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.res_email_succ), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.res_email_err), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
