package gr.gdschua.bloodapp.Entities;

import java.util.UUID;

public class Alert {
    public String owner, bloodType, dateCreated;
    int id; //ensure notification uniqueness, allows multiple notifications at once, and prevents duplicate notifications

    public Alert(String owner, String bloodType, String dateCreated) {
        this.owner = owner;
        this.bloodType = bloodType;
        this.dateCreated = dateCreated;
        id = Math.abs(UUID.randomUUID().hashCode());
    }

    public Alert() {

    }

    public int getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
