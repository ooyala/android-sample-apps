package com.ooyala.android.performance.startend;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.performance.PerformanceStatisticsSnapshotBuilder;
import com.ooyala.android.performance.matcher.PerformanceEventMatcherInterface;
import com.ooyala.android.performance.PerformanceEventWatchInterface;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final public class PerformanceEventWatchStartEnd
    implements PerformanceEventWatchInterface {

  private final Set<String> wantedNotificationNames;
  private final PerformanceEventMatcherInterface start;
  private final PerformanceEventMatcherInterface end;
  private final PerformanceStartEndStatistics statistics;
  private Long startMsec;

  public PerformanceEventWatchStartEnd( final PerformanceEventMatcherInterface start, final PerformanceEventMatcherInterface end ) {
    this.start = start;
    this.end = end;
    this.statistics = new PerformanceStartEndStatistics( String.format( "%s-to-%s", start.getReportName(), end.getReportName() ) );

    Set<String> names = new HashSet<String>();
    names.add( start.getNotificationName() );
    names.add( end.getNotificationName() );
    this.wantedNotificationNames = Collections.unmodifiableSet( names );
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append( "[" );
    b.append( getClass().getSimpleName() );
    b.append( " start:" );
    b.append(start.toString());
    b.append(" end:");
    b.append( end.toString() );
    return b.toString();
  }

  @Override
  public Set<String> getWantedNotificationNames() {
    return wantedNotificationNames;
  }

  @Override
  public void onNotification( final OoyalaNotification notification ) {
    if( end.matches( notification ) ) {
      onEnd();
    }
    if( start.matches( notification ) ) {
      onStart();
    }
  }

  private void onEnd() {
    if( startMsec != null ) {
      long now = System.currentTimeMillis();
      long diff = now - startMsec;
      statistics.mergeTimeInterval( diff );
      startMsec = null;
    }
  }

  private void onStart() {
    startMsec = System.currentTimeMillis();
  }

  @Override
  public void addStatisticsToBuilder( final PerformanceStatisticsSnapshotBuilder builder ) {
    builder.addStartEndStatistics( statistics );
  }
}
