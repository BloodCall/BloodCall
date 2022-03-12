package gr.gdschua.bloodapp.DatabaseAccess;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import gr.gdschua.bloodapp.Entities.Alert;

public class DAOAlerts {

    public Task<Void> insertAlert(Alert newAlert) {

        return FirebaseDatabase.getInstance().getReference("Alerts")
                .child(String.valueOf(newAlert.getId()))
                .setValue(newAlert);
    }

}
