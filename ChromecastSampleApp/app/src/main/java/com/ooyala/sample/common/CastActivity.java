package com.ooyala.sample.common;

import android.os.Bundle;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.cast.CastManager;
import com.ooyala.cast.MediaRouterManager;

import java.util.Observable;

import androidx.annotation.Nullable;

public abstract class CastActivity extends PlayerActivity {
  protected boolean wasInCastMode;
  protected CastManager castManager;
  protected MediaRouterManager mediaRouterManager;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    castManager = CastManager.getCastManager();
    mediaRouterManager = MediaRouterManager.getMediaRouterManager();
  }

  @Override
  protected void completePlayerSetup() {
    super.completePlayerSetup();
    if (castManager != null && player != null) {
      castManager.registerWithOoyalaPlayer(player);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (player != null && !player.isInCastMode() && wasInCastMode) {
      castManager.hideCastView();
      wasInCastMode = false;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (castManager != null) {
      castManager.destroy();
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (castManager != null && player != null) {
      castManager.registerWithOoyalaPlayer(player);
      mediaRouterManager.registerToOoyalaPlayer(player);
      mediaRouterManager.addMediaRouterCallback();
      mediaRouterManager.sendCurrentCastMediaRoutesToPlayer();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (castManager != null) {
      castManager.deregisterFromOoyalaPlayer();
      mediaRouterManager.deregisterFromOoyalaPlayer(player);
      mediaRouterManager.removeMediaRouterCallback();
    }
  }
}
