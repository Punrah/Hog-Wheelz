package com.hogwheelz.userapps.webservices;

import android.os.AsyncTask;

import com.hogwheelz.userapps.app.AppConfig;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import com.hogwheelz.userapps.helper.HttpHandler;

/**
 * Created by Startup on 12/21/16.
 */

public class WebServices {
    private  String price;
    private  LatLng origins,destinations;

    public String getPrice(LatLng originsPar, LatLng destinationsPar)
    {
        origins=originsPar;
        destinations=destinationsPar;
        new calPrice().execute();
        return price;
    }



    private class calPrice extends AsyncTask<Void, Void, Void> {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog


        }
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String originsLat = String.valueOf(origins.latitude);
            String originsLng = String.valueOf(origins.longitude);
            String destinationsLat = String.valueOf(destinations.latitude);
            String destinationsLng = String.valueOf(destinations.longitude);
            String url = AppConfig.getPriceURL(originsLat + "" + originsLng, destinationsLat + "," + destinationsLng);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    price = jsonObj.getString("price");

                } catch (final JSONException e) {
                    price = "Json parsing error: " + e.getMessage();

                }
            } else {
                price = "Couldn't get json from server.";
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog

        }
    }


}
