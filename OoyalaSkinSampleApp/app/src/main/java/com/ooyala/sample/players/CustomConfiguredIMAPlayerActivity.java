package com.ooyala.sample.players;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

/**
 * This activity illustrates how to override IMA parameters in application code
 *
 * Supported methods:
 * imaManager.setAdUrlOverride(String)
 * imaManager.setAdTagParameters(Map<String, String>)
 */
public class CustomConfiguredIMAPlayerActivity extends Activity implements Observer {
  public final static String getName() {
    return "Custom Configured IMA Player";
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


    //Initialize the player
    OoyalaSkinLayout skinLayout = (OoyalaSkinLayout)findViewById(R.id.ooyalaPlayer);

    // Create the OoyalaPlayer, with some built-in UI disabled
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    Options options = new Options.Builder().setShowAdsControls(false).setShowPromoImage(false).build();
    player = new OoyalaPlayer(PCODE, domain, options);

    //Create the SkinOptions, and setup React
    SkinOptions skinOptions = new SkinOptions.Builder().build();
    OoyalaSkinLayoutController controller = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);

    player.addObserver(this);


    /** DITA_START:<ph id="ima_custom"> **/

	OoyalaIMAManager imaManager = new OoyalaIMAManager(player, skinLayout);
	
	// This ad tag returns a midroll video
    imaManager.setAdUrlOverride("http://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/7521029/pb_test_mid&ciu_szs=640x480&impl=s&cmsid=949&vid=FjbGRjbzp0DV_5-NtXBVo5Rgp3Sj0R5C&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]");
    // imaManager.setAdTagParameters(null);
    /** DITA_END:</ph> **/
    
    if (player.setEmbedCode(EMBED)) {
//      player.play();
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
    Playbacklog.writeToSdcardLog(text);

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

}
