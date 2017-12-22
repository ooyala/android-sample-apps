package com.ooyala.android.configuration;

import android.text.TextUtils;

import com.ooyala.android.PlayerInfo;
import com.ooyala.android.Utils;
import com.ooyala.android.analytics.IqConfiguration;
import com.ooyala.android.util.DebugMode;

import java.util.List;

/**
 * A read-only class of configurations for OoyalaPlayer. Use the Builder class to generate a valid
 * Options for the OoyalaPlayer
 * @see Options.Builder
 */
public class Options implements ReadonlyOptionsInterface {

  private static String TAG = Options.class.getSimpleName();

  /**
   * Supports a fluid syntax for configuration.
   */
  public static class Builder {

    /**
     * FCCTVRatingConfiguration;
     * Default: default FCCTVRatingConfiguration object
     */
    private FCCTVRatingConfiguration tvRatingConfiguration;

    /**
     * ExoConfiguration
     * Default: default ExoConfiguration object
     */
    private ExoConfiguration exoConfiguration;

    /**
     *If set to "true",  show ad controls during ads playback.
     * Default: true
     */
    private boolean showAdsControls;

    /**
     * If set to "true", show cuepoint markers for ads.
     * Default: true
     */
    private boolean showCuePoints;

    /**
     * if set to the object, shows the default values for the iqAnalytics.
     * default:ooyala_android
     */
    private IqConfiguration iqConfiguration;

    /**
     * If set to "true", show live controls for live content playback (live stream only).
     * Default: true
     */
    private boolean showLiveControls;

    /**
     * If set to "true", load the content when the required information and authorization is available.
     * If set to "false", load the content after pre-roll (if pre-roll is available).
     *  Default: true
     */
    private boolean preloadContent;

    /**
     * If set to "true", show a promo image if one is available.
     *  Default: false
     */
    private boolean showPromoImage;

    /**
     * Network connection timeout value used by networking operations.
     *  Default: 0
     */
    private int connectionTimeoutInMillisecond;

    /**
     * Read timeout value used by networking operations.
     * Default: 0
     */
    private int readTimeoutInMillisecond;

    /**
     * True to prevent screen sharing, false to allow.
     */
    private boolean preventVideoViewSharing;

    /**
     * True to use exoplayer
     */
    private boolean useExoPlayer;

    /**
     * True to use adobeplayer
     */
    private boolean useAdobePlayer;

    /**
     * True to show native learn more button
     */
    private boolean showNativeLearnMoreButton;

    /**
     * True to bypass PCode matching.
     *  Default: false
     */
    private boolean bypassPCodeMatching;

    /**
     * The player info to be used
     */
    private PlayerInfo playerInfo;

    /**
     * True to disable the VAST and Ooyala ad manager, default false
     */
    private boolean disableVASTOoyalaAds;

    /**
     * Dynamic filters to be sent to Azure.
     */
    private List<String> dynamicFilters;

    /**
     * Defaults to the following values:
     * tvRatingConfiguration = FCCTVRatingConfiguration.s_getDefaultTVRatingConfiguration();
     * exoConfiguration = ExoConfiguration.getDefaultExoConfiguration();
     * iqConfiguration = IqConfiguration.getDefaultIqConfiguration();
     * showCuePoints = true;
     * showAdsControls = true;
     * showLiveControls = true;
     * preloadContent = true;
     * showPromoImage = false;
     * connectionTimeoutInMillisecond = 0;
     * readTimeoutInMillisecond = 0;
     * preventVideoViewSharing = false;
     * playerInfo = null;
     * showNativeLearnMore = true;
     * bypassPCodeMatching = false;
     * disableVASTOoyalaAds = false;
     * dynamicFilters = null;
     */
    public Builder() {
      this.tvRatingConfiguration = FCCTVRatingConfiguration.s_getDefaultTVRatingConfiguration();
      this.exoConfiguration = ExoConfiguration.getDefaultExoConfiguration();
      this.iqConfiguration = IqConfiguration.getDefaultIqConfiguration();
      this.showCuePoints = true;
      this.showAdsControls = true;
      this.showLiveControls = true;
      this.preloadContent = true;
      this.showPromoImage = false;
      this.connectionTimeoutInMillisecond = 10 * 1000;
      this.readTimeoutInMillisecond = 10 * 1000;
      this.preventVideoViewSharing = false;
      this.useExoPlayer = true;
      this.useAdobePlayer = false;
      this.playerInfo = null;
      this.showNativeLearnMoreButton = true;
      this.bypassPCodeMatching = false;
      this.disableVASTOoyalaAds = false;
      this.dynamicFilters = null;
    }

