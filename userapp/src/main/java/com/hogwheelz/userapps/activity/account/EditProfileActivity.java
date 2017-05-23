package com.hogwheelz.userapps.activity.account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.activity.other.ChangePhoneVerifyActivity;
import com.hogwheelz.userapps.activity.other.VerifyActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.app.Formater;
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

public class EditProfileActivity extends RootActivity {

    EditText textName;
    EditText textEmail;
    EditText textPhone;
    ImageView confirm;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        textName = (EditText) findViewById(R.id.name);
        textEmail = (EditText) findViewById(R.id.email);
        textPhone = (EditText ) findViewById(R.id.phone);
        confirm = (ImageView) findViewById(R.id.confirm);


        textName.setText(UserGlobal.getUser(getApplicationContext()).name);
        textEmail.setText(UserGlobal.getUser(getApplicationContext()).username);
        textPhone.setText(UserGlobal.getUser(getApplicationContext()).phone);

        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textName.getText().toString().equals(""))
                {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.empty_name_pop_up), Toast.LENGTH_SHORT).show();
                }
                else if(textEmail.getText().toString().equals(""))
                {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.empty_email_pop_up), Toast.LENGTH_SHORT).show();
                }
                else if(textPhone.getText().toString().equals(""))
                {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.empty_phone_pop_up), Toast.LENGTH_SHORT).show();
                }
                else if(textName.getText().toString().equals(UserGlobal.getUser(getApplicationContext()).name)
                        &&textEmail.getText().toString().equals(UserGlobal.getUser(getApplicationContext()).username)
                        &&textPhone.getText().toString().equals(UserGlobal.getUser(getApplicationContext()).phone))
                {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.user_changed_nothing), Toast.LENGTH_SHORT).show();
                }
                else {
                    new editProfile(textName.getText().toString(),textEmail.getText().toString(),textPhone.getText().toString()).execute();

                }
            }
        });

    }

    private class editProfile extends MyAsyncTask {

        String name;
        String phone;
        String email;
        User user = new User();

        public editProfile(String name,String email,String phone)
        {
            this.email=email;
            this.phone=phone;
            this.name=name;
        }




        @Override
        public Context getContext () {
            return EditProfileActivity.this;
        }

        @Override
        protected Void doInBackground (Void...params){
            postData();
            return super.doInBackground(params);
        }

        @Override
        public void setSuccessPostExecute() {
            if(status.equals("1"))
            {
                User user = new User();
                user.idCustomer=UserGlobal.getUser(getApplicationContext()).idCustomer;
                user.name=textName.getText().toString();
                user.username=textEmail.getText().toString();
                user.phone=textPhone.getText().toString();
                UserGlobal.setUser(getApplicationContext(),user);
                setAlertSuccess(getString(R.string.profile_updated),DIALOG);
            }
            else if(status.equals("2"))
            {
                Intent intent = new Intent(
                        EditProfileActivity.this,
                        ChangePhoneVerifyActivity.class);
                intent.putExtra("id_customer",UserGlobal.getUser(getApplicationContext()).idCustomer);
                intent.putExtra("name", textName.getText().toString());
                intent.putExtra("email",textEmail.getText().toString());
                intent.putExtra("phone",textPhone.getText().toString());
                startActivity(intent);
            }

        }

        @Override
        public void setFailPostExecute() {

        }

        public void postData() {
            String url = AppConfig.URL_EDIT_PROFILE;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_customer", UserGlobal.getUser(getApplicationContext()).idCustomer));
                nameValuePairs.add(new BasicNameValuePair("name",textName.getText().toString() ));
                nameValuePairs.add(new BasicNameValuePair("email", textEmail.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("phone", textPhone.getText().toString()));



                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String jsonStr = EntityUtils.toString(entity, "UTF-8");

                if (jsonStr != null) {
                    try {
                        JSONObject obj = new JSONObject(jsonStr);
                        status = obj.getString("status");
                        if(status.equals("1"))
                        {
                            isSucces=true;
                            smsg = obj.getString("msg");
                        }
                        else if(status.equals("2"))
                        {
                            isSucces=true;
                            smsg = obj.getString("msg");
                        }



                    } catch (final JSONException e) {
                        badServerAlert();
                    }
                } else {
                    badServerAlert();
                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                badInternetAlert();
            }
        }

    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }
}
