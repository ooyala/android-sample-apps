package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ooyala.android.DebugMode;
import com.ooyala.android.LocalizationSupport;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.freewheelsdk.OoyalaFreewheelManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class TimeoutOptionsPlayerActivity extends Activity implements OnClickListener, Observer {
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
  private EditText connectionTimeout;
  private EditText readTimeout;

  @Override
  public void onCreate(Bundle savedInstanceState) {
	String localeString = getResources().getConfiguration().locale.toString();
	Log.d(TAG, "locale is " + localeString);
	LocalizationSupport.useLocalizedStrings(LocalizationSupport
	    .loadLocalizedStrings(localeString));
	
	super.onCreate(savedInstanceState);
	setContentView(R.layout.player_double_textedit_layout);
	EMBEDCODE = getIntent().getExtras().getString("embed_code");
	
	setButton = (Button) findViewById(R.id.setButton);
	setButton.setText("Create Video");
	setButton.setOnClickListener(this);
	
	connectionTimeout = (EditText) findViewById(R.id.edit1);
	connectionTimeout.setHint("connection timeout in milliseconds");
	
	readTimeout = (EditText) findViewById(R.id.edit2);
	readTimeout.setHint("read timeout in milliseconds");
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
    // remove the previous player to only play the current player
	if(player != null){
		player.suspend();
		player.removeVideoView();
	}
	OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
	PlayerDomain domain = new PlayerDomain(DOMAIN);
	int connectionTimeoutMs = Integer.valueOf(this.connectionTimeout.getText().toString());
	int readTimeoutMs = Integer.valueOf(this.readTimeout.getText().toString());
	DebugMode.logD(TAG, "connectionTimeout: " + connectionTimeoutMs
	    + " readTimeout: " + readTimeoutMs);
  //TODO: uncomment when 3.5.0 is released
	  Options options =
    new Options.Builder().build();//.setConnectionTimeout(connectionTimeoutMs).setReadTimeout(readTimeoutMs).build();
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
	public void update(Observable arg0, Object arg1) {
	if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION) {
	  return;
	}
	Log.d(TAG,
	    "Notification Received: " + arg1 + " - state: " + player.getState());
	}
}