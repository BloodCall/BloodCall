package gr.gdschua.bloodapp.Activities.HospitalActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import gr.gdschua.bloodapp.Activities.AboutActivity;
import gr.gdschua.bloodapp.Activities.LauncherActivity;
import gr.gdschua.bloodapp.Activities.MainActivity;
import gr.gdschua.bloodapp.R;

public class HospitalSettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_hospital);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.hospital_prefrences, rootKey);
            Preference oss_btn = findPreference(getString(R.string.settings_toolbar));
            Objects.requireNonNull(oss_btn).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getContext(), OssLicensesMenuActivity.class));
                    return true;
                }
            });

            Preference about_btn = findPreference(getString(R.string.about));
            Objects.requireNonNull(about_btn).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getContext(), AboutActivity.class));
                    return true;
                }
            });

            Preference signout_btn = findPreference(getString(R.string.side_bar_signout));
            Objects.requireNonNull(signout_btn).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getContext(), LauncherActivity.class);
                    startActivity(intent);
                    getActivity().finishAffinity();
                    Toast.makeText(getContext(), getResources().getString(R.string.signed_out), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }
}