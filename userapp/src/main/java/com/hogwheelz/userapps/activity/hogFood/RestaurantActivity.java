package com.hogwheelz.userapps.activity.hogFood;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.NotifActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Item;
import com.hogwheelz.userapps.persistence.Menu;
import com.hogwheelz.userapps.persistence.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends NotifActivity {
    private String TAG = RestaurantActivity.class.getSimpleName();
    public Toolbar toolbar;
    LinearLayout linearLayoutSignatureDishes;
    LinearLayout linearLayoutMenu;
    TextView textViewRetaurantName;
    TextView textViewRestaurantAddress;
    TextView textViewOpenHour;
    TextView textViewRetaurantDetail;
    Restaurant restaurant;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);
        linearLayoutMenu = (LinearLayout) findViewById(R.id.list_menu);
        textViewRetaurantName = (TextView) findViewById(R.id.restaurant_name);
        textViewRestaurantAddress= (TextView) findViewById(R.id.restaurant_address);
        textViewOpenHour= (TextView) findViewById(R.id.restaurant_distance_and_open_hour);
        textViewRetaurantDetail=(TextView) findViewById(R.id.button_restaurant_detail);
        new fetchRestaurant().execute();



    }

    private class fetchRestaurant extends AsyncTask<Void, Void, Void> {
        @Override


        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.getRestaurantDetail();

            restaurant = new Restaurant();

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {

                    JSONObject restaurantJson = new JSONObject(jsonStr);

                    restaurant.idRestaurant= restaurantJson.getString("id_restaurant");
                    restaurant.name =  restaurantJson.getString("name");
                    restaurant.address =  restaurantJson.getString("address");
                    restaurant.location=new LatLng( restaurantJson.getDouble("latitude"), restaurantJson.getDouble("longitude"));
                    restaurant.photo= restaurantJson.getString("photo");
                    restaurant.openHour = restaurantJson.getString("open_hours");

                    JSONArray openHourArray = restaurantJson.getJSONArray("hours_detail");
                    if (openHourArray.length() > 0) {

                        for (int j = 0; j < openHourArray.length(); j++) {
                            try {

                                // Getting JSON Array node
                                restaurant.openHourComplete.add(j,openHourArray.getString(j));
                            } catch (JSONException e) {
                                Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                            }
                        }
                    }

                    JSONArray menuArray = restaurantJson.getJSONArray("menu");
                    if (menuArray.length() > 0) {

                        for (int i = 0; i < menuArray.length(); i++) {
                            try {

                                // Getting JSON Array node
                                JSONObject menuJson = menuArray.getJSONObject(i);

                                Menu menu = new Menu();
                                menu.idMenu = menuJson.getString("id_menu");
                                menu.name = menuJson.getString("menu");
                                JSONArray itemArray = menuJson.getJSONArray("item");
                                if (itemArray.length() > 0) {

                                    for (int j = 0; j < itemArray.length(); j++) {
                                        try {

                                            // Getting JSON Array node
                                            JSONObject itemJson = itemArray.getJSONObject(j);

                                            Item item = new Item();
                                            item.idItem = itemJson.getString("id_item");
                                            item.name = itemJson.getString("item_name");
                                            item.price = itemJson.getInt("price");
                                            item.photo = itemJson.getString("photo_item");
                                            item.description = itemJson.getString("description");
                                            menu.item.add(j,item);
                                        } catch (JSONException e) {
                                            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                        }
                                    }
                                }
                                restaurant.menu.add(i,menu);
                            } catch (JSONException e) {
                                Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                            }
                        }
                    }

                } catch (final JSONException e) {

                    Log.e(TAG, "Order Detail: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Json null");

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            setTextView();
        }
    }

    private void setTextView()
    {
        textViewRetaurantName.setText(restaurant.name);
        textViewRestaurantAddress.setText(restaurant.address);
        textViewOpenHour.setText(restaurant.openHour);
        textViewRetaurantDetail.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(RestaurantActivity.this, RestaurantDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("location", restaurant.location);
                bundle.putStringArrayList("open_hours_complete", (ArrayList<String>) restaurant.openHourComplete);
                bundle.putString("name", restaurant.name);
                bundle.putString("address", restaurant.address);
                bundle.putString("phone", restaurant.name);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        for(int i=0;i<restaurant.menu.size();i++)
        {
            final LayoutInflater inflater = getLayoutInflater();
            FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.list_menu, linearLayoutMenu, false);
            TextView name = (TextView) convertView.findViewById(R.id.menu_name);

            name.setText(String.valueOf(restaurant.menu.get(i).name));
            final List<Item> items =restaurant.menu.get(i).item;
            convertView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    // Start an alpha animation for clicked item
                    Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                    animation1.setDuration(800);
                    view.startAnimation(animation1);
                    Intent intent = new Intent(RestaurantActivity.this,ListItemActivity.class);
                    intent.putParcelableArrayListExtra("item", (ArrayList<? extends Parcelable>) items);
                    startActivity(intent);
                }

            });

            linearLayoutMenu.addView(convertView);
        }
        Log.e(TAG, "resto: " + restaurant.toString());
    }



    private void fetchMenuRestaurant() {

        // appending offset to url
        String url = AppConfig.getMenuURL();

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

                                    Item item = new Item();
                                    item.name = c.getString("nama");


                                    LayoutInflater inflater = getLayoutInflater();
                                    FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.list_menu, linearLayoutMenu, false);
                                    TextView name = (TextView) convertView.findViewById(R.id.menu_name);

                                    name.setText(String.valueOf(item.name));

                                    convertView.setOnClickListener(new View.OnClickListener() {

                                        public void onClick(View view) {
                                            // Start an alpha animation for clicked item
                                            Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                                            animation1.setDuration(800);
                                            view.startAnimation(animation1);
                                            Intent i = new Intent(RestaurantActivity.this,ListItemActivity.class);
                                            startActivity(i);
                                        }

                                    });

                                    linearLayoutMenu.addView(convertView);

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

                Toast.makeText(RestaurantActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });



        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
}
