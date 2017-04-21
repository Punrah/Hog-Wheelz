package com.hogwheelz.driverapps.app;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Startup on 3/24/17.
 */

public class Formater {

    public static String getPrice(String price)
    {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));
        return formatter.format(Double.parseDouble(price));
    }

    public static String getDistance(String distance) {
        String value="";
        if(Double.parseDouble(distance)<1)
        {
            double p=Double.parseDouble(distance);
            DecimalFormat df = new DecimalFormat("#.0"); // Set your desired format here.
            value= "0"+df.format(p)+"KM";
        }
        else {
            double p=Double.parseDouble(distance);
            DecimalFormat df = new DecimalFormat("#.0"); // Set your desired format here.
            value=df.format(p)+"KM";
        }
        return value;

    }
}
