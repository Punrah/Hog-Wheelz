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

import com.hogwheelz.driverapps.persistence.Message;

import java.util.ArrayList;
import java.util.List;


public class MessageSQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = MessageSQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "hogDB";

	// Login table name
	private static final String TABLE_NAME= "message_hogwheelz";

	// Login Table Columns names
	private static final String KEY_ID = "id_message";
	private static final String FIELD1 = "subject";
	private static final String FIELD2 = "body";
	private static final String FIELD3 = "date";
	private static final String FIELD4 = "status";

	public MessageSQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME+ "("
				+ KEY_ID + " TEXT,"
				+ FIELD1 + " TEXT,"
				+ FIELD2 + " TEXT,"
				+ FIELD3 + " TEXT,"
				+ FIELD4 + " TEXT" + ")";
		db.execSQL(CREATE_TABLE);

		Log.d(TAG, "Database tables created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addMessage(Message message) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, message.idMessage);
		values.put(FIELD1, message.subject); // Name
		values.put(FIELD2, message.body); // Email
		values.put(FIELD3, message.date);
		values.put(FIELD4, message.status);// Phone
		// Inserting Row
		long id = db.insert(TABLE_NAME, null, values);
		db.close(); // Closing database connection

		Log.d(TAG, "New user inserted into sqlite: " + id);
	}
	public void setRead(String idMessage) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(FIELD4, "read");// Phone
		// Inserting Row
		long id = db.update(TABLE_NAME, values, KEY_ID+"="+idMessage,null);
		db.close(); // Closing database connection

		Log.d(TAG, "New user inserted into sqlite: " + id);
	}


	/**
	 * Getting user data from database
	 * */
	public List<Message> getMessageList() {
		List<Message> messageList = new ArrayList<>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);


		if (cursor .moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				Message message = new Message();
				message.idMessage=cursor.getString(cursor.getColumnIndex(KEY_ID));
				message.subject=cursor.getString(cursor.getColumnIndex(FIELD1));
				message.body=cursor.getString(cursor.getColumnIndex(FIELD2));
				message.date=cursor.getString(cursor.getColumnIndex(FIELD3));
				message.status=cursor.getString(cursor.getColumnIndex(FIELD4));

				messageList.add(message);
				cursor.moveToNext();
			}
		}


		cursor.close();
		db.close();

		return messageList;
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void deleteDriver() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_NAME, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from sqlite");
	}

	public void deleteMessage(String idMessage) {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_NAME,KEY_ID+"="+idMessage,null);
		db.close();

	}
}
