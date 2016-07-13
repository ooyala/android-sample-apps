package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.configuration.VisualOnConfiguration;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you enable SecurePlayer video playback.
 * This activity contains code to generate local Ooyala Player Tokens, to more easily test DRM'ed
 * assets
 *
 * While this Player is not actively used in the SecurePlayerSampleApp, you can modify the
 * ListActivity to use this Player instead of the default.
 */
public class SecurePlayerOPTPlayerActivity extends Activity implements Observer, EmbedTokenGenerator {
  final String TAG = this.getClass().toString();

  String EMBED = null;
  String PCODE = null;
  String DOMAIN = null;

  /*
   * The API Key and Secret should not be saved inside your applciation (even in git!).
   * However, for debugging you can use them to locally generate Ooyala Player Tokens.
   */
  private final String APIKEY = "Use this for testing, don't keep your secret in the application";
  private final String SECRET = "Use this for testing, don't keep your secret in the application";

  // An account ID, if you are using Concurrent Streams or Entitlements
  private final String ACCOUNT_ID = "accountID";

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

    OoyalaPlayer.enableCustomHLSPlayer = true;
    OoyalaPlayer.enableCustomPlayreadyPlayer = true;

    // Mandatory - You need to get an OPID for your application (setSessionId)
    // and need to reference the version string for your specific SecurePlayer Libraries.
    // Talk to your CSM or Technical Support for more information
    VisualOnConfiguration voOpts = new VisualOnConfiguration.Builder().setSessionId("session").setVersion("GENERAL_ANDR_VOP_PROB_RC_03_08_02_0000").build();
    Options options = new Options.Builder().setVisualOnConfiguration(voOpts).build();

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), this, options);

    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
    player.addObserver(this);

    if (player.setEmbedCode(EMBED)) {
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
      final String msg = "Error Event Received";
      if (player != null && player.getError() != null) {
        Log.e(TAG, msg, player.getError());
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
      if (ec.equals("")) embedCodesString += ",";
      embedCodesString += ec;
    }

    HashMap<String, String> params = new HashMap<String, String>();
    params.put("account_id", ACCOUNT_ID);

    /* Uncommenting this will bypass all syndication rules on your asset
       This will not work unless you have a working API Key and Secret.
       This is one reason why you shouldn't keep the Secret in your app/source control */
//     params.put("override_syndication_group", "override_all_synd_groups");

    String uri = "/sas/embed_token/" + PCODE + "/" + embedCodesString;

    EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

    URL tokenUrl = urlGen.secureURL("http://player.ooyala.com", uri, params);

    callback.setEmbedToken(tokenUrl.toString());
  }
}
