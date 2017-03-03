package com.hogwheelz.driverapps.app;

public class AppConfig {
	public static final String CANCEL_ACCEPTED_ORDER ="http://31.220.53.232/cms/modul/order/action.php?aksi=cancel_accepted_order_driver" ;
	// Server user login url
	public static final String URL_LOGIN = "http://31.220.53.232/cms/modul/order/action_driver.php?aksi=login";
	public static final String UPDATE_LOC = "http://31.220.53.232/cms/modul/order/action.php?aksi=update_loc";

	// Server user register url
	public static final String URL_REGISTER = "http://31.220.53.232/cms/modul/order/action.php?aksi=register";

	public static final String ACCEPT_ORDER = "http://31.220.53.232/cms/modul/order/action.php?aksi=accept_order";

	public static final String OTW_ORDER = "http://31.220.53.232/cms/modul/order/action_driver.php?aksi=status_order_otw";
	public static final String START_ORDER = "http://31.220.53.232/cms/modul/order/action_driver.php?aksi=status_order_start";
	public static final String COMPLETE_ORDER = "http://31.220.53.232/cms/modul/order/action_driver.php?aksi=status_order_complete";

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
