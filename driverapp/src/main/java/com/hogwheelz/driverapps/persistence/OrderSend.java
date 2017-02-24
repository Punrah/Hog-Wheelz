package com.hogwheelz.driverapps.persistence;

/**
 * Created by Startup on 1/31/17.
 */

public class OrderSend extends Order {

    public String senderName;
    public String senderPhone;
    public String receiverName;
    public String receiverPhone;
    public String description;


    public OrderSend()
    {
        senderName="";
        senderPhone="";
        receiverName="";
        receiverPhone="";
    }

}
