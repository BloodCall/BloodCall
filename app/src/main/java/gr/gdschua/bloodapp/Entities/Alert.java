package gr.gdschua.bloodapp.Entities;

public class Alert {
    public String owner,bloodType,dateCreated;

    public Alert(String owner, String bloodType, String dateCreated) {
        this.owner = owner;
        this.bloodType = bloodType;
        this.dateCreated = dateCreated;
    }

    public Alert(){

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
