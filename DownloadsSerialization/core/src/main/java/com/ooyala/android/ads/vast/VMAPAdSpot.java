package com.ooyala.android.ads.vast;

import org.w3c.dom.Element;

import java.net.URL;

/**
 * A model of a VMAP Ad spot, which extends VAST Ad spot with additional VMAP attributes
 */
public class VMAPAdSpot extends VASTAdSpot {
  private static final String TAG = VASTAdSpot.class.getSimpleName();

  protected final TimeOffset timeOffset;
  protected final String breakType;
  protected final String breakId;
  protected final String adSourceId;
  protected final Boolean allowMultipleAds;
  protected final Boolean followRedirects;
  protected final double repeatAfter;

  protected int repeatCounter;

  /**
   * Create a VMAP Ad Spot with an XML document
   * @param timeOffset the time offset of the ad spot
   * @param duration the duration of the content
   * @param repeatAfter after what time the spot should be repeated. ignored for now
   * @param breakType the break type. ignored for now
   * @param breakId the break ID. ignored for now
   * @param sourceId the source ID.
   * @param allowMultipleAds if multiple ads are allowed
   * @param followRedirects if redirects are followed. ignored for now
   * @param e the root element of the VAST XML
   *
   */
  public VMAPAdSpot(final TimeOffset timeOffset, int duration, double repeatAfter, String breakType, String breakId, String sourceId, Boolean allowMultipleAds, Boolean followRedirects, Element e) {
    super(0, duration, e);
    this.timeOffset = timeOffset;
    this.repeatAfter = repeatAfter;
    this.breakType = breakType;
    this.breakId = breakId;
    this.adSourceId = sourceId;
    this.allowMultipleAds = allowMultipleAds;
    this.followRedirects = followRedirects;
    this.repeatCounter = 0;
  }

  /**
   * create a VMAP Ad Spot with an url to the VAST XML
   * @param timeOffset the time offset of the ad spot
   * @param duration the duration of the content
   * @param repeatAfter after what time the spot should be repeated. ignored for now
   * @param breakType the break type. ignored for now
   * @param breakId the break ID. ignored for now
   * @param sourceId the ad source ID.
   * @param allowMultipleAds if multiple ads are allowed
   * @param followRedirects if redirects are followed. ignored for now
   * @param vastUrl the url to the VAST XML
   *
   */
  public VMAPAdSpot(final TimeOffset timeOffset, int duration, double repeatAfter, String breakType, String breakId, String sourceId, Boolean allowMultipleAds, Boolean followRedirects, URL vastUrl) {
    super(0, duration, null, null, vastUrl);
    this.timeOffset = timeOffset;
    this.repeatAfter = repeatAfter;
    this.breakType = breakType;
    this.breakId = breakId;
    this.adSourceId = sourceId;
    this.allowMultipleAds = allowMultipleAds;
    this.followRedirects = followRedirects;
    this.repeatCounter = 0;
  }

  /**
   * @return the time offset in seconds of when the ad spot should be played
   */
  @Override
  public int getTime() {
    double time = getOriginalTimeInMilliseconds();
    if (repeatAfter > 0) {
      time += repeatCounter * repeatAfter;
    }
    return (int)time;
  }

  /**
   * @return the original time offset, in seconds
   */
  public double getOriginalTimeInMilliseconds() {
    if (timeOffset == null) {
      return super.getTime();
    }

    switch (timeOffset.getType()) {
      case Percentage:
        return (int)(timeOffset.getPercentage() * _contentDuration);
      case Seconds:
        return timeOffset.getSeconds() * 1000;
      default:
        return super.getTime();
    }
  }

  /**
   * @return the time offset
   */
  public final TimeOffset getTimeOffset() {
    return timeOffset;
  }

  /**
   * @return repeat after
   */
  public double getRepeatAfter() {
    return repeatAfter;
  }

  /**
   * @return break type
   */
  public final String getBreakType() {
    return breakType;
  }

  /**
   * @return break id
   */
  public final String getBreakId() {
    return breakId;
  }

  /**
   * @return ad source id
   */
  public final String getAdSourceId() {
    return adSourceId;
  }

  /**
   * @return allow multiple ads
   */
  public boolean getAllowMultipleAds() {
    return allowMultipleAds;
  }

  /**
   * @return follow redirects
   */
  public boolean getFollowRedirects() {
    return followRedirects;
  }

  /**
   * @return true if repeatalbe, false otherwise
   */
  public boolean isRepeatable() {
    return (repeatAfter > 0);
  }

  /**
   * mark the ad spot as played
   */
  public void markAsPlayed() {
    repeatCounter++;
  }

  /**
   * mark the ad spot as unplayed
   */
  public void markAsUnplayed() {
    repeatCounter = 0;
  }
}
