package com.ooyala.android.util;

import android.util.Log;

/**
 * A tool that provides two new features: (1) Hiding all Ooyala debug logs, and (2) enforcing a "strict mode"
 * to force runtime exceptions on assertions throughout the Ooyala SDK.
 *
 * You can set your desired mode by calling DebugMode.setMode(DebugMode.mode);
 *
 */
public final class DebugMode {

  /**
   * The logging mode that you'd like for the Ooyala SDK
   *
   * None will remove all logs that come from the Ooyala SDK code.  Note, that this does not hide logs from
   * third parties such as the Android MediaPlayer class.
   *
   * LogOnly (default) will log all messages from the SDK, and when assertions fail, the SDK will try to
   * continue with video playback
   *
   * LogAndAbort will log all messages from the SDK, as well as exit the application when assertions fail
   */
  public enum Mode {
    None,
    LogOnly,
    LogAndAbort,
  }

  private DebugMode() {}

  private static Mode mode = Mode.LogOnly;

  /**
   * Gets the debug mode that was set for the SDK.
   * @return the currently active DebugMode
   */
  public static Mode getMode() {
    return DebugMode.mode;
  }

  /**
   * Set the DebugMode that the SDK will execute with.  Consult DebugMode.Mode for the different options
   * @param mode the logging mode for the Ooyala SDK
   */
  public static void setMode( Mode mode ) {
    DebugMode.mode = mode;
  }

  /**
   * a DebugMode wrapper to Log.i
   * @param tag a Log tag
   * @param message the Log message
   */
  public static void logI( String tag, String message ) {
    if( DebugMode.mode != Mode.None ) { Log.i( tag, message ); }
  }
  /**
   * a DebugMode wrapper to Log.i
   * @param tag a Log tag
   * @param message the Log message
   * @param throwable a throwable to connect with the message
   */
  public static void logI( String tag, String message, Throwable throwable ) {
    if( DebugMode.mode != Mode.None ) { Log.i( tag, message, throwable ); }
  }

  /**
   * a DebugMode wrapper to Log.w
   * @param tag a Log tag
   * @param message the Log message
   */
  public static void logW( String tag, String message ) {
    if( DebugMode.mode != Mode.None ) { Log.w( tag, message ); }
  }
  /**
   * a DebugMode wrapper to Log.w
   * @param tag a Log tag
   * @param message the Log message
   * @param throwable a throwable to connect with the message
   */
  public static void logW( String tag, String message, Throwable throwable ) {
    if( DebugMode.mode != Mode.None ) { Log.w( tag, message, throwable ); }
  }

  /**
   * a DebugMode wrapper to Log.d
   * @param tag a Log tag
   * @param message the Log message
   */
  public static void logD( String tag, String message ) {
    if( DebugMode.mode != Mode.None ) { Log.d( tag, message ); }
  }
  /**
   * a DebugMode wrapper to Log.d
   * @param tag a Log tag
   * @param message the Log message
   * @param throwable a throwable to connect with the message
   */
  public static void logD( String tag, String message, Throwable throwable ) {
    if( DebugMode.mode != Mode.None ) { Log.d( tag, message, throwable ); }
  }

  /**
   * a DebugMode wrapper to Log.v
   * @param tag a Log tag
   * @param message the Log message
   */
  public static void logV( String tag, String message ) {
    if( DebugMode.mode != Mode.None ) { Log.v( tag, message ); }
  }
  /**
   * a DebugMode wrapper to Log.v
   * @param tag a Log tag
   * @param message the Log message
   * @param throwable a throwable to connect with the message
   */
  public static void logV( String tag, String message, Throwable throwable ) {
    if( DebugMode.mode != Mode.None ) { Log.v( tag, message, throwable ); }
  }

  public static void logE( String tag, String message ) {
    if( DebugMode.mode != Mode.None ) { Log.e( tag, message ); }
  }
  /**
   * a DebugMode wrapper to Log.d
   * @param tag a Log tag
   * @param message the Log message
   * @param throwable a throwable to connect with the message
   */
  public static void logE( String tag, String message, Throwable throwable ) {
    if( DebugMode.mode != Mode.None ) { Log.e( tag, message, throwable ); }
  }

  public static boolean assertEquals( Object a, Object b, String tag, String message ) {
    boolean equals = false;
    if( a != null || b != null ) {
      Object left = a == null ? b : a;
      Object right = a == null ? a : b;
      String preMessage = "(" + left + "?=" + right + ") ";
      equals = left.equals(right);
      DebugMode.assertCondition( equals, tag, preMessage+message );
    }
    return equals;
  }

  /**
   * Assert that the condition returns true.  If not, perform an action based on the DebugMode.Mode
   * @param condition The boolean condition to assert on
   * @param tag the Log tag
   * @param message the Log message
   */
  public static boolean assertCondition( boolean condition, String tag, String message ) {
    if (!condition) {
      switch( getMode() ) {
      case None:
        break;
      case LogOnly:
        Log.e( tag, message );
        break;
      case LogAndAbort:
        Log.e( tag, message );
        System.exit( -1 );
        break;
      }
    }
    return condition;
  }

  /**
   * Used when the SDK comes across an error state.  Equivalent to assertCondition(false, tag, message)
   * @param tag the Log tag
   * @param message the Log message
   */
  public static void assertFail( String tag, String message ) {
    DebugMode.assertCondition( false, tag, message );
  }
}
