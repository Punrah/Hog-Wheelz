package com.hogwheelz.userapps.activity.hogFood;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderFood;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.persistence.Restaurant;

import java.util.List;

public class RestaurantDetailActivity extends RootActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    TextView textViewRestaurantName;
    TextView textViewRestaurantAddress;
    TextView textViewRestaurantPhone;
    Toolbar toolbar;
    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    View mapView;
    Marker markerRestaurant;
    LatLng location;
    Restaurant restaurant;
    LinearLayout linearLayoutOpenHoursComplete;
    List<String> openHoursComplete;
    ImageView back;
    TextView call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        restaurant = getIntent().getParcelableExtra("restaurant");

        location = restaurant.location;
        openHoursComplete = restaurant.openHourComplete;
        String name=restaurant.name;
        String adress=restaurant.address;
        final String phone ="081805314808";


        buildGoogleApiClient();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();


        textViewRestaurantName= (TextView) findViewById(R.id.restaurant_name);
        textViewRestaurantAddress= (TextView) findViewById(R.id.restaurant_address);
        textViewRestaurantPhone= (TextView) findViewById(R.id.restaurant_phone);
        linearLayoutOpenHoursComplete=(LinearLayout) findViewById(R.id.open_hours_complete);

        back =(ImageView) findViewById(R.id.back);
        call = (TextView) findViewById(R.id.call);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RestaurantDetailActivity.this);

                builder.setTitle("Call Restaurant");
                builder.setMessage("Do you want to call "+phone+" ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        setConditionCall();

                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                android.support.v7.app.AlertDialog alert = builder.create();
                alert.show();
            }
        });

        textViewRestaurantName.setText(name);
        textViewRestaurantAddress.setText(adress);
        textViewRestaurantPhone.setText(phone);
        loadOpenHoursComplete();
    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }

    private void loadOpenHoursComplete()
    {
        for (int i=0;i<openHoursComplete.size();i++)
        {

            LayoutInflater inflater = getLayoutInflater();
            LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.open_hours_complete, linearLayoutOpenHoursComplete, false);
            TextView openHours = (TextView) convertView.findViewById(R.id.open_hours);
            TextView day = (TextView) convertView.findViewById(R.id.day);

            String[] openHour = openHoursComplete.get(i).split(" ");
            day.setText(openHour[0]);
            openHours.setText(openHour[1]);


            linearLayoutOpenHoursComplete.addView(convertView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        markerRestaurant = mMap.addMarker(new MarkerOptions().position(location));
        adjustCamera();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
    private void adjustCamera()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        builder.include(markerRestaurant.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.1); // offset from edges of the map 12% of screen


        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }
}
