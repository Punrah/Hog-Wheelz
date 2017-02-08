package com.hogwheelz.userapps.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.persistence.Order;

import java.util.List;


/**
 * Created by Ravi on 13/05/15.
 */
public class HistoryProgressSwipeListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Order> orderList;

    public HistoryProgressSwipeListAdapter(Activity activity, List<Order> orderList) {
        this.activity = activity;
        this.orderList = orderList;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int location) {
        return orderList.get(location);
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
            convertView = inflater.inflate(R.layout.list_history, null);

        TextView status = (TextView) convertView.findViewById(R.id.status);
        TextView orderDate = (TextView) convertView.findViewById(R.id.order_date);
        TextView dropOffAddress = (TextView) convertView.findViewById(R.id.dropoff_address);
        TextView orderType = (TextView) convertView.findViewById(R.id.order_type);

        status.setText(String.valueOf(orderList.get(position).status));
        orderDate.setText(String.valueOf(orderList.get(position).orderDate));
        dropOffAddress.setText(String.valueOf(orderList.get(position).dropoffAddress));
        orderType.setText(String.valueOf(orderList.get(position).orderType));

        return convertView;
    }

}