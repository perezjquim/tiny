package com.perezjquim.tiny;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.perezjquim.PermissionChecker;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        PermissionChecker.init(this);
        setContentView(R.layout.activity_main);
        super.setTheme(android.R.style.Theme_DeviceDefault_NoActionBar);

        WebView wWeb = findViewById(R.id.web);
        EditText eUrl = findViewById(R.id.url);

        wWeb.getSettings().setJavaScriptEnabled(true);
        wWeb.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                eUrl.setText(url);
                return false;
            }
        });

        eUrl.setOnEditorActionListener((text,id,event) ->
        {
            wWeb.loadUrl(text.getText()+"");
            return true;
        });
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
}
