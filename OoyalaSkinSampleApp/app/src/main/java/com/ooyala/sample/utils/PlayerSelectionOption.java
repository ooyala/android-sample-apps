package com.ooyala.sample.utils;

import android.app.Activity;

/**
 * This is used to store information of a sample activity for use in a Map or List
 *
 */
public class PlayerSelectionOption {
  private String embedCode;
  private Class <? extends Activity> activity;
  private String pcode;
  private String domain;
  private String markersFileName = "";

  public PlayerSelectionOption(String embedCode, String pcode, String domain, Class<? extends Activity> activity) {
    this.embedCode = embedCode;
    this.activity = activity;
    this.pcode = pcode;
    this.domain = domain;
  }

  public PlayerSelectionOption(String embedCode, String pcode, String domain, String markers, Class<? extends Activity> activity) {
    this.embedCode = embedCode;
    this.activity = activity;
    this.pcode = pcode;
    this.domain = domain;
    this.markersFileName = markers;
  }

  /**
   * Get the pcode for this sample
   * @return the pcode
   */
  public String getPcode() {
    return pcode;
  }

  /**
   * Get the domain for this sample
   * @return the domain
   */
  public String getDomain() {
    return domain;
  }

  /**
   * Get the embed code for this sample
   * @return the embed code
   */
  public String getEmbedCode() {
    return this.embedCode;
  }

  /**
   * Get the chapter markers file name for this sample
   * @return chapter markers file name
   */
  public String getMarkersFileName() {
    return this.markersFileName;
  }

  /**
   * Get the activity to use for this sample
   * @return the activity to launch
   */
  public Class <? extends Activity> getActivity() {
    return this.activity;
  }
}
