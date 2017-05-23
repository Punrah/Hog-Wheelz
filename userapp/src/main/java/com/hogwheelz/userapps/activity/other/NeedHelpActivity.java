package com.hogwheelz.userapps.activity.other;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.UserGlobal;

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

public class NeedHelpActivity extends RootActivity {

    EditText editTextMessage;
    TextView counter;
    ImageView submit;
    TextView call;
    int count=0;

    String code;

    String callCenter;
    ImageView back;
    String idOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_help);
        editTextMessage = (EditText) findViewById(R.id.message);
        counter = (TextView) findViewById(R.id.counter);
        submit = (ImageView) findViewById(R.id.submit);
        call = (TextView) findViewById(R.id.call);

        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });

        editTextMessage.addTextChangedListener(textWatcher);
        idOrder=getIntent().getStringExtra("id_order");
        new callCenter().execute();

    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + callCenter));
        startActivity(intent);

    }

    TextWatcher textWatcher = new TextWatcher() {
        int isi = 0;


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            isi =editTextMessage.getText().length();
            count=30-isi;
            counter.setText(String.valueOf(count));
            validateSubmit();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            isi =editTextMessage.getText().length();
            count=30-isi;
            counter.setText(String.valueOf(count));
            validateSubmit();
        }

        @Override
        public void afterTextChanged(Editable s) {
            isi =editTextMessage.getText().length();
            count=30-isi;
            counter.setText(String.valueOf(count));
            validateSubmit();

        }
    };

    public void validateSubmit()
    {
        if(count<0)
        {
            counter.setText("");
            submit.setBackgroundResource(R.color.colorAccent);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new uploadNeedHelp().execute();
                }
            });
        }
        else
        {
            submit.setBackgroundResource(R.color.softgray);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

    public class uploadNeedHelp extends MyAsyncTask {

        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return null;
        }



        @Override
        public Context getContext() {
            return NeedHelpActivity.this;
        }

        @Override
        public void setSuccessPostExecute() {
            setSuccessAlert();

        }

        @Override
        public void setFailPostExecute() {

        }

        public void postData() {
            // Create a new HttpClient and Post Header


            String url = AppConfig.UPLOAD_NEED_HELP;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_customer",UserGlobal.getUser(getApplicationContext()).idCustomer ));
                nameValuePairs.add(new BasicNameValuePair("id_order",idOrder));
                nameValuePairs.add(new BasicNameValuePair("message",editTextMessage.getText().toString()));
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
                            msg = obj.getString("msg");
                            //Toast.makeText(FindOrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();

                        }

                    } catch (final JSONException e) {
                        msg =e.getMessage();//Toast.makeText(FindOrderDetailActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    //Toast.makeText(FindOrderDetailActivity.this, "Couldn't get json from server", Toast.LENGTH_SHORT).show();
                    msg ="JSON NULL";
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                msg =e.getMessage();
            }
        }



    }


    private class callCenter extends MyAsyncTask {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog


        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            String url = AppConfig.URL_CALL_CENTER;

            String jsonStr = null;
            try {
                jsonStr = sh.makeServiceCall(url);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        callCenter = jsonObj.getString("nohp");
                        isSucces=true;

                    } catch (final JSONException e) {
                        badServerAlert();
                    }
                } else {
                    badServerAlert();
                }
            } catch (IOException e) {
                badInternetAlert();
            }

            return null;
        }


        @Override
        public Context getContext() {
            return NeedHelpActivity.this;
        }

        @Override
        public void setSuccessPostExecute() {
            call.setBackgroundResource(R.color.colorAccent);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setConditionCall();


                }
            });

        }

        @Override
        public void setFailPostExecute() {
            call.setBackgroundResource(R.color.softgray);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

        }
    }
}
