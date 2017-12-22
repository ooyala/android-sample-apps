package com.ooyala.android.performance;

import com.ooyala.android.performance.counting.PerformanceCountingStatistics;
import com.ooyala.android.performance.startend.PerformanceStartEndStatistics;
import java.util.Set;

final public class PerformanceStatisticsSnapshot {

  public final Set<PerformanceCountingStatistics> countingStatistics;
  public final Set<PerformanceStartEndStatistics> startEndStatistics;

  public PerformanceStatisticsSnapshot( final Set<PerformanceCountingStatistics> countingStatistics, final Set<PerformanceStartEndStatistics> startEndStatistics ) {
    this.countingStatistics = countingStatistics;
    this.startEndStatistics = startEndStatistics;
  }

  /**
   * @return a string reporting the statistics gathered so far.
   */
  public String generateReport() {
    final StringBuilder b = new StringBuilder();
    b.append( getClass().getSimpleName() );
    b.append( " Report:" );
    for( PerformanceCountingStatistics s : countingStatistics ) {
      b.append( s.generateReport() );
      b.append( System.getProperty( "line.separator" ) );
    }
    for( PerformanceStartEndStatistics s : startEndStatistics ) {
      b.append( s.generateReport() );
      b.append( System.getProperty( "line.separator" ) );
    }
    return b.toString();
  }
}
