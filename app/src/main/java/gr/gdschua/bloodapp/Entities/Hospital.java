package gr.gdschua.bloodapp.Entities;

public class Hospital {
    private String name, email, id, address;
    private double lat, lon;

    public Hospital() {
    }

    public Hospital(String name, String email, double lat, double lon, String address) {
        this.name = name;
        this.email = email;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id; //store firebase authentication id into object itself
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
