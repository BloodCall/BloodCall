package gr.gdschua.bloodapp.Entities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.firebase.database.Exclude;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Event {
    private final String id;
    private String name;
    private String date;
    private String owner;
    private double lat, lon;

    public Event(String name, String date, String owner, double lat, double lon) {
        this.name = name;
        this.date = date;
        this.owner = owner;
        this.lat = lat;
        this.lon = lon;
        id = UUID.randomUUID().toString();
    }


    public String getId() {
        return id;
    }

    @Exclude
    public String getAddress(Context context) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(this.lat, this.lon, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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
