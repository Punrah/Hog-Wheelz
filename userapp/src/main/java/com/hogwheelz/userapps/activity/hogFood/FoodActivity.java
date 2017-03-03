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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.NotifActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.persistence.Explore;
import com.hogwheelz.userapps.persistence.Item;
import com.hogwheelz.userapps.persistence.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FoodActivity extends NotifActivity {

    private String TAG = FoodActivity.class.getSimpleName();
    private LinearLayout linearLayoutExploreGanjil;
    private LinearLayout linearLayoutExploreGenap;
    private LinearLayout linearLayoutNearMe;
    private LinearLayout linearLayoutTopPicks;
    public Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Hogfood");
        setSupportActionBar(toolbar);

        linearLayoutExploreGanjil =(LinearLayout) findViewById(R.id.explore_ganjil);
        linearLayoutExploreGenap =(LinearLayout) findViewById(R.id.explore_genap);

        linearLayoutNearMe = (LinearLayout) findViewById(R.id.button_nearme);
        linearLayoutTopPicks = (LinearLayout) findViewById(R.id.button_top_picks);

        linearLayoutNearMe.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                view.startAnimation(animation1);
                Intent i = new Intent(FoodActivity.this, ListRestaurantActivity.class);
                startActivity(i);
            }

        });

        linearLayoutTopPicks.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                view.startAnimation(animation1);
                Intent i = new Intent(FoodActivity.this, ListRestaurantActivity.class);
                startActivity(i);
            }

        });

        fetchExplore();
    }

    private void fetchExplore() {

        // appending offset to url
        String url = AppConfig.getExploreURL();

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

                                    Explore explore = new Explore();
                                    explore.id = c.getString("id");
                                    explore.name = c.getString("nama");

                                    if(i%2==0)
                                    {
                                        LayoutInflater inflater = getLayoutInflater();
                                        FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.list_explore, linearLayoutExploreGenap, false);
                                        TextView name = (TextView) convertView.findViewById(R.id.explore_name);

                                        name.setText(String.valueOf(explore.name));

                                        convertView.setOnClickListener(new View.OnClickListener() {

                                            public void onClick(View view) {
                                                // Start an alpha animation for clicked item
                                                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                                                animation1.setDuration(800);
                                                view.startAnimation(animation1);
                                                Intent i = new Intent(FoodActivity.this, ListRestaurantActivity.class);
                                                startActivity(i);
                                            }

                                        });

                                        linearLayoutExploreGenap.addView(convertView);
                                    }
                                    else if(i%2==1)
                                    {
                                        LayoutInflater inflater = getLayoutInflater();
                                        FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.list_explore, linearLayoutExploreGanjil, false);
                                        TextView name = (TextView) convertView.findViewById(R.id.explore_name);

                                        name.setText(String.valueOf(explore.name));

                                        convertView.setOnClickListener(new View.OnClickListener() {

                                            public void onClick(View view) {
                                                // Start an alpha animation for clicked item
                                                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                                                animation1.setDuration(800);
                                                view.startAnimation(animation1);
                                                Intent i = new Intent(FoodActivity.this, ListRestaurantActivity.class);
                                                startActivity(i);
                                            }

                                        });

                                        linearLayoutExploreGanjil.addView(convertView);
                                    }

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

                Toast.makeText(FoodActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });



        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
}
