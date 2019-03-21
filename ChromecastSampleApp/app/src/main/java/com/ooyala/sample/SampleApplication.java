package com.ooyala.sample;

import android.app.Application;

import com.ooyala.cast.CastManager;
import com.ooyala.cast.RemoteDeviceConnector;

public class SampleApplication extends Application {
  private final static String NAMESPACE = "urn:x-cast:ooyala";

  @Override
  public void onCreate() {
    super.onCreate();
    try {
      CastManager.initCastManager(this, NAMESPACE);
      RemoteDeviceConnector.initRemoteDeviceConnector(this, getResources().getString(R.string.ooyala_cast_id));
    } catch (CastManager.CastManagerInitializationException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
