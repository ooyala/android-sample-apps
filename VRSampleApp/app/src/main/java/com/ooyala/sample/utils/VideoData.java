package com.ooyala.sample.utils;

import com.ooyala.sample.parser.AdType;

import java.io.Serializable;

public class VideoData implements Serializable {

  private VideoItemType itemType;
  private VideoFeatureType featureType;
  private String title;
  private AdType adType;
  private String embedCode;
  private String pCode;
  private String domain;

  public VideoData(VideoItemType itemType, String title, AdType adType, String embedCode) {
    this(itemType, VideoFeatureType.REGULAR, title, adType, embedCode, "BzY2syOq6kIK6PTXN7mmrGVSJEFj", "http://www.ooyala.com");
  }

  public VideoData(VideoItemType itemType, VideoFeatureType featureType, String title, AdType adType, String embedCode, String pCode) {
    this(itemType, featureType, title, adType, embedCode, (pCode == null ? "BzY2syOq6kIK6PTXN7mmrGVSJEFj" : pCode), "http://www.ooyala.com");
  }

  public VideoData(VideoItemType itemType, VideoFeatureType featureType, String title, AdType AdType, String embedCode, String pCode, String domain) {
    this.itemType = itemType;
    this.featureType = featureType;
    this.title = title;
    this.adType = AdType;
    this.embedCode = embedCode;
    this.pCode = pCode;
    this.domain = domain;
  }

  public VideoItemType getItemType() {
    return itemType;
  }

  public String getTitle() {
    return title;
  }

  public AdType getAdType() {
    return adType;
  }

  public String getEmbedCode() {
    return embedCode;
  }

  public String getpCode() {
    return pCode;
  }

  public String getDomain() {
    return domain;
  }

  public VideoFeatureType getFeatureType() {
    return featureType;
  }
}
