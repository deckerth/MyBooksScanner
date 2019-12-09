package com.deckerth.thomas.mybooksscanner.ui.views;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.deckerth.thomas.mybooksscanner.BasicApp;
import com.deckerth.thomas.mybooksscanner.R;

public class HelpBrowserActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_browser);

        String url = BasicApp.getContext().getResources().getString(R.string.help_url);

        WebView webview = findViewById(R.id.help_web_view);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(url);
    }
}
