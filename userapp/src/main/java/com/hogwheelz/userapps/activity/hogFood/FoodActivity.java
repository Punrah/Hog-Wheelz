package com.hogwheelz.userapps.activity.hogFood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.ImageAsyncTask;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Explore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FoodActivity extends RootActivity {

    private String TAG = FoodActivity.class.getSimpleName();

    ImageView back;
    private LinearLayout linearLayoutExploreGanjil;
    private LinearLayout linearLayoutExploreGenap;
    private LinearLayout linearLayout1;
    private LinearLayout linearLayout2;
    private LinearLayout linearLayout3;
    private LinearLayout linearLayout4;
    private LinearLayout linearLayout5;
    private LinearLayout linearLayout6;

    public Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        back = (ImageView) findViewById(R.id.back);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodActivity.super.onBackPressed();
            }
        });

        linearLayoutExploreGanjil =(LinearLayout) findViewById(R.id.explore_ganjil);
        linearLayoutExploreGenap =(LinearLayout) findViewById(R.id.explore_genap);

        linearLayout1 = (LinearLayout) findViewById(R.id.linear1);
        linearLayout2 = (LinearLayout) findViewById(R.id.linear2);
        linearLayout3 = (LinearLayout) findViewById(R.id.linear3);
        linearLayout4 = (LinearLayout) findViewById(R.id.linear4);
        linearLayout5 = (LinearLayout) findViewById(R.id.linear5);
        linearLayout6 = (LinearLayout) findViewById(R.id.linear6);


        linearLayout1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                view.startAnimation(animation1);
                Intent i = new Intent(FoodActivity.this, ListRestaurantActivity.class);
                i.putExtra("category","The One");
                startActivity(i);
            }

        });
        linearLayout2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                view.startAnimation(animation1);
                Intent i = new Intent(FoodActivity.this, ListRestaurantActivity.class);
                i.putExtra("category","The Two");
                startActivity(i);
            }

        });
        linearLayout3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                view.startAnimation(animation1);
                Intent i = new Intent(FoodActivity.this, ListRestaurantActivity.class);
                i.putExtra("category","The Three");
                startActivity(i);
            }

        });
        linearLayout4.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                view.startAnimation(animation1);
                Intent i = new Intent(FoodActivity.this, ListRestaurantActivity.class);
                i.putExtra("category","The Four");
                startActivity(i);
            }

        });
        linearLayout5.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                view.startAnimation(animation1);
                Intent i = new Intent(FoodActivity.this, ListRestaurantActivity.class);
                i.putExtra("category","The Five");
                startActivity(i);
            }

        });
        linearLayout6.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                view.startAnimation(animation1);
                Intent i = new Intent(FoodActivity.this, ListRestaurantActivity.class);
                i.putExtra("category","The Sex");
                startActivity(i);
            }

        });




        new fetchExplore().execute();
    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }


    private class fetchExplore extends MyAsyncTask {
        JSONArray response;




        @Override
        protected Void doInBackground(Void... params) {
            String url = AppConfig.getExploreURL();
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    response = new JSONArray(jsonStr);
                    isSucces=true;

                } catch (final JSONException e) {
                    emsg="Json parsing error: " + e.getMessage();
                }
            } else {
                emsg="Couldn't get json from server.";

            }
            return null;
        }

        @Override
        public void setMyPostExecute() {

        if (response.length() > 0) {

                    for (int i = 0; i < response.length(); i++) {
                        try {

                            // Getting JSON Array node
                            JSONObject c = response.getJSONObject(i);

                            Explore explore = new Explore();
                            explore.id = c.getString("id_kategori_explore");
                            explore.name = c.getString("name");
                            explore.photo = c.getString("foto");

                            if(i%2==0)
                            {
                                LayoutInflater inflater = getLayoutInflater();
                                FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.list_explore, linearLayoutExploreGenap, false);
                                TextView name = (TextView) convertView.findViewById(R.id.explore_name);
                                ImageView imageView = (ImageView) convertView.findViewById(R.id.img_explore) ;

                                name.setText(String.valueOf(explore.name));
                                imageView.setTag(explore.photo);
                                new ImageAsyncTask().execute(imageView);
                                final String catFood = String.valueOf(explore.name);
                                final String idCat = String.valueOf(explore.id);
                                convertView.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View view) {
                                        // Start an alpha animation for clicked item
                                        Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                                        animation1.setDuration(800);
                                        view.startAnimation(animation1);
                                        Intent i = new Intent(FoodActivity.this, ListRestaurantActivity.class);
                                        i.putExtra("category",catFood);
                                        i.putExtra("id_category",idCat);
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
                                ImageView imageView = (ImageView) convertView.findViewById(R.id.img_explore) ;

                                name.setText(String.valueOf(explore.name));
                                imageView.setTag(explore.photo);
                                new ImageAsyncTask().execute(imageView);
                                name.setText(String.valueOf(explore.name));
                                final String catFood = String.valueOf(explore.name);

                                convertView.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View view) {
                                        // Start an alpha animation for clicked item
                                        Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                                        animation1.setDuration(800);
                                        view.startAnimation(animation1);
                                        Intent i = new Intent(FoodActivity.this, ListRestaurantActivity.class);
                                        i.putExtra("category",catFood);
                                        startActivity(i);
                                    }

                                });

                                linearLayoutExploreGanjil.addView(convertView);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(FoodActivity.this, "JSON Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

        @Override
        public Context getContext() {
            return FoodActivity.this;
        }


    }
}
