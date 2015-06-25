package com.ooyala.sample.utils;

import android.app.Activity;

/**
 * This is used to store information of a sample activity for use in a Map or List
 *
 */
public class PlayerSelectionOption {
  private String embedCode;
  private Class<? extends Activity> activity;
  public PlayerSelectionOption(String embedCode, Class<? extends Activity> activity) {
    this.embedCode = embedCode;
    this.activity = activity;
  }

  /**
   * Get the embed code for this sample
   * @return the embed code
   */
  public String getEmbedCode() {
    return this.embedCode;
  }

  /**
   * Get the activity to use for this sample
   * @return the activity to launch
   */
  public Class<? extends Activity> getActivity() {
    return this.activity;
  }
}
