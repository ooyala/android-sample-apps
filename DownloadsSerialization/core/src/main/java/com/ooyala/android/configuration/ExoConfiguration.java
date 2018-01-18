package com.ooyala.android.configuration;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.ooyala.android.util.DebugMode;

/**
 * A read-only configuration of ExoPlayer functionality in the OoyalaPlayer.
 *
 * @see ExoConfiguration.Builder
 */
public class ExoConfiguration {
  private static final String TAG = ExoConfiguration.class.getSimpleName();
  private final long upperBitrateThreshold;
  private final long lowerBitrateThreshold;
  private final int minBufferMs;
  private final int maxBufferMs;
  private final long bufferForPlaybackMs;
  private final long bufferForPlaybackAfterRebufferMs;
  private final boolean filterHdContent;

  /**
   * Build the object of VisualOn configurations
   */
  public static class Builder {
    private long upperBitrateThreshold;
    private long lowerBitrateThreshold;
    private int minBufferMs;
    private int maxBufferMs;
    private long bufferForPlaybackMs;
    private long bufferForPlaybackAfterRebufferMs;
    private boolean filterHdContent;

    public Builder() {
      this.upperBitrateThreshold = Integer.MAX_VALUE;
      this.lowerBitrateThreshold = 0;
      this.minBufferMs = DefaultLoadControl.DEFAULT_MIN_BUFFER_MS;
      this.maxBufferMs = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
      this.bufferForPlaybackMs = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS;
      this.bufferForPlaybackAfterRebufferMs = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;
      this.filterHdContent = true;
    }

    /**
     * Set the upper bit rate threshold
     *
     * @param upperBitrateThreshold
     * @return the Builder object to continue building
     */
    public Builder setUpperBitrateThreshold(long upperBitrateThreshold) {
      this.upperBitrateThreshold = upperBitrateThreshold;
      return this;
    }

    /**
     * Set the lower bit rate threshold
     *
     * @param lowerBitrateThreshold
     * @return the Builder object to continue building
     */
    public Builder setLowerBitrateThreshold(long lowerBitrateThreshold) {
      this.lowerBitrateThreshold = lowerBitrateThreshold;
      return this;
    }

    /**
     * Set maximum duration of media that can be buffered during video playback
     *
     * @param maxBufferMs The maximum duration of media that the player will attempt to buffer,
     *                    in milliseconds
     * @return the Builder object to continue building
     */
    public Builder setMaxBufferMs(int maxBufferMs) {
      this.maxBufferMs = maxBufferMs;
      return this;
    }

    /**
     * Set minimum duration of media that can be buffered during video playback
     *
     * @param minBufferMs The minimum duration of media that the player will attempt to
     *                    ensure is buffered at all times, in milliseconds
     * @return the Builder object to continue building
     */
    public Builder setMinBufferMs(int minBufferMs) {
      this.minBufferMs = minBufferMs;
      return this;
    }

    /**
     * Set the duration of media that must be buffered for playback to resume after a rebuffer,
     * in milliseconds
     *
     * @param bufferForPlaybackAfterRebufferMs The duration of media that must be buffered for
     *                                      playback to resume after a rebuffer, in milliseconds. A rebuffer is defined to be caused by
     *                                      buffer depletion rather than a user action
     * @return the Builder object to continue building
     */
    public Builder setBufferPlaybackAfterRebufferMs(long bufferForPlaybackAfterRebufferMs) {
      this.bufferForPlaybackAfterRebufferMs = bufferForPlaybackAfterRebufferMs;
      return this;
    }

    /**
     * Set the default duration of media that must be buffered for playback to start or resume
     * following a user action
     *
     * @param bufferForPlaybackMs The default duration of media that must be buffered for playback
     *                         to start or resume following a user action such as a seek, in milliseconds
     * @return the Builder object to continue building
     */
    public Builder setBufferPlaybackMs(long bufferForPlaybackMs) {
      this.bufferForPlaybackMs = bufferForPlaybackMs;
      return this;
    }

