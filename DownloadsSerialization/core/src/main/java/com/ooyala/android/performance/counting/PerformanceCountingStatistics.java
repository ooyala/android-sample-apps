package com.ooyala.android.performance.counting;

import com.ooyala.android.performance.PerformanceStatisticsInterface;
import com.ooyala.android.util.DebugMode;

public class PerformanceCountingStatistics
    implements PerformanceStatisticsInterface {

    private static final String TAG = PerformanceCountingStatistics.class.getSimpleName();
    private int count;
    private final String name;

    public PerformanceCountingStatistics( final String name ) {
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
        b.append( " count:" );
        b.append( count );
        return b.toString();
    }

    public void mergeCount( int count ) {
        DebugMode.assertCondition(count >= 0, TAG, "expected count >= 0, got " + count);
        this.count += count;
    }

}
