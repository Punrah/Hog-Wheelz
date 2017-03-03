package com.hogwheelz.userapps.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.hogFood.FoodActivity;
import com.hogwheelz.userapps.activity.makeOrder.MakeOrderRideActivity;
import com.hogwheelz.userapps.activity.makeOrder.MakeOrderSendActivity;


public class HomeFragment extends Fragment {

    private Button buttonHogRide;
    private Button buttonHogSend;
    private Button buttonHogFood;
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
        buttonHogRide =(Button) myInflater.findViewById(R.id.button_hogride);
        buttonHogSend =(Button) myInflater.findViewById(R.id.button_hogsend);
        buttonHogFood =(Button) myInflater.findViewById(R.id.button_hogfood);

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
        return myInflater;




    }

}
