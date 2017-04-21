package com.hogwheelz.userapps.activity.hogFood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.ImageAsyncTask;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.persistence.OrderFood;

import java.util.ArrayList;
import java.util.List;

public class ListItemActivity extends RootActivity {

    private String TAG = ListItemActivity.class.getSimpleName();

    Toolbar toolbar;
    LinearLayout linearLayoutItem;
    OrderFood orderFood;
    List<TextView> textViewsQty;
    int index;
    TextView textViewRecapPrice;

    TextView textViewRecapQty;
    ImageView buttonCheckout;
    ImageView back;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        linearLayoutItem = (LinearLayout) findViewById(R.id.list_item);
        textViewRecapPrice=(TextView) findViewById(R.id.price_recap);
        textViewRecapQty=(TextView) findViewById(R.id.order_qty);
        buttonCheckout = (ImageView) findViewById(R.id.checkout);

        orderFood = getIntent().getParcelableExtra("order");
        index = getIntent().getIntExtra("index",0);
        fetchItem();
        toolbar.setTitle(orderFood.menu.get(index).name);
        setSupportActionBar(toolbar);

        back = (ImageView) findViewById(R.id.back);

        buttonCheckout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(orderFood.getRecapQty()>0) {
                    Intent intent = new Intent(ListItemActivity.this,MakeOrderFoodActivity.class);
                    intent.putExtra("order",orderFood);
                    startActivityForResult(intent,1);
                }
                else
                {
                    Toast.makeText(ListItemActivity.this, "No item ordered", Toast.LENGTH_SHORT).show();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void fetchItem() {
        textViewsQty = new ArrayList<TextView>();
        linearLayoutItem.removeAllViews();

        if (orderFood.menu.get(index).item.size() > 0) {

            for (int i = 0; i < orderFood.menu.get(index).item.size(); i++) {
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.list_item, linearLayoutItem, false);
                TextView name = (TextView) convertView.findViewById(R.id.item_name);
                TextView price = (TextView) convertView.findViewById(R.id.price);
                TextView description = (TextView) convertView.findViewById(R.id.item_description);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.img_item);
                final ImageView minus = (ImageView) convertView.findViewById(R.id.minus);
                final ImageView plus = (ImageView) convertView.findViewById(R.id.plus);
                textViewsQty.add((TextView) convertView.findViewById(R.id.qty));
                name.setText(String.valueOf(orderFood.menu.get(index).item.get(i).name));
                price.setText(Formater.getPrice(String.valueOf(orderFood.menu.get(index).item.get(i).price)));
                description.setText(String.valueOf(orderFood.menu.get(index).item.get(i).description));
                imageView.setTag(orderFood.menu.get(index).item.get(i).photo);
                new ImageAsyncTask().execute(imageView);
                textViewsQty.get(i).setText(String.valueOf(orderFood.menu.get(index).item.get(i).qty));
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

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }

    public void minusQty(int i) {

        orderFood.menu.get(index).item.get(i).minOne();
        textViewsQty.get(i).setText(String.valueOf(orderFood.menu.get(index).item.get(i).qty));
        setRecapPrice();
    }

    public void plusQty(int i) {
        orderFood.menu.get(index).item.get(i).plusOne();
        textViewsQty.get(i).setText(String.valueOf(orderFood.menu.get(index).item.get(i).qty));
        setRecapPrice();
    }




    @Override
    public void onStart() {
        super.onStart();

    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("order",orderFood);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }
    private void setRecapPrice()
    {
        textViewRecapPrice.setText(Formater.getPrice(String.valueOf(orderFood.getRecapPrice())));
        textViewRecapQty.setText(String.valueOf(orderFood.getRecapQty()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                orderFood = data.getParcelableExtra("order");
                Intent intent = new Intent();
                intent.putExtra("order",orderFood);
                setResult(RESULT_OK,intent);
                finish();
            }
        }
    }

}
