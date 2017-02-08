package com.hogwheelz.userapps.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
import com.hogwheelz.userapps.app.AppController;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.OrderRide;
import com.hogwheelz.userapps.persistence.UserGlobal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HogrideActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = HogrideActivity.class.getSimpleName();
    private Toolbar toolbar;
    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    View mapView;

    Location mLastLocation;
    double lat =0, lng=0;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_PICKUP = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DROPOFF = 2;

    Marker pickUpMarker;
    Marker dropOffMarker;

    TextView textViewPickUpAddres;
    TextView textViewDropOffAddress;
    TextView textViewPrice;

    LinearLayout linearLayoutPickupNote;
    LinearLayout linearLayoutDropoffNote;
    Button buttonPickupNote;
    Button buttonDropoffNote;

    EditText editTextPickupNote;
    EditText editTextDropoffNote;

    boolean isPickupNoteActive;
    boolean isDropoffNoteActive;
    private static boolean isPickUpAddressSetted,isDropOffAddressSetted;

    Double driverLocationLat[];
    Double driverLocationLng[];

    Button buttonBook;

    LinearLayout linearLayoutOrder;



    OrderRide orderRide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hogride);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Hogride");
        setSupportActionBar(toolbar);


        buildGoogleApiClient();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        textViewPickUpAddres=(TextView)findViewById(R.id.textView_pickup_address);
        textViewDropOffAddress=(TextView)findViewById(R.id.textView_dropoff_address);
        textViewPrice=(TextView)findViewById(R.id.price);

        linearLayoutPickupNote=(LinearLayout) findViewById(R.id.pickup_note_edittext);
        linearLayoutDropoffNote=(LinearLayout) findViewById(R.id.dropoff_note_edittext);



        LinearLayout pickUpAddressButton = (LinearLayout) findViewById(R.id.pickup_button);
        pickUpAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPlaceAutocompleteActivityIntentForPickUp();
            }
        });

        LinearLayout dropOffAddressButton = (LinearLayout) findViewById(R.id.dropoff_button);
        dropOffAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPlaceAutocompleteActivityIntentForDropOff();
            }
        });

        editTextPickupNote=new EditText(this);
        editTextDropoffNote=new EditText(this);

        isDropoffNoteActive=false;
        isPickupNoteActive=false;


        buttonPickupNote = (Button) findViewById(R.id.pickup_note_button);
        buttonPickupNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPickupNoteState();
            }
        });

        buttonDropoffNote = (Button) findViewById(R.id.dropoff_note_button);
        buttonDropoffNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDropoffNoteState();
            }
        });


        linearLayoutOrder= (LinearLayout) findViewById(R.id.order);
        buttonBook =(Button) findViewById(R.id.book_button);

        orderRide=new OrderRide();
    }


    private void setPickupNoteState()
    {
        if(isPickupNoteActive)
        {
            linearLayoutPickupNote.removeView(editTextPickupNote);
            isPickupNoteActive=false;
        }
        else if(!isPickupNoteActive)
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            editTextPickupNote.setHint("add notes");
            editTextPickupNote.setLayoutParams(params);
            linearLayoutPickupNote.addView(editTextPickupNote);
            isPickupNoteActive=true;
        }
    }

    private void setDropoffNoteState()
    {

        if(isDropoffNoteActive)
        {
            linearLayoutDropoffNote.removeView(editTextDropoffNote);
            isDropoffNoteActive=false;
        }
        else if(!isDropoffNoteActive)
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            editTextDropoffNote.setHint("add notes");
            editTextDropoffNote.setLayoutParams(params);
            linearLayoutDropoffNote.addView(editTextDropoffNote);
            isDropoffNoteActive=true;
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();

            orderRide.pickupPosition = new LatLng(lat, lng);
            orderRide.pickupAddress=getAddress(lat,lng);
            pickUpMarker = mMap.addMarker(new MarkerOptions().position(orderRide.pickupPosition).title(orderRide.pickupAddress));
            dropOffMarker = mMap.addMarker(new MarkerOptions().position(orderRide.pickupPosition).title(orderRide.pickupAddress));

            new getDriverLocation().execute();
            dropOffMarker.setVisible(false);
            setAwal();
            adjustCamera();
            fillPickUpAddress();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                android.location.Address address = addresses.get(0);
                result.append(address.getAddressLine(0));
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    private class getDriverLocation extends AsyncTask<Void, Void, Void> {
        @Override



        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            String myLat = String.valueOf(lat);
            String myLng = String.valueOf(lng);
            String url = AppConfig.getDriverLocationURL(myLat + "," + myLng);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray arrayDriverLocation = jsonObj.getJSONArray("records");

                    driverLocationLat= new Double[arrayDriverLocation.length()];
                    driverLocationLng= new Double[arrayDriverLocation.length()];
                    for(int i=0;i<arrayDriverLocation.length();i++)
                    {
                        JSONObject location=arrayDriverLocation.getJSONObject(i);
                        driverLocationLat[i] = Double.parseDouble(location.getString("lat_cur").toString());
                        driverLocationLng[i] = Double.parseDouble(location.getString("long_cur").toString());
                    }

                } catch (final JSONException e) {
                    Toast.makeText(HogrideActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(HogrideActivity.this, "Couldn't get json from server", Toast.LENGTH_SHORT).show();

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Marker driver[]= new Marker[driverLocationLat.length];
            for(int i=0;i<driverLocationLat.length;i++) {
                driver[i] = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(driverLocationLat[i], driverLocationLng[i]))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.motorcycle)));
            }

            // Dismiss the progress dialog

        }
    }

    private void adjustCamera()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        builder.include(pickUpMarker.getPosition());
        builder.include(dropOffMarker.getPosition());


        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.1); // offset from edges of the map 12% of screen


        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, 500);
        mMap.animateCamera(cu);
    }

    private void setAwal()
    {
        textViewPrice.setText("Choose your drop off above");
        linearLayoutOrder.setVisibility(View.INVISIBLE);
        isDropOffAddressSetted=false;
        isPickUpAddressSetted=false;
    }

    private void fillPickUpAddress()
    {
        pickUpMarker.setPosition(orderRide.pickupPosition);
        pickUpMarker.setTitle(orderRide.pickupAddress);
        adjustCamera();
        textViewPickUpAddres.setText(orderRide.pickupAddress);
        isPickUpAddressSetted=true;
        setBookingState();
    }

    private void fillDropOffAddress()
    {
        dropOffMarker.setPosition(orderRide.dropoofPosition);
        dropOffMarker.setTitle(orderRide.dropoffAddress);
        dropOffMarker.setVisible(true);
        adjustCamera();
        textViewDropOffAddress.setText(orderRide.dropoffAddress);
        isDropOffAddressSetted=true;
        setBookingState();
    }

    private void setBookingState()
    {
        if(isDropOffAddressSetted&&isPickUpAddressSetted) {

            new calculatePrice().execute();
        }
        else
        {
            linearLayoutOrder.setVisibility(View.INVISIBLE);
        }
    }

    private class calculatePrice extends AsyncTask<Void, Void, Void> {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            textViewPrice.setText("Please wait");

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            String originsLat = orderRide.getPickupLatString();
            String originsLng =orderRide.getPickupLngString();
            String destinationsLat = orderRide.getDropoffLatString();
            String destinationsLng = orderRide.getDropoffLngString();
            String url = AppConfig.getPriceURL(originsLat + "," + originsLng, destinationsLat + "," + destinationsLng);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    orderRide.setPrice(jsonObj.getString("price"));

                } catch (final JSONException e) {
                    Toast.makeText(HogrideActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(HogrideActivity.this, "Couldn't get json from server.", Toast.LENGTH_SHORT).show();

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            textViewPrice.setText(orderRide.getPriceString());
            linearLayoutOrder.setVisibility(View.VISIBLE);


            // Link to Register Screen
            buttonBook.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    booking();

                }
            });

            // Dismiss the progress dialog

        }
    }

    private void callPlaceAutocompleteActivityIntentForPickUp() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_PICKUP);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

    }

    private void callPlaceAutocompleteActivityIntentForDropOff() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DROPOFF);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_PICKUP) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                orderRide.setPickupPlace(place);
                fillPickUpAddress();


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.

                textViewPickUpAddres.setText(status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DROPOFF) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                orderRide.setDropoffPlace(place);
                fillDropOffAddress();


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                textViewDropOffAddress.setText(status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

    private void booking() {
        // Tag used to cancel the request
        String tag_string_req = "booking";
        orderRide.user = UserGlobal.getUser(getApplicationContext());

        orderRide.pickupNote=editTextPickupNote.getText().toString();
        orderRide.dropoffNote=editTextDropoffNote.getText().toString();



        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_BOOKING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");

                    // Check for error node in json
                    if (status.contentEquals("1")) {

                        String idOrder=jObj.getString("id_order");

                        Intent i = new Intent(HogrideActivity.this,
                                BookingActivity.class);
                        i.putExtra("id_order",idOrder);
                        startActivity(i);

                    } else {

                        String errorMsg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(),
                                "status"+errorMsg, Toast.LENGTH_LONG).show();
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
                        "error listener"+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id_customer",orderRide.user.idCustomer);
                params.put("add_from",orderRide.pickupAddress);
                params.put("add_to",orderRide.dropoffAddress);
                params.put("lat_from",orderRide.getPickupLatString());
                params.put("long_from",orderRide.getPickupLngString());
                params.put("lat_to",orderRide.getDropoffLatString());
                params.put("long_to",orderRide.getDropoffLngString());
                params.put("price",orderRide.getPriceString());
                params.put("note",orderRide.getPickupNoteString()+orderRide.getDropoffNoteString());

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
