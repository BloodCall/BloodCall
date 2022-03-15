package gr.gdschua.bloodapp.Entities;

import com.google.firebase.database.Exclude;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String fullName, email, bloodType, id, notifications;
    private int xp;
    public ArrayList<CheckIn> checkIns= new ArrayList<>();

    public User(String fullName, String email, String bloodType) {
        this.fullName = fullName;
        this.email = email;
        this.bloodType = bloodType;
    }


    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }


    public User() {
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getNotifications() {
        return notifications;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }

    @Exclude
    public boolean getNotificationsB() {
        return Boolean.parseBoolean(notifications);
    }

    @Exclude
    public void setNotificationsB(boolean notifications) {
        this.notifications = String.valueOf(notifications);
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
