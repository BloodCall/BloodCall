package gr.gdschua.bloodapp.Activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import gr.gdschua.bloodapp.R;
import gr.gdschua.bloodapp.Utils.NetworkChangeReceiver;

@SuppressWarnings("ALL")
public class LoginActivity extends AppCompatActivity {
    EditText passET;
    EditText emailET;
    ImageView showPasswordIV;
    boolean loginClicked = false;
    BroadcastReceiver broadcastReceiver = new NetworkChangeReceiver();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailET = findViewById(R.id.email_input);
        passET = findViewById(R.id.password_input);
        showPasswordIV = findViewById(R.id.showPassword);
        showPasswordIV.setTag(R.drawable.show_password_image);

        showPasswordIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((Integer) showPasswordIV.getTag() == R.drawable.show_password_image) {

                    passET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passET.setSelection(passET.getText().length());
                    showPasswordIV.setImageDrawable(ContextCompat.getDrawable(LoginActivity.this, R.drawable.hide_password_image));
                    showPasswordIV.setTag(R.drawable.hide_password_image);


                } else if ((Integer) showPasswordIV.getTag() == R.drawable.hide_password_image) {

                    passET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passET.setSelection(passET.getText().length());
                    showPasswordIV.setImageDrawable(ContextCompat.getDrawable(LoginActivity.this, R.drawable.show_password_image));
                    showPasswordIV.setTag(R.drawable.show_password_image);
                }
            }
        });

        findViewById(R.id.login_auth_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passET.getText().toString().trim();
                String email = emailET.getText().toString().trim();
                signIn(email, password);
            }
        });

        findViewById(R.id.forgetPasswordText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

    }


    private void signIn(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty() && !loginClicked) {
            loginClicked = true;
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        emailET.requestFocus();
                        loginClicked = false;
                        emailET.setError(getResources().getString(R.string.login_error));
                    }
                }
            });
        }
    }


}