package com.hogwheelz.userapps.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.hogpay.WebActivity;
import com.hogwheelz.userapps.activity.main.LoginActivity;
import com.hogwheelz.userapps.activity.account.ChangePasswordActivity;
import com.hogwheelz.userapps.activity.account.EditProfileActivity;
import com.hogwheelz.userapps.activity.hogpay.PayActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.helper.UserSQLiteHandler;
import com.hogwheelz.userapps.helper.SessionManager;
import com.hogwheelz.userapps.persistence.UserGlobal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MyAccountFragment extends Fragment {

    private TextView textViewName, textViewUsername,textViewUserPhone;

    private UserSQLiteHandler db;
    private SessionManager session;
    TextView textViewBalance;
    String balance;

    TextView buttonLogout;
    ImageView imageViewBalance;

    TextView edit;
    LinearLayout changePassword;
    LinearLayout termOfService;
    LinearLayout privacyPolicy;




    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflate=inflater.inflate(R.layout.fragment_my_account, container, false);

        db = new UserSQLiteHandler(getActivity().getApplicationContext());
        // session manager
        session = new SessionManager(getActivity());

        textViewName=(TextView) myInflate.findViewById(R.id.name);
        textViewUsername=(TextView) myInflate.findViewById(R.id.username);
        textViewUserPhone=(TextView) myInflate.findViewById(R.id.phone);
        imageViewBalance = (ImageView) myInflate.findViewById(R.id.hogpay);

        textViewBalance=(TextView) myInflate.findViewById(R.id.balance);
        buttonLogout=(TextView) myInflate.findViewById(R.id.logout);

        edit = (TextView) myInflate.findViewById(R.id.edit);
        changePassword = (LinearLayout) myInflate.findViewById(R.id.change_password);
        termOfService = (LinearLayout) myInflate.findViewById(R.id.term_of_service);
        privacyPolicy = (LinearLayout) myInflate.findViewById(R.id.privacy_policy);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });




        imageViewBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PayActivity.class);
                startActivity(i);

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                changePassword.startAnimation(animation1);
                Intent i=new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(i);
            }
        });
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                privacyPolicy.startAnimation(animation1);
                Intent i = new Intent(getActivity(),WebActivity.class);
                i.putExtra("title", "Privacy Policy");
                i.putExtra("action","privacy_policy");
                startActivity(i);
            }
        });
        termOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                termOfService.startAnimation(animation1);
                Intent i = new Intent(getActivity(),WebActivity.class);
                i.putExtra("title", "Term of Service");
                i.putExtra("action","term_of_service");
                startActivity(i);
            }
        });
        new calculateBalance().execute();
        return myInflate;
    }

    @Override
    public void onResume() {
        //Displaying the user details on the screen
        textViewName.setText(UserGlobal.getUser(getActivity().getApplicationContext()).name);
        textViewUsername.setText(UserGlobal.getUser(getActivity().getApplicationContext()).username);
        textViewUserPhone.setText(UserGlobal.getUser(getActivity().getApplicationContext()).phone);
        new calculateBalance().execute();
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean visible){
        super.setUserVisibleHint(visible);
        if (visible && isResumed()){
            new calculateBalance().execute();
        }
    }

    private class calculateBalance extends MyAsyncTask{
        @Override


        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            textViewBalance.setText("Please wait");

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            String url = AppConfig.getBalanceURL(UserGlobal.getUser(getActivity()).idCustomer);

            String jsonStr = null;
            try {
                jsonStr = sh.makeServiceCall(url);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        balance = jsonObj.getString("saldo");
                        isSucces=true;

                    } catch (final JSONException e) {
                        badServerAlert();
                    }
                } else {
                    badServerAlert();
               }
            } catch (IOException e) {
                badInternetAlert();
            }

            return null;
        }


            @Override
        public Context getContext() {
            return getActivity();
        }

        @Override
        public void setSuccessPostExecute() {
            textViewBalance.setText(Formater.getPrice(balance));
        }

        @Override
        public void setFailPostExecute() {
            textViewBalance.setText(Formater.getPrice("0"));

        }

        @Override
        public void setPreloading() {

        }

        @Override
        public void setPostLoading() {


        }
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }





}
