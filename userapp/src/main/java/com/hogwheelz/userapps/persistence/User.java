package com.hogwheelz.userapps.persistence;

import android.app.Application;

import java.io.Serializable;

/**
 * Created by Startup on 1/27/17.
 */

public class User implements Serializable {
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




}
