package com.ooyala.android.item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ooyala.android.util.DebugMode;

class ItemUtils {
  static final String SEPARATOR_TIME = ":";
  private static final String TAG = ItemUtils.class.getName();

  static Map<String, String> mapFromJSONObject(JSONObject obj) {
    Map<String, String> map = new HashMap<String, String>();

    if (obj == null) {
      return map;
    }

    Iterator<?> itr = obj.keys();
    while (itr.hasNext()) {
      String key = (String)itr.next();
      try {
        map.put(key, obj.getString(key));
      } catch (JSONException e) {
        //do nothing
      }
    }

    return map;
  }

  static boolean isNullOrEmpty(String string) {
    return string == null || string.equals("");
  }

  static double secondsFromTimeString(String time) {
    if (time == null) {
      DebugMode.assertFail(TAG, "secondsFromTimeString the string is null");
      return 0;
    }
    String[] hms = time.split(SEPARATOR_TIME);
    double timeMetric = 1.0;
    double milliseconds = 0.0;

    // set time metric
    switch (hms.length) {
    case 1:
      return bareMilliSecondsFromTimeString(time);
    case 2:
    case 3:
      // use default time metric
      break;
    case 4:
      // if four items are presented, set the last one is frame
      timeMetric = 1.0 / 30;
      break;
    default:
      DebugMode.assertFail(TAG, "invalid time format: " + time);
      return 0;
    }

    for (int i = hms.length - 1; i >= 0; i--) {
      if (hms[i].length() > 0) {
        try {
          milliseconds += Double.valueOf(hms[i]) * timeMetric;
        } catch (NumberFormatException e) {
          DebugMode.assertFail(TAG, e.toString());
          return 0;
        }
      }
      if (timeMetric < 1) {
        // reset metric from frame metric to default(second) matic
        timeMetric = 1.0;
      } else {
        timeMetric *= 60.0;
      }
    }
    return milliseconds;
  }

  private static double bareMilliSecondsFromTimeString(String time) {
    int dotCount = 0;
    int i = 0;
    for (; i < time.length(); ++i) {
      char c = time.charAt(i);
      if (c >= '0' && c <= '9') {
        continue;
      } else if (c == '.') {
        dotCount++;
        if (dotCount > 1 || i == 0) {
          DebugMode.assertFail(TAG,
              "bareMilliSecondsFromTimeString invalid format: " + time);
          return 0;
        }
      } else {
        break;
      }
    }

    if (time.charAt(i - 1) == '.') {
      DebugMode.assertFail(TAG,
          "bareMilliSecondsFromTimeString invalid format: " + time);
      return 0;
    }

    double value = Double.valueOf(time.substring(0, i));
    double timeMetric = 1.0;
    if (i < time.length()) {
      String metricString = time.substring(i);
      if (metricString.equals("h")) {
        timeMetric = 3600;
      } else if (metricString.equals("m")) {
        timeMetric = 60;
      } else if (metricString.equals("s")) {
        // default value, do nothing.
      } else if (metricString.equals("ms")) {
        timeMetric = 0.001;
      } else if (metricString.equals("f")) {
        timeMetric = 1.0 / 30;
      } else {
        DebugMode.assertFail(TAG,
            "invalid cc bare time string, unknown time metric: " + time);
        return 0;
      }
    }
    return value * timeMetric;
  }
}
