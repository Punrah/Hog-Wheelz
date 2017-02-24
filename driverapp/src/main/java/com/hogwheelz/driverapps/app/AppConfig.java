package com.hogwheelz.driverapps.app;

public class AppConfig {
	// Server user login url
	public static String URL_LOGIN = "http://31.220.53.232/cms/modul/order/action_driver.php?aksi=login";
	public static String UPDATE_LOC = "http://31.220.53.232/cms/modul/order/action.php?aksi=update_loc";

	// Server user register url
	public static String URL_REGISTER = "http://31.220.53.232/cms/modul/order/action.php?aksi=register";

	public static String ACCEPT_ORDER = "http://31.220.53.232/cms/modul/order/action.php?aksi=accept_order";

	public static String OTW_ORDER = "http://31.220.53.232/cms/modul/order/action_driver.php?aksi=status_order_otw";
	public static String START_ORDER = "http://31.220.53.232/cms/modul/order/action_driver.php?aksi=status_order_start";
	public static String COMPLETE_ORDER = "http://31.220.53.232/cms/modul/order/action_driver.php?aksi=status_order_complete";
	public static String getPriceURL(String origins,String destinations)
	{
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=request_price&origins="+origins+"&destinations="+destinations;
	}

	public static String getActiveBookingURL(String idDriver) {
		return "http://31.220.53.232/cms/modul/order/action_driver.php?aksi=list_order_progress&id_driver="+idDriver;
	}

	public static String getBookingHistoryURL(String idDriver) {
		return "http://31.220.53.232/cms/modul/order/action_driver.php?aksi=list_order_complete&id_driver="+idDriver;
	}

	public static String getOrderDetail(String idOrder) {
		return "http://31.220.53.232/cms/modul/order/action_driver.php?aksi=list_order_detail&id_order="+idOrder;
	}


}
