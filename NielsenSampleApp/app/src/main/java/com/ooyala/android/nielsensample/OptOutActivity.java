package com.ooyala.android.nielsensample;

// taken from Nielsen sample app.

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import com.ooyala.android.util.DebugMode;
import java.util.Arrays;

public class OptOutActivity extends Activity {
  private static final String TAG = OptOutActivity.class.getSimpleName();

  private WebView webView;

  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView( R.layout.optout );

    final Button close = (Button) findViewById(R.id.btnOptOutClose);
    close.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v)
      {
        bailOut(null);
      }
    });

    final Bundle extras = getIntent().getExtras();

    String url = extras.getString(NielsenSampleAppActivity.OPT_OUT_URL_EXTRAS_KEY);
    webView = (WebView) findViewById(R.id.webView);
    webView.getSettings().setJavaScriptEnabled(true);

    // Handle webview scaling
    webView.setInitialScale( 1 );
    webView.getSettings().setBuiltInZoomControls( true );
    webView.getSettings().setSupportZoom( true );
    webView.getSettings().setLoadWithOverviewMode( true );
    webView.getSettings().setUseWideViewPort( true );
    webView.getSettings().setLayoutAlgorithm( WebSettings.LayoutAlgorithm.SINGLE_COLUMN );
    webView.setWebViewClient( new MonitorWebView() );
    webView.setWebChromeClient( new WebChromeClient() );

    Log.d( "WEB", "Launching: " + url );
    webView.loadUrl( url );
  }

  private void bailOut( String result )
  {
    Log.d( TAG, "bailOut: result = " + result + ", from " + Arrays.toString( Thread.currentThread().getStackTrace() ) );
    Intent i = new Intent();
    i.putExtra(NielsenSampleAppActivity.OPT_OUT_RESULT_KEY, result);
    setResult( RESULT_OK, i );
    finish();
  }

  public void onBackPressed()
  {
    bailOut(null);
  }

  @Override
  protected void onStop() {
    DebugMode.logD( TAG, "onStop" );
    super.onStop();
    NielsenSampleAppActivity.decrementRunningActivityCount();
  }

  @Override
  protected void onStart() {
    DebugMode.logD( TAG, "onStart" );
    super.onStart();
    NielsenSampleAppActivity.incrementRunningActivityCount();
  }

  private class MonitorWebView extends WebViewClient
  {
    private final String TAG = MonitorWebView.class.getSimpleName();
    private ProgressDialog progressDialog;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
      Log.d( TAG, "shouldOverrideUrlLoading: url = " + url );
      if (url.indexOf("nielsen") == 0) {
        bailOut(url);
        return false;
      } else {
        progressDialog = ProgressDialog.show( OptOutActivity.this, "OptOut", "Loading..." );
        return true;
      }
    }

    @Override
    public void onPageFinished(WebView view, final String url) {
      if( progressDialog != null ) {
        progressDialog.dismiss();
      }
    }
  }
}
