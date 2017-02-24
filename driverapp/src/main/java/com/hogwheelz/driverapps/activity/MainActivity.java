package com.hogwheelz.driverapps.activity;

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
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;


import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.app.Config;
import com.hogwheelz.driverapps.fragment.MessageFragment;
import com.hogwheelz.driverapps.fragment.MyAccountFragment;
import com.hogwheelz.driverapps.fragment.MyBookingFragment;
import com.hogwheelz.driverapps.fragment.FindOrderFragment;
import com.hogwheelz.driverapps.helper.SessionManager;
import com.hogwheelz.driverapps.helper.DriverSQLiteHandler;
import com.hogwheelz.driverapps.persistence.Driver;
import com.hogwheelz.driverapps.persistence.DriverGlobal;
import com.hogwheelz.driverapps.service.LocationUpdateService;
import com.hogwheelz.driverapps.util.NotificationUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DriverSQLiteHandler db;
    private SessionManager session;
    public static Driver driver;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    AlertDialog.Builder alert;
    boolean isAlertShow=false;

    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        db = new DriverSQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            logoutUser();
        }





        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

               if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                   String msg=intent.getStringExtra("message");
                   Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
               }
            }

        };




    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext(),MainActivity.this)) {
            startLocationServices();
        }
        else
        {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,PERMISSION_REQUEST_CODE_LOCATION,getApplicationContext(),MainActivity.this);
        }


    }

    private void startLocationServices()
    {
        Intent intent = new Intent(this, LocationUpdateService.class);
        intent.putExtra("id_driver", DriverGlobal.getDriver(getApplicationContext()).idDriver);
        startService(intent);
    }

    public  void requestPermission(String strPermission, int perCode, Context _c, Activity _a){

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a,strPermission)){
            Toast.makeText(getApplicationContext(),"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(_a,new String[]{strPermission},perCode);
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
                    startLocationServices();
                } else {

                    Toast.makeText(getApplicationContext(),"Permission Denied, You cannot access location data.",Toast.LENGTH_LONG).show();

                }
                break;

        }
    }

    @Override
    protected void onStop() {
        stopService(new Intent(this, LocationUpdateService.class));
        super.onStop();

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

    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Home");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home_black_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("My Booking");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home_black_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Message");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home_black_24dp, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("My Account");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_home_black_24dp, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    /**
     * Adding fragments to ViewPager
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FindOrderFragment(), "ONE");
        adapter.addFrag(new MyBookingFragment(), "TWO");
        adapter.addFrag(new MessageFragment(), "THREE");
        adapter.addFrag(new MyAccountFragment(), "FOUR");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteDriver();

        // Launching the login activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
