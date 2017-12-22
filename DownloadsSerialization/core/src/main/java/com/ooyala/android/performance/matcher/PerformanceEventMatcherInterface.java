package com.ooyala.android.performance.matcher;

import com.ooyala.android.OoyalaNotification;

public interface PerformanceEventMatcherInterface {
  /**
   * @return the notification name this matcher will successfully match against.
   * @see #matches(OoyalaNotification)
   */
  String getNotificationName();

  /**
   * @param notification non-null.
   * @return true if the given notification's name matches our registered notification name.
   * @see #getNotificationName()
   */
  boolean matches( OoyalaNotification notification );

  /**
   * @return a string identifying this matcher for use in pretty printing performnace reports.
   */
  String getReportName();
}
