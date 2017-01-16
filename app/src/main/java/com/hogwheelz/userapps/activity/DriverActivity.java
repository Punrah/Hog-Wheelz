package com.hogwheelz.userapps.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.hogwheelz.userapps.R;

public class DriverActivity extends AppCompatActivity {
    String name,phone,plat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        name =getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        plat = getIntent().getStringExtra("plat");

        TextView textViewName=(TextView)findViewById(R.id.name);
        TextView textViewPhone=(TextView)findViewById(R.id.phone);
        TextView textViewPlat=(TextView)findViewById(R.id.plat);

        textViewName.setText(name);
        textViewPhone.setText(phone);
        textViewPlat.setText(plat);

        MainActivity.isBookingState=false;


    }
}
