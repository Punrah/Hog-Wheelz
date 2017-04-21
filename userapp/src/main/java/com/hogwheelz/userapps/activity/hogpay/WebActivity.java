package com.hogwheelz.userapps.activity.hogpay;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hogwheelz.userapps.R;
import com.hogwheelz.userapps.activity.main.RootActivity;
import com.hogwheelz.userapps.app.AppConfig;
import com.hogwheelz.userapps.helper.HttpHandler;

public class WebActivity extends RootActivity {

    private WebView webView;
    TextView title;
    Toolbar toolbar;
    String webAction;
    String webActionTitle;
    String web;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (WebView) findViewById(R.id.webView1);
        title=(TextView) findViewById(R.id.title);
        webView.getSettings().setJavaScriptEnabled(true);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                WebActivity.super.onBackPressed();
            }
        });


        webAction=getIntent().getStringExtra("action");
        webActionTitle = getIntent().getStringExtra("title");
        title.setText(webActionTitle);


        new getWeb().execute();





    }

    @Override
    public void setPermissionLocation() {

    }

    @Override
    public void setPermissionCall() {

    }

    private class getWeb extends AsyncTask<Void, Void, Void> {
        @Override


        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String url = AppConfig.getWebURL(webAction);


            web = sh.makeServiceCall(url);

            return null;
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            webView.loadData(web, "text/html", "UTF-8");
        }
    }
}
