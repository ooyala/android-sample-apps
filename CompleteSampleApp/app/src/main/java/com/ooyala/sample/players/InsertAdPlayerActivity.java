package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ooyala.android.OoyalaAdSpot;
import com.ooyala.android.OoyalaManagedAdsPlugin;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.ads.vast.VASTAdSpot;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you can insert Ooyala and VAST advertisements programmatically
 * through the SDK
 *
 * If you want to play an advertisement immediately, you can set the time of your Ad Spot to
 * the current playhead time
 *
 *
 */
public class InsertAdPlayerActivity extends Activity implements Observer {
  public final static String getName() {
    return "Insert Ad at Runtime";
  }
  final String TAG = this.getClass().toString();

  String EMBED = null;
  final String PCODE  = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
  final String DOMAIN = "http://ooyala.com";

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
    setContentView(R.layout.player_double_button_layout);

    EMBED = getIntent().getExtras().getString("embed_code");

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN));
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
    player.addObserver(this);

    if (player.setEmbedCode(EMBED)) {
      player.play();
    }

    /** DITA_START:<ph id="insert_ad_vast"> **/
    //Setup the left button, which will immediately insert a VAST advertisement
    Button leftButton = (Button) findViewById(R.id.doubleLeftButton);
    leftButton.setText("Insert VAST Ad");
    leftButton.setOnClickListener( new OnClickListener() {

      @Override
      public void onClick(View v) {
        OoyalaManagedAdsPlugin plugin = player.getManagedAdsPlugin();
        try {
          VASTAdSpot vastAd = new VASTAdSpot(player.getPlayheadTime(), player.getDuration(), null, null, new URL("http://xd-team.ooyala.com.s3.amazonaws.com/ads/VastAd_Preroll.xml"));
          plugin.insertAd(vastAd);
        } catch (MalformedURLException e) {
          Log.e(TAG, "VAST Ad Tag was malformed");
          e.printStackTrace();
        }
      }
    });
    /** DITA_END:</ph> **/

    //Setup the right button, which will immediately insert an Ooyala advertisement
    Button rightButton = (Button) findViewById(R.id.doubleRightButton);
    rightButton.setText("Insert Ooyala Ad");
    rightButton.setOnClickListener( new OnClickListener() {

      @Override
      public void onClick(View v) {
        OoyalaManagedAdsPlugin plugin = player.getManagedAdsPlugin();
        OoyalaAdSpot ooyalaAd = new OoyalaAdSpot(player.getPlayheadTime(), null, null, "Zvcmp0ZDqD6xnQVH8ZhWlxH9L9bMGDDg", player.getOoyalaAPIClient());
        plugin.insertAd(ooyalaAd);
      }
    });
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
