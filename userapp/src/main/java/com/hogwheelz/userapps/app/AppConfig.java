package com.hogwheelz.userapps.app;

public class AppConfig {
	// Server user login url
	public static String URL_LOGIN = "http://31.220.53.232/cms/modul/order/action.php?aksi=login";

	// Server user register url
	public static String URL_REGISTER = "http://31.220.53.232/cms/modul/order/action.php?aksi=register";

	public static String URL_BOOKING = "http://31.220.53.232/cms/modul/order/action.php?aksi=input_order_ride";

	public static String getPriceURL(String origins,String destinations)
	{
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=request_price&origins="+origins+"&destinations="+destinations;
	}

	public static String getDriverLocationURL(String location)
	{
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=send_loc_driver&loc="+location;
	}

	public static String getOrderedDriverLocationURL(String idDriver)
	{
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=get_ordered_driver_loc&id_driver="+idDriver;
	}

	public static String getHistoryProgressURL(String idCustomer)
	{
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=list_order_progress&id_customer="+idCustomer;
	}

	public static String getOrderDetail(String idOrder)
	{
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=list_order_detail&id_order="+idOrder;
	}

}
