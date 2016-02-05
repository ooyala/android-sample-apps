package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ooyalaskinsampleapp.R;
import com.ooyala.android.ooyalaskinsdk.OoyalaSkinLayout;
import com.ooyala.android.ooyalaskinsdk.OoyalaSkinLayoutController;
import com.ooyala.android.ooyalaskinsdk.configuration.SkinOptions;

import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you can play basicPlayback Video
 * you can also play Ooyala and VAST advertisements programmatically
 * through the SDK
 *
 */
public class OoyalaSkinPlayerActivity extends Activity implements Observer {
  final String TAG = this.getClass().toString();

  String EMBED = null;
  final String PCODE  = "c0cTkxOqALQviQIGAHWY5hP0q9gU";
  final String DOMAIN = "http://ooyala.com";

  protected OoyalaSkinLayoutController playerLayoutController;
  protected OoyalaPlayer player;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getIntent().getExtras().getString("selection_name"));
    setContentView(R.layout.player_simple_layout);
    EMBED = getIntent().getExtras().getString("embed_code");


    // Get the SkinLayout from our layout xml
    OoyalaSkinLayout skinLayout = (OoyalaSkinLayout)findViewById(R.id.ooyalaSkin);

    // Create the OoyalaPlayer, with some built-in UI disabled
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    Options options = new Options.Builder().setShowAdsControls(false).setShowPromoImage(false).build();
    player = new OoyalaPlayer(PCODE, domain, options);

    //Create the SkinOptions, and setup React
    JSONObject overrides = createSkinOverrides();
    SkinOptions skinOptions = new SkinOptions.Builder().setSkinOverrides(overrides).setEnableReactJSServer(true).build();
    skinLayout.initializeSkin(getApplication(), player, skinOptions);

    if (player.setEmbedCode(EMBED)) {
      //Uncomment for autoplay
      //player.play();
    }
    else {
      Log.e(TAG, "Asset Failure");
    }
  }

  /**
   * Create skin overrides to show up in the skin.
   * Default commented. Uncomment to show changes to the start screen.
   * @return the overrides to apply to the skin.json in the assets folder
   */
  private JSONObject createSkinOverrides() {
    JSONObject overrides = new JSONObject();
//    JSONObject startScreenOverrides = new JSONObject();
//    JSONObject playIconStyleOverrides = new JSONObject();
//    try {
//      playIconStyleOverrides.put("color", "red");
//      startScreenOverrides.put("playButtonPosition", "bottomLeft");
//      startScreenOverrides.put("playIconStyle", playIconStyleOverrides);
//      overrides.put("startScreen", startScreenOverrides);
//    } catch (Exception e) {
//      Log.e(TAG, "Exception Thrown", e);
//    }
    return overrides;
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "Player Activity Stopped");
    if (player != null) {
      player.suspend();
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.d(TAG, "Player Activity Restarted");
    if (player != null) {
      player.resume();
    }
  }

  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable arg0, Object arg1) {
    if (arg0 != player) {
      return;
    }

    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION) {
      return;
    }

    if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION) {
      final String msg = "Error Event Received";
      if (player != null && player.getError() != null) {
        Log.e(TAG, msg, player.getError());
      }
      else {
        Log.e(TAG, msg);
      }
      return;
    }

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

}
