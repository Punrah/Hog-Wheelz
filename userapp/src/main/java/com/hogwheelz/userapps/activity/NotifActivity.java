package com.hogwheelz.userapps.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrder;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderRide;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderSend;
import com.hogwheelz.userapps.app.Config;
import com.hogwheelz.userapps.fragment.HelpFragment;
import com.hogwheelz.userapps.fragment.HistoryFragment;
import com.hogwheelz.userapps.fragment.HomeFragment;
import com.hogwheelz.userapps.fragment.MyAccountFragment;
import com.hogwheelz.userapps.helper.SessionManager;
import com.hogwheelz.userapps.helper.UserSQLiteHandler;
import com.hogwheelz.userapps.persistence.User;
import com.hogwheelz.userapps.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class NotifActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    BroadcastReceiver mRegistrationBroadcastReceiver;
    String idOrder;
    int orderType;
    AlertDialog.Builder alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
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
            Intent i = new Intent(this, ViewOrderRide.class);
            i.putExtra("id_order", idOrder);
            startActivity(i);
            finish();
        }
        else if (orderType==2)
        {
            Intent i = new Intent(this, ViewOrderSend.class);
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
