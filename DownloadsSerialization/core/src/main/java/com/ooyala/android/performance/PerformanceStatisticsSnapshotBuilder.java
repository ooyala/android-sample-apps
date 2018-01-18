package com.ooyala.android.performance;

import com.ooyala.android.performance.counting.PerformanceCountingStatistics;
import com.ooyala.android.performance.startend.PerformanceStartEndStatistics;
import java.util.HashSet;
import java.util.Set;

final public class PerformanceStatisticsSnapshotBuilder {

  private final Set<PerformanceCountingStatistics> countingStatistics;
  private final Set<PerformanceStartEndStatistics> startEndStatistics;

  public PerformanceStatisticsSnapshotBuilder() {
    this.countingStatistics = new HashSet<PerformanceCountingStatistics>();
    this.startEndStatistics = new HashSet<PerformanceStartEndStatistics>();
  }

  public void addCountingStatistics( final PerformanceCountingStatistics statistics ) {
    countingStatistics.add( statistics );
  }

  public void addStartEndStatistics( final PerformanceStartEndStatistics statistics ) {
    startEndStatistics.add( statistics );
  }

  public PerformanceStatisticsSnapshot build() {
    return new PerformanceStatisticsSnapshot( countingStatistics, startEndStatistics );
  }

}
