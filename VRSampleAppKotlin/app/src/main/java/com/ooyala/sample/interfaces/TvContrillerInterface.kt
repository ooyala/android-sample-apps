package com.ooyala.sample.interfaces

import android.view.KeyEvent

interface TvControllerInterface {

  fun onKeyUp(keyCode: Int, event: KeyEvent)
  fun onKeyDown(keyCode: Int, event: KeyEvent)

}