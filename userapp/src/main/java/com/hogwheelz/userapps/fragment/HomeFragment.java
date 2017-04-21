package com.hogwheelz.userapps.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.hogFood.FoodActivity;
import com.hogwheelz.userapps.activity.hogpay.PayActivity;
import com.hogwheelz.userapps.activity.makeOrder.MakeOrderRideActivity;
import com.hogwheelz.userapps.activity.makeOrder.MakeOrderSendActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.User;
import com.hogwheelz.userapps.persistence.UserGlobal;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;


public class HomeFragment extends Fragment {

    private LinearLayout buttonHogRide;
    private LinearLayout buttonHogSend;
    private LinearLayout buttonHogFood;
    private LinearLayout buttonHogPay;

    private  ImageView imageViewBalance;

    TextView textViewBalance;
    float lastX;

   String balance;
    public HomeFragment() {
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


    View myInflater =inflater.inflate(R.layout.fragment_home, container, false);
        buttonHogRide =(LinearLayout) myInflater.findViewById(R.id.button_hogride);
        buttonHogSend =(LinearLayout) myInflater.findViewById(R.id.button_hogsend);
        buttonHogFood =(LinearLayout) myInflater.findViewById(R.id.button_hogfood);
        buttonHogPay =(LinearLayout) myInflater.findViewById(R.id.button_hogpay);
        textViewBalance = (TextView) myInflater.findViewById(R.id.balance);
        imageViewBalance = (ImageView) myInflater.findViewById(R.id.hogpay);




        buttonHogRide.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MakeOrderRideActivity.class);
                startActivity(i);
            }

        });
        buttonHogSend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MakeOrderSendActivity.class);
                startActivity(i);
            }

        });
        buttonHogFood.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FoodActivity.class);
                startActivity(i);
            }

        });
        buttonHogPay.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PayActivity.class);
                startActivity(i);
            }

        });

        imageViewBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PayActivity.class);
                startActivity(i);

            }
        });
        new calculateBalance().execute();
        return myInflater;




    }

    @Override
    public void onResume() {
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

    private class calculateBalance extends AsyncTask<Void, Void, Void> {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            textViewBalance.setText("Please wait");

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            String url = AppConfig.getBalanceURL(UserGlobal.getUser(getActivity()).idCustomer);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    balance = jsonObj.getString("saldo");

                } catch (final JSONException e) {
                   //Toast.makeText(getActivity(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
               // Toast.makeText(getActivity(), "Couldn't get json from server.", Toast.LENGTH_SHORT).show();

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            UserGlobal.balance=Double.parseDouble(balance);
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));
            String moneyString = formatter.format(Double.parseDouble(balance));
            textViewBalance.setText(moneyString);


            // Dismiss the progress dialog

        }
    }

}
