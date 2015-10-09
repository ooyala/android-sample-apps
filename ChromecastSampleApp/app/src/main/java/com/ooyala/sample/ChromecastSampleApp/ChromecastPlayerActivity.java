package com.ooyala.sample.ChromecastSampleApp;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.castsdk.CastManager;
import com.ooyala.android.item.*;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ChromecastPlayerActivity extends ActionBarActivity implements EmbedTokenGenerator, Observer{
  
  private static final String TAG = ChromecastPlayerActivity.class.getSimpleName();
  private static final double DEFAULT_VOLUME_INCREMENT = 0.05;
  private String embedCode;
  private String pcode;
  private String domain;
  private OoyalaPlayer player;
  private CastManager castManager;
  private View castView;
  private final String ACCOUNT_ID = "accountID";
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
    // Setup castView
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // onClick of a DefaultMiniController only provides an embedcode through the extras
    Bundle extras = getIntent().getExtras();
    embedCode = extras.getString("embedcode");
    pcode = extras.getString("pcode") != null ? extras.getString("pcode") : "FoeG863GnBL4IhhlFC1Q2jqbkH9m";
    domain = extras.getString("domain") != null ? extras.getString("domain") :  "http://ooyala.com";
    
    // Initialize Ooyala Player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    PlayerDomain playerDomain = new PlayerDomain(domain);
    player = new OoyalaPlayer(pcode, playerDomain, this, null);
    OoyalaPlayerLayoutController playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);

    // Initialize action bar
    ActionBar actionBar = getSupportActionBar();
    actionBar.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setTitle(R.string.app_name);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    castView = getLayoutInflater().inflate(R.layout.cast_video_view, null);
    castManager = CastManager.getCastManager();
    castManager.setCastView(castView);
    castManager.registerWithOoyalaPlayer(player);

    player.addObserver(this);
    player.setEmbedCode(embedCode);
    player.play();
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
    castManager.deregisterFromOoyalaPlayer();
    player = null;
    super.onDestroy();
  }

  @Override
  protected void onResume() {
    castManager.getVideoCastManager().incrementUiCounter();
    if (player != null) {
      player.resume();
    }  
    super.onResume();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.d(TAG, "onCreateOptionsMenu()");
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.main, menu);
    castManager.getVideoCastManager().addMediaRouterButton(menu, R.id.media_route_menu_item);
    return true;
  }

 
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (castManager != null && castManager.getVideoCastManager() != null) {
      if (castManager.getVideoCastManager().onDispatchVolumeKeyEvent(event, DEFAULT_VOLUME_INCREMENT)) {
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  private void onVolumeChange(double volumeIncrement) {
    if (castManager == null) {
      return;
    }
    try {
      Log.d(TAG, "Increase DeviceVolume: " + volumeIncrement);
      castManager.getVideoCastManager().adjustDeviceVolume(volumeIncrement);
    } catch (Exception e) {
      Log.e(TAG, "onVolumeChange() Failed to change volume", e);
    }
  }

  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable arg0, Object arg1) {
    if (arg0 != player) {
      return;
    }

    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION) {
      return;
    }

    if (arg1 == OoyalaPlayer.CURRENT_ITEM_CHANGED_NOTIFICATION) {
      configureCastPlaybackViewBasedOnItem(player.getCurrentItem());
    } else if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION) {
      final String msg = "Error Event Received";
      if (player != null && player.getError() != null) {
        Log.e(TAG, msg, player.getError());
      }
      else {
        Log.e(TAG, msg);
      }
    }

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

  /**
   * Configure the information on the CastView whenever a new video is put into the OoyalaPlayer
   * @param video
   */
  private void configureCastPlaybackViewBasedOnItem(Video video) {
    final ImageView castBackgroundImage = (ImageView) castView.findViewById(R.id.castBackgroundImage);

    // Update the ImageView on a separate thread

    new Thread(new UpdateImageViewRunnable(castBackgroundImage, video.getPromoImageURL(0, 0))).start();

    TextView videoTitle = (TextView) castView.findViewById(R.id.videoTitle);
    videoTitle.setText(video.getTitle());

    TextView videoDescription = (TextView) castView.findViewById(R.id.videoDescription);
    videoDescription.setText(video.getDescription());
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

    //In 4.3.0, this class will be public in the com.ooyala.android package
    EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

    URL tokenUrl  = urlGen.secureURL("http://player.ooyala.com", uri, params);

    callback.setEmbedToken(tokenUrl.toString());
  }

}