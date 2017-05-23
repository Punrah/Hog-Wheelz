/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 */
package com.hogwheelz.userapps.activity.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.main.LoginActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.helper.SessionManager;
import com.hogwheelz.userapps.helper.UserSQLiteHandler;
import com.hogwheelz.userapps.persistence.User;
import com.hogwheelz.userapps.persistence.UserGlobal;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChangePhoneVerifyActivity extends AppCompatActivity {
    private static final String TAG = ChangePhoneVerifyActivity.class.getSimpleName();
    private Button buttonVerify ;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputCode;
    private EditText inputPassword;
    private EditText inputConfirmPassword;
    private SessionManager session;
    private UserSQLiteHandler db;
    String idCustomer;
    String name;
    String email;
    String phone;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hog_verify);

        idCustomer=getIntent().getStringExtra("id_customer");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");

        inputCode = (EditText) findViewById(R.id.code);
        buttonVerify= (Button) findViewById(R.id.btnVerify);



        // Register Button Click event


        buttonVerify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String token = inputCode.getText().toString().trim();

                if (!token.isEmpty()) {
                        new verifyToken(token, idCustomer).execute();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your verification code!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });




    }



    private class verifyToken extends MyAsyncTask {

        String token;
        String idCustomer;

        public verifyToken(String token,String idCustomer) {
            this.token = token;
            this.idCustomer=idCustomer;
        }


            @Override
            public Context getContext () {
                return ChangePhoneVerifyActivity.this;
            }

            @Override
            protected Void doInBackground (Void...params){
                postData();
                return super.doInBackground(params);
            }

            @Override
            public void setSuccessPostExecute() {
                // Launch login activity


                    User user = new User();
                    user.idCustomer= UserGlobal.getUser(getApplicationContext()).idCustomer;
                    user.name=name;
                    user.username=email;
                    user.phone=phone;
                    UserGlobal.setUser(getApplicationContext(),user);
                    setSuccessAlert();
                    finish();

            }

        @Override
        public void setFailPostExecute() {

        }

        public void postData() {
            String url="";

                url = AppConfig.URL_VERIFY_PROFILE;


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("token", token));
                nameValuePairs.add(new BasicNameValuePair("id_customer", idCustomer));
                nameValuePairs.add(new BasicNameValuePair("token", token));
                nameValuePairs.add(new BasicNameValuePair("name",name ));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("phone", phone));


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        status = obj.getString("status");

                        if (status.contentEquals("1")) {
                            isSucces = true;
                            smsg = obj.getString("msg");
                        } else {
                            msg = obj.getString("msg");
                            msgTitle="";
                            alertType=TOAST;
                        }

                    } catch (final JSONException e) {
                        badServerAlert();
                    }
                } else {
                    badServerAlert();
                }


            } catch (IOException e) {
                badInternetAlert();
            }
        }

    }


}
