package com.hogwheelz.driverapps.activity.viewOrder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.activity.other.MapsActivity;
import com.hogwheelz.driverapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.Formater;
import com.hogwheelz.driverapps.persistence.OrderSend;

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

public class ViewOrderSendActivity extends ViewOrderActivity  {
    private static final String TAG = ViewOrderSendActivity.class.getSimpleName();
 LinearLayout convertView;
    boolean detailOpen=false;
    OrderSend order;
    Location mLastLocation;
    LinearLayout callerSender;
    LinearLayout callerReceiver;

    @Override
    public void initializeOrder() {
        order = new OrderSend();
    }

    @Override
    public void getOrderDetail() {
        new getOrderDetail().execute();
    }





    private void viewDetail()
    {
        linearLayoutDetail.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        convertView = (LinearLayout) inflater.inflate(R.layout.view_order_send_detail, linearLayoutDetail, false);
        TextView description = (TextView) convertView.findViewById(R.id.item_description);
        TextView sender_name = (TextView) convertView.findViewById(R.id.sender_name_send);
        TextView receiver_name = (TextView) convertView.findViewById(R.id.receiver_name_send);
        TextView sender_call = (TextView) convertView.findViewById(R.id.sender_call);
        TextView sender_text = (TextView) convertView.findViewById(R.id.sender_text);
        TextView receiver_call= (TextView) convertView.findViewById(R.id.receiver_call);
        TextView receiver_text = (TextView) convertView.findViewById(R.id.receiver_text);
        callerSender =(LinearLayout) convertView.findViewById(R.id.caller_sender);
        callerReceiver =(LinearLayout) convertView.findViewById(R.id.caller_receiver);
        if (order.status.contentEquals("Complete"))
        {
            callerSender.removeAllViews();
            callerReceiver.removeAllViews();
        }
        else if(order.status.contentEquals("Cancel"))
        {
            callerSender.removeAllViews();
            callerReceiver.removeAllViews();
        }
        description.setText(String.valueOf(order.description));
        sender_name.setText(String.valueOf(order.senderName));
        receiver_name.setText(String.valueOf(order.receiverName));
        sender_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewOrderSendActivity.this);

                builder.setTitle("Call Customer");
                builder.setMessage("Do you want to call "+order.senderPhone+" ?");

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

