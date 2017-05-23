package com.hogwheelz.userapps.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.other.FindDriverActivity;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderFood;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderRide;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderSend;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.activity.adapter.HistorySwipeListAdapter;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Order;
import com.hogwheelz.userapps.persistence.UserGlobal;
import android.view.animation.Animation;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class HistoryProgressFragment extends  HistoryFragmentUp  implements SwipeRefreshLayout.OnRefreshListener{
    private String TAG = HistoryProgressFragment.class.getSimpleName();

    View viewEmptyState;
    TextView textViewEmptyState;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myInflate=inflater.inflate(R.layout.fragment_history_progress, container, false);
        listView = (ListView) myInflate.findViewById(R.id.list_history_progress);
        viewEmptyState =  inflater.inflate(R.layout.empty_state_layout, null);
        textViewEmptyState = (TextView) viewEmptyState.findViewById(R.id.empty_state);
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

    @Override
    public void fetchOrder() {
        new fetchOrder().execute();
    }


    private class fetchOrder extends MyAsyncTask{
        JSONArray response;

        @Override
        protected Void doInBackground(Void... params) {

            String url = AppConfig.getHistoryProgressURL(UserGlobal.getUser(getActivity().getApplicationContext()).idCustomer);
            HttpHandler sh = new HttpHandler();


            String jsonStr = null;
            try {
                jsonStr = sh.makeServiceCall(url);
                if (jsonStr != null) {
                    try {
                        response = new JSONArray(jsonStr);
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
            return getActivity();
        }

        @Override
        public void setSuccessPostExecute() {
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
                        badServerAlert();
                    }
                }


                adapter.notifyDataSetChanged();
                listView.removeHeaderView(viewEmptyState);
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
            }
            else
            {
                textViewEmptyState.setText(getString(R.string.in_progress_empty_state));
                listView.removeHeaderView(viewEmptyState);
                listView.addHeaderView(viewEmptyState);
            }

        }

        @Override
        public void setFailPostExecute() {
            textViewEmptyState.setText(getString(R.string.in_progress_empty_state));
            listView.removeHeaderView(viewEmptyState);
            listView.addHeaderView(viewEmptyState);
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
            swipeRefreshLayout.setRefreshing(false);


        }
    }



}
