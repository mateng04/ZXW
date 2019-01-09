package com.mobile.zxw.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mobile.zxw.myapplication.R;

public class WebViewActivity extends AppCompatActivity{

    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webview = (WebView) findViewById(R.id.webView);

        Intent intent = getIntent();
        String tempData = intent.getStringExtra("from");
        if("fwtk".equals(tempData)){
            webview.loadUrl("http://zhengxinw.com/m/reg/index2.asp");
        }else{
            webview.loadUrl("http://zhengxinw.com/m/reg/agreement2.asp");
        }


        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

}


