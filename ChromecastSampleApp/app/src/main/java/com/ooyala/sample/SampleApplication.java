package com.ooyala.sample;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ooyala.cast.CastManager;
import com.ooyala.cast.RemoteDeviceConnector;
import com.ooyala.cast.mediainfo.VideoData;
import com.ooyala.sample.skin.SkinCastPlayerActivity;

public class SampleApplication extends Application {
  private static final String NAMESPACE = "urn:x-cast:ooyala";
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

  public static void updatePlayerData(Context context, ChromecastPlayerSelectionOption option) {
    updateLastChosenParams(context, option.embedCode, option.secondEmbedCode, option.pcode, option.domain);
  }

  public static void updatePlayerData(Context context, VideoData data) {
    updateLastChosenParams(context, data.getEmbedCode(), null, data.getPCode(), data.getDomain());
  }

  private static void updateLastChosenParams(Context context, String embedCode, String secondEmbedCode,
                                             String pCode, String domain ) {
    SharedPreferences lastChosenParams = context.getSharedPreferences("LastChosenParams", MODE_PRIVATE);
    lastChosenParams
      .edit()
      .putString("embedcode", embedCode)
      .putString("secondEmbedCode", secondEmbedCode)
      .putString("pcode", pCode)
      .putString("domain", domain)
      .apply();
  }
}
