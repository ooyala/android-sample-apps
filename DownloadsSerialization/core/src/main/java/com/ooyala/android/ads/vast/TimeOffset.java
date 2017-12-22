package com.ooyala.android.ads.vast;

import com.ooyala.android.util.DebugMode;

/**
 * This class is used to hold a VAST 3.0 time offset value.
 * Supported format:
 * 1. Seconds in HH:MM:SS.MMM, e.g. 01:30:20.250
 * 2. Percentage end up with a %, e.g. 20%
 * 3. Predefined position starting with #, e.g. #1
 */
public class TimeOffset {
  // The number is ooyala defined, in seconds, after converting to milliseconds it should not
  // exceeds max integer.
  public static int MAX_OFFSET = Integer.MAX_VALUE / 1024;
  private static final String TAG = TimeOffset.class.getName();
  public enum Type{
    Seconds,
    Percentage,
    Position
  };

  private final Type type;
  private final double value;

  private TimeOffset(Type type, double value) {
    this.type = type;
    this.value = value;
  }

  /**
   * @return the time offset type
   */
  public Type getType() {
      return type;
    }

  /**
   * @return the percentage time offset, negative if this is not a percentage time offset.
   */
  public double getPercentage() {
    return type == Type.Percentage ? value : -1.0;
  }

  /**
   * @return the second time offset, negative if this is not a second time offset.
   */
  public double getSeconds() {
    return type == Type.Seconds ? value : -1.0;
  }

  /**
   * @return the position time offset, negative if this is not a position time offset.
   */
  public int getPosition() {
    return type == Type.Position ? (int)value : -1;
  }

  /**
   * Parse a time offset string to a time offset object.
   * @param offsetString the offset string.
   * @return the time offset object, null if the string is not formatted properly.
   */
  public static TimeOffset parseOffset(String offsetString) {
    if (offsetString == null) {
      return null;
    }

    if (offsetString.equals("start")) {
      return new TimeOffset(Type.Seconds, 0);
    }

    if (offsetString.equals("end")) {
      return new TimeOffset(Type.Seconds, MAX_OFFSET);
    }

    int percentageIndex = offsetString.indexOf('%');
    double value = -1;
    if (percentageIndex > 0) {
      // Parse percentage string
      try {
        value = Double.parseDouble(offsetString.substring(0, percentageIndex)) / 100.0;
        if (value > 1) {
          value = 1;
        } else if (value < 0) {
          value = 0;
        }
        return new TimeOffset(Type.Percentage, value);
      } catch (NumberFormatException e) {
        DebugMode.logE(TAG, "Invalid time offset:" + offsetString);
        return null;
      }
    } else {
      value = VASTUtils.secondsFromTimeString(offsetString, -1);
      if (value >= 0) {
        return new TimeOffset(Type.Seconds, value);
      } else if (offsetString.charAt(0) == '#') {
        try {
          int position = Integer.parseInt(offsetString.substring(1));
          return new TimeOffset(Type.Position, position);
        } catch (NumberFormatException ex) {
          return null;
        }
      }
    }
    return null;
  }
}
