package com.hogwheelz.driverapps.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderFoodActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderRideActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderSendActivity;
import com.hogwheelz.driverapps.app.Config;
import com.hogwheelz.driverapps.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class NotifActivity extends AppCompatActivity {
    private static final String TAG = NotifActivity.class.getSimpleName();

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String idOrder;
    int orderType;
    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

               if (intent.getAction().equals(Config.ORDER_COMING)) {
                    // new push notification is received
                   String msg=intent.getStringExtra("message");
                   Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
               }
               else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    JSONObject json = null;
                    try {
                        json = new JSONObject(intent.getStringExtra("payload"));
                        idOrder=json.getString("id_order");
                        orderType=json.getInt("order_type");


                        String msg=intent.getStringExtra("message");
                        alert = new AlertDialog.Builder(NotifActivity.this);
                        alert.setTitle("Alert");
                        alert.setMessage("Order no:"+idOrder+" "+msg);
                        alert.setCancelable(true);
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                openOrderActivity();
                            }
                        });
                        alert.show();




                    } catch (JSONException e) {
                        Log.e(TAG, "Json Exception: " + e.getMessage());
                        Toast.makeText(getApplicationContext(),  e.getMessage(), Toast.LENGTH_LONG).show();
                    }


                }
            }

        };




    }

    public void openOrderActivity()
    {
        if(orderType==1) {
            Intent i = new Intent(this, ViewOrderRideActivity.class);
            i.putExtra("id_order", idOrder);
            startActivity(i);
            finish();
        }
        else if (orderType==2)
        {
            Intent i = new Intent(this, ViewOrderSendActivity.class);
            i.putExtra("id_order", idOrder);
            startActivity(i);
            finish();
        }
        else if (orderType==3)
        {
            Intent i = new Intent(this, ViewOrderFoodActivity.class);
            i.putExtra("id_order", idOrder);
            startActivity(i);
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


}
