package com.hogwheelz.driverapps.activity.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderFoodActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderRideActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderSendActivity;
import com.hogwheelz.driverapps.app.Config;
import com.hogwheelz.driverapps.helper.DriverSQLiteHandler;
import com.hogwheelz.driverapps.helper.MessageSQLiteHandler;
import com.hogwheelz.driverapps.persistence.DriverGlobal;
import com.hogwheelz.driverapps.persistence.Message;
import com.hogwheelz.driverapps.service.LocationUpdateService;
import com.hogwheelz.driverapps.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;


public abstract class RootActivity extends AppCompatActivity {
    private static final String TAG = RootActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_CALL = 2;
    private static final int PERMISSION_REQUEST_TEXT = 3;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String idOrder;
    int orderType;
    AlertDialog.Builder alert;
    private MessageSQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        db = new MessageSQLiteHandler(getApplicationContext());
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
                        String notif=json.getString("notif");
                        String msg = intent.getStringExtra("message");
                        if(notif.contentEquals("order")) {
                            idOrder = json.getString("id_order");
                            orderType = json.getInt("order_type");
                            alert = new AlertDialog.Builder(RootActivity.this);
                            alert.setTitle("ORDER");
                            alert.setMessage("Order no:" + idOrder + " " + msg);
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
                        }
                        else if (notif.contentEquals("message"))
                        {
                            Message message = new Message();
                            message.idMessage=json.getString("id_message");
                            message.subject=json.getString("subject");
                            message.body=json.getString("body");
                            message.date=json.getString("date");
                            db.addMessage(message);
                            alert = new AlertDialog.Builder(RootActivity.this);
                            alert.setTitle("New Message");
                            alert.setMessage(msg);
                            alert.setCancelable(true);
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                }
                            });
                            alert.show();

                        }
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


    public  void requestPermission(String strPermission, int perCode, Context _c, Activity _a){
        switch (perCode) {
            case  PERMISSION_REQUEST_CODE_LOCATION:
                if (ActivityCompat.shouldShowRequestPermissionRationale(_a,strPermission)){
                    Toast.makeText(getApplicationContext(),"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
                } else {

                    ActivityCompat.requestPermissions(_a,new String[]{strPermission},perCode);
                }
                break;
            case PERMISSION_REQUEST_CALL:
                if (ActivityCompat.shouldShowRequestPermissionRationale(_a,strPermission)){
                    Toast.makeText(getApplicationContext(),"Call permission allows us to access your phone call. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
                } else {

                    ActivityCompat.requestPermissions(_a,new String[]{strPermission},perCode);
                }
                break;

        }


    }

    public  boolean checkPermission(String strPermission,Context _c,Activity _a){
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setPermissionLocation();
                } else {

                    Toast.makeText(getApplicationContext(),"Permission Denied, You cannot access location data.",Toast.LENGTH_LONG).show();

                }
                break;

        }
    }

    public void setConditionLocation()
    {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext(),RootActivity.this)) {
            setPermissionLocation();
        }
        else
        {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,PERMISSION_REQUEST_CODE_LOCATION,getApplicationContext(),RootActivity.this);
        }
    }

    public void setConditionCall()
    {
        if (checkPermission(Manifest.permission.CALL_PHONE,getApplicationContext(),RootActivity.this)) {
            setPermissionCall();
        }
        else
        {
            requestPermission(Manifest.permission.CALL_PHONE,PERMISSION_REQUEST_CALL,getApplicationContext(),RootActivity.this);
        }
    }

    public void startLocationServices()
    {
        Intent intent = new Intent(this, LocationUpdateService.class);
        intent.putExtra("id_driver", DriverGlobal.getDriver(getApplicationContext()).idDriver);
        startService(intent);
    }


    @Override
    public void onStop() {
        stopService(new Intent(this, LocationUpdateService.class));
        super.onStop();

    }



    public abstract void setPermissionLocation();
    public abstract void setPermissionCall();


}
