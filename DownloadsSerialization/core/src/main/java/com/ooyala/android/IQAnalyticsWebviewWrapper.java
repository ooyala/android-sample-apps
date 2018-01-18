package com.ooyala.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ooyala.android.analytics.IqConfiguration;
import com.ooyala.android.util.DebugMode;
import com.ooyala.android.util.TemporaryInternalStorageFile;
import com.ooyala.android.util.TemporaryInternalStorageFileManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Integration of Ooyala client-side player with Ooyala server-side analytics collection.
 */
@SuppressLint("SetJavaScriptEnabled")
class IQAnalyticsWebviewWrapper {

  private static final String TAG = IQAnalyticsWebviewWrapper.class.getSimpleName();
  private static final String TMP_PREFIX = "pb2823";
  private static final String TMP_EXT = ".html";
  private String embedModuleParamsHtml = null;

  private static final String JS_ANALYTICS_USER_AGENT = "Ooyala Android SDK %s [%s]";
  private static final String JS_ANALYTICS_PLAYER_NAME = "ooyala android sdk";

  private boolean _disabled;
  private boolean _ready;
  private boolean _failed;
  private boolean _shouldReportPlayRequest;
  private WebView _jsAnalytics;
  private List<String> _queue = new ArrayList<String>();
  private String _defaultUserAgent = "";
  private String _userAgent = "";
  private TemporaryInternalStorageFileManager tmpBootHtmlFileManager;
  private final IqConfiguration iqConfig;

  //TODO: Sanitization here would be nice
  private String generateEmbedHTML(Context context, UserInfo userInfo, String pcode, PlayerDomain domain, IqConfiguration iqConfig) {     //, IQConfiguration config In method
    embedModuleParamsHtml = "<html><head><script src=\"" + iqConfig.getAnalyticsJSURL() + "\"></script>\n" +
            "<script>function _init() {\n" +
            "reporter = new Ooyala.Analytics.Reporter('_PCODE_');\n" +
            getSetDeviceInfoString(iqConfig.getDeviceInfo()) +
            getSetUserInfoString(userInfo) +
            getSetPlayerInfoString(iqConfig.getPlayerID(), OoyalaPlayer.getVersion()) +
            getSetDocumentURLString(iqConfig.getDomain(), domain.toString()) +
            getSetIQBackendURL(iqConfig.getBackendEndpointURL()) +
            "};</script></head><body onLoad=\"_init();\"></body></html>";
    String clientId = ClientId.getId(context);
    String encryptedId = Utils.encryptString(clientId);
    return embedModuleParamsHtml.replaceAll("_PCODE_", pcode).replaceAll("_GUID_", encryptedId);
  }

  private static String getSetDeviceInfoString(DeviceInfo deviceInfo){
    if (deviceInfo != null) {
      return "reporter.setDeviceInfo('_GUID_'," +
          "{os: '" + (deviceInfo.getOs() != null ? deviceInfo.getOs() : "") + "'," +
          "browser: '" + (deviceInfo.getBrowser() != null ? deviceInfo.getBrowser() : "") + "', " +
          "deviceType: '" + (deviceInfo.getDeviceType() != null ? deviceInfo.getDeviceType() : "") + "', " +
          "osVersion:'"+ (deviceInfo.getOsVersion() != null ? deviceInfo.getOsVersion() : "") + "', " +
          "deviceBrand:'" + (deviceInfo.getDeviceBrand() != null ? deviceInfo.getDeviceBrand() : "") + "', " +
          "model:'" + (deviceInfo.getModel() != null ? deviceInfo.getModel() : "") + "'});";
    } else {
      return "reporter.setDeviceInfo('_GUID_');";
    }

  }
  private static String getSetPlayerInfoString(String playerId, String version){
    return "reporter.setPlayerInfo('" + playerId + "'," +
            "'" + JS_ANALYTICS_PLAYER_NAME + "'," +
            "'" + version + "');\n";
  }
  private static String getSetUserInfoString(UserInfo userInfo){
        if (userInfo.getAccountId() != null) {
          return "reporter.setUserInfo('','" + userInfo.getAccountId() +
              "','','','');\n";
        } else {
          return "";
        }
  }

  private static String getSetDocumentURLString(String configDomain, String ooyalaPlayerDomain){
    String usedDomain = ooyalaPlayerDomain;
    if (configDomain != null) {
      DebugMode.logI(TAG, "Domain set in IqConfiguration, overriding domain from OoyalaPlayer");
      usedDomain = configDomain;
    } else {
      DebugMode.logI(TAG, "No Domain set in IqConfiguration, using domain from OoyalaPlayer Initialization");
    }
    return "reporter.setDocumentURL('" + usedDomain + "');";
  }

  private static String getSetIQBackendURL(String iqBackendUrl){
    return "reporter.setIQBackendURL('" + iqBackendUrl + "');";
  }