    /**
     * Set the TV Rating configuration.
     * @param tvRatingConfiguration - The configuration.
     * @return this.
     */
    public Builder setTVRatingConfiguration( FCCTVRatingConfiguration tvRatingConfiguration ) {
      this.tvRatingConfiguration = tvRatingConfiguration;
      return this;
    }

    public Builder setIqConfiguration(IqConfiguration iqConfiguration) {
      this.iqConfiguration = iqConfiguration;
      return this;
    }

    /**
     * Set the ExoPlayer configuration.
     * @param exoConfig - The configuration.
     * @return this.
     */
    public Builder setExoConfiguration(ExoConfiguration exoConfig) {
      this.exoConfiguration = exoConfig;
      return this;
    }

    /**
     * Set show cue points
     * @param showCuePoints - True to show, false otherwise, the default value is true.
     * @return this.
     */
    public Builder setShowCuePoints(boolean showCuePoints) {
      this.showCuePoints = showCuePoints;
      return this;
    }

    /**
     * Set show ads controls
     * @param showAdsControls - True to show controls while playing ads, false otherwise.
     *                        The default value is true.
     * @return this.
     */
    public Builder setShowAdsControls(boolean showAdsControls) {
      this.showAdsControls = showAdsControls;
      return this;
    }

    /**
     * Set preload content
     * @param preloadContent - True to preload, false otherwise. The default value is true.
     * @return this.
     */
    public Builder setPreloadContent(boolean preloadContent) {
      this.preloadContent = preloadContent;
      return this;
    }

    /**
     * Set show promotion image
     * @param showPromoImage - True to show, false otherwise. The default value is false.
     * @return this.
     */
    public Builder setShowPromoImage(boolean showPromoImage) {
      this.showPromoImage = showPromoImage;
      return this;
    }

    /**
     * Set show live controls
     * @param showLiveControls - True to show, false otherwise. The default value is true.
     * @return
     */
    public Builder setShowLiveControls(boolean showLiveControls) {
      this.showLiveControls = showLiveControls;
      return this;
    }

    /**
     * Set the connection timeout value used by networking operations.
     * @param connectionTimeoutInMillisecond - The connection timeout in milliseconds.
     * The default value is 0 (never times out).
     */
    public Builder setConnectionTimeout(int connectionTimeoutInMillisecond) {
      this.connectionTimeoutInMillisecond = connectionTimeoutInMillisecond;
      return this;
    }

    /**
     * Set the stream read timeout value used by networking operations.
     * @param readTimeoutInMillisecond -  The read timeout in milliseconds.
     *The default value is 0, (never times out).
     */
    public Builder setReadTimeout(int readTimeoutInMillisecond) {
      this.readTimeoutInMillisecond = readTimeoutInMillisecond;
      return this;
    }

    public Builder setPreventVideoViewSharing( boolean preventVideoViewSharing ) {
      this.preventVideoViewSharing = preventVideoViewSharing;
      return this;
    }

    /**
     * Set use exoplayer
     * @param useExoPlayer - True to use ExoPlayer, false uses MediaPlayer. The default value is true.
     */
    public Builder setUseExoPlayer(boolean useExoPlayer) {
      this.useExoPlayer = useExoPlayer;
      return this;
    }

    /**
     * Set use adobePlayer
     * @param useAdobePlayer - True to use, false otherwise. The default value is false.
     */
    public Builder setUseAdobePlayer(boolean useAdobePlayer) {
      this.useAdobePlayer = useAdobePlayer;
      return this;
    }

    /**
     * Set the player info to be communicated to the cloud.
     * @param playerInfo -  The info
     */
    public Builder setPlayerInfo(PlayerInfo playerInfo) {
      this.playerInfo = playerInfo;
      return this;
    }

    /**
     * Set show native learn more button
     * @param showNativeLearnMoreButton - True to show, false otherwise. The default value is true.
     */
    public Builder setShowNativeLearnMoreButton( boolean showNativeLearnMoreButton ) {
      this.showNativeLearnMoreButton = showNativeLearnMoreButton;
      return this;
    }

    /** Set bypass PCode matching
     * @param bypassPCodeMatching - True to bypass PCode matching.  The default value is false.
     */
    public Builder setBypassPCodeMatching(boolean bypassPCodeMatching) {
      this.bypassPCodeMatching = bypassPCodeMatching;
      return this;
    }

