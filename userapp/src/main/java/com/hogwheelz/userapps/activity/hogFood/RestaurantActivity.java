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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.NotifActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Item;
import com.hogwheelz.userapps.persistence.Menu;
import com.hogwheelz.userapps.persistence.OrderFood;
import com.hogwheelz.userapps.persistence.Restaurant;
import com.hogwheelz.userapps.persistence.UserGlobal;

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
    TextView textViewRecapPrice;
    Button buttonCheckout;
    String idRestaurant;

    OrderFood orderFood;

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
        textViewRecapPrice=(TextView) findViewById(R.id.price_recap);
        buttonCheckout= (Button) findViewById(R.id.checkout);

        idRestaurant=getIntent().getStringExtra("id_restaurant");
        new fetchRestaurant().execute();






    }

    private void setRecapPrice()
    {
        textViewRecapPrice.setText(String.valueOf(restaurant.getRecapPrice()));
    }


    private class fetchRestaurant extends AsyncTask<Void, Void, Void> {
        @Override


        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.getRestaurantDetail(idRestaurant);

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
            setRecapPrice();
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
                intent.putExtra("restaurant",restaurant);
                startActivity(intent);
            }
        });
        buttonCheckout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(RestaurantActivity.this,MakeOrderFoodActivity.class);
                intent.putExtra("restaurant",restaurant);
                startActivityForResult(intent,1);
            }
        });

        for(int i=0;i<restaurant.menu.size();i++)
        {
            final LayoutInflater inflater = getLayoutInflater();
            FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.list_menu, linearLayoutMenu, false);
            TextView name = (TextView) convertView.findViewById(R.id.menu_name);

            name.setText(String.valueOf(restaurant.menu.get(i).name));
            final List<Item> items =restaurant.menu.get(i).item;
            final int j=i;
            convertView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    // Start an alpha animation for clicked item
                    Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                    animation1.setDuration(800);
                    view.startAnimation(animation1);
                    openListItem(j);
                }

            });

            linearLayoutMenu.addView(convertView);
        }
        Log.e(TAG, "resto: " + restaurant.toString());
    }

    private void openListItem(int j)
    {
        Intent intent = new Intent(RestaurantActivity.this,ListItemActivity.class);
        intent.putExtra("restaurant",restaurant);
        intent.putExtra("index",j);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                restaurant = data.getParcelableExtra("restaurant");
                setRecapPrice();
            }
        }
    }

}
