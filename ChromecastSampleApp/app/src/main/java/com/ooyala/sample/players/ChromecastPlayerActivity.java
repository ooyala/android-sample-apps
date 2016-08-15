package com.ooyala.sample.players;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.utils.Utils;
import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.castsdk.CastManager;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

import com.ooyala.sample.utils.CastViewManager;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * This activity demonstrates all functionality supported by the Ooyala Cast Integration.
 *
 * This includes, but is not limited to:
 * Multi video playback
 * OPT and OPT-related content protection
 * CastViews - views that display in the player during Cast playback
 *   Updating of Cast View based on the state of the player
 * Responding to hardware volume buttons
 * Notifications support
 */
public class ChromecastPlayerActivity extends AppCompatActivity implements EmbedTokenGenerator, Observer{
  
  private static final String TAG = ChromecastPlayerActivity.class.getSimpleName();
  private static final double DEFAULT_VOLUME_INCREMENT = 0.05;
  private String embedCode;
  private String embedCode2;
  private String pcode;
  private String domain;
  private OoyalaPlayer player;
  private CastViewManager castViewManager;
  private final String ACCOUNT_ID = "accountID";

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger Playbacklog= new SDCardLogcatOoyalaEventsLogger();

  /*
   * The API Key and Secret should not be saved inside your applciation (even in git!).
   * However, for debugging you can use them to locally generate Ooyala Player Tokens.
   */
  private final String APIKEY = "fill me in";
  private final String SECRET = "fill me in";

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate()");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // onClick of a DefaultMiniController only provides an embedcode through the extras
    Bundle extras = getIntent().getExtras();
    if (extras.containsKey(VideoCastManager.EXTRA_MEDIA)) {
      Bundle mediaInfoBundle = extras.getBundle(VideoCastManager.EXTRA_MEDIA);
      MediaInfo mediaInfo = Utils.bundleToMediaInfo(mediaInfoBundle);
      embedCode = mediaInfo.getContentId();
      JSONObject json = mediaInfo.getCustomData();
      try {
        pcode = json.getString("pcode");
        domain = json.getString("domain");
      } catch(Exception e) {
        pcode = "FoeG863GnBL4IhhlFC1Q2jqbkH9m";
        domain = "http://www.ooyala.com";
      }
    } else {
      embedCode = extras.getString("embedcode");
      embedCode2 = extras.getString("embedcode2");
      pcode = extras.getString("pcode");
      domain = extras.getString("domain");
    }

    // Initialize Ooyala Player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    PlayerDomain playerDomain = new PlayerDomain(domain);
    player = new OoyalaPlayer(pcode, playerDomain, this, null);
    OoyalaPlayerLayoutController playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);

    //Create a CastManager, and connect to the OoyalaPlayer
    CastManager castManager = CastManager.getCastManager();
    castManager.registerWithOoyalaPlayer(player);
    castViewManager = new CastViewManager(this, castManager);

    //Observe, set the embed code, and play
    player.addObserver(this);
    play( embedCode );
  }

  private void play( String ec ) {
    player.setEmbedCode(ec);
    // Uncomment for Auto-Play
    //player.play();
  }

  @Override
  public void onPause() {
    Log.d(TAG, "onPause()");
    if (player != null) {
      player.suspend();
    }
    //onPause and onResume, call CCL code to support Cast Notifications
    CastManager.getCastManager().onPause();
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
    //onPause and onResume, call CCL code to support Cast Notifications
    CastManager.getCastManager().onResume();
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

 
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (CastManager.getVideoCastManager() != null) {
      if (CastManager.getVideoCastManager().onDispatchVolumeKeyEvent(event, DEFAULT_VOLUME_INCREMENT)) {
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  private void onVolumeChange(double volumeIncrement) {
    try {
      Log.d(TAG, "Increase DeviceVolume: " + volumeIncrement);
      CastManager.getVideoCastManager().adjustDeviceVolume(volumeIncrement);
    } catch (Exception e) {
      Log.e(TAG, "onVolumeChange() Failed to change volume", e);
    }
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

    if (arg1 == OoyalaPlayer.CURRENT_ITEM_CHANGED_NOTIFICATION_NAME) {
      castViewManager.configureCastView(player.getCurrentItem());
    } else if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
      final String msg = "Error Event Received";
      if (player != null && player.getError() != null) {
        Log.e(TAG, msg, player.getError());
      }
      else {
        Log.e(TAG, msg);
      }
    }

    if (arg1 == OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME) {
      if (player.isInCastMode()) {
        OoyalaPlayer.State state =  player.getState();
        castViewManager.updateCastState(this, state);
      }
    }

    if( arg1 == OoyalaPlayer.PLAY_COMPLETED_NOTIFICATION_NAME && embedCode2 != null ) {
      play( embedCode2 );
      embedCode2 = null;
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text="Notification Received: " + arg1 + " - state: " + player.getState();
    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    Playbacklog.writeToSdcardLog(text);

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

  /*
    * Get the Ooyala Player Token to play the embed code.
    * This should contact your servers to generate the OPT server-side.
    * For debugging, you can use Ooyala's EmbeddedSecureURLGenerator to create local embed tokens
    */
  @Override
  public void getTokenForEmbedCodes(List<String> embedCodes,
                                    EmbedTokenGeneratorCallback callback) {
    String embedCodesString = "";
    for (String ec : embedCodes) {
      if(ec.equals("")) embedCodesString += ",";
      embedCodesString += ec;
    }

    HashMap<String, String> params = new HashMap<String, String>();
    params.put("account_id", ACCOUNT_ID);

    String uri = "/sas/embed_token/" + pcode + "/" + embedCodesString;

    EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

    URL tokenUrl  = urlGen.secureURL("http://player.ooyala.com", uri, params);

    callback.setEmbedToken(tokenUrl.toString());
  }

}
