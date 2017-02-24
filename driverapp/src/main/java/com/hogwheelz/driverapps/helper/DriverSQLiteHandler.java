/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.hogwheelz.driverapps.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hogwheelz.driverapps.persistence.Driver;


public class DriverSQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = DriverSQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "hogDB";

	// Login table name
	private static final String TABLE_DRIVER = "driver_hogwheelz";

	// Login Table Columns names
	private static final String KEY_ID = "id_driver";
	private static final String KEY_NAME = "name";
	private static final String KEY_USERNAME = "email";
	private static final String KEY_PHONE = "phone";
	private static final String KEY_PLAT = "plat";

	public DriverSQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_DRIVER+ "("
				+ KEY_ID + " TEXT UNIQUE,"
				+ KEY_NAME + " TEXT,"
				+ KEY_USERNAME + " TEXT,"
				+ KEY_PLAT + " TEXT,"
				+ KEY_PHONE + " TEXT" + ")";
		db.execSQL(CREATE_LOGIN_TABLE);

		Log.d(TAG, "Database tables created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVER);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addDriver(Driver driver) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID,driver.idDriver);
		values.put(KEY_NAME, driver.name); // Name
		values.put(KEY_USERNAME, driver.username); // Email
		values.put(KEY_PHONE, driver.phone); // Phone
		values.put(KEY_PLAT, driver.plat);
		// Inserting Row
		long id = db.insert(TABLE_DRIVER, null, values);
		db.close(); // Closing database connection

		Log.d(TAG, "New user inserted into sqlite: " + id);
	}

	/**
	 * Getting user data from database
	 * */
	public Driver getDriverDetails() {
		Driver driver = new Driver();
		String selectQuery = "SELECT  * FROM " + TABLE_DRIVER;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			driver.idDriver=cursor.getString(cursor.getColumnIndex(KEY_ID));
			driver.name=cursor.getString(cursor.getColumnIndex(KEY_NAME));
			driver.username=cursor.getString(cursor.getColumnIndex(KEY_USERNAME));
			driver.phone=cursor.getString(cursor.getColumnIndex(KEY_PHONE));
			driver.plat=cursor.getString(cursor.getColumnIndex(KEY_PLAT));
		}
		cursor.close();
		db.close();
		// return user
		Log.d(TAG, "Fetching user from Sqlite: " + driver.toString());

		return driver;
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void deleteDriver() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_DRIVER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from sqlite");
	}

}
