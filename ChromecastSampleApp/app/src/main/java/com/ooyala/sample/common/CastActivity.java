package com.ooyala.sample.common;

import android.os.Bundle;

import com.ooyala.cast.CastManager;
import com.ooyala.cast.RemoteDeviceConnector;

import androidx.annotation.Nullable;

public abstract class CastActivity extends PlayerActivity {
  protected boolean wasInCastMode;
  protected CastManager castManager;
  protected RemoteDeviceConnector remoteDeviceConnector;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    castManager = CastManager.getCastManager();
    remoteDeviceConnector = RemoteDeviceConnector.getRemoteDeviceConnector();
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
      remoteDeviceConnector.registerToOoyalaPlayer(player);
      remoteDeviceConnector.sendAvailableCastDevicesToPlayer();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (castManager != null) {
      castManager.deregisterFromOoyalaPlayer();
      remoteDeviceConnector.deregisterFromOoyalaPlayer(player);
    }
  }
}
