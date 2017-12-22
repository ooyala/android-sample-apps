package com.ooyala.android;

import com.ooyala.android.item.ClosedCaptions;

import java.util.concurrent.Callable;

/**
 * A class that implements fetching closed captions.
 */
class SubTaskClosedCaptions implements Callable<Boolean> {
  private final ClosedCaptions cc;

  public SubTaskClosedCaptions(ClosedCaptions cc) {
    this.cc = cc;
  }

  @Override
  public Boolean call() {
    return cc.fetchClosedCaptionsInfo();
  }
}