        sender_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewOrderSendActivity.this);

                builder.setTitle("Text Customer");
                builder.setMessage("Do you want to text "+order.senderPhone+" ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Uri sms_uri = Uri.parse("smsto:"+order.user.Phone);
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
        receiver_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewOrderSendActivity.this);

                builder.setTitle("Call Customer");
                builder.setMessage("Do you want to call "+order.receiverPhone+" ?");

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

        receiver_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewOrderSendActivity.this);

                builder.setTitle("Text Customer");
                builder.setMessage("Do you want to text "+order.receiverPhone+" ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Uri sms_uri = Uri.parse("smsto:"+order.user.Phone);
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
        linearLayoutDetail.addView(convertView);
    }




    public class getOrderDetail extends MyAsyncTask {

        @Override
        public Context getContext() {
            return ViewOrderSendActivity.this;
        }

        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return super.doInBackground(params);
        }

        public void postData()
        {

            String url = AppConfig.getOrderDetail(idOrder);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);


                            isSucces=true;

                        JSONObject orderJson = obj;


                            order.id_order=idOrder;
                            order.user.name= orderJson.getString("cus_name");
                            order.user.Phone=orderJson.getString("cus_phone");
                            order.status = orderJson.getString("status_order");
                            order.dropoffAddress = orderJson.getString("destination_address");
                            order.pickupAddress=orderJson.getString("origin_address");
                            order.price=orderJson.getInt("price");
                            order.distance=orderJson.getDouble("distance");
                            order.pickupNote=orderJson.getString("note_from");
                            order.dropoffNote=orderJson.getString("note_to");
                            order.pickupPosition=new LatLng(orderJson.getDouble("lat_from"),orderJson.getDouble("long_from"));
                            order.dropoofPosition=new LatLng(orderJson.getDouble("lat_to"),orderJson.getDouble("long_to"));
                            order.orderType = orderJson.getInt("order_type");
                            order.paymentType = orderJson.getString("payment_type");
                            order.description = orderJson.getString("item_description");
                            order.senderName = orderJson.getString("sender_name");
                            order.senderPhone = orderJson.getString("sender_phone");
                            order.receiverName = orderJson.getString("receiver_name");
                            order.receiverPhone = orderJson.getString("receiver_phone");


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
        public void setSuccesPostExecute() {
            setAllTextView();
            adjustCamera();

        }
    }

    public void cancelAcceptedOrder(){
        new cancelAcceptedOrder().execute();
    }
    public  class cancelAcceptedOrder extends MyAsyncTask {

        @Override
        public Context getContext() {
            return ViewOrderSendActivity.this;
        }

        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return super.doInBackground(params);
        }

        public void postData()
        {

            String url = AppConfig.CANCEL_ACCEPTED_ORDER;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_order",idOrder));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);

                            isSucces=true;
                            smsg = obj.getString("msg");


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
        public void setSuccesPostExecute() {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewOrderSendActivity.this);

            builder.setTitle("CANCEL ORDER");
            builder.setMessage(smsg);
            builder.setCancelable(false);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    finish();
                }
            });
            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();

        }
    }

    public void otwOrder() {
        new otwOrder().execute();
    }
    public  class otwOrder extends MyAsyncTask {

        @Override
        public Context getContext() {
            return ViewOrderSendActivity.this;
        }

        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return super.doInBackground(params);
        }

        public void postData()
        {

            String url = AppConfig.OTW_ORDER;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_order",idOrder));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);


                            isSucces=true;
                            smsg = obj.getString("msg");


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
        public void setSuccesPostExecute() {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewOrderSendActivity.this);

            builder.setTitle("PICK UP ORDER");
            builder.setMessage(smsg);
            builder.setCancelable(false);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    Intent i=new Intent(ViewOrderSendActivity.this,ViewOrderSendActivity.class);
                    i.putExtra("id_order", order.id_order);
                    startActivity(i);
                    finish();
                }
            });
            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();

        }
    }

    public void startOrder() {new startOrder().execute();}
    public  class startOrder extends MyAsyncTask {

        @Override
        public Context getContext() {
            return ViewOrderSendActivity.this;
        }

        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return super.doInBackground(params);
        }

        public void postData()
        {

            String url = AppConfig.START_ORDER;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_order",idOrder));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);


                            isSucces=true;
                            smsg = obj.getString("msg");


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
        public void setSuccesPostExecute() {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewOrderSendActivity.this);

            builder.setTitle("ON THE WAY");
            builder.setMessage(smsg);
            builder.setCancelable(false);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    Intent i=new Intent(ViewOrderSendActivity.this,ViewOrderSendActivity.class);
                    i.putExtra("id_order", order.id_order);
                    startActivity(i);
                    finish();
                }
            });
            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();

        }
    }

    public void completeOrder() {new completeOrder().execute();}
    public  class completeOrder extends MyAsyncTask {

        @Override
        public Context getContext() {
            return ViewOrderSendActivity.this;
        }

        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return super.doInBackground(params);
        }

        public void postData()
        {

            String url = AppConfig.COMPLETE_ORDER;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_order",idOrder));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);

                            isSucces=true;
                            smsg = obj.getString("msg");


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
        public void setSuccesPostExecute() {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewOrderSendActivity.this);

            builder.setTitle("COMPLETE ORDER");
            builder.setMessage(smsg);
            builder.setCancelable(false);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();

        }
    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + order.user.Phone));
        startActivity(intent);
    }

    public void setAllTextView()
    {
        textViewOrderType.setText(order.getOrderTypeString());
        textViewPaymentType.setText("BY "+order.paymentType.toUpperCase());
        textViewCustomerName.setText(order.user.name);
        textViewDestination.setText(order.dropoffAddress);
        textViewDistance.setText(Formater.getDistance(order.getDistanceString()));
        textViewOrderId.setText(order.id_order);
        textViewOrigin.setText(order.pickupAddress);
        textViewPrice.setText(Formater.getPrice(order.getPriceString()));
        textViewNoteOrigin.setText(order.pickupNote);
        textViewNoteDestination.setText(order.dropoffNote);

        viewDetail();
        textViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewOrderSendActivity.this);

                builder.setTitle("Call Customer");
                builder.setMessage("Do you want to call "+order.user.Phone+" ?");

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

        textViewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ViewOrderSendActivity.this);

                builder.setTitle("Text Customer");
                builder.setMessage("Do you want to text "+order.user.Phone+" ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Uri sms_uri = Uri.parse("smsto:"+order.user.Phone);
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
        buttonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ViewOrderSendActivity.this,MapsActivity.class);
                i.putExtra("pick_up",order.pickupPosition);
                i.putExtra("drop_off",order.dropoofPosition);
                startActivity(i);
            }
        });

        if(order.status.contentEquals("Accept"))
        {
            buttonGo.setText("Pick Up");
            textView1.setBackgroundResource(R.drawable.stepper_active);
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
            textView1.setBackgroundResource(R.drawable.stepper_active);
            textView2.setBackgroundResource(R.drawable.stepper_active);
            buttonGo.setText("On the Way");
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
            textView1.setBackgroundResource(R.drawable.stepper_active);
            textView2.setBackgroundResource(R.drawable.stepper_active);
            textView3.setBackgroundResource(R.drawable.stepper_active);
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
            textView1.setBackgroundResource(R.drawable.stepper_active);
            textView2.setBackgroundResource(R.drawable.stepper_active);
            textView3.setBackgroundResource(R.drawable.stepper_active);
            textView4.setBackgroundResource(R.drawable.stepper_active);
            linearLayoutCaller.removeAllViews();
            buttonGo.setText("NEED HELP?");
            buttonGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ViewOrderSendActivity.this, "need help", Toast.LENGTH_SHORT).show();
                }
            });
            if(buttonCancel.getParent()!=null) {
                ((ViewManager) buttonCancel.getParent()).removeView(buttonCancel);
            }
        }
        else if (order.status.contentEquals("Cancel"))
        {
            ((ViewManager)textView1.getParent()).removeView(textView1);
            ((ViewManager)textView2.getParent()).removeView(textView2);
            ((ViewManager)textView3.getParent()).removeView(textView3);
            linearLayoutCaller.removeAllViews();
            textView4.setText("Canceled");
            textView4.setBackgroundResource(R.drawable.border_round_red);

            buttonGo.setText("NEED HELP?");
            buttonGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ViewOrderSendActivity.this, "need help", Toast.LENGTH_SHORT).show();
                }
            });
            if(buttonCancel.getParent()!=null) {
                ((ViewManager) buttonCancel.getParent()).removeView(buttonCancel);
            }
        }
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getOrderDetail().execute();
            }
        });

    }

    public void adjustCamera()
    {

    }


}
