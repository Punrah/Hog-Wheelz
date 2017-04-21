package com.hogwheelz.userapps.activity.makeOrder;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CancelReasonActivity extends RootActivity {
    private static final String TAG = CancelReasonActivity.class.getSimpleName();


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
        new skipOrder().execute();

        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                CancelReasonActivity.super.onBackPressed();
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


    public class skipOrder extends AsyncTask<String, Integer, Double> {
        String status;
        Boolean sukses=false;


        @Override
        protected Double doInBackground(String... params) {
            // TODO Auto-generated method stub
            postData();
            return null;
        }

        protected void onPostExecute(Double result){

            fetchItem();

            super.onPostExecute(result);
        }



        protected void onProgressUpdate(Integer... progress){
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
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
                        //Toast.makeText(FindOrderDetailActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    //Toast.makeText(FindOrderDetailActivity.this, "Couldn't get json from server", Toast.LENGTH_SHORT).show();

                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
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
                                cancelAcceptedOrder();
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

    public void cancelAcceptedOrder() {
        // Tag used to cancel the request
        String tag_string_req = "cancel_accepted_order";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.CANCEL_ACCEPTED_ORDER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");

                    // Check for error node in json
                    if (status.contentEquals("1")) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                        Toast.makeText(CancelReasonActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    else if (status.contentEquals("2"))
                    {
                        Toast.makeText(CancelReasonActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // Error in login. Get the error message

                        String errorMsg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_order",idOrder);
                params.put("id_reason",idReasonSelected);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
