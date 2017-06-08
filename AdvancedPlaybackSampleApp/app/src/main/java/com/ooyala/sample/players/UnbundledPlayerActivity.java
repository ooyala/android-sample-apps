package com.ooyala.sample.players;


import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.Stream;
import com.ooyala.android.item.UnbundledVideo;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

public class UnbundledPlayerActivity extends AbstractHookActivity {

  public static String getName() {
    return "Unbundled";
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {
      //Initialize the player
      OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
      Options options = new Options.Builder().setUseExoPlayer(true).build();
      player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
      playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);

      final String url = getIntent().getExtras().getString("embed_code");
      Stream s = new Stream(url, Stream.DELIVERY_TYPE_MP4);
      UnbundledVideo u = new UnbundledVideo(s);
      final boolean success = player.setUnbundledVideo(u);
      if (success) {
        //Uncomment for Auto-Play
        //player.play();
      } else {
        Log.e(TAG, "Asset Failure");
      }
    }
  }

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_simple_layout);
    completePlayerSetup(asked);
  }
}
