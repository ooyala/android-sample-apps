package com.ooyala.android.configuration;

import com.ooyala.android.PlayerInfo;
import com.ooyala.android.analytics.IqConfiguration;

import java.util.List;

public interface ReadonlyOptionsInterface {

  /**
   * @see FCCTVRatingConfiguration
   */
  FCCTVRatingConfiguration getTVRatingConfiguration();

  /**
   * @see ExoConfiguration
   */
  ExoConfiguration getExoConfiguration();

  /**
   * @see IqConfiguration
   */
  IqConfiguration getIqConfiguration();

  /**
   * If "true",  show ad controls during ads playback.
   */
  boolean getShowAdsControls();

  /**
   * If "true", show cuepoint markers for ads.
   */
  boolean getShowCuePoints();

  /**
   * If "true", show live controls for live content playback (live stream only).
   */
  boolean getShowLiveControls();

  /**
   * If "true", load the content when the rquired information and authorization is available.
   * If "false", load the content after pre-roll (if pre-roll is available).
   */
  boolean getPreloadContent();

  /**
   * If "true", show a promo image if one is available.
   */
  boolean getShowPromoImage();

  /**
   * Network connection timeout value used by networking operations.
   */
  int getConnectionTimeoutInMillisecond();

  /**
   * Read timeout value used by networking operations.
   */
  int getReadTimeoutInMillisecond();

  /**
   * True is prevent video view sharing, false is allow.
   */
  boolean getPreventVideoViewSharing();

  /**
   * True if use exoplayer, false otherwise.
   */
  boolean getUseExoPlayer();

  /**
   * @see PlayerInfo
   */
  PlayerInfo getPlayerInfo();

  /**
   * True to show native learn more button, false otherwise.
   */
  boolean getShowNativeLearnMoreButton();

  /**
   * True to bypass PCode matching, false otherwise.
   */
  boolean getBypassPCodeMatching();

  /**
   * True to disable VAST and Ooyala ads
   */
  boolean getDisableVASTOoyalaAds();

  /**
   * Dynamic filters to be sent to Azure.
   */
  List<String> getDynamicFilters();
}

