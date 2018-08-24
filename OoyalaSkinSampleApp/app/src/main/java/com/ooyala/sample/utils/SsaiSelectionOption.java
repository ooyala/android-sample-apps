package com.ooyala.sample.utils;

import android.app.Activity;

/**
 * This is used to store information of a Ssai sample activity for use in a Map or List
 *
 */
public class SsaiSelectionOption extends PlayerSelectionOption {
  private String playerParams;

  public SsaiSelectionOption(String embedCode, String pcode, String domain, Class<? extends Activity> activity) {
    this(embedCode, pcode, domain, activity, "");
  }

  public SsaiSelectionOption(String embedCode, String pcode, String domain, Class<? extends Activity> activity, String playerParams) {
    super(embedCode, pcode, domain, activity);
    this.playerParams = playerParams;
  }

  public String getPlayerParams() {
    return playerParams;
  }

  public void setPlayerParams(String playerParams) {
    this.playerParams = playerParams;
  }
}
