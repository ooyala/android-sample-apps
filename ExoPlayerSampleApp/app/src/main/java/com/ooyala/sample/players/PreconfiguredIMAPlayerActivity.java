package com.ooyala.sample.players;


import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.R;

/**
 * This activity illustrates how to use Freewheel when all configuration is stored in Ooyala Servers
 *
 * In order for Freewheel to work this simply, you need the following parameters set in your Third Party Module Parameters
 * - fw_android_ad_server
 * - fw_android_player_profile
 *
 * And an Freewheel Ad Spot configured in Backlot with at least the following:
 * - Network ID
 * - Video Asset Network ID
 * - Site Section ID
 *
 */
public class PreconfiguredIMAPlayerActivity extends AbstractHookActivity {
  public final static String getName() {
    return "Preconfigured IMA Player";
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {
      // Get the SkinLayout from our layout xml
      OoyalaSkinLayout skinLayout = (OoyalaSkinLayout) findViewById(R.id.ooyalaSkin);

      // Create the OoyalaPlayer, with some built-in UI disabled
      PlayerDomain domain = new PlayerDomain(DOMAIN);
      Options options = new Options.Builder().setShowPromoImage(false).setUseExoPlayer(true).setShowNativeLearnMoreButton(false).build();
      player = new OoyalaPlayer(pcode, domain, options);

      //Create the SkinOptions, and setup React
      SkinOptions skinOptions = new SkinOptions.Builder().build();
      playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
      //Add observer to listen to fullscreen open and close events
      playerLayoutController.addObserver(this);
      player.addObserver(this);

      if (player.setEmbedCode(embedCode)) {
        //player.play();
      }

      @SuppressWarnings("unused")
      OoyalaIMAManager imaManager = new OoyalaIMAManager(player, skinLayout);
    }
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_skin_simple_layout);
    completePlayerSetup(asked);
  }
}
