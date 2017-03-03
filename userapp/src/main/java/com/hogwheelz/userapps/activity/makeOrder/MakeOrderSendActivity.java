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
import com.google.gson.Gson;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Order;
import com.hogwheelz.userapps.persistence.OrderSend;
import com.hogwheelz.userapps.persistence.UserGlobal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Startup on 2/8/17.
 */

public class MakeOrderSendActivity extends MakeOrder {
    private static final String TAG = MakeOrder.class.getSimpleName();


    @Override
    public void setToolbarTitle() {

            toolbar.setTitle("Hogsend");
            setSupportActionBar(toolbar);
    }

    @Override
    public void inizializeOrder() {
        order=new OrderSend();
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
                    Toast.makeText(MakeOrderSendActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MakeOrderSendActivity.this, "Couldn't get json from server.", Toast.LENGTH_SHORT).show();

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
                    order.pickupNote=editTextPickupNote.getText().toString();
                    order.dropoffNote=editTextDropoffNote.getText().toString();
                    addOrderDetail();

                }
            });

            // Dismiss the progress dialog

        }
    }

    private void addOrderDetail()
    {

        Intent i = new Intent(MakeOrderSendActivity.this,
                MakeOrderSendDetailActivity.class);
        Gson gson = new Gson();
        String myJson = gson.toJson(order);
        i.putExtra("order_send", myJson);
        startActivity(i);

    }

}
