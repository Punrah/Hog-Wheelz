package com.hogwheelz.driverapps.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.driverapps.activity.other.MessageActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderFoodActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderRideActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderSendActivity;
import com.hogwheelz.driverapps.adapter.MessageSwipeListAdapter;
import com.hogwheelz.driverapps.adapter.TransactionSwipeListAdapter;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.helper.MessageSQLiteHandler;
import com.hogwheelz.driverapps.persistence.DriverGlobal;
import com.hogwheelz.driverapps.persistence.Message;
import com.hogwheelz.driverapps.persistence.Transaction;

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
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class MessageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public ListView listView;
    public SwipeRefreshLayout swipeRefreshLayout;
    MessageSQLiteHandler db;

    public MessageSwipeListAdapter adapter;

    public List<Message> messageList;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = new MessageSQLiteHandler(getActivity().getApplicationContext());
        // Inflate the layout for this fragment
        View myInflater = inflater.inflate(R.layout.fragment_message, container, false);
        listView = (ListView) myInflater.findViewById(R.id.list_message);
        swipeRefreshLayout = (SwipeRefreshLayout) myInflater.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        new fetchMessage().execute();
        return myInflater;
    }

    @Override
    public void onRefresh() {
        new fetchMessage().execute();
    }


    private class fetchMessage extends MyAsyncTask {
        List<Message> messageListTemp;


        public void postData() {
            // Create a new HttpClient and Post Header

            messageListTemp = db.getMessageList();
            Collections.reverse(messageListTemp);
            isSucces=true;
        }


        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return null;
        }

        @Override
        public Context getContext() {
            return getActivity();
        }




        @Override
        public void setSuccesPostExecute() {


            for(int i=0;i<messageListTemp.size();i++)
            {
                messageList.add(messageListTemp.get(i));
            }

            adapter.notifyDataSetChanged();


        }

        @Override
        public void setPreloading() {
            messageList = new ArrayList<>();
            adapter = new MessageSwipeListAdapter(getActivity(), messageList);
            listView.setAdapter(adapter);

            // showing refresh animation before making http call
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void setPostLoading() {
            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Start an alpha animation for clicked item
                    Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                    animation1.setDuration(800);
                    view.startAnimation(animation1);

                    db.setRead(messageList.get(position).idMessage);
                    Intent i = new Intent(getActivity(), MessageActivity.class);
                    i.putExtra("id_message", messageList.get(position).idMessage);
                    i.putExtra("subject", messageList.get(position).subject);
                    i.putExtra("body", messageList.get(position).body);
                    i.putExtra("date", messageList.get(position).date);
                    startActivityForResult(i,1);
                }
            });



        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                new fetchMessage().execute();

            }

            }
    }
}
