package com.ooyala.sample.screen;

import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.skin.OoyalaSkinLayout;

public class IMAVideoFragment extends VideoFragment {

  @Override
  public void applyADSManager(OoyalaSkinLayout skinLayout) {
    final OoyalaIMAManager imaManager = new OoyalaIMAManager(player, skinLayout);
  }
}
