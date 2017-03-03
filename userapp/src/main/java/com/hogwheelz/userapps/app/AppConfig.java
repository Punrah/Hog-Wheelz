package com.hogwheelz.userapps.app;

public class AppConfig {
	public static final String CANCEL_ACCEPTED_ORDER ="http://31.220.53.232/cms/modul/order/action.php?aksi=cancel_accepted_order" ;

	public static final String CANCEL_ORDER="http://31.220.53.232/cms/modul/order/action.php?aksi=cancel_order";
	// Server user login url
	public static final String URL_LOGIN = "http://31.220.53.232/cms/modul/order/action.php?aksi=login";

	// Server user register url
	public static final String URL_REGISTER = "http://31.220.53.232/cms/modul/order/action.php?aksi=register";

	public static final String URL_ORDER_RIDE = "http://31.220.53.232/cms/modul/order/action.php?aksi=input_order_ride";

	public static final String URL_ORDER_SEND = "http://31.220.53.232/cms/modul/order/action.php?aksi=input_order_send";

	public static final String getPriceURL(String origins,String destinations)
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


	public static String getHistoryCompletedURL(String idCustomer) {
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=list_order_complete&id_customer="+idCustomer;
	}

	public static String getTopPicksURL() {
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=coba_nearme";
	}

	public static String getRecommendedDishesURL() {
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=coba_recomended";
	}

	public static String getExploreURL() {
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=coba_explore";
	}

	public static String getListRestaurantURL() {
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=list_restaurant";
	}

	public static String getSignatureDishesURL() {
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=coba_recomended";
	}

	public static String getMenuURL() {
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=coba_explore";
	}

	public static String getItemURL() {
		return "http://31.220.53.232/cms/modul/order/action.php?aksi=coba_item";
	}


	public static String getRestaurantDetail() {
		return  "http://31.220.53.232/cms/modul/order/action.php?aksi=restaurant";
	}
}
