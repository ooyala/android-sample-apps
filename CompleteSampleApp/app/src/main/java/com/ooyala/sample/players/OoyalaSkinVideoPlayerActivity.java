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

import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you can play basicPlayback Video
 * you can also play Ooyala and VAST advertisements programmatically
 * through the SDK
 *
 */
public class OoyalaSkinVideoPlayerActivity extends Activity implements Observer {
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

    //Initialize the player
//    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaSkin);
//    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN));
//    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
//    player.addObserver(this);

    OoyalaSkinLayout skinLayout = (OoyalaSkinLayout)findViewById(R.id.ooyalaSkin);
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    Options options = new Options.Builder().setShowAdsControls(false)
            .setShowCuePoints(true).setShowPromoImage(false)
            .setPreloadContent(true).build();
    player = new OoyalaPlayer(PCODE, domain, options);
    skinLayout.setupViews(getApplication(), player);
//    playerLayoutController = new OoyalaSkinLayoutController(context,skinLayout, player);
//    player.addObserver(this);


    if (player.setEmbedCode(EMBED)) {
      //player.play();
    }
    else {
      Log.e(TAG, "Asset Failure");
    }
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
