package gr.gdschua.bloodapp.DatabaseAcess;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Random;

import gr.gdschua.bloodapp.Entities.Event;

public class DAOEvents {
    Random rand = new Random();


    public DAOEvents(){}

    public Task<Void> insertEvent(Event newEvent){

        return FirebaseDatabase.getInstance().getReference("Events")
                .child(String.valueOf(Math.abs(rand.nextLong())))
                .setValue(newEvent);
    }

}
