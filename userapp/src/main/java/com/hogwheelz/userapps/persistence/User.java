package com.hogwheelz.userapps.persistence;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Startup on 1/27/17.
 */

public class User implements Parcelable {
    public  String name;
    public  String username;
    public  String phone;
    public  String idCustomer;

    public User()
    {
        name="";
        username="";
        phone="";
        idCustomer="";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(phone);
        dest.writeString(idCustomer);

    }
    // Method to recreate a Question from a Parcel
    public static Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }

    };

    public User (Parcel parcel) {

        name=parcel.readString();
        username=parcel.readString();
        phone=parcel.readString();
        idCustomer=parcel.readString();
    }




}
