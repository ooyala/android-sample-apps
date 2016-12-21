package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerControls;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.CustomOverlay;
import com.ooyala.sample.utils.CustomPlayerControls;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you can implement Custom Overlays for the OoyalaPlayer
 *
 * The class CustomOverlay is inserted into the player using setInlineOverlay
 */
public class CustomOverlayPlayerActivity extends Activity implements Observer {
  public final static String getName() {
    return "Custom Overlay";
  }
  final String TAG = this.getClass().toString();

  String EMBED = null;
  String PCODE = null;
  String DOMAIN = null;

  protected OoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayer player;

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger playbacklog = new SDCardLogcatOoyalaEventsLogger();

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());
    setContentView(R.layout.player_simple_layout);
    EMBED = getIntent().getExtras().getString("embed_code");
    PCODE = getIntent().getExtras().getString("pcode");
    DOMAIN = getIntent().getExtras().getString("domain");

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

    Options options = new Options.Builder().setUseExoPlayer(true).build();
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);

    //Insert the new overlay into the LayoutController
    CustomOverlay overlay = new CustomOverlay(this, playerLayout);
    playerLayoutController.setInlineOverlay(overlay);
    playerLayoutController.setFullscreenOverlay(overlay);
    player.addObserver(this);

    if (player.setEmbedCode(EMBED)) {
      //Uncomment for Auto-Play
      //player.play();
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
  public void update(Observable arg0, Object argN) {
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text="Notification Received: " + arg1 + " - state: " + player.getState();
    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    playbacklog.writeToSdcardLog(text);

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

}
