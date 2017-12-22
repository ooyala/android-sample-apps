package com.ooyala.android;

import com.ooyala.android.util.DebugMode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

class OoyalaAPIHelper {
  private static final String TAG = OoyalaAPIHelper.class.getName();

  // Static context and cookies to be used for MoviePlayer instantiation for 4.0+
  public static Map<String, String> cookies = new HashMap<String, String>();

  public static JSONObject objectForAPI(String host, String uri, Map<String, String> params,
                                        int connectionTimeoutInMillisecond,
                                        int readTimeoutInMillisecond) {
    URL url = Utils.makeURL(host, uri, params);
    if (url == null) { return null; }
    return objectForAPI(url, connectionTimeoutInMillisecond, readTimeoutInMillisecond);
  }

  public static JSONObject objectForAPI(URL url,
                                        int connectionTimeoutInMillisecond,
                                        int readTimeoutInMillisecond) {
    return Utils.objectFromJSON(jsonForAPI(url,
            connectionTimeoutInMillisecond, readTimeoutInMillisecond));
  }

  private static String jsonForAPI(URL url,
                                   int connectionTimeoutInMillisecond,
                                   int readTimeoutInMillisecond) {
    DebugMode.logD(TAG, "Sending Request: " + url.toString());
    StringBuffer sb = new StringBuffer();
    BufferedReader rd = null;
    try {
      URLConnection conn = url.openConnection();
      conn.setConnectTimeout(connectionTimeoutInMillisecond);
      conn.setReadTimeout(readTimeoutInMillisecond);
      rd = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);
      String line;
      while ((line = rd.readLine()) != null) {
        sb.append(line);
      }

      String headerName = null;
      for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
        if (headerName.equals("Set-Cookie")) {
          String fullCookie = conn.getHeaderField(i);
          DebugMode.logD(OoyalaAPIHelper.class.getName(), "FOUND COOKIE: " + fullCookie);
          if (fullCookie.indexOf(";") > 0) {
            String cookie = fullCookie.substring(0, fullCookie.indexOf(";"));
            String cookieName = cookie.substring(0, cookie.indexOf("="));
            String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
            OoyalaAPIHelper.cookies.put(cookieName, cookieValue);
          }
        }
      }
    } catch (SocketTimeoutException e) {
      DebugMode.logE(TAG, "Connection to " + url.toString() + " timed out.");
    } catch (IOException e) {
      DebugMode.logE(TAG, "Caught!", e);
    } finally {
      if (rd != null) {
        try {
         rd.close();
        } catch (IOException e) {
          DebugMode.logE(TAG, "Caught!", e);
        }
      }
    }

    return sb.toString();
  }
}
