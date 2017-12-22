package com.ooyala.android.analytics;

import com.ooyala.android.SeekInfo;
import com.ooyala.android.item.Video;

/**
 * An Interface that can be used to plug an Analytics reporter into the OoyalaPlayer
 *
 * Your Analytics plugin may require more functionality than is reported here.  If this is the case
 * you can additionally observe OoyalaPlayer, and report any other metrics through the observation chain
 *
 * You should most likely NOT implement this interface, and instead extend AnalyticsPluginBaseImpl
 * @see AnalyticsPluginBaseImpl
 *
 * @see com.ooyala.android.OoyalaPlayer#registerPlugin(AnalyticsPluginInterface)
 *
 */
public interface AnalyticsPluginInterface {

  /**
   * Called when a new video is set
   * @param currentItem the video that we're going to send information about. It may be null, be sure to check it.
   */
  void onCurrentItemAboutToPlay(Video currentItem);
  /**
   * Called when video content starts playing
   */
  void reportPlayStarted();

  /**
   * Called when video content is paused
   */
  void reportPlayPaused();

  /**
   * Called when video content is resumed from paused state;
   */
  void reportPlayResumed();

  /**
   * Called when video content is completed;
   */
  void reportPlayCompleted();

  /**
   * Called when the Plugin is registered, effectively reporting when the player is loaded
   */
  void reportPlayerLoad();

  /**
   * Called whenever the Player reports a playhead change
   */
  void reportPlayheadUpdate(int playheadTime);

  /**
   * Called during the first time video starts playing back after a video was Completed
   */
  void reportReplay();

  /**
   * Called whenever the user or application calls OoyalaPlayer.play()
   */
  void reportPlayRequested();
  /**
   * Called whenever the user starts to seek the video.
   */
  void reportSeek(SeekInfo seekInfo);
}
