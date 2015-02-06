package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.CustomPlayerControls;

import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you can implement Custom controls for the OoyalaPlayer
 *
 * The following files were slightly modified from the DefaultControlsSource provided with the
 * Ooyala SDK:
 *   CustomPlayerControls (was DefaultOoyalaPlayerInlineControls)
 *   CuePointsSeekBar
 *   AbstractDefaultOoyalaPlayerControls
 *   Images
 *
 *  This example was made with Ooyala SDK 3.4.0 source, but is still a good example of how
 *  the default controls can be overridden.
 */
public class CustomControlsPlayerActivity extends Activity implements Observer {
  public final static String getName() {
    return "Custom Controls";
  }
  final String TAG = this.getClass().toString();

  String EMBED = null;
  final String PCODE  = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
  final String DOMAIN = "http://ooyala.com";

  protected OoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayer player;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());
    setContentView(R.layout.player_simple_layout);
    EMBED = getIntent().getExtras().getString("embed_code");

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, PCODE, new PlayerDomain(DOMAIN));
    player = playerLayoutController.getPlayer();

    //Set the controls to use for Inline Control style.
    playerLayoutController.setInlineControls(new CustomPlayerControls(player,playerLayout));
    player.addObserver(this);

    if (player.setEmbedCode(EMBED)) {
      player.play();
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
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION) {
      return;
    }
    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

}
