package com.ooyala.sample.players;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;

public class PreloadWithInitTimePlayerActivity extends Activity implements OnClickListener, Observer {
  /**
  * Called when the activity is first created.
  */
  private final String TAG = this.getClass().toString();
  private final String PCODE = "R2d3I6s06RyB712DN0_2GsQS-R-Y";
  private final String DOMAIN = "http://ooyala.com";
  private String EMBEDCODE = "";

  private OptimizedOoyalaPlayerLayoutController playerLayoutController;
  private OoyalaPlayer player;
  private Button setButton;
  private ToggleButton preloadButton;
  private ToggleButton showPromoImageButton;

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
	
	preloadButton = (ToggleButton) findViewById(R.id.toggleButton1);
	preloadButton.setTextOn("Preload On");
	preloadButton.setTextOff("Preload Off");
	preloadButton.setChecked(true);
	
	showPromoImageButton = (ToggleButton) findViewById(R.id.toggleButton2);
	showPromoImageButton.setTextOn("Show PromoImage On");
	showPromoImageButton.setTextOff("Show PromoImage Off");
	showPromoImageButton.setChecked(true);
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
	
  @Override
  public void onClick(View v) {
	if(player != null){
		player.suspend();
		player.removeVideoView();
	}
	
	OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
	PlayerDomain domain = new PlayerDomain(DOMAIN);
	boolean showPromoImage = this.showPromoImageButton.isChecked();
	boolean preload = this.preloadButton.isChecked();
	DebugMode.logD(TAG, "showPromoImage: " + showPromoImage
	    + " preload: " + preload);
	Options options = new Options.Builder().setPreloadContent(preload).setShowPromoImage(showPromoImage).build();

    player = new OoyalaPlayer(PCODE, domain, options);
    playerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);
	player.addObserver(this);
	
	player.setEmbedCode(EMBEDCODE);
	player.play(20000);
  }
	
  @Override
  public void update(Observable arg0, Object argN) {
  final String arg1 = ((OoyalaNotification)argN).getName();
	if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
	  return;
	}
	Log.d(TAG,
	    "Notification Received: " + arg1 + " - state: " + player.getState());
  }
}
