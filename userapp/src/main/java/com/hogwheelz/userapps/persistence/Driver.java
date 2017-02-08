package com.hogwheelz.userapps.persistence;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Startup on 2/1/17.
 */

public class Driver implements Serializable {
    public String name;
    public String phone;
    public String plat;
    public String idDriver;
    public LatLng driverLocation;

    public Driver()
    {
        name="";
        phone="";
        phone="";
        idDriver="";
        driverLocation = new LatLng(0,0);
    }

}
