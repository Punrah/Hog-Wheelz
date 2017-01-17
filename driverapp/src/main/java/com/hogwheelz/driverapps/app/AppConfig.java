package com.hogwheelz.driverapps.app;

public class AppConfig {
	// Server user login url
	public static String URL_LOGIN = "http://primagsm.com/hogwheelz/cms/modul/order/action.php?aksi=login";
	public static String UPDATE_LOC = "http://primagsm.com/hogwheelz/cms/modul/order/action.php?aksi=update_loc";

	// Server user register url
	public static String URL_REGISTER = "http://primagsm.com/hogwheelz/cms/modul/order/action.php?aksi=register";

	public static String getPriceURL(String origins,String destinations)
	{
		return "http://primagsm.com/hogwheelz/cms/modul/order/action.php?aksi=request_price&origins="+origins+"&destinations="+destinations;
	}
}
