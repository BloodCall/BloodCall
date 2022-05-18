package gr.gdschua.bloodapp.DatabaseAccess;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import gr.gdschua.bloodapp.Entities.Post;

public class DAOPosts {

    public Task<Void> insertPost(Post newPost) {

        return FirebaseDatabase.getInstance().getReference("Alerts")
                .child(String.valueOf(newPost.getId()))
                .setValue(newPost);
    }

}
