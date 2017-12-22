package com.ooyala.android.item;

import com.ooyala.android.OoyalaAPIClient;
import com.ooyala.android.ads.ooyala.OoyalaAdSpot;
import com.ooyala.android.PlayerInfo;
import com.ooyala.android.ads.vast.Constants;
import com.ooyala.android.ads.vast.VASTAdSpot;
import com.ooyala.android.util.DebugMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class OoyalaManagedAdSpot extends AdSpot implements JSONUpdatableItem {
  private static final String TAG = OoyalaManagedAdSpot.class.getSimpleName();
  protected static final String KEY_TYPE = "type";  //AdSpot
  protected static final String KEY_TIME = "time";  //AdSpot
  protected static final String KEY_CLICK_URL = "click_url";
  protected static final String KEY_TRACKING_URL = "tracking_url";

  protected static final String AD_TYPE_OOYALA = "ooyala";
  protected static final String AD_TYPE_VAST = "vast";

  static final int DEFAULT_AD_TIME_SECONDS = 0;

  protected int _time = -1;
  protected int _priority = 0;
  protected URL _clickURL = null;
  protected List<URL> _trackingURLs = null;

  protected OoyalaManagedAdSpot() {
  }

  protected OoyalaManagedAdSpot(int time, URL clickURL, List<URL> trackingURLs) {
    _time = time;
    _clickURL = clickURL;
    _trackingURLs = trackingURLs;
  }

  protected OoyalaManagedAdSpot(JSONObject data) {
    update(data);
  }

  protected ReturnState update(JSONObject data) {
    if (data == null) { return ReturnState.STATE_FAIL; }

    try {
      if (!data.isNull(KEY_TIME)) {
        _time = data.getInt(KEY_TIME);
      } else if (_time < 0) {
        _time = DEFAULT_AD_TIME_SECONDS;
      }

      if (!data.isNull(KEY_CLICK_URL)) {
        try {
          _clickURL = new URL(data.getString(KEY_CLICK_URL));
        } catch (MalformedURLException exception) {
          DebugMode.logD(this.getClass().getName(),
              "Malformed Ad Click URL: " + data.getString(KEY_CLICK_URL));
          _clickURL = null;
        }
      }

      if (!data.isNull(KEY_TRACKING_URL)) {
        JSONArray pixels = data.getJSONArray(KEY_TRACKING_URL);
        _trackingURLs = new ArrayList<URL>(pixels.length());
        for (int i = 0; i < pixels.length(); i++) {
          try {
            _trackingURLs.add(new URL(pixels.getString(i)));
          } catch (MalformedURLException exception) {
            DebugMode.logD(this.getClass().getName(),
                "Malformed Ad Tracking URL: " + data.getString(KEY_TRACKING_URL));
          }
        }
      }
    } catch (JSONException exception) {
      DebugMode.logD(this.getClass().getName(), "JSONException: " + exception);
      return ReturnState.STATE_FAIL;
    }
    return ReturnState.STATE_MATCHED;
  }

  public static OoyalaManagedAdSpot create(JSONObject data, int contentDuration) {
    if (data == null || data.isNull(KEY_TYPE)) { return null; }
    String type = null;
    try {
      type = data.getString(KEY_TYPE);
    } catch (JSONException exception) {
      DebugMode.logD(OoyalaManagedAdSpot.class.getName(), "Ad create failed due to JSONException: " + exception);
      return null;
    }

    if (type == null) {
      return null;
    } else if (type.equals(AD_TYPE_OOYALA)) {
      return new OoyalaAdSpot(data);
    } else if (type.equals(AD_TYPE_VAST)) {
      try {
        data.put(Constants.KEY_DURATION, contentDuration);
      } catch (JSONException e) {
        DebugMode.logE(TAG, "unable to add duration to json", e);
      }
      return new VASTAdSpot(data);
    } else {
      DebugMode.logD(OoyalaManagedAdSpot.class.getName(), "Unknown ad type: " + type);
      return null;
    }
  }

  /**
   * Fetch the time at which this AdSpot should play.
   * @return The time at which this AdSpot should play in milliseconds.
   */
  public int getTime() {
    return _time;
  }

  /**
   * Fetch the URL to ping when this AdSpot is clicked.
   * @return the click URL
   */
  public URL getClickURL() {
    return _clickURL;
  }

  /**
   * Fetch the list of tracking URLs to ping when this AdSpot is played.
   * @return the priority
   */
  public List<URL> getTrackingURLs() {
    return _trackingURLs;
  }

  @Override
  public int getPriority() {
    return _priority;
  }

  /**
   * set the priority of the ad spot.
   * if two adspot have the same time, the one with higher priority(smaller value)
   * will be played first.
   */
  public void setPriority(int priority) {
    _priority = priority;
  }

  public abstract boolean fetchPlaybackInfo(OoyalaAPIClient api, PlayerInfo info);
}
