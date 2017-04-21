package com.hogwheelz.userapps.persistence;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.helper.UserSQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Startup on 2/1/17.
 */

public class UserGlobal {

    private static UserSQLiteHandler db;
    public static double balance;

    public static User getUser(Context context)
    {
        db = new UserSQLiteHandler(context);
        return db.getUserDetails();

    }

    public static void setUser(Context context,User user)
    {
        db = new UserSQLiteHandler(context);
        db.updateUser(user);
    }





}
