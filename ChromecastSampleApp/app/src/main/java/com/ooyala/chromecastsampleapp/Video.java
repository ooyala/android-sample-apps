package com.ooyala.chromecastsampleapp;

public class Video {
  public String title;
  public int icon;
  public String embedCode;

  public Video() {
    super();
  }

  public Video(int icon, String title, String embedCode) {
    super();
    this.icon = icon;
    this.title = title;
    this.embedCode = embedCode;
  }
}
