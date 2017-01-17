package com.hogwheelz.driverapps.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.app.Config;
import com.hogwheelz.driverapps.fragment.AgreementFragment;
import com.hogwheelz.driverapps.fragment.AutobidFragment;
import com.hogwheelz.driverapps.fragment.HistoryFragment;
import com.hogwheelz.driverapps.fragment.InboxFragment;
import com.hogwheelz.driverapps.fragment.MessageFragment;
import com.hogwheelz.driverapps.fragment.RatingFragment;
import com.hogwheelz.driverapps.fragment.TransactionHistoryFragment;
import com.hogwheelz.driverapps.fragment.PerformaFragment;
import com.hogwheelz.driverapps.fragment.DepositFragment;
import com.hogwheelz.driverapps.fragment.BookingFragment;
import com.hogwheelz.driverapps.fragment.VersionFragment;
import com.hogwheelz.driverapps.fragment.WithdrawFragment;
import com.hogwheelz.driverapps.other.CircleTransform;
import com.hogwheelz.driverapps.service.LocationUpdateService;
import com.hogwheelz.driverapps.util.NotificationUtils;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_TRANSACTION_HISTORY = "transaction_history";
    private static final String TAG_DEPOSIT = "deposit";
    private static final String TAG_WITHDRAW = "withdraw";
    private static final String TAG_PERFORMA = "performs";
    private static final String TAG_RATING = "rating";
    private static final String TAG_BOOKING = "booking";
    private static final String TAG_INBOX = "inbox";
    private static final String TAG_HISTORY = "history";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_AGREEMENT = "agreement";
    private static final String TAG_VERSION = "version";
    private static final String TAG_AUTOBID = "autobid";
    public static String CURRENT_TAG = TAG_BOOKING;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_BOOKING;
            loadHomeFragment();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    String regId=intent.getStringExtra("token");
                    Toast.makeText(context, regId, Toast.LENGTH_SHORT).show();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(),  message, Toast.LENGTH_LONG).show();

                }
            }
        };
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText("Ravi Tamada");
        txtWebsite.setText("www.androidhive.info");

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                TransactionHistoryFragment transactionHistoryFragment = new TransactionHistoryFragment();
                return transactionHistoryFragment;
            case 1:
                // photos
                DepositFragment depositFragment = new DepositFragment();
                return depositFragment;
            case 2:
                // movies fragment
               WithdrawFragment withdrawFragment = new WithdrawFragment();
                return withdrawFragment;
            case 3:
                // movies fragment
                PerformaFragment performaFragment = new PerformaFragment();
                return performaFragment;
            case 4:
                // notifications fragment
                RatingFragment ratingFragment = new RatingFragment();
                return ratingFragment;
            case 5:
                // settings fragment
                BookingFragment bookingFragment = new BookingFragment();
                return bookingFragment;
            case 6:
                // settings fragment
                InboxFragment inboxFragment = new InboxFragment();
                return inboxFragment;
            case 7:
                // settings fragment
                HistoryFragment historyFragment = new HistoryFragment();
                return historyFragment;
            case 8:
                // settings fragment
                MessageFragment messageFragment = new MessageFragment();
                return messageFragment;
            case 9:
                // settings fragment
                AgreementFragment agreementFragment = new AgreementFragment();
                return agreementFragment;
            case 10:
                // settings fragment
               VersionFragment versionFragment = new VersionFragment();
                return versionFragment;
            case 11:
                // settings fragment
                AutobidFragment autobidFragment = new AutobidFragment();
                return autobidFragment;
            default:
                return new BookingFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;

                    case R.id.nav_transaction_history:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_TRANSACTION_HISTORY;
                        break;
                    case R.id.nav_deposit:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_DEPOSIT;
                        break;
                    case R.id.nav_withdraw:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_WITHDRAW;
                        break;
                    case R.id.nav_performa:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_PERFORMA;
                        break;
                    case R.id.nav_rating:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_RATING;
                        break;
                    case R.id.nav_booking:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_BOOKING;
                        break;
                    case R.id.nav_inbox:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_INBOX;
                        break;
                    case R.id.nav_history:
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_HISTORY;
                        break;
                    case R.id.nav_message:
                        navItemIndex = 8;
                        CURRENT_TAG = TAG_MESSAGE;
                        break;
                    case R.id.nav_aggrement:
                        navItemIndex = 9;
                        CURRENT_TAG = TAG_AGREEMENT;
                        break;
                    case R.id.nav_version:
                        navItemIndex = 10;
                        CURRENT_TAG = TAG_VERSION;
                        break;
                    case R.id.nav_autobid:
                        navItemIndex = 11;
                        CURRENT_TAG = TAG_AUTOBID;
                        break;
                    default:
                        navItemIndex = 5;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_BOOKING;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        if (navItemIndex == 1) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        if (navItemIndex == 2) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        if (navItemIndex == 4) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        if (navItemIndex == 5) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        if (navItemIndex == 6) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        if (navItemIndex == 7) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        if (navItemIndex == 8) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        if (navItemIndex == 9) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        if (navItemIndex == 10) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        if (navItemIndex == 11) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }


    @Override
    protected void onStart() {
        super.onStart();
            Intent intent = new Intent(this, LocationUpdateService.class);
            intent.putExtra("id_driver", "1480997953");
            startService(intent);

    }

    @Override
    protected void onStop() {
        stopService(new Intent(this, LocationUpdateService.class));
        super.onStop();

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }
}
