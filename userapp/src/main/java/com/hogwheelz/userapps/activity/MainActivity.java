package com.hogwheelz.userapps.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.helper.SQLiteHandler;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.hogwheelz.userapps.R;

import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.helper.SessionManager;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private View navHeader;
    private TextView txtName, txtUsername;
    private SQLiteHandler db;
    private SessionManager session;

    public static String username;
    public static String name;
    public static String idCustomer;


    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    double lat =0, lng=0;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_PICKUP = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DROPOFF = 2;
    int RESULT_DRIVER = 3;

    Marker pickUpMarker;
    Marker dropOffMarker;

    TextView textViewPickUpAddres;
    TextView textViewDropOffAddress;
    TextView textViewPrice;
    TextView textViewNote;

    private int HOGRIDE =1;
    private int HOGSEND=2;
    private int HOGFOOD=3;


    private static int hogButtonFlag;

    private static boolean isPickUpAddressSetted,isDropOffAddressSetted;
     private static LatLng pickUpLatLang,dropOffLatlang;

    private Animation animShow, animHide;

    private LinearLayout linearLayoutInfo;



    String price;

    private LinearLayout hogRideBottomLine,hogSendBottomLine,hogFoodBottomLine;
    View mapView;

    Button buttonBook;

    LinearLayout preBooking;
    LinearLayout pastBooking;

    LinearLayout linearLayoutAddress;

    TextView textViewDriverName;
    TextView textViewDriverPhone;
    TextView textViewDriverPlat;

    public static boolean isBookingState;

    Double driverLocationLat[];
    Double driverLocationLng[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtUsername = (TextView) navHeader.findViewById(R.id.username);

        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        username = user.get("email");
        name = user.get("name");
        idCustomer = user.get("id_customer");
        //Displaying the user details on the screen
        txtUsername.setText(username);
        txtName.setText(name);


        buildGoogleApiClient();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();


        textViewPickUpAddres=(TextView)findViewById(R.id.textView_pickup_address);
        textViewDropOffAddress=(TextView)findViewById(R.id.textView_dropoff_address);
        textViewPrice=(TextView)findViewById(R.id.price);
        linearLayoutInfo=(LinearLayout) findViewById(R.id.infoAddress);



        textViewNote=(TextView) findViewById(R.id.note_edittext);


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

        LinearLayout hogRideButton = (LinearLayout) findViewById(R.id.hogride_button);
        hogRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hogRideListener();
            }
        });

        LinearLayout hogSendButton = (LinearLayout) findViewById(R.id.hogsend_button);
        hogSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hogSendListener();
            }
        });

        LinearLayout hogFoodButton = (LinearLayout) findViewById(R.id.hogfood_button);
        hogFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hogFoodListener();
            }
        });

        LinearLayout noteButton = (LinearLayout) findViewById(R.id.note_button);
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteListener();
            }
        });

        buttonBook =(Button) findViewById(R.id.book_button);


        hogRideBottomLine = (LinearLayout) findViewById(R.id.hogride_line);
        hogSendBottomLine = (LinearLayout) findViewById(R.id.hogsend_line);
        hogFoodBottomLine = (LinearLayout) findViewById(R.id.hogfood_line);
        hogButtonFlag=HOGRIDE;
        hogRadioButtonLikeFuction();

        preBooking= (LinearLayout) findViewById(R.id.prebooking);
        pastBooking= (LinearLayout) findViewById(R.id.pastbooking);

        linearLayoutAddress = (LinearLayout) findViewById(R.id.address);

        textViewDriverName=(TextView) findViewById(R.id.driver_name);
        textViewDriverPhone=(TextView) findViewById(R.id.driver_phone);
        textViewDriverPlat=(TextView) findViewById(R.id.driver_plat);

        isBookingState=true;

        initAnimation();

    }

    private void bookingLayoutState()
    {
        if(isBookingState)
        {
            preBooking.setVisibility(View.VISIBLE);
            pastBooking.setVisibility(View.INVISIBLE);
        }
        else if(!isBookingState)
        {
            preBooking.setVisibility(View.INVISIBLE);
            pastBooking.setVisibility(View.VISIBLE);
        }
    }


    private void initAnimation()
    {
        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_hogpay) {
            // Handle the camera action
        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_scheduled) {

        } else if (id == R.id.nav_notification) {

        } else if (id == R.id.nav_invite) {

        } else if (id == R.id.nav_support) {

        } else if (id == R.id.nav_ride) {

        } else if (id == R.id.nav_rate) {

        }else if (id == R.id.nav_logout) {
        logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

    private void hogRideListener() {
        hogButtonFlag=HOGRIDE;
        hogRadioButtonLikeFuction();
    }
    private void hogSendListener() {
        hogButtonFlag=HOGSEND;
        hogRadioButtonLikeFuction();

    }
    private void hogFoodListener() {
        hogButtonFlag=HOGFOOD;
        hogRadioButtonLikeFuction();

    }

    private void noteListener() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Notes to driver");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setText(textViewNote.getText());
        alert.setView(input);


        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                textViewNote.setText(input.getText());
                // Do something with value!
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.

            }
        });

        alert.show();
    }

    private void bookListener() {
    }

    private void hogRadioButtonLikeFuction() {
        if(hogButtonFlag==HOGRIDE)
        {
            hogRideBottomLine.setBackgroundColor(getResources().getColor(R.color.green));
            hogSendBottomLine.setBackgroundColor(getResources().getColor(R.color.white));
            hogFoodBottomLine.setBackgroundColor(getResources().getColor(R.color.white));
        }
        else if(hogButtonFlag==HOGSEND)
        {
            hogRideBottomLine.setBackgroundColor(getResources().getColor(R.color.white));
            hogSendBottomLine.setBackgroundColor(getResources().getColor(R.color.green));
            hogFoodBottomLine.setBackgroundColor(getResources().getColor(R.color.white));
        }
        else if(hogButtonFlag==HOGFOOD)
        {
            hogRideBottomLine.setBackgroundColor(getResources().getColor(R.color.white));
            hogSendBottomLine.setBackgroundColor(getResources().getColor(R.color.white));
            hogFoodBottomLine.setBackgroundColor(getResources().getColor(R.color.green));
        }

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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();
            pickUpLatLang = new LatLng(lat, lng);

            String pickUpAddress=getAddress(lat,lng);
            pickUpMarker = mMap.addMarker(new MarkerOptions().position(pickUpLatLang).title(pickUpAddress));
            dropOffMarker = mMap.addMarker(new MarkerOptions().position(pickUpLatLang).title(pickUpAddress));

            new getDriverLocation().execute();
            dropOffMarker.setVisible(false);
            setAwal();
            adjustCamera();
            fillPickUpAddress(pickUpAddress);
            linearLayoutInfo.setVisibility(View.INVISIBLE);

        }
    }


    private void fillPickUpAddress(String pickUpAddress)
    {
        textViewPickUpAddres.setText(pickUpAddress);
        isPickUpAddressSetted=true;
        setBookingState();
    }

    private void fillDropOffAddress(String dropOffAddress)
    {
        textViewDropOffAddress.setText(dropOffAddress);
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
            textViewPrice.setText("Choose your drop off above");
            linearLayoutInfo.setVisibility(View.INVISIBLE);
            linearLayoutInfo.startAnimation( animHide );
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Toast.makeText(this, "onstart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_PICKUP) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                pickUpLatLang=place.getLatLng();
                pickUpMarker.setPosition(place.getLatLng());
                pickUpMarker.setTitle(place.getName().toString());
                adjustCamera();
                fillPickUpAddress(place.getName().toString());


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
                dropOffLatlang=place.getLatLng();
                dropOffMarker.setPosition(place.getLatLng());
                dropOffMarker.setTitle(place.getName().toString());
                dropOffMarker.setVisible(true);
                adjustCamera();
                fillDropOffAddress(place.getName().toString());


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                textViewDropOffAddress.setText(status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        else if (requestCode == RESULT_DRIVER) {
            if (resultCode == RESULT_OK) {
                String name=data.getStringExtra("name");
                String phone=data.getStringExtra("phone");
                String plat=data.getStringExtra("plat");
                textViewDriverName.setText(name);
                textViewDriverPhone.setText(phone);
                textViewDriverPlat.setText(plat);

                linearLayoutAddress.setVisibility(View.INVISIBLE);

            } else
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
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

            String originsLat = String.valueOf(pickUpLatLang.latitude);
            String originsLng = String.valueOf(pickUpLatLang.longitude);
            String destinationsLat = String.valueOf(dropOffLatlang.latitude);
            String destinationsLng = String.valueOf(dropOffLatlang.longitude);
            String url = AppConfig.getPriceURL(originsLat + "," + originsLng, destinationsLat + "," + destinationsLng);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    price = jsonObj.getString("price");

                } catch (final JSONException e) {
                    price = "Json parsing error: " + e.getMessage();

                }
            } else {
                price = "Couldn't get json from server.";
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            textViewPrice.setText(price);
            linearLayoutInfo.setVisibility(View.VISIBLE);
            linearLayoutInfo.startAnimation( animShow );
            buttonBook.setBackgroundColor(Color.BLUE);

            // Link to Register Screen
            buttonBook.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(),
                            BookingActivity.class);
                    i.putExtra("id_customer",idCustomer);
                    i.putExtra("pickup_lat",String.valueOf(pickUpLatLang.latitude));
                    i.putExtra("pickup_lng",String.valueOf(pickUpLatLang.longitude));
                    i.putExtra("dropoff_lat",String.valueOf(dropOffLatlang.latitude));
                    i.putExtra("dropoff_lng",String.valueOf(dropOffLatlang.longitude));
                    i.putExtra("pickup_address",textViewPickUpAddres.getText());
                    i.putExtra("dropoff_address",textViewDropOffAddress.getText());
                    i.putExtra("price",price);
                    i.putExtra("notes",textViewNote.getText());
                    startActivityForResult(i,RESULT_DRIVER);
                }
            });

            // Dismiss the progress dialog

        }
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
                    price = "Json parsing error: " + e.getMessage();

                }
            } else {
                price = "Couldn't get json from server.";
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
        linearLayoutInfo.setVisibility(View.INVISIBLE);
        isDropOffAddressSetted=false;
        isPickUpAddressSetted=false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bookingLayoutState();
    }



}
