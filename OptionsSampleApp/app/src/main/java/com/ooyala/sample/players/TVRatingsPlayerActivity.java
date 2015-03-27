package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;

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

    playerLayoutController = new OptimizedOoyalaPlayerLayoutController(
            playerLayout, PCODE, domain, options);
    player = playerLayoutController.getPlayer();
    player.addObserver(this);

    player.setEmbedCode(EMBEDCODE);
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION) {
      return;
    }
    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }
}