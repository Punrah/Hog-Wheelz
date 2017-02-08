package com.hogwheelz.driverapps.persistence;

/**
 * Created by Startup on 1/27/17.
 */

public class User {
    private static String name;
    private static String Username;
    private static String Phone;


    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }

    public static String getUsername() {
        return Username;
    }

    public static void setUsername(String username) {
        Username = username;
    }

    public static String getPhone() {
        return Phone;
    }

    public static void setPhone(String phone) {
        Phone = phone;
    }
}
