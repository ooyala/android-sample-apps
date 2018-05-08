package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.player.exoplayer.upstream.OoyalaDrmHttpDataSource;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.EmbeddedSecureURLGenerator;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you use Ooyala Player Token.
 * Ooyala Player Token can also be used in conjunction with the following security mechanisms
 * 1) Device Management,
 * 2) Concurrent Streams,
 * 3) Entitlements, and
 * 4) Stream Takedown
 *
 * This activity will NOT Playback any video.  You will need to:
 *  1) provide your own embed code, restricted with Ooyala Player Token
 *  2) provide your own PCODE, which owns the embed code
 *  3) have your API Key and Secret, which correlate to a user from the provider
 *
 * To play OPT-enabled videos, you must implement the EmbedTokenGenerator interface
 */
public class OoyalaPlayerTokenPlayerActivity extends Activity implements Observer, EmbedTokenGenerator {
  final String TAG = this.getClass().toString();

  String EMBED = null;
  String PCODE = null;
  String DOMAIN = null;
  Boolean AUTOPLAY = false;
  private String ACCOUNT_ID = "";

  /*
   * The API Key and Secret should not be saved inside your applciation (even in git!).
   * However, for debugging you can use them to locally generate Ooyala Player Tokens.
   */
  private String APIKEY = "BjcWYyOu1KK2DiKOkF41Z2k0X57l.0I-V4";
  private String SECRET = "1ysC3V-g8m9V3QoGJwRa_fXcfi_G2ZV778m17pux";

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
    PCODE = getIntent().getExtras().getString("pcode");
    DOMAIN = getIntent().getExtras().getString("domain");
    APIKEY =getIntent().getExtras().getString("apikey");
    SECRET = getIntent().getExtras().getString("secret");
    ACCOUNT_ID = getIntent().getExtras().getString("accountid");
    AUTOPLAY = getIntent().getExtras().getBoolean("autoPlay");

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

    Options options = new Options.Builder().setUseExoPlayer(true).build();
    //Need to pass `this` as the embedTokenGenerator
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), this, options);
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
    player.addObserver(this);

    if (player.setEmbedCode(EMBED)) {
      if(AUTOPLAY)
      player.play();
    }
    else {
      Log.e(TAG, "Asset Failure");
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
    if (arg0 != player) {
      return;
    }

    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
      String msg = "Error Event Received";
      OoyalaException exception = player.getError();
      if (player != null && exception != null) {
        Log.e(TAG, msg + ": " + exception.getMessage(), exception);
      }
      else {
        Log.e(TAG, msg);
      }
      return;
    }

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

    // Uncommenting this will bypass all syndication rules on your asset
    // This will not work unless you have a working API Key and Secret.
    // This is one reason why you shouldn't keep the Secret in your app/source control
    // params.put("override_syndication_group", "override_all_synd_groups"); 

    String uri = "/sas/embed_token/" + PCODE + "/" + embedCodesString;

    EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

    URL tokenUrl  = urlGen.secureURL("http://player.ooyala.com", uri, params);

    callback.setEmbedToken(tokenUrl.toString());
  }
}
