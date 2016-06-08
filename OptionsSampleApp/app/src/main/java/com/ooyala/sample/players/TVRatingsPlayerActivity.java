package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

import java.util.Observable;
import java.util.Observer;

public class TVRatingsPlayerActivity extends Activity implements OnClickListener, Observer {
  /**
   * Called when the activity is first created.
   */
  private final String TAG = this.getClass().toString();
  private final String PCODE = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
  private final String DOMAIN = "http://ooyala.com";
  private String EMBEDCODE = "";

  private final int TVRATING_DURATION = 5;

  private OptimizedOoyalaPlayerLayoutController playerLayoutController;
  private OoyalaPlayer player;

  SDCardLogcatOoyalaEventsLogger playbacklog;

  private Button setButton;
  private ToggleButton verticalAlignToggle;
  private ToggleButton horizontalAlignToggle;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_toggle_button_layout);
    EMBEDCODE = getIntent().getExtras().getString("embed_code");

    setButton = (Button) findViewById(R.id.setButton);
    setButton.setText("Create Video");
    setButton.setOnClickListener(this);

    verticalAlignToggle = (ToggleButton) findViewById(R.id.toggleButton1);
    verticalAlignToggle.setTextOn("Align Top");
    verticalAlignToggle.setTextOff("Align Bottom");
    verticalAlignToggle.setChecked(false);

    horizontalAlignToggle = (ToggleButton) findViewById(R.id.toggleButton2);
    horizontalAlignToggle.setTextOn("Align Left");
    horizontalAlignToggle.setTextOff("Align Right");
    horizontalAlignToggle.setChecked(false);

    // Initialize playBackLog : Write the sdk events text along with events count to log file in sdcard if the log file already exists
    playbacklog = new SDCardLogcatOoyalaEventsLogger();
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "App Stopped");
    if (playerLayoutController != null && playerLayoutController.getPlayer() != null) {
      playerLayoutController.getPlayer().suspend();
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.d(TAG, "App Restarted");
    if (playerLayoutController != null && playerLayoutController.getPlayer() != null) {
      playerLayoutController.getPlayer().resume();
    }
  }

  private FCCTVRatingConfiguration.Position getTVRatingPosition() {
    if (verticalAlignToggle.isChecked() && horizontalAlignToggle.isChecked()) {
      return FCCTVRatingConfiguration.Position.TopLeft;
    } else if (verticalAlignToggle.isChecked() && !horizontalAlignToggle.isChecked()) {
      return FCCTVRatingConfiguration.Position.TopRight;
    } else if (!verticalAlignToggle.isChecked() && horizontalAlignToggle.isChecked()) {
      return FCCTVRatingConfiguration.Position.BottomLeft;
    } else {// if (!verticalAlignToggle.isChecked() && !horizontalAlignToggle.isChecked()) {
      return FCCTVRatingConfiguration.Position.BottomRight;
    }
  }

  @Override
  public void onClick(View v) {
    // remove the previous player to only play the current player
    if(player != null){
      player.suspend();
      player.removeVideoView();
    }
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    PlayerDomain domain = new PlayerDomain(DOMAIN);

    FCCTVRatingConfiguration fccConfig = new FCCTVRatingConfiguration.Builder().setPosition(getTVRatingPosition()).setDurationSeconds(TVRATING_DURATION).build();
    Options options = new Options.Builder().setTVRatingConfiguration(fccConfig).build();

    player = new OoyalaPlayer(PCODE, domain, options);
    playerLayoutController = new OptimizedOoyalaPlayerLayoutController(
            playerLayout, player);
    player.addObserver(this);

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
    playbacklog.writeToSdcardLog(text);

    Log.d(TAG, text);
  }
}
