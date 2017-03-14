package com.hogwheelz.driverapps.activity.viewOrder;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import com.hogwheelz.driverapps.activity.NotifActivity;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.AppController;
import com.hogwheelz.driverapps.helper.HttpHandler;
import com.hogwheelz.driverapps.persistence.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class ViewOrderActivity extends NotifActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = ViewOrderActivity.class.getSimpleName();

    View mapView;
    public GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    //Order order;

    String idOrder;

    Marker pickUpMarker;
    Marker dropOffMarker;
    Marker driverMarker;

    TextView textViewCustomerName;
    TextView textViewDestination;
    TextView textViewDistance;
    TextView textViewOrderId;
    TextView textViewOrigin;
    TextView textViewPrice;
    TextView textViewNoteOrigin;
    TextView textViewNoteDestination;

    Button buttonGo;
    Button buttonCancel;

    LinearLayout linearLayoutDetail;
    Button buttonDetail;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        buildGoogleApiClient();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

         textViewCustomerName=(TextView)findViewById(R.id.customer_name);
         textViewDestination=(TextView)findViewById(R.id.destination);
         textViewDistance=(TextView)findViewById(R.id.distance);
         textViewOrderId=(TextView)findViewById(R.id.order_id);
         textViewOrigin=(TextView)findViewById(R.id.origin);
         textViewPrice=(TextView)findViewById(R.id.price);
         textViewNoteOrigin=(TextView)findViewById(R.id.note_origin);
         textViewNoteDestination=(TextView)findViewById(R.id.note_destination);

         buttonGo = (Button) findViewById(R.id.button_go);
         buttonCancel = (Button) findViewById(R.id.button_cancel);
         linearLayoutDetail = (LinearLayout) findViewById(R.id.detail);
         buttonDetail=(Button) findViewById(R.id.button_detail);



        idOrder= getIntent().getStringExtra("id_order");
        initializeOrder();
    }

    public abstract void initializeOrder();




    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

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
    public void onConnected(@Nullable Bundle bundle) {
        getOrderDetail();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public abstract void getOrderDetail();


    public abstract void cancelAcceptedOrder();
    public abstract void otwOrder();
    public abstract void startOrder();
    public abstract void completeOrder();






    public abstract void setAllTextView();

    public abstract void adjustCamera();


}
