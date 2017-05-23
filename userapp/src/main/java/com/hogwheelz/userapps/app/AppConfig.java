package com.hogwheelz.userapps.app;

public class AppConfig {
	private  static String  general = "http://31.220.53.232/cms/modul/";
	public static final String URL_IMAGE = "http://31.220.53.232/cms/image/restaurant_image/";
	public static final String URL_DRIVER_IMAGE = "http://31.220.53.232/cms/image/driver_image/";


	public static final String CANCEL_ACCEPTED_ORDER =general+"order/action.php?aksi=cancel_accepted_order" ;

	public static final String CANCEL_ORDER=general+"order/action.php?aksi=cancel_order";
	// Server user login url
	public static final String URL_LOGIN = general+"order/action.php?aksi=login";

	// Server user register url
	public static final String URL_REGISTER = general+"order/action.php?aksi=register";

	public static final String URL_ORDER_RIDE = general+"order/action.php?aksi=input_order_ride";
	public static final String URL_ORDER_FOOD = general+"order/action_food.php?aksi=input_order_food";

	public static final String URL_ORDER_SEND = general+"order/action.php?aksi=input_order_send";
    public static final String GET_REASON = general+"order/action.php?aksi=getcancelreason";

	public static final String UPLOAD_TRANSFER = general+"order/action_topup.php?aksi=insert_topup";
	public static final String URL_VERIFY =  general+"order/action.php?aksi=cek_token";


    public static final String UPLOAD_HELP =general+"order/action.php?aksi=insert_help" ;
	public static final String URL_CALL_CENTER =general+"order/action.php?aksi=callcenter" ;
	public static final String URL_EDIT_PROFILE = general+"order/action.php?aksi=edit_profile";
	public static final String URL_VERIFY_PROFILE = general+"order/action.php?aksi=cek_token_profile";
    public static final String URL_CHANGE_PASSWORD = general+"order/action.php?aksi=change_password";
	public static final String UPLOAD_RATING =general+"order/action.php?aksi=insert_rating" ;
    public static final String UPLOAD_NEED_HELP = general+"order/action.php?aksi=insert_help_order";
	public static final String UPLOAD_VOUCHER = general+"order/action.php?aksi=insert_voucher";

	public static final String getPriceURL(String origins,String destinations,String orderType,String vehicle)
	{
		return general+"order/action.php?aksi=request_price&origins="+origins+"&destinations="+destinations+"&order_type="+orderType+"&vehicle="+vehicle;
	}

	public static String getDriverLocationURL(String location)
	{
		return general+"order/action.php?aksi=send_loc_driver&loc="+location;
	}

	public static String getOrderedDriverLocationURL(String idDriver)
	{
		return general+"order/action.php?aksi=get_ordered_driver_loc&id_driver="+idDriver;
	}

	public static String getHistoryProgressURL(String idCustomer)
	{
		return general+"order/action.php?aksi=list_order_progress&id_customer="+idCustomer;
	}

	public static String getOrderDetail(String idOrder)
	{
		return general+"order/action.php?aksi=list_order_detail&id_order="+idOrder;
	}


	public static String getHistoryCompletedURL(String idCustomer) {
		return general+"order/action.php?aksi=list_order_complete&id_customer="+idCustomer;
	}

	public static String getTopPicksURL() {
		return general+"order/action.php?aksi=coba_nearme";
	}

	public static String getRecommendedDishesURL() {
		return general+"order/action.php?aksi=coba_recomended";
	}

	public static String getExploreURL() {
		return general+"order/action.php?aksi=explore";
	}

	public static String getListRestaurantURL() {
		return general+"order/action.php?aksi=list_restaurant_explore";
	}

	public static String getSignatureDishesURL() {
		return general+"order/action.php?aksi=coba_recomended";
	}

	public static String getMenuURL() {
		return general+"order/action.php?aksi=coba_explore";
	}

	public static String getItemURL() {
		return general+"order/action.php?aksi=coba_item";
	}


	public static String getRestaurantDetail(String idRestaurant) {
		return  general+"order/action.php?aksi=restaurant&&id_restaurant="+idRestaurant;
	}

	public static String getBalanceURL(String idCustomer) {
		return  general+"order/action.php?aksi=getsaldo&&id_customer="+idCustomer;
	}

	public static String getWebURL(String action) {
		return  general+"order/action_topup.php?aksi="+action;
	}


    public static String getTransactionURL() {
		return general+"order/action.php?aksi=list_hogpay";
    }
}
