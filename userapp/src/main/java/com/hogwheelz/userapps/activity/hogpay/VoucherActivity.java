package com.hogwheelz.userapps.activity.hogpay;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.activity.other.HelpActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
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

public class VoucherActivity extends RootActivity {
    TextView textViewBalance;
    EditText voucher;
    TextView buttonRedeem;
    String balance;
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        back = (ImageView) findViewById(R.id.back);
        voucher = (EditText) findViewById(R.id.voucher);
        buttonRedeem = (TextView) findViewById(R.id.redeem);
        textViewBalance = (TextView) findViewById(R.id.balance);



        voucher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(voucher.getText().equals(null))
                {
                    buttonRedeem.setBackgroundResource(R.color.softgray);
                    buttonRedeem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                }
                else {
                    buttonRedeem.setBackgroundResource(R.color.colorAccent);
                    buttonRedeem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new uploadVoucher().execute();
                        }
                    });
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(voucher.getText().equals(null))
                {
                    buttonRedeem.setBackgroundResource(R.color.softgray);
                    buttonRedeem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                }
                else {
                    buttonRedeem.setBackgroundResource(R.color.colorAccent);
                    buttonRedeem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new uploadVoucher().execute();
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(voucher.getText().equals(null))
                {
                    buttonRedeem.setBackgroundResource(R.color.softgray);
                    buttonRedeem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                }
                else {
                    buttonRedeem.setBackgroundResource(R.color.colorAccent);
                    buttonRedeem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new uploadVoucher().execute();
                        }
                    });
                }

            }
        });
        new calculateBalance().execute();
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });



    }



    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }


    private class calculateBalance extends AsyncTask<Void, Void, Void> {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            textViewBalance.setText("Please wait");

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            String url = AppConfig.getBalanceURL(UserGlobal.getUser(VoucherActivity.this).idCustomer);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    balance = jsonObj.getString("saldo");

                } catch (final JSONException e) {
                    Toast.makeText(VoucherActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(VoucherActivity.this, "Couldn't get json from server.", Toast.LENGTH_SHORT).show();

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            UserGlobal.balance=Double.parseDouble(balance);
            textViewBalance.setText(Formater.getPrice(balance));


            // Dismiss the progress dialog

        }
    }

    public class uploadVoucher extends MyAsyncTask {

        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return null;
        }



        @Override
        public Context getContext() {
            return VoucherActivity.this;
        }

        @Override
        public void setMyPostExecute() {
            setAlert();

        }

        public void postData() {
            // Create a new HttpClient and Post Header


            String url = AppConfig.UPLOAD_VOUCHER;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_customer",UserGlobal.getUser(getApplicationContext()).idCustomer ));
                nameValuePairs.add(new BasicNameValuePair("voucher",voucher.getText().toString()));
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

}
