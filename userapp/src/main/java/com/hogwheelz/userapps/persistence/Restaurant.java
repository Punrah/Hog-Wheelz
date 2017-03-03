package com.hogwheelz.userapps.persistence;

import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Startup on 2/10/17.
 */

public class Restaurant{

    public String idRestaurant;
    public String name;
    public LatLng location;
    public String address;
    public Double distance;
    public String openHour;
    public String  photo;
    public List<Menu> menu;
    public List<String> openHourComplete;

    public Restaurant()
    {
        idRestaurant="";
        name="";
        location=new LatLng(0,0);
        address="";
        distance=Double.valueOf(0);
        openHour ="";
        photo="";
        menu=new ArrayList<Menu>();
        openHourComplete=new ArrayList<String>();
    }

    public String getDistance() {
        return String.valueOf(distance);
    }
}
