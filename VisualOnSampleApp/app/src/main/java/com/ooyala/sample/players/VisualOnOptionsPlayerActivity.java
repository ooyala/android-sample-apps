package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.configuration.VisualOnConfiguration;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you enable VisualOn video playback.
 *
 * This application will playback even if VisualOn Libraries were not properly added.
 * You will know VisualOn is being used if you see logs with "VisualOnStreamPlayer" in it
 *
 */
public class VisualOnOptionsPlayerActivity extends Activity implements Observer {
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
    setTitle(getIntent().getExtras().getString("selection_name"));
    setContentView(R.layout.player_simple_layout);
    EMBED = getIntent().getExtras().getString("embed_code");

    //get the VisualOn configuration information
    VisualOnConfiguration voConfig = getVisualOnConfiguration();
    Options options = new Options.Builder().setVisualOnConfiguration(voConfig).build();

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);

    OoyalaPlayer.enableCustomHLSPlayer = true;
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
    player.addObserver(this);

    if (player.setEmbedCode(EMBED)) {
      player.play();
    }
    else {
      Log.e(TAG, "Asset Failure");
    }
  }

  private VisualOnConfiguration getVisualOnConfiguration() {
    VisualOnConfiguration.Builder voConfigBuilder = new VisualOnConfiguration.Builder();

    // The target bitrate to start video playback
    voConfigBuilder.setInitialBitrate(600);

    // The upper and lower bounds of bitrates that should be selected
    voConfigBuilder.setLowerBitrateThreshold(200).setUpperBitrateThreshold(1400);

    // The amount of video in ms to buffer when video is initialized or seeked
    voConfigBuilder.setInitialBufferingTime(10000);

    // The maximum buffer size to be downloaded during video playback
    voConfigBuilder.setMaxBufferingTime(10000);

    // The amount of video to buffer if buffer underruns
    voConfigBuilder.setPlaybackBufferingTime(2000);

    // Disable the check for the correct version of VisualOn Libraries with the Ooyala SDK
    voConfigBuilder.setDisableLibraryVersionChecks(true);

    return voConfigBuilder.build();
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
