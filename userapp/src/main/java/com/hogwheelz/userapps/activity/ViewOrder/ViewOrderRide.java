package com.hogwheelz.userapps.activity.ViewOrder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.DriverImageAsyncTask;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.other.NeedHelpActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.OrderRide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class ViewOrderRide extends ViewOrder
        implements  GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = ViewOrderRide.class.getSimpleName();
    LinearLayout convertView;



OrderRide order;
    @Override
    public void getOrderDetail() {
        new getOrderRideDetail().execute();
    }

    @Override
    public void initializeOrder() {
        order = new OrderRide();
    }

    @Override
    public void adjustCamera()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        builder.include(pickUpMarker.getPosition());
        builder.include(dropOffMarker.getPosition());
        if(!order.driver.idDriver.contentEquals("0")) {
            if(!(order.status.contentEquals("Cancel")||order.status.contentEquals("Complete"))) {
                builder.include(driverMarker.getPosition());
            }
        }


        LatLngBounds bounds = builder.build();


        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.3); // offset from edges of the map 12% of screen
        int padding2 = (int) (width * 0.1); // offset from edges of the map 12% of screen

        mMap.setPadding(padding2,padding,padding2,padding);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,0);
        mMap.moveCamera(cu);
    }


    public void setAllTextView()
    {
        if(!(order.status.contentEquals("Complete")||order.status.contentEquals("Cancel"))) {
            friendsDatabaseReference = friendsDatabase.getReference("location_driver/" + order.driver.idDriver);
            addValueEventListener(friendsDatabaseReference);
        }
        textViewDriverName.setText(order.driver.name);
        textViewIdOrder.setText(order.getOrderNo());
        imageViewDriver.setTag(order.driver.photo);
        new DriverImageAsyncTask().execute(imageViewDriver);
        setStar(order.driver.rating);

        textViewPrice.setText(Formater.getPrice(order.getPriceString()));
        textViewDistance.setText(Formater.getDistance(order.getDistanceString()));
        textViewPickUpAddres.setText(order.pickupAddress);
        textViewDropOffAddress.setText(order.dropoffAddress);
        textViewPlat.setText(order.driver.plat);
        textViewDistanceLabel.setText("DISTANCE ");
        textViewFareLabel.setText("FARE ");

        imageViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewOrderRide.this);

                builder.setTitle("Call Customer");
                builder.setMessage("Do you want to call "+order.driver.phone+" ?");

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

        imageViewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewOrderRide.this);

                builder.setTitle("Text Customer");
                builder.setMessage("Do you want to text "+order.driver.phone+" ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Uri sms_uri = Uri.parse("smsto:"+order.driver.phone);
                        Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                        startActivity(sms_intent);
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

        if(order.vehicle.contentEquals("bike"))
        {
            vehicle.setImageResource(R.drawable.motor_ride_yellow);
        }
        else if(order.vehicle.contentEquals("car"))
        {
            vehicle.setImageResource(R.drawable.car_ride_yellow);
        }

        paymentType.setText("BY "+order.payment_type.toUpperCase()+" ");




        textViewStatus.setText(order.getStatusString());

        if(!order.pickupNote.equals(""))
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewPickupNote.setText(order.getPickupNoteString());
            textViewPickupNote.setLayoutParams(params);
            linearLayoutPickupNote.removeAllViews();
            linearLayoutPickupNote.addView(textViewPickupNote);
        }

        if(!order.dropoffNote.equals(""))
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
            textViewDropoffNote.setText(order.getDropoffNoteString());
            textViewDropoffNote.setLayoutParams(params);
            linearLayoutDropoffNote.removeAllViews();
            linearLayoutDropoffNote.addView(textViewDropoffNote);
        }

        if(order.status.contentEquals("Complete"))
        {
            linearLayoutOrderActive.removeAllViewsInLayout();
            LayoutInflater inflater = getLayoutInflater();
            convertView = (LinearLayout) inflater.inflate(R.layout.need_help_star, linearLayoutOrderActive, false);
            TextView textViewNeedHelp = (TextView) convertView.findViewById(R.id.need_help);
            star1a=(ImageView) convertView.findViewById(R.id.star1);
            star2a=(ImageView) convertView.findViewById(R.id.star2);
            star3a=(ImageView) convertView.findViewById(R.id.star3);
            star4a=(ImageView) convertView.findViewById(R.id.star4);
            star5a=(ImageView) convertView.findViewById(R.id.star5);
            setStar2(order.rating);
            linearLayoutOrderActive.addView(convertView);
            textViewNeedHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ViewOrderRide.this, NeedHelpActivity.class);
                    i.putExtra("id_order",idOrder);
                    startActivity(i);
                }
            });
        }
        else if(order.status.contentEquals("Cancel"))
        {
            linearLayoutOrderActive.removeAllViewsInLayout();
            LayoutInflater inflater = getLayoutInflater();
            convertView = (LinearLayout) inflater.inflate(R.layout.need_help, linearLayoutOrderActive, false);
            TextView textViewNeedHelp = (TextView) convertView.findViewById(R.id.need_help);
            linearLayoutOrderActive.addView(convertView);
            textViewNeedHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ViewOrderRide.this, NeedHelpActivity.class);
                    i.putExtra("id_order",idOrder);
                    startActivity(i);
                }
            });
        }
        else if(order.status.contentEquals("Start"))
        {
            buttonCancel.setOnClickListener(null);
            buttonCancel.setImageResource(R.drawable.cancel_gray);
        }
        else {
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelOrder();
                }
            });
            buttonCancel.setImageResource(R.drawable.cancel);
        }

            refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getOrderRideDetail().execute();
            }
        });

        buttonDetail.removeAllViews();
    }

    @Override
    public void setPermissionCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + order.driver.phone));
        startActivity(intent);
    }


    private class getOrderRideDetail extends MyAsyncTask{
        @Override


        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.getOrderDetail(idOrder);

            String jsonStr = null;
            try {
                jsonStr = sh.makeServiceCall(url);
                if (jsonStr != null) {
                    try {
                        isSucces=true;

                        JSONObject orderJson = new JSONObject(jsonStr);

                        order.id_order=idOrder;

                        order.status = orderJson.getString("status_order");
                        if(!(order.status.contentEquals("Complete")||order.status.contentEquals("Cancel"))) {
                            order.driver.idDriver = orderJson.getString("id_driver");
                            order.driver.name = orderJson.getString("driver_name");
                            order.driver.plat = orderJson.getString("plat");
                            order.driver.phone = orderJson.getString("driver_phone");
                            order.driver.driverLocation = new LatLng(orderJson.getDouble("driver_lat"), orderJson.getDouble("driver_long"));
                            order.driver.photo = orderJson.getString("foto");
                            order.driver.rating = orderJson.getInt("rating_driver");
                        }
                        order.dropoffAddress = orderJson.getString("destination_address");
                        order.pickupAddress=orderJson.getString("origin_address");
                        order.price=orderJson.getInt("price");
                        order.distance=orderJson.getDouble("distance");
                        order.pickupNote=orderJson.getString("note_from");
                        order.dropoffNote=orderJson.getString("note_to");
                        order.pickupPosition=new LatLng(orderJson.getDouble("lat_from"),orderJson.getDouble("long_from"));
                        order.dropoofPosition=new LatLng(orderJson.getDouble("lat_to"),orderJson.getDouble("long_to"));
                        order.vehicle = orderJson.getString("vehicle");
                        order.payment_type = orderJson.getString("payment_type");
                        order.orderType=orderJson.getInt("order_type");
                        order.rating = orderJson.getInt("rating_order");


                    } catch (final JSONException e) {
                        badServerAlert();
                    }
                } else {
                    badServerAlert();

                }
            } catch (IOException e) {
                badInternetAlert();
            }

            return null;
        }


        @Override
        public Context getContext() {
            return ViewOrderRide.this;
        }

        @Override
        public void setSuccessPostExecute() {
            pickUpMarker = mMap.addMarker(new MarkerOptions()
                    .position(order.pickupPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_marker)));
            dropOffMarker= mMap.addMarker(new MarkerOptions()
                    .position(order.dropoofPosition)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker)));
            if(!order.driver.idDriver.contentEquals("0")) {
                if (order.vehicle.contentEquals("car")) {
                    driverMarker = mMap.addMarker(new MarkerOptions()
                            .position(order.driver.driverLocation)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_car)));
                } else if (order.vehicle.contentEquals("bike")) {
                    driverMarker = mMap.addMarker(new MarkerOptions()
                            .position(order.driver.driverLocation)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_motor)));
                }
            }
            setAllTextView();
            adjustCamera();
        }

        @Override
        public void setFailPostExecute() {

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Intent i = new Intent(ViewOrderRide.this,ViewOrderRide.class);
                i.putExtra("id_order",idOrder);
                startActivity(i);
                finish();
            }
        }
    }


}
