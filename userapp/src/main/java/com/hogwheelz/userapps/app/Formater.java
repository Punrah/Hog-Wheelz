package com.hogwheelz.userapps.app;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;

import com.hogwheelz.userapps.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Startup on 3/24/17.
 */

public  class Formater extends AppCompatActivity{

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

        private static final Pattern EUROPEAN_DIALING_PLAN = Pattern.compile("^\\+|(00)|(0)");



        public static String phoneNumber(String number,String countryCode) {


            // Remove all weird characters such as /, -, ...
            number = number.replaceAll("[^+0-9]", "");

            Matcher match = EUROPEAN_DIALING_PLAN.matcher(number);
            if (!match.find()) {
                throw new IllegalArgumentException(number);
            } else if (match.group(1) != null) {     // Starts with "00"
                return match.replaceFirst("+");
            } else if (match.group(2) != null) {     // Starts with "0"
                return match.replaceFirst("+" + countryCode);
            } else {                                 // Starts with "+"
                return number;
            }
        }



}
