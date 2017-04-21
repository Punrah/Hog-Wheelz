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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.ImageAsyncTask;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.persistence.Restaurant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListRestaurantActivity extends RootActivity {
    private String TAG = ListRestaurantActivity.class.getSimpleName();
    private LinearLayout linearLayoutListRestaurant;
    public Toolbar toolbar;
    String category;
    String idCat;
    ImageView back;
    TextView textViewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_restaurant);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        back = (ImageView) findViewById(R.id.back);
        textViewCategory = (TextView) findViewById(R.id.category);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        linearLayoutListRestaurant = (LinearLayout) findViewById(R.id.linear_layout_list_restaurant);
        category=getIntent().getStringExtra("category");
        idCat=getIntent().getStringExtra("id_category");
        textViewCategory.setText(category);
        new fetchRestaurant().execute();
    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }


    private class fetchRestaurant extends MyAsyncTask {
        JSONArray data;




        @Override
        protected Void doInBackground(Void... params) {
            String url = AppConfig.getListRestaurantURL();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id_kategori_explore",idCat ));
               httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");
            if (jsonStr != null) {
                try {
                    data = new JSONArray(jsonStr);
                    isSucces=true;

                } catch (final JSONException e) {
                    emsg="Json parsing error: " + e.getMessage();
                }
            } else {
                emsg="Couldn't get json from server.";

            }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                emsg=e.getMessage();
            }
            return null;
        }



        @Override
        public Context getContext() {
            return ListRestaurantActivity.this;
        }

        @Override
        public void setMyPostExecute() {
            if (data.length() > 0) {

                for (int i = 0; i < data.length(); i++) {
                    try {

                        // Getting JSON Array node
                        JSONObject c = data.getJSONObject(i);

                        final Restaurant restaurant = new Restaurant();
                        restaurant.name = c.getString("name");
                        restaurant.address = c.getString("address");
                        restaurant.distance =c.getDouble("distance");
                        restaurant.idRestaurant=c.getString("id_restaurant");
                        restaurant.photo = c.getString("photo");


                        LayoutInflater inflater = getLayoutInflater();
                        FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.list_restaurant, linearLayoutListRestaurant, false);
                        TextView name = (TextView) convertView.findViewById(R.id.restaurant_name);
                        TextView address = (TextView) convertView.findViewById(R.id.restaurant_address);
                        TextView distance = (TextView) convertView.findViewById(R.id.restaurant_distance);
                        TextView openHour = (TextView) convertView.findViewById(R.id.open_hour);
                        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_restaurant);

                        name.setText(String.valueOf(restaurant.name));
                        address.setText(String.valueOf(restaurant.address));
                        distance.setText(Formater.getDistance(String.valueOf(restaurant.distance)));
                        openHour.setText(String.valueOf(restaurant.openHour));
                        imageView.setTag(restaurant.photo);
                        new ImageAsyncTask().execute(imageView);

                        convertView.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View view) {
                                // Start an alpha animation for clicked item
                                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                                animation1.setDuration(800);
                                view.startAnimation(animation1);
                                Intent i = new Intent(ListRestaurantActivity.this, RestaurantActivity.class);
                                i.putExtra("id_restaurant",restaurant.idRestaurant);
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
    }


}