    /**
     * Set this flag to "false" if you wanted to play HD contents on devices which are running
     * widevine L3 security. Most widevine L3 security devices are running on Android 4.4.X OR api 19.
     * <p>
     * CAUTION : We do not recommend using this. This is only for customers who want to have HD
     * contents all the time.
     * If customers are having low bandwidth connections. It might cause streaming performance problems
     * as it will prioritize HD contents rather than best stream according to network connections speed.
     */
    public Builder setFilterHdContent(boolean filterHdContent) {
      this.filterHdContent = filterHdContent;
      return this;
    }

    public ExoConfiguration build() {
      if (validate()) {
        return new ExoConfiguration(upperBitrateThreshold, lowerBitrateThreshold, minBufferMs,
            maxBufferMs, bufferForPlaybackMs, bufferForPlaybackAfterRebufferMs, filterHdContent);
      } else {
        return null;
      }
    }

    private boolean validate() {
      if ((lowerBitrateThreshold > upperBitrateThreshold) ||
          (minBufferMs > maxBufferMs)) {
        DebugMode.logE(TAG,
            "Invalid parameters: upperBitrate " + upperBitrateThreshold + " lowerBitrate " + lowerBitrateThreshold + " maxBufferMs "
                + maxBufferMs + " minBufferMs " + minBufferMs + "bufferForPlaybackAfterRebufferMs" + bufferForPlaybackAfterRebufferMs
                + "bufferForPlaybackMs " + bufferForPlaybackMs);
        return false;
      }
      return true;
    }
  }

  /**
   * Provides the default ExoConfiguration
   *
   * @return the default configuration
   */
  public static ExoConfiguration getDefaultExoConfiguration() {
    return new Builder().build();
  }

  private ExoConfiguration(
      long upperBitrateThreshold,
      long lowerBitrateThreshold,
      int minBufferMs,
      int maxBufferMs,
      long bufferForPlaybackMs,
      long bufferForPlaybackAfterRebufferMs,
      boolean filterHdContent) {
    this.upperBitrateThreshold = upperBitrateThreshold;
    this.lowerBitrateThreshold = lowerBitrateThreshold;
    this.minBufferMs = minBufferMs;
    this.maxBufferMs = maxBufferMs;
    this.bufferForPlaybackAfterRebufferMs = bufferForPlaybackAfterRebufferMs;
    this.bufferForPlaybackMs = bufferForPlaybackMs;
    this.filterHdContent = filterHdContent;
  }

  /**
   * @return upper bit rate threshold
   */
  public long getUpperBitrateThreshold() {
    return upperBitrateThreshold;
  }

  /**
   * @return lower bit rate threshold
   */
  public long getLowerBitrateThreshold() {
    return lowerBitrateThreshold;
  }

  /**
   * @return the maximum duration of media that the player will attempt to buffer,
   * in milliseconds
   */
  public long getMaxBufferMs() {
    return maxBufferMs;
  }

  /**
   * @return the minimum duration of media that the player will attempt to
   * ensure is buffered at all times, in milliseconds
   */
  public long getMinBufferMs() {
    return minBufferMs;
  }

  /**
   * @return high buffer load
   */
  public float getBufferForPlaybackAfterRebufferMs() {
    return bufferForPlaybackAfterRebufferMs;
  }

  /**
   * @return the duration of media that must be buffered for playback
   * to start or resume following a user action such as a seek, in milliseconds
   */
  public float getBufferForPlaybackMs() {
    return bufferForPlaybackMs;
  }

  /**
   * @return filter HD content Default is "true"
   */
  public boolean isFilterHdContent() {
    return filterHdContent;
  }

  /**
   * Log all of the parameters that are part of the Options class
   */
  public void logOptionsData() {
    DebugMode.logD(TAG,
        "this.upperBitrateThreshold = " + upperBitrateThreshold + "\n" +
            "this.lowerBitrateThreshold = " + lowerBitrateThreshold + "\n" +
            "this.minBufferMs = " + minBufferMs + "\n" +
            "this.maxBufferMs = " + maxBufferMs + "\n" +
            "this.bufferForPlaybackMs = " + bufferForPlaybackMs + "\n" +
            "this.bufferPlaybackAfterRebufferMs = " + bufferForPlaybackAfterRebufferMs + "\n" +
            "this.filterHdContent = " + filterHdContent + "\n");
  }
}
