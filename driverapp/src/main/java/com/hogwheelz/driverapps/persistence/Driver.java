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

    public Driver()
    {
        username="";
        name="";
        plat="";
        phone="";
        idDriver="";
        driverLocation = new LatLng(0,0);
    }

}
