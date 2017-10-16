package com.ooyala.sample;

import android.content.Intent;
import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;

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
          .setShowNativeLearnMoreButton(false)
          .setShowPromoImage(false)
          .build();

      player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
      player.addObserver(this);

      OoyalaSkinLayout skinLayout = (OoyalaSkinLayout) findViewById(R.id.ad_player_skin_layout);
      SkinOptions skinOptions = new SkinOptions.Builder().build();
      final OoyalaSkinLayoutController playerController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
      playerController.addObserver(this);

      createAdditionalAdverbManager();

      player.setEmbedCode(embedCode);
    }
  }

  public void createAdditionalAdverbManager() {
    //Should be override if you want to use additional adverb manager, e.g. OoyalaIMAManager
  }

  @Override
  void initPlayerData() {
    Intent intent = getIntent();
    if (intent != null && intent.getExtras() != null) {
      embedCode = intent.getExtras().getString("embed_code");
      pcode = intent.getExtras().getString("pcode");
      domain = intent.getExtras().getString("domain");
    }
  }
}
