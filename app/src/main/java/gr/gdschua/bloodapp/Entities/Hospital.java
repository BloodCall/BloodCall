package gr.gdschua.bloodapp.Entities;

public class Hospital {
    private String Name,Address,Email;

    public Hospital(String name, String address, String email) {
        Name = name;
        Address = address;
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
