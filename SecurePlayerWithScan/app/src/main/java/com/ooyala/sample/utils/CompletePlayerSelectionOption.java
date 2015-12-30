package com.ooyala.sample.utils;

import android.app.Activity;

/**
 * Created by zchen on 12/29/15.
 */
public class CompletePlayerSelectionOption extends PlayerSelectionOption {
  private String pcode;
  private String domain;

  public CompletePlayerSelectionOption(String embedCode, String pcode, String domain, Class<? extends Activity> activity) {
    super(embedCode, activity);
    this.pcode = pcode;
    this.domain = domain;
  }

  public String getPcode() {
    return pcode;
  }
  
  public String getDomain() {
    return domain;
  }
}
