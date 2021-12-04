package gr.gdschua.bloodapp.Entities;

public class User {
    private String fullName,email,bloodType,uId;

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
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


    public User(String fullName, String email, String bloodType) {
        this.fullName = fullName;
        this.email = email;
        this.bloodType = bloodType;
    }

    public User() {
    }
}
