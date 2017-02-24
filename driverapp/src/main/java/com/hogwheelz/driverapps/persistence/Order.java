package com.hogwheelz.driverapps.persistence;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Startup on 2/1/17.
 */

public  class Order implements Serializable{

    public User user;
    public String id_order;
    public String orderDate;
    public int price;
    public double distance;
    public LatLng pickupPosition;
    public LatLng dropoofPosition;
    public String pickupAddress;
    public String dropoffAddress;
    public Driver driver;
    public String pickupNote="";
    public String dropoffNote="";
    public String status;
    public int orderType;

    public Order()
    {
        user = new User();
        id_order="";
        orderDate="";
        price=0;
        distance=0;
        pickupPosition = new LatLng(0,0);
        dropoofPosition=new LatLng(0,0);
        pickupAddress="";
        dropoffAddress="";
        driver = new Driver();
        pickupNote="";
        dropoffNote="";
        status="";
        orderType=0;
    }



    public void setPrice(String priceString)
    {
        price=Integer.parseInt(priceString);
    }
    public String getPriceString() {
        return String.valueOf(price);
    }
    public String getDistanceString() {
        return String.valueOf(distance);
    }
    public String getPickupLatString() {
        return String.valueOf(pickupPosition.latitude);
    }

    public String getPickupLngString() {
        return String.valueOf(pickupPosition.longitude);
    }

    public String getDropoffLatString() {
        return String.valueOf(dropoofPosition.latitude);
    }

    public String getDropoffLngString() {
        return String.valueOf(dropoofPosition.longitude);
    }

    public String getPickupNoteString()
    {

            return pickupNote;

    }
    public String getDropoffNoteString()
    {

            return dropoffNote;

    }


    public void setPickupPlace(Place place)
    {
        pickupPosition=place.getLatLng();
        pickupAddress=place.getName().toString();
    }

    public void setDropoffPlace(Place place)
    {
        dropoofPosition=place.getLatLng();
        dropoffAddress=place.getName().toString();
    }

}
