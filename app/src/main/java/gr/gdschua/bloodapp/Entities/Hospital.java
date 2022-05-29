package gr.gdschua.bloodapp.Entities;

import com.google.firebase.database.Exclude;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.gdschua.bloodapp.DatabaseAccess.DAOHospitals;

public class Hospital {
    private List<String> accepts;
    private String name, email, id, address;
    private double lat, lon;
    private int serviced;

    @Exclude
    public void updateSelf(){
        new DAOHospitals().updateUser(this);
    }

    public Hospital() {
    }

    public Hospital(String name, String email, double lat, double lon, String address,List<String> accepts) {
        this.name = name;
        this.email = email;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.serviced=0;
        this.accepts = accepts;
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

    public List<String> getAccepts() {
        return accepts;
    }

    public void setAccepts(List<String> accepts) {
        this.accepts = accepts;
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

    public int getServiced() {
        return serviced;
    }

    public void setServiced(int serviced) {
        this.serviced = serviced;
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
