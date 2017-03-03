package com.hogwheelz.userapps.activity.makeOrder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.FindDriverActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.persistence.Order;
import com.hogwheelz.userapps.persistence.OrderSend;
import com.hogwheelz.userapps.persistence.UserGlobal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MakeOrderSendDetailActivity extends AppCompatActivity {

    private static final String TAG = MakeOrderSendDetailActivity.class.getSimpleName();

    Toolbar toolbar;
    OrderSend order;

    EditText editTextSenderName;
    EditText editTextSenderPhone;
    EditText editTextReceiverName;
    EditText editTextReceiverPhone;
    EditText editTextDescription;

    Button buttonOrderSend;

    TextView textViewPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order_send_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("HogSend");
        setSupportActionBar(toolbar);

        Gson gson = new Gson();
        order = gson.fromJson(getIntent().getStringExtra("order_send"), OrderSend.class);

        editTextDescription = (EditText) findViewById(R.id.description_send);
        editTextSenderName=(EditText) findViewById(R.id.sender_name_send);
        editTextSenderPhone=(EditText) findViewById(R.id.sender_phone_send);
        editTextReceiverName=(EditText) findViewById(R.id.receiver_name_send);
        editTextReceiverPhone=(EditText) findViewById(R.id.receiver_phone_send);
        textViewPrice=(TextView) findViewById(R.id.price_send);

        textViewPrice.setText(order.getPriceString());

        buttonOrderSend = (Button) findViewById(R.id.button_detail_send);

        buttonOrderSend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                order.description=editTextDescription.getText().toString();
                order.senderName=editTextSenderName.getText().toString();
                order.senderPhone=editTextSenderPhone.getText().toString();
                order.receiverName=editTextReceiverName.getText().toString();
                order.receiverPhone=editTextReceiverPhone.getText().toString();
                booking();

            }
        });

    }

    private void booking() {
        // Tag used to cancel the request
        String tag_string_req = "order send";
        order.user = UserGlobal.getUser(getApplicationContext());

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ORDER_SEND, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");

                    // Check for error node in json
                    if (status.contentEquals("1")) {

                        String idOrder=jObj.getString("id_order");

                        Intent i = new Intent(MakeOrderSendDetailActivity.this,
                                FindDriverActivity.class);
                        i.putExtra("id_order",idOrder);
                        startActivity(i);
                        finish();

                    } else {

                        String errorMsg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(),
                                "status"+errorMsg, Toast.LENGTH_LONG).show();
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
                        "error listener"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_customer", order.user.idCustomer);
                params.put("add_from", order.pickupAddress);
                params.put("add_to", order.dropoffAddress);
                params.put("lat_from", order.getPickupLatString());
                params.put("long_from", order.getPickupLngString());
                params.put("lat_to", order.getDropoffLatString());
                params.put("long_to", order.getDropoffLngString());
                params.put("price", order.getPriceString());
                params.put("note", order.getPickupNoteString()+ order.getDropoffNoteString());
                params.put("description",order.description);
                params.put("sender_name",order.senderName);
                params.put("sender_phone",order.senderPhone);
                params.put("receiver_name",order.receiverName);
                params.put("receiver_phone",order.receiverPhone);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
