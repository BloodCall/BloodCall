package gr.gdschua.bloodapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import gr.gdschua.bloodapp.R;

public class LoginActivity extends AppCompatActivity {
    EditText passET;
    EditText emailET;
    private FirebaseAuth mAuth;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        emailET= findViewById(R.id.email_input);
        passET = findViewById(R.id.password_input);
        findViewById(R.id.login_auth_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passET.getText().toString().trim();
                String email = emailET.getText().toString().trim();
                signIn(email,password);
            }
        });
    }


    private void signIn(String email,String password){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class );
                    startActivity(intent);
                }else{
                    emailET.setError("Invalid email or password , try again or sign up");
                }
            }
        });
    }
}