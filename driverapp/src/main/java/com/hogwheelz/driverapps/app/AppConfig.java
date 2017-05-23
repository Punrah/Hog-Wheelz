package com.hogwheelz.driverapps.app;

public class AppConfig {

	private  static String  general = "http://31.220.53.232/cms/modul/";
	public static final String URL_DRIVER_IMAGE = "http://31.220.53.232/cms/image/driver_image/";

	public static final String CANCEL_ACCEPTED_ORDER =general+"order/action.php?aksi=cancel_accepted_order_driver" ;
	// Server user login url
	public static final String URL_LOGIN = general+"order/action_driver.php?aksi=login";
	public static final String UPDATE_LOC = general+"order/action.php?aksi=update_loc";


	public static final String ACCEPT_ORDER = general+"order/action.php?aksi=accept_order";

	public static final String SKIP_ORDER = general+"order/action_driver.php?aksi=skip_order";

	public static final String OTW_ORDER = general+"order/action_driver.php?aksi=status_order_otw";
	public static final String START_ORDER = general+"order/action_driver.php?aksi=status_order_start";
	public static final String COMPLETE_ORDER = general+"order/action_driver.php?aksi=status_order_complete";


	public static String getActiveBookingURL(String idDriver) {
		return general+"order/action_driver.php?aksi=list_order_progress&id_driver="+idDriver;
	}

	public static String getBookingHistoryURL(String idDriver) {
		return general+"order/action_driver.php?aksi=list_order_complete&id_driver="+idDriver;
	}

	public static String getOrderDetail(String idOrder) {
		return general+"order/action_driver.php?aksi=list_order_detail&id_order="+idOrder;
	}

	public static String getBalanceURL(String idDriver) {
		return general+"order/action_driver.php?aksi=getsaldo&id_driver="+idDriver;
	}


	public static String setStatusOrderOn(String idDriver) {
		return general+"order/action_driver.php?aksi=set_status_order_on&id_driver="+idDriver;
	}

	public static String setStatusOrderOff(String idDriver) {
		return general+"order/action_driver.php?aksi=set_status_order_off&id_driver="+idDriver;
	}

	public static String getStatusOrder(String idDriver) {
		return general+"order/action_driver.php?aksi=get_status_order&id_driver="+idDriver;
	}

	public static String getTotalOrder(String idDriver) {
		return general+"order/action_driver.php?aksi=total_order&id_driver="+idDriver;
	}

	public static String getTransactionURL() {
		return general+"order/action_driver.php?aksi=list_hogpay";
	}

	public static String getRatingsURL(String idDriver) {
		return general+"order/action_driver.php?aksi=view_ratings&id_driver="+idDriver;
	}
}