    /**
     * Set disable VAST and Ooyala ads.  This does not affect IMA, Freewheel, Pulse, or other third party ad managers
     * This only affects VAST and Ooyala ads, as defined in the backlot ad sets
     * @param disableVASTOoyalaAds - True to disable VAST and Ooyala Ad Managers
     */
    public Builder setDisableVASTOoyalaAds(boolean disableVASTOoyalaAds) {
      this.disableVASTOoyalaAds = disableVASTOoyalaAds;
      return this;
    }

    /**
     * Set the dynamic filters to be sent to Azure.
     * @param dynamicFilters - True to disable VAST and Ooyala Ad Managers
     */
    public Builder setDynamicFilters(List<String> dynamicFilters) {
      this.dynamicFilters = dynamicFilters;
      return this;
    }
    public Options build() {
      return new Options(
          tvRatingConfiguration,
          exoConfiguration,
          iqConfiguration,
          showCuePoints,
          showAdsControls,
          preloadContent,
          showPromoImage,
          showLiveControls,
          connectionTimeoutInMillisecond,
          readTimeoutInMillisecond,
          preventVideoViewSharing,
          useExoPlayer,
          showNativeLearnMoreButton,
          bypassPCodeMatching,
          playerInfo,
          disableVASTOoyalaAds,
          useAdobePlayer,
          dynamicFilters);
    }
  }

  private final FCCTVRatingConfiguration tvRatingConfiguration;
  private final ExoConfiguration exoConfiguration;
  private final boolean showCuePoints;
  private final IqConfiguration iqConfiguration;
  private final boolean showAdsControls;
  private final boolean preloadContent;
  private final boolean showPromoImage;
  private final int connectionTimeoutInMillisecond;
  private final int readTimeoutInMillisecond;
  private final boolean showLiveControls;
  private final boolean preventVideoViewSharing;
  private final boolean useExoPlayer;
  private final PlayerInfo playerInfo;
  private final boolean showNativeLearnMoreButton;
  private final boolean bypassPCodeMatching;
  private final boolean disableVASTOoyalaAds;
  private final boolean useAdobePlayer;
  private final List<String> dynamicFilters;

  /**
   * Initialize an Options object with given parameters:
   * @param tvRatingConfiguration - Configure to use TV Ratings.
   * @param exoConfiguration - Configure to use ExoPlayer.
   * @param iqConfiguration - configure to use Iq Analytics.
   * @param showCuePoints - Configure to show cue p oint markers.
   * @param showAdsControls - Configure to show ad controls in the player.
   * @param preloadContent - Configure to preload content before playback is initiated.
   * @param showPromoImage - Configure to show a promo image if one is avaiable.
   * @param showLiveControls - Configure to show live controls
   * @param connectionTimeoutInMillisecond - Configure to set a connection timeout value.
   * @param readTimeoutInMillisecond - Configure to set a read time out value.
   * @param preventVideoViewSharing - Configure to prevent/allow video sharing.
   * @param useExoPlayer- Indicates if we want to use ExoPlayer or not. If false, MediaPlayer will be used instead.
   * @param showNativeLearnMoreButton - Configure to show learn more button.
   * @param playerInfo - Congfigure to use custom player info.
   * @param bypassPCodeMatching - Configure to bypass PCode matching.
   * @param disableVASTOoyalaAds - Configure to disable VAST and Ooyala ads.
   * @param dynamicFilters - Dynamic Filters to be sent to Azure.

   * @return the initialized Options - Return the configured options.
   */
  private Options(FCCTVRatingConfiguration tvRatingConfiguration,
                  ExoConfiguration exoConfiguration,
                  IqConfiguration iqConfiguration,
                  boolean showCuePoints,
                  boolean showAdsControls,
                  boolean preloadContent,
                  boolean showPromoImage,
                  boolean showLiveControls,
                  int connectionTimeoutInMillisecond,
                  int readTimeoutInMillisecond,
                  boolean preventVideoViewSharing,
                  boolean useExoPlayer,
                  boolean showNativeLearnMoreButton,
                  boolean bypassPCodeMatching,
                  PlayerInfo playerInfo,
                  boolean disableVASTOoyalaAds,
                  boolean useAdobePlayer,
                  List<String> dynamicFilters) {

    this.tvRatingConfiguration = tvRatingConfiguration;
    this.exoConfiguration = exoConfiguration;
    this.iqConfiguration = iqConfiguration;
    this.showCuePoints = showCuePoints;
    this.showAdsControls = showAdsControls;
    this.preloadContent = preloadContent;
    this.showPromoImage = showPromoImage;
    this.showLiveControls = showLiveControls;
    this.connectionTimeoutInMillisecond = connectionTimeoutInMillisecond;
    this.readTimeoutInMillisecond = readTimeoutInMillisecond;
    this.preventVideoViewSharing = preventVideoViewSharing;
    this.useExoPlayer = useExoPlayer;
    this.playerInfo = playerInfo;
    this.showNativeLearnMoreButton = showNativeLearnMoreButton;
    this.bypassPCodeMatching = bypassPCodeMatching;
    this.disableVASTOoyalaAds = disableVASTOoyalaAds;
    this.useAdobePlayer = useAdobePlayer;
    this.dynamicFilters = dynamicFilters;
  }

