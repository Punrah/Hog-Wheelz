package com.hogwheelz.driverapps.persistence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.hogwheelz.driverapps.activity.FindOrderDetailActivity;

import java.util.List;

/**
 * Created by Startup on 2/20/17.
 */

public class OrderQueue extends Activity {

    private static List<Order> orders;


    public void viewOrder(Context context)
    {
        if(orders.size()!=0) {
            Intent i = new Intent(context, FindOrderDetailActivity.class);
            startActivity(i);
        }
    }

    public void addOrder(Order order)
    {
        orders.add(order);
    }


    public void popOrders()
    {
        orders.get(orders.size());

    }


}
