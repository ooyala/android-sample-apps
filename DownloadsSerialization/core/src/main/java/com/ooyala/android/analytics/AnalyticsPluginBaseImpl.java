package com.ooyala.android.analytics;

import com.ooyala.android.SeekInfo;
import com.ooyala.android.item.Video;

/**
 * A class that implements empty methods for the entire Analytics plugin interface
 *
 * Implement your plugin by extending this, and whenever the Analytics Plugin Interface is modified,
 * your integration will be unaffected, and continue to compile and be usable
 *
 * Your Analytics plugin may require more functionality than is reported here.  If this is the case
 * you can additionally observe OoyalaPlayer, and report any other metrics through the observation chain
 *
 */
public class AnalyticsPluginBaseImpl implements AnalyticsPluginInterface {

  @Override
  public void onCurrentItemAboutToPlay(Video currentItem){}
  /**
   * Called during the first time video starts playing back after the video is changed
   */
  @Override
  public void reportPlayStarted(){}

  /**
   * Called when content is paused
   */
  @Override
  public void reportPlayPaused(){}

  /**
   * Called when content is resumed
   */
  @Override
  public void reportPlayResumed(){}

  /**
   * Called when content is resumed
   */
  @Override
  public void reportPlayCompleted(){}

  /**
   * Called when the Plugin is registered, effectively reporting when the player is loaded
   */
  @Override
  public void reportPlayerLoad(){}

  /**
   * Called whenever the Player reports a playhead change
   */
  @Override
  public void reportPlayheadUpdate(int playheadTime){}

  /**
   * Called during the first time video starts playing back after a video was Completed
   */
  @Override
  public void reportReplay(){}

  /**
   * Called whenever the user or application calls OoyalaPlayer.play()
   */
  @Override
  public void reportPlayRequested(){}

  @Override
  public void reportSeek(SeekInfo seekInfo) {

  }

}
