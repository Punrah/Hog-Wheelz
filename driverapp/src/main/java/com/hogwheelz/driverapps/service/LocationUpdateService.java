package com.hogwheelz.driverapps.service;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.hogwheelz.driverapps.Utils.Const;
import com.hogwheelz.driverapps.Utils.LocationVo;
import com.hogwheelz.driverapps.activity.main.RootActivity;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.hogwheelz.driverapps.persistence.DriverGlobal;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Grishma on 16/5/16.
 */
public class LocationUpdateService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    protected static final String TAG = "LocationUpdateService";
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;

    private String idDriver;
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    public static Boolean mRequestingLocationUpdates;
    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    public static Location mCurrentLocation;
    public static boolean isEnded = false;
    private ArrayList<LocationVo> mLocationData;


    @Override
    public void onCreate() {
        super.onCreate();
        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        idDriver = DriverGlobal.getDriver(getApplicationContext()).idDriver;
        Log.d("LOC", "Service init...");
        isEnded = false;
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        buildGoogleApiClient();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        return Service.START_STICKY;
    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended==");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient===");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    public void setLocationData() {

        mLocationData = new ArrayList<>();
        LocationVo mLocVo = new LocationVo();
        mLocVo.setmLongitude(mCurrentLocation.getLongitude());
        mLocVo.setmLatitude(mCurrentLocation.getLatitude());
        mLocVo.setmLocAddress(Const.getCompleteAddressString(this, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        mLocationData.add(mLocVo);

    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI() {
        setLocationData();
        Log.d(TAG, "Latitude:==" + mCurrentLocation.getLatitude() + "\n Longitude:==" + mCurrentLocation.getLongitude
                ());
        String lat = Double.toString(mCurrentLocation.getLatitude());
        String lng = Double.toString(mCurrentLocation.getLongitude());


        updateLoc(idDriver, lat, lng);
    }


    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mGoogleApiClient.connect();
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            getMyLocation();

        }
    }


    private void getMyLocation() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);

            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }

        Log.i(TAG, " startLocationUpdates===");
        isEnded = true;
    }



    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            // It is a good practice to remove location requests when the activity is in a paused or
            // stopped state. Doing so helps battery performance and is especially
            // recommended in applications that request frequent location updates.

            Log.d(TAG, "stopLocationUpdates();==");
            // The final argument to {@code requestLocationUpdates()} is a LocationListener
            // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();

    }


    /**
     * function to verify login details in mysql db
     * */
    private void updateLoc(final String idDriver, final String lat, final String lng) {
        // Tag used to cancel the request
        String tag_string_req = "update_loc";


        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.UPDATE_LOC, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");

                    // Check for error node in json
                    if (status.contentEquals("1")) {
                        // user successfully logged in
                        // Create login session
                        String msg = jObj.getString("msg");
                    } else {
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

                params.put("id_driver", idDriver);
                params.put("lat", lat);
                params.put("long", lng);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public static Location getLocation()
    {
        return mCurrentLocation;
    }





}
