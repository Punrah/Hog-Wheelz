package com.hogwheelz.userapps.activity.hogpay;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
import com.hogwheelz.userapps.helper.HttpHandler;
import com.hogwheelz.userapps.persistence.UserGlobal;

import org.json.JSONException;
import org.json.JSONObject;

public class PayActivity extends RootActivity {
    TextView textViewBalance;
    LinearLayout linearLayoutTopUp;
    LinearLayout linearLayoutVoucher;
    LinearLayout linearLayoutTransaction;
    String balance;
    ImageView back;

    LinearLayout buttonTopUp;
    LinearLayout buttonVoucher;
    LinearLayout buttonTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        textViewBalance = (TextView) findViewById(R.id.balance);
        linearLayoutTopUp = (LinearLayout) findViewById(R.id.top_up);
        linearLayoutVoucher = (LinearLayout) findViewById(R.id.voucher);
        linearLayoutTransaction = (LinearLayout) findViewById(R.id.transaction);
        back = (ImageView) findViewById(R.id.back);


        buttonTopUp = (LinearLayout) findViewById(R.id.top_up);
        buttonVoucher = (LinearLayout) findViewById(R.id.voucher);
        buttonTransaction = (LinearLayout) findViewById(R.id.transaction);


        buttonTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PayActivity.this,TransactionActivity.class);
                startActivity(i);
            }
        });

        buttonVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PayActivity.this,VoucherActivity.class);
                startActivity(i);
            }
        });

        buttonTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PayActivity.this);

                builder.setTitle("Transfer Conformation");
                builder.setMessage("Are you have been transfer your money?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(PayActivity.this,TopUpActivity.class);
                        startActivity(i);dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent i = new Intent(PayActivity.this,WebActivity.class);
                        i.putExtra("title", "Transfer Guide");
                        i.putExtra("action","transfer_guide");
                        startActivity(i);
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
new calculateBalance().execute();
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                PayActivity.super.onBackPressed();
            }
        });

    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        new calculateBalance().execute();
    }

    private class calculateBalance extends AsyncTask<Void, Void, Void> {
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            textViewBalance.setText("Please wait");

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();

            String url = AppConfig.getBalanceURL(UserGlobal.getUser(PayActivity.this).idCustomer);

            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    balance = jsonObj.getString("saldo");

                } catch (final JSONException e) {
                    Toast.makeText(PayActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PayActivity.this, "Couldn't get json from server.", Toast.LENGTH_SHORT).show();

            }
            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            UserGlobal.balance=Double.parseDouble(balance);
            textViewBalance.setText(Formater.getPrice(balance));


            // Dismiss the progress dialog

        }
    }
}
