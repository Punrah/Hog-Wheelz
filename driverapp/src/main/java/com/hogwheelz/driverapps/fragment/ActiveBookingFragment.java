package com.hogwheelz.driverapps.fragment;

import android.content.Context;
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

import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderFoodActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderRideActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderSendActivity;
import com.hogwheelz.driverapps.adapter.HistorySwipeListAdapter;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.helper.HttpHandler;
import com.hogwheelz.driverapps.persistence.DriverGlobal;
import com.hogwheelz.driverapps.persistence.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ActiveBookingFragment extends BookingFragmentUp  implements SwipeRefreshLayout.OnRefreshListener{

    private String TAG = ActiveBookingFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
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
        new fetchOrder().execute();
    }

    private class fetchOrder extends MyAsyncTask {
        JSONArray response;

        @Override
        protected Void doInBackground(Void... params) {
            String url = AppConfig.getActiveBookingURL(DriverGlobal.getDriver(getActivity().getApplicationContext()).idDriver);
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    response = new JSONArray(jsonStr);
                    isSucces=true;

                } catch (final JSONException e) {
                    emsg="Json parsing error: " + e.getMessage();
                }
            } else {
                emsg="Couldn't get json from server.";

            }
            return null;
        }

        @Override
        public Context getContext() {
            return getActivity();
        }



        @Override
        public void setSuccesPostExecute() {
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

        }

        @Override
        public void setPreloading() {
            orderList = new ArrayList<>();
            adapter = new HistorySwipeListAdapter(getActivity(), orderList);
            listView.setAdapter(adapter);

            // showing refresh animation before making http call
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void setPostLoading() {
            // stopping swipe refresh
            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);

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

        }
    }



}
