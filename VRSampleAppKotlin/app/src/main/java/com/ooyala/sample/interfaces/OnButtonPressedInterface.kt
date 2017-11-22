package com.ooyala.sample.interfaces

import android.view.KeyEvent

interface OnButtonPressedInterface {

  fun onKeyUp(keyCode: Int, event: KeyEvent)
  fun onKeyDown(keyCode: Int, event: KeyEvent)
  fun onBackPressed()

}