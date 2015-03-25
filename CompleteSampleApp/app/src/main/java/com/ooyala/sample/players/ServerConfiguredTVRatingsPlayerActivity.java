package com.ooyala.sample.players;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.ooyala.android.LocalizationSupport;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.FCCTVRatingConfiguration.Position;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;

/**
 * This activity illustrates how you enable TV Ratings Display.
 *
 * In order for this to work, you must have the following values in your asset's CustomMetadata:
 *   tvrating
 *   tvratingsurl (optional)
 *   tvsubratings (optional)
 */
public class ServerConfiguredTVRatingsPlayerActivity extends Activity implements Observer {
  /**
  * Called when the activity is first created.
  */
  private final String TAG = this.getClass().toString();
  private final String PCODE = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
  private final String DOMAIN = "http://ooyala.com";
  private String EMBEDCODE = "";

  private OptimizedOoyalaPlayerLayoutController playerLayoutController;
  private OoyalaPlayer player;

  @Override
  public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.player_simple_layout);
	EMBEDCODE = getIntent().getExtras().getString("embed_code");
	
	OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
	PlayerDomain domain = new PlayerDomain(DOMAIN);
	
	// Configure FCC TV Ratings
	FCCTVRatingConfiguration.Builder builder = new FCCTVRatingConfiguration.Builder();
	FCCTVRatingConfiguration tvRatingConfiguration = builder.setPosition(Position.TopLeft).setDurationSeconds(5).build();
	Options options = new Options.Builder().setTVRatingConfiguration(tvRatingConfiguration).build();
	
    player = new OoyalaPlayer(PCODE, domain, options);
    playerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);
	player.addObserver(this);
	
	player.setEmbedCode(EMBEDCODE);
	player.play();
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
  public void update(Observable arg0, Object arg1) {
	if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION) {
	  return;
	}
	Log.d(TAG,
	    "Notification Received: " + arg1 + " - state: " + player.getState());
  }
}
