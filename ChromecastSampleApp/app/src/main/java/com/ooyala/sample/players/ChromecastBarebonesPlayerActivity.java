package com.ooyala.sample.players;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.castsdk.CastManager;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

import java.util.Observable;
import java.util.Observer;

/**
 * This activity is meant to show the absolute bare minimum code you need to see video playback
 * on the Chromecast.  This will not have some key functionality for user experience, but will show
 * the simplest code for Chromecast integration with the player.
 *
 * Please check the ChromecastPlayerActivity to see a more complete integration, containing all supported
 * functionality
 */
public class ChromecastBarebonesPlayerActivity extends AppCompatActivity implements Observer{
  
  private static final String TAG = ChromecastBarebonesPlayerActivity.class.getSimpleName();
  private String embedCode;
  private String pcode;
  private String domain;
  private OoyalaPlayer player;

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger Playbacklog= new SDCardLogcatOoyalaEventsLogger();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate()");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // Get the information from the extras
    Bundle extras = getIntent().getExtras();
    embedCode = extras.getString("embedcode");
    pcode = extras.getString("pcode");
    domain = extras.getString("domain");

    // Initialize Ooyala Player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    PlayerDomain playerDomain = new PlayerDomain(domain);
    player = new OoyalaPlayer(pcode, playerDomain);
    OoyalaPlayerLayoutController playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);

    //Create a CastManager, and connect to the OoyalaPlayer
    CastManager castManager = CastManager.getCastManager();
    castManager.registerWithOoyalaPlayer(player);

    //Observe, set the embed code, and play
    player.addObserver(this);
    player.setEmbedCode(embedCode);
    //Uncomment for Auto-Play
    //player.play();
  }

  @Override
  public void onPause() {
    Log.d(TAG, "onPause()");
    if (player != null) {
      player.suspend();
    }
    super.onPause();
  }
  
  @Override
  protected void onStart() {
    Log.d(TAG, "onStart()");
    super.onStart();
  }

  @Override
  protected void onDestroy() {
    Log.d(TAG, "onDestroy()");

    //When we destroy, clean up the Singleton CastManager by removing our OoyalaPlayer.
    CastManager.getCastManager().deregisterFromOoyalaPlayer();
    player = null;
    super.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (player != null) {
      player.resume();
    }
  }

  /**
   * Populate the ActionBar Menu with the Cast button
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.d(TAG, "onCreateOptionsMenu()");
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.main, menu);
    CastManager.getVideoCastManager().addMediaRouterButton(menu, R.id.media_route_menu_item);
    return true;
  }

  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable arg0, Object argN) {
    if (arg0 != player) {
      return;
    }

    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
      final String msg = "Error Event Received";
      if (player != null && player.getError() != null) {
        Log.e(TAG, msg, player.getError());
      }
      else {
        Log.e(TAG, msg);
      }
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text="Notification Received: " + arg1 + " - state: " + player.getState();
    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    Playbacklog.writeToSdcardLog(text);

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }


}
