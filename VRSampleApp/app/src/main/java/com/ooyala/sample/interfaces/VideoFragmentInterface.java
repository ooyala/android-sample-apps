package com.ooyala.sample.interfaces;

import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;

public interface VideoFragmentInterface {

  void applyADSManager(OoyalaSkinLayout skinLayout);

  Options createOptions(FCCTVRatingConfiguration tvRatingConfiguration);
}
