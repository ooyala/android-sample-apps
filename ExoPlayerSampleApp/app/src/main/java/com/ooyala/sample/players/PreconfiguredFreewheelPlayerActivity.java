package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.freewheelsdk.OoyalaFreewheelManager;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.R;

import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how to use Freewheel when all configuration is stored in Ooyala Servers
 *
 * In order for Freewheel to work this simply, you need the following parameters set in your Third Party Module Parameters
 * - fw_android_ad_server
 * - fw_android_player_profile
 * 
 * And an Freewheel Ad Spot configured in Backlot with at least the following:
 * - Network ID
 * - Video Asset Network ID
 * - Site Section ID
 * 
 */
public class PreconfiguredFreewheelPlayerActivity extends Activity implements Observer {
  public final static String getName() {
    return "Preconfigured Freewheel Player";
  }
  final String TAG = this.getClass().toString();

  String EMBED = null;
  final String PCODE  = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
  final String DOMAIN = "http://ooyala.com";

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger Playbacklog= new SDCardLogcatOoyalaEventsLogger();

  protected OptimizedOoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayer player;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());
    setContentView(R.layout.player_simple_frame_layout);

    EMBED = getIntent().getExtras().getString("embed_code");

    /** DITA_START:<ph id="freewheel_preconfigured"> **/
    //Initialize the player

    // Get the SkinLayout from our layout xml
    OoyalaSkinLayout skinLayout = (OoyalaSkinLayout)findViewById(R.id.ooyalaPlayer);

    // Create the OoyalaPlayer, with some built-in UI disabled
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    Options options = new Options.Builder().setShowAdsControls(false).setShowPromoImage(false).setUseExoPlayer(true).build();
    player = new OoyalaPlayer(PCODE, domain, options);

    //Create the SkinOptions, and setup React
    SkinOptions skinOptions = new SkinOptions.Builder().build();
    OoyalaSkinLayoutController controller = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);

    player.addObserver(this);

    @SuppressWarnings("unused")
    OoyalaFreewheelManager fwManager = new OoyalaFreewheelManager(this, skinLayout, player);
    
    if (player.setEmbedCode(EMBED)) {
//      player.play();
    }
    /** DITA_END:</ph> **/

  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d(TAG, "Player Activity Paused");
    if (player != null) {
      player.suspend();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "Player Activity Resumed");
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
    Playbacklog.writeToSdcardLog(text);

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

}
