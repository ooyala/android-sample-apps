package com.ooyala.chromecastv3sampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.castsdkv3.CastManager;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ChromecastPlayerActivity extends AppCompatActivity implements Observer, EmbedTokenGenerator {
  private static final String TAG = ChromecastPlayerActivity.class.getSimpleName();

  /*
   * The API Key and Secret should not be saved inside your applciation (even in git!).
   * However, for debugging you can use them to locally generate Ooyala Player Tokens.
   */
  private final String APIKEY = "fill me in";
  private final String SECRET = "fill me in";
  private final String ACCOUNT_ID = "accountID";

  CastManager castManager;
  CastViewManager castViewManager;

  private String embedCode;
  private String secondEmbedCode;
  private String pcode;
  private String domain;

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger Playbacklog = new SDCardLogcatOoyalaEventsLogger();

  private OoyalaPlayer player;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_activity);
    setupActionBar();
    parseBundle(getIntent().getExtras());
    castManager = CastManager.getCastManager();
    castManager.setMiniControllerView(findViewById(R.id.cast_mini_controller));
    initOoyala();
    castViewManager = new CastViewManager(this, castManager);
  }

  private void parseBundle(Bundle extras) {
    if (extras != null) {
      embedCode = extras.getString("embedcode");
      secondEmbedCode = extras.getString("embedcode2");
      pcode = extras.getString("pcode");
      domain = extras.getString("domain");
    }
  }

  private void initOoyala() {
    PlayerDomain playerDomain = new PlayerDomain(domain);
    Options options = new Options.Builder().setUseExoPlayer(true).build();
    OoyalaPlayerLayout ooyalaPlayerLayout = findViewById(R.id.ooyalaPlayer);
    player = new OoyalaPlayer(pcode, playerDomain, this, options);
    new OoyalaPlayerLayoutController(ooyalaPlayerLayout, player);
    castManager.registerWithOoyalaPlayer(player);
    player.addObserver(this);
    play(embedCode);
  }

  private void play(String ec) {
    player.setEmbedCode(ec);
    // Uncomment for Auto-Play
    player.play();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean createOptionsMenu = super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.browse, menu);
    CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
        R.id.media_route_menu_item);
    return createOptionsMenu;
  }

  private void setupActionBar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
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
      } else {
        Log.e(TAG, msg);
      }
    }

    if (arg1 == OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME) {
      if (player.isInCastMode()) {
        OoyalaPlayer.State state = player.getState();
        castViewManager.updateCastState(this, state);
      }
    }

    if (arg1 == OoyalaPlayer.PLAY_COMPLETED_NOTIFICATION_NAME && secondEmbedCode != null) {
      play(secondEmbedCode);
      secondEmbedCode = null;
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text = "Notification Received: " + arg1 + " - state: " + player.getState();
    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    Playbacklog.writeToSdcardLog(text);

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

  @Override
  public void getTokenForEmbedCodes(List<String> embedCodes, EmbedTokenGeneratorCallback callback) {
    String embedCodesString = "";
    for (String ec : embedCodes) {
      if (ec.equals("")) embedCodesString += ",";
      embedCodesString += ec;
    }

    HashMap<String, String> params = new HashMap<>();
    params.put("account_id", ACCOUNT_ID);

    String uri = "/sas/embed_token/" + pcode + "/" + embedCodesString;

    EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

    URL tokenUrl = urlGen.secureURL("http://player.ooyala.com", uri, params);

    callback.setEmbedToken(tokenUrl.toString());
  }
}

