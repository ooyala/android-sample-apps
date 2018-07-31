package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.analytics.IqConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.ssai.OoyalaSsaiManager;
import com.ooyala.sample.R;

import org.json.JSONObject;


public class SsaiPlayerActivity extends AbstractHookActivity {
  private OoyalaSsaiManager ssaiManager;
  private String playerParams = "";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      playerParams = extras.getString("player_params");
    }
    setContentView(R.layout.player_skin_simple_layout);
    completePlayerSetup(asked);
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {
      // Get the SkinLayout from our layout xml
      skinLayout = (OoyalaSkinLayout) findViewById(R.id.ooyalaSkin);
      // Create the OoyalaPlayer, with some built-in UI disabled
      PlayerDomain playerDomain = new PlayerDomain(domain);
      // Create an IQConfiguration class
      IqConfiguration iqConfiguration = new IqConfiguration.Builder().setPlayerID("SSAI Android Player ID").build();

      Options options = new Options.Builder().setIqConfiguration(iqConfiguration).setShowNativeLearnMoreButton(false).setShowPromoImage(false).setUseExoPlayer(true).build();
      player = new OoyalaPlayer(pcode, playerDomain, options);
      //Create the SkinOptions, and setup React
      JSONObject overrides = createSkinOverrides();
      SkinOptions skinOptions = new SkinOptions.Builder().setSkinOverrides(overrides).build();
      playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
      //Add observer to listen to fullscreen open and close events
      playerLayoutController.addObserver(this);
      player.addObserver(this);

      ssaiManager = new OoyalaSsaiManager.Builder(player).usePlayerParams(playerParams).build();

      if (player.setEmbedCode(embedCode)) {
      } else {
        Log.e(TAG, "Asset Failure");
      }
    }
  }

  /**
   * Create skin overrides to show up in the skin.
   *
   * @return the overrides to apply to the skin.json in the assets folder
   */
  private JSONObject createSkinOverrides() {
    JSONObject overrides = new JSONObject();
    JSONObject adScreenOverrides = new JSONObject();
    try {
      adScreenOverrides.put("showControlBar", true);
      overrides.put("adScreen", adScreenOverrides);
    } catch (Exception e) {
      Log.e(TAG, "Exception Thrown", e);
    }
    return overrides;
  }

  @Override
  protected void onDestroy() {
    ssaiManager.remove();
    super.onDestroy();
  }
}