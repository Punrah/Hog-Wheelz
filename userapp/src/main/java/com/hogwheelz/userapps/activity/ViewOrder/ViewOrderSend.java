package com.hogwheelz.userapps.activity.ViewOrder;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.hogwheelz.userapps.persistence.OrderSend;

import org.json.JSONException;
import org.json.JSONObject;


public class ViewOrderSend extends ViewOrder
        implements  GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = ViewOrderSend.class.getSimpleName();

    OrderSend order;

    private LinearLayout linearLayoutDetail;
    LinearLayout convertView;
    boolean detailOpen=false;
    Button buttonDetail;
    @Override
    public void initializeOrder() {
        order=new OrderSend();
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
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewPickupNote.setText(order.getPickupNoteString());
            textViewPickupNote.setLayoutParams(params);
            linearLayoutPickupNote.addView(textViewPickupNote);
        }

        if(!order.dropoffNote.equals(""))
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
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
        convertView = (LinearLayout) inflater.inflate(R.layout.view_order_send_detail, linearLayoutDetail, false);
        TextView description = (TextView) convertView.findViewById(R.id.item_description);
        TextView sender_name = (TextView) convertView.findViewById(R.id.sender_name_send);
        TextView sender_phone = (TextView) convertView.findViewById(R.id.sender_phone_send);
        TextView receiver_name = (TextView) convertView.findViewById(R.id.receiver_name_send);
        TextView receiver_phone = (TextView) convertView.findViewById(R.id.receiver_phone_send);
        description.setText(String.valueOf(order.description));
        sender_name.setText(String.valueOf(order.senderName));
        sender_phone.setText(String.valueOf(order.senderPhone));
        receiver_name.setText(String.valueOf(order.receiverName));
        receiver_phone.setText(String.valueOf(order.receiverPhone));

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
                    order.description = orderJson.getString("item_description");
                    order.senderName = orderJson.getString("sender_name");
                    order.senderPhone = orderJson.getString("sender_phone");
                    order.receiverName = orderJson.getString("receiver_name");
                    order.receiverPhone = orderJson.getString("receiver_phone");


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
