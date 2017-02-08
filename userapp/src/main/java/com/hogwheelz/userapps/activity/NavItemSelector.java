package com.hogwheelz.userapps.activity;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.hogwheelz.userapps.R;

/**
 * Created by Startup on 1/27/17.
 */

public class NavItemSelector extends Activity {

    public static void navSelected(int id, Context context)
    {
        if (id == R.id.nav_hogpay) {
            Toast.makeText(context, "1", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_history) {
            Toast.makeText(context, "2", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_favorites) {
            Toast.makeText(context, "3", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_scheduled) {
            Toast.makeText(context, "4", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_notification) {
            Toast.makeText(context, "5", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_invite) {
            Toast.makeText(context, "6", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_support) {
            Toast.makeText(context, "7", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_ride) {
            Toast.makeText(context, "8", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_rate) {
            Toast.makeText(context, "9", Toast.LENGTH_SHORT).show();
        }
    }
}
