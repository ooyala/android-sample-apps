
package com.ooyala.sample;

import android.content.Context;

import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;
import com.google.android.gms.cast.framework.media.CastMediaOptions;
import com.google.android.gms.cast.framework.media.NotificationOptions;

import java.util.List;


/**
 * It is used to provide google cast options by manifest.
 */
public class CastOptionsProvider implements OptionsProvider {
  private final String APP_ID = "4172C76F";

  @Override
  public CastOptions getCastOptions(Context context) {
    NotificationOptions notificationOptions = new NotificationOptions.Builder()
        .setTargetActivityClassName(ChromecastPlayerActivity.class.getName())
        .setPlayDrawableResId(R.drawable.ic_media_play_light)
        .setPauseDrawableResId(R.drawable.ic_media_pause_light)
        .build();
    CastMediaOptions mediaOptions = new CastMediaOptions.Builder()
        .setNotificationOptions(notificationOptions)
        .setExpandedControllerActivityClassName(ChromecastPlayerActivity.class.getName())
        .build();

    return new CastOptions.Builder()
        .setReceiverApplicationId(APP_ID)
        .setCastMediaOptions(mediaOptions)
        .build();
  }

  @Override
  public List<SessionProvider> getAdditionalSessionProviders(Context context) {
    return null;
  }
}