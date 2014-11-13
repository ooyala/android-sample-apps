package com.ooyala.demo.social;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.ooyala.demo.Constants;
import com.ooyala.demo.R;
import com.ooyala.demo.utils.TwitterUtils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterActivity extends Activity {


    private static final String TAG = TwitterActivity.class.getSimpleName();

    static final FrameLayout.LayoutParams FILL =
            new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.FILL_PARENT);


    private RequestToken requestToken;
    private static Twitter twitter;
    private static DialogListener mListener;

    private ProgressDialog mSpinner;
    private WebView mWebView;
    private LinearLayout mContent;


    public static void setListener(final DialogListener mListener) {
        TwitterActivity.mListener = mListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpinner = new ProgressDialog(this);
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage(getString(R.string.message_loading));

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        twitter = TwitterUtils.getTwitter(this);


        mContent = new LinearLayout(this);
        mContent.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));


        mWebView = new WebView(this);

        mWebView.addJavascriptInterface(new HtmlOutJavaScriptInterface(), "HTMLOUT");
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new TwitterWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setLayoutParams(FILL);
        mWebView.setVisibility(View.INVISIBLE);


        mContent.addView(mWebView);


        setContentView(mContent);

        //noinspection unchecked
        new TwitterRequestTokenTask().execute();
    }

    class TwitterRequestTokenTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            if (result == 0) {
                finish();
            }
        }

        @Override
        protected Integer doInBackground(final Void... voids) {
            try {
                requestToken = twitter.getOAuthRequestToken();
                mWebView.loadUrl(requestToken.getAuthorizationURL());
                return 1;
            } catch (TwitterException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return 0;
        }
    }

    public class TwitterWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Util.logd("Facebook-WebView", "Redirect URL: " + failingUrl);
            super.onReceivedError(view, errorCode, description, failingUrl);
            mListener.onError(
                    new DialogError(description, errorCode, failingUrl));
            TwitterActivity.this.finish();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Util.logd("Facebook-WebView", "Redirect URL: " + url);
            super.onPageStarted(view, url, favicon);
            if (url.endsWith("authorize")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.setVisibility(View.INVISIBLE);
                    }
                });
            }

            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            mWebView.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
            mSpinner.dismiss();
            mContent.setBackgroundColor(Color.TRANSPARENT);
            view.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('code')[0].innerHTML);");

        }
    }

    class HtmlOutJavaScriptInterface {

        @SuppressWarnings("unused")
        public void processHTML(final String html) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.setVisibility(View.INVISIBLE);
                }
            });

            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, html.trim());

                SharedPreferences sharedPreferences = getSharedPreferences(Constants.TWITTER_PREFERENCE, MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString(Constants.P_TOKEN, accessToken.getToken());
                edit.putString(Constants.P_SECRET, accessToken.getTokenSecret());
                edit.putString(Constants.P_SCREEN_NAME, accessToken.getScreenName());
                edit.putString(Constants.P_USER_ID, String.valueOf(accessToken.getUserId()));
                edit.commit();

                mListener.onComplete(null);
                TwitterActivity.this.finish();
            } catch (TwitterException e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }
    }

}
