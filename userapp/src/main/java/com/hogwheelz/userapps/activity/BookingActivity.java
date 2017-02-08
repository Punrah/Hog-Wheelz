package com.hogwheelz.userapps.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.app.Config;
import com.hogwheelz.userapps.persistence.Driver;
import com.hogwheelz.userapps.persistence.Order;
import com.hogwheelz.userapps.persistence.OrderRide;
import com.hogwheelz.userapps.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class BookingActivity extends AppCompatActivity {
    private static final String TAG = BookingActivity.class.getSimpleName();

private TextView textViewIdOrder;
    private String idOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hog_book);

        final RelativeLayout loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        textViewIdOrder= (TextView) findViewById(R.id.id_order);
        idOrder=getIntent().getStringExtra("id_order");
        textViewIdOrder.setText(idOrder);

    }
    @Override
    protected void onStart() {
        super.onStart();


    }



    @Override
    protected void onResume() {

            super.onResume();
            NotificationUtils.clearNotifications(getApplicationContext());



    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
