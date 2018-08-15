package com.perezjquim.tiny;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.perezjquim.PermissionChecker;
import com.perezjquim.SharedPreferencesHelper;

import static com.perezjquim.UIHelper.askBinary;
import static com.perezjquim.UIHelper.closeProgressDialog;
import static com.perezjquim.UIHelper.hide;
import static com.perezjquim.UIHelper.openProgressDialog;
import static com.perezjquim.UIHelper.show;

public class MainActivity extends AppCompatActivity
{
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        PermissionChecker.init(this);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        super.setTheme(android.R.style.Theme_DeviceDefault_NoActionBar);

        WebView wWeb = findViewById(R.id.web);
        EditText eUrl = findViewById(R.id.url);
        EditText eSearch = findViewById(R.id.search);

        Activity self = this;

        CookieManager.getInstance().setAcceptCookie(true);

        WebSettings settings = wWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setSavePassword(false);

        wWeb.setWebChromeClient(new WebChromeClient()
        {
            private View view;
            private WebChromeClient.CustomViewCallback callback;

            public void onHideCustomView()
            {
                ((FrameLayout)getWindow().getDecorView()).removeView(this.view);
                show(findViewById(R.id.main));
                this.view = null;
                this.callback.onCustomViewHidden();
            }

            public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback)
            {
                if (this.view != null)
                {
                    onHideCustomView();
                }
                else
                {
                    this.view = view;
                    this.callback = callback;
                    ((FrameLayout)getWindow().getDecorView()).addView(
                            this.view,
                            new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT));
                    hide(findViewById(R.id.main));
                }
            }
        });

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
            eUrl.setText(prev_url);
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
            askBinary(this,"Confirmation","Are you sure you want to exit?", super::onBackPressed);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        WebView wWeb = findViewById(R.id.web);

        SharedPreferencesHelper prefs = new SharedPreferencesHelper(this);
        prefs.setString("config","prev_url",wWeb.getUrl());
    }

    public void onRefresh(View v)
    {
        WebView wWeb = findViewById(R.id.web);
        wWeb.reload();
    }
}
