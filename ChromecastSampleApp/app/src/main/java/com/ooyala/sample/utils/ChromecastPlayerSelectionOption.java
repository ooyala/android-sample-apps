package com.ooyala.sample.utils;

public class ChromecastPlayerSelectionOption {
  public String title;
  public String embedCode;
  public String embedCode2;
  public String pcode;
  public String domain;

  public ChromecastPlayerSelectionOption(String title, String embedCode, String pcode, String domain) {
    this( title, embedCode, null, pcode, domain );
  }

  public ChromecastPlayerSelectionOption(String title, String embedCode, String embedCode2, String pcode, String domain) {
    super();
    this.title = title;
    this.embedCode = embedCode;
    this.embedCode2 = embedCode2;
    this.pcode = pcode;
    this.domain = domain;
  }
}
