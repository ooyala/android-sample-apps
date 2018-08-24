package com.ooyala.sample.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Utils {
  /**
   * Get a JSON from the resources directory as a JSONObject
   *
   * @param resource File to fetch
   * @return the resource as JSONObject
   */
  public static JSONObject getResourceAsJsonObject(ClassLoader classLoader, String resource)  {
    InputStream is = classLoader.getResourceAsStream(resource);
    try {
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      return new JSONObject(new String(buffer, "UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }
}
