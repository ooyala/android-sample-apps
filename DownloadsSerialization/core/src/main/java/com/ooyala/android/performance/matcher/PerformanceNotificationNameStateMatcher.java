package com.ooyala.android.performance.matcher;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import java.util.Map;

final public class PerformanceNotificationNameStateMatcher
    implements PerformanceEventMatcherInterface {

  private final String name;
  private final OoyalaPlayer.State state;

  /**
   * @param name non-null.
   */
  public PerformanceNotificationNameStateMatcher( final String name, final OoyalaPlayer.State state ) {
    this.name = name;
    this.state = state;
  }

  @Override
  public String getNotificationName() {
    return name;
  }

  @Override
  public boolean matches( final OoyalaNotification notification ) {
    boolean nameMatches = false;
    boolean stateMatches = false;
    if (notification != null) {
      if (notification.getName() != null && name != null) {
        nameMatches = notification.getName().equals(name);
      }
      if (notification.getData() instanceof Map) {
        final OoyalaPlayer.State newState = ((Map<String, OoyalaPlayer.State>) notification.getData()).get(OoyalaNotification.NEW_STATE_KEY);
        if( newState != null && state != null ) {
          stateMatches = newState.equals(state);
        }
      }
    }
    return nameMatches && stateMatches;
  }

  @Override
  public String getReportName() {
    final StringBuilder b = new StringBuilder();
    b.append( "[" );
    b.append( getClass().getSimpleName() );
    b.append( " name:" );
    b.append( name );
    b.append( " state:" );
    b.append( state );
    b.append( "]" );
    return b.toString();
  }

}
