package com.hogwheelz.driverapps.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.activity.main.MainActivity;
import com.hogwheelz.driverapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.driverapps.activity.findOrder.FindOrderDetailFoodActivity;
import com.hogwheelz.driverapps.activity.findOrder.FindOrderDetailRideActivity;
import com.hogwheelz.driverapps.activity.findOrder.FindOrderDetailSendActivity;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.Config;
import com.hogwheelz.driverapps.app.Formater;
import com.hogwheelz.driverapps.helper.HttpHandler;
import com.hogwheelz.driverapps.persistence.DriverGlobal;
import com.hogwheelz.driverapps.persistence.OrderFirebase;
import com.hogwheelz.driverapps.service.LocationUpdateService;
import com.hogwheelz.driverapps.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;


public class FindOrderFragment extends Fragment   {

    private static final String TAG = FindOrderFragment.class.getSimpleName();
    private ListView listView;
    ArrayList<HashMap<String, Object>> orderList;
    private FirebaseDatabase friendsDatabase;
    private DatabaseReference friendsDatabaseReference;

    ProgressDialog prgDialog;

    private NotificationUtils notificationUtils;
    private Switch switchStatusOrder;
    private TextView textViewSwitch;
    private String statusOrder="";
    private int totalOrder=0;
    private int monthOrder=0;
    private int weekOrder=0;
    private int todayOrder=0;

    TextView textViewOrderTotal;
    TextView textViewOrderMonth;
    TextView textViewOrderWeek;
    TextView textViewOrderToday;



    public FindOrderFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflate=inflater.inflate(R.layout.fragment_find_order, container, false);

        listView = (ListView) myInflate.findViewById(R.id.listView);
        switchStatusOrder= (Switch) myInflate.findViewById(R.id.switch_status_order);
        textViewSwitch = (TextView) myInflate.findViewById(R.id.switch_status);
        textViewOrderTotal=(TextView) myInflate.findViewById(R.id.order_total);
        textViewOrderMonth=(TextView) myInflate.findViewById(R.id.order_month);
        textViewOrderWeek=(TextView) myInflate.findViewById(R.id.order_week);
        textViewOrderToday=(TextView) myInflate.findViewById(R.id.order_today);

