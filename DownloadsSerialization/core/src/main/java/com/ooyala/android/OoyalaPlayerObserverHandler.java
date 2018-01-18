package com.ooyala.android;

import com.ooyala.android.player.PlayerInterface;
import com.ooyala.android.util.DebugMode;
import com.ooyala.android.OoyalaPlayer.State;
import com.ooyala.android.OoyalaPlayer.InitPlayState;

import java.util.Observable;

/**
 * A helper class that handles all observation code in our player
 *
 * This class is not the actual observer (It could be in the future), but at least all of the
 * observer code is represented outside the OoyalaPlayer
 */
class OoyalaPlayerObserverHandler {
  private static final String TAG = OoyalaPlayerObserverHandler.class.getSimpleName();

  OoyalaPlayer ooyalaPlayer;

  OoyalaPlayerObserverHandler(OoyalaPlayer player) {
    this.ooyalaPlayer = player;
  }

  /**
   * This class is not an actual observer,
   */
  void handleObserverUpdate(Observable arg0, Object arg1) {

    // Only listen to Ooyala certified notifications from our players
    if (arg0 instanceof PlayerInterface && arg1 instanceof OoyalaNotification) {
      processContentNotifications((PlayerInterface) arg0, (OoyalaNotification) arg1);
    }
  }

  /**
   * For Internal Use Only. Process content player notification.
   *
   * @param player
   *          the notification sender
   * @param notification
   *          the notification
   */
  private void processContentNotifications(PlayerInterface player, OoyalaNotification notification) {
    if (ooyalaPlayer.currentPlayer() != player) {
      DebugMode.logE(TAG, "Notification received from a player that is not expected.  Will continue: " + notification);
    }

    final String name = notification.getName();
    if (name.equals(OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME)) {
      //Check if any ads need to play now
      ooyalaPlayer.contextSwitcher.processAdModes(AdPluginManagerInterface.AdMode.Playhead, player.currentTime());

      //Update closed captions if necessary
      ooyalaPlayer.sendClosedCaptionsNotification();
      ooyalaPlayer.sendNotification(OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME);

    } else if (name.equals(OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME)) {
      OoyalaPlayer.State state = player.getState();
      DebugMode.logD(TAG, "content player state change to " + state);
      switch (state) {
        case COMPLETED:
          DebugMode.logE(TAG, "Content finished! Should check for post-roll");
          ooyalaPlayer.contextSwitcher.processAdModes(AdPluginManagerInterface.AdMode.ContentFinished, 0);
          break;

        case ERROR:
          ooyalaPlayer.onError(player.getError(), "Error received from content.  Cleaning up everything");
          int errorCode = player.getError() == null ? 0 : player.getError().getIntErrorCode();
          ooyalaPlayer.contextSwitcher.processAdModes(AdPluginManagerInterface.AdMode.ContentError, player.getError() == null ? 0 : errorCode);
          break;
        case PLAYING:

          //Remember that play has started, and send first notification
          if (ooyalaPlayer.stateManager.getInitPlayState() != InitPlayState.ContentPlayed) {
            ooyalaPlayer.stateManager.setInitPlayState(InitPlayState.ContentPlayed);
            ooyalaPlayer.sendNotification(OoyalaPlayer.PLAY_STARTED_NOTIFICATION_NAME);
          }

          //TODO: Promo image should be removed from OoyalaPlayer over time
          ooyalaPlayer.hidePromoImage();

          ooyalaPlayer.stateManager.setState(OoyalaPlayer.State.PLAYING);
          break;
        case READY:
          ooyalaPlayer.stateManager.setState(State.READY);
          ooyalaPlayer.playIfDesired();
          break;
        case INIT:
        case LOADING:
        case PAUSED:
        default:
          ooyalaPlayer.stateManager.setState(player.getState());
          break;
      }
    } else {

      //Bubble up all other notifications to all listeners
      ooyalaPlayer.sendNotification(notification);
    }
  }

}
