package com.ooyala.android.plugin;

import com.ooyala.android.OoyalaPlayer.State;

public interface LifeCycleInterface {
  /**
   * This is called when plugin should be reset
   */
  public void reset();

  /**
   * This is called when plugin should be suspended
   */
  public void suspend();

  /**
   * This is called when plugin should be resumed
   */
  public void resume();

  /**
   * This is called when plugin should be resumed
   * 
   * @param timeInMillisecond the playhead time to set
   * @param stateToResume
   *          the player state after resume
   */
  public void resume(int timeInMillisecond, State stateToResume);

  /**
   * This is called when plugin should be destryed
   */
  public void destroy();

}
