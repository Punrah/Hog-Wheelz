package com.hogwheelz.userapps.activity.makeOrder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.other.FindDriverActivity;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.persistence.OrderSend;
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

public class MakeOrderSendDetailActivity extends AppCompatActivity {

    private static final String TAG = MakeOrderSendDetailActivity.class.getSimpleName();


    private static final int REQUEST_CODE_SENDER = 1;
    private static final int REQUEST_CODE_RECEIVER = 2;
    Toolbar toolbar;
    OrderSend order;



    EditText editTextSenderName;
    EditText editTextSenderPhone;
    EditText editTextReceiverName;
    EditText editTextReceiverPhone;
    EditText editTextDescription;

    Button buttonOrderSend;
    ImageView back;

    TextView textViewPrice;
    TextView textViewDistance;
    TextView textViewPaymentType;


    ImageView buttonSenderContact;
    ImageView buttonReceiverContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order_send_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        back =(ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
               onBackPressed();
            }
        });

        order =getIntent().getParcelableExtra("order");//gson.fromJson(getIntent().getStringExtra("order_send"), OrderSend.class);

        editTextDescription = (EditText) findViewById(R.id.description_send);
        editTextSenderName=(EditText) findViewById(R.id.sender_name_send);
        editTextSenderPhone=(EditText) findViewById(R.id.sender_phone_send);
        editTextReceiverName=(EditText) findViewById(R.id.receiver_name_send);
        editTextReceiverPhone=(EditText) findViewById(R.id.receiver_phone_send);
        textViewPrice=(TextView) findViewById(R.id.price);
        textViewDistance=(TextView) findViewById(R.id.distance);
        textViewPaymentType=(TextView) findViewById(R.id.payment_type);

        buttonSenderContact=(ImageView) findViewById(R.id.sender_contact);
        buttonReceiverContact=(ImageView) findViewById(R.id.receiver_contact);

        textViewPrice.setText(Formater.getPrice(order.getPriceString()));
        textViewDistance.setText(Formater.getDistance(order.getDistanceString()));
        textViewPaymentType.setText("BY "+order.payment_type.toUpperCase());

        buttonOrderSend = (Button) findViewById(R.id.button_detail_send);

        editTextDescription.setText(order.description);
        editTextSenderName.setText(order.senderName);
        editTextSenderPhone.setText(order.senderPhone);
        editTextReceiverName.setText(order.receiverName);
        editTextReceiverPhone.setText(order.receiverPhone);

        editTextDescription.addTextChangedListener(textWatcher);
        editTextSenderName.addTextChangedListener(textWatcher);
        editTextSenderPhone.addTextChangedListener(textWatcher);
        editTextReceiverName.addTextChangedListener(textWatcher);
        editTextReceiverPhone.addTextChangedListener(textWatcher);
        validateFields();


        buttonSenderContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE_SENDER);
            }
        });
        buttonReceiverContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE_RECEIVER);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == REQUEST_CODE_SENDER) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);
                editTextSenderPhone.setText(number);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);
                editTextSenderName.setText(name);

                Log.d(TAG, "ZZZ number : " + number +" , name : "+name);

            }
        }
        else if (requestCode == REQUEST_CODE_RECEIVER) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor = getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);
                editTextReceiverPhone.setText(number);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);
                editTextReceiverName.setText(name);

                Log.d(TAG, "ZZZ number : " + number +" , name : "+name);

            }
        }
    };


    private class booking extends MyAsyncTask {
        String idOrder;

        public void postData()
        {
            order.user = UserGlobal.getUser(getApplicationContext());

            String url = AppConfig.URL_ORDER_SEND;
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
                nameValuePairs.add(new BasicNameValuePair("description",order.description));
                nameValuePairs.add(new BasicNameValuePair("sender_name",order.senderName));
                nameValuePairs.add(new BasicNameValuePair("sender_phone",order.senderPhone));
                nameValuePairs.add(new BasicNameValuePair("receiver_name",order.receiverName));
                nameValuePairs.add(new BasicNameValuePair("receiver_phone",order.receiverPhone));
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
            return MakeOrderSendDetailActivity.this;
        }

        @Override
        public void setMyPostExecute() {
            Intent i = new Intent(MakeOrderSendDetailActivity.this,
                    FindDriverActivity.class);
            i.putExtra("id_order",idOrder);
            startActivity(i);
            finish();
        }
    }

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            order.description=editTextDescription.getText().toString();
            order.senderName=editTextSenderName.getText().toString();
            order.senderPhone=editTextSenderPhone.getText().toString();
            order.receiverName=editTextReceiverName.getText().toString();
            order.receiverPhone=editTextReceiverPhone.getText().toString();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            order.description=editTextDescription.getText().toString();
            order.senderName=editTextSenderName.getText().toString();
            order.senderPhone=editTextSenderPhone.getText().toString();
            order.receiverName=editTextReceiverName.getText().toString();
            order.receiverPhone=editTextReceiverPhone.getText().toString();

        }

        @Override
        public void afterTextChanged(Editable s) {
            order.description=editTextDescription.getText().toString();
            order.senderName=editTextSenderName.getText().toString();
            order.senderPhone=editTextSenderPhone.getText().toString();
            order.receiverName=editTextReceiverName.getText().toString();
            order.receiverPhone=editTextReceiverPhone.getText().toString();
            validateFields();
        }
    };

    protected void validateFields() {

        if(editTextDescription.getText().length()>0
                && editTextSenderName.getText().length()>0
                && editTextSenderPhone.getText().length()>0
                && editTextReceiverName.getText().length()>0
                && editTextReceiverPhone.getText().length()>0
                ){

            buttonOrderSend.setBackgroundResource(R.color.colorAccent);
            buttonOrderSend.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    order.description=editTextDescription.getText().toString();
                    order.senderName=editTextSenderName.getText().toString();
                    order.senderPhone=editTextSenderPhone.getText().toString();
                    order.receiverName=editTextReceiverName.getText().toString();
                    order.receiverPhone=editTextReceiverPhone.getText().toString();
                    new booking().execute();

                }
            });
        }
        else{
            buttonOrderSend.setBackgroundResource(R.color.softgray);
            buttonOrderSend.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {


                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("order",order);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }
}
