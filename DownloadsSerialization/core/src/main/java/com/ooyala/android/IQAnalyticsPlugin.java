package com.ooyala.android;

import com.ooyala.android.analytics.AnalyticsPluginBaseImpl;
import com.ooyala.android.analytics.IqConfiguration;
import com.ooyala.android.item.Video;
import com.ooyala.android.util.DebugMode;

class IQAnalyticsPlugin extends AnalyticsPluginBaseImpl {
  private static final String TAG = IQAnalyticsPlugin.class.getSimpleName();
  private OoyalaPlayer player;
  private final IqConfiguration iqConfig;

  IQAnalyticsWebviewWrapper webviewWrapper;

  private String lastAccountID;
  public IQAnalyticsPlugin(OoyalaPlayer player, IqConfiguration iqConfig) {
    this.player = player;
    this.iqConfig = iqConfig;
  }

  @Override
  public void onCurrentItemAboutToPlay(Video currentItem) {
    if (null == currentItem || null == currentItem.getEmbedCode() || currentItem.getDuration() == 0) {
      DebugMode.logW(TAG, "Not initializing the Analytics Engine because the currentItem doesn't have enough info");
      return;
    }

    DebugMode.assertCondition(player.authTokenManager != null,
            TAG, "AuthTokenManager was not created, No UserInfo available");

    String accountId = player.authTokenManager.getUserInfo().getAccountId();
    if (accountId == null) {
      accountId = "";
    }

    //TODO: We want to do this during Initialization, but can't due to that Webview Context!
    //  When OoyalaPlayer is given a context, we can then do this earlier.
    if (webviewWrapper == null || !accountId.equals(lastAccountID)) {
      webviewWrapper = new IQAnalyticsWebviewWrapper(player.getLayout().getContext(),
              player.authTokenManager.getUserInfo(),
              player.getPcode(),
              player.getDomain(),iqConfig);
    }

    // last account ID seen. Could be null
    lastAccountID = accountId;

    if (player.contextSwitcher.getCastManager() == null || !player.contextSwitcher.getCastManager().isConnectedToReceiverApp()) {
      webviewWrapper.initializeVideo(currentItem.getEmbedCode(), currentItem.getDuration());
    } else {
      DebugMode.logI(TAG,  "CastManager is connected to Receiver App, We're going to go " +
              "into cast mode. Don't fire initializeVideo");
    }
  }

  @Override
  public void reportPlayStarted() {
    if (webviewWrapper != null) {
      webviewWrapper.reportPlayStarted();
    }
  }

  @Override
  public void reportPlayPaused() {
    if (webviewWrapper != null) {
      webviewWrapper.reportPlayPaused();
    }
  }

  @Override
  public void reportPlayResumed() {
    if (webviewWrapper != null) {
      webviewWrapper.reportPlayResumed();
    }
  }

  @Override
  public void reportPlayerLoad() {
// This is done automatically in the WebviewWrapper
//    if (webviewWrapper != null) {
//      webviewWrapper.reportPlayerLoad();
//    }
  }

  @Override
  public void reportPlayheadUpdate(int playheadTime) {
    if (webviewWrapper != null) {
      webviewWrapper.reportPlayheadUpdate(playheadTime);
    }
  }

  @Override
  public void reportReplay() {
    if (webviewWrapper != null) {
      webviewWrapper.reportReplay();
    }
  }

  @Override
  public void reportPlayRequested() {
    if (webviewWrapper != null) {
      webviewWrapper.reportPlayRequested();
    }
  }

  @Override
  public void reportSeek(SeekInfo seekInfo) {
    if (webviewWrapper != null) {
      webviewWrapper.reportSeek(seekInfo);
    }
  }

  @Override
  public void reportPlayCompleted() {
    if (webviewWrapper != null) {
      webviewWrapper.reportPlayCompleted();
    }
  }
}
