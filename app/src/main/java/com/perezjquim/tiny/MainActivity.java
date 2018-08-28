package com.perezjquim.tiny;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.perezjquim.PermissionChecker;
import com.perezjquim.SharedPreferencesHelper;
import com.perezjquim.tiny.db.DatabaseManager;

import static com.perezjquim.UIHelper.askBinary;
import static com.perezjquim.UIHelper.askString;
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
        DatabaseManager.initDatabase();
        super.setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        WebView wWeb = findViewById(R.id.web);
        EditText eUrl = findViewById(R.id.url);

        Activity self = this;

        CookieManager.getInstance().setAcceptCookie(true);

        WebSettings settings = wWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setSavePassword(false);
        settings.setGeolocationEnabled(false);
        settings.setSaveFormData(true);

        wWeb.setWebChromeClient(new WebChromeClient()
        {
            private View view;
            private WebChromeClient.CustomViewCallback callback;

            public void onHideCustomView()
            {
                ((FrameLayout)getWindow().getDecorView()).removeView(this.view);
                show(findViewById(R.id.main));
                this.view = null;
                if(this.callback != null) this.callback.onCustomViewHidden();
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
                loadUrl(url);
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
        });


        eUrl.setOnEditorActionListener((text,id,event) ->
        {
            String url = text.getText()+"";
            loadUrl(url);
            return true;
        });

        SharedPreferencesHelper prefs = new SharedPreferencesHelper(this);

        String prev_url = prefs.getString("config","prev_url");
        if(prev_url != null)
        {
            loadUrl(prev_url);
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
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);

        String url = intent.getStringExtra("bookmark_url");

        if(url != null)
        {
            loadUrl(url);
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
    public void onPause()
    {
        super.onPause();
        registerLastPage();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        registerLastPage();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        registerLastPage();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            startActivity(new Intent(this,BookmarksActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void registerLastPage()
    {
        WebView wWeb = findViewById(R.id.web);

        SharedPreferencesHelper prefs = new SharedPreferencesHelper(this);
        prefs.setString("config","prev_url",wWeb.getUrl());
    }

    public void onRefresh(View v)
    {
        WebView wWeb = findViewById(R.id.web);
        wWeb.reload();
    }

    public void onSearch(View v)
    {
        askString(this,"Search", "Type in your search below:",(s) ->
        {
            loadUrl("http://www.google.com/search?q="+s);
        });
    }

    private void loadUrl(String url)
    {
        if(!url.startsWith("http://") && !url.startsWith("https://"))
        {
            url = "http://" + url;
        }

        WebView wWeb = findViewById(R.id.web);
        wWeb.loadUrl(url);

        EditText eUrl = findViewById(R.id.url);
        eUrl.setText(url);
    }
}
