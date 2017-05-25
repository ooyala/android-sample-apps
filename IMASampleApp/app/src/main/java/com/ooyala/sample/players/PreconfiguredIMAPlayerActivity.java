package com.ooyala.sample.players;



import android.os.Bundle;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;

/**
 * This activity illustrates how to use Freewheel when all configuration is stored in Ooyala Servers
 *
 * In order for Freewheel to work this simply, you need the following parameters set in your Third Party Module Parameters
 * - ima_android_ad_server
 * - ima_android_player_profile
 *
 * And an IMA Ad Spot configured in Backlot with at least the following:
 * - Network ID
 * - Video Asset Network ID
 * - Site Section ID
 *
 */
public class PreconfiguredIMAPlayerActivity extends AbstractHookActivity {

  protected OptimizedOoyalaPlayerLayoutController playerLayoutController;

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
      player = new OoyalaPlayer(pcode, new PlayerDomain(domain));
      player.addObserver(this);

      OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
      playerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);

      @SuppressWarnings("unused")
      OoyalaIMAManager imaManager = new OoyalaIMAManager(player);

      player.setEmbedCode(embedCode);
    }
  }
}
