package com.ooyala.android.player;

import android.view.View;

import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayer.SeekStyle;
import com.ooyala.android.OoyalaPlayer.State;
import com.ooyala.android.item.Stream;
import com.ooyala.android.plugin.LifeCycleInterface;

import java.util.Observable;
import java.util.Set;

/**
 * The interface that must be implemented in order to plug into OoyalaPlayer and Ooyala UI
 *
 */
public class Player extends Observable implements PlayerInterface,
    LifeCycleInterface {
  protected OoyalaPlayer _parent = null;
  /** The Player's current error if it exists */
  protected OoyalaException _error = null;
  protected ControlSharingSurfaceView _view = null;
  protected int _buffer = 0;
  protected State _state = State.INIT;

  /**
   * Init the player
   */
  protected Player() {}

  public void init(OoyalaPlayer parent, Set<Stream> streams) {}

  @Override
  public State getState() {
    return _state;
  }

  protected void setState(State state) {
    final State oldState = this._state;
    this._state = state;
    super.setChanged();
    super.notifyObservers( PlayerInterfaceUtil.buildSetStateNotification( oldState, this._state ) );
  }

  public OoyalaException getError() {
    return _error;
  }

  public View getView() {
    return _view;
  }

  public void setParent(OoyalaPlayer parent) {
    _parent = parent;
  }

  public OoyalaPlayer getParent() {
    return _parent;
  }

  public int getBufferPercentage() {
    return _buffer;
  }

  public boolean isPlaying() {
    return false;
  }

  public void setError(OoyalaException error) {
    _error = error;
  }

  /**
   * Returns if the current player has found live closed caption info in the stream
   * @return true if the stream has embedded live closed captions
   */
  public boolean isLiveClosedCaptionsAvailable(){
    return false;
  }

  public SeekStyle getSeekStyle() {
    return SeekStyle.BASIC;
  }

  @Override
  public void reset() {
    // TODO Auto-generated method stub

  }

  @Override
  public void suspend() {
    // TODO Auto-generated method stub

  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub

  }

  @Override
  public void resume(int timeInMilliSecond, State stateToResume) {
    // TODO Auto-generated method stub

  }

  @Override
  public void destroy() {
    // TODO Auto-generated method stub

  }

  @Override
  public void pause() {
    // TODO Auto-generated method stub

  }

  @Override
  public void play() {
    // TODO Auto-generated method stub

  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub

  }

  @Override
  public int currentTime() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int duration() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int buffer() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void seekToTime(int timeInMillis) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean seekable() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public int livePlayheadPercentage() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void seekToPercentLive(int percent) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setClosedCaptionsLanguage(String language) {
    // TODO Auto-generated method stub
  }

  public void setVolume(float volume) { }
}
