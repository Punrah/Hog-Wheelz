package com.hogwheelz.userapps.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.helper.HistoryProgressSwipeListAdapter;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Order;
import com.hogwheelz.userapps.persistence.UserGlobal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HistoryCompletedFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{

    private String TAG = HistoryProgressFragment.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    // URL to get contacts JSON
    private static String url;

    ArrayList<HashMap<String, String>> dataList;
    private HistoryProgressSwipeListAdapter adapter;

    private List<Order> orderList;

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

    @Override
    public void onRefresh() {
        fetchOrder();

    }




    private void fetchOrder() {
        orderList = new ArrayList<>();
        adapter = new HistoryProgressSwipeListAdapter(getActivity(), orderList);
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

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }



}
