package com.ooyala.android;

import com.ooyala.android.ads.vast.NonLinear;

/**
 * A class to transfer ad information required to render overlays
 */
public class AdOverlayInfo {
  private final int width;
  private final int height;
  private int expandedWidth;
  private int expandedHeight;
  private String resourceUrl;
  private String clickUrl;

  /**
   * create an ad overlay info
   * @param width the width in pixels
   * @param height the height in pixels
   * @param expandedWidth the expandedWidth in pixels
   * @param expandedHeight the expandedHeight in pixels
   * @param resourceUrl the resource url
   * @param clickUrl the click url
   */
  public AdOverlayInfo(int width, int height, int expandedWidth, int expandedHeight, String resourceUrl, String clickUrl) {
    this.width = width;
    this.height = height;
    this.expandedWidth = expandedWidth;
    this.expandedHeight = expandedHeight;
    this.resourceUrl = resourceUrl;
    this.clickUrl = clickUrl;
  }

  /**
   * create an ad overlay info by VAST nonlinear
   * @param nonLinear the VAST nonlinear object
   */
  public AdOverlayInfo(NonLinear nonLinear) {
    this.width = nonLinear.getWidth();
    this.height = nonLinear.getHeight();
    this.expandedWidth = nonLinear.getExpandedWidth();
    this.expandedHeight = nonLinear.getExpandedHeight();
    this.resourceUrl = nonLinear.getResource().getUri();
    this.clickUrl = nonLinear.getClickThrough();
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
    return this.height;
  }

  /**
   * @return the expanded width
   */
  public int getExpandedWidth() {
    return this.expandedWidth;
  }

  /**
   * @return the expanded height
   */
  public int getExpandedHeight() {
    return this.expandedHeight;
  }

  /**
   * @return the resource url
   */
  public String getResourceUrl() {
    return resourceUrl;
  }

  /**
   * @return the click url
   */
  public String getClickUrl() {
    return clickUrl;
  }
}
