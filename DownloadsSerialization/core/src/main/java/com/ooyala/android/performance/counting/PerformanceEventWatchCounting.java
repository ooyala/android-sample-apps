package com.ooyala.android.performance.counting;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.performance.PerformanceEventWatchInterface;
import com.ooyala.android.performance.PerformanceStatisticsSnapshotBuilder;
import com.ooyala.android.performance.matcher.PerformanceEventMatcherInterface;
import java.util.Collections;
import java.util.Set;

final public class PerformanceEventWatchCounting
  implements PerformanceEventWatchInterface {

  private final PerformanceEventMatcherInterface matcher;
  private final Set<String> wantedNotificationNames;
  private final PerformanceCountingStatistics statistics;

  public PerformanceEventWatchCounting( final PerformanceEventMatcherInterface matcher ) {
    this.matcher = matcher;
    this.wantedNotificationNames = Collections.unmodifiableSet(Collections.singleton(matcher.getNotificationName()));
    this.statistics = new PerformanceCountingStatistics( matcher.getReportName() );
  }

  public PerformanceEventMatcherInterface getMatcher() {
    return matcher;
  }

  @Override
  public Set<String> getWantedNotificationNames() {
    return wantedNotificationNames;
  }

  @Override
  public void onNotification( final OoyalaNotification notification ) {
    if( matcher.matches( notification ) ) {
      statistics.mergeCount( 1 );
    }
  }

  @Override
  public void addStatisticsToBuilder( final PerformanceStatisticsSnapshotBuilder builder ) {
    builder.addCountingStatistics( this.statistics );
  }

}
