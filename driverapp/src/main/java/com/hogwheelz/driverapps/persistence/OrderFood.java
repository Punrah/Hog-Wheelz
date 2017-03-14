package com.hogwheelz.driverapps.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Startup on 1/31/17.
 */

public class OrderFood extends Order {

    public List<Item> item;
    public int totalPrice;


    public OrderFood()
    {
        item = new ArrayList<Item>();
        totalPrice=0;
    }





}