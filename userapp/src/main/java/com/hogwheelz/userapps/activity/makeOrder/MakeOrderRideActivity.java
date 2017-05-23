package com.hogwheelz.userapps.activity.makeOrder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.other.FindDriverActivity;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.OrderRide;
import com.hogwheelz.userapps.persistence.UserGlobal;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Startup on 2/8/17.
 *
 */

public class MakeOrderRideActivity extends MakeOrder {

    OrderRide order;

    private static final String TAG = MakeOrder.class.getSimpleName();

    @Override
    public void setToolbarTitle() {


        logo.setImageResource(R.drawable.ride_white);
    }

    @Override
    public void inizializeOrder() {
        order = new OrderRide();
    }

    @Override
    public void setTypeImage(int type) {
        if(type==1)
        {
            imgBike.setImageResource(R.drawable.motor_ride_yellow);
            imgCar.setImageResource(R.drawable.car_ride_gray);
        }
        if(type==2)
        {
            imgBike.setImageResource(R.drawable.motor_ride_gray);
            imgCar.setImageResource(R.drawable.car_ride_yellow);
        }
    }

    @Override
    public void readyToOrder() {
        new calculatePrice().execute();
    }

    private class calculatePrice extends MyAsyncTask {

        @Override

        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            String originsLat = order.getPickupLatString();
            String originsLng = order.getPickupLngString();
            String destinationsLat = order.getDropoffLatString();
            String destinationsLng = order.getDropoffLngString();
            order.orderType=1;
            order.vehicle=vehicleState;
            String url = AppConfig.getPriceURL(originsLat + "," + originsLng, destinationsLat + "," + destinationsLng,String.valueOf(order.orderType),order.vehicle);

