package com.hogwheelz.userapps.activity.hogFood;

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
import com.google.gson.Gson;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.NotifActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.persistence.Item;
import com.hogwheelz.userapps.persistence.OrderSend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ListItemActivity extends NotifActivity {

    private String TAG = ListItemActivity.class.getSimpleName();

    Toolbar toolbar;
    LinearLayout linearLayoutItem;
    List<Item> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Item");
        setSupportActionBar(toolbar);

        linearLayoutItem = (LinearLayout) findViewById(R.id.list_item);

        items = getIntent().getParcelableArrayListExtra("item");
        fetchItem();
    }

    private void fetchItem() {



                        if (items.size() > 0) {

                            for (int i = 0; i < items.size(); i++) {

                                    LayoutInflater inflater = getLayoutInflater();
                                    FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.list_item, linearLayoutItem, false);
                                    TextView name = (TextView) convertView.findViewById(R.id.item_name);
                                    TextView price = (TextView) convertView.findViewById(R.id.price);
                                    TextView description = (TextView) convertView.findViewById(R.id.item_description);
                                    name.setText(String.valueOf(items.get(i).name));
                                    price.setText(String.valueOf(items.get(i).price));
                                    description.setText(String.valueOf(items.get(i).description));

                                    convertView.setOnClickListener(new View.OnClickListener() {

                                        public void onClick(View view) {
                                            // Start an alpha animation for clicked item
                                            Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                                            animation1.setDuration(800);
                                            view.startAnimation(animation1);
                                            Toast.makeText(ListItemActivity.this, "makan", Toast.LENGTH_SHORT).show();
                                        }

                                    });

                                    linearLayoutItem.addView(convertView);

                            }
                        }

                    }




}
