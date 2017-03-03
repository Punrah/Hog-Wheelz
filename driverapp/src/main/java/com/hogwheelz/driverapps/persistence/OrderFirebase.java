package com.hogwheelz.driverapps.persistence;

/**
 * Created by Startup on 2/21/17.
 */

public class OrderFirebase {


    private String destinationAddress;
    private String destinationLat;
    private String destinationLng;
    private String distance;
    private String orderId;
    private String orderType;
    private String originAddress;
    private String originLat;
    private String originLng;
    private String price;
    private String note;

    public  OrderFirebase( String destinationAddress,
                   String destinationLat,
                   String destinationLng,
                   String distance,
                   String orderId,
                   String orderType,
                   String originAddress,
                   String originLat,
                   String originLng,
                   String price,
                   String note)
    {
        this.setDestinationAddress(destinationAddress);
        this.setDestinationLat(destinationLat);
        this.setDestinationLng(destinationLng);
        this.setDistance(distance);
        this.setOrderId(orderId);
        this.setOrderType(orderType);
        this.setOriginAddress(originAddress);
        this.setOriginLat(originLat);
        this.setOriginLng(originLng);
        this.setPrice(price);
        this.setNote(note);

    }

    public OrderFirebase()
    {}


    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(String destinationLat) {
        this.destinationLat = destinationLat;
    }

    public String getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(String destinationLng) {
        this.destinationLng = destinationLng;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getOriginLat() {
        return originLat;
    }

    public void setOriginLat(String originLat) {
        this.originLat = originLat;
    }

    public String getOriginLng() {
        return originLng;
    }

    public void setOriginLng(String originLng) {
        this.originLng = originLng;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}