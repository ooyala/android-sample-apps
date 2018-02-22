package com.ooyala.chromecastv3sampleapp;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.ooyala.android.castsdkv3.CastManager;

public class SampleApplication extends Application {
  CastManager castManager;
  private final String NAMESPACE = "urn:x-cast:ooyala";

  @Override
  public void onCreate() {
    super.onCreate();
    try {
      CastManager.initialize(this, NAMESPACE);
      castManager = CastManager.getCastManager();
    } catch (CastManager.CastManagerInitializationException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
