package com.hogwheelz.userapps.activity.hogFood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Item;
import com.hogwheelz.userapps.persistence.Menu;
import com.hogwheelz.userapps.persistence.OrderFood;
import com.hogwheelz.userapps.persistence.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RestaurantActivity extends RootActivity {
    private String TAG = RestaurantActivity.class.getSimpleName();
    public Toolbar toolbar;
    LinearLayout linearLayoutSignatureDishes;
    LinearLayout linearLayoutMenu;
    TextView textViewRetaurantName;
    TextView textViewRestaurantAddress;
    TextView textViewOpenHour;
    TextView textViewDistance;
    ImageView textViewRetaurantDetail;
    Restaurant restaurant;
    TextView textViewRecapPrice;
    TextView textViewRecapQty;
    ImageView buttonCheckout;
    ImageView back;

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
        textViewOpenHour= (TextView) findViewById(R.id.open_hour);
        textViewDistance= (TextView) findViewById(R.id.restaurant_distance);
        textViewRetaurantDetail=(ImageView) findViewById(R.id.button_restaurant_detail);
        textViewRecapPrice=(TextView) findViewById(R.id.price_recap);
        textViewRecapQty=(TextView) findViewById(R.id.order_qty);
        buttonCheckout= (ImageView) findViewById(R.id.checkout);
        back =(ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        idRestaurant=getIntent().getStringExtra("id_restaurant");
        new fetchRestaurant().execute();
orderFood = new OrderFood();





    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }

    private void setRecapPrice()
    {
        textViewRecapPrice.setText(Formater.getPrice(String.valueOf(orderFood.getRecapPrice())));
        textViewRecapQty.setText(String.valueOf(orderFood.getRecapQty()));
    }


    private class fetchRestaurant extends MyAsyncTask{

        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.getRestaurantDetail(idRestaurant);

            restaurant = new Restaurant();

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    isSucces=true;

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

                    emsg="Order Detail: " + e.getMessage();
                    Log.e(TAG, "Order Detail: " + e.getMessage());
                }
            } else {
                emsg = "Json Null";
                Log.e(TAG, "Json null");

            }
            return null;
        }


        @Override
        public Context getContext() {
            return RestaurantActivity.this;
        }

        @Override
        public void setMyPostExecute() {
            setTextView();
            setRecapPrice();
        }
    }

    private void setTextView()
    {
        textViewRetaurantName.setText(restaurant.name);
        textViewRestaurantAddress.setText(restaurant.address);
        textViewOpenHour.setText(restaurant.openHour);
        textViewDistance.setText(Formater.getDistance(String.valueOf(restaurant.distance)));
        textViewRetaurantDetail.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(RestaurantActivity.this, RestaurantDetailActivity.class);
                intent.putExtra("restaurant",restaurant);
                startActivity(intent);
            }
        });
        buttonCheckout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(orderFood.getRecapQty()>0) {
                    orderFood.menu = restaurant.menu;
                    orderFood.pickupPosition = restaurant.location;
                    Intent intent = new Intent(RestaurantActivity.this, MakeOrderFoodActivity.class);
                    intent.putExtra("order", orderFood);
                    startActivityForResult(intent, 1);
                }
                else
                {
                    Toast.makeText(RestaurantActivity.this, "No item ordered", Toast.LENGTH_SHORT).show();
                }
            }
        });
        linearLayoutMenu.removeAllViews();

        for(int i=0;i<restaurant.menu.size();i++)
        {

            final LayoutInflater inflater = getLayoutInflater();
            LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.list_menu, linearLayoutMenu, false);
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
        setRecapPrice();
        Log.e(TAG, "resto: " + restaurant.toString());
    }

    private void openListItem(int j)
    {
        orderFood.menu=restaurant.menu;
        Intent intent = new Intent(RestaurantActivity.this,ListItemActivity.class);
        intent.putExtra("order",orderFood);
        intent.putExtra("index",j);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                orderFood = data.getParcelableExtra("order");
                restaurant.menu = orderFood.menu;
                setTextView();
            }

        }
    }

}
