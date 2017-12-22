package com.ooyala.android.player;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.util.DebugMode;

import java.util.HashMap;
import java.util.Map;

import static com.ooyala.android.OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME;
import static com.ooyala.android.OoyalaPlayer.DESIRED_STATE_CHANGED_NOTIFICATION_NAME;
import static com.ooyala.android.OoyalaPlayer.State;

import static com.ooyala.android.OoyalaPlayer.DesiredState;
/**
 * Utilities to collect common code around how notifications are generated.
 */
public class PlayerInterfaceUtil {

  private static final String TAG = PlayerInterfaceUtil.class.getSimpleName();

  /**
   * Build the OoyalaNotification to send out when state changes.
   * @param oldState the previous state e.g. PLAYING.
   * @param newState the new state e.g. PAUSED.
   * @return OoyalaNotification named STATE_CHANGED_NOTIFICATION_NAME, and data that is Map&lt;String,State&gt; with keys OLD_STATE_KEY and NEW_STATE_KEY.
   */
  public static final OoyalaNotification buildSetStateNotification(OoyalaPlayer.State oldState, OoyalaPlayer.State newState) {
    DebugMode.logD(TAG, "player set state " + newState + ", old state was " + oldState);
    final Map<String,State> data = new HashMap<String,OoyalaPlayer.State>();
    data.put(OoyalaNotification.OLD_STATE_KEY, oldState);
    data.put(OoyalaNotification.NEW_STATE_KEY, newState);
    return new OoyalaNotification(
        STATE_CHANGED_NOTIFICATION_NAME,
        data
    );
  }

  public static final OoyalaNotification buildSetDesiredStateNotification() {
    return new OoyalaNotification(
            DESIRED_STATE_CHANGED_NOTIFICATION_NAME,
            null
    );
  }
}
