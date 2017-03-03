package com.hogwheelz.userapps.activity.makeOrder;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hogwheelz.userapps.activity.FindDriverActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.OrderRide;
import com.hogwheelz.userapps.persistence.UserGlobal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Startup on 2/8/17.
 */

public class MakeOrderRideActivity extends MakeOrder {

    private static final String TAG = MakeOrder.class.getSimpleName();

    @Override
    public void setToolbarTitle() {

        toolbar.setTitle("Hogride");
        setSupportActionBar(toolbar);
    }

    @Override
    public void inizializeOrder() {
        order = new OrderRide();
    }

    @Override
    public void readyToOrder() {
        new calculatePrice().execute();
    }

    private class calculatePrice extends AsyncTask<Void, Void, Void> {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            textViewPrice.setText("Please wait");

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            String originsLat = order.getPickupLatString();
            String originsLng = order.getPickupLngString();
            String destinationsLat = order.getDropoffLatString();
            String destinationsLng = order.getDropoffLngString();
            String url = AppConfig.getPriceURL(originsLat + "," + originsLng, destinationsLat + "," + destinationsLng);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    order.price= jsonObj.getInt("price");
                    order.distance = jsonObj.getDouble("distance");

                } catch (final JSONException e) {
                    Toast.makeText(MakeOrderRideActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MakeOrderRideActivity.this, "Couldn't get json from server.", Toast.LENGTH_SHORT).show();

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            textViewPrice.setText(order.getPriceString()+" "+ order.getDistanceString());
            linearLayoutOrder.setVisibility(View.VISIBLE);


            // Link to Register Screen
            buttonBook.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    booking();

                }
            });

            // Dismiss the progress dialog

        }
    }

    private void booking() {
        // Tag used to cancel the request
        String tag_string_req = "order_ride";
        order.user = UserGlobal.getUser(getApplicationContext());

        order.pickupNote=editTextPickupNote.getText().toString();
        order.dropoffNote=editTextDropoffNote.getText().toString();



        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ORDER_RIDE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");

                    // Check for error node in json
                    if (status.contentEquals("1")) {

                        String idOrder=jObj.getString("id_order");

                        Intent i = new Intent(MakeOrderRideActivity.this,
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

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