            String jsonStr = null;
            try {
                jsonStr = sh.makeServiceCall(url);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        String status=jsonObj.getString("status");

                        order.priceCar = 0;
                        order.priceBike = 0;
                        order.distance = 0;

                        if(status.contentEquals("1")) {
                            order.priceCar = jsonObj.getInt("price_car");
                            order.priceBike = jsonObj.getInt("price_bike");
                            order.distance = jsonObj.getDouble("distance");
                            isSucces = true;
                        }
                        else if(status.contentEquals("2"))
                        {
                            if(order.vehicle.contentEquals("car"))
                            {

                                msg="The maximum distance of HOGRIDE by car is 75 km. Please try again";
                                msgTitle="Distance Above 75 km";
                                alertType=DIALOG_TITLE;

                            }
                            else if (order.vehicle.contentEquals("bike"))
                            {
                                msg="The maximum distance of HOGRIDE by bike is 25 km. Please try again";
                                msgTitle="Distance Above 25 km";
                                alertType=DIALOG_TITLE;
                            }
                        }
                        else if(status.contentEquals("3"))
                        {
                            msgTitle="Calculation Error";
                            msg="You cannot have the same pick-up and drop-off";
                            alertType=DIALOG_TITLE;

                        }
                        else if (status.contentEquals("4"))
                        {
                            msgTitle="Calculation Error";
                            msg="Please try in sometime";
                            alertType=DIALOG_TITLE;
                        }
                        else {

                            msgTitle="Unable to Calculate Price";
                            msg="Sorry, we were unable to calculate price at this time, please try again.";
                            alertType=DIALOG_TITLE;
                        }

                    } catch (final JSONException e) {
                        msgTitle="Unable to Calculate Price";
                        msg="Sorry, we were unable to calculate price at this time, please try again.";
                        alertType=DIALOG_TITLE;
                    }
                } else {
                    msgTitle="Unable to Calculate Price";
                    msg="Sorry, we were unable to calculate price at this time, please try again.";
                    alertType=DIALOG_TITLE;

                }
            } catch (IOException e) {
                badInternetAlert();
            }

            return null;
        }


        @Override
        public Context getContext() {
            return MakeOrderRideActivity.this;
        }

        @Override
        public void setSuccessPostExecute() {
            setPriceByVehicle();
            textViewDistance.setText(Formater.getDistance(order.getDistanceString()));
            buttonBook.setImageResource(R.drawable.order_active);




            // Link to Register Screen
            buttonBook.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    new booking().execute();

                }
            });
        }

        @Override
        public void setFailPostExecute() {

            setPriceByVehicle();
            textViewDistance.setText(Formater.getDistance(order.getDistanceString()));
            buttonBook.setImageResource(R.drawable.order_inactive);



            buttonBook.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                }
            });

        }
    }


    private class booking extends MyAsyncTask {
        // Tag used to cancel the request

        String idOrder="";

        public void postData() {
            order.user = UserGlobal.getUser(getApplicationContext());

            order.pickupNote=editTextPickupNote.getText().toString();
            order.dropoffNote=editTextDropoffNote.getText().toString();

            if(radioCash.isChecked())
            {
                order.payment_type="cash";
            }
            else if(radioHogpay.isChecked())
            {
                order.payment_type="hogpay";
            }

            order.vehicle=vehicleState;

            String url = AppConfig.URL_ORDER_RIDE;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_customer", order.user.idCustomer));
                nameValuePairs.add(new BasicNameValuePair("add_from", order.pickupAddress));
                nameValuePairs.add(new BasicNameValuePair("add_to", order.dropoffAddress));
                nameValuePairs.add(new BasicNameValuePair("lat_from", order.getPickupLatString()));
                nameValuePairs.add(new BasicNameValuePair("long_from", order.getPickupLngString()));
                nameValuePairs.add(new BasicNameValuePair("lat_to", order.getDropoffLatString()));
                nameValuePairs.add(new BasicNameValuePair("long_to", order.getDropoffLngString()));
                nameValuePairs.add(new BasicNameValuePair("price", order.getPriceString()));
                nameValuePairs.add(new BasicNameValuePair("note_from", order.getPickupNoteString()));
                nameValuePairs.add(new BasicNameValuePair("note_to", order.getDropoffNoteString()));
                nameValuePairs.add(new BasicNameValuePair("distance", order.getDistanceString()));
                nameValuePairs.add(new BasicNameValuePair("payment_type", order.payment_type));
                nameValuePairs.add(new BasicNameValuePair("vehicle", order.vehicle));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        status=obj.getString("status");

                        if(status.contentEquals("1") )
                        {
                            idOrder = obj.getString("id_order");
                            isSucces=true;
                            smsg = obj.getString("msg");
                        }
                        else
                        {
                            msg = obj.getString("msg");
                            //Toast.makeText(FindOrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();

                        }

                    } catch (final JSONException e) {
                        msg =e.getMessage();//Toast.makeText(FindOrderDetailActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    //Toast.makeText(FindOrderDetailActivity.this, "Couldn't get json from server", Toast.LENGTH_SHORT).show();
                    msg ="JSON NULL";
                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                msg =e.getMessage();
            }

        }
        @Override
        protected Void doInBackground(Void... params) {



            postData();

            return super.doInBackground(params);
        }


        @Override
        public Context getContext() {
            return MakeOrderRideActivity.this;
        }

        @Override
        public void setSuccessPostExecute() {
            Intent i = new Intent(MakeOrderRideActivity.this,
                    FindDriverActivity.class);
            i.putExtra("id_order",idOrder);
            startActivity(i);
            finish();
        }

        @Override
        public void setFailPostExecute() {

        }
    }

    public void setPriceByVehicle()
    {
        if(vehicleState.equals("bike"))
        {
            textViewPrice.setText(Formater.getPrice(String.valueOf(order.priceBike)));
            order.price=order.priceBike;
            if (order.price > UserGlobal.balance) {
                radioHogpay.setEnabled(false);
                radioCash.setChecked(true);
                radioHogpay.setChecked(false);

            } else {
                radioHogpay.setEnabled(true);
            }
        }
        else if(vehicleState.equals("car"))
        {
            textViewPrice.setText(Formater.getPrice(String.valueOf(order.priceCar)));
            order.price=order.priceCar;
            if (order.price > UserGlobal.balance) {
                radioHogpay.setEnabled(false);
                radioCash.setChecked(true);
                radioHogpay.setChecked(false);
            } else {
                radioHogpay.setEnabled(true);
            }
        }
    }


    private void getMyLocation()
    {
        mMap.setMyLocationEnabled(true);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();
            order.pickupPosition = new LatLng(lat, lng);
            order.pickupAddress = getAddress(lat, lng);
            pickUpMarker = mMap.addMarker(new MarkerOptions()
                    .position(order.pickupPosition)
                    .title(order.pickupAddress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker))
            );
            dropOffMarker = mMap.addMarker(new MarkerOptions()
                    .position(order.pickupPosition)
                    .title(order.pickupAddress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker))
            );

            addValueEventListener(friendsDatabaseReference);
           // new getDriverLocation().execute();
            dropOffMarker.setVisible(false);
            setAwal();
            adjustCamera();
            fillPickUpAddress();
        }
    }

    private void fillPickUpAddress()
    {
        pickUpMarker.setPosition(order.pickupPosition);
        pickUpMarker.setTitle(order.pickupAddress);
        adjustCamera();
        textViewPickUpAddres.setText(order.pickupAddress);
        textViewPickUpAddres.setTypeface(null, Typeface.NORMAL);
        textViewPickUpAddres.setTextColor(getResources().getColor(R.color.black));
        isPickUpAddressSetted=true;
        setBookingState();
    }

    private void fillDropOffAddress()
    {
        dropOffMarker.setPosition(order.dropoofPosition);
        dropOffMarker.setTitle(order.dropoffAddress);
        dropOffMarker.setVisible(true);
        adjustCamera();
        textViewDropOffAddress.setText(order.dropoffAddress);
        textViewDropOffAddress.setTypeface(null, Typeface.NORMAL);
        textViewDropOffAddress.setTextColor(getResources().getColor(R.color.black));
        isDropOffAddressSetted=true;
        setBookingState();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_PICKUP) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                order.setPickupPlace(place);
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
                order.setDropoffPlace(place);
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

    @Override
    public void setPermissionLocation() {
        getMyLocation();
    }

    @Override
    public void setPermissionCall() {

    }
}
