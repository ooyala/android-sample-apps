package com.ooyala.sample.ChromecastSampleApp;

import android.app.Application;

import com.ooyala.android.castsdk.CastManager;

/**
 * Created by zchen on 10/9/15.
 */

public class SampleApplication extends Application {
  private final String NAMESPACE = "urn:x-cast:ooyala";
  private final String APP_ID = "1F894B93";

  @Override
  public void onCreate() {
    super.onCreate();
    try {
      CastManager.initialize(this, APP_ID, ChromecastPlayerActivity.class, NAMESPACE);
    } catch (CastManager.CastManagerInitializationException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
