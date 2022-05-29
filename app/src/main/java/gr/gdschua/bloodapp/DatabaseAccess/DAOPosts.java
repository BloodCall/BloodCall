package gr.gdschua.bloodapp.DatabaseAccess;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import gr.gdschua.bloodapp.Entities.Post;
import gr.gdschua.bloodapp.Entities.User;

public class DAOPosts {

    public Task<Void> insertPost(Post newPost) {

        return FirebaseDatabase.getInstance().getReference("Posts")
                .child(String.valueOf(newPost.getId()))
                .setValue(newPost);
    }

    public Task<DataSnapshot> getPost(String uID) {
        return FirebaseDatabase.getInstance().getReference().child("Posts").child(uID).get();
    }

    public Task<Void> updatePost(Post newPost) {
        return FirebaseDatabase.getInstance().getReference("Posts")
                .child(newPost.getId())
                .updateChildren(newPost.getAsMap());
    }

}
