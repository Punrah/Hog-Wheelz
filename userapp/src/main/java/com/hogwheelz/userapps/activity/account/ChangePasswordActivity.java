package com.hogwheelz.userapps.activity.account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.asynctask.MyAsyncTask;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.app.AppConfig;
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

public class ChangePasswordActivity extends RootActivity {



    EditText oldPass;
    EditText newPass;
    EditText confirmPass;
    ImageView confirm;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPass = (EditText) findViewById(R.id.old_password);
        newPass = (EditText) findViewById(R.id.new_password);
        confirmPass = (EditText) findViewById(R.id.confirm_password);
        confirm = (ImageView) findViewById(R.id.confirm);

        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldPass.getText().toString().equals(""))
                {
                    Toast.makeText(ChangePasswordActivity.this, "Field old password is required", Toast.LENGTH_SHORT).show();
                }
                else if(newPass.getText().toString().equals(""))
                {
                    Toast.makeText(ChangePasswordActivity.this, "Field new password is required", Toast.LENGTH_SHORT).show();
                }
                else if(confirmPass.getText().toString().equals(""))
                {
                    Toast.makeText(ChangePasswordActivity.this, "Field confirm new password is required", Toast.LENGTH_SHORT).show();
                }
                if(!newPass.getText().toString().equals(confirmPass.getText().toString()))
                {
                    AlertDialog.Builder  alert = new AlertDialog.Builder(ChangePasswordActivity.this);
                    alert.setTitle("Unable To Process");
                    alert.setMessage("Your new password is not match");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
                else {
                    new changePassword(oldPass.getText().toString(),newPass.getText().toString()).execute();


                }
            }
        });
    }

    private class changePassword extends MyAsyncTask {

        String oldPass;
        String newPass;
        User user = new User();

        public changePassword(String oldPass,String newPass)
        {
            this.oldPass=oldPass;
            this.newPass=newPass;
        }




        @Override
        public Context getContext () {
            return ChangePasswordActivity.this;
        }

        @Override
        protected Void doInBackground (Void...params){
            postData();
            return super.doInBackground(params);
        }

        @Override
        public void setMyPostExecute () {
            if(status.equals("1"))
            {
                setAlert();
            }
        }

        public void postData() {
            String url = AppConfig.URL_CHANGE_PASSWORD;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                nameValuePairs.add(new BasicNameValuePair("id_customer", UserGlobal.getUser(getApplicationContext()).idCustomer));
                nameValuePairs.add(new BasicNameValuePair("old_password",oldPass));
                nameValuePairs.add(new BasicNameValuePair("new_password", newPass));

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
                        emsg=obj.getString("msg");



                    } catch (final JSONException e) {
                        emsg = e.getMessage();//Toast.makeText(FindOrderDetailActivity.this, "Json parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    //Toast.makeText(FindOrderDetailActivity.this, "Couldn't get json from server", Toast.LENGTH_SHORT).show();
                    emsg = "JSON NULL";
                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                emsg = e.getMessage();
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
