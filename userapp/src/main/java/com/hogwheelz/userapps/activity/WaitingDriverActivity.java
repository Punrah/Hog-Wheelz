package com.hogwheelz.userapps.activity;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.OrderRide;

import org.json.JSONException;
import org.json.JSONObject;


public class WaitingDriverActivity extends AppCompatActivity
        implements  GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = WaitingDriverActivity.class.getSimpleName();



    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    double lat =0, lng=0;

    View mapView;

    TextView textViewPickUpAddres;
    TextView textViewDropOffAddress;
    TextView textViewDriverName;
    TextView textViewIdOrder;
    TextView textViewPrice;

    LinearLayout linearLayoutPickupNote;
    LinearLayout linearLayoutDropoffNote;

    TextView textViewPickupNote;
    TextView textViewDropoffNote;


    Marker pickUpMarker;
    Marker dropOffMarker;
    Marker driverMarker;

    Double driverLocationLat;
    Double driverLocationLng;

    OrderRide orderRide;
    String idOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Driver Found");
        setSupportActionBar(toolbar);



        buildGoogleApiClient();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();




        textViewDriverName=(TextView) findViewById(R.id.driver_name);
        textViewIdOrder=(TextView) findViewById(R.id.id_order);
        textViewPrice=(TextView) findViewById(R.id.price);


        textViewPickUpAddres=(TextView) findViewById(R.id.textView_pickup_address);
        textViewDropOffAddress=(TextView) findViewById(R.id.textView_dropoff_address);

        linearLayoutPickupNote=(LinearLayout) findViewById(R.id.pickup_note_edittext);
        linearLayoutDropoffNote=(LinearLayout) findViewById(R.id.dropoff_note_edittext);

        textViewPickupNote=new TextView(this);
        textViewDropoffNote=new TextView(this);

        idOrder= getIntent().getStringExtra("id_order");
        orderRide = new OrderRide();
        new getOrderDetail().execute();


    }

    private void setAllTextView()
    {
        textViewDriverName.setText(orderRide.driver.name);
        textViewIdOrder.setText(orderRide.id_order);
        textViewPrice.setText(orderRide.getPriceString());
        textViewPickUpAddres.setText(orderRide.pickupAddress);
        textViewDropOffAddress.setText(orderRide.dropoffAddress);

        if(!orderRide.pickupNote.equals(""))
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewPickupNote.setText(orderRide.getPickupNoteString());
            textViewPickupNote.setLayoutParams(params);
            linearLayoutPickupNote.addView(textViewPickupNote);
        }

        if(!orderRide.dropoffNote.equals(""))
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewDropoffNote.setText(orderRide.getDropoffNoteString());
            textViewDropoffNote.setLayoutParams(params);
            linearLayoutDropoffNote.addView(textViewDropoffNote);
        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }





    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0,0,0,500);
        }


    }

    @Override
    public void onConnected(Bundle bundle) {


    }

    private void adjustCamera()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        builder.include(pickUpMarker.getPosition());
        builder.include(dropOffMarker.getPosition());
        builder.include(driverMarker.getPosition());


        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.1); // offset from edges of the map 12% of screen


        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, 500);
        mMap.animateCamera(cu);
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private class getOrderDetail extends AsyncTask<Void, Void, Void> {
        @Override




        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.getOrderDetail(idOrder);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {

                    JSONObject order = new JSONObject(jsonStr);

                    orderRide.id_order=idOrder;
                    orderRide.driver.idDriver = order.getString("id_driver");
                    orderRide.driver.name = order.getString("driver_name");
                    orderRide.driver.plat = order.getString("plat");
                    orderRide.driver.phone = order.getString("driver_phone");
                    orderRide.driver.driverLocation=new LatLng(order.getDouble("driver_lat"),order.getDouble("driver_long"));
                    orderRide.status = order.getString("status_order");
                    orderRide.dropoffAddress = order.getString("destination_address");
                    orderRide.pickupAddress=order.getString("origin_address");
                    orderRide.price=order.getInt("price");
                    orderRide.pickupNote=order.getString("note");
                    orderRide.dropoffNote=order.getString("note");
                    orderRide.pickupPosition=new LatLng(order.getDouble("lat_from"),order.getDouble("long_from"));
                    orderRide.dropoofPosition=new LatLng(order.getDouble("lat_to"),order.getDouble("long_to"));


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

            pickUpMarker = mMap.addMarker(new MarkerOptions().position(orderRide.pickupPosition));
            dropOffMarker= mMap.addMarker(new MarkerOptions().position(orderRide.dropoofPosition));
            driverMarker=mMap.addMarker(new MarkerOptions()
                    .position(orderRide.driver.driverLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.motorcycle)));
            setAllTextView();
            adjustCamera();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {

        super.onStop();
    }
}
