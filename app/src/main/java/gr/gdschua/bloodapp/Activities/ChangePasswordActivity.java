package gr.gdschua.bloodapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;

public class ChangePasswordActivity extends AppCompatActivity {


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        EditText currPW = findViewById(R.id.currPW);
        EditText newPW = findViewById(R.id.newPW_1);
        EditText repeatNewPW = findViewById(R.id.newPW_2);
        Button applyBTN = findViewById(R.id.button);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        new DAOUsers().getUser().addOnSuccessListener(dataSnapshot -> applyBTN.setOnClickListener(v -> {
            if (!newPW.getText().toString().equals(repeatNewPW.getText().toString())) {
                new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setMessage(getString(R.string.pwd_not_matching))
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(dataSnapshot.getValue(User.class).getEmail(), currPW.getText().toString());

                Objects.requireNonNull(user).reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPW.getText().toString()).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.pwd_updated), Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.pwd_upd_error), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                new AlertDialog.Builder(ChangePasswordActivity.this)
                                        .setMessage(getString(R.string.pwd_wrong_curr))
                                        .setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.dismiss())
                                        .show();
                            }
                        });
            }
        }));
    }
}