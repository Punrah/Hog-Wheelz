package com.hogwheelz.userapps.activity.makeOrder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.app.AppConfig;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CancelReasonSearchDriverActivity extends RootActivity {
    private static final String TAG = CancelReasonSearchDriverActivity.class.getSimpleName();


    LinearLayout linearLayoutReason;
    List<String> reason;
    List<String> idReason;
    TextView butonCancel;
    String idOrder;
    String idReasonSelected;
    ImageView back;
    List<ImageView> check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_reason);
        linearLayoutReason = (LinearLayout) findViewById(R.id.list_reason);
        butonCancel = (TextView) findViewById(R.id.cancel_button);
        idOrder= getIntent().getStringExtra("id_order");
        new getReason().execute();

        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                CancelReasonSearchDriverActivity.super.onBackPressed();
            }
        });
        butonCancel.setBackgroundResource(R.color.softgray);
    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }



    public class getReason extends MyAsyncTask {



        protected Void doInBackground(Void... params) {

            postData();
            return null;
        }


        @Override
        public Context getContext() {
            return CancelReasonSearchDriverActivity.this;
        }

        @Override
        public void setSuccessPostExecute() {
            fetchItem();
        }

        @Override
        public void setFailPostExecute() {
            linearLayoutReason.removeAllViews();
        }

        public void postData() {
            // Create a new HttpClient and Post Header


            String url = AppConfig.GET_REASON;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                //nameValuePairs.add(new BasicNameValuePair("username", UserGlobal.getUser(getApplicationContext()).username));
                //              nameValuePairs.add(new BasicNameValuePair("password", UserGlobal.getUser(getApplicationContext()).password));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        isSucces=true;
                        JSONArray arrayReason = new JSONArray(jsonStr);

                        reason = new ArrayList<String>();
                        idReason = new ArrayList<String>();
                        for(int i=0;i<arrayReason.length();i++)
                        {

                            JSONObject reasonJson=arrayReason.getJSONObject(i);
                            reason.add(reasonJson.getString("cancel_reason"));
                            idReason.add(reasonJson.getString("id_cancel_reason"));
                        }

                    } catch (final JSONException e) {
                        msg="There’s a problem loading this screen";
                        alertType=DIALOG;
                    }
                } else {
                    msg="There’s a problem loading this screen";
                    alertType=DIALOG;
                }

            } catch (IOException e) {
                badInternetAlert();
            }
        }




    }



    private void fetchItem() {

        check = new ArrayList<ImageView>();
        if (reason.size()> 0) {

            for (int i = 0; i < reason.size(); i++) {
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.list_reason, linearLayoutReason, false);
                TextView textViewReason = (TextView) convertView.findViewById(R.id.reason);
                check.add((ImageView) convertView.findViewById(R.id.check));
                textViewReason.setText(reason.get(i));

                final int j=i;
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        idReasonSelected=idReason.get(j);
                        setCheck(j);
                        butonCancel.setBackgroundResource(R.color.green);
                        butonCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new cancelAcceptedOrder().execute();
                            }
                        });
                    }
                });
                linearLayoutReason.addView(convertView);

            }
        }

    }

    private  void setCheck(int index)
    {
        for (int i = 0; i < check.size(); i++) {
            check.get(i).setImageResource(0);
        }
        check.get(index).setImageResource(R.drawable.check);


    }


    public class cancelAcceptedOrder extends MyAsyncTask {



        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            postData();
            return null;
        }

        @Override
        public Context getContext() {
            return CancelReasonSearchDriverActivity.this;
        }

        @Override
        public void setSuccessPostExecute() {
            Intent intent = new Intent();
            setResult(RESULT_OK,intent);
            finish();
        }

        @Override
        public void setFailPostExecute() {
        }

        public void postData() {
            // Create a new HttpClient and Post Header


            String url = AppConfig.CANCEL_ACCEPTED_ORDER;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("id_order",idOrder));
                nameValuePairs.add(new BasicNameValuePair("id_reason",idReasonSelected));
               httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject jObj = new JSONObject(jsonStr);
                        String status = jObj.getString("status");

                        // Check for error node in json
                        if (status.contentEquals("1")) {
                            isSucces=true;
                        }
                        else if (status.contentEquals("2"))
                        {
                            msg="Your order was canceled, but we're unable to submit your cancelation reason.";
                            msgTitle="Unable to Submit Reason";
                            alertType=DIALOG_TITLE;
                        }
                        else {
                            badServerAlert();
                        }

                    } catch (final JSONException e) {
                        badServerAlert();
                    }
                } else {
                    badServerAlert();
                }

            } catch (IOException e) {
                badInternetAlert();
            }
        }



    }


}
