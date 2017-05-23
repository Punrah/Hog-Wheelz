package com.hogwheelz.userapps.activity.makeOrder;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.DriverLocation;
import com.hogwheelz.userapps.persistence.UserGlobal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public abstract class MakeOrder extends RootActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MakeOrder.class.getSimpleName();
    private static final long SET_INTERVAL = 10 * 1000;
    private static final long FASTEST_INTERVAL = 1 * 1000;
    public Toolbar toolbar;
    public GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    View mapView;

    Location mLastLocation;
    double lat = 0, lng = 0;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_PICKUP = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DROPOFF = 2;

    int REQUEST_SEND = 3;

    ImageView back;
    ImageView logo;

    Marker pickUpMarker;
    Marker dropOffMarker;

    TextView textViewDistanceLabel;
    TextView textViewPriceLabel;

    TextView textViewPickUpAddres;
    TextView textViewDropOffAddress;
    TextView textViewPrice;
    TextView textViewDistance;
    RadioButton radioHogpay;
    RadioButton radioCash;
    ImageView imgCar;
    ImageView imgBike;
    LinearLayout buttonBike;
    LinearLayout buttonCar;
    String vehicleState="bike";

    LinearLayout linearLayoutPickupNote;
    LinearLayout linearLayoutDropoffNote;
    ImageView buttonPickupNote;
    ImageView buttonDropoffNote;

    EditText editTextPickupNote;
    EditText editTextDropoffNote;

    boolean isPickupNoteActive;
    boolean isDropoffNoteActive;
    public static boolean isPickUpAddressSetted, isDropOffAddressSetted;

    Double driverLocationLat[];
    Double driverLocationLng[];
    String vehicleType[];

    List<Marker> driver;

    ImageView buttonBook;


    public FirebaseDatabase friendsDatabase;
    public DatabaseReference friendsDatabaseReference;

    List<DriverLocation> driverLocation;


    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_order);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        back = (ImageView) findViewById(R.id.back);
        logo = (ImageView) findViewById(R.id.logo);

        setToolbarTitle();



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeOrder.super.onBackPressed();
            }
        });

        buildGoogleApiClient();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        textViewDistanceLabel=(TextView) findViewById(R.id.distance_label);
        textViewPriceLabel=(TextView) findViewById(R.id.fare_label);
        textViewPickUpAddres = (TextView) findViewById(R.id.textView_pickup_address);
        textViewDropOffAddress = (TextView) findViewById(R.id.textView_dropoff_address);
        textViewPrice = (TextView) findViewById(R.id.price);
        textViewDistance=(TextView) findViewById(R.id.distance);
        radioHogpay = (RadioButton) findViewById(R.id.radio_hogpay);
        radioCash = (RadioButton) findViewById(R.id.radio_cash);
        imgCar =(ImageView) findViewById(R.id.img_car);
        imgBike= (ImageView) findViewById(R.id.img_bike);

        buttonBike=(LinearLayout) findViewById(R.id.bike_button);
        buttonCar=(LinearLayout) findViewById(R.id.car_button);

        linearLayoutPickupNote = (LinearLayout) findViewById(R.id.pickup_note_edittext);
        linearLayoutDropoffNote = (LinearLayout) findViewById(R.id.dropoff_note_edittext);


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

        editTextPickupNote = new EditText(this);
        editTextDropoffNote = new EditText(this);

        isDropoffNoteActive = false;
        isPickupNoteActive = false;


        buttonPickupNote = (ImageView) findViewById(R.id.pickup_note_button);
        buttonPickupNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPickupNoteState();
            }
        });

        buttonDropoffNote = (ImageView) findViewById(R.id.dropoff_note_button);
        buttonDropoffNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDropoffNoteState();
            }
        });

        radioCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCash.setChecked(true);
                radioHogpay.setChecked(false);
            }
        });
        radioHogpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCash.setChecked(false);
                radioHogpay.setChecked(true);
            }
        });

        buttonBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bikeView();
                bikePressed();
            }
        });
        buttonCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carView();
                carPressed();
            }
        });




        if(UserGlobal.balance<=0)
        {
            radioHogpay.setEnabled(false);
            radioCash.setChecked(true);
        }

         /*get instance of our friendsDatabase*/
        friendsDatabase = FirebaseDatabase.getInstance();
        /*get a reference to the friends node location*/
        friendsDatabaseReference = friendsDatabase.getReference("location_driver");
        driver = new ArrayList<Marker>();



        buttonBook = (ImageView) findViewById(R.id.book_button);

        buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        inizializeOrder();
    }

    /*updates data in realtime, displays the data in a list*/
    public void addValueEventListener(final DatabaseReference friendsReference) {


        friendsReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String count;
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    Log.e(snap.getKey(),snap.getChildrenCount() + "");
                }

                driverLocation = new ArrayList<>();
                /*this is called when first passing the data and
                * then whenever the data is updated*/
                   /*get the data children*/

                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    /*get the values as a Order object*/
                    DriverLocation value = iterator.next().getValue(DriverLocation.class);
                    /*add the friend to the list for the adapter*/

                    driverLocation.add(value);
                }

                for(int i=0;i<driver.size();i++)
                {
                    driver.get(i).remove();
                }

                driver = new ArrayList<Marker>();

                for(int i=0;i<driverLocation.size();i++) {
                    if (driverLocation.get(i).type.contentEquals("bike")) {

                        driver.add(mMap.addMarker(new MarkerOptions()
                                .position(driverLocation.get(i).getLatLang())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_motor))));
                    } else if (driverLocation.get(i).type.contentEquals("car")) {

                        driver.add(mMap.addMarker(new MarkerOptions()
                                .position(driverLocation.get(i).getLatLang())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_car))));
                    }
                }

                if(vehicleState=="bike")
                {
                    bikeView();
                }
                else if (vehicleState=="car")
                {
                    carView();
                }


        }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                /*listener failed or was removed for security reasons*/
            }
        });

    }


    private void bikeView() {
        vehicleState="bike";
        setPriceByVehicle();
        for(int i=0;i<driverLocation.size();i++) {

            if(driverLocation.get(i).type.contentEquals("car")) {
                driver.get(i).setVisible(false);
            }
            else
            {
                driver.get(i).setVisible(true);
            }

        }

    }

    public abstract void setPriceByVehicle();


    private void carView() {
        vehicleState="car";
        setPriceByVehicle();
        for(int i=0;i<driverLocation.size();i++) {

            if(driverLocation.get(i).type.contentEquals("bike")) {
                driver.get(i).setVisible(false);
            }
            else
            {
                driver.get(i).setVisible(true);
            }
        }

    }

    private void bikePressed() {
        setTypeImage(1);
    }
    private void carPressed() {
        setTypeImage(2);
    }



    public abstract void setToolbarTitle();

    public abstract void inizializeOrder();


    private void setPickupNoteState() {
        if (isPickupNoteActive) {
            buttonPickupNote.setImageResource(R.drawable.note_inactive);
            linearLayoutPickupNote.removeView(editTextPickupNote);
            isPickupNoteActive = false;

        } else if (!isPickupNoteActive) {
            buttonPickupNote.setImageResource(R.drawable.note_active);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            editTextPickupNote.setHint("Add notes");
            editTextPickupNote.setLayoutParams(params);
            editTextPickupNote.setBackgroundColor(Color.TRANSPARENT);
            editTextPickupNote.setTextSize(14);
            linearLayoutPickupNote.addView(editTextPickupNote);
            isPickupNoteActive = true;
        }
    }

    private void setDropoffNoteState() {

        if (isDropoffNoteActive) {
            buttonDropoffNote.setImageResource(R.drawable.note_inactive);
            linearLayoutDropoffNote.removeView(editTextDropoffNote);
            isDropoffNoteActive = false;
        } else if (!isDropoffNoteActive) {
            buttonDropoffNote.setImageResource(R.drawable.note_active);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            editTextDropoffNote.setHint("Add notes");
            editTextDropoffNote.setLayoutParams(params);
            editTextDropoffNote.setBackgroundColor(Color.TRANSPARENT);
            editTextDropoffNote.setTextSize(14);
            linearLayoutDropoffNote.addView(editTextDropoffNote);
            isDropoffNoteActive = true;
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
            layoutParams.setMargins(0, 0, 0, 0);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        setConditionLocation();

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

    public String getAddress(double latitude, double longitude) {
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


    public void adjustCamera()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        builder.include(pickUpMarker.getPosition());
        builder.include(dropOffMarker.getPosition());


        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.3); // offset from edges of the map 12% of screen
        int padding2 = (int) (width * 0.1); // offset from edges of the map 12% of screen

        mMap.setPadding(padding2,padding,padding2,padding);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,0);
        mMap.animateCamera(cu);
    }

    public void setAwal()
    {
        textViewPrice.setText(Formater.getPrice("0"));
        textViewDistance.setText(Formater.getDistance("0"));
        radioHogpay.setChecked(false);
        radioCash.setChecked(true);
        buttonBook.setImageResource(R.drawable.order_inactive);;
        setTypeImage(1);
        isDropOffAddressSetted=false;
        isPickUpAddressSetted=false;
    }

    public abstract void setTypeImage(int type);



    public void setBookingState()
    {
        if(isDropOffAddressSetted&&isPickUpAddressSetted) {

            readyToOrder();
        }
        else
        {

        }
    }

    public abstract void readyToOrder();


    private void callPlaceAutocompleteActivityIntentForPickUp() {
        try {
//            Intent intent =
//                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
//                            .build(this);
//            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_PICKUP);
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            startActivityForResult(builder.build(this), PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_PICKUP);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

    }

    private void callPlaceAutocompleteActivityIntentForDropOff() {
        try {


            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();



            startActivityForResult(builder.build(this), PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DROPOFF);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }

    }



}
