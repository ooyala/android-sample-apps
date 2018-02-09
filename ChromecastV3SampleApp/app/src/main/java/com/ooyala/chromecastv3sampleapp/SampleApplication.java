package com.ooyala.chromecastv3sampleapp;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.ooyala.android.castsdkv3.CastManager;

public class SampleApplication extends Application {
  CastManager castManager;

  @Override
  public void onCreate() {
    super.onCreate();
    try {
      boolean isDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
      CastManager.initialize(this);
      castManager = CastManager.getCastManager();
    } catch (CastManager.CastManagerInitializationException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