  @Override
  public FCCTVRatingConfiguration getTVRatingConfiguration() {
    return tvRatingConfiguration;
  }

  @Override
  public ExoConfiguration getExoConfiguration() {
    return exoConfiguration;
  }

  @Override
  public IqConfiguration getIqConfiguration() {
    return iqConfiguration;
  }

  @Override
  public boolean getShowAdsControls() {
    return showAdsControls;
  }

  @Override
  public boolean getShowCuePoints() {
    return showCuePoints;
  }

  @Override
  public boolean getPreloadContent() {
    return preloadContent;
  }

  @Override
  public boolean getShowPromoImage() {
    return showPromoImage;
  }

  @Override
  public boolean getShowLiveControls() {
    return showLiveControls;
  }

  @Override
  public int getConnectionTimeoutInMillisecond() {
    return connectionTimeoutInMillisecond;
  }

  @Override
  public int getReadTimeoutInMillisecond() {
    return readTimeoutInMillisecond;
  }

  @Override
  public boolean getPreventVideoViewSharing() {
    return preventVideoViewSharing;
  }

  @Override
  public boolean getUseExoPlayer() {
    return useExoPlayer;
  }

  //@Override
  public boolean getUseAdobePlayer() {
    return useAdobePlayer;
  }

  @Override
  public PlayerInfo getPlayerInfo() {
    return playerInfo;
  }

  @Override
  public boolean getShowNativeLearnMoreButton() {
    return showNativeLearnMoreButton;
  }

  @Override
  public boolean getBypassPCodeMatching() {
    return bypassPCodeMatching;
  }

  @Override
  public boolean getDisableVASTOoyalaAds() {
    return disableVASTOoyalaAds;
  }

  @Override
  public List<String> getDynamicFilters() { return dynamicFilters; }

  /**
   * Log all of the parameters that are part of the Options class
   */
  public void logOptionsData() {
    DebugMode.logD(TAG,
        "this.tvRatingConfiguration = " + tvRatingConfiguration + "\n" +
        "this.exoConfiguration = " + exoConfiguration + "\n" +
        "this.iqConfiguration = " + iqConfiguration + "\n" +
        "this.showCuePoints = " + showCuePoints + "\n" +
        "this.showAdsControls = " + showAdsControls + "\n" +
        "this.preloadContent = " + preloadContent + "\n" +
        "this.showPromoImage = " + showPromoImage + "\n" +
        "this.showLiveControls = " + showLiveControls + "\n" +
        "this.connectionTimeoutInMillisecond = " + connectionTimeoutInMillisecond + "\n" +
        "this.readTimeoutInMillisecond = " + readTimeoutInMillisecond + "\n" +
        "this.preventVideoViewSharing = " + preventVideoViewSharing + "\n" +
        "this.useExoPlayer = " + useExoPlayer + "\n" +
        "this.useAdobePlayer = " + useAdobePlayer + "\n" +
        "this.playerInfo = " + playerInfo + "\n" +
        "this.showNativeLearnMoreButton = " + showNativeLearnMoreButton + "\n" +
        "this.bypassPCodeMatching = " + bypassPCodeMatching + "\n" +
        "this.disableVASTOoyalaAds = " + disableVASTOoyalaAds + "\n" +
        "this.dynamicFilters = " + (dynamicFilters != null ? Utils.join(dynamicFilters, ",") : null));

    tvRatingConfiguration.logOptionsData();
    exoConfiguration.logOptionsData();
    iqConfiguration.logOptionsData();
  }
}
