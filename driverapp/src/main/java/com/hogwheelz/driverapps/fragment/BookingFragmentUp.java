package com.hogwheelz.driverapps.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.hogwheelz.driverapps.adapter.HistorySwipeListAdapter;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.AppController;
import com.hogwheelz.driverapps.persistence.DriverGlobal;
import com.hogwheelz.driverapps.persistence.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class BookingFragmentUp extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{

    public ListView listView;
    public SwipeRefreshLayout swipeRefreshLayout;

    public HistorySwipeListAdapter adapter;

    public List<Order> orderList;



    @Override
    public void onRefresh() {
            fetchOrder();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchOrder();
    }

    public abstract void fetchOrder();



}
