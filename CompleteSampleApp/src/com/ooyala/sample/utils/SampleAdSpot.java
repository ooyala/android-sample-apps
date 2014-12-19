package com.ooyala.sample.utils;

import com.ooyala.android.item.AdSpot;

public class SampleAdSpot extends AdSpot {
  private int _time;
  private String _text;
  private boolean _played;

  SampleAdSpot(int time, String text) {
    _time = time;
    _text = text;
    _played = false;
  }

  @Override
  public int getTime() {
    return _time;
  }

  public boolean isPlayed() {
    return _played;
  }

  public void setPlayed(boolean played) {
    _played = played;
  }

  public String text() {
    return _text;
  }
}
