package com.ooyala.android.performance;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.performance.counting.PerformanceEventWatchCounting;
import com.ooyala.android.performance.matcher.PerformanceNotificationNameMatcher;
import com.ooyala.android.performance.matcher.PerformanceNotificationNameStateMatcher;
import com.ooyala.android.performance.startend.PerformanceEventWatchStartEnd;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

final public class PerformanceMonitorBuilder {

  private final Observable observable;
  private final Set<PerformanceEventWatchInterface> eventWatches;

  public PerformanceMonitorBuilder( final Observable observable ) {
    this.observable = observable;
    this.eventWatches = new HashSet<PerformanceEventWatchInterface>();
  }

  public static PerformanceMonitorBuilder getStandardMonitorBuilder( final Observable observable ) {
    final PerformanceMonitorBuilder b = new PerformanceMonitorBuilder( observable );
    /* PLAYER READY FOR THE FIRST TIME */
    b.addEventWatch(
            new PerformanceEventWatchStartEnd(
                    new PerformanceNotificationNameMatcher( OoyalaPlayer.EMBED_CODE_SET_NOTIFICATION_NAME ),
                    new PerformanceNotificationNameStateMatcher( OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME, OoyalaPlayer.State.READY )
            )
    );
    /* SET EMBED CODE */
    b.addEventWatch(
            new PerformanceEventWatchCounting(
                    new PerformanceNotificationNameMatcher( OoyalaPlayer.EMBED_CODE_SET_NOTIFICATION_NAME )
            )
    );
    /* BUFFERING EVENTS */
    b.addEventWatch(
            new PerformanceEventWatchStartEnd(
                    new PerformanceNotificationNameMatcher(OoyalaPlayer.BUFFERING_STARTED_NOTIFICATION_NAME),
                    new PerformanceNotificationNameMatcher(OoyalaPlayer.BUFFERING_COMPLETED_NOTIFICATION_NAME)
            )
    );
    /* SEEK COUNT */
    b.addEventWatch(
            new PerformanceEventWatchStartEnd(
                    new PerformanceNotificationNameMatcher(OoyalaPlayer.SEEK_COMPLETED_NOTIFICATION_NAME),
                    new PerformanceNotificationNameMatcher(OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME)
            )
    );
    return b;
  }

  /**
   * @return OOPerformanceMonitor configured with standard monitoring.
   */
  public static PerformanceMonitor getStandardMonitor( final Observable observable ) {
    return getStandardMonitorBuilder(observable).build();
  }

  /**
   * @return OOPerformanceMonitor configured with standard monitoring extended with ADS events watchers.
   */
  public static PerformanceMonitor getStandardAdsMonitor(final Observable observable) {
    PerformanceMonitorBuilder b = getStandardMonitorBuilder(observable);

    /* CONTENT -> AD */
    b.addEventWatch(
            new PerformanceEventWatchStartEnd(
                    new PerformanceNotificationNameStateMatcher(OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME, OoyalaPlayer.State.PLAYING),
                    new PerformanceNotificationNameMatcher(OoyalaPlayer.AD_STARTED_NOTIFICATION_NAME)
            )
    );
    /* AD -> CONTENT */
    b.addEventWatch(
            new PerformanceEventWatchStartEnd(
                    new PerformanceNotificationNameMatcher(OoyalaPlayer.AD_COMPLETED_NOTIFICATION_NAME),
                    new PerformanceNotificationNameStateMatcher(OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME, OoyalaPlayer.State.PLAYING)
            )
    );
    return b.build();
  }

  public void addEventWatch( final PerformanceEventWatchInterface eventWatch ) {
    eventWatches.add( eventWatch );
  }

  public PerformanceMonitor build() {
    return new PerformanceMonitor( eventWatches, observable );
  }
}
