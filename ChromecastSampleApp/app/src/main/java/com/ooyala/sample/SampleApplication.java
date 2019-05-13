package com.ooyala.sample;

import android.app.Activity;
import android.app.Application;
import com.ooyala.cast.CastManager;
import com.ooyala.cast.RemoteDeviceConnector;
import com.ooyala.sample.skin.SkinCastPlayerActivity;

public class SampleApplication extends Application {
  private final static String NAMESPACE = "urn:x-cast:ooyala";
  private static Class<? extends Activity> activity = SkinCastPlayerActivity.class;

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

  public void setExpandedControllerActivity(Class<? extends Activity> activity) {
    SampleApplication.activity = activity;
  }

  public Class<? extends Activity> getExpandedControllerActivity() {
    return activity;
  }
}
