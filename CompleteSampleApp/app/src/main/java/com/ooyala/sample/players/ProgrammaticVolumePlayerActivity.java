package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.notifications.BitrateChangedNotificationInfo;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.R;

import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you can change the volume of the OoyalaPlayer programmatically.
 * Here you can see the volume being set onCreate, and changed every tick of TIME_CHANGED
 *
 * Over 10 seconds, volume should slowly increase from muted to full volume
 *
 * Please read the APIDocs for OoyalaPlayer.setVolume() for more information
 *
 */
public class ProgrammaticVolumePlayerActivity extends Activity implements Observer {
  public final static String getName() {
    return "Programmatic Volume";
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
    Options opts = new Options.Builder().setUseExoPlayer(true).build();
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), opts);
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
    player.addObserver(this);

    // You can set the volume anytime after the OoyalaPlayer is instantiated
    player.setVolume(0.0f);

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

  @Override
  public void update(Observable arg0, Object argN) {
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);

    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      //Slowly lower the volume every tick of playback.
      player.setVolume(player.getVolume() + .025f);
      return;
    }
    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text="Notification Received: " + arg1 + " - state: " + player.getState();
    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    playbacklog.writeToSdcardLog(text);

    //Generic notification handler
    Log.d(TAG, text);
  }

}
