package com.ooyala.android;

import com.ooyala.android.util.DebugMode;

/**
 * All of the OoyalaPlayer APIs that interact directly with the Playback of ads or content.  Get or
 * set information about the actual video playback (play, pause, closed captions, volume, etc.).
 *
 * Generally this will be most interactions with _player or currentPlayer()
 * This also maintains any state that changes due to UI interaction that relates directly to video playback
 */

class OoyalaPlayerPlaybackInteractions {
  private static final String TAG = OoyalaPlayerPlaybackInteractions.class.getName();
  OoyalaPlayer ooyalaPlayer;
  
  private float volume = 1f;
  int queuedSeekTime;  // package private - needs to be cleared when going to cast
  private String closedCaptionLanguage;
  private boolean allowedToSeek = true;

  private int percentToMillis(int percent) {
    float fMillis = ((percent) / (100f)) * (ooyalaPlayer.getDuration());
    return (int) fMillis;
  }

  private int millisToPercent(int millis) {
    float fPercent = (((float) millis) / ((float) this.ooyalaPlayer.getDuration())) * (100f);
    return (int) fPercent;
  }

  OoyalaPlayerPlaybackInteractions(OoyalaPlayer player) {
    this.ooyalaPlayer = player;
  }

  /**
   * When we are about to start another video, reset everything to original state
   */
  void resetState() {
    queuedSeekTime = 0;
  }

  /**
   * Pause the current video
   */
  void pause() {
    this.ooyalaPlayer.stateManager.setDesiredState(OoyalaPlayer.DesiredState.DESIRED_PAUSE);
    if (this.ooyalaPlayer.currentPlayer() != null && this.ooyalaPlayer.showingAdWithHiddenControlls() == false) {
      this.ooyalaPlayer.currentPlayer().pause();
    }
  }

  void play() {
    this.ooyalaPlayer.stateManager.setDesiredState(OoyalaPlayer.DesiredState.DESIRED_PLAY);

    //If there is a player, and it is playable
    if (this.ooyalaPlayer.currentPlayer() != null) {
      OoyalaPlayer.State currentPlayerState = this.ooyalaPlayer.currentPlayer().getState();
      if (this.ooyalaPlayer.isPlayable(currentPlayerState)) {
        //Seek if necessary
        if (!this.ooyalaPlayer.isShowingAd() && queuedSeekTime > 0) {
          this.ooyalaPlayer.seek(queuedSeekTime);
          queuedSeekTime = 0;
        }

        if (!this.ooyalaPlayer.needPlayAdsOnInitialContentPlay()) {
          this.ooyalaPlayer.currentPlayer().play();
        }
      } else if (this.ooyalaPlayer.stateManager.getState() == OoyalaPlayer.State.COMPLETED) {
        // handle replay
        if (currentPlayerState == OoyalaPlayer.State.SUSPENDED && this.ooyalaPlayer.currentPlayer() == this.ooyalaPlayer.contextSwitcher.contentPlayer) {
          this.ooyalaPlayer.contextSwitcher.contentPlayer.resume(0, OoyalaPlayer.State.PLAYING);
        } else {
          this.ooyalaPlayer.currentPlayer().seekToTime(0);
          this.ooyalaPlayer.currentPlayer().play();
        }
      }
    } else if (this.ooyalaPlayer.contextSwitcher.contentPlayer == null && this.ooyalaPlayer.stateManager.getState() == OoyalaPlayer.State.READY) {
      if (!this.ooyalaPlayer.needPlayAdsOnInitialContentPlay()) {
        this.ooyalaPlayer.prepareContent(false);
      }
    } else {
      this.ooyalaPlayer.restart();
    }
  }

  void seek(int timeInMillis) {
    DebugMode.logV(TAG, "seek()...: msec=" + timeInMillis);
    if (this.ooyalaPlayer.seekable() && this.ooyalaPlayer.currentPlayer() != null) {
      this.ooyalaPlayer.currentPlayer().seekToTime(timeInMillis);
      queuedSeekTime = 0;
    } else {
      queuedSeekTime = timeInMillis;
    }
    DebugMode.logV(TAG, "...seek(): _queuedSeekTime=" + queuedSeekTime);
  }


  /**
   * Seek to the given percentage
   *
   * @param percent
   *          percent (between 0 and 100) to seek to
   */
  void seekToPercent(int percent) {
    if (percent < 0 || percent > 100) {
      return;
    }

    if (this.ooyalaPlayer.getCurrentItem() == null) {
      DebugMode.logI(TAG, "Trying to seekToPercent without a currentItem");
      return;
    }

    DebugMode.logV(TAG, "seekToPercent()...: percent=" + percent);
    if (this.ooyalaPlayer.seekable()) {
      if (this.ooyalaPlayer.getCurrentItem().isLive()) {
        this.ooyalaPlayer.currentPlayer().seekToPercentLive(percent);
      } else {
        seek(percentToMillis(percent));
      }
    }
    DebugMode.logV(TAG, "...seekToPercent()");
  }

