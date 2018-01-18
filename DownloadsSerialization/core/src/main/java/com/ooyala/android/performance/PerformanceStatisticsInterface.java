package com.ooyala.android.performance;

public interface PerformanceStatisticsInterface {
  /**
   * @return descriptive name for reporting what the statistics are related to.
   */
  String getName();

  /**
   * @return a string reporting the statistics gathered so far.
   */
  String generateReport();
}
