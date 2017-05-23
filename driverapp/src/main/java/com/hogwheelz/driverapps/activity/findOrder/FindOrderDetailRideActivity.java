package com.hogwheelz.driverapps.activity.findOrder;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.hogwheelz.driverapps.activity.other.MapsActivity;
import com.hogwheelz.driverapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.Formater;
import com.hogwheelz.driverapps.persistence.OrderRide;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FindOrderDetailRideActivity extends FindOrderDetailActivity {
    private static final String TAG = FindOrderDetailRideActivity.class.getSimpleName();
OrderRide order;

    Location mLastLocation;

    @Override
    public void initializeOrder() {
        order = new OrderRide();

    }

    @Override
    public void getOrderDetail() {
        new getOrderDetail().execute();
    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }


    public class getOrderDetail extends MyAsyncTask {

        @Override
        public Context getContext() {
            return FindOrderDetailRideActivity.this;
        }

        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return super.doInBackground(params);
        }

        public void postData()
        {

            String url = AppConfig.getOrderDetail(idOrder);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);

                            isSucces=true;

                            JSONObject orderJson = obj;



                            order.id_order=idOrder;
                            order.user.name= orderJson.getString("cus_name");
                            order.user.Phone=orderJson.getString("cus_phone");
                            order.status = orderJson.getString("status_order");
                            order.dropoffAddress = orderJson.getString("destination_address");
                            order.pickupAddress=orderJson.getString("origin_address");
                            order.price=orderJson.getInt("price");
                            order.distance=orderJson.getDouble("distance");
                            order.pickupNote=orderJson.getString("note_from");
                            order.dropoffNote=orderJson.getString("note_to");
                            order.pickupPosition=new LatLng(orderJson.getDouble("lat_from"),orderJson.getDouble("long_from"));
                            order.dropoofPosition=new LatLng(orderJson.getDouble("lat_to"),orderJson.getDouble("long_to"));
                            order.orderType = orderJson.getInt("order_type");
                            order.paymentType = orderJson.getString("payment_type");



                    } catch (final JSONException e) {
                        emsg=e.getMessage();//Toast.makeText(FindOrderDetailActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    //Toast.makeText(FindOrderDetailActivity.this, "Couldn't get json from server", Toast.LENGTH_SHORT).show();
                    emsg="JSON NULL";
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                emsg=e.getMessage();
            }

        }

        @Override
        public void setSuccesPostExecute() {
            setAllTextView();
            adjustCamera();

        }
    }



    public void setAllTextView()
    {
        textViewOrderType.setText(order.getOrderTypeString());
        textViewPaymentType.setText("BY "+order.paymentType.toUpperCase());
        textViewCustomerName.setText(order.user.name);
        textViewDestination.setText(order.dropoffAddress);
        textViewDistance.setText(Formater.getDistance(order.getDistanceString()));
        textViewOrderId.setText(order.id_order);
        textViewOrigin.setText(order.pickupAddress);
        textViewPrice.setText(Formater.getPrice(order.getPriceString()));
        textViewNoteOrigin.setText(order.pickupNote);
        textViewNoteDestination.setText(order.dropoffNote);

        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FindOrderDetailRideActivity.this,MapsActivity.class);
                i.putExtra("pick_up",order.pickupPosition);
                i.putExtra("drop_off",order.dropoofPosition);
                startActivity(i);
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getOrderDetail().execute();
            }
        });
        super.setAllTextView();
    }

    public void adjustCamera()
    {
    }







}
