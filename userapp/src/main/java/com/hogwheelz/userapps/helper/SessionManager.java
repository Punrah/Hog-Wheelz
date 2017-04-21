package com.hogwheelz.userapps.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SessionManager {
	// LogCat tag
	private static String TAG = SessionManager.class.getSimpleName();

	// Shared Preferences
	SharedPreferences pref;

	Editor editor;
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Shared preferences file name
	private static final String PREF_NAME = "AndroidHiveLogin";
	
	private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
	private static final String KEY_IS_ORDER = "isFine";

	private static final String ORDER = "order";

	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setLogin(boolean isLoggedIn) {

		editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

		// commit changes
		editor.commit();

		Log.d(TAG, "User login session modified!");
	}

	public void setOrder(String order) {


		editor.putString(ORDER, order);
		editor.putBoolean(KEY_IS_ORDER,true);

		// commit changes
		editor.commit();

		Log.d(TAG, "User login session modified!");
	}

	public void deleteOrder()
	{
		editor.putBoolean(KEY_IS_ORDER,false);

		// commit changes
		editor.commit();

		Log.d(TAG, "User login session modified!");

	}
	public boolean isOrder()
	{

		return pref.getBoolean(KEY_IS_ORDER,false);

	}
	public String getOrder()
	{
		String prefString=pref.getString(ORDER,"");
		return prefString;

	}




	
	public boolean isLoggedIn(){
		return pref.getBoolean(KEY_IS_LOGGED_IN, false);
	}
}
