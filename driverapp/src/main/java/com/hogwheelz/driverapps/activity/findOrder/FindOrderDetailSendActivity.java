package com.hogwheelz.driverapps.activity.findOrder;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.AppController;
import com.hogwheelz.driverapps.helper.HttpHandler;
import com.hogwheelz.driverapps.persistence.OrderSend;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FindOrderDetailSendActivity extends FindOrderDetailActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = FindOrderDetailSendActivity.class.getSimpleName();
 LinearLayout convertView;
    boolean detailOpen=false;
    OrderSend order;

    @Override
    public void initializeOrder() {
        order = new OrderSend();
    }

    @Override
    public void getOrderDetail() {
        new getOrderDetail().execute();
    }

    @Override
    public void acceptOrder() {

    }


    public void setDetail() {

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


    private class getOrderDetail extends AsyncTask<Void, Void, Void> {
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
                    order.user.name= orderJson.getString("cus_name");
                    order.user.Phone=orderJson.getString("cus_phone");
                    order.status = orderJson.getString("status_order");
                    order.dropoffAddress = orderJson.getString("destination_address");
                    order.pickupAddress=orderJson.getString("origin_address");
                    order.price=orderJson.getInt("price");
                    order.distance=orderJson.getDouble("distance");
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
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            double lat = mLastLocation.getLatitude();
            double lng = mLastLocation.getLongitude();
            driverMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.motorcycle)));
            setAllTextView();
            adjustCamera();

        }
    }

    public void setAllTextView()
    {
        textViewCustomerName.setText(order.user.name);
        textViewDestination.setText(order.dropoffAddress);
        textViewDistance.setText(order.getDistanceString());
        textViewOrderId.setText(order.id_order);
        textViewOrigin.setText(order.pickupAddress);
        textViewPrice.setText(order.getPriceString());
        textViewNoteOrigin.setText(order.pickupNote);
        textViewNoteDestination.setText(order.dropoffNote);

        setDetail();
        super.setAllTextView();
    }

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
        int padding = (int) (width * 0.1); // offset from edges of the map 12% of screen


        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }


}
