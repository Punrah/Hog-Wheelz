package com.hogwheelz.driverapps.persistence;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Startup on 2/1/17.
 */

public class Driver implements Serializable {
    public String username;
    public String name;
    public String phone;
    public String plat;
    public String idDriver;
    public LatLng driverLocation;
    public int rating;
    public String photo;
    public String photoBinary;
    public String type;

    public Driver()
    {
        rating=0;
        username="";
        name="";
        plat="";
        phone="";
        idDriver="";
        driverLocation = new LatLng(0,0);
        photo="";
        photoBinary="";
        type="";

    }

}
