package com.yogesh.appoinment.controllers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.yogesh.appoinment.R;

public class WebActivity extends AppCompatActivity {
    public static String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("https://docs.google.com/viewer?url==" + url);
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
    }
}