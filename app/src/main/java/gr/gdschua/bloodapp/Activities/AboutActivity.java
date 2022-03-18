package gr.gdschua.bloodapp.Activities;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import gr.gdschua.bloodapp.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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