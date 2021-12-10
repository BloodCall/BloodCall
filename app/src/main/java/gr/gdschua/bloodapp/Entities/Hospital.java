package gr.gdschua.bloodapp.Entities;

public class Hospital {
    private String Name,Email;
    private double lat,lon;

    public Hospital(String name, String email, double lat, double lon) {
        Name = name;
        Email = email;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
