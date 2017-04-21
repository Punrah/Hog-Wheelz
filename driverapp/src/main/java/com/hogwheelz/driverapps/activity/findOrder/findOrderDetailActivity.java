package com.hogwheelz.driverapps.activity.findOrder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.driverapps.activity.main.RootActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderFoodActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderRideActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderSendActivity;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.persistence.DriverGlobal;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class FindOrderDetailActivity extends RootActivity {
    private static final String TAG = FindOrderDetailActivity.class.getSimpleName();



    String idOrder;

    ImageView back;

    TextView textViewOrderType;
    TextView textViewPaymentType;
    TextView textViewCustomerName;
    TextView textViewDestination;
    TextView textViewDistance;
    TextView textViewOrderId;
    TextView textViewOrigin;
    TextView textViewPrice;
    TextView textViewNoteOrigin;
    TextView textViewNoteDestination;

    Button buttonAccept;
    Button buttonSkip;
    TextView buttonMaps;

    LinearLayout linearLayoutDetail;
    String orderType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_order);


         textViewCustomerName=(TextView)findViewById(R.id.customer_name);
         textViewDestination=(TextView)findViewById(R.id.destination);
         textViewDistance=(TextView)findViewById(R.id.distance);
         textViewOrderId=(TextView)findViewById(R.id.order_id);
         textViewOrigin=(TextView)findViewById(R.id.origin);
         textViewPrice=(TextView)findViewById(R.id.price);
         textViewNoteOrigin=(TextView)findViewById(R.id.note_origin);
         textViewNoteDestination=(TextView)findViewById(R.id.note_destination);
         textViewOrderType=(TextView)findViewById(R.id.order_type);
         textViewPaymentType=(TextView)findViewById(R.id.payment_type);

        linearLayoutDetail = (LinearLayout) findViewById(R.id.detail);

         buttonAccept = (Button) findViewById(R.id.button_accept);
         buttonSkip = (Button) findViewById(R.id.button_skip);
        buttonMaps =(TextView) findViewById(R.id.button_maps);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });

        idOrder= getIntent().getStringExtra("id_order");
        orderType = getIntent().getStringExtra("order_type");
        initializeOrder();
        getOrderDetail();

    }




    public abstract void initializeOrder();




    public abstract void getOrderDetail();


    public  class acceptOrder extends MyAsyncTask {

        @Override
        public Context getContext() {
            return FindOrderDetailActivity.this;
        }

        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return super.doInBackground(params);
        }

        public void postData()
        {

            String url = AppConfig.ACCEPT_ORDER;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("order_id",idOrder));
                nameValuePairs.add(new BasicNameValuePair("driver_id", DriverGlobal.getDriver(getApplicationContext()).idDriver));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        status=obj.getString("status");

                        if(status.contentEquals("1") )
                        {
                            isSucces=true;
                            smsg = obj.getString("msg");
                        }
                        else
                        {
                            emsg = obj.getString("msg");
                            //Toast.makeText(FindOrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();

                        }

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
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(FindOrderDetailActivity.this);

            builder.setTitle("Congratulations!");
            builder.setMessage(smsg);
            builder.setCancelable(false);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    if (orderType.contentEquals("1")) {
                        Intent i = new Intent(FindOrderDetailActivity.this, ViewOrderRideActivity.class);
                        i.putExtra("id_order",idOrder);
                        startActivity(i);
                        finish();
                    }
                    else if (orderType.contentEquals("2")) {
                        Intent i = new Intent(FindOrderDetailActivity.this, ViewOrderSendActivity.class);
                        i.putExtra("id_order",idOrder);
                        startActivity(i);
                        finish();
                    }
                    else if(orderType.contentEquals("3"))
                    {
                        Intent i = new Intent(FindOrderDetailActivity.this, ViewOrderFoodActivity.class);
                        i.putExtra("id_order",idOrder);
                        startActivity(i);
                        finish();
                    }
                }
            });



            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();

        }
    }



    public class skipOrder extends MyAsyncTask {


        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            postData();
            return super.doInBackground(params);
        }




        @Override
        public Context getContext() {
            return FindOrderDetailActivity.this;
        }

        @Override
        public void setSuccesPostExecute() {

        }

        public void postData() {
            // Create a new HttpClient and Post Header


            String url = AppConfig.SKIP_ORDER;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_order", idOrder));
                nameValuePairs.add(new BasicNameValuePair("id_driver", DriverGlobal.getDriver(getApplicationContext()).idDriver));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        status=obj.getString("status");
                        smsg = obj.getString("msg");

                    } catch (final JSONException e) {
                        emsg=e.getMessage();
                    }
                } else {
                    emsg="Couldn't get json from server";
                }

            } catch (IOException e) {
                emsg=e.getMessage();
            }
        }



    }




    public void setAllTextView()
    {
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new acceptOrder().execute();
            }
        });
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new skipOrder().execute();
            }
        });
    };

    public abstract void adjustCamera();





}
