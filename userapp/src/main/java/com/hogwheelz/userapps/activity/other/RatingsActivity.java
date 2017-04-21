package com.hogwheelz.userapps.activity.other;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.DriverImageAsyncTask;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.helper.SessionManager;
import com.hogwheelz.userapps.persistence.Order;

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

public class RatingsActivity extends AppCompatActivity {

    String idOrder;
    Order order;

    TextView name;
    TextView phone;
    TextView textViewIdOrder;
    TextView price;

    ImageView fotoDriver;

    int star;
    ImageView star1;
    ImageView star2;
    ImageView star3;
    ImageView star4;
    ImageView star5;

    int totalPrice;

    private SessionManager session;


    Button submit;

    EditText comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);
        idOrder =getIntent().getStringExtra("id_order");
        order = new Order();


         name=(TextView) findViewById(R.id.driver_name);
         phone = (TextView) findViewById(R.id.phone);
         textViewIdOrder=(TextView) findViewById(R.id.id_order);
         price = (TextView) findViewById(R.id.price);

         fotoDriver = (ImageView) findViewById(R.id.img_driver);

        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        star5 = (ImageView) findViewById(R.id.star5);

        submit = (Button) findViewById(R.id.button_submit);
        comment =(EditText) findViewById(R.id.message);

        session = new SessionManager(getApplicationContext());

        setStar(5);

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStar(1);
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStar(2);
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStar(3);
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStar(4);
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStar(5);
            }
        });

        new getOrderRideDetail().execute();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(star<3&&comment.getText().toString().equals(""))
                {
                    Toast.makeText(RatingsActivity.this, "Please write you comment", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    new uploadrating().execute();
                }
            }
        });


    }

    public  void  setStar(int count)
    {
        star=count;
        if(count==1)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.star_gray);
            star3.setImageResource(R.drawable.star_gray);
            star4.setImageResource(R.drawable.star_gray);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==2)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.yellow_star);
            star3.setImageResource(R.drawable.star_gray);
            star4.setImageResource(R.drawable.star_gray);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==3)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.yellow_star);
            star3.setImageResource(R.drawable.yellow_star);
            star4.setImageResource(R.drawable.star_gray);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==4)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.yellow_star);
            star3.setImageResource(R.drawable.yellow_star);
            star4.setImageResource(R.drawable.yellow_star);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==5)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.yellow_star);
            star3.setImageResource(R.drawable.yellow_star);
            star4.setImageResource(R.drawable.yellow_star);
            star5.setImageResource(R.drawable.yellow_star);
        }
    }


    private class getOrderRideDetail extends MyAsyncTask {
        @Override


        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.getOrderDetail(idOrder);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    isSucces=true;

                    JSONObject orderJson = new JSONObject(jsonStr);

                    order.id_order=idOrder;
                    order.driver.idDriver = orderJson.getString("id_driver");
                    if(!order.driver.idDriver.contentEquals("0")) {
                        order.driver.name = orderJson.getString("driver_name");
                        order.driver.plat = orderJson.getString("plat");
                        order.driver.phone = orderJson.getString("driver_phone");
                        order.driver.driverLocation = new LatLng(orderJson.getDouble("driver_lat"), orderJson.getDouble("driver_long"));
                        order.driver.photo = orderJson.getString("foto");
                    }
                    order.status = orderJson.getString("status_order");
                    order.dropoffAddress = orderJson.getString("destination_address");
                    order.pickupAddress=orderJson.getString("origin_address");
                    order.price=orderJson.getInt("price");
                    order.price=orderJson.getInt("price");
                    order.distance=orderJson.getDouble("distance");
                    order.pickupNote=orderJson.getString("note_from");
                    order.dropoffNote=orderJson.getString("note_to");
                    order.pickupPosition=new LatLng(orderJson.getDouble("lat_from"),orderJson.getDouble("long_from"));
                    order.dropoofPosition=new LatLng(orderJson.getDouble("lat_to"),orderJson.getDouble("long_to"));
                    order.vehicle = orderJson.getString("vehicle");
                    order.payment_type = orderJson.getString("payment_type");
                    order.orderType=orderJson.getInt("order_type");
                    if(order.orderType==3)
                    {
                        totalPrice = orderJson.getInt("total_price");
                    }



                } catch (final JSONException e) {
                    emsg="Order Detail: " + e.getMessage();
                }
            } else {
                emsg="json null";

            }
            return null;
        }


        @Override
        public Context getContext() {
            return RatingsActivity.this;
        }

        @Override
        public void setMyPostExecute() {

            name.setText(order.driver.name);
            fotoDriver.setTag(order.driver.photo);
            new DriverImageAsyncTask().execute(fotoDriver);
            phone.setText(order.driver.phone);
            textViewIdOrder.setText(order.id_order);
            if(order.orderType==3)
            {
                price.setText(Formater.getPrice(String.valueOf(totalPrice)));
            }
            else {
                price.setText(Formater.getPrice(order.getPriceString()));
            }

        }
    }

    public class uploadrating extends MyAsyncTask {

        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return null;
        }



        @Override
        public Context getContext() {
            return RatingsActivity.this;
        }

        @Override
        public void setMyPostExecute() {
            setAlert();
            session.deleteOrder();
            finish();

        }

        public void postData() {
            // Create a new HttpClient and Post Header


            String url = AppConfig.UPLOAD_RATING;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_order", idOrder ));
                nameValuePairs.add(new BasicNameValuePair("rating",String.valueOf(star)));
                nameValuePairs.add(new BasicNameValuePair("comment",comment.getText().toString()));
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



    }

    @Override
    public void onBackPressed() {

    }
}
