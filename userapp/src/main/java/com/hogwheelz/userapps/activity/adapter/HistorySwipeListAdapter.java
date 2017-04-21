package com.hogwheelz.userapps.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.persistence.Order;

import java.util.List;


/**
 * Created by Ravi on 13/05/15.
 */
public class HistorySwipeListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Order> orderList;

    public HistorySwipeListAdapter(Activity activity, List<Order> orderList) {
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

        LinearLayout statusColor = (LinearLayout) convertView.findViewById(R.id.status_color);
        TextView status = (TextView) convertView.findViewById(R.id.status);
        TextView orderDate = (TextView) convertView.findViewById(R.id.order_date);
        TextView dropOffAddress = (TextView) convertView.findViewById(R.id.dropoff_address);
        ImageView orderType = (ImageView) convertView.findViewById(R.id.order_type);

       if(orderList.get(position).status.contentEquals("Complete"))
       {
           status.setText("COMPLETED");
           statusColor.setBackgroundResource(R.drawable.border_round_green);
       }
       else if(orderList.get(position).status.contentEquals("Cancel"))
       {
           status.setText("CANCELLED");
           statusColor.setBackgroundResource(R.drawable.border_round_red);
       }
       else
       {
           status.setText("");
           statusColor.setBackgroundColor(Color.TRANSPARENT);
       }

        orderDate.setText(String.valueOf(orderList.get(position).getDateTime()));
        dropOffAddress.setText(String.valueOf(orderList.get(position).dropoffAddress));

        if(orderList.get(position).orderType==1)
        {
            orderType.setImageResource(R.drawable.ride_logo);
        }
        else if(orderList.get(position).orderType==2)
        {
            orderType.setImageResource(R.drawable.send_logo);
        }
        else if(orderList.get(position).orderType==3)
        {
            orderType.setImageResource(R.drawable.food_logo);
        }
        else
        {
        }

        return convertView;
    }

}