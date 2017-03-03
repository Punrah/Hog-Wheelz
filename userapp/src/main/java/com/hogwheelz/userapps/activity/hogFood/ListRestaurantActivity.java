package com.hogwheelz.userapps.activity.hogFood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.NotifActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.persistence.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListRestaurantActivity extends NotifActivity {
    private String TAG = ListRestaurantActivity.class.getSimpleName();
    private LinearLayout linearLayoutListRestaurant;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_restaurant);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Hogfood");
        setSupportActionBar(toolbar);
        linearLayoutListRestaurant = (LinearLayout) findViewById(R.id.linear_layout_list_restaurant);
        fetchRestaurant();
    }

    private void fetchRestaurant() {

        // appending offset to url
        String url = AppConfig.getListRestaurantURL();

        // Volley's json array request object
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if (response.length() > 0) {

                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    // Getting JSON Array node
                                    JSONObject c = response.getJSONObject(i);

                                    Restaurant restaurant = new Restaurant();
                                    restaurant.name = c.getString("name");
                                    restaurant.address = c.getString("address");
                                    restaurant.distance =c.getDouble("distance");


                                    LayoutInflater inflater = getLayoutInflater();
                                    FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.list_restaurant, linearLayoutListRestaurant, false);
                                    TextView name = (TextView) convertView.findViewById(R.id.restaurant_name);
                                    TextView address = (TextView) convertView.findViewById(R.id.restaurant_address);
                                    TextView distanceAndOpenHour = (TextView) convertView.findViewById(R.id.restaurant_distance_and_open_hour);

                                    name.setText(String.valueOf(restaurant.name));
                                    address.setText(String.valueOf(restaurant.address));
                                    distanceAndOpenHour.setText(String.valueOf(restaurant.distance+"  "+String.valueOf(restaurant.openHour)));

                                    convertView.setOnClickListener(new View.OnClickListener() {

                                        public void onClick(View view) {
                                            // Start an alpha animation for clicked item
                                            Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                                            animation1.setDuration(800);
                                            view.startAnimation(animation1);
                                            Intent i = new Intent(ListRestaurantActivity.this, RestaurantActivity.class);
                                            i.putExtra("id_restaurant","makan");
                                            startActivity(i);
                                        }

                                    });

                                    linearLayoutListRestaurant.addView(convertView);

                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

                Toast.makeText(ListRestaurantActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });



        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
}
