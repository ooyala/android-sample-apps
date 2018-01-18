package com.ooyala.android.player;

import android.view.View;

import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayer.SeekStyle;
import com.ooyala.android.OoyalaPlayer.State;
import com.ooyala.android.item.Stream;
import com.ooyala.android.util.DebugMode;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class MoviePlayer extends Player implements Observer {

  private static final String TAG = "MoviePlayer";
  protected static final String DRM_TENENT_PATH = "/sas/drm2/%s/%s/%s/%s"; // '/drm2/:pcode/:embed_code/:drm_type/:tenant'

  private State _stateToResume = State.INIT;
  private int _millisToResume = 0;
  private StreamPlayer _basePlayer;
  protected Set<Stream> _streams;
  private boolean _suspended = true;
  protected boolean _seekable = true;
  private boolean _live = false;

  private void setStreams( Set<Stream> streams ) {
    if( streams == null ) {
      _streams = new HashSet<Stream>();
    }
    else {
      _streams = streams;
    }
  }

  protected StreamPlayer createStreamPlayer() {
    return new BaseStreamPlayer();
  }

  @Override
  public void init(OoyalaPlayer parent, Set<Stream> streams) {
    setStreams( streams );
    _parent = parent;
    _streams = streams;
    _suspended = false;
    if(_basePlayer == null) {
      _basePlayer = createStreamPlayer();
    }
    _basePlayer.addObserver(this);
    _basePlayer.init(parent, streams);
  }

  /**
   * Specify if this baseplayer will be playing a live video or not (default false)
   * @param isLive
   */
  public void setLive(boolean isLive) {
    _live = isLive;
  }

  public StreamPlayer getBasePlayer() {
    return _basePlayer;
  }

  @Override
  public void reset() {
    _suspended = false;
    if (_basePlayer != null) {
      _basePlayer.reset();
    }
  }

  @Override
  public void suspend() {
    if (_basePlayer != null) {
      suspend(_basePlayer.currentTime(), _basePlayer.getState());
    } else {
      suspend(0, State.INIT);
    }
  }

  public void suspend(int millisToResume, State stateToResume) {
    // If we're already suspended, we don't need to do it again
    if (_suspended) {
      DebugMode.logI(this.getClass().toString(), "Trying to suspend an already suspended MoviePlayer");
      return;
    }
    DebugMode.logD(this.getClass().toString(), "Movie Player Suspending. ms to resume: " + millisToResume + ". State to resume: " + stateToResume);
    _millisToResume = millisToResume;
    _stateToResume = stateToResume;
    if (_basePlayer != null) {
      _basePlayer.suspend();
    }
    _suspended = true;
  }

  @Override
  public void resume() {
    resume(_millisToResume, _stateToResume);
    setState(getState());
  }

  @Override
  public void resume(int millisToResume, State stateToResume) {  // TODO: Wtf to do here?
    _suspended = false;

    if (_basePlayer != null) {
    _basePlayer.init(_parent, _streams);

    DebugMode.logD(this.getClass().toString(), "Movie Player Resuming. ms to resume: " + millisToResume + ". State to resume: " + stateToResume);
    _basePlayer.resume(millisToResume, stateToResume);
    }
    else {
      DebugMode.logE(TAG, "Trying to resume MoviePlayer without a base player!");
    }
  }

  public int timeToResume() {
    return _millisToResume;
  }

  @Override
  public void destroy() {
    if (_basePlayer != null) {
      _basePlayer.deleteObserver(this);
      _basePlayer.destroy();
      _basePlayer = null;
    }
  }

  @Override
  public void update(Observable arg0, Object arg) {
    if (_suspended) {
      return;
    }
    setChanged();
    notifyObservers(arg);
  }

  @Override
  public View getView() {
    if (_basePlayer != null) {
      return _basePlayer.getView();
    } else {
      DebugMode.logE(TAG, "Trying to getView without a Base Player");
      return null;
    }
  }

  @Override
  public void setParent(OoyalaPlayer parent) {
    _parent = parent;
    if (_basePlayer != null) {
    _basePlayer.setParent(parent);
    } else {
      DebugMode.logE(TAG, "Trying to setParent MoviePlayer without a Base Player");
    }
  }

  //Delegated to base player
  @Override
  public void pause() {
    if (_basePlayer != null) {
      _basePlayer.pause();
    } else {
      DebugMode.logE(TAG, "Trying to pause MoviePlayer without a Base Player");
    }
  }
  @Override
  public void play() {
    if (_basePlayer != null) {
      DebugMode.logV( TAG, "play()" );
      _basePlayer.play();
    } else {
      DebugMode.logE(TAG, "Trying to play MoviePlayer without a Base Player");
    }
  }
  @Override
  public void stop() {
    if (_basePlayer != null) {
      _basePlayer.stop();
    } else {
    DebugMode.logE(TAG, "Trying to stop MoviePlayer without a Base Player");
    }
  }

  @Override
  public int currentTime() { return _basePlayer != null ? _basePlayer.currentTime() : 0; }
  @Override
  public int duration() { return _basePlayer != null ? _basePlayer.duration() : 0; }
  @Override
  public int buffer() { return _basePlayer != null ? _basePlayer.buffer() : 0; }
  @Override
  public int getBufferPercentage() { return _basePlayer != null ? _basePlayer.getBufferPercentage() : 0; }
  
  @Override
  public int livePlayheadPercentage() { return _basePlayer != null ? _basePlayer.livePlayheadPercentage() : 0; }
  
  @Override
  public void seekToPercentLive(int percent) {
    if (_basePlayer != null) {
      _basePlayer.seekToPercentLive(percent);
    }
  }

  @Override
  public boolean seekable() { return _seekable; }
  public void setSeekable(boolean seekable) { _seekable = seekable; }

  @Override
  public void seekToTime(int timeInMillis) {
    if (_basePlayer != null) {
      if (_seekable) { _basePlayer.seekToTime(timeInMillis); }
    } else {
      DebugMode.logE(TAG, "Trying to seek MoviePlayer without a Base Player");
    }
}

  @Override
  public SeekStyle getSeekStyle() {
    return _basePlayer != null ? _basePlayer.getSeekStyle() : SeekStyle.BASIC;
  }

  @Override
  public State getState() { return _basePlayer != null ? _basePlayer.getState() : super.getState(); }
  @Override
  protected void setState(State state) {
    if (_basePlayer != null) {
      _basePlayer.setState(state);
    } else {
      super.setState(state);
    }
  }

  @Override
  public OoyalaException getError() { return _error != null ? _error : (_basePlayer != null ? _basePlayer.getError() : null); }
  @Override
  public boolean isPlaying() { return _basePlayer != null ? _basePlayer.isPlaying() : false; }
  @Override
  public boolean isLiveClosedCaptionsAvailable() { return _basePlayer != null ? _basePlayer.isLiveClosedCaptionsAvailable() : false; }

  @Override
  public void setClosedCaptionsLanguage(String language) {
    if (_basePlayer != null) {
      _basePlayer.setClosedCaptionsLanguage(language);
    } else {
      DebugMode.logE(TAG, "Trying to setClosedCaptionsLanguage MoviePlayer without a Base Player");
    }
  }

  public void setVolume(float volume) {
    if (_basePlayer != null) {
      _basePlayer.setVolume(volume);
    } else {
      DebugMode.logW(TAG, "Volume set, but there is no baseStreamPlayer");
    }
  }
}