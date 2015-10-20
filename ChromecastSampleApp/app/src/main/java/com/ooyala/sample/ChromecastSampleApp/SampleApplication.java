package com.ooyala.sample.ChromecastSampleApp;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.ooyala.android.castsdk.CastManager;
import com.ooyala.android.castsdk.CastOptions;

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
      boolean isDebuggable =  ( 0 != ( getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );
      CastOptions options = new CastOptions.Builder(APP_ID, NAMESPACE).setEnableCastDebug(isDebuggable).setTargetActivity(ChromecastPlayerActivity.class).build();
      CastManager.initialize(this, options);
    } catch (CastManager.CastManagerInitializationException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
