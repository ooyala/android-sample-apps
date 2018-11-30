package com.ooyala.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.ooyala.android.*;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerControls;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.cast.CastManager;
import com.ooyala.cast.mediainfo.VideoData;

import java.net.URL;
import java.util.*;

public class ChromecastPlayerActivity extends AppCompatActivity implements Observer, EmbedTokenGenerator {
  private static final String TAG = ChromecastPlayerActivity.class.getSimpleName();

  /*
   * The API Key and Secret should not be saved inside your applciation (even in git!).
   * However, for debugging you can use them to locally generate Ooyala Player Tokens.
   */
  private final String APIKEY = "fill me in";
  private final String SECRET = "fill me in";
  private final String ACCOUNT_ID = "accountID";

  private OoyalaPlayer player;

  private CastManager castManager;
  private CastViewManager castViewManager;
  private  OoyalaPlayerLayoutController layoutController;

  private String embedCode;
  private String secondEmbedCode;
  private String pcode;
  private String domain;
  private boolean wasInCastMode;

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  private SDCardLogcatOoyalaEventsLogger playbackLog = new SDCardLogcatOoyalaEventsLogger();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_activity);
    setupActionBar();
    parseSharedPreferences();
    castManager = CastManager.getCastManager();
//    Uncomment to set custom parameters
//    Map<String, String> userPreferences = loadUserPreferences();
//    castManager.setAdditionalInitParams(userPreferences);
    initOoyala();
    castViewManager = new CastViewManager(this, castManager);
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (castManager != null && player != null) {
      castManager.registerWithOoyalaPlayer(player);
    }
    if (layoutController != null) {
      final OoyalaPlayerControls controls = layoutController.getControls();
      if (controls != null) {
        controls.refresh();
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (player != null) {
      player.resume();
      if (!player.isInCastMode() && wasInCastMode) {
        castManager.hideCastView();
        wasInCastMode = false;
      }
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (player != null) {
      player.suspend();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (castManager != null) {
      castManager.deregisterFromOoyalaPlayer();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean createOptionsMenu = super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.browse, menu);
    CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
            R.id.media_route_menu_item);
    return createOptionsMenu;
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

  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable arg0, Object argN) {
    if (arg0 != player) {
      return;
    }

    OoyalaNotification notification = null;
    if (argN instanceof OoyalaNotification ){
      notification = (OoyalaNotification) argN;
    }

    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    if (arg1 == OoyalaPlayer.CURRENT_ITEM_CHANGED_NOTIFICATION_NAME) {
      updateCastView(notification);
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
        if (!wasInCastMode) {
          wasInCastMode = true;
        }
        OoyalaPlayer.State state = player.getState();
        castViewManager.updateCastState(this, state);
      } else if (wasInCastMode) {
        wasInCastMode = false;
      }
    }

    if (arg1 == OoyalaPlayer.PLAY_COMPLETED_NOTIFICATION_NAME && secondEmbedCode != null) {
      play(secondEmbedCode);
      secondEmbedCode = null;
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text = "Notification Received: " + arg1 + " - state: " + player.getState();
    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    playbackLog.writeToSdcardLog(text);

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

  private void parseSharedPreferences() {
    SharedPreferences lastChosenParams = getSharedPreferences("LastChosenParams", MODE_PRIVATE);
    if (lastChosenParams != null) {
      embedCode = lastChosenParams.getString("embedcode", "");
      secondEmbedCode = lastChosenParams.getString("secondEmbedCode", null);
      pcode = lastChosenParams.getString("pcode", "");
      domain = lastChosenParams.getString("domain", "");
    }
  }

  private void initOoyala() {
    PlayerDomain playerDomain = new PlayerDomain(domain);
    Options options = new Options.Builder().setUseExoPlayer(true).build();
    OoyalaPlayerLayout ooyalaPlayerLayout = findViewById(R.id.ooyalaPlayer);
    player = new OoyalaPlayer(pcode, playerDomain, this, options);
    layoutController = new OoyalaPlayerLayoutController(ooyalaPlayerLayout, player);
    castManager.registerWithOoyalaPlayer(player);
    player.addObserver(this);
    play(embedCode);
  }

  private void play(String ec) {
    player.setEmbedCode(ec);
    // Uncomment for Auto-Play
    player.play();
  }

  private void setupActionBar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  private void updateCastView(OoyalaNotification notification) {
    if (notification != null && notification.getData() != null && notification.getData() instanceof VideoData) {
      VideoData data = (VideoData) notification.getData();
      castViewManager.configureCastView(data.getTitle(), data.getDescription(), data.getUrl());
    } else if (player != null && player.getCurrentItem() != null) {
      castViewManager.configureCastView(
          player.getCurrentItem().getTitle(),
          player.getCurrentItem().getDescription(),
          player.getCurrentItem().getPromoImageURL(0, 0)
      );
    }
  }

  private Map<String, String> loadUserPreferences() {
    Map<String, String> preferences = new HashMap<>();
    // Send user name to receiver
    preferences.put("userName", "User");
    // Define initial volume based on user preferences (this property will override volume defined in player params)
    preferences.put("initialVolume", "0");
    // Pass embed token (Token generated in getTokenForEmbedCodes() will be overridden)
    preferences.put("embedToken", "some-uu-embed-token");
    // Change video title and description
    preferences.put("title", "New title");
    preferences.put("description", "New description");
    return preferences;
  }
}

