package com.hogwheelz.userapps.activity.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.activity.makeOrder.CancelReasonActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindDriverActivity extends RootActivity {
    private static final String TAG = FindDriverActivity.class.getSimpleName();

    private Button buttonCancel;

private TextView textViewIdOrder;
    private String idOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hog_book);

        buttonCancel = (Button) findViewById(R.id.button_cancel);
        textViewIdOrder= (TextView) findViewById(R.id.id_order);
        idOrder=getIntent().getStringExtra("id_order");
        textViewIdOrder.setText(idOrder);


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new cancelAcceptedOrder().execute();
            }
        });


    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }


    public class cancelAcceptedOrder extends MyAsyncTask {



        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            postData();
            return null;
        }

        @Override
        public Context getContext() {
            return FindDriverActivity.this;
        }

        @Override
        public void setSuccessPostExecute() {
            setAlertSuccessClose("Your order has been cancelled.");
        }

        @Override
        public void setFailPostExecute() {
        }

        public void postData() {
            // Create a new HttpClient and Post Header


            String url = AppConfig.CANCEL_ORDER;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id_order",idOrder));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                try {
                    JSONObject jObj = new JSONObject(jsonStr);
                    String status = jObj.getString("status");

                    // Check for error node in json
                    if (status.contentEquals("1")) {
                        isSucces=true;
                    }
                    else if (status.contentEquals("2"))
                    {
                        msgTitle="Unable to Cancel Order";
                        msg="Sorry, we're unable to cancel your order at this time. Please wait a moment and try again.";
                        alertType=DIALOG_TITLE;
                    }
                    else {
                        msgTitle="Unable to Cancel Order";
                        msg="Sorry, we're unable to cancel your order at this time. Please wait a moment and try again.";
                        alertType=DIALOG_TITLE;
                    }
                } catch (JSONException e) {
                    msgTitle="Unable to Cancel Order";
                    msg="Sorry, we're unable to cancel your order at this time. Please wait a moment and try again.";
                    alertType=DIALOG_TITLE;
                }

            } catch (IOException e) {
                badInternetAlert();
            }
        }



    }


}
