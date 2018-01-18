package com.ooyala.android.performance;

import com.ooyala.android.OoyalaNotification;
import java.util.Set;

public interface PerformanceEventWatchInterface {
  Set<String> getWantedNotificationNames();
  void onNotification( OoyalaNotification notification );
  void addStatisticsToBuilder( PerformanceStatisticsSnapshotBuilder builder );
}
