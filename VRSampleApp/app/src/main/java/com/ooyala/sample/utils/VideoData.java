package com.ooyala.sample.utils;

public class VideoData {

  private VideoItemType itemType;
  private String title;
  private boolean hasIma;
  private String embedCode;
  private String pCode;
  private String domain;

  public VideoData(VideoItemType itemType, String title, boolean hasIma, String embedCode) {
    this.itemType = itemType;
    this.title = title;
    this.hasIma = hasIma;
    this.embedCode = embedCode;
    this.pCode = "BzY2syOq6kIK6PTXN7mmrGVSJEFj";
    this.domain = "http://www.ooyala.com";
  }

  public VideoData(VideoItemType itemType, String title, boolean hasIma, String embedCode, String pCode) {
    this.itemType = itemType;
    this.title = title;
    this.hasIma = hasIma;
    this.embedCode = embedCode;
    this.pCode = pCode;
    this.domain = "http://www.ooyala.com";
  }

  public VideoData(VideoItemType itemType, String title, boolean hasIma, String embedCode, String pCode, String domain) {
    this.itemType = itemType;
    this.title = title;
    this.hasIma = hasIma;
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

  public boolean isHasIma() {
    return hasIma;
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
