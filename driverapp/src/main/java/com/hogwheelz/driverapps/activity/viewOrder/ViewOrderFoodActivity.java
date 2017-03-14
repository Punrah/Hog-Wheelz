package com.hogwheelz.driverapps.activity.viewOrder;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.AppController;
import com.hogwheelz.driverapps.helper.HttpHandler;
import com.hogwheelz.driverapps.persistence.Item;
import com.hogwheelz.driverapps.persistence.OrderFood;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewOrderFoodActivity extends ViewOrderActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = ViewOrderFoodActivity.class.getSimpleName();
 LinearLayout convertView;
    boolean detailOpen=false;
    OrderFood order;
    List<TextView> textViewsQty;

    @Override
    public void initializeOrder() {
        order = new OrderFood();
    }

    @Override
    public void getOrderDetail() {
        new getOrderDetail().execute();
    }


    public void setDetail() {

        buttonDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDetailButton();
            }
        });
    }

    private void setDetailButton()
    {
        if(detailOpen)
        {
            linearLayoutDetail.removeView(convertView);
            detailOpen=false;
        }
        else
        {
            viewDetail();
            detailOpen=true;
        }

    }


    private void viewDetail()
    {
        LayoutInflater inflater = getLayoutInflater();
        convertView = (LinearLayout) inflater.inflate(R.layout.view_order_food_detail, linearLayoutDetail, false);
        LinearLayout linearLayoutItem = (LinearLayout) convertView.findViewById(R.id.list_item);
        textViewsQty = new ArrayList<TextView>();
        linearLayoutItem.removeAllViews();

        if (order.item.size() > 0) {

            for (int i = 0; i < order.item.size(); i++) {
                LayoutInflater inflater2 = getLayoutInflater();
                FrameLayout convertView = (FrameLayout) inflater2.inflate(R.layout.list_item_detail, linearLayoutItem, false);
                TextView name = (TextView) convertView.findViewById(R.id.item_name);
                TextView price = (TextView) convertView.findViewById(R.id.price);
                TextView description = (TextView) convertView.findViewById(R.id.item_description);
                textViewsQty.add((TextView) convertView.findViewById(R.id.qty));
                name.setText(String.valueOf(order.item.get(i).name));
                price.setText(String.valueOf(order.item.get(i).price));
                textViewsQty.get(i).setText(String.valueOf(order.item.get(i).qty));
                linearLayoutItem.addView(convertView);
            }
        }
        linearLayoutDetail.addView(convertView);
    }




    private class getOrderDetail extends AsyncTask<Void, Void, Void> {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.getOrderDetail(idOrder);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {

                    JSONObject orderJson = new JSONObject(jsonStr);



                    order.id_order=idOrder;
                    order.user.name= orderJson.getString("cus_name");
                    order.user.Phone=orderJson.getString("cus_phone");
                    order.status = orderJson.getString("status_order");
                    order.dropoffAddress = orderJson.getString("destination_address");
                    order.pickupAddress=orderJson.getString("origin_address");
                    order.price=orderJson.getInt("price");
                    order.distance=orderJson.getDouble("distance");
                    order.pickupNote=orderJson.getString("note");
                    order.dropoffNote=orderJson.getString("note");
                    order.pickupPosition=new LatLng(orderJson.getDouble("lat_from"),orderJson.getDouble("long_from"));
                    order.dropoofPosition=new LatLng(orderJson.getDouble("lat_to"),orderJson.getDouble("long_to"));
                    JSONArray itemJArray = new JSONArray();
                    itemJArray = orderJson.getJSONArray("item");
                    for(int i=0;i<itemJArray.length();i++) {

                        JSONObject itemJson = itemJArray.getJSONObject(i);
                        Item item = new Item();
                        item.idItem=itemJson.getString("id_item");
                        item.name = itemJson.getString("item_name");
                        item.price = itemJson.getInt("price");
                        item.qty = itemJson.getInt("quantity");
                        order.item.add(item);
                    }

                } catch (final JSONException e) {

                    Log.e(TAG, "Order Detail: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Json null");

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            pickUpMarker = mMap.addMarker(new MarkerOptions().position(order.pickupPosition));
            dropOffMarker= mMap.addMarker(new MarkerOptions().position(order.dropoofPosition));
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            double lat = mLastLocation.getLatitude();
            double lng = mLastLocation.getLongitude();
            driverMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.motorcycle)));
            setAllTextView();
            adjustCamera();

        }
    }

    public void cancelAcceptedOrder() {
        // Tag used to cancel the request
        Toast.makeText(this, "makann", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ViewOrderFoodActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    else if (status.contentEquals("2"))
                    {
                        Toast.makeText(ViewOrderFoodActivity.this, msg, Toast.LENGTH_SHORT).show();
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
    public void otwOrder() {
        // Tag used to cancel the request
        String tag_string_req = "go_order";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.OTW_ORDER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");

                    // Check for error node in json
                    if (status.contentEquals("1")) {
                        Toast.makeText(ViewOrderFoodActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(ViewOrderFoodActivity.this,ViewOrderFoodActivity.class);
                        i.putExtra("id_order", order.id_order);
                        startActivity(i);
                        finish();
                    }
                    else if (status.contentEquals("2"))
                    {
                        Toast.makeText(ViewOrderFoodActivity.this, msg, Toast.LENGTH_SHORT).show();
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

                params.put("id_order", order.id_order);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void startOrder() {
        // Tag used to cancel the request
        String tag_string_req = "go_order";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.START_ORDER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");

                    // Check for error node in json
                    if (status.contentEquals("1")) {
                        Toast.makeText(ViewOrderFoodActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(ViewOrderFoodActivity.this,ViewOrderFoodActivity.class);
                        i.putExtra("id_order", order.id_order);
                        startActivity(i);
                        finish();
                    }
                    else if (status.contentEquals("2"))
                    {
                        Toast.makeText(ViewOrderFoodActivity.this, msg, Toast.LENGTH_SHORT).show();
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

                params.put("id_order", order.id_order);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    public void completeOrder() {
        // Tag used to cancel the request
        String tag_string_req = "go_order";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.COMPLETE_ORDER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    String msg = jObj.getString("msg");

                    // Check for error node in json
                    if (status.contentEquals("1")) {
                        Toast.makeText(ViewOrderFoodActivity.this, msg, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else if (status.contentEquals("2"))
                    {
                        Toast.makeText(ViewOrderFoodActivity.this, msg, Toast.LENGTH_SHORT).show();
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

                params.put("id_order", order.id_order);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void setAllTextView()
    {
        textViewCustomerName.setText(order.user.name);
        textViewDestination.setText(order.dropoffAddress);
        textViewDistance.setText(order.getDistanceString());
        textViewOrderId.setText(order.id_order);
        textViewOrigin.setText(order.pickupAddress);
        textViewPrice.setText(order.getPriceString());
        textViewNoteOrigin.setText(order.pickupNote);
        textViewNoteDestination.setText(order.dropoffNote);

        setDetail();

        if(order.status.contentEquals("Accept"))
        {
            buttonGo.setText("OTW");
            buttonGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    otwOrder();
                }
            });
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelAcceptedOrder();
                }
            });
        }
        else if (order.status.contentEquals("OTW"))
        {
            buttonGo.setText("Start");
            buttonGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startOrder();
                }
            });
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelAcceptedOrder();
                }
            });
        }
        else if (order.status.contentEquals("start"))
        {
            buttonGo.setText("Complete");
            buttonGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    completeOrder();
                }
            });
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelAcceptedOrder();
                }
            });
        }
        else if (order.status.contentEquals("Complete"))
        {
            buttonGo.setText("");
            buttonGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        else if (order.status.contentEquals("Cancel"))
        {
            buttonGo.setText("");
            buttonGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

    }

    public void adjustCamera()
    {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        builder.include(pickUpMarker.getPosition());
        builder.include(dropOffMarker.getPosition());
        if(!order.driver.idDriver.contentEquals("0")) {
            builder.include(driverMarker.getPosition());
        }


        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.1); // offset from edges of the map 12% of screen


        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }


}