  private static void setAllowUniversalAccessFromFileURLs( final WebSettings settings ) {
    for( Method m : settings.getClass().getMethods() ) {
      if( m.getName().equals( "setAllowUniversalAccessFromFileURLs" ) ) {
        try {
          m.invoke( settings, true );
        }
        catch (Exception e) {
          DebugMode.logD( TAG, "failed: " + e.getStackTrace() );
        }
        break;
      }
    }
  }

  /**
   * Initialize an Analytics using the specified api
   * @param context the context the initialize the internal WebView with
   * @param userInfo the userInfo of the authorized user
   * @param pcode the PCode of the customer
   * @param domain the Domain specified by the customer
   */
  IQAnalyticsWebviewWrapper(Context context, UserInfo userInfo, String pcode, PlayerDomain domain,IqConfiguration iqConfig) {
    this(context, userInfo, pcode, domain, domain.toString(),iqConfig);
  }

  /**
   * Initialize an Analytics using the specified api and HTML (used internally)
   * @param context the context the initialize the internal WebView with
   * @param userInfo  the userInfo of the authorized user
   * @param pcode the PCode of the customer
   * @param domain  the Domain specified by the customer
   * @param embedDomain the domain of the dummy page hosting reporter.js
   */
  @SuppressLint("SetJavaScriptEnabled")
  IQAnalyticsWebviewWrapper(Context context, UserInfo userInfo, String pcode, PlayerDomain domain, String embedDomain, IqConfiguration iqConfig) {
    tmpBootHtmlFileManager = new TemporaryInternalStorageFileManager();
    _disabled = false;
    _jsAnalytics = new WebView(context);
    this.iqConfig = iqConfig;
    iqConfig.getDeviceInfo().setDeviceType(getDeviceType(context));
    _defaultUserAgent = String.format(JS_ANALYTICS_USER_AGENT, OoyalaPlayer.getVersion(),
        _jsAnalytics.getSettings().getUserAgentString());
    _userAgent = _defaultUserAgent;
    _jsAnalytics.getSettings().setUserAgentString(_defaultUserAgent);
    _jsAnalytics.getSettings().setJavaScriptEnabled(true);
    setAllowUniversalAccessFromFileURLs( _jsAnalytics.getSettings() );

    String embedHTML = generateEmbedHTML(context, userInfo, pcode, domain, this.iqConfig);
    _jsAnalytics.setWebViewClient( new WebViewClient() {
      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        DebugMode.logD(TAG, "onPageStarted");
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        if (!_ready && !_failed) {
          _ready = true;
          DebugMode.logD(this.getClass().getName(), "Initialized Analytics.");
          performQueuedActions();
        }
      }

      @Override
      public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        DebugMode.logE(TAG, "on Received Error" + request.toString() + " With error:" + error.toString());
        _failed = true;
      }

      @Override
      public void onReceivedHttpError(
        WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        DebugMode.logE(TAG, "on Received Error" + request.toString() + " With error:" + errorResponse.toString());
        _failed = true;
      }

      @Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                     SslError error) {
        DebugMode.logE(TAG, "on Received Error" + error.toString());
        _failed = true;
        handler.cancel();
      }

    });

    _jsAnalytics.setWebChromeClient( new WebChromeClient() {
      @Override
      public boolean onConsoleMessage(ConsoleMessage cm) {
        DebugMode.logD(TAG,  cm.message() + cm.lineNumber() + cm.sourceId() );
        return true;
      }
    });

    bootHtml( context, embedDomain, embedHTML );
    DebugMode.logD(TAG, "Initialized Analytics with user agent: "
        + _jsAnalytics.getSettings().getUserAgentString());

    //TODO: This should be done on reportPlayerLoad from the AnalyticsPluginManager, instead of hard-wired here
    reportPlayerLoad();
  }

  private void bootHtml( final Context context, final String embedDomain, final String embedHTML ) {
    try {
      final TemporaryInternalStorageFile tmpBootHtmlFile = tmpBootHtmlFileManager.next( context, TMP_PREFIX, TMP_EXT );
      tmpBootHtmlFile.write( embedHTML );
      loadTmpBootHtmlFile( tmpBootHtmlFile );
    }
    catch (IOException e) {
      DebugMode.logE( TAG, "failed: " + e.getStackTrace() );
    }
    catch (IllegalArgumentException e) {
      DebugMode.logE( TAG, "failed: " + e.getStackTrace() );
    }
  }

  private void loadTmpBootHtmlFile( final TemporaryInternalStorageFile tmpBootHtmlFile ) {
    final String htmlUrlStr = "file://" + tmpBootHtmlFile.getAbsolutePath();
    DebugMode.logD( TAG, "trying to load: " + htmlUrlStr );

    // this is purely for our own debugging purposes...
    try {
      final Scanner scanner = new Scanner( tmpBootHtmlFile.getFile() );
      try { while( true ) { DebugMode.logD( TAG, scanner.nextLine() ); } }
      catch( NoSuchElementException e ) {}
      finally { scanner.close(); }
    }
    catch( FileNotFoundException e ) { }
    // ...this is purely for our own debugging purposes.

    _jsAnalytics.loadUrl( htmlUrlStr );
  }

  /**
   * Helper function to report a player load
   */
  private void report(String action) {
    DebugMode.logE("string: ", action);
    if (_failed  || _disabled) { return; }
    if (!_ready) {
      queue(action);
    } else {
      new ReportAsyncTask().execute(action);
    }
  }

  private class ReportAsyncTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
      String action = params[0];
      return action;
    }

    protected void onPostExecute(String action) {
      _jsAnalytics.loadUrl(action);
    }
  }

  /**
   * Report a new video being initialized with the given embed code and duration
   * @param embedCode the embed code of the new video
   * @param duration the duration (in milliseconds) of the new video
   */
  void initializeVideo(String embedCode, double duration) {
    int durationInt = (int)(duration);
    String action = "javascript:reporter.initializeMedia('" + embedCode + "', Ooyala.Analytics.MediaContentType.OOYALA_CONTENT);reporter.setMediaDuration(" + durationInt + ");";
    _shouldReportPlayRequest = true;
    report(action);
  }

  /**
   * Report a player load
   */
  private void reportPlayerLoad() {
    report("javascript:reporter.reportPlayerLoad();");
  }

  /**
   * Report a playhead update to the specified time
   * @param time the new playhead time (in milliseconds)
   */
  void reportPlayheadUpdate(double time) {
    int timeInt = (int)(time);
    String action = "javascript:reporter.reportPlayHeadUpdate(" + timeInt + ");";
    report(action);
  }

  /**
   * Report that the player has started playing
   */
  void reportPlayStarted() {
    report("javascript:reporter.reportPlaybackStarted();");
  }

  /**
   * Report that the player was asked to replay
   */
  void reportReplay() {
    report("javascript:reporter.reportReplay();");
  }

  void reportPlayRequested() {
    if (!_shouldReportPlayRequest) {
      return;
    }
    String action = "javascript:reporter.reportPlayRequested(false);";
    _shouldReportPlayRequest = false;
     report(action);
  }

  void reportPlayResumed() {
    String action = "javascript:reporter.reportResume();";
    report(action);
  }

  void reportPlayPaused() {
    String action = "javascript:reporter.reportPause();";
    report(action);
  }

  void reportPlayCompleted() {
    String action = "javascript:reporter.reportComplete();";
    report(action);
  }

  void setTags(List<String> tags) {
//    String action = "javascript:reporter.setTags([\"" + Utils.join(tags, "\",\"") + "\"]);";
//    report(action);
  }

  private void queue(String action) {
    _queue.add(action);
  }

  private void performQueuedActions() {
    for (String action : _queue) {
      DebugMode.logI(TAG, "reporting:" + action);
      _jsAnalytics.loadUrl(action);
    }
    _queue.clear();
  }

  public void setUserAgent(String userAgent) {
    if (userAgent != null) {
      _userAgent = userAgent;
    }
    else {
      _userAgent = _defaultUserAgent;
    }
    _jsAnalytics.getSettings().setUserAgentString(_userAgent);
  }

  /**
   * temporary enable/disable
   * @param disable true to disable, false to enable.
   */
  public void disable(boolean disable) {
    _disabled = disable;
  }

  /**
   * @return true if disabled, false otherwise
   */
  public boolean isDisabled() {
    return _disabled;
  }

  /**
   * sets device type.
   * ref:https://developer.android.com/guide/practices/screens_support.html
   * @param context
     */
  private String getDeviceType(Context context) {
    String deviceType;
    int screenLayout = context.getResources().getConfiguration().screenLayout;
    screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

    switch (screenLayout) {
      case Configuration.SCREENLAYOUT_SIZE_SMALL:
      case Configuration.SCREENLAYOUT_SIZE_NORMAL:
      case Configuration.SCREENLAYOUT_SIZE_LARGE:
        deviceType = "mobile";
        break;
      case Configuration.SCREENLAYOUT_SIZE_XLARGE:
        deviceType = "tablet";
        break;
      default:
        deviceType = "unknown";
        break;
    }
    return deviceType;
  }

  /**
   * reports seek
   * @param seekInfo has information of seek start and seek end time in milliseconds.
   */
  public void reportSeek(SeekInfo seekInfo) {
    Number seekStart = seekInfo.getSeekStart();
    Number seekEnd = seekInfo.getSeekEnd();
    String action = "javascript:reporter.reportSeek("+seekStart+","+seekEnd+");";
    report(action);

  }
}
