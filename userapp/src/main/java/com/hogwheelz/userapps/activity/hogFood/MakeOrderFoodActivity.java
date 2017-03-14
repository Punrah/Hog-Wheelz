package com.hogwheelz.userapps.activity.hogFood;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.FindDriverActivity;
import com.hogwheelz.userapps.activity.makeOrder.MakeOrder;
import com.hogwheelz.userapps.activity.makeOrder.MakeOrderRideActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Item;
import com.hogwheelz.userapps.persistence.OrderFood;
import com.hogwheelz.userapps.persistence.Restaurant;
import com.hogwheelz.userapps.persistence.UserGlobal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakeOrderFoodActivity extends AppCompatActivity {
    private static final String TAG = MakeOrder.class.getSimpleName();

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DELIVERY_ADDRESS = 1 ;
    Toolbar toolbar;
    LinearLayout linearLayoutItems;
    Restaurant restaurant;
    List<Item> items;
    List<TextView> textViewsQty;
    TextView textViewRecapPrice;
    TextView textViewAddMoreItems;
    TextView textViewAddDeliveryAddress;
    TextView textViewDeliveryFee;
    TextView textViewTotalPrice;
    Button buttonOrder;

    int totalPrice;
    OrderFood order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order_food);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Hogfood");
        setSupportActionBar(toolbar);

        order = new OrderFood();

        linearLayoutItems = (LinearLayout) findViewById(R.id.list_item);
        textViewRecapPrice = (TextView) findViewById(R.id.cost);
        textViewAddMoreItems = (TextView) findViewById(R.id.add_more_items);
        textViewAddDeliveryAddress = (TextView) findViewById(R.id.delivery_address);
        textViewDeliveryFee = (TextView) findViewById(R.id.delivery_fee);
        textViewTotalPrice = (TextView) findViewById(R.id.total_price);
        buttonOrder = (Button) findViewById(R.id.button_order_food);

        restaurant = getIntent().getParcelableExtra("restaurant");
        items = restaurant.getSelectedItem();

        fetchItem();
        textViewAddMoreItems.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            }
        });

        textViewAddDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPlaceAutocompleteActivityIntentForDeliveryAddress();
            }
        });

    }

    private void callPlaceAutocompleteActivityIntentForDeliveryAddress() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DELIVERY_ADDRESS);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    private void fetchItem() {

        textViewsQty = new ArrayList<TextView>();
        if (items.size() > 0) {

            for (int i = 0; i < items.size(); i++) {

                LayoutInflater inflater = getLayoutInflater();
                FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.list_item, linearLayoutItems, false);
                TextView name = (TextView) convertView.findViewById(R.id.item_name);
                TextView price = (TextView) convertView.findViewById(R.id.price);
                TextView description = (TextView) convertView.findViewById(R.id.item_description);
                final Button minus = (Button) convertView.findViewById(R.id.minus);
                final Button plus = (Button) convertView.findViewById(R.id.plus);
                textViewsQty.add((TextView) convertView.findViewById(R.id.qty));
                name.setText(String.valueOf(items.get(i).name));
                price.setText(String.valueOf(items.get(i).price));
                description.setText(String.valueOf(items.get(i).description));
                textViewsQty.get(i).setText(String.valueOf(items.get(i).qty));
                final int j = i;
                minus.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        minusQty(j);
                    }

                });

                plus.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        plusQty(j);
                    }
                });
                linearLayoutItems.addView(convertView);

            }
        }
        setRecapPrice();

    }

    public void minusQty(int i) {
        items.get(i).minOne();
        textViewsQty.get(i).setText(String.valueOf(items.get(i).qty));
        setRecapPrice();
    }

    public void plusQty(int i) {
        items.get(i).plusOne();
        textViewsQty.get(i).setText(String.valueOf(items.get(i).qty));
        setRecapPrice();
    }

    private void setRecapPrice()
    {
        textViewRecapPrice.setText(String.valueOf(restaurant.getRecapPrice()));
        textViewDeliveryFee.setText(order.getPriceString());
        totalPrice = restaurant.getRecapPrice()+order.price;
        textViewTotalPrice.setText(String.valueOf(totalPrice));
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("restaurant",restaurant);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

         if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DELIVERY_ADDRESS) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                order.setDropoffPlace(place);
                fillDeliveryAddress();



            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                textViewAddDeliveryAddress.setText(status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

    private void fillDeliveryAddress() {
        textViewAddDeliveryAddress.setText(order.dropoffAddress);
        new calculatePrice().execute();

    }

    private class calculatePrice extends AsyncTask<Void, Void, Void> {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            textViewDeliveryFee.setText("Please wait");
            textViewTotalPrice.setText("Please wait");
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
                    Toast.makeText(MakeOrderFoodActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MakeOrderFoodActivity.this, "Couldn't get json from server.", Toast.LENGTH_SHORT).show();

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            setRecapPrice();


            // Link to Register Screen
            buttonOrder.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    booking();

                }
            });

            // Dismiss the progress dialog

        }
    }

    private void booking() {
        Toast.makeText(this, "makan", Toast.LENGTH_SHORT).show();
        // Tag used to cancel the request
        String tag_string_req = "order_food";
        order.user = UserGlobal.getUser(getApplicationContext());

        order.pickupAddress = restaurant.address;
        order.pickupPosition = restaurant.location;
        order.totalPrice =  totalPrice;
        order.item = restaurant.getSelectedItem();



        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ORDER_FOOD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");

                    // Check for error node in json
                    if (status.contentEquals("1")) {

                        String idOrder=jObj.getString("id_order");

                        Intent i = new Intent(MakeOrderFoodActivity.this,
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
                params.put("total_price",String.valueOf(order.totalPrice));
                Gson gson = new Gson();
                // convert your list to json
                String itemjson = gson.toJson(order.item);
                params.put("item_json",itemjson);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}

