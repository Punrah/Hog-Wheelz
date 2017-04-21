package com.hogwheelz.userapps.activity.hogpay;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.activity.adapter.TransactionSwipeListAdapter;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.fragment.HistoryProgressFragment;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Transaction;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TransactionActivity extends RootActivity implements SwipeRefreshLayout.OnRefreshListener{

    private String TAG = HistoryProgressFragment.class.getSimpleName();

    public ListView listView;
    public SwipeRefreshLayout swipeRefreshLayout;

    public TransactionSwipeListAdapter adapter;

    public List<Transaction> transactionList;
    ImageView back;
    TextView textViewBalance;
    String balance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        listView = (ListView) findViewById(R.id.list_transaction);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        textViewBalance = (TextView) findViewById(R.id.balance);

        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);

        new calculateBalance().execute();

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        fetchOrder();
                                    }
                                }
        );
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

            String url = AppConfig.getBalanceURL(UserGlobal.getUser(getApplicationContext()).idCustomer);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    balance = jsonObj.getString("saldo");

                } catch (final JSONException e) {
//                    Toast.makeText(getActivity(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                //Toast.makeText(getActivity(), "Couldn't get json from server.", Toast.LENGTH_SHORT).show();

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            textViewBalance.setText(Formater.getPrice(String.valueOf(balance)));


            // Dismiss the progress dialog

        }
    }



    public void fetchOrder() {
        new fetchOrder().execute();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }


    private class fetchOrder extends MyAsyncTask {
        JSONArray response;


        public void postData() {
            // Create a new HttpClient and Post Header


            String url = AppConfig.getTransactionURL();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);


            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_customer",UserGlobal.getUser(getApplicationContext()).idCustomer ));
              httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response1 = httpclient.execute(httppost);
                HttpEntity entity = response1.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {

                        response = new JSONArray(jsonStr);
                        if (response.length() > 0) {
                            isSucces = true;
                        }
                        else
                        {
                            emsg="Data null";
                        }

                    } catch (final JSONException e) {
                        emsg="Json parsing error: " + e.getMessage();
                    }
                } else {
                    emsg="Couldn't get json from server.";

                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                emsg=e.getMessage();
            }
        }


        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return null;
        }

        @Override
        public Context getContext() {
            return TransactionActivity.this;
        }

        @Override
        public void setMyPostExecute() {

                for (int i = 0; i < response.length(); i++) {
                    try {

                        // Getting JSON Array node
                        JSONObject c = response.getJSONObject(i);

                        Transaction transaction = new Transaction();
                        transaction.type = c.getString("jenis");
                        transaction.amount = c.getInt("jumlah");
                        transaction.date = c.getString("date");
                        transaction.info = c.getString("keterangan");

                        transactionList.add(0, transaction);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                    }
                }

                adapter.notifyDataSetChanged();


        }

        @Override
        public void setPreloading() {
            transactionList = new ArrayList<>();
            adapter = new TransactionSwipeListAdapter(TransactionActivity.this, transactionList);
            listView.setAdapter(adapter);

            // showing refresh animation before making http call
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void setPostLoading() {
            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);


        }
    }



}
