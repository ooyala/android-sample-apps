package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.AudioOnlyPlayerInfo;
import org.json.JSONObject;

public class AudioOnlyPlayerActivity extends AbstractHookActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_skin_simple_layout);
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
    Options.Builder builder = new Options.Builder()
        .setShowNativeLearnMoreButton(false)
        .setShowPromoImage(false)
        .setUseExoPlayer(true)
        .setPlayerInfo(new AudioOnlyPlayerInfo());

    return builder.build();
  }

  private JSONObject createSkinOverrides() {
    try {
      return new JSONObject()
          .put("upNext", new JSONObject().put("showUpNext", false))
          .put("endScreen", new JSONObject().put("screenToShowOnEnd", "default"));
    } catch (Exception e) {
      throw new RuntimeException("Exception while creating skin overrides", e);
    }
  }
}