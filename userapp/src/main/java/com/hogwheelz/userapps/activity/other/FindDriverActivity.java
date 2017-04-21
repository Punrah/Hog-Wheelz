package com.hogwheelz.userapps.activity.other;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FindDriverActivity extends RootActivity {
    private static final String TAG = FindDriverActivity.class.getSimpleName();

    private Button buttonCancel;

private TextView textViewIdOrder;
    private String idOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hog_book);

        buttonCancel = (Button) findViewById(R.id.button_cancel);
        textViewIdOrder= (TextView) findViewById(R.id.id_order);
        idOrder=getIntent().getStringExtra("id_order");
        textViewIdOrder.setText(idOrder);


        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder();
            }
        });


    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }

    private void cancelOrder() {
            // Tag used to cancel the request
            String tag_string_req = "cancel_order";


            StringRequest strReq = new StringRequest(Request.Method.POST,
                    AppConfig.CANCEL_ORDER, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Login Response: " + response.toString());

                    try {
                        JSONObject jObj = new JSONObject(response);
                        String status = jObj.getString("status");
                        String msg = jObj.getString("msg");

                        // Check for error node in json
                        if (status.contentEquals("1")) {
                            finish();
                            Toast.makeText(FindDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        else if (status.contentEquals("2"))
                        {
                            Toast.makeText(FindDriverActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            // Error in login. Get the error message

                            String errorMsg = jObj.getString("msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();

                        Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Login Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("id_order",idOrder);

                    return params;
                }

            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

}
