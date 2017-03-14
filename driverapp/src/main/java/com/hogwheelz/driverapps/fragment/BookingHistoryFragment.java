package com.hogwheelz.driverapps.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderFoodActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderRideActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderSendActivity;
import com.hogwheelz.driverapps.adapter.HistorySwipeListAdapter;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.AppController;
import com.hogwheelz.driverapps.persistence.DriverGlobal;
import com.hogwheelz.driverapps.persistence.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BookingHistoryFragment extends BookingFragmentUp  implements SwipeRefreshLayout.OnRefreshListener{

    private String TAG = ActiveBookingFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View myInflate=inflater.inflate(R.layout.fragment_history_completed, container, false);


        listView = (ListView) myInflate.findViewById(R.id.list_history_completed);
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
        String url = AppConfig.getBookingHistoryURL(DriverGlobal.getDriver(getActivity().getApplicationContext()).idDriver);

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

                if (orderList.get(position).orderType == 1) {
                    Intent i = new Intent(getActivity(), ViewOrderRideActivity.class);
                    i.putExtra("id_order", (String) orderList.get(position).id_order);
                    startActivity(i);
                }
                else if (orderList.get(position).orderType == 2) {
                    Intent i = new Intent(getActivity(), ViewOrderSendActivity.class);
                    i.putExtra("id_order", (String) orderList.get(position).id_order);
                    startActivity(i);
                }
                else if(orderList.get(position).orderType==3)
                {
                    Intent i = new Intent(getActivity(), ViewOrderFoodActivity.class);
                    i.putExtra("id_order", (String) orderList.get(position).id_order);
                    startActivity(i);
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }



}
