package com.hogwheelz.driverapps.app;

public class AppConfig {
	// Server user login url
	public static String URL_LOGIN = "http://31.220.53.232/cms/modul/order/action.php?aksi=login";
	public static String UPDATE_LOC = "http://31.220.53.232/cms/modul/order/action.php?aksi=update_loc";

	// Server user register url
	public static String URL_REGISTER = "http://31.220.53.232/cms/modul/order/action.php?aksi=register";

	public static String ACCEPT_ORDER = "http://31.220.53.232/cms/modul/order/action.php?aksi=accept_order";
	public static String getPriceURL(String origins,String destinations)
	{
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=request_price&origins="+origins+"&destinations="+destinations;
	}
}
