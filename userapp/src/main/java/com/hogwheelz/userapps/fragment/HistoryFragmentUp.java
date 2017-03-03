package com.hogwheelz.userapps.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrder;
import com.hogwheelz.userapps.activity.adapter.HistorySwipeListAdapter;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.app.Config;
import com.hogwheelz.userapps.persistence.Order;
import com.hogwheelz.userapps.persistence.UserGlobal;
import com.hogwheelz.userapps.util.NotificationUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class HistoryFragmentUp extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{

    public ListView listView;
    public SwipeRefreshLayout swipeRefreshLayout;

    public HistorySwipeListAdapter adapter;

    public List<Order> orderList;
    public BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    fetchOrder();
                }
            }

        };
    }




    @Override
    public void onRefresh() {
        fetchOrder();

    }
    @Override
    public void onResume() {
        super.onResume();
        fetchOrder();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getActivity().getApplicationContext());
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();

    }

    public abstract void fetchOrder();



}
