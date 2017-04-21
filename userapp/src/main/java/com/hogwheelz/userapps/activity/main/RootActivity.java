package com.hogwheelz.userapps.activity.main;

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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.other.RatingsActivity;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderFood;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderRide;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderSend;
import com.hogwheelz.userapps.app.Config;
import com.hogwheelz.userapps.helper.SessionManager;
import com.hogwheelz.userapps.helper.UserSQLiteHandler;
import com.hogwheelz.userapps.persistence.User;
import com.hogwheelz.userapps.util.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;


public abstract class RootActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_CALL = 2;

    BroadcastReceiver mRegistrationBroadcastReceiver;
    String idOrder;
    int orderType;
    AlertDialog.Builder alert;
    private UserSQLiteHandler db;
    private SessionManager session;
    public static User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new UserSQLiteHandler(getApplicationContext());
        // session manager

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        if(session.isOrder())
        {

            String idOrder=session.getOrder();
            Intent intent = new Intent(RootActivity.this, RatingsActivity.class);
            intent.putExtra("id_order",idOrder);
            startActivity(intent);

        }

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
                        String statusOrder=json.getString("status");
                        if(statusOrder.equals("complete")) {
                            session.setOrder(idOrder);
                        }
                        alert = new AlertDialog.Builder(RootActivity.this);
                        alert.setTitle("Order No: "+idOrder);
                        alert.setMessage(msg);
                        alert.setCancelable(false);
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
        else if (orderType==3)
        {
            Intent i = new Intent(this, ViewOrderFood.class);
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
            case PERMISSION_REQUEST_CALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setPermissionLocation();
                } else {

                    Toast.makeText(getApplicationContext(),"Permission Denied, You cannot access phone call.",Toast.LENGTH_LONG).show();

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

    public abstract void setPermissionLocation();
    public abstract void setPermissionCall();

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl= getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }
}
