package gr.gdschua.bloodapp.Entities;

public class Event {
    private String Name,Date,Owner;
    private double lat,lon;

    public Event(String name, String date, String owner, double lat, double lon) {
        Name = name;
        Date = date;
        Owner = owner;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }
}
