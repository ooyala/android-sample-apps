package com.ooyala.android.performance.matcher;

import com.ooyala.android.OoyalaNotification;

final public class PerformanceNotificationNameMatcher
implements PerformanceEventMatcherInterface {

  private final String name;

  /**
   * @param name non-null.
   */
  public PerformanceNotificationNameMatcher( final String name ) {
    this.name = name;
  }

  @Override
  public String getNotificationName() {
    return name;
  }

  @Override
  public boolean matches( final OoyalaNotification notification ) {
    boolean matches = false;
    if( notification != null && notification.getName() != null && name != null ) {
      matches = notification.getName().equals( name );
    }
    return matches;
  }

  @Override
  public String getReportName() {
    final StringBuilder b = new StringBuilder();
    b.append( "[" );
    b.append( getClass().getSimpleName() );
    b.append( " name:" );
    b.append( name );
    b.append( "]" );
    return b.toString();
  }

}
