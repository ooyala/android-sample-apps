package com.ooyala.android.performance.startend;

import com.ooyala.android.performance.PerformanceStatisticsInterface;
import com.ooyala.android.util.DebugMode;

final public class PerformanceStartEndStatistics
  implements PerformanceStatisticsInterface {

  private static final String TAG = PerformanceStartEndStatistics.class.getSimpleName();
  private long totalMsec;
  private long smallestMsec;
  private long biggestMsec;
  private int count;
  private final String name;

  public PerformanceStartEndStatistics( final String name ) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return generateReport();
  }

  @Override
  public String generateReport() {
    final StringBuilder b = new StringBuilder();
    b.append( "[" );
    b.append( this.getClass().getSimpleName() );
    b.append( " " );
    b.append( getName() );
    b.append( " totalMsec:" );
    b.append( totalMsec );
    b.append( " smallestMsec:");
    b.append( smallestMsec );
    b.append( " biggestMsec:" );
    b.append( biggestMsec );
    b.append( " averageMsec:" );
    b.append( count == 0 ? 0 : totalMsec / (double)count );
    b.append( " count:" );
    b.append( count );
    return b.toString();
  }

  public void mergeTimeInterval( final long msec ) {
    DebugMode.assertCondition( msec >= 0, TAG, "expected msec >= 0, got " + msec );
    totalMsec += msec;
    count++;
    smallestMsec = count == 1 ? msec : Math.min( smallestMsec, msec );
    biggestMsec = count == 1 ? msec : Math.max( biggestMsec, msec );
  }

}
