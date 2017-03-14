package com.hogwheelz.userapps.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.FindDriverActivity;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrder;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderFood;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderRide;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderSend;
import com.hogwheelz.userapps.activity.makeOrder.MakeOrderRideActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.activity.adapter.HistorySwipeListAdapter;
import com.hogwheelz.userapps.app.Config;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Order;
import com.hogwheelz.userapps.persistence.UserGlobal;
import android.view.animation.Animation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class HistoryProgressFragment extends  HistoryFragmentUp  implements SwipeRefreshLayout.OnRefreshListener{
    private String TAG = HistoryProgressFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myInflate=inflater.inflate(R.layout.fragment_history_progress, container, false);
        listView = (ListView) myInflate.findViewById(R.id.list_history_progress);
        swipeRefreshLayout = (SwipeRefreshLayout) myInflate.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

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
        return myInflate;
    }

     public void fetchOrder() {
        orderList = new ArrayList<>();
        adapter = new HistorySwipeListAdapter(getActivity(), orderList);
        listView.setAdapter(adapter);

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // appending offset to url
        String url = AppConfig.getHistoryProgressURL(UserGlobal.getUser(getActivity().getApplicationContext()).idCustomer);

        // Volley's json array request object
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        if (response.length() > 0) {

                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    // Getting JSON Array node
                                    JSONObject c = response.getJSONObject(i);

                                    Order order = new Order();
                                    order.id_order = c.getString("id_order");
                                    order.dropoffAddress = c.getString("address");
                                    order.status = c.getString("status");
                                    order.orderDate = c.getString("order_date");
                                    order.orderType = c.getInt("order_type");

                                    orderList.add(0, order);
                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                                }
                            }

                            adapter.notifyDataSetChanged();
                        }

                        // stopping swipe refresh
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());

                Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                view.startAnimation(animation1);

                if (orderList.get(position).status.contentEquals("order")) {

                    Intent i = new Intent(getActivity(), FindDriverActivity.class);
                    i.putExtra("id_order", (String) orderList.get(position).id_order);
                    startActivity(i);
                } else {
                    if (orderList.get(position).orderType == 1) {
                        Intent i = new Intent(getActivity(), ViewOrderRide.class);
                        i.putExtra("id_order", (String) orderList.get(position).id_order);
                        startActivity(i);
                    }
                    else if (orderList.get(position).orderType == 2) {
                        Intent i = new Intent(getActivity(), ViewOrderSend.class);
                        i.putExtra("id_order", (String) orderList.get(position).id_order);
                        startActivity(i);
                    }
                    else if(orderList.get(position).orderType==3) {
                    {
                        Intent i = new Intent(getActivity(), ViewOrderFood.class);
                        i.putExtra("id_order", (String) orderList.get(position).id_order);
                        startActivity(i);
                    }

                }



                }
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }



}
