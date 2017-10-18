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
  private boolean hasIMA;

  public PlayerSelectionOption(String embedCode, String pcode, String domain, Class<? extends Activity> activity, boolean hasIMA) {
    this.embedCode = embedCode;
    this.activity = activity;
    this.pcode = pcode;
    this.domain = domain;
    this.hasIMA = hasIMA;
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
   * Get information about Ima adverb
   * @return the embed code
   */
  public boolean hasIMA() {
    return hasIMA;
  }

  /**
   * Get the activity to use for this sample
   * @return the activity to launch
   */
  public Class <? extends Activity> getActivity() {
    return this.activity;
  }
}
