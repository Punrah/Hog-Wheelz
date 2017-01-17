package com.hogwheelz.userapps.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.app.Config;
import com.hogwheelz.userapps.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {
    private static final String TAG = BookingActivity.class.getSimpleName();

    String idCustomer;
    String pickupLat ;
    String pickupLng ;
    String dropoffLat ;
    String dropoffLng ;
    String pickupAddress ;
    String dropoffAddress ;
    String price ;
    String notes ;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    String name;
    String phone;
    String plat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hog_book);

        idCustomer =getIntent().getStringExtra("id_customer");
        pickupLat = getIntent().getStringExtra("pickup_lat");
        pickupLng = getIntent().getStringExtra("pickup_lng");
        dropoffLat = getIntent().getStringExtra("dropoff_lat");
        dropoffLng = getIntent().getStringExtra("dropoff_lng");
        pickupAddress = getIntent().getStringExtra("pickup_address");
        dropoffAddress = getIntent().getStringExtra("dropoff_address");
        price = getIntent().getStringExtra("price");
        notes = getIntent().getStringExtra("notes");

        TextView textViewPickUpAddres=(TextView)findViewById(R.id.textView_pickup_address);
        TextView textViewDropOffAddress=(TextView)findViewById(R.id.textView_dropoff_address);
        TextView textViewPrice=(TextView)findViewById(R.id.price_edittext);
        TextView textViewNote=(TextView) findViewById(R.id.note_edittext);

        final RelativeLayout loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);

        textViewPickUpAddres.setText(pickupAddress);
        textViewDropOffAddress.setText(dropoffAddress);
        textViewPrice.setText(price);
        textViewNote.setText(notes);



        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    String regId=intent.getStringExtra("token");
                    Toast.makeText(context, regId, Toast.LENGTH_SHORT).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("payload");

                    JSONObject json = null;
                    try {
                        json = new JSONObject(intent.getStringExtra("payload"));
                        name = json.getString("name");
                        phone = json.getString("phone");
                        plat = json.getString("plat");

                        Intent i = new Intent(getApplicationContext(),
                                DriverActivity.class);
                        i.putExtra("name",name);
                        i.putExtra("phone",phone);
                        i.putExtra("plat",plat);
                        startActivity(i);
                        putResult();
                        finish();

                    } catch (JSONException e) {
                        Log.e(TAG, "Json Exception: " + e.getMessage());
                        Toast.makeText(getApplicationContext(),  e.getMessage(), Toast.LENGTH_LONG).show();
                    }


                }
            }

        };








    }
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, idCustomer, Toast.LENGTH_SHORT).show();
        booking();

    }


    private void putResult()
    {
        Intent output = new Intent();
        output.putExtra("name",name);
        output.putExtra("phone", phone);
        output.putExtra("plat", plat);
        setResult(RESULT_OK, output);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void booking() {
        // Tag used to cancel the request
        String tag_string_req = "req_order";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_BOOKING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");

                    // Check for error node in json
                    if (status.contentEquals("1")) {


                    } else {
                        // Error in login. Get the error message

                        String errorMsg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_customer",idCustomer);
                params.put("add_from",pickupAddress);
                params.put("add_to",dropoffAddress);
                params.put("lat_from",pickupLat);
                params.put("long_from",pickupLng);
                params.put("lat_to",dropoffLat);
                params.put("long_to",dropoffLng);
                params.put("price",price);
                params.put("note",notes);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onStop() {
        Intent output = new Intent();
        output.putExtra("name",name);
        output.putExtra("phone", phone);
        output.putExtra("plat", plat);
        setResult(RESULT_OK, output);
        super.onStop();
    }
}
