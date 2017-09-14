package com.ooyala.sample.vrsampleapp;

import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;

/**
 * Created by Alina_Voronkova on 12/09/17.
 */

public class PreconfiguredAdPlayerActivity extends AbstractHookActivity {

  private OoyalaSkinLayout skinLayout;
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

      skinLayout = (OoyalaSkinLayout) findViewById(R.id.ad_player_skin_layout);
      SkinOptions skinOptions = new SkinOptions.Builder().build();
      final OoyalaSkinLayoutController playerController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
      playerController.addObserver(this);

      @SuppressWarnings("unused")
      OoyalaIMAManager imaManager = new OoyalaIMAManager(player);

      player.setEmbedCode(embedCode);
    }
  }
}
