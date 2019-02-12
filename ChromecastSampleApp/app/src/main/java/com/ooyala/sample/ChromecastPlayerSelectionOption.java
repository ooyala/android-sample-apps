package com.ooyala.sample;

public class ChromecastPlayerSelectionOption {
  public String title;
  public String embedCode;
  public String secondEmbedCode;
  public String pcode;
  public String domain;

  ChromecastPlayerSelectionOption(String title, String embedCode, String pcode, String domain) {
    this(title, embedCode, null, pcode, domain);
  }

  ChromecastPlayerSelectionOption(String title, String embedCode, String secondEmbedCode, String pcode, String domain) {
    super();
    this.title = title;
    this.embedCode = embedCode;
    this.secondEmbedCode = secondEmbedCode;
    this.pcode = pcode;
    this.domain = domain;
  }
}
