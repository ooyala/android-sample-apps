package com.ooyala.android;

/**
 * Created by zchen on 3/3/16.
 * A class to transfer ad icon information required to render ad icon
 */
public class AdIconInfo {
  private final int index;
  private final int width;
  private final int height;
  private final int xPosition;
  private final int yPosition;
  private final double offset;
  private final double duration;
  private final String resourceUrl;

  /**
   * Create an ad icon info
   *
   * @param index the index of the icon in the list
   * @param width the width of icon, in pixels
   * @param height a click through url, if presented then the ad is clickable
   * @param x the x position, in pixels, top is 0
   * @param y the y position, in pixels, left is 0
   * @param offset the time offset, in seconds, when icon should start displaying
   * @param duration the duration for the icon to display
   * @param resourceUrl the url for the icon
   */
  public AdIconInfo(int index, int width, int height, int x, int y, double offset, double duration, String resourceUrl) {
    this.index = index;
    this.width = width;
    this.height = height;
    this.xPosition = x;
    this.yPosition = y;
    this.offset = offset;
    this.duration = duration;
    this.resourceUrl = resourceUrl;
  }

  /**
   * @return the index
   */
  public int getIndex() {
    return index;
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * @return the x position
   */
  public int getxPosition() {
    return xPosition;
  }

  /**
   * @return the y position
   */
  public int getyPosition() {
    return yPosition;
  }

  /**
   * @return the offset
   */
  public double getOffset() {
    return offset;
  }

  /**
   * @return the duration
   */
  public double getDuration() {
    return duration;
  }

  /**
   * @return the resource url
   */
  public String getResourceUrl() {
    return resourceUrl;
  }
}
