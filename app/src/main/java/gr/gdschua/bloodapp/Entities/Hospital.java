package gr.gdschua.bloodapp.Entities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.util.List;
import java.util.Locale;

public class Hospital {
    private String Name,Email;
    private double lat,lon;

    public Hospital(){

    }
    public Hospital(String name, String email, double lat, double lon) {
        Name = name;
        Email = email;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return Name;
    }

    public String getAddress(Context context){
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(this.lat, this.lon, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
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
