package com.hogwheelz.driverapps.activity.viewOrder;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.activity.main.RootActivity;

public abstract class ViewOrderActivity extends RootActivity {
    private static final String TAG = ViewOrderActivity.class.getSimpleName();


    String idOrder;

    ImageView back;

    Marker pickUpMarker;
    Marker dropOffMarker;
    Marker driverMarker;

    TextView textViewCustomerName;
    TextView textViewDestination;
    TextView textViewDistance;
    TextView textViewOrderId;
    TextView textViewOrigin;
    TextView textViewPrice;
    TextView textViewNoteOrigin;
    TextView textViewNoteDestination;
    TextView textViewOrderType;
    TextView textViewPaymentType;
    TextView buttonMaps;

    Button buttonGo;
    Button buttonCancel;

    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;

    TextView textViewCall;
    TextView textViewText;
     LinearLayout linearLayoutCaller;



    LinearLayout linearLayoutDetail;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);


         textViewCustomerName=(TextView)findViewById(R.id.customer_name);
         textViewDestination=(TextView)findViewById(R.id.destination);
         textViewDistance=(TextView)findViewById(R.id.distance);
         textViewOrderId=(TextView)findViewById(R.id.order_id);
         textViewOrigin=(TextView)findViewById(R.id.origin);
         textViewPrice=(TextView)findViewById(R.id.price);
         textViewNoteOrigin=(TextView)findViewById(R.id.note_origin);
         textViewNoteDestination=(TextView)findViewById(R.id.note_destination);
        textViewOrderType=(TextView)findViewById(R.id.order_type);
        textViewPaymentType=(TextView)findViewById(R.id.payment_type);
        textView1 = (TextView) findViewById(R.id.stepper1);
        textView2 = (TextView) findViewById(R.id.stepper2);
        textView3 = (TextView) findViewById(R.id.stepper3);
        textView4 = (TextView) findViewById(R.id.stepper4);

        buttonMaps =(TextView) findViewById(R.id.button_maps);
        buttonGo = (Button) findViewById(R.id.button_go);
         buttonCancel = (Button) findViewById(R.id.button_cancel);
         linearLayoutDetail = (LinearLayout) findViewById(R.id.detail);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });

        textViewCall=(TextView) findViewById(R.id.call);
        textViewText=(TextView) findViewById(R.id.text);
        linearLayoutCaller = (LinearLayout) findViewById(R.id.caller);



        idOrder= getIntent().getStringExtra("id_order");
        initializeOrder();
        getOrderDetail();
    }

    public abstract void initializeOrder();




    public abstract void getOrderDetail();


    public abstract void cancelAcceptedOrder();
    public abstract void otwOrder();
    public abstract void startOrder();
    public abstract void completeOrder();





    public abstract void setAllTextView();

    public abstract void adjustCamera();


}
