package com.ooyala.android.item;

/**
 * The base of OoyalaAdSpot, VastAdSpot and other type of ad spot
 */
public abstract class AdSpot implements Comparable<AdSpot> {
  /**
   * Fetch the time at which this AdSpot should play.
   * @return The time at which this AdSpot should play in milliseconds.
   */
  public abstract int getTime();

  /**
   * Fetch the priority for the adspots that have same time.
   * The ad spot with higher priority(smaller value) will be played first.
   * subclass should override the default implementation.
   * @return The priority .
   */
  public int getPriority() {
    return 0;
  };

  /**
   * compare two ad spots based on time, which is required to properly sort ad
   * spots.
   * 
   * @param ad the ad to be compared
   */
  public int compareTo(AdSpot ad) {
    int result = this.getTime() - ad.getTime();
    if (result == 0) {
      result = ad.getPriority() - this.getPriority();
    }
    return result;
  }

  /**
   * @return if the ad spot needs to pause content and play an ad stream
   * return true if content needs to be paused, e.g. VAST Linear ads
   * false if content does not need to be paused, e.g. VAST Nonlinear ads
   */
  public boolean needsPauseContent() {
    return true;
  }
}
