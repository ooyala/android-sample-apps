package com.ooyala.sample.interfaces;

import android.view.KeyEvent;

public interface TvControllerInterface {

  void onKeyUp(int keyCode, KeyEvent event);
  void onKeyDown(int keyCode, KeyEvent event);
  
}
