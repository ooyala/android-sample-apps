package com.ooyala.sample.players;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

public class AudioOnlyPlayerActivity extends AbstractHookActivity {

  public static String getNameM4a() {
    return "Audio Only m4a Player";
  }

  public static String getNameHls() {
    return "Audio Only hls Player";
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_simple_layout);
    completePlayerSetup(asked);
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {
      OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
      player = new OoyalaPlayer(pcode, new PlayerDomain(domain), createPlayerOptions());
      playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
      player.addObserver(this);
    }
    if (!player.setEmbedCode(embedCode))
      Log.e(TAG, "Asset Failure");
  }

  private Options createPlayerOptions() {
    return new Options.Builder()
        .setShowNativeLearnMoreButton(false)
        .setShowPromoImage(false)
        .setUseExoPlayer(true)
        .setAudioOnly(true)
        .build();
  }
}