  /**
   * Get the current item's playhead time as a percentage
   *
   * @return the playhead time percentage (between 0 and 100 inclusive)
   */
  int getPlayheadPercentage() {
    if (this.ooyalaPlayer.currentPlayer() == null) {
      return 0;
    } else if (this.ooyalaPlayer.getCurrentItem().isLive() && !this.ooyalaPlayer.isAdPlaying()) {
      return this.ooyalaPlayer.currentPlayer().livePlayheadPercentage();
    }
    return millisToPercent(this.ooyalaPlayer.currentPlayer().currentTime());
  }

  /**
   * Find where the playhead is with millisecond accuracy
   *
   * @return time in milliseconds
   */
  int getPlayheadTime() {
    if (this.ooyalaPlayer.currentPlayer() == null) {
      return queuedSeekTime > 0 ? queuedSeekTime : -1;
    }
    return this.ooyalaPlayer.currentPlayer().currentTime();
  }

  /**
   * Ignores Ad Players, and always returns the time from ContentPlayer (or queued seek time)
   * @return the time we should start the video in Cast mode
   */
  int getCurrentPlayheadForCastMode() {
    if (queuedSeekTime != 0) {
      return queuedSeekTime;
    }
    if (this.ooyalaPlayer.contextSwitcher.contentPlayer != null) {
      return this.ooyalaPlayer.contextSwitcher.contentPlayer.currentTime();
    }
    return 0;
  }

  boolean isLiveClosedCaptionsAvailable() {
    if (this.ooyalaPlayer.contextSwitcher.contentPlayer != null) {
      return this.ooyalaPlayer.contextSwitcher.contentPlayer.isLiveClosedCaptionsAvailable();
    }
    return false;
  }


  /**
   * Set the displayed closed captions language
   *
   * @param language
   *          2 letter country code of the language to display or nil to hide
   *          closed captions
   */
  void setClosedCaptionsLanguage(String language) {
    closedCaptionLanguage = language;

    // If we're given the "cc" language, we know it's live closed captions
    if (ooyalaPlayer.currentPlayer() != null) {
      ooyalaPlayer.currentPlayer().setClosedCaptionsLanguage(closedCaptionLanguage);
    }

    ooyalaPlayer.sendNotification(OoyalaPlayer.CLOSED_CAPTIONS_LANGUAGE_CHANGED_NAME);
  }

  /**
   * Get the currently enabled closed captions language
   *
   * @return 2 letter country code of the language to display or nil to hide
   *          closed captions
   */
  String getClosedCaptionsLanguage() {
    return closedCaptionLanguage;
  }

  /**
   * Get the current item's duration
   *
   * @return the duration in milliseconds
   */
  int getDuration() {
    if (ooyalaPlayer.currentPlayer() != null) {
      int playerDuration = ooyalaPlayer.currentPlayer().duration();
      if (playerDuration > 0) {
        return playerDuration;
      }
    }
    if (ooyalaPlayer.getCurrentItem() != null) {
      return ooyalaPlayer.getCurrentItem().getDuration();
    }
    return 0;
  }

  int getBufferPercentage() {
    if (ooyalaPlayer.currentPlayer() == null) {
      return 0;
    }
    return ooyalaPlayer.currentPlayer().buffer();
  }


  /**
   * @return true if the current player is seekable, false if there is no
   *         current player or it is not seekable
   */
  public boolean seekable() {
    DebugMode.logV(TAG, "seekable(): !null=" + (ooyalaPlayer.currentPlayer() != null) + ", seekable="
            + (ooyalaPlayer.currentPlayer() == null ? "false" : ooyalaPlayer.currentPlayer().seekable()));
    return ooyalaPlayer.currentPlayer() != null ? ooyalaPlayer.currentPlayer().seekable() : allowedToSeek;
  }
  /**
   * Set whether videos played by this OoyalaPlayer are seekable (default is
   * true)
   *
   * @param seekable
   *          true if seekable, false if not.
   */
  public void setSeekable(boolean seekable) {
    allowedToSeek = seekable;
    if (this.ooyalaPlayer.contextSwitcher.contentPlayer != null) {
      this.ooyalaPlayer.contextSwitcher.contentPlayer.setSeekable(allowedToSeek);
    }
  }

  float getVolume() {
    return this.volume;
  }

  void setVolume(float volume) {
    if (volume > 1) {
      DebugMode.logE(TAG, "Attempted to set volume to an invalid number: " + volume + ". setting to 1");
      volume = 1;
    } else if (volume < 0) {
      DebugMode.logE(TAG, "Attempted to set volume to an negative number: " + volume + ". setting to 0");
      volume = 0;
    }

    this.volume = volume;
    if (ooyalaPlayer.currentPlayer() != null) {
      ooyalaPlayer.currentPlayer().setVolume(this.volume);
    }
  }

}