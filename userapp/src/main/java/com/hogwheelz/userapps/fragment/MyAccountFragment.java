package com.hogwheelz.userapps.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.helper.UserSQLiteHandler;
import com.hogwheelz.userapps.helper.SessionManager;
import com.hogwheelz.userapps.persistence.User;
import com.hogwheelz.userapps.persistence.UserGlobal;


public class MyAccountFragment extends Fragment {

    private TextView textViewName, textViewUsername,textViewUserPhone;

    private UserSQLiteHandler db;
    private SessionManager session;




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


        //Displaying the user details on the screen
        textViewName.setText(UserGlobal.getUser(getActivity().getApplicationContext()).name);
        textViewUsername.setText(UserGlobal.getUser(getActivity().getApplicationContext()).username);
        textViewUserPhone.setText(UserGlobal.getUser(getActivity().getApplicationContext()).phone);


        return myInflate;
    }





}
