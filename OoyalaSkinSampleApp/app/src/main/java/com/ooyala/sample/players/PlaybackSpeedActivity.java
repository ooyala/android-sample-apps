package com.ooyala.sample.players;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.analytics.IqConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.ssai.OoyalaSsaiManager;
import com.ooyala.sample.R;

import org.json.JSONObject;

/**
 * This activity illustrates how you can set the initial playback speed using Options {@link Options}
 * and set a playback speed at a player directly {@link OoyalaPlayer}.
 *
 * Manage the following values: {@value INITIAL_PLAYBACK_SPEED} and {@value PLAYBACK_SPEED} to
 * set the initial playback speed and a speed during the playback.
 */
public class PlaybackSpeedActivity extends AbstractHookActivity {
  private static final String TAG = PlaybackSpeedActivity.class.getSimpleName();

  private static final float INITIAL_PLAYBACK_SPEED = 2.0f;
  private static final float PLAYBACK_SPEED = 0.5f;
  private static final String SSAI_PLAYER_ID = "SSAI Android Player ID";
  private static final String AD_SCREEN_SKIN_OVERRIDES = "showControlBar";
  private static final String SKIN_OVERRIDES = "adScreen";

  private OoyalaSsaiManager ssaiManager;
  private String playerParams;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_skin_playback_speed_layout);

    retrieveExtras();
    completePlayerSetup(asked);
    initButtonListener();
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {
      // Get the SkinLayout from our layout xml
      skinLayout = findViewById(R.id.ooyalaSkin);

      boolean isSsaiUsed = isPlayerParamsDefined();
      Options options = isSsaiUsed ? createSsaiOptions() : createRegularOptions();
      SkinOptions skinOptions = isSsaiUsed ? createSsaiSkinOptions() : createRegularSkinOptions();

      // Create the OoyalaPlayer, with some built-in UI disabled
      PlayerDomain playerDomain = new PlayerDomain(domain);
      player = new OoyalaPlayer(pcode, playerDomain, options);
      player.addObserver(this);

      //Initialize player layout controller and add observer to listen to fullscreen open and close events
      playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
      playerLayoutController.addObserver(this);

      if (isSsaiUsed) {
        ssaiManager = new OoyalaSsaiManager.Builder(player)
          .usePlayerParams(playerParams)
          .build();
      }

      if (!player.setEmbedCode(embedCode)) {
        Log.e(TAG, "Asset Failure");
      }
      OoyalaIMAManager imaManager = new OoyalaIMAManager(player, skinLayout);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (ssaiManager != null) {
      ssaiManager.remove();
    }
  }

  private void retrieveExtras() {
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      playerParams = extras.getString("player_params");
    }
  }

  private void initButtonListener() {
    Button setPlaybackSpeedButton = findViewById(R.id.setPlaybackSpeedButton);
    setPlaybackSpeedButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (player != null) {
          // Set the desired playback speed here
        }
      }
    });
  }

  private Options createRegularOptions() {
    return new Options.Builder()
      .setShowNativeLearnMoreButton(false)
      .setShowPromoImage(false)
      .setUseExoPlayer(true)
      .setInitialPlaybackSpeed(INITIAL_PLAYBACK_SPEED)
      .build();
  }

  private Options createSsaiOptions() {
    // Create an IQConfiguration class
    IqConfiguration iqConfiguration = new IqConfiguration.Builder()
      .setPlayerID(SSAI_PLAYER_ID)
      .build();

    return new Options.Builder()
      .setIqConfiguration(iqConfiguration)
      .setShowNativeLearnMoreButton(false)
      .setShowPromoImage(false)
      .setUseExoPlayer(true)
      .setInitialPlaybackSpeed(INITIAL_PLAYBACK_SPEED)
      .build();
  }

  private SkinOptions createRegularSkinOptions() {
    return new SkinOptions.Builder().build();
  }

  private SkinOptions createSsaiSkinOptions() {
    JSONObject overrides = createSkinOverrides();
    SkinOptions skinOptions = new SkinOptions.Builder().setSkinOverrides(overrides).build();
    return skinOptions;
  }

  public boolean isPlayerParamsDefined() {
    return !TextUtils.isEmpty(playerParams);
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
      adScreenOverrides.put(AD_SCREEN_SKIN_OVERRIDES, true);
      overrides.put(SKIN_OVERRIDES, adScreenOverrides);
    } catch (Exception e) {
      Log.e(TAG, "Exception thrown: ", e);
    }
    return overrides;
  }
}
