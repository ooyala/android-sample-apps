package com.ooyala.sample;


import android.util.Log;

import com.ooyala.android.imasdk.OoyalaIMAManager;

public class PreconfiguredImaAdPlayerActivity extends PreconfiguredAdPlayerActivity {

  @Override
  public void createAdditionalAdverbManager() {
    @SuppressWarnings("unused")
    OoyalaIMAManager imaManager = new OoyalaIMAManager(player);
  }
}
