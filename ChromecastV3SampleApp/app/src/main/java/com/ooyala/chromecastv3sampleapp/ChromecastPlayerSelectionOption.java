package com.ooyala.chromecastv3sampleapp;

import android.app.Activity;

public class ChromecastPlayerSelectionOption {
  public String title;
  public String embedCode;
  public String secondEmbedCode;
  public String pcode;
  public String domain;
  public Class<? extends Activity> activity;

  public ChromecastPlayerSelectionOption(String title, String embedCode, String pcode, String domain, Class<? extends Activity> activity) {
    this(title, embedCode, null, pcode, domain, activity);
  }

  public ChromecastPlayerSelectionOption(String title, String embedCode, String secondEmbedCode, String pcode, String domain, Class<? extends Activity> activity) {
    super();
    this.title = title;
    this.embedCode = embedCode;
    this.secondEmbedCode = secondEmbedCode;
    this.pcode = pcode;
    this.domain = domain;
    this.activity = activity;
  }
}
