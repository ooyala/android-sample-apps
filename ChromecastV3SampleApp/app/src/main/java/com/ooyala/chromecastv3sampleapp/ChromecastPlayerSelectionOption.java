package com.ooyala.chromecastv3sampleapp;

import android.app.Activity;

public class ChromecastPlayerSelectionOption {
  public String title;
  public String embedCode;
  public String embedCode2;
  public String pcode;
  public String domain;
  public Class<? extends Activity> activity;

  public ChromecastPlayerSelectionOption(String title, String embedCode, String pcode, String domain, Class<? extends Activity> activity) {
    this(title, embedCode, null, pcode, domain, activity);
  }

  public ChromecastPlayerSelectionOption(String title, String embedCode, String embedCode2, String pcode, String domain, Class<? extends Activity> activity) {
    super();
    this.title = title;
    this.embedCode = embedCode;
    this.embedCode2 = embedCode2;
    this.pcode = pcode;
    this.domain = domain;
    this.activity = activity;
  }
}
