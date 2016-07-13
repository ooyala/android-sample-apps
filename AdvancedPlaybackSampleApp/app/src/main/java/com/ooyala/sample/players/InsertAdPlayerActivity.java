package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ooyala.android.ads.ooyala.OoyalaAdSpot;
import com.ooyala.android.OoyalaManagedAdsPlugin;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.ads.vast.VASTAdSpot;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.performance.PerformanceMonitor;
import com.ooyala.android.performance.PerformanceMonitorBuilder;
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
  final String PERFORMANCE_MONITOR_TAG = "MONITOR_" + TAG;

  String EMBED = null;
  String PCODE = null;
  String DOMAIN = null;

  protected OoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayer player;

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger playbacklog = new SDCardLogcatOoyalaEventsLogger();

  private PerformanceMonitor performanceMonitor;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());
    setContentView(R.layout.player_double_button_layout);

    EMBED = getIntent().getExtras().getString("embed_code");
    PCODE = getIntent().getExtras().getString("pcode");
    DOMAIN = getIntent().getExtras().getString("domain");

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    Options opts = new Options.Builder().build();

    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), opts);
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
    player.addObserver(this);

//  Set up performance monitoring to watch standard events and ads events.
    performanceMonitor = PerformanceMonitorBuilder.getStandardAdsMonitor(player);

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
        OoyalaAdSpot ooyalaAd = new OoyalaAdSpot(player.getPlayheadTime(), null, null, "Zvcmp0ZDqD6xnQVH8ZhWlxH9L9bMGDDg");
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

    // print collected performance statistics
    Log.d(PERFORMANCE_MONITOR_TAG, performanceMonitor.buildStatisticsSnapshot().generateReport());
    performanceMonitor.destroy();
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

    // pass player's notifications to performance monitor
    performanceMonitor.update(arg0, argN);

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
