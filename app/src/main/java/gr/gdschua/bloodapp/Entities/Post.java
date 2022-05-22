package gr.gdschua.bloodapp.Entities;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.Exclude;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import gr.gdschua.bloodapp.DatabaseAccess.DAOPosts;
import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;

public class Post {

    private String authorName;
    private String authorLevel;
    private String title;
    private String body;
    private String flair;
    private String authorType;
    private String id;
    private String dateStamp;
    private ArrayList<Comment> comments;

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Post(String authorName, String authorLevel, String title, String body, String flair, String authorType) {
        this.authorName = authorName;
        this.authorLevel = authorLevel;
        this.title = title;
        this.body = body;
        this.flair = flair; //question o experience
        this.authorType = authorType; // user o hospital
        this.id = UUID.randomUUID().toString();
        this.dateStamp = java.time.LocalDate.now().toString();
        this.comments = new ArrayList<>();
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(String dateStamp) {
        this.dateStamp = dateStamp;
    }

    public Post(){

    }

    public String getId() {
        return id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorLevel() {
        return authorLevel;
    }

    public void setAuthorLevel(String authorLevel) {
        this.authorLevel = authorLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFlair() {
        return flair;
    }

    public void setFlair(String flair) {
        this.flair = flair;
    }

    public String getAuthorType() {
        return authorType;
    }

    public void setAuthorType(String authorType) {
        this.authorType = authorType;
    }

    @Exclude
    public void updateSelf(){
        new DAOPosts().updatePost(this);
    }

    @Exclude
    public Map<String, Object> getAsMap() {
        Map<String, Object> map = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
