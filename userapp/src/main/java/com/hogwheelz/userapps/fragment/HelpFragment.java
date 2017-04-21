package com.hogwheelz.userapps.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.other.HelpActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.helper.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class HelpFragment extends Fragment {

    LinearLayout helpRide;
    LinearLayout helpSend;
    LinearLayout helpFood;
    LinearLayout helpOther;
    LinearLayout helpCall;

    String callCenter;
    public HelpFragment() {
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
        View myInflater= inflater.inflate(R.layout.fragment_help, container, false);
        helpRide = (LinearLayout) myInflater.findViewById(R.id.help_ride);
        helpSend = (LinearLayout) myInflater.findViewById(R.id.help_send);
        helpFood = (LinearLayout) myInflater.findViewById(R.id.help_food);
        helpOther = (LinearLayout) myInflater.findViewById(R.id.help_others);
        helpCall = (LinearLayout) myInflater.findViewById(R.id.help_call);

        helpRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                helpRide.startAnimation(animation1);
                Intent i = new Intent(getActivity(), HelpActivity.class);
                i.putExtra("code","ride");
                startActivity(i);
            }
        });
        helpSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                helpSend.startAnimation(animation1);
                Intent i = new Intent(getActivity(), HelpActivity.class);
                i.putExtra("code","send");
                startActivity(i);
            }
        });
        helpFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                helpFood.startAnimation(animation1);
                Intent i = new Intent(getActivity(), HelpActivity.class);
                i.putExtra("code","food");
                startActivity(i);
            }
        });
        helpOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Start an alpha animation for clicked item
                Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                animation1.setDuration(800);
                helpOther.startAnimation(animation1);
                Intent i = new Intent(getActivity(), HelpActivity.class);
                i.putExtra("code","others");
                startActivity(i);
            }
        });
        new callCenter().execute();



        return myInflater;
    }

    private class callCenter extends AsyncTask<Void, Void, Void> {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog


        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            String url = AppConfig.URL_CALL_CENTER;

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    callCenter = jsonObj.getString("nohp");

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

            helpCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
// Start an alpha animation for clicked item
                    Animation animation1 = new AlphaAnimation(0.3f, 5.0f);
                    animation1.setDuration(800);
                    helpCall.startAnimation(animation1);
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());

                    builder.setTitle("Call Support");
                    builder.setMessage("Standart charges will apply for calls placed. Or you can write to us for FREE!");

                    builder.setPositiveButton("CALL US", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + callCenter));
                            startActivity(intent);

                        }
                    });

                    builder.setNegativeButton("WRITE TO US", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();

                }
            });


            // Dismiss the progress dialog

        }
    }

}
