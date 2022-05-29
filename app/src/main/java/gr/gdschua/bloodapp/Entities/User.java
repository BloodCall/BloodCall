package gr.gdschua.bloodapp.Entities;

import com.google.firebase.database.Exclude;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gr.gdschua.bloodapp.DatabaseAccess.DAOUsers;

public class User {
    public final ArrayList<CheckIn> checkIns = new ArrayList<>();
    public Boolean notifFirstTime, notifications, eventNotifs;
    private String fullName, email, bloodType, id;
    private int xp;

    public User(String fullName, String email, String bloodType) {
        this.fullName = fullName;
        this.email = email;
        this.bloodType = bloodType;
        notifFirstTime = false;
    }

    public User() {
    }



    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public Boolean getNotifications() {
        return notifications;
    }

    public void setNotifications(Boolean notifications) {
        this.notifications = notifications;
    }


    @Exclude
    public void updateSelf(){
        new DAOUsers().updateUser(this);
    }

    public Boolean getEventNotifs() {
        return eventNotifs;
    }

    public void setEventNotifs(Boolean eventNotifs) {
        this.eventNotifs = eventNotifs;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getTopic() {
        return this.bloodType.replace("+", "pos").replace("-", "neg");
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id; //store firebase auth id into object itself.
    }
}
