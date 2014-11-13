package com.ooyala.demo.social;

import android.app.Activity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;

public class LikeWebClient extends WebViewClient {
    public static final String LOGIN_PHP = "login.php";
    private final Facebook facebook;
    private final Activity activity;
    private final BaseAdapter arrayAdapter;

    public LikeWebClient(Activity activity, final Facebook facebook, final BaseAdapter arrayAdapter) {

        this.facebook = facebook;
        this.activity = activity;
        this.arrayAdapter = arrayAdapter;
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
        if (url.contains(LOGIN_PHP)) {
//            if (!facebook.isSessionValid()) {
            facebook.authorize(activity, new String[]{}, new LoginDialogListener(arrayAdapter));
//            }
            return true;

        }
        return super.shouldOverrideUrlLoading(view, url);
    }

}

