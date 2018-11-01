package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

/**
 * This activity illustrates how you can create, play a basic playback video
 * and completely destroy OoyalaPlayer
 */
public class ReinitPlayerActivity extends AbstractHookActivity {

  private OoyalaPlayerLayout playerLayout;

  public static String getName() {
    return "Reinit player activity";
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_set_asset_layout);

    // Get the SkinLayout from our layout xml
    playerLayout = findViewById(R.id.ooyalaSkin);

    completePlayerSetup(asked);
    initButtonListeners();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    destroyPlayer();
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {

      // Create the OoyalaPlayer, with some built-in UI disabled
      PlayerDomain playerDomain = new PlayerDomain(domain);
      Options options = new Options.Builder()
        .setShowNativeLearnMoreButton(false)
        .setShowPromoImage(false)
        .setUseExoPlayer(true)
        .build();

      player = new OoyalaPlayer(pcode, playerDomain, options);
      // Use setupPlayerFrame() method only after release() method has been called
      playerLayout.setupPlayerFrame();
      playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);

      //Add observer to listen to fullscreen open and close events
      player.addObserver(this);

      if (!player.setEmbedCode(embedCode)) {
        Log.e(TAG, "Asset Failure");
      }
    }
  }

  private void initButtonListeners() {
    Button setFirstAssetButton = findViewById(R.id.set_first_asset);
    setFirstAssetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        reinitPlayer("E4bDRwZTE6rMB8oYrzOsuHSPz0XM0dAV");
      }
    });

    Button setSecondAssetButton = findViewById(R.id.set_second_asset);
    setSecondAssetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        reinitPlayer("92cmZwZTE613kYlorJkkSJXAw4DnFRxv");
      }
    });
  }

  private void destroyPlayer() {
    if (player != null) {
      player.destroy();
      player = null;
    }
    if (playerLayout != null) {
      playerLayout.release();
    }
    playerLayoutController = null;
  }

  private void reinitPlayer(String embedCode) {
    destroyPlayer();
    setAssetInfo(embedCode);
    completePlayerSetup(asked);
  }

  private void setAssetInfo(String embedCode) {
    this.embedCode = embedCode;
  }
}