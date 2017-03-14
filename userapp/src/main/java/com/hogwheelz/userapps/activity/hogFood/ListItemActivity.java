package com.hogwheelz.userapps.activity.hogFood;

import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.NotifActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.persistence.Item;
import com.hogwheelz.userapps.persistence.OrderSend;
import com.hogwheelz.userapps.persistence.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListItemActivity extends NotifActivity {

    private String TAG = ListItemActivity.class.getSimpleName();

    Toolbar toolbar;
    LinearLayout linearLayoutItem;
    Restaurant restaurant;
    List<TextView> textViewsQty;
    int index;
    TextView textViewRecapPrice;
    Button buttonCheckout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Item");
        setSupportActionBar(toolbar);


linearLayoutItem = (LinearLayout) findViewById(R.id.list_item);
        textViewRecapPrice=(TextView) findViewById(R.id.price_recap);
        buttonCheckout = (Button) findViewById(R.id.checkout);

        restaurant = getIntent().getParcelableExtra("restaurant");
        index = getIntent().getIntExtra("index",0);
        fetchItem();

        buttonCheckout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(ListItemActivity.this,MakeOrderFoodActivity.class);
                intent.putExtra("restaurant",restaurant);
                startActivityForResult(intent,1);
            }
        });

    }

    private void fetchItem() {
        textViewsQty = new ArrayList<TextView>();
        linearLayoutItem.removeAllViews();

        if (restaurant.menu.get(index).item.size() > 0) {

            for (int i = 0; i < restaurant.menu.get(index).item.size(); i++) {
                LayoutInflater inflater = getLayoutInflater();
                FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.list_item, linearLayoutItem, false);
                TextView name = (TextView) convertView.findViewById(R.id.item_name);
                TextView price = (TextView) convertView.findViewById(R.id.price);
                TextView description = (TextView) convertView.findViewById(R.id.item_description);
                final Button minus = (Button) convertView.findViewById(R.id.minus);
                final Button plus = (Button) convertView.findViewById(R.id.plus);
                textViewsQty.add((TextView) convertView.findViewById(R.id.qty));
                name.setText(String.valueOf(restaurant.menu.get(index).item.get(i).name));
                price.setText(String.valueOf(restaurant.menu.get(index).item.get(i).price));
                description.setText(String.valueOf(restaurant.menu.get(index).item.get(i).description));
                textViewsQty.get(i).setText(String.valueOf(restaurant.menu.get(index).item.get(i).qty));
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
                linearLayoutItem.addView(convertView);

            }
        }
        setRecapPrice();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void minusQty(int i) {
        restaurant.menu.get(index).item.get(i).minOne();
        textViewsQty.get(i).setText(String.valueOf(restaurant.menu.get(index).item.get(i).qty));
        setRecapPrice();
    }

    public void plusQty(int i) {
        restaurant.menu.get(index).item.get(i).plusOne();
        textViewsQty.get(i).setText(String.valueOf(restaurant.menu.get(index).item.get(i).qty));
        setRecapPrice();
    }




    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("restaurant",restaurant);
        intent.putExtra("index",index);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }
    private void setRecapPrice()
    {
        textViewRecapPrice.setText(String.valueOf(restaurant.getRecapPrice()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                restaurant = data.getParcelableExtra("restaurant");
                fetchItem();
            }
        }
    }

}
