package com.hogwheelz.driverapps.activity.other;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.hogwheelz.driverapps.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng pickup;
    LatLng dropoff;
    Marker pickUpMarker;
    Marker dropOffMarker;
ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });

        pickup = getIntent().getParcelableExtra("pick_up");
        dropoff = getIntent().getParcelableExtra("drop_off");


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


        pickUpMarker=mMap.addMarker(new MarkerOptions()
                .position(pickup)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker))
        );
        dropOffMarker=mMap.addMarker(new MarkerOptions()
                .position(dropoff)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker))
        );
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                adjustCamera();
            }
        });


    }

    private void adjustCamera()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        builder.include(pickUpMarker.getPosition());
        builder.include(dropOffMarker.getPosition());


        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.1); // offset from edges of the map 12% of screen
        int padding2 = (int) (width * 0.1); // offset from edges of the map 12% of screen

        mMap.setPadding(padding2,padding,padding2,padding);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,0);
        mMap.moveCamera(cu);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
