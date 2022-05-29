package gr.gdschua.bloodapp.Activities;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import gr.gdschua.bloodapp.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView names = findViewById(R.id.namesTv);
        TextView git = findViewById(R.id.gitTv);
        TextView licence = findViewById(R.id.LicTv);

        names.setMovementMethod(LinkMovementMethod.getInstance());
        git.setMovementMethod(LinkMovementMethod.getInstance());
        licence.setMovementMethod(LinkMovementMethod.getInstance());
    }
}