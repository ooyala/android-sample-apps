package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.OfflineVideo;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.DemoApplication;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.PlayerSelectionOption;

import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class OoyalaSkinOPTPlayerActivity extends Activity
  implements Observer, DefaultHardwareBackBtnHandler, EmbedTokenGenerator {

  private static final String TAG = OoyalaSkinOPTPlayerActivity.class.getSimpleName();
  private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
  boolean writePermission = false;

  String EMBED = null;
  String PCODE  = null;
  String DOMAIN = null;

  private String APIKEY = "";
  private String SECRET = "";

  // An account ID, if you are using Concurrent Streams or Entitlements
  private String ACCOUNT_ID = "";

  SDCardLogcatOoyalaEventsLogger Playbacklog= new SDCardLogcatOoyalaEventsLogger();

  protected OoyalaSkinLayout skinLayout;
  protected OoyalaSkinLayoutController playerLayoutController;
  protected OoyalaPlayer player;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    } else {
      writePermission= true;
    }
    setTitle(getIntent().getExtras().getString("selection_name"));
    setContentView(R.layout.player_skin_simple_layout);
    EMBED = getIntent().getExtras().getString("embed_code");
    PCODE = getIntent().getExtras().getString("pcode");
    DOMAIN = getIntent().getExtras().getString("domain");
    APIKEY = getIntent().getExtras().getString("api_key");
    SECRET = getIntent().getExtras().getString("secret_key");
    ACCOUNT_ID = getIntent().getExtras().getString("account_id");

    initializePlayer();

    final int playbackType = getIntent().getExtras().getInt("playback_type");
    switch (playbackType) {
      case PlayerSelectionOption.ONLINE_PLAYBACK:
        tryPlayOnline();
        break;
      case PlayerSelectionOption.OFFLINE_ONLINE_PLAYBACK:
        try {
          tryPlayOnlineOffline();
        } catch (Exception ex) {
          handleError("Asset Failure");
        }
        break;
      case PlayerSelectionOption.OFFLINE_EMBED_CODE_PLAYBACK:
      case PlayerSelectionOption.OFFLINE_URL_PLAYBACK:
        tryPlayOffline();
        break;
      default:
        handleError("Playback type is not defined");
        break;
    }
  }

  private void tryPlayOnline() {
    try {
      playOnline();
    } catch (Exception error) {
      Log.e(TAG, error.getMessage());
    }
  }

  private void tryPlayOffline() {
    try {
      playOffline();
    } catch (Exception error) {
      handleError(error.getMessage());
    }
  }

  private void tryPlayOnlineOffline() throws Exception {
    // Online playback has higher priority than offline playback
    try {
      playOnline();
    } catch (Exception error) {
      playOffline();
    }
  }

  private void initializePlayer() {
    // Get the SkinLayout from our layout xml
    skinLayout = findViewById(R.id.ooyalaSkin);

    // Create the OoyalaPlayer, with some built-in UI disabled
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    Options options = new Options.Builder()
        .setShowPromoImage(false)
        .setShowNativeLearnMoreButton(false)
        .setUseExoPlayer(true)
        .build();
    player = new OoyalaPlayer(PCODE, domain, this, options);
    player.addObserver(this);

    //Create the SkinOptions, and setup React
    JSONObject overrides = createSkinOverrides();
    SkinOptions skinOptions = new SkinOptions.Builder().setSkinOverrides(overrides).build();
    playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
  }

  private void playOnline() throws Exception {
    if (player.setEmbedCode(EMBED)) {
      //Uncomment for autoplay
      //player.play();
    } else {
      throw new Exception("Asset Failure");
    }
  }

  private void playOffline() throws Exception{
    File folder = new File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_MOVIES), EMBED);
    OfflineVideo ov = OfflineVideo.getVideo(this, folder, ((DemoApplication) getApplication()).getDownloadCache(), EMBED);

    if (player.setUnbundledVideo(ov)) {
      //Uncomment for autoplay
      //player.play();
    } else {
      throw new Exception("No downloaded files for playback");
    }
  }

  private void handleError(String errorMessage) {
    Log.e(TAG, errorMessage);
    Toast.makeText(OoyalaSkinOPTPlayerActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    finish();
  }

  /** Start DefaultHardwareBackBtnHandler **/
  @Override
  public void invokeDefaultOnBackPressed() {
    super.onBackPressed();
  }
  /** End DefaultHardwareBackBtnHandler **/

  /** Start Activity methods for Skin **/
  @Override
  public void onStart() {
    super.onStart();
    if (null != player) {
      player.resume();
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    if (player != null) {
      player.suspend();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (playerLayoutController != null) {
      playerLayoutController.onPause();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (playerLayoutController != null) {
      playerLayoutController.onResume(this, this);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    destroyPlayer();
  }

  private void destroyPlayer() {
    if (player != null) {
      player.destroy();
      player = null;
    }
    if (skinLayout != null) {
      skinLayout.release();
    }
    if (playerLayoutController != null) {
      playerLayoutController.destroy();
      playerLayoutController = null;
    }
  }


  @Override
  public void onBackPressed() {
    if (playerLayoutController != null) {
      playerLayoutController.onBackPressed();
    } else {
      super.onBackPressed();
    }
  }

  /** End Activity methods for Skin **/

  /**
   * Create skin overrides to show up in the skin.
   * Default commented. Uncomment to show changes to the start screen.
   * @return the overrides to apply to the skin.json in the assets folder
   */
  private JSONObject createSkinOverrides() {
    JSONObject overrides = new JSONObject();
//    JSONObject startScreenOverrides = new JSONObject();
//    JSONObject playIconStyleOverrides = new JSONObject();
//    try {
//      playIconStyleOverrides.put("color", "red");
//      startScreenOverrides.put("playButtonPosition", "bottomLeft");
//      startScreenOverrides.put("playIconStyle", playIconStyleOverrides);
//      overrides.put("startScreen", startScreenOverrides);
//    } catch (Exception e) {
//      Log.e(TAG, "Exception Thrown", e);
//    }
    return overrides;
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
      return;
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text="Notification Received: " + arg1 + " - state: " + player.getState();
    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    Playbacklog.writeToSdcardLog(text);

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
    if (writePermission) {
      Log.d(TAG, "Writing log to SD card");
      // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
      Playbacklog.writeToSdcardLog(text);
    }
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
      if (ec.equals("")) embedCodesString += ",";
      embedCodesString += ec;
    }

    HashMap<String, String> params = new HashMap<String, String>();
    params.put("account_id", ACCOUNT_ID);

    String uri = "/sas/embed_token/" + PCODE + "/" + embedCodesString;

    EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

    URL tokenUrl = urlGen.secureURL("http://player.ooyala.com", uri, params);

    callback.setEmbedToken(tokenUrl.toString());
  }

}
