package com.hogwheelz.userapps.persistence;

import android.app.Application;
import android.content.Context;

import com.hogwheelz.userapps.helper.UserSQLiteHandler;

/**
 * Created by Startup on 2/1/17.
 */

public class UserGlobal {

    private static UserSQLiteHandler db;

    public static User getUser(Context context)
    {
        db = new UserSQLiteHandler(context);
        return db.getUserDetails();

    }
}
