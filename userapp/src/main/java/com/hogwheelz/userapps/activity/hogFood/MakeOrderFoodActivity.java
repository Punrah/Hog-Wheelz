package com.hogwheelz.userapps.activity.hogFood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.other.FindDriverActivity;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.activity.makeOrder.MakeOrder;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.Item;
import com.hogwheelz.userapps.persistence.OrderFood;
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

public class MakeOrderFoodActivity extends RootActivity {
    private static final String TAG = MakeOrder.class.getSimpleName();

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DELIVERY_ADDRESS = 1 ;
    Toolbar toolbar;
    LinearLayout linearLayoutItems;
    List<Item> items;
    List<TextView> textViewsQty;

    List<EditText> editTextPickupNote;

    TextView textViewRecapPrice;
    TextView textViewAddMoreItems;
    TextView textViewAddDeliveryAddress;
    TextView textViewDeliveryFee;
    TextView textViewTotalPrice;
    Button buttonOrder;
    TextView textViewDistance;

    EditText editTextDropOff;

    ImageView back;

    int totalPrice;
    OrderFood order;

    RadioButton radioHogpay;
    RadioButton radioCash;
    ImageView imgCar;
    ImageView imgBike;
    LinearLayout buttonBike;
    LinearLayout buttonCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order_food);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        linearLayoutItems = (LinearLayout) findViewById(R.id.list_item);
        textViewRecapPrice = (TextView) findViewById(R.id.cost);
        textViewAddMoreItems = (TextView) findViewById(R.id.add_more_items);
        textViewAddDeliveryAddress = (TextView) findViewById(R.id.delivery_address);
        textViewDeliveryFee = (TextView) findViewById(R.id.delivery_fee);
        textViewTotalPrice = (TextView) findViewById(R.id.total_price);
        buttonOrder = (Button) findViewById(R.id.button_order_food);
        textViewDistance = (TextView) findViewById(R.id.distance);


        radioHogpay = (RadioButton) findViewById(R.id.radio_hogpay);
        radioCash = (RadioButton) findViewById(R.id.radio_cash);
        imgCar =(ImageView) findViewById(R.id.img_car);
        imgBike= (ImageView) findViewById(R.id.img_bike);
        buttonBike=(LinearLayout) findViewById(R.id.bike_button);
        buttonCar=(LinearLayout) findViewById(R.id.car_button);

        editTextDropOff =(EditText) findViewById(R.id.dropoff_note);

        editTextDropOff.addTextChangedListener(textWatcher);

        order = getIntent().getParcelableExtra("order");


        fetchItem();
        if(!order.dropoffAddress.equals("")) {
            textViewAddDeliveryAddress.setText(order.dropoffAddress);
        }
        editTextDropOff.setText(order.dropoffNote);
        if(order.payment_type.equals("hogpay"))
        {
            radioHogpay.setChecked(true);
            radioCash.setChecked(false);
        }
        else
        {
            radioCash.setChecked(true);
            radioHogpay.setChecked(false);
        }

        if(order.vehicle.equals("car"))
        {
            carPressed();
        }
        else
        {
            bikePressed();
        }

        textViewAddMoreItems.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });

        textViewAddDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPlaceAutocompleteActivityIntentForDeliveryAddress();
            }
        });


        radioCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.payment_type="cash";
                radioCash.setChecked(true);
                radioHogpay.setChecked(false);
            }
        });
        radioHogpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.payment_type="hogpay";
                radioCash.setChecked(false);
                radioHogpay.setChecked(true);
            }
        });

        buttonBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bikePressed();
            }
        });
        buttonCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carPressed();
            }
        });

        if(UserGlobal.balance<=0)
        {
            radioHogpay.setEnabled(false);
            radioCash.setChecked(true);
        }
        setTypeImage(1);




    }

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            order.dropoffNote=editTextDropOff.getText().toString();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            order.dropoffNote=editTextDropOff.getText().toString();

        }

        @Override
        public void afterTextChanged(Editable s) {
            order.dropoffNote=editTextDropOff.getText().toString();

        }
    };

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }

    private void bikePressed() {

        order.vehicle="bike";
        setTypeImage(1);
        setRecapPrice();
    }
    private void carPressed() {
        order.vehicle="car";
        setTypeImage(2);
        setRecapPrice();
    }

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

    private void callPlaceAutocompleteActivityIntentForDeliveryAddress() {
        try {
//            Intent intent =
//                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
//                            .build(this);
//            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DELIVERY_ADDRESS);
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            startActivityForResult(builder.build(this), PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DELIVERY_ADDRESS);
//PLACE_AUTOCOMPLETE_REQUEST_CODE is integer for request code
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    private void fetchItem() {
        items = order.getSelectedItem();

        textViewsQty = new ArrayList<TextView>();
        editTextPickupNote = new ArrayList<EditText>();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);

        if (items.size() > 0) {

            for (int i = 0; i < items.size(); i++) {

                LayoutInflater inflater = getLayoutInflater();
                LinearLayout convertView = (LinearLayout) inflater.inflate(R.layout.list_item, linearLayoutItems, false);
                TextView name = (TextView) convertView.findViewById(R.id.item_name);
                TextView price = (TextView) convertView.findViewById(R.id.price);
                TextView description = (TextView) convertView.findViewById(R.id.item_description);

                LinearLayout noteItem = (LinearLayout) convertView.findViewById(R.id.note_item);
                final EditText editTextPickupNote1=new EditText(this);
                editTextPickupNote1.setHint("add notes");
                editTextPickupNote1.setLayoutParams(params);
                editTextPickupNote1.setTextSize(12);
                editTextPickupNote1.setBackgroundColor(Color.TRANSPARENT);

                final int j=i;
                editTextPickupNote1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        textWatcherAction(j);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        textWatcherAction(j);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        textWatcherAction(j);
                    }
                });
                noteItem.addView(editTextPickupNote1);

                editTextPickupNote.add(editTextPickupNote1);

                final ImageView minus = (ImageView) convertView.findViewById(R.id.minus);
                final ImageView plus = (ImageView) convertView.findViewById(R.id.plus);
                textViewsQty.add((TextView) convertView.findViewById(R.id.qty));
                name.setText(String.valueOf(items.get(i).name));
                price.setText(Formater.getPrice(String.valueOf(items.get(i).price)));
                description.setText(String.valueOf(items.get(i).description));
                textViewsQty.get(i).setText(String.valueOf(items.get(i).qty));
                editTextPickupNote.get(i).setText(items.get(i).notes);
                minus.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        minusQty(j);
                    }

                });

                plus.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        plusQty(j);
                    }
                });
                linearLayoutItems.addView(convertView);

            }
        }
        setRecapPrice();

    }

    public void textWatcherAction(int i) {
        items.get(i).notes=editTextPickupNote.get(i).getText().toString();
    }

    public void minusQty(int i) {
        items.get(i).minOne();
        textViewsQty.get(i).setText(String.valueOf(items.get(i).qty));
        setRecapPrice();
    }

    public void plusQty(int i) {
        items.get(i).plusOne();
        textViewsQty.get(i).setText(String.valueOf(items.get(i).qty));
        setRecapPrice();
    }

    private void setRecapPrice()
    {
        setPriceByVehicle();
        textViewDistance.setText(" ("+Formater.getDistance(String.valueOf(order.distance))+")");
        textViewRecapPrice.setText(Formater.getPrice(String.valueOf(order.getRecapPrice())));
        textViewDeliveryFee.setText(Formater.getPrice(order.getPriceString()));
        totalPrice = order.getRecapPrice()+order.price;
        textViewTotalPrice.setText(Formater.getPrice(String.valueOf(totalPrice)));
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("order",order);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

         if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_IS_DELIVERY_ADDRESS) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                order.setDropoffPlace(place);
                fillDeliveryAddress();



            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                textViewAddDeliveryAddress.setText(status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

    private void fillDeliveryAddress() {
        textViewAddDeliveryAddress.setText(order.dropoffAddress);

        new calculatePrice().execute();

    }

    private class calculatePrice extends AsyncTask<Void, Void, Void> {
        @Override



        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            textViewDeliveryFee.setText("Please wait");
            textViewTotalPrice.setText("Please wait");
        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String originsLat = order.getPickupLatString();
            String originsLng = order.getPickupLngString();
            String destinationsLat = order.getDropoffLatString();
            String destinationsLng = order.getDropoffLngString();
            order.orderType=3;
            String url = AppConfig.getPriceURL(originsLat + "," + originsLng, destinationsLat + "," + destinationsLng,String.valueOf(order.orderType),order.vehicle);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    order.priceCar= jsonObj.getInt("price_car");
                    order.priceBike= jsonObj.getInt("price_bike");
                    order.distance = jsonObj.getDouble("distance");
                    setPriceByVehicle();

                } catch (final JSONException e) {
                    Toast.makeText(MakeOrderFoodActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MakeOrderFoodActivity.this, "Couldn't get json from server.", Toast.LENGTH_SHORT).show();

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            setRecapPrice();


            // Link to Register Screen
            buttonOrder.setBackgroundColor(Color.parseColor("#f1c40f"));
            buttonOrder.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    new booking().execute();

                }
            });

            // Dismiss the progress dialog

        }
    }

    public void setPriceByVehicle()
    {
        if(order.vehicle.equals("bike"))
        {
            order.price=order.priceBike;
        }
        else if(order.vehicle.equals("car"))
        {
            order.price=order.priceCar;
        }
    }



    private class booking extends MyAsyncTask{

        String idOrder;
        public void postData()
        {
            order.user = UserGlobal.getUser(getApplicationContext());

            order.dropoffNote=editTextDropOff.getText().toString();
            order.totalPrice =  totalPrice;

            if(radioCash.isChecked())
            {
                order.payment_type="cash";
            }
            else if(radioHogpay.isChecked())
            {
                order.payment_type="hogpay";
            }

            String url = AppConfig.URL_ORDER_FOOD;
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
                nameValuePairs.add(new BasicNameValuePair("total_price",String.valueOf(order.totalPrice)));
                nameValuePairs.add(new BasicNameValuePair("payment_type", order.payment_type));
                nameValuePairs.add(new BasicNameValuePair("vehicle", order.vehicle));



                Gson gson = new Gson();
                // convert your list to json
                String itemjson = gson.toJson(order.getSelectedItem());
                nameValuePairs.add(new BasicNameValuePair("item_json",itemjson));


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
                            emsg = obj.getString("msg");
                            //Toast.makeText(FindOrderDetailActivity.this, msg, Toast.LENGTH_SHORT).show();

                        }

                    } catch (final JSONException e) {
                        emsg=e.getMessage();//Toast.makeText(FindOrderDetailActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    //Toast.makeText(FindOrderDetailActivity.this, "Couldn't get json from server", Toast.LENGTH_SHORT).show();
                    emsg="JSON NULL";
                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                emsg=e.getMessage();
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return super.doInBackground(params);
        }

        @Override
        public Context getContext() {
            return MakeOrderFoodActivity.this;
        }

        @Override
        public void setMyPostExecute() {
            Intent i = new Intent(MakeOrderFoodActivity.this,
                    FindDriverActivity.class);
            i.putExtra("id_order",idOrder);
            startActivity(i);
            finish();
        }
    }
}

