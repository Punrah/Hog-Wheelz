package com.hogwheelz.userapps.activity.ViewOrder;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.activity.makeOrder.CancelReasonActivity;
import com.hogwheelz.userapps.persistence.DriverLocation;

import java.util.List;


public abstract class ViewOrder extends RootActivity
        implements  GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = ViewOrder.class.getSimpleName();

    public FirebaseDatabase friendsDatabase;
    public DatabaseReference friendsDatabaseReference;

    DriverLocation driverLocation;

    public GoogleMap mMap;
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

    LinearLayout linearLayoutOrderActive;

    TextView textViewPickupNote;
    TextView textViewDropoffNote;
    TextView textViewStatus;
    TextView textViewPlat;
    TextView textViewDistance;

    ImageView vehicle;
    TextView paymentType;


    Marker pickUpMarker;
    Marker dropOffMarker;
    Marker driverMarker;

    ImageView buttonCancel;

    LinearLayout buttonDetail;
    TextView textViewButtonDetail;
    LinearLayout linearLayoutDetail;
    LinearLayout linearLayoutIsiDetail;
    ImageView back;
    ImageView refresh;

    TextView textViewDistanceLabel;
    TextView textViewFareLabel;

    ImageView imageViewDriver;
    ImageView imageViewCall;
    ImageView imageViewText;

    String idOrder;
    int orderType;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    AlertDialog.Builder alert;


    ImageView star1;
    ImageView star2;
    ImageView star3;
    ImageView star4;
    ImageView star5;

    ImageView star1a;
    ImageView star2a;
    ImageView star3a;
    ImageView star4a;
    ImageView star5a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Driver Found");
        setSupportActionBar(toolbar);
        back = (ImageView) findViewById(R.id.back);
        refresh=(ImageView) findViewById(R.id.refresh);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                ViewOrder.super.onBackPressed();
            }
        });


        buildGoogleApiClient();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();


        linearLayoutDetail = (LinearLayout) findViewById(R.id.detail);
        linearLayoutIsiDetail = (LinearLayout) findViewById(R.id.isi_detail);

        textViewDriverName=(TextView) findViewById(R.id.driver_name);
        textViewIdOrder=(TextView) findViewById(R.id.id_order);
        textViewPrice=(TextView) findViewById(R.id.price);
        textViewPlat= (TextView) findViewById(R.id.plat);
        textViewDistanceLabel = (TextView) findViewById(R.id.distance_label);
        textViewFareLabel = (TextView) findViewById(R.id.fare_label);


        textViewPickUpAddres=(TextView) findViewById(R.id.textView_pickup_address);
        textViewDropOffAddress=(TextView) findViewById(R.id.textView_dropoff_address);
        textViewStatus=(TextView) findViewById(R.id.status);
        textViewDistance=(TextView) findViewById(R.id.distance);

        imageViewDriver=(ImageView) findViewById(R.id.img_driver);

        buttonDetail = (LinearLayout) findViewById(R.id.button_detail);
        textViewButtonDetail = (TextView) findViewById(R.id.details_button);

        linearLayoutPickupNote=(LinearLayout) findViewById(R.id.pickup_note_edittext);
        linearLayoutDropoffNote=(LinearLayout) findViewById(R.id.dropoff_note_edittext);

        linearLayoutOrderActive=(LinearLayout) findViewById(R.id.order_active);

        vehicle =(ImageView) findViewById(R.id.vehicle);
        paymentType = (TextView) findViewById(R.id.payment_type);

        imageViewCall = (ImageView) findViewById(R.id.call_driver);
        imageViewText= (ImageView) findViewById(R.id.text_driver);

        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        star5 = (ImageView) findViewById(R.id.star5);

        textViewPickupNote=new TextView(this);
        textViewDropoffNote=new TextView(this);

        buttonCancel=(ImageView) findViewById(R.id.cancel_accepted_order);



        idOrder=getIntent().getStringExtra("id_order");

         /*get instance of our friendsDatabase*/
        friendsDatabase = FirebaseDatabase.getInstance();

        initializeOrder();
    }

    public abstract void setAllTextView();


    public abstract void initializeOrder();





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
        setConditionLocation();

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
            layoutParams.setMargins(0,0,0,10);
        }


    }

    /*updates data in realtime, displays the data in a list*/
    public void addValueEventListener(final DatabaseReference friendsReference) {


        friendsReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                driverLocation = dataSnapshot.getValue(DriverLocation.class);
                driverMarker.setPosition(driverLocation.getLatLang());


            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            }
        });

    }

    @Override
    public void onConnected(Bundle bundle) {
        getOrderDetail();
    }

    public abstract void adjustCamera();





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


    public abstract void getOrderDetail();


    public void cancelOrder()
    {
        Intent i = new Intent(ViewOrder.this, CancelReasonActivity.class);
        i.putExtra("id_order",idOrder);
        startActivityForResult(i,1);
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void setPermissionLocation() {
        mMap.setMyLocationEnabled(true);
    }


    public  void  setStar(int count)
    {
        if(count==0)
        {
            star1.setImageResource(R.drawable.star_gray);
            star2.setImageResource(R.drawable.star_gray);
            star3.setImageResource(R.drawable.star_gray);
            star4.setImageResource(R.drawable.star_gray);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==1)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.star_gray);
            star3.setImageResource(R.drawable.star_gray);
            star4.setImageResource(R.drawable.star_gray);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==2)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.yellow_star);
            star3.setImageResource(R.drawable.star_gray);
            star4.setImageResource(R.drawable.star_gray);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==3)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.yellow_star);
            star3.setImageResource(R.drawable.yellow_star);
            star4.setImageResource(R.drawable.star_gray);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==4)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.yellow_star);
            star3.setImageResource(R.drawable.yellow_star);
            star4.setImageResource(R.drawable.yellow_star);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==5)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.yellow_star);
            star3.setImageResource(R.drawable.yellow_star);
            star4.setImageResource(R.drawable.yellow_star);
            star5.setImageResource(R.drawable.yellow_star);
        }
    }

    public  void  setStar2(int count)
    {
        if(count==0)
        {
            star1a.setImageResource(R.drawable.star_gray);
            star2a.setImageResource(R.drawable.star_gray);
            star3a.setImageResource(R.drawable.star_gray);
            star4a.setImageResource(R.drawable.star_gray);
            star5a.setImageResource(R.drawable.star_gray);
        }
        if(count==1)
        {
            star1a.setImageResource(R.drawable.yellow_star);
            star2a.setImageResource(R.drawable.star_gray);
            star3a.setImageResource(R.drawable.star_gray);
            star4a.setImageResource(R.drawable.star_gray);
            star5a.setImageResource(R.drawable.star_gray);
        }
        if(count==2)
        {
            star1a.setImageResource(R.drawable.yellow_star);
            star2a.setImageResource(R.drawable.yellow_star);
            star3a.setImageResource(R.drawable.star_gray);
            star4a.setImageResource(R.drawable.star_gray);
            star5a.setImageResource(R.drawable.star_gray);
        }
        if(count==3)
        {
            star1a.setImageResource(R.drawable.yellow_star);
            star2a.setImageResource(R.drawable.yellow_star);
            star3a.setImageResource(R.drawable.yellow_star);
            star4a.setImageResource(R.drawable.star_gray);
            star5a.setImageResource(R.drawable.star_gray);
        }
        if(count==4)
        {
            star1a.setImageResource(R.drawable.yellow_star);
            star2a.setImageResource(R.drawable.yellow_star);
            star3a.setImageResource(R.drawable.yellow_star);
            star4a.setImageResource(R.drawable.yellow_star);
            star5a.setImageResource(R.drawable.star_gray);
        }
        if(count==5)
        {
            star1a.setImageResource(R.drawable.yellow_star);
            star2a.setImageResource(R.drawable.yellow_star);
            star3a.setImageResource(R.drawable.yellow_star);
            star4a.setImageResource(R.drawable.yellow_star);
            star5a.setImageResource(R.drawable.yellow_star);
        }
    }
}

