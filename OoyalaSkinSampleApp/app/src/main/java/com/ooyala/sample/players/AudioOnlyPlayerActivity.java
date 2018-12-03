package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.R;
import org.json.JSONObject;

public class AudioOnlyPlayerActivity extends AbstractHookActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_skin_audio_layout);
    completePlayerSetup(asked);
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {
      skinLayout = findViewById(R.id.ooyalaSkin);
      player = new OoyalaPlayer(pcode, new PlayerDomain(domain), createPlayerOptions());
      playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, createSkinOptions());
      playerLayoutController.addObserver(this);
      player.addObserver(this);

      if (player.setEmbedCode(embedCode)) {
        if (autoPlay)
          player.play();
      } else {
        Log.e(TAG, "Asset Failure");
      }
    }
  }

  private SkinOptions createSkinOptions() {
    return new SkinOptions.Builder().setSkinOverrides(createSkinOverrides()).build();
  }

  private Options createPlayerOptions() {
    return new Options.Builder()
        .setShowNativeLearnMoreButton(false)
        .setShowPromoImage(false)
        .setUseExoPlayer(true)
        .setAudioOnly(true) // This is crucial
        .build();
  }

  private JSONObject createSkinOverrides() {
    try {
      return new JSONObject();
    } catch (Exception e) {
      throw new RuntimeException("Exception while creating skin overrides", e);
    }
  }
}