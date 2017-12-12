package com.ooyala.sample.utils;

import com.ooyala.sample.parser.AdType;

import java.io.Serializable;

public class VideoData implements Serializable {

  private VideoItemType itemType;
  private String title;
  private AdType adType;
  private String embedCode;
  private String pCode;
  private String domain;

  public VideoData(VideoItemType itemType, String title, AdType AdType, String embedCode) {
    this.itemType = itemType;
    this.title = title;
    this.adType = AdType;
    this.embedCode = embedCode;
    this.pCode = "BzY2syOq6kIK6PTXN7mmrGVSJEFj";
    this.domain = "http://www.ooyala.com";
  }

  public VideoData(VideoItemType itemType, String title, AdType AdType, String embedCode, String pCode) {
    this.itemType = itemType;
    this.title = title;
    this.adType = AdType;
    this.embedCode = embedCode;
    this.pCode = pCode == null ? "BzY2syOq6kIK6PTXN7mmrGVSJEFj" : pCode;
    this.domain = "http://www.ooyala.com";
  }

  public VideoData(VideoItemType itemType, String title, AdType AdType, String embedCode, String pCode, String domain) {
    this.itemType = itemType;
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
}
