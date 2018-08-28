package com.ooyala.sample.screen;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.skin.OoyalaSkinLayout;

import java.util.Observer;

/**
 * This activity illustrates how you can set the initial playback speed using Options {@link Options}
 * and set a playback speed at a player directly {@link OoyalaPlayer}.
 *
 * Manage the following values: {@value INITIAL_PLAYBACK_SPEED} and {@value PLAYBACK_SPEED} to
 * set the initial playback speed and a speed during the playback.
 */
public class PlaybackSpeedVideoFragment extends VideoFragment implements Observer, DefaultHardwareBackBtnHandler {
  public static final String TAG = PlaybackSpeedVideoFragment.class.getCanonicalName();

  private static final float INITIAL_PLAYBACK_SPEED = 2.0f;
  private static final float PLAYBACK_SPEED = 0.5f;

  @Override
  public void applyADSManager(OoyalaSkinLayout skinLayout) {
    final OoyalaIMAManager imaManager = new OoyalaIMAManager(player, skinLayout);
  }

  @Override
  public Options createOptions(FCCTVRatingConfiguration tvRatingConfiguration) {
    final Options options = new Options.Builder()
      .setTVRatingConfiguration(tvRatingConfiguration)
      .setBypassPCodeMatching(true)
      .setUseExoPlayer(true)
      .setShowNativeLearnMoreButton(false)
      .setShowPromoImage(false)
      .setInitialPlaybackSpeed(INITIAL_PLAYBACK_SPEED)
      .build();
    return options;
  }
}
