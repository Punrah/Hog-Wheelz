package com.hogwheelz.userapps.activity.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderRide;
import com.hogwheelz.userapps.activity.ViewOrder.ViewOrderSend;
import com.hogwheelz.userapps.activity.adapter.CustomViewPager;
import com.hogwheelz.userapps.fragment.MyAccountFragment;
import com.hogwheelz.userapps.fragment.HomeFragment;
import com.hogwheelz.userapps.fragment.HelpFragment;
import com.hogwheelz.userapps.fragment.HistoryFragment;
import com.hogwheelz.userapps.helper.UserSQLiteHandler;
import com.hogwheelz.userapps.helper.SessionManager;
import com.hogwheelz.userapps.persistence.User;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends RootActivity {

    private static final String TAG = MainActivity.class.getSimpleName();



    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private UserSQLiteHandler db;
    private SessionManager session;
    public static User user;

    LinearLayout button1;
    LinearLayout button2;
    LinearLayout button3;
    LinearLayout button4;

    ImageView icon1;
    ImageView icon2;
    ImageView icon3;
    ImageView icon4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hogwheelz_tabs);


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

        icon1= (ImageView) findViewById(R.id.icon1);
        icon2= (ImageView) findViewById(R.id.icon2);
        icon3= (ImageView) findViewById(R.id.icon3);
        icon4= (ImageView) findViewById(R.id.icon4);



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


    }

    private void tabAdapter(int no)
    {
        viewPager.setCurrentItem(no);
        if(no==0)
        {

            icon1.setImageResource(R.drawable.active_home);
            icon2.setImageResource(R.drawable.inactive_history);
            icon3.setImageResource(R.drawable.inactive_help);
            icon4.setImageResource(R.drawable.inactive_my_account);
        }
        else  if(no==1)
        {
            icon1.setImageResource(R.drawable.inactive_home);
            icon2.setImageResource(R.drawable.active_history);
            icon3.setImageResource(R.drawable.inactive_help);
            icon4.setImageResource(R.drawable.inactive_my_account);
        }
        else if(no==2)
        {
            icon1.setImageResource(R.drawable.inactive_home);
            icon2.setImageResource(R.drawable.inactive_history);
            icon3.setImageResource(R.drawable.active_help);
            icon4.setImageResource(R.drawable.inactive_my_account);
        }
        else  if(no==3)
        {
            icon1.setImageResource(R.drawable.inactive_home);
            icon2.setImageResource(R.drawable.inactive_history);
            icon3.setImageResource(R.drawable.inactive_help);
            icon4.setImageResource(R.drawable.active_my_account);
        }
    }

    @Override
    public void openOrderActivity() {
        if(orderType==1) {
            Intent i = new Intent(this, ViewOrderRide.class);
            i.putExtra("id_order", idOrder);
            startActivity(i);
        }
        else if (orderType==2)
        {
            Intent i = new Intent(this, ViewOrderSend.class);
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


    /**
     * Adding fragments to ViewPager
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment(), "ONE");
        adapter.addFrag(new HistoryFragment(), "TWO");
        adapter.addFrag(new HelpFragment(), "THREE");
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


    public static Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

}
