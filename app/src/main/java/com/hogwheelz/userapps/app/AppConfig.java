package com.hogwheelz.userapps.app;

public class AppConfig {
	// Server user login url
	public static String URL_LOGIN = "http://primagsm.com/hogwheelz/cms/modul/order/action.php?aksi=login";

	// Server user register url
	public static String URL_REGISTER = "http://primagsm.com/hogwheelz/cms/modul/order/action.php?aksi=register";

	public static String URL_BOOKING = "http://primagsm.com/hogwheelz/cms/modul/order/action.php?aksi=input_order_ride";

	public static String getPriceURL(String origins,String destinations)
	{
		return "http://primagsm.com/hogwheelz/cms/modul/order/action.php?aksi=request_price&origins="+origins+"&destinations="+destinations;
	}
}