        new getStatusOrder().execute();
        new getTotalOrder().execute();
        switchStatusOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new setStatusOrderON().execute();

                } else {
                    new setStatusOrderOff().execute();
                }
            }
        });
         /*get instance of our friendsDatabase*/
        friendsDatabase = FirebaseDatabase.getInstance();
        /*get a reference to the friends node location*/
        friendsDatabaseReference = friendsDatabase.getReference("driver/"+ DriverGlobal.getDriver(getActivity().getApplicationContext()).idDriver);
        //friendsDatabaseReference = friendsDatabase.getReference("driver/1480997953");//+DriverGlobal.getDriver(getActivity().getApplicationContext()).idDriver);



        addValueEventListener(friendsDatabaseReference);

        return myInflate;

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();
    }

    private Context contextFindOrder;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        contextFindOrder = activity;
    }

    static int before=0;
    static int after=0;

    /*updates data in realtime, displays the data in a list*/
    private void addValueEventListener(final DatabaseReference friendsReference) {

        /*add ValueEventListener to update data in realtime*/
        friendsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!NotificationUtils.isAppIsInBackground(contextFindOrder)) {
                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Config.ORDER_COMING);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(pushNotification);

                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils(contextFindOrder);
                    notificationUtils.playNotificationSound();
                } else {
                    // app is in background, show the notification in notification tray
                    Intent resultIntent = new Intent(contextFindOrder, MainActivity.class);
                    // check for image attachment
                    showNotificationMessage(contextFindOrder,"Alert", "Brace yourself, order is coming", "", resultIntent);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        friendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                orderList = new ArrayList<>();
                /*this is called when first passing the data and
                * then whenever the data is updated*/
                   /*get the data children*/

                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    /*get the values as a Order object*/
                    OrderFirebase value = iterator.next().getValue(OrderFirebase.class);
                    /*add the friend to the list for the adapter*/

                    HashMap<String, Object> data = new HashMap<>();

                    data.put("destination_address", value.getDestinationAddress());
                    data.put("destination_lat", value.getDestinationLat());
                    data.put("destination_lng", value.getDestinationLng());
                    data.put("distance", "("+Formater.getDistance(String.valueOf(value.getDistance()))+")");
                    data.put("order_id", value.getOrderId());
                    if(value.getOrderType().contentEquals("1"))
                    {
                        data.put("order_type", "RIDE");
                    }
                    else if(value.getOrderType().contentEquals("2"))
                    {
                        data.put("order_type", "SEND");
                    }
                    else if(value.getOrderType().contentEquals("3"))
                    {
                        data.put("order_type", "FOOD");
                    }
                    data.put("origin_address", value.getOriginAddress());
                    data.put("origin_lat", value.getOriginLat());
                    data.put("origin_lng", value.getOriginLng());
                    data.put("price", Formater.getPrice(String.valueOf(value.getPrice())));
                    data.put("note", value.getNote());
                    orderList.add(data);
                }
                /*set up the adapter*/
                Collections.reverse(orderList);

                if(FindOrderFragment.this.isVisible()) {
                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(), orderList,
                            R.layout.list_order, new String[]{ "order_type","price", "distance", "origin_address", "destination_address"}, new int[]{ R.id.order_type, R.id.price, R.id.distance, R.id.origin, R.id.destination});

                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Start an alpha animation for clicked item
                            Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                            animation1.setDuration(800);
                            view.startAnimation(animation1);

                            if (orderList.get(position).get("order_type").equals("RIDE")) {
                                Intent i = new Intent(getActivity(), FindOrderDetailRideActivity.class);
                                i.putExtra("id_order", (String) orderList.get(position).get("order_id"));
                                i.putExtra("order_type", "1");
                                startActivity(i);
                            }
                            else if (orderList.get(position).get("order_type").equals("SEND")){
                                Intent i = new Intent(getActivity(), FindOrderDetailSendActivity.class);
                                i.putExtra("id_order", (String) orderList.get(position).get("order_id"));
                                i.putExtra("order_type", "2");
                                startActivity(i);
                            }
                            else if(orderList.get(position).get("order_type").equals("FOOD")) {
                                Intent i = new Intent(getActivity(), FindOrderDetailFoodActivity.class);
                                i.putExtra("id_order", (String) orderList.get(position).get("order_id"));
                                i.putExtra("order_type", "3");
                                startActivity(i);
                            }

                        }
                    });
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            }
        });

    }



    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    private void startLocationServices()
    {
        Intent intent = new Intent(getActivity(), LocationUpdateService.class);
        intent.putExtra("id_driver", DriverGlobal.getDriver(getActivity().getApplicationContext()).idDriver);
        getActivity().startService(intent);
    }

    protected void stopService() {
        getActivity().stopService(new Intent(getActivity(), LocationUpdateService.class));
        super.onStop();

    }


    private class getStatusOrder extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.getStatusOrder(DriverGlobal.getDriver(getActivity().getApplicationContext()).idDriver);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {

                    JSONObject orderJson = new JSONObject(jsonStr);

                    String status=orderJson.getString("status");
                    String msg=orderJson.getString("msg");
                    if(status.contentEquals("1"))
                    {
                        statusOrder = orderJson.getString("status_order");
                    }
                    else
                    {
                        Toast.makeText(contextFindOrder, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (final JSONException e) {

                    Log.e(TAG, "Order Detail: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Json null");

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(statusOrder.contentEquals("active"))
            {
                setSwitchOn();
            }
            else if (statusOrder.contentEquals("inactive"))
            {
                setSwitchOff();
            }

        }
    }

    private class getTotalOrder extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.getTotalOrder(DriverGlobal.getDriver(getActivity().getApplicationContext()).idDriver);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {

                    JSONObject orderJson = new JSONObject(jsonStr);

                    String status=orderJson.getString("status");
                    String msg=orderJson.getString("msg");
                    if(status.contentEquals("1"))
                    {
                        totalOrder = orderJson.getInt("total_order");
                        monthOrder = orderJson.getInt("total_order_month");
                        weekOrder = orderJson.getInt("total_order_week");
                        todayOrder = orderJson.getInt("total_order_today");
                    }
                    else
                    {
                        Toast.makeText(contextFindOrder, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (final JSONException e) {

                    Log.e(TAG, "Order Detail: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Json null");

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            textViewOrderTotal.setText(String.valueOf(totalOrder));
            textViewOrderMonth.setText(String.valueOf(monthOrder));
            textViewOrderWeek.setText(String.valueOf(weekOrder));
            textViewOrderToday.setText(String.valueOf(todayOrder));


        }
    }

    private class setStatusOrderON extends MyAsyncTask {


        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.setStatusOrderOn(DriverGlobal.getDriver(getActivity().getApplicationContext()).idDriver);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {

                    JSONObject orderJson = new JSONObject(jsonStr);

                    String status=orderJson.getString("status");
                    String msg=orderJson.getString("msg");
                    if(status.contentEquals("1"))
                    {
                        isSucces=true;
                        smsg = msg;
                    }
                    else
                    {
                        emsg = msg;
                    }
                } catch (final JSONException e) {

                    emsg = e.getMessage();
                }
            } else {
                emsg = "Json null";

            }
            return null;
        }

        @Override
        public Context getContext() {
            return getActivity();
        }

        @Override
        public void setSuccesPostExecute() {
            new getStatusOrder().execute();
        }


    }


    private class setStatusOrderOff extends MyAsyncTask {


        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.setStatusOrderOff(DriverGlobal.getDriver(getActivity().getApplicationContext()).idDriver);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {

                    JSONObject orderJson = new JSONObject(jsonStr);

                    String status=orderJson.getString("status");
                    String msg=orderJson.getString("msg");
                    if(status.contentEquals("1"))
                    {
                        isSucces=true;
                        smsg = msg;
                    }
                    else
                    {
                        emsg=msg;
                    }


                } catch (final JSONException e) {

                    emsg= e.getMessage();
                }
            } else {
                emsg="Json null";
            }
            return null;
        }

        @Override
        public Context getContext() {
            return getActivity();
        }

        @Override
        public void setSuccesPostExecute() {
            new getStatusOrder().execute();
        }

    }
    private void setSwitchOn()
    {
        textViewSwitch.setText("Status Order: ON");
        switchStatusOrder.setChecked(true);
        startLocationServices();
    }

    private void setSwitchOff()
    {
        textViewSwitch.setText("Status Order: OFF");
        switchStatusOrder.setChecked(false);
        stopService();
    }






}
