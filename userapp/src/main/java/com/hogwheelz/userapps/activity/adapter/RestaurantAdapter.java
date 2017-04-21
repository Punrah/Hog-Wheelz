package com.hogwheelz.userapps.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.persistence.Order;
import com.hogwheelz.userapps.persistence.Restaurant;

import java.util.List;


/**
 * Created by Ravi on 13/05/15.
 */
public class RestaurantAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Restaurant> restaurantList;

    public RestaurantAdapter(Activity activity, List<Restaurant> restaurantList) {
        this.activity = activity;
        this.restaurantList = restaurantList;
    }

    @Override
    public int getCount() {
        return restaurantList.size();
    }

    @Override
    public Object getItem(int location) {
        return restaurantList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_restaurant, null);

        TextView name = (TextView) convertView.findViewById(R.id.restaurant_name);
        TextView address = (TextView) convertView.findViewById(R.id.restaurant_address);
        TextView distance = (TextView) convertView.findViewById(R.id.restaurant_distance);
        TextView openHour = (TextView) convertView.findViewById(R.id.open_hour);

        name.setText(String.valueOf(restaurantList.get(position).name));
        address.setText(String.valueOf(restaurantList.get(position).address));
        distance.setText(Formater.getDistance(String.valueOf(restaurantList.get(position).distance)));
        openHour.setText(String.valueOf(restaurantList.get(position).openHour));

        return convertView;
    }

}