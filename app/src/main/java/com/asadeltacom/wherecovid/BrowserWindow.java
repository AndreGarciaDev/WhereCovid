package com.asadeltacom.wherecovid;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class BrowserWindow extends AppCompatActivity {

    WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_window);

        webView = (WebView) findViewById(R.id.webview);

        Button button;

        button = (Button) findViewById (R.id.sus);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPage("https://coronavirus.saude.gov.br/");
            }
        });

        button = (Button) findViewById (R.id.map1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPage("https://www.google.com/covid19-map/");
            }
        });

        button = (Button) findViewById (R.id.map2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPage("https://bing.com/covid");
            }
        });

        button = (Button) findViewById (R.id.map3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPage("https://www.arcgis.com/apps/opsdashboard/index.html#/85320e2ea5424dfaaa75ae62e5c06e61");
            }
        });

        button = (Button) findViewById (R.id.map4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoPage("https://www.worldometers.info/coronavirus/");
            }
        });
    }

    private void gotoPage(String url){

        WebSettings webSettings;
        webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new Callback());  //HERE IS THE MAIN CHANGE
        webView.loadUrl(url);

    }

    private class Callback extends WebViewClient {  //HERE IS THE MAIN CHANGE.

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return (false);
        }

    }

}
