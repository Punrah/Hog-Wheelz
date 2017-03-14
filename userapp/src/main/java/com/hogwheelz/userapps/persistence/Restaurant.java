package com.hogwheelz.userapps.persistence;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Startup on 2/10/17.
 */

public class Restaurant implements Parcelable{

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

    public int getRecapPrice()
    {
        int price=0;
        for(int i=0;i<menu.size();i++)
        {
            price=price+menu.get(i).getMenuPrice();
        }
        return price;
    }

    public List<Item> getSelectedItem()
    {
        List<Item> selectedItem = new ArrayList<Item>();
        for(int i=0;i<menu.size();i++)
        {
            selectedItem.addAll(menu.get(i).getSelectedItem());
        }
        return selectedItem;
    }

    public String getDistance() {
        return String.valueOf(distance);
    }

    public Restaurant (Parcel parcel) {
        this.idRestaurant = parcel.readString();
        this.name = parcel.readString();
        this.location = (LatLng) parcel.readParcelable(LatLng.class.getClassLoader());
        this.address = parcel.readString();
        this.distance = parcel.readDouble();
        this.openHour = parcel.readString();
        this.photo = parcel.readString();
        this.menu = parcel.readArrayList(Menu.class.getClassLoader());
        this.openHourComplete = parcel.readArrayList(String.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Required method to write to Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idRestaurant);
        dest.writeString(name);
        dest.writeParcelable(location,flags);
        dest.writeString(address);
        dest.writeDouble(distance);
        dest.writeString(openHour);
        dest.writeString(photo);
        dest.writeList(menu);
        dest.writeList(openHourComplete);
    }

    // Method to recreate a Question from a Parcel
    public static Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {

        @Override
        public Restaurant createFromParcel(Parcel source) {
            return new Restaurant(source);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }

    };
}
