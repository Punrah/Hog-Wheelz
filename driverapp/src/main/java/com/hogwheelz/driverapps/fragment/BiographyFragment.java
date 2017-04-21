package com.hogwheelz.driverapps.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.app.AppConfig;
import com.hogwheelz.driverapps.app.Formater;
import com.hogwheelz.driverapps.helper.HttpHandler;
import com.hogwheelz.driverapps.persistence.DriverGlobal;

import org.json.JSONException;
import org.json.JSONObject;


public class BiographyFragment extends Fragment {

    TextView textViewName,textViewUsername,textViewUserPhone,textViewUserPlat;


    ImageView star1;
    ImageView star2;
    ImageView star3;
    ImageView star4;
    ImageView star5;

    int ratings;
    public BiographyFragment() {
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
        View myInflate=inflater.inflate(R.layout.fragment_biography, container, false);

        textViewName=(TextView) myInflate.findViewById(R.id.name);
        textViewUsername=(TextView) myInflate.findViewById(R.id.username);
        textViewUserPhone=(TextView) myInflate.findViewById(R.id.phone);
        textViewUserPlat=(TextView) myInflate.findViewById(R.id.plat);

        star1 = (ImageView) myInflate.findViewById(R.id.star1);
        star2 = (ImageView) myInflate.findViewById(R.id.star2);
        star3 = (ImageView) myInflate.findViewById(R.id.star3);
        star4 = (ImageView) myInflate.findViewById(R.id.star4);
        star5 = (ImageView) myInflate.findViewById(R.id.star5);




        //Displaying the user details on the screen
        textViewName.setText(DriverGlobal.getDriver(getActivity().getApplicationContext()).name);
        textViewUsername.setText(DriverGlobal.getDriver(getActivity().getApplicationContext()).username);
        textViewUserPhone.setText(DriverGlobal.getDriver(getActivity().getApplicationContext()).phone);
        textViewUserPlat.setText(DriverGlobal.getDriver(getActivity().getApplicationContext()).plat);
        new calculateRatings().execute();
        return myInflate;
    }

    public  void  setStar(int count)
    {
        if(count==0)
        {
            star1.setImageResource(R.drawable.star_gray);
            star2.setImageResource(R.drawable.star_gray);
            star3.setImageResource(R.drawable.star_gray);
            star4.setImageResource(R.drawable.star_gray);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==1)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.star_gray);
            star3.setImageResource(R.drawable.star_gray);
            star4.setImageResource(R.drawable.star_gray);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==2)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.yellow_star);
            star3.setImageResource(R.drawable.star_gray);
            star4.setImageResource(R.drawable.star_gray);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==3)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.yellow_star);
            star3.setImageResource(R.drawable.yellow_star);
            star4.setImageResource(R.drawable.star_gray);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==4)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.yellow_star);
            star3.setImageResource(R.drawable.yellow_star);
            star4.setImageResource(R.drawable.yellow_star);
            star5.setImageResource(R.drawable.star_gray);
        }
        if(count==5)
        {
            star1.setImageResource(R.drawable.yellow_star);
            star2.setImageResource(R.drawable.yellow_star);
            star3.setImageResource(R.drawable.yellow_star);
            star4.setImageResource(R.drawable.yellow_star);
            star5.setImageResource(R.drawable.yellow_star);
        }
    }

    private class calculateRatings extends AsyncTask<Void, Void, Void> {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            setStar(0);

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            String url = AppConfig.getRatingsURL(DriverGlobal.getDriver(getActivity()).idDriver);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    ratings = jsonObj.getInt("rating");

                } catch (final JSONException e) {
//                    Toast.makeText(getActivity(), "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                //Toast.makeText(getActivity(), "Couldn't get json from server.", Toast.LENGTH_SHORT).show();

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            setStar(ratings);


            // Dismiss the progress dialog

        }
    }

}
