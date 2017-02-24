package com.hogwheelz.driverapps.persistence;

import android.content.Context;

import com.hogwheelz.driverapps.helper.DriverSQLiteHandler;

public class DriverGlobal {
    private static DriverSQLiteHandler db;

    public static Driver getDriver(Context context) {
        db = new DriverSQLiteHandler(context);
        return db.getDriverDetails();

    }
}