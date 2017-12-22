package com.ooyala.android;

import com.ooyala.android.OoyalaPlayer.State;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 * Connect State changing objects with listeners thereof.
 * There are two kinds of listeners:
 * (1) OoyalaPlayer, which is updated via notifyPluginStateChange.
 * (2) StateNotifierListener, which is updated via onStateChange.
 */
public class StateNotifier {
  private WeakReference<OoyalaPlayer> _player;
  private State _state;
  private Set<StateNotifierListener> _listeners;

  StateNotifier(OoyalaPlayer player) {
    _player = new WeakReference<OoyalaPlayer>(player);
    _listeners = new HashSet<StateNotifierListener>();
  }
  
  public void setState(State state) {
    State oldState = _state;
    _state = state;
    for (StateNotifierListener l : _listeners) {
      l.onStateChange(this);
    }
    if (_player.get() != null) {
      _player.get().notifyPluginStateChange(this, oldState, state);
    }
  }

  public State getState() {
    return _state;
  }
  
  public void notifyPlayheadChange() {
    if (_player.get() != null) {
      _player.get().notifyPluginEvent(this,
        OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME);
    }
  }

  public void notifyBufferChange() {
    if (_player.get() != null) {
      _player.get().notifyPluginEvent(this,
        OoyalaPlayer.BUFFER_CHANGED_NOTIFICATION_NAME);
    }
  }

  public void notifyAdSkipped() {
    if (_player.get() != null) {
      _player.get().notifyPluginEvent(this,
          OoyalaPlayer.AD_SKIPPED_NOTIFICATION_NAME);
    }
  }

  public void notifyAdStartWithAdInfo(AdPodInfo info) {
    if (_player.get() != null) {
      _player.get().notifyPluginEvent(this,
              new OoyalaNotification(OoyalaPlayer.AD_STARTED_NOTIFICATION_NAME, info) );
    }
  }

  public void notifyAdCompleted() {
    final OoyalaPlayer p = _player.get();
    if (p != null) {
      p.notifyPluginEvent(this, OoyalaPlayer.AD_COMPLETED_NOTIFICATION_NAME);
    }
  }

  public void notifyAdOverlay(AdOverlayInfo overlayInfo) {
    if (_player.get() != null) {
      _player.get().notifyPluginEvent(this,
          new OoyalaNotification(OoyalaPlayer.AD_OVERLAY_NOTIFICATION_NAME, overlayInfo));
    }
  }

  public void addListener(StateNotifierListener l) {
    _listeners.add(l);
  }

  public void removeListener(StateNotifierListener l) {
    _listeners.remove(l);
  }

}
