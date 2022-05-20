package gr.gdschua.bloodapp.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import gr.gdschua.bloodapp.Activities.AboutActivity;
import gr.gdschua.bloodapp.Activities.LauncherActivity;
import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;
import gr.gdschua.bloodapp.Entities.User;
import gr.gdschua.bloodapp.R;

public class UserSettingsActivity extends AppCompatActivity {

    static User currUser;
    static Boolean showPermsDialog = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DAOUsers daoUsers = new DAOUsers();
        daoUsers.getUser().addOnCompleteListener(task -> {
            currUser = task.getResult().getValue(User.class);
            setContentView(R.layout.settings_activity_user);
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
        });
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {

        final ActivityResultLauncher<String> bgLocationRequest = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> setNotifications(result, currUser));


        final ActivityResultLauncher<String> locationRequest = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                bgLocationRequest.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            } else {
                setNotifications(result, currUser);
            }
        });

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void handleBgLoc() {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) && showPermsDialog) {
                new AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.CustomDialogTheme)
                        .setTitle(R.string.bg_loc_title)
                        .setMessage(R.string.bg_loc_text)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> locationRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION))
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            setNotifications(false,currUser);
                            dialog.dismiss();
                            showPermsDialog = true;
                        }).show();
            } else {
                setNotifications(true,currUser);
            }
            showPermsDialog = false;
        }

        public void setNotifications(Boolean state, User currUser){
            if (state) {
                FirebaseMessaging.getInstance().subscribeToTopic(currUser.getTopic());
                currUser.setNotifications(true);
                if (!currUser.notifFirstTime) {
                    currUser.setXp(currUser.getXp() + 10);
                    currUser.notifFirstTime = true;
                    Snackbar snackbar = Snackbar.make(requireView(),R.string.notif_first_time, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    currUser.notifFirstTime=true;
                }
            }
            else{
                FirebaseMessaging.getInstance().unsubscribeFromTopic(currUser.getTopic());
                currUser.setNotifications(false);
            }
            currUser.updateSelf();
        }

        public void setEventNotifications(Boolean state, User currUser){
            if (state) {
                FirebaseMessaging.getInstance().subscribeToTopic("events");
                currUser.setEventNotifs(true);
            }
            else{
                FirebaseMessaging.getInstance().unsubscribeFromTopic("events");
                currUser.setEventNotifs(false);
            }
            currUser.updateSelf();
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.user_preferences, rootKey);
            Preference oss_btn = findPreference(getString(R.string.settings_toolbar));
            Objects.requireNonNull(oss_btn).setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getContext(), OssLicensesMenuActivity.class));
                return true;
            });

            Preference editProfile = findPreference("edit_profile");

            Objects.requireNonNull(editProfile).setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getContext(),EditProfileActivity.class);
                startActivity(intent);
                return true;
            });

            ListPreference push_n_dist = findPreference("notifs_distance");

            SwitchPreference push_n_switch = findPreference("push_notif_switch");
            Objects.requireNonNull(push_n_switch).setChecked(currUser.getNotifications());
            if(push_n_switch.isChecked()){
                Objects.requireNonNull(push_n_dist).setVisible(true);
            }

            ListPreference push_n_e_dist = findPreference("notifs_event_distance");
            SwitchPreference push_n_e_switch = findPreference("push_notif_switch_events");

            if(Objects.requireNonNull(push_n_e_switch).isChecked()){
                Objects.requireNonNull(push_n_e_dist).setVisible(true);
            }

            push_n_e_switch.setOnPreferenceClickListener(preference -> {
                if (push_n_e_switch.isChecked()){
                    push_n_e_dist.setVisible(true);
                    setEventNotifications(true,currUser);
                }
                else{
                    push_n_e_dist.setVisible(false);
                    setEventNotifications(false,currUser);
                }
                return true;
            });

            push_n_switch.setOnPreferenceClickListener(preference -> {
                if (push_n_switch.isChecked()){
                    push_n_dist.setVisible(true);
                    handleBgLoc();
                }
                else{
                    setNotifications(false,currUser);
                    push_n_dist.setVisible(false);
                }
                return true;
            });

            Preference about_btn = findPreference(getString(R.string.about));
            Objects.requireNonNull(about_btn).setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getContext(), AboutActivity.class));
                return true;
            });


            Preference signout_btn = findPreference(getString(R.string.side_bar_signout));
            Objects.requireNonNull(signout_btn).setOnPreferenceClickListener(preference -> {
                FirebaseAuth.getInstance().signOut();
                FirebaseMessaging.getInstance().unsubscribeFromTopic(currUser.getTopic());
                FirebaseMessaging.getInstance().unsubscribeFromTopic("events");
                Intent intent = new Intent(getContext(), LauncherActivity.class);
                startActivity(intent);
                getActivity().finishAffinity();
                Toast.makeText(getContext(), getResources().getString(R.string.signed_out), Toast.LENGTH_SHORT).show();
                return true;
            });
        }

    }
}