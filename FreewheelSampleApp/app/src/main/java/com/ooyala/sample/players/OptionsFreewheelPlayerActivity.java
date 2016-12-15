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

import com.ooyala.android.util.DebugMode;
import com.ooyala.android.LocalizationSupport;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.freewheelsdk.OoyalaFreewheelManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

public class OptionsFreewheelPlayerActivity extends Activity implements
    OnClickListener, Observer {
  /**
   * Called when the activity is first created.
   */
  private final String TAG = this.getClass().toString();
  String PCODE = null;
  String DOMAIN = null;
  String EMBEDCODE = null;

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger Playbacklog= new SDCardLogcatOoyalaEventsLogger();

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
    PCODE = getIntent().getExtras().getString("pcode");
    DOMAIN = getIntent().getExtras().getString("domain");
    
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
    if (player != null) {
      player.suspend();
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.d(TAG, "App Restarted");
    if (player != null) {
      player.resume();
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
         .setShowCuePoints(showCuePoints).setUseExoPlayer(true).build();

    player = new OoyalaPlayer(PCODE, domain, options);
    playerLayoutController = new OptimizedOoyalaPlayerLayoutController(
        playerLayout, player);
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
  public void update(Observable arg0, Object argN) {
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text="Notification Received: " + arg1 + " - state: " + player.getState();
    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    Playbacklog.writeToSdcardLog(text);

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }
}
