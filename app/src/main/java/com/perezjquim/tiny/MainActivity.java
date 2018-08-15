package com.perezjquim.tiny;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.perezjquim.PermissionChecker;
import com.perezjquim.SharedPreferencesHelper;
import com.perezjquim.UIHelper;

import static com.perezjquim.UIHelper.closeProgressDialog;
import static com.perezjquim.UIHelper.openProgressDialog;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        PermissionChecker.init(this);
        setContentView(R.layout.activity_main);
        super.setTheme(android.R.style.Theme_Black_NoTitleBar);

        WebView wWeb = findViewById(R.id.web);
        EditText eUrl = findViewById(R.id.url);
        EditText eSearch = findViewById(R.id.search);

        Context self = this;

        CookieManager.getInstance().setAcceptCookie(true);
        wWeb.getSettings().setJavaScriptEnabled(true);
        wWeb.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                eUrl.setText(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                openProgressDialog(self,"Loading..");
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);
                closeProgressDialog();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
            {
                super.onReceivedError(view, request, error);
                closeProgressDialog();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse)
            {
                super.onReceivedHttpError(view, request, errorResponse);
                closeProgressDialog();
            }
        });


        eUrl.setOnEditorActionListener((text,id,event) ->
        {
            String url = text.getText()+"";
            if(!url.contains("http://"))
            {
                url = "http://" + url;
            }
            wWeb.loadUrl(url);
            return true;
        });

        eSearch.setOnEditorActionListener((text,id,event) ->
        {
            wWeb.loadUrl("http://www.google.com/search?q="+text.getText());
            return true;
        });

        SharedPreferencesHelper prefs = new SharedPreferencesHelper(this);

        String prev_url = prefs.getString("config","prev_url");
        if(prev_url != null)
        {
            wWeb.loadUrl(prev_url);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case PermissionChecker.REQUEST_CODE:
                PermissionChecker.restart();
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        WebView wWeb = findViewById(R.id.web);

        if(wWeb.canGoBack())
        {
            wWeb.goBack();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        WebView wWeb = findViewById(R.id.web);

        SharedPreferencesHelper prefs = new SharedPreferencesHelper(this);
        prefs.setString("config","prev_url",wWeb.getUrl());
    }
}
