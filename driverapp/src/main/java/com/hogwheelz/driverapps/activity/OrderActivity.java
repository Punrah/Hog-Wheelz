package com.hogwheelz.driverapps.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private static final String TAG = OrderActivity.class.getSimpleName();

    private String destinationAddress;
    private String destinationLat;
    private String destinationLng;
    private String distance;
    private String orderId;
    private String orderType;
    private String originAddress;
    private String originLat;
    private String originLng;
    private String price;
    private String note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        destinationAddress=getIntent().getStringExtra("destination_address");
        destinationLat=getIntent().getStringExtra("destination_lat");
        destinationLng=getIntent().getStringExtra("destination_lng");
        distance=getIntent().getStringExtra("distance");
        orderId=getIntent().getStringExtra("order_id");
        orderType=getIntent().getStringExtra("order_type");
        originAddress=getIntent().getStringExtra("origin_address");
        originLat=getIntent().getStringExtra("origin_lat");
        originLng=getIntent().getStringExtra("origin_lng");
        price=getIntent().getStringExtra("price");
        note=getIntent().getStringExtra("note");

        TextView textViewDestination=(TextView)findViewById(R.id.destination);
        TextView textViewDistance=(TextView)findViewById(R.id.distance);
        TextView textViewOrderId=(TextView)findViewById(R.id.order_id);
        TextView textVieworderType=(TextView)findViewById(R.id.order_type);
        TextView textViewOrigin=(TextView)findViewById(R.id.origin);
        TextView textViewPrice=(TextView)findViewById(R.id.price);
        TextView textViewNote=(TextView)findViewById(R.id.note);

        textViewDestination.setText(destinationAddress);
        textViewDistance.setText(String.valueOf(distance));
        textViewOrderId.setText(orderId);
        textVieworderType.setText(String.valueOf(orderType));
        textViewOrigin.setText(originAddress);
        textViewPrice.setText(String.valueOf(price));
        textViewNote.setText(String.valueOf(note));

        Button buttonAccept = (Button) findViewById(R.id.accept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptOrder();
            }
        });

    }



    private void acceptOrder() {
        // Tag used to cancel the request
        String tag_string_req = "accept_order";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.ACCEPT_ORDER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");

                    // Check for error node in json
                    if (status.contentEquals("1")) {
                        Toast.makeText(OrderActivity.this, msg, Toast.LENGTH_SHORT).show();

                    }
                    else if (status.contentEquals("2"))
                    {
                        Toast.makeText(OrderActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    else {
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

                params.put("order_id",orderId);
                params.put("driver_id",MainActivity.idDriver);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
