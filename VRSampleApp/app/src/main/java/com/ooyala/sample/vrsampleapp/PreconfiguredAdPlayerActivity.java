package com.ooyala.sample.vrsampleapp;

import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.ui.AbstractOoyalaPlayerLayoutController;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;

/**
 * Created by Alina_Voronkova on 12/09/17.
 */

public class PreconfiguredAdPlayerActivity extends AbstractHookActivity {

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_simple_frame_layout);

    completePlayerSetup(asked);
  }

  @Override
  void completePlayerSetup(final boolean asked) {
    if (asked) {

      final FCCTVRatingConfiguration tvRatingConfiguration = new FCCTVRatingConfiguration.Builder().setDurationSeconds(5).build();
      final Options options = new Options.Builder()
          .setTVRatingConfiguration(tvRatingConfiguration)
          .setBypassPCodeMatching(true)
          .setUseExoPlayer(true)
          .build();

      player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
      player.addObserver(this);

      playerLayoutController = new OptimizedOoyalaPlayerLayoutController(
          (OoyalaPlayerLayout) findViewById(R.id.ooyala_player),
          player,
          AbstractOoyalaPlayerLayoutController.DefaultControlStyle.AUTO
      );

      @SuppressWarnings("unused")
      OoyalaIMAManager imaManager = new OoyalaIMAManager(player);

      player.setEmbedCode(embedCode);
    }
  }
}
