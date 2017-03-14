package com.hogwheelz.userapps.activity.ViewOrder;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Item;
import com.hogwheelz.userapps.persistence.OrderFood;
import com.hogwheelz.userapps.persistence.OrderSend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ViewOrderFood extends ViewOrder
        implements  GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = ViewOrderFood.class.getSimpleName();

    OrderFood order;

    private LinearLayout linearLayoutDetail;
    LinearLayout convertView;
    boolean detailOpen=false;
    Button buttonDetail;
    List<TextView> textViewsQty;
    @Override
    public void initializeOrder() {
        order=new OrderFood();
    }

    @Override
    public void adjustCamera()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        builder.include(pickUpMarker.getPosition());
        builder.include(dropOffMarker.getPosition());
        if(!order.driver.idDriver.contentEquals("0")) {
            builder.include(driverMarker.getPosition());
        }


        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.25); // offset from edges of the map 12% of screen


        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }

    public void setAllTextView()
    {
        textViewDriverName.setText(order.driver.name);
        textViewIdOrder.setText(order.id_order);
        textViewPrice.setText(order.getPriceString());
        textViewPickUpAddres.setText(order.pickupAddress);
        textViewDropOffAddress.setText(order.dropoffAddress);
        textViewStatus.setText(order.status);

        if(!order.pickupNote.equals(""))
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewPickupNote.setText(order.getPickupNoteString());
            textViewPickupNote.setLayoutParams(params);
            linearLayoutPickupNote.addView(textViewPickupNote);
        }

        if(!order.dropoffNote.equals(""))
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewDropoffNote.setText(order.getDropoffNoteString());
            textViewDropoffNote.setLayoutParams(params);
            linearLayoutDropoffNote.addView(textViewDropoffNote);
        }

        if(order.status.contentEquals("Cancel")||order.status.contentEquals("Complete"))
        {
            linearLayoutOrderInactive.setVisibility(View.VISIBLE);
            linearLayoutOrderActive.setVisibility(View.INVISIBLE);
        }
        else {
            linearLayoutOrderInactive.setVisibility(View.INVISIBLE);
            linearLayoutOrderActive.setVisibility(View.VISIBLE);
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelAcceptedOrder();
                }
            });
        }

        buttonDetail = (Button) findViewById(R.id.button_detail_send);
        buttonDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetailButton();
            }
        });



    }

    private void setDetailButton()
    {
        if(detailOpen)
        {
                    linearLayoutDetail.removeView(convertView);
                    detailOpen=false;
        }
        else
        {
                    viewDetail();
                    detailOpen=true;
        }

    }


    private void viewDetail()
    {
        linearLayoutDetail = (LinearLayout) findViewById(R.id.detail);
        LayoutInflater inflater = getLayoutInflater();
        convertView = (LinearLayout) inflater.inflate(R.layout.view_order_food_detail, linearLayoutDetail, false);

        LinearLayout linearLayoutItem = (LinearLayout) convertView.findViewById(R.id.list_item);
        textViewsQty = new ArrayList<TextView>();
        linearLayoutItem.removeAllViews();

        if (order.item.size() > 0) {

            for (int i = 0; i < order.item.size(); i++) {
                LayoutInflater inflater2 = getLayoutInflater();
                FrameLayout convertView = (FrameLayout) inflater2.inflate(R.layout.list_item_detail, linearLayoutItem, false);
                TextView name = (TextView) convertView.findViewById(R.id.item_name);
                TextView price = (TextView) convertView.findViewById(R.id.price);
                TextView description = (TextView) convertView.findViewById(R.id.item_description);
                textViewsQty.add((TextView) convertView.findViewById(R.id.qty));
                name.setText(String.valueOf(order.item.get(i).name));
                price.setText(String.valueOf(order.item.get(i).price));
                textViewsQty.get(i).setText(String.valueOf(order.item.get(i).qty));
                linearLayoutItem.addView(convertView);
            }
        }
        linearLayoutDetail.addView(convertView);
    }

    @Override
    public void getOrderDetail() {
        new getOrderSendDetail().execute();
    }


    private class getOrderSendDetail extends AsyncTask<Void, Void, Void> {
        @Override


        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.getOrderDetail(idOrder);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {

                    JSONObject orderJson = new JSONObject(jsonStr);

                    order.id_order=idOrder;
                    order.driver.idDriver = orderJson.getString("id_driver");
                    if(!order.driver.idDriver.contentEquals("0")) {
                        order.driver.name = orderJson.getString("driver_name");
                        order.driver.plat = orderJson.getString("plat");
                        order.driver.phone = orderJson.getString("driver_phone");
                        order.driver.driverLocation = new LatLng(orderJson.getDouble("driver_lat"), orderJson.getDouble("driver_long"));
                    }
                    order.status = orderJson.getString("status_order");
                    order.dropoffAddress = orderJson.getString("destination_address");
                    order.pickupAddress=orderJson.getString("origin_address");
                    order.price=orderJson.getInt("price");
                    order.pickupNote=orderJson.getString("note");
                    order.dropoffNote=orderJson.getString("note");
                    order.pickupPosition=new LatLng(orderJson.getDouble("lat_from"),orderJson.getDouble("long_from"));
                    order.dropoofPosition=new LatLng(orderJson.getDouble("lat_to"),orderJson.getDouble("long_to"));
                    JSONArray itemJArray = orderJson.getJSONArray("item");
                    for(int i=0;i<itemJArray.length();i++) {
                        JSONObject itemJson = itemJArray.getJSONObject(i);
                        Item item = new Item();
                        item.idItem=itemJson.getString("id_item");
                        item.name = itemJson.getString("item_name");
                        item.price = itemJson.getInt("price");
                        item.qty = itemJson.getInt("quantity");
                        order.item.add(item);
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

            pickUpMarker = mMap.addMarker(new MarkerOptions().position(order.pickupPosition));
            dropOffMarker= mMap.addMarker(new MarkerOptions().position(order.dropoofPosition));
            if(!order.driver.idDriver.contentEquals("0")) {
                driverMarker = mMap.addMarker(new MarkerOptions()
                        .position(order.driver.driverLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.motorcycle)));
            }
            setAllTextView();
            adjustCamera();

        }
    }


}
