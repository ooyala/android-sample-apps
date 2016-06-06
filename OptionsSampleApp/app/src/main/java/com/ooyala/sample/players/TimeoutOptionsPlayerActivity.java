package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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
  SDCardLogcatOoyalaEventsLogger playbacklog;

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

  @Override
  public void onClick(View v) {
    // remove the previous player to only play the current player
	if(player != null){
		player.suspend();
		player.removeVideoView();
	}
	OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

  //If the connection timeout is specified, add it to the builder
  Options.Builder builder = new Options.Builder();
  if (!this.connectionTimeout.getText().toString().equals("")) {
    try {
      int connectionTimeoutMs = Integer.valueOf(this.connectionTimeout.getText().toString());
      builder.setConnectionTimeout(connectionTimeoutMs);
    } catch (Exception e) {
      DebugMode.logE(TAG, "The value provided was not a number. Cannot continue");
      return;
    }
  }

  //If read timeout is specified, add it to the builder
  if (!this.readTimeout.getText().toString().equals("")) {
    try {
      int readTimeoutMs = Integer.valueOf(this.readTimeout.getText().toString());
      builder.setReadTimeout(readTimeoutMs);
    } catch (Exception e) {
      DebugMode.logE(TAG, "The value provided was not a number. Cannot continue");
      return;
    }
  }

  //Build the options with the potentially updated builder
  Options options = builder.build();
  player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);
  playerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);

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
    playbacklog.writeToSdcardLog(text);

	Log.d(TAG, text);
	}
}
