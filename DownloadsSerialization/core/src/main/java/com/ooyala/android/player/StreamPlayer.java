package com.ooyala.android.player;

import android.os.Handler;
import android.os.Message;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public abstract class StreamPlayer extends Player {
  protected Timer _playheadUpdateTimer = null;

  protected static final long TIMER_DELAY = 0;
  protected static final long TIMER_PERIOD = 250;

  // Playhead time update notifications
  private static class TimerHandler extends Handler {
    private WeakReference<StreamPlayer> _player;
    private int _lastPlayhead = -1;

    public TimerHandler(StreamPlayer player) {
      _player = new WeakReference<StreamPlayer>(player);
    }

    public void handleMessage(Message msg) {
      StreamPlayer player = _player.get();
      if (player != null && _lastPlayhead != player.currentTime() && player.isPlaying()) {
        player.notifyTimeChanged();
      }
    }
  }

  private final Handler _playheadUpdateTimerHandler = new TimerHandler(this);

  // Timer tasks for playhead updates
  protected void startPlayheadTimer() {
    if (_playheadUpdateTimer != null) {
      stopPlayheadTimer();
    }
    _playheadUpdateTimer = new Timer();
    _playheadUpdateTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        _playheadUpdateTimerHandler.sendEmptyMessage(0);
      }
    }, TIMER_DELAY, TIMER_PERIOD);
  }

  protected void stopPlayheadTimer() {
    if (_playheadUpdateTimer != null) {
      _playheadUpdateTimer.cancel();
      _playheadUpdateTimer = null;
    }
  }

  protected void notifyTimeChanged() {
    setChanged();
    notifyObservers(new OoyalaNotification(OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME));
  }
}
