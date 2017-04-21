package com.hogwheelz.driverapps.activity.main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.activity.adapter.CustomViewPager;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderFoodActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderRideActivity;
import com.hogwheelz.driverapps.activity.viewOrder.ViewOrderSendActivity;
import com.hogwheelz.driverapps.fragment.BiographyFragment;
import com.hogwheelz.driverapps.fragment.MessageFragment;
import com.hogwheelz.driverapps.fragment.MyBookingFragment;
import com.hogwheelz.driverapps.fragment.FindOrderFragment;
import com.hogwheelz.driverapps.fragment.PaymentFragment;
import com.hogwheelz.driverapps.helper.SessionManager;
import com.hogwheelz.driverapps.helper.DriverSQLiteHandler;
import com.hogwheelz.driverapps.persistence.Driver;
import com.hogwheelz.driverapps.persistence.DriverGlobal;
import com.hogwheelz.driverapps.service.LocationUpdateService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends RootActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private DriverSQLiteHandler db;
    private SessionManager session;
    public static Driver driver;


    LinearLayout button1;
    LinearLayout button2;
    LinearLayout button3;
    LinearLayout button4;
    LinearLayout button5;

    ImageView icon1;
    ImageView icon2;
    ImageView icon3;
    ImageView icon4;
    ImageView icon5;


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
        getSupportActionBar().setIcon(R.drawable.main_logo);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        setupViewPager(viewPager);

        button1 = (LinearLayout) findViewById(R.id.button1);
        button2 = (LinearLayout) findViewById(R.id.button2);
        button3 = (LinearLayout) findViewById(R.id.button3);
        button4 = (LinearLayout) findViewById(R.id.button4);
        button5 = (LinearLayout) findViewById(R.id.button5);

        icon1= (ImageView) findViewById(R.id.icon1);
        icon2= (ImageView) findViewById(R.id.icon2);
        icon3= (ImageView) findViewById(R.id.icon3);
        icon4= (ImageView) findViewById(R.id.icon4);
        icon5= (ImageView) findViewById(R.id.icon5);



        db = new DriverSQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        tabAdapter(0);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabAdapter(0);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabAdapter(1);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabAdapter(2);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabAdapter(3);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabAdapter(4);
            }
        });


    }
    private void tabAdapter(int no)
    {
        viewPager.setCurrentItem(no);
        if(no==0)
        {

            icon1.setImageResource(R.drawable.active_home);
            icon2.setImageResource(R.drawable.inactive_history);
            icon3.setImageResource(R.drawable.payment_inactive);
            icon4.setImageResource(R.drawable.message_inactive);
            icon5.setImageResource(R.drawable.inactive_my_account);
        }
        else  if(no==1)
        {
            icon1.setImageResource(R.drawable.inactive_home);
            icon2.setImageResource(R.drawable.active_history);
            icon3.setImageResource(R.drawable.payment_inactive);
            icon4.setImageResource(R.drawable.message_inactive);
            icon5.setImageResource(R.drawable.inactive_my_account);
        }
        else if(no==2)
        {
            icon1.setImageResource(R.drawable.inactive_home);
            icon2.setImageResource(R.drawable.inactive_history);
            icon3.setImageResource(R.drawable.payment_active);
            icon4.setImageResource(R.drawable.message_inactive);
            icon5.setImageResource(R.drawable.inactive_my_account);
        }
        else  if(no==3)
        {
            icon1.setImageResource(R.drawable.inactive_home);
            icon2.setImageResource(R.drawable.inactive_history);
            icon3.setImageResource(R.drawable.payment_inactive);
            icon4.setImageResource(R.drawable.message_active);
            icon5.setImageResource(R.drawable.inactive_my_account);
        }
        else  if(no==4)
        {
            icon1.setImageResource(R.drawable.inactive_home);
            icon2.setImageResource(R.drawable.inactive_history);
            icon3.setImageResource(R.drawable.payment_inactive);
            icon4.setImageResource(R.drawable.message_inactive);
            icon5.setImageResource(R.drawable.active_my_account);
        }
    }

    @Override
    public void openOrderActivity() {
        if(orderType==1) {
            Intent i = new Intent(this, ViewOrderRideActivity.class);
            i.putExtra("id_order", idOrder);
            startActivity(i);
        }
        else if (orderType==2)
        {
            Intent i = new Intent(this, ViewOrderSendActivity.class);
            i.putExtra("id_order", idOrder);
            startActivity(i);
        }
        else if (orderType==3)
        {
            Intent i = new Intent(this, ViewOrderFoodActivity.class);
            i.putExtra("id_order", idOrder);
            startActivity(i);
        }
    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        setConditionLocation();


    }

    private void startLocationServices()
    {
        Intent intent = new Intent(this, LocationUpdateService.class);
        intent.putExtra("id_driver", DriverGlobal.getDriver(getApplicationContext()).idDriver);
        startService(intent);
    }


    @Override
    protected void onStop() {
        stopService(new Intent(this, LocationUpdateService.class));
        super.onStop();

    }



    /**
     * Adding custom view to tab
     */


    /**
     * Adding fragments to ViewPager
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FindOrderFragment(), "ONE");
        adapter.addFrag(new MyBookingFragment(), "TWO");
        adapter.addFrag(new PaymentFragment(), "THREE");
        adapter.addFrag(new MessageFragment(), "FOUR");
        adapter.addFrag(new BiographyFragment(), "FIVE");
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
