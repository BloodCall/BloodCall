package gr.gdschua.bloodapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import org.w3c.dom.Text;

import gr.gdschua.bloodapp.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView names= findViewById(R.id.namesTv);
        TextView git= findViewById(R.id.gitTv);
        TextView licence = findViewById(R.id.LicTv);

        names.setMovementMethod(LinkMovementMethod.getInstance());
        git.setMovementMethod(LinkMovementMethod.getInstance());
        licence.setMovementMethod(LinkMovementMethod.getInstance());
    }
}