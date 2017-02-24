package com.hogwheelz.driverapps.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.persistence.DriverGlobal;


public class MyAccountFragment extends Fragment {

    TextView textViewName,textViewUsername,textViewUserPhone,textViewUserPlat;
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

        textViewName=(TextView) myInflate.findViewById(R.id.name);
        textViewUsername=(TextView) myInflate.findViewById(R.id.username);
        textViewUserPhone=(TextView) myInflate.findViewById(R.id.phone);
        textViewUserPlat=(TextView) myInflate.findViewById(R.id.plat);

        //Displaying the user details on the screen
        textViewName.setText(DriverGlobal.getDriver(getActivity().getApplicationContext()).name);
        textViewUsername.setText(DriverGlobal.getDriver(getActivity().getApplicationContext()).username);
        textViewUserPhone.setText(DriverGlobal.getDriver(getActivity().getApplicationContext()).phone);
        textViewUserPlat.setText(DriverGlobal.getDriver(getActivity().getApplicationContext()).plat);
        return myInflate;
    }

}
