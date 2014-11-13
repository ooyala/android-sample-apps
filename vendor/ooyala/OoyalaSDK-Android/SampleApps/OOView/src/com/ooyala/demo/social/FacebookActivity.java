/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ooyala.demo.social;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.ooyala.demo.R;

public class FacebookActivity extends Activity {

    static final int FB_BLUE = 0xFF6D84B4;
    static final float[] DIMENSIONS_DIFF_LANDSCAPE = {20, 60};
    static final float[] DIMENSIONS_DIFF_PORTRAIT = {40, 60};
    static final FrameLayout.LayoutParams FILL =
            new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.FILL_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;
    static final String DISPLAY_STRING = "touch";
    static final String FB_ICON = "icon.png";

    private static String mUrl;
    private static DialogListener mListener;
    private ProgressDialog mSpinner;
    private ImageView mCrossImage;
    private WebView mWebView;
    private LinearLayout mContent;

    public static void setUrl(final String mUrl) {
        FacebookActivity.mUrl = mUrl;
    }

    public static void setListener(final DialogListener mListener) {
        FacebookActivity.mListener = mListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpinner = new ProgressDialog(this);
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage(getString(R.string.message_loading));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContent = new LinearLayout(this);
        mContent.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        /* Create the 'x' image, but don't add to the mContent layout yet
         * at this point, we only need to know its drawable width and height
         * to place the webview
         */
//        createCrossImage();

        /* Now we know 'x' drawable width and height,
         * layout the webivew and add it the mContent layout
         */

        mWebView = new WebView(this);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new FacebookActivity.FbWebViewClient());
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.2; nl-nl; Desire_A8181 Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://m.facebook.com/login.php");
        mWebView.setLayoutParams(FILL);
        mWebView.setVisibility(View.INVISIBLE);
        mContent.addView(mWebView);
        setContentView(mContent);
        /* Finally add the 'x' image to the mContent layout and
         * add mContent to the Dialog view
         */
//        mContent.addView(mCrossImage, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//        addContentView(mContent, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    private void createCrossImage() {
        mCrossImage = new ImageView(this);
        // Dismiss the dialog when user click on the 'x'
        mCrossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancel();
                finish();
            }
        });
        Drawable crossDrawable = getResources().getDrawable(R.drawable.close);
        mCrossImage.setImageDrawable(crossDrawable);
        /* 'x' should not be visible while webview is loading
         * make it visible only after webview has fully loaded
        */
        mCrossImage.setVisibility(View.INVISIBLE);
    }

    private void setUpWebView(int margin) {
//        LinearLayout webViewContainer = new LinearLayout(this);
        mWebView = new WebView(this);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new FacebookActivity.FbWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setSaveFormData(true);

        mWebView.loadUrl(mUrl);
        mWebView.setLayoutParams(FILL);
        mWebView.setVisibility(View.INVISIBLE);
//        webViewContainer.setLayoutParams(FILL);
//        webViewContainer.setPadding(margin, margin, margin, margin);
//        webViewContainer.addView(mWebView);
        mContent.addView(mWebView);
    }


    public class FbWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Util.logd("Facebook-WebView", "Redirect URL: " + url);//http://m.facebook.com/home.php?refsrc=http%3A%2F%2Fm.facebook.com%2Flogin.php&refid=9&_rdr
            if (url.contains("/home.php")) {
//                Bundle values = Util.parseUrl(url);
//
//                String error = values.getString("error");
//                if (error == null) {
//                    error = values.getString("error_type");
//                }

//                if (error == null) {
                mListener.onComplete(null);
//                } else if (error.equals("access_denied") ||
//                        error.equals("OAuthAccessDeniedException")) {
//                    mListener.onCancel();
//                } else {
//                    mListener.onFacebookError(new FacebookError(error));
//                }

                finish();
                return true;
            } else if (url.startsWith(Facebook.CANCEL_URI)) {
                mListener.onCancel();
                finish();
                return true;
            } else if (url.contains(DISPLAY_STRING)) {
                return false;
            }
            // launch non-dialog URLs in a full browser
//            getContext().startActivity(
//                    new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mListener.onError(
                    new DialogError(description, errorCode, failingUrl));
            finish();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Util.logd("Facebook-WebView", "Webview loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mSpinner.dismiss();
            /* 
             * Once webview is fully loaded, set the mContent background to be transparent
             * and make visible the 'x' image. 
             */
            mContent.setBackgroundColor(Color.TRANSPARENT);
            mWebView.setVisibility(View.VISIBLE);
//            mCrossImage.setVisibility(View.VISIBLE);
        }
    }
}
