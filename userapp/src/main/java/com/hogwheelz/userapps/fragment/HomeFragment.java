package com.hogwheelz.userapps.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.BookingActivity;
import com.hogwheelz.userapps.activity.HogrideActivity;


public class HomeFragment extends Fragment {

    private Button buttonHogRide;
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

        buttonHogRide.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getActivity(), HogrideActivity.class);
                startActivity(i);
            }

        });
        return myInflater;




    }

}
