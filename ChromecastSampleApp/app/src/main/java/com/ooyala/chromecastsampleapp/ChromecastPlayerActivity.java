package com.ooyala.chromecastsampleapp;

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
import com.ooyala.android.ui.OoyalaPlayerLayoutController;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChromecastPlayerActivity extends ActionBarActivity implements EmbedTokenGenerator{
  
  private static final String TAG = "ChromecastPlayerActivity";
  private static final double DEFAULT_VOLUME_INCREMENT = 0.05;
  private String embedCode;
  private int icon;
  final String PCODE  = "c0cTkxOqALQviQIGAHWY5hP0q9gU";
  final String DOMAIN = "http://www.ooyala.com";
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
    embedCode = getIntent().getExtras().getString("embedcode");
    icon = getIntent().getExtras().getInt("icon");

    // Initialize Ooyala Player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    player = new OoyalaPlayer(PCODE, domain, this, null);
    OoyalaPlayerLayoutController playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);

    // Initialize CastManager
    castManager = CastManager.initialize(this, "4172C76F", "urn:x-cast:ooyala");
    castManager.destroyNotificationService(this);
    castManager.registerWithOoyalaPlayer(player);
    castManager.setTargetActivity(ChromecastPlayerActivity.class);


    // Initialize action bar
    ActionBar actionBar = getSupportActionBar();
    actionBar.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setTitle(R.string.app_name);
    getSupportActionBar().setDisplayShowTitleEnabled(false);


    setupCastView();

    player.setEmbedCode(embedCode);
    if (!castManager.isInCastMode()) {
      player.play();
    }
  }

  private void setupCastView() {
    castView = getLayoutInflater().inflate(R.layout.cast_video_view, null);

    final ImageView castBackgroundImage = (ImageView) castView.findViewById(R.id.castBackgroundImage);
    castBackgroundImage.setImageResource(icon);
    TextView videoTitle = (TextView) castView.findViewById(R.id.videoTitle);
    videoTitle.setText("TITLE");
    TextView videoDescription = (TextView) castView.findViewById(R.id.videoDescription);
    videoDescription.setText("VIDEO DESCRIPTION");

    castManager.setCastView(castView);
  }


  @Override
  public void onPause() {
    Log.d(TAG, "onPause()");
    ChromecastListActivity.activatedActivity --;
    super.onPause();
  }
  
  @Override
  protected void onStart() {
    Log.d(TAG, "onStart()");
    super.onStart();
    castManager.setCurrentContext(this);
  }

  @Override
  protected void onStop() {
    Log.d(TAG, "onStop()");
    super.onStop();
    if (ChromecastListActivity.activatedActivity == 0 && castManager != null && castManager.isInCastMode()) {
      castManager.createNotificationService(this);
      castManager.registerLockScreenControls(this);
    }
  }

  @Override
  protected void onRestart() {
    Log.d(TAG, "onRestart()");
    super.onRestart();
  }

  @Override
  protected void onDestroy() {
    Log.d(TAG, "onDestroy()");
    castManager.destroyNotificationService(this);
    castManager.unregisterLockScreenControls();
    player = null;
    super.onDestroy();
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume()");
    ChromecastListActivity.activatedActivity++;

    if (castManager != null && castManager.getCastPlayer() != null) {
      castManager.destroyNotificationService(this);
      castManager.unregisterLockScreenControls();
    } else if (player != null) {
      player.resume();
    }  
  super.onResume();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    Log.d(TAG, "onCreateOptionsMenu()");
    super.onCreateOptionsMenu(menu);
    castManager.addCastButton(this, menu);
    return true;
  }

 
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (castManager != null && castManager.getCastPlayer() != null) {
      if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
        Log.w(TAG, "KeyEvent.KEYCODE_VOLUME_UP");
        onVolumeChange(DEFAULT_VOLUME_INCREMENT);
        return true;
      } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
        Log.w(TAG, "KeyEvent.KEYCODE_VOLUME_DOWN");
        onVolumeChange(-DEFAULT_VOLUME_INCREMENT);
        return true;
      } else {
        // we don't want to consume non-volume key events
        return super.onKeyDown(keyCode, event);
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  private void onVolumeChange(double volumeIncrement) {
    if (castManager == null) {
      return;
    }
    try {
      Log.d(TAG, "Increase DeviceVolume!!!!!!!!!!!" + volumeIncrement);
      castManager.incrementDeviceVolume(volumeIncrement);
    } catch (Exception e) {
      Log.e(TAG, "onVolumeChange() Failed to change volume", e);
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
      if(ec.equals("")) embedCodesString += ",";
      embedCodesString += ec;
    }

    HashMap<String, String> params = new HashMap<String, String>();
    params.put("account_id", ACCOUNT_ID);

    String uri = "/sas/embed_token/" + PCODE + "/" + embedCodesString;

    //In 4.3.0, this class will be public in the com.ooyala.android package
    EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

    URL tokenUrl  = urlGen.secureURL("http://player.ooyala.com", uri, params);

    callback.setEmbedToken(tokenUrl.toString());
  }

}