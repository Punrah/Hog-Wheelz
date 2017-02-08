package com.hogwheelz.driverapps.fragment;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.activity.MainActivity;
import com.hogwheelz.driverapps.activity.OrderActivity;
import com.hogwheelz.driverapps.persistence.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class BookingFragment extends Fragment   {
    private ListView listView;
    ArrayList<HashMap<String, Object>> orderList;
    private FirebaseDatabase friendsDatabase;
    private DatabaseReference friendsDatabaseReference;

    ProgressDialog progressDialog;

    public BookingFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         /*get instance of our friendsDatabase*/
        friendsDatabase = FirebaseDatabase.getInstance();
        /*get a reference to the friends node location*/
        friendsDatabaseReference = friendsDatabase.getReference("driver/"+ MainActivity.idDriver);

        addValueEventListener(friendsDatabaseReference);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflate=inflater.inflate(R.layout.fragment_booking, container, false);

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

    /*updates data in realtime, displays the data in a list*/
    private void addValueEventListener(final DatabaseReference friendsReference) {

        /*add ValueEventListener to update data in realtime*/
        ValueEventListener valueEventListener = friendsReference.addValueEventListener(new ValueEventListener() {
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
                    Order value = iterator.next().getValue(Order.class);
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

                if(BookingFragment.this.isVisible()) {
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
                                    OrderActivity.class);
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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            }
        });

    }





}
