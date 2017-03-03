package com.hogwheelz.userapps.activity.ViewOrder;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.FindDriverActivity;
import com.hogwheelz.userapps.activity.MainActivity;
import com.hogwheelz.userapps.activity.NotifActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.app.Config;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Order;
import com.hogwheelz.userapps.persistence.OrderRide;
import com.hogwheelz.userapps.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public abstract class ViewOrder extends NotifActivity
        implements  GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = ViewOrder.class.getSimpleName();



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
    LinearLayout linearLayoutOrderInactive;

    TextView textViewPickupNote;
    TextView textViewDropoffNote;
    TextView textViewStatus;


    Marker pickUpMarker;
    Marker dropOffMarker;
    Marker driverMarker;

    Button buttonCancel;

    String idOrder;
    int orderType;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_order);
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
        textViewStatus=(TextView) findViewById(R.id.status);

        linearLayoutPickupNote=(LinearLayout) findViewById(R.id.pickup_note_edittext);
        linearLayoutDropoffNote=(LinearLayout) findViewById(R.id.dropoff_note_edittext);

        linearLayoutOrderActive=(LinearLayout) findViewById(R.id.order_active);
        linearLayoutOrderInactive=(LinearLayout) findViewById(R.id.order_inactive);

        textViewPickupNote=new TextView(this);
        textViewDropoffNote=new TextView(this);

        buttonCancel=(Button) findViewById(R.id.cancel_accepted_order);



        idOrder=getIntent().getStringExtra("id_order");

        initializeOrder();
    }

    public abstract void setAllTextView();


    public abstract void initializeOrder();


    public void cancelAcceptedOrder() {
        // Tag used to cancel the request
        String tag_string_req = "cancel_accepted_order";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.CANCEL_ACCEPTED_ORDER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");

                    // Check for error node in json
                    if (status.contentEquals("1")) {
                        finish();
                        Toast.makeText(ViewOrder.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    else if (status.contentEquals("2"))
                    {
                        Toast.makeText(ViewOrder.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // Error in login. Get the error message

                        String errorMsg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();

                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_order",idOrder);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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



    @Override
    protected void onStop() {
        super.onStop();
    }
}
