package com.ooyala.sample.utils;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaPlayer.State;
import com.ooyala.android.StateNotifier;
import com.ooyala.android.player.PlayerInterface;
import com.ooyala.android.player.PlayerType;
import com.ooyala.android.plugin.LifeCycleInterface;

public class SampleAdPlayer extends LinearLayout implements PlayerInterface,
    LifeCycleInterface {
  private final int DURATION = 5000;
  private final int REFRESH_RATE = 250;

  private WeakReference<SampleAdPlugin> _plugin;

  private StateNotifier _stateNotifier;
  private Timer _timer;
  private int _playhead = 0;
  private String _adText;
  private Handler _timerHandler;
  private TextView textView;

  public SampleAdPlayer(Context context, StateNotifier notifier,
      ViewGroup parent) {
    super(context);
    this.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
    this.setBackgroundColor(Color.BLACK);
    textView = new TextView(context);
    this.addView(textView);
    parent.addView(this);
    _stateNotifier = notifier;
    _timerHandler = new Handler() {
      public void handleMessage(Message msg) {
        refresh();
      }
    };
    _stateNotifier.setState(State.LOADING);
  }

  @Override
  public int buffer() {
    // TODO Auto-generated method stub
    return 100;
  }

  @Override
  public int currentTime() {
    return _playhead;
  }

  @Override
  public int duration() {
    return DURATION;
  }

  @Override
  public State getState() {
    return _stateNotifier.getState();
  }

  @Override
  public int livePlayheadPercentage() {
    return 0;
  }

  @Override
  public void seekToPercentLive(int i) {

  }

  @Override
  public PlayerType getPlayerType() {
    return PlayerType.FLAT_PLAYER;
  }

  @Override
  public boolean isLiveClosedCaptionsAvailable() {
    return false;
  }

  @Override
  public void setClosedCaptionsLanguage(String s) {
    
  }

  @Override
  public void pause() {
    if (_timer != null) {
      _timer.cancel();
      _timer = null;
    }
    _stateNotifier.setState(State.PAUSED);
  }

  @Override
  public void play() {
    if (_timer == null) {
      _timer = new Timer();
      _timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          _timerHandler.sendEmptyMessage(0);
        }
      }, REFRESH_RATE, REFRESH_RATE);
    }
    _stateNotifier.setState(State.PLAYING);
  }

  @Override
  public void seekToTime(int arg0) {
  }

  @Override
  public boolean seekable() {
    return false;
  }

  @Override
  public void stop() {
  }

  private void refresh() {
    _playhead += REFRESH_RATE;
    String text = _adText + " " + String.valueOf((DURATION - _playhead) / 1000);
    textView.setText(text);
    if (_playhead >= DURATION) {
      _timer.cancel();
      _timer = null;
      _stateNotifier.setState(State.COMPLETED);
    } else {
      _stateNotifier.notifyPlayheadChange();
    }
  }

  @Override
  public void destroy() {
    // TODO Auto-generated method stub
    if (getParent() != null) {
      ((ViewGroup) getParent()).removeView(this);
    }
  }

  @Override
  public void reset() {
    // TODO Auto-generated method stub

  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub

  }

  @Override
  public void resume(int arg0, State arg1) {
    // TODO Auto-generated method stub

  }

  @Override
  public void suspend() {
    // TODO Auto-generated method stub
  }

  public void loadAd(SampleAdSpot ad) {
    if (ad != null) {
      _adText = ad.text();
    } else {
      _adText = "null";
    }
    textView.setText(_adText);
    _stateNotifier.setState(State.READY);
  }

  public OoyalaException getError() {
    return null;
  }

  @Override
  public void setVolume(float v) {}
}
