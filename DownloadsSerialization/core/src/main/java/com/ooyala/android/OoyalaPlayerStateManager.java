package com.ooyala.android;

import com.ooyala.android.OoyalaPlayer.DesiredState;
import com.ooyala.android.OoyalaPlayer.State;
import com.ooyala.android.OoyalaPlayer.InitPlayState;
import com.ooyala.android.player.PlayerInterfaceUtil;
import com.ooyala.android.util.DebugMode;

/**
 * The State Manager's purpose is to handle all manipulation and storage of OoyalaPlayer-related state.
 * Eventually this can be used as a place to perform state and transition validation
 */
class OoyalaPlayerStateManager {
  private static final String TAG = OoyalaPlayerStateManager.class.getSimpleName();

  private InitPlayState currentItemInitPlayState;
  private State state;
  private DesiredState desiredState;
  private OoyalaPlayer player;

  OoyalaPlayerStateManager(OoyalaPlayer player) {
    this.player = player;
    this.currentItemInitPlayState = InitPlayState.NONE;
    this.state = State.INIT;
    this.desiredState = DesiredState.DESIRED_PAUSE;
  }

  /**
   * Set the general Player State
   * @param state
   */
  void setState(State state) {
    if (state != this.state) {
      final State oldState = this.state;
      this.state = state;
      player.sendNotification(PlayerInterfaceUtil.buildSetStateNotification(oldState, this.state));
    }
  }

  /**
   * Set the intended playback state based on User/API interaction
   * For example - this state is always DESIRED_PAUSE unless Developer/User presses the Play button/API
   * @param desiredState
   */
  void setDesiredState(DesiredState desiredState) {
    if (desiredState != this.desiredState) {
      this.desiredState = desiredState;
      player.sendNotification(PlayerInterfaceUtil.buildSetDesiredStateNotification());
    }
    else {
      DebugMode.logV(TAG, "SetDesiredState: desired state is already " + desiredState);
    }
  }

  /**
   * Set the Initialization state of the player
   * Allows Ooyala to know when video playback finally first starts
   * @param initState
   */
  void setInitPlayState(InitPlayState initState) {
    this.currentItemInitPlayState = initState;
  }

  State getState() {
    return state;
  }

  DesiredState getDesiredState() {
    return desiredState;
  }

  InitPlayState getInitPlayState() {
    return currentItemInitPlayState;
  }

  /**
   * Reset the state of the Player.  Generally used when the video player is reinitialized
   */
  void resetState() {
    setState(State.INIT);
    setDesiredState(DesiredState.DESIRED_PAUSE);
    setInitPlayState(InitPlayState.NONE);
  }
}
