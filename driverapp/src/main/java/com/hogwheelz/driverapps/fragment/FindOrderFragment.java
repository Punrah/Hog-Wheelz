package com.hogwheelz.driverapps.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.activity.FindOrderDetailActivity;
import com.hogwheelz.driverapps.activity.MainActivity;
import com.hogwheelz.driverapps.app.Config;
import com.hogwheelz.driverapps.persistence.DriverGlobal;
import com.hogwheelz.driverapps.persistence.OrderFirebase;
import com.hogwheelz.driverapps.util.NotificationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class FindOrderFragment extends Fragment   {
    private ListView listView;
    ArrayList<HashMap<String, Object>> orderList;
    private FirebaseDatabase friendsDatabase;
    private DatabaseReference friendsDatabaseReference;

    ProgressDialog progressDialog;

    private NotificationUtils notificationUtils;

    public FindOrderFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         /*get instance of our friendsDatabase*/
        friendsDatabase = FirebaseDatabase.getInstance();
        /*get a reference to the friends node location*/
        friendsDatabaseReference = friendsDatabase.getReference("driver/"+ DriverGlobal.getDriver(getActivity().getApplicationContext()).idDriver);
        //friendsDatabaseReference = friendsDatabase.getReference("driver/1480997953");//+DriverGlobal.getDriver(getActivity().getApplicationContext()).idDriver);

        addValueEventListener(friendsDatabaseReference);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflate=inflater.inflate(R.layout.fragment_find_order, container, false);

        listView = (ListView) myInflate.findViewById(R.id.listView);

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
                    data.put("distance", value.getDistance());
                    data.put("order_id", value.getOrderId());
                    data.put("order_type", value.getOrderType());
                    data.put("origin_address", value.getOriginAddress());
                    data.put("origin_lat", value.getOriginLat());
                    data.put("origin_lng", value.getOriginLng());
                    data.put("price", value.getPrice());
                    data.put("note", value.getNote());
                    orderList.add(data);
                }
                /*set up the adapter*/

                if(FindOrderFragment.this.isVisible()) {
                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(), orderList,
                            R.layout.list_order, new String[]{"order_id", "order_type", "price", "distance", "origin_address", "destination_address"}, new int[]{R.id.order_id, R.id.order_type, R.id.price, R.id.distance, R.id.origin, R.id.destination});

                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Start an alpha animation for clicked item
                            Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                            animation1.setDuration(800);
                            view.startAnimation(animation1);

                            Intent i = new Intent(getActivity(),
                                    FindOrderDetailActivity.class);
                            i.putExtra("destination_address", (String) orderList.get(position).get("destination_address"));
                            i.putExtra("destination_lat", (String) orderList.get(position).get("destination_lat"));
                            i.putExtra("destination_lng", (String) orderList.get(position).get("destination_lng"));
                            i.putExtra("distance", (String) orderList.get(position).get("distance"));
                            i.putExtra("order_id", (String) orderList.get(position).get("order_id"));
                            i.putExtra("order_type", (String) orderList.get(position).get("order_type"));
                            i.putExtra("origin_address", (String) orderList.get(position).get("origin_address"));
                            i.putExtra("origin_lat", (String) orderList.get(position).get("origin_lat"));
                            i.putExtra("origin_lng", (String) orderList.get(position).get("origin_lng"));
                            i.putExtra("price", (String) orderList.get(position).get("price"));
                            i.putExtra("note", (String) orderList.get(position).get("note"));
                            startActivity(i);
                        }
                    });
                }
                after=orderList.size();
                if(after>before)
                {
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
                    before=orderList.size();
                }
                else
                {
                    before=orderList.size();
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


}
