package com.ooyala.sample.players;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

import com.ooyala.android.DebugMode;
import com.ooyala.android.LocalizationSupport;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.freewheelsdk.OoyalaFreewheelManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;

public class CuePointsOptionsFreewheelPlayerActivity extends Activity implements
    OnClickListener, Observer {
  /**
   * Called when the activity is first created.
   */
  private final String TAG = this.getClass().toString();
  private final String PCODE = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
  private final String DOMAIN = "http://ooyala.com";
  private String EMBEDCODE = "NqcGg4bzoOmMiV35ZttQDtBX1oNQBnT-";

  private OptimizedOoyalaPlayerLayoutController playerLayoutController;
  private OoyalaPlayer player;
  private Button setButton;
  private ToggleButton cuePointsButton;
  private ToggleButton adsControlsButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    String localeString = getResources().getConfiguration().locale.toString();
    Log.d(TAG, "locale is " + localeString);
    LocalizationSupport.useLocalizedStrings(LocalizationSupport
        .loadLocalizedStrings(localeString));
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_toggle_button_layout);
    EMBEDCODE = getIntent().getExtras().getString("embed_code");
    
    setButton = (Button) findViewById(R.id.setButton);
    setButton.setText("Create Video");
    setButton.setOnClickListener(this);

    cuePointsButton = (ToggleButton) findViewById(R.id.toggleButton1);
    cuePointsButton.setTextOn("CuePoints On");
    cuePointsButton.setTextOff("CuePoints Off");
    cuePointsButton.setChecked(true);
    
    adsControlsButton = (ToggleButton) findViewById(R.id.toggleButton2);
    adsControlsButton.setTextOn("AdsControls On");
    adsControlsButton.setTextOff("AdsControls Off");
    adsControlsButton.setChecked(true);
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "App Stopped");
    if (playerLayoutController.getPlayer() != null) {
      playerLayoutController.getPlayer().suspend();
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.d(TAG, "App Restarted");
    if (playerLayoutController.getPlayer() != null) {
      playerLayoutController.getPlayer().resume();
    }
  }

  @Override
  public void onClick(View v) {
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    boolean showAdsControls = this.adsControlsButton.isChecked();
    boolean showCuePoints = this.cuePointsButton.isChecked();
    DebugMode.logD(TAG, "showAdsControls: " + showAdsControls
        + " showCuePoints: " + showCuePoints);
    Options options = new Options.Builder().setShowAdsControls(showAdsControls)
        .setShowCuePoints(showCuePoints).build();
    playerLayoutController = new OptimizedOoyalaPlayerLayoutController(
        playerLayout, PCODE, domain, options);
    player = playerLayoutController.getPlayer();
    player.addObserver(this);

    OoyalaFreewheelManager freewheelManager = new OoyalaFreewheelManager(this,
        playerLayoutController);
    Map<String, String> freewheelParameters = new HashMap<String, String>();
    freewheelParameters.put("fw_android_ad_server", "http://g1.v.fwmrm.net/");
    freewheelParameters
        .put("fw_android_player_profile", "90750:ooyala_android");
    freewheelParameters.put("fw_android_site_section_id",
        "ooyala_android_internalapp");
    freewheelParameters.put("fw_android_video_asset_id", EMBEDCODE);

    freewheelManager.overrideFreewheelParameters(freewheelParameters);
    player.setEmbedCode(EMBEDCODE);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (playerLayoutController.onKeyUp(keyCode, event)) {
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION) {
      return;
    }
    Log.d(TAG,
        "Notification Recieved: " + arg1 + " - state: " + player.getState());
  }
  
  public void onToggleClicked(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	       // do something
	    } else {
	        // do something
	    }
	}
}
