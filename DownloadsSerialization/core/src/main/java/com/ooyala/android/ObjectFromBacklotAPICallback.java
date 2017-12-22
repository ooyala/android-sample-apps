package com.ooyala.android;

import org.json.JSONObject;

public interface ObjectFromBacklotAPICallback {
  /**
   * This callback is used for objectFromBacklotAPI calls
   * @param obj the fetched object
   */
  public void callback(JSONObject obj);
}
