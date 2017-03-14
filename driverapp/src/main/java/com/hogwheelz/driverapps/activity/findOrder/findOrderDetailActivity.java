package com.hogwheelz.driverapps.activity.findOrder;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.activity.NotifActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderFoodActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderRideActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderSendActivity;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.AppController;
import com.hogwheelz.driverapps.persistence.DriverGlobal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class FindOrderDetailActivity extends NotifActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = FindOrderDetailActivity.class.getSimpleName();

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

    Button buttonAccept;
    Button buttonDetail;

    LinearLayout linearLayoutDetail;
    String orderType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_order);

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

        linearLayoutDetail = (LinearLayout) findViewById(R.id.detail);

         buttonAccept = (Button) findViewById(R.id.button_accept);
         buttonDetail = (Button) findViewById(R.id.button_detail);



        idOrder= getIntent().getStringExtra("id_order");
        orderType = getIntent().getStringExtra("order_type");
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


    public void acceptOrder() {
        // Tag used to cancel the request
        String tag_string_req = "accept_order";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.ACCEPT_ORDER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");

                    // Check for error node in json
                    if (status.contentEquals("1")) {
                        Toast.makeText(FindOrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();



                        if (orderType.contentEquals("1")) {
                            Intent i = new Intent(FindOrderDetailActivity.this, ViewOrderRideActivity.class);
                            i.putExtra("id_order",idOrder);
                            startActivity(i);
                            finish();
                        }
                        else if (orderType.contentEquals("2")) {
                            Intent i = new Intent(FindOrderDetailActivity.this, ViewOrderSendActivity.class);
                            i.putExtra("id_order",idOrder);
                            startActivity(i);
                            finish();
                        }
                        else if(orderType.contentEquals("3"))
                        {
                            Intent i = new Intent(FindOrderDetailActivity.this, ViewOrderFoodActivity.class);
                            i.putExtra("id_order",idOrder);
                            startActivity(i);
                            finish();
                        }

                    }
                    else if (status.contentEquals("2"))
                    {
                        Toast.makeText(FindOrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
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

                params.put("order_id",idOrder);
                params.put("driver_id", DriverGlobal.getDriver(getApplicationContext()).idDriver);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }






    public void setAllTextView()
    {
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptOrder();
            }
        });
    };

    public abstract void adjustCamera();


}
