package com.hogwheelz.driverapps.activity.findOrder;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.activity.other.MapsActivity;
import com.hogwheelz.driverapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.Formater;
import com.hogwheelz.driverapps.persistence.OrderSend;

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

public class FindOrderDetailSendActivity extends FindOrderDetailActivity {
    private static final String TAG = FindOrderDetailSendActivity.class.getSimpleName();
 LinearLayout convertView;
    boolean detailOpen=false;
    OrderSend order;
    Location mLastLocation;
    LinearLayout callerSender;
    LinearLayout callerReceiver;

    @Override
    public void initializeOrder() {
        order = new OrderSend();
    }

    @Override
    public void getOrderDetail() {
        new getOrderDetail().execute();
    }









    private void viewDetail()
    {
        linearLayoutDetail.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        convertView = (LinearLayout) inflater.inflate(R.layout.view_order_send_detail, linearLayoutDetail, false);
        TextView description = (TextView) convertView.findViewById(R.id.item_description);
        TextView sender_name = (TextView) convertView.findViewById(R.id.sender_name_send);
        TextView receiver_name = (TextView) convertView.findViewById(R.id.receiver_name_send);
        description.setText(String.valueOf(order.description));
        sender_name.setText(String.valueOf(order.senderName));
        receiver_name.setText(String.valueOf(order.receiverName));
        callerSender =(LinearLayout) convertView.findViewById(R.id.caller_sender);
        callerReceiver =(LinearLayout) convertView.findViewById(R.id.caller_receiver);

            callerSender.removeAllViews();
            callerReceiver.removeAllViews();

        linearLayoutDetail.addView(convertView);
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
            return FindOrderDetailSendActivity.this;
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
                            order.description = orderJson.getString("item_description");
                            order.senderName = orderJson.getString("sender_name");
                            order.senderPhone = orderJson.getString("sender_phone");
                            order.receiverName = orderJson.getString("receiver_name");
                            order.receiverPhone = orderJson.getString("receiver_phone");
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

        viewDetail();
        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FindOrderDetailSendActivity.this,MapsActivity.class);
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
