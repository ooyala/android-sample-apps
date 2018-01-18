package com.ooyala.android.analytics;

import com.ooyala.android.item.Video;
import com.ooyala.android.util.DebugMode;

/**
 * A very simple Analytics Plugin that can be used for testing
 */
public class TestAnalyticsPlugin extends AnalyticsPluginBaseImpl {
  private static final String TAG = TestAnalyticsPlugin.class.getSimpleName();

  int playheadUpdateTicker = 0;

  @Override
  public void onCurrentItemAboutToPlay(Video currentItem) {
    DebugMode.logI(TAG, "onCurrentItemAboutToPlays");
  }

  @Override
  public void reportPlayStarted() {
    DebugMode.logI(TAG, "reportPlayStarted");
  }

  @Override
  public void reportPlayerLoad() {
    DebugMode.logI(TAG, "reportPlayerLoad");
  }

  @Override
  public void reportPlayheadUpdate(int playheadTime) {
    playheadUpdateTicker++;
    if (playheadUpdateTicker > 30) {
      DebugMode.logI(TAG, "reportPlayheadUpdate fired " + playheadUpdateTicker + " times");
      playheadUpdateTicker = 0;
    }
  }

  @Override
  public void reportReplay() {
    DebugMode.logI(TAG, "reportReplay");
  }

  @Override
  public void reportPlayRequested() {
    DebugMode.logI(TAG, "reportPlayRequested");
  }

}
