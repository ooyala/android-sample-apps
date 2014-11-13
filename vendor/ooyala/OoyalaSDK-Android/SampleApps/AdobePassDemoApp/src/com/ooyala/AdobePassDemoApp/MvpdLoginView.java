package com.ooyala.AdobePassDemoApp;

import java.net.URLDecoder;

import com.adobe.adobepass.accessenabler.api.AccessEnabler;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MvpdLoginView extends WebView {
  private NavigatedbackToAppListener listener;
  
  public MvpdLoginView(Context context, NavigatedbackToAppListener listener) {
    super(context);
    this.listener = listener;
    getSettings().setJavaScriptEnabled(true);
    getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    setWebViewClient(webViewClient);
  }

  private final WebViewClient webViewClient = new WebViewClient() {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (url.equals(URLDecoder.decode(AccessEnabler.ADOBEPASS_REDIRECT_URL))) {
        listener.onNavigatedBackToApp();
        return true;
      }
      return false;
    }
  };
  
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    Context context = getContext();
    WindowManager wm = (WindowManager) context
        .getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();

    setMeasuredDimension(display.getWidth(), display.getHeight());
  }
}
