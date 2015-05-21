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

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.castsdk.OOCastManager;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;

import java.util.HashMap;
import java.util.Map;

public class PlayerStartingActivity extends ActionBarActivity {
  
  private static final String TAG = "PlayerStartingActivity";
  private static final double DEFAULT_VOLUME_INCREMENT = 0.05;
  private String embedCode;
  final String PCODE  = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
  final String DOMAIN = "http://www.ooyala.com";
  private OoyalaPlayer player;
  private OOCastManager castManager;
  private View castView;
  private Map<String, Integer> thumbnailMap;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate()");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    embedCode = getIntent().getExtras().getString("embedcode");
    
    // Initialize Ooyala Player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    player = new OoyalaPlayer(PCODE, domain);
    OoyalaPlayerLayoutController playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);

    // Initialize CastManager
    castManager = OOCastManager.initialize(this, "4172C76F", "urn:x-cast:ooyala");
    castManager.destroyNotificationService(this);
    castManager.registerWithOoyalaPlayer(player);
    castManager.setTargetActivity(PlayerStartingActivity.class);


    // Initialize action bar
    ActionBar actionBar = getSupportActionBar();
    actionBar.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setTitle(R.string.app_name);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    // Setup castView
    buildThumbnailMap();

    setupCastView();

    player.setEmbedCode(embedCode);
    if (!castManager.isInCastMode()) {
      player.play();
    }
  }

  private void buildThumbnailMap() {
    thumbnailMap = new HashMap<String, Integer>();
    thumbnailMap.put("wxaWd5bTrJFI--Ga7TgbJtzcPrbzENBV", R.drawable.test1);
    thumbnailMap.put("IzNGg3bzoHHjEfnJP-fj2jB0-oci0Jnm", R.drawable.test2);
    thumbnailMap.put("xiNmg3bzpFkkwsYqkb5UtGvNOpcwiOCS", R.drawable.test3);
    thumbnailMap.put("Y4OWg3bzoNtSZ9TOg3wl9BPUspXZiMYc", R.drawable.test4);
    thumbnailMap.put("o0OWg3bzrLBNfadaXSaCA7HbknPLFRPP", R.drawable.test5);
  }

  private void setupCastView() {
    castView = getLayoutInflater().inflate(R.layout.cast_video_view, null);

    final ImageView castBackgroundImage = (ImageView) castView.findViewById(R.id.castBackgroundImage);
    castBackgroundImage.setImageResource(thumbnailMap.get(embedCode));
    TextView videoTitle = (TextView) castView.findViewById(R.id.videoTitle);
    videoTitle.setText("TITLE");
    TextView videoDescription = (TextView) castView.findViewById(R.id.videoDescription);
    videoDescription.setText("VIDEO DESCRIPTION");

    castManager.setCastView(castView);
  }


  @Override
  public void onPause() {
    Log.d(TAG, "onPause()");
    ChromecastSampleAppActivity.activatedActivity --;
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
    if (ChromecastSampleAppActivity.activatedActivity == 0 && castManager != null && castManager.isInCastMode()) {
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
    castManager.deregisterOoyalaPlayer();
    player = null;
    super.onDestroy();
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume()");
    ChromecastSampleAppActivity.activatedActivity++;

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

}