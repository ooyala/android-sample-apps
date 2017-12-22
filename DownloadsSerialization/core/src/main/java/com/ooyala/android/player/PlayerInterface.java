package com.ooyala.android.player;

import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaPlayer.State;

/**
 * The interface that must be implemented in order to receive control events
 * from Ooyala UI
 *
 */
public interface PlayerInterface {
  /**
   * This is called when pause is clicked
   */
  public void pause();

  /**
   * This is called when play is clicked
   */
  public void play();

  /**
   * This is called when stop is clicked
   */
  public void stop();

  /**
   * @return current time
   */
  public int currentTime();

  /**
   * @return duration.
   */
  public int duration();

  /**
   * @return the buffer percentage (between 0 and 100 inclusive)
   */
  public int buffer();

  /**
   * @return true if the current player is seekable, false if there is no
   *         current player or it is not seekable
   */
  public boolean seekable();

  /**
   * Move the playhead to a new location in seconds with millisecond accuracy
   * 
   * @param timeInMillis
   *          time in milliseconds
   */
  public void seekToTime(int timeInMillis);

  /**
   * This returns the player state
   * 
   * @return the state
   */
  public State getState();
  
  /**
   * @return the percentage of the cursor should be on scrubber for live stream playback
   */
  public int livePlayheadPercentage();
  
  /**
   * Seek to the given percent position of scrubber for live stream playback
   * @param percent The percent of scrubber the cursor ends after seek 
   */
  public void seekToPercentLive(int percent);

  /**
   * @return true if the player can support live closed captions
   */
  public boolean isLiveClosedCaptionsAvailable();

  /**
   * Set the displayed closed captions language.
   *
   * @param language
   *          2 letter country code of the language to display or nil to hide
   *          closed captions
   */
  public void setClosedCaptionsLanguage(String language);

  public OoyalaException getError();

  /**
   * Set the volume for the player.
   * */
  public void setVolume(float volume);

}
