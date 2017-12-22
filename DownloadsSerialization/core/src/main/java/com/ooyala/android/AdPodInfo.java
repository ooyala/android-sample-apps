package com.ooyala.android;

import java.util.List;

/**
 * A class to transfer ad pod information required to render ads
 */
public class AdPodInfo {
  private final String title;
  private final String description;
  private final String clickUrl;
  private final int adsCount;
  private final int unplayedCount;
  private final double skipoffset;
  private final boolean adbar;
  private final boolean controls;
  private final List<AdIconInfo> icons;

  /**
   * Create a non-skippable ad pod info
   *
   * @param title the title of the ad
   * @param description the description of the ad
   * @param clickUrl a click through url, if presented then the ad is clickable
   * @param adsCount the number of total ads in the spot
   * @param unplayedCount the number of ads, excluding the current one, that has not been played.
   */
  public AdPodInfo(String title, String description, String clickUrl, int adsCount, int unplayedCount) {
    this(title, description, clickUrl, adsCount, unplayedCount, -1.0, false, false, null);
  }

  /**
   * Create a complete ad pod info
   *
   * @param title the title of the ad
   * @param description the description of the ad
   * @param clickUrl a click through url, if presented then the ad is clickable
   * @param adsCount the number of total ads in the spot
   * @param unplayedCount the number of ads, excluding the current one, that has not been played.
   * @param adbar specify if UI adbar is required
   * @param controls specify if
   * @param icons a list of ad icons
   */
  public AdPodInfo(String title, String description, String clickUrl, int adsCount, int unplayedCount, double skipoffset, boolean adbar, boolean controls, List<AdIconInfo> icons) {
    this.title = title;
    this.description = description;
    this.clickUrl = clickUrl;
    this.adsCount = adsCount;
    this.unplayedCount = unplayedCount;
    this.skipoffset = skipoffset;
    this.adbar = adbar;
    this.controls = controls;
    this.icons = icons;
  }

  /**
   * @return adbar
   */
  public boolean isAdbar() {
      return adbar;
  }

  /**
   * @return description
   */
  public String getDescription() {
      return description;
  }

  /**
   * @return title
   */
  public String getTitle() {
      return title;
  }

  /**
   * @return clickUrl
   */
  public String getClickUrl() {
      return clickUrl;
  }

  /**
   * @return ads count
   */
  public int getAdsCount() {
      return adsCount;
  }

  /**
   * @return unplayed count
   */
  public int getUnplayedCount() {
      return unplayedCount;
  }

  /**
   * @return controls
   */
  public boolean isControls() {
      return controls;
  }

  /**
   * @return skip offset, in seconds
   */
  public double getSkipOffset() {
    return skipoffset;
  }

  /**
   * @return icons
   */
  public List<AdIconInfo> getIcons() {
    return icons;
  }

  @Override
  public String toString() {
    String string = super.toString();
    if (null != title && title.trim().length() > 0) {
      string += "\ntitle: " + title;
    }
    if (null != description && description.trim().length() > 0) {
      string += "\ndescription: " + description;
    }

    return string;
  }
}
