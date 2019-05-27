package com.ooyala.sample.players;

import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.CustomPlayerControls;

/**
 * This activity illustrates how you can implement Custom controls for the OoyalaPlayer
 * <p>
 * The following files were slightly modified from the DefaultControlsSource provided with the
 * Ooyala SDK:
 * CustomPlayerControls (was DefaultOoyalaPlayerInlineControls)
 * CuePointsSeekBar
 * AbstractDefaultOoyalaPlayerControls
 * Images
 * <p>
 * This example was made with Ooyala SDK 3.4.0 source, but is still a good example of how
 * the default controls can be overridden.
 */
public class CustomControlsPlayerActivity extends AbstractHookActivity {

  public static String getName() {
    return "Custom Controls";
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {
      //Initialize the player
      player = new OoyalaPlayer(pcode, new PlayerDomain(domain), createPlayerOptions());
      playerLayout = findViewById(R.id.ooyalaPlayer);
      playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);

      //Set the controls to use for Inline Control style.
      playerLayoutController.setInlineControls(new CustomPlayerControls(player, playerLayout));
      player.addObserver(this);

      if (player.setEmbedCode(embedCode)) {
        //Uncomment for Auto-Play
        //player.play();
      }
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_simple_layout);
    completePlayerSetup(asked);
  }
}
