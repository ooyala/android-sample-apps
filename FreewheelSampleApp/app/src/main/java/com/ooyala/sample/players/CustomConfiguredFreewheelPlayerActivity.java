package com.ooyala.sample.players;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.freewheelsdk.OoyalaFreewheelManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

/**
 * This activity illustrates how to use Freewheel while manually configuring Freewheel settings
 *
 * Supported parameters for Freewheel Configuration:
 * - fw_android_mrm_network_id
 * - fw_android_ad_server
 * - fw_android_player_profile
 * - FRMSegment
 * - fw_android_site_section_id
 * - fw_android_video_asset_id
 */
public class CustomConfiguredFreewheelPlayerActivity extends Activity implements Observer {
	private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
	public final static String getName() {
		return "Preconfigured Freewheel Player";
	}
	final String TAG = this.getClass().toString();

	String EMBED = null;
	String PCODE = null;
	String DOMAIN = null;
	String text;

	// Write the sdk events text along with events count to log file in sdcard if the log file already exists
	SDCardLogcatOoyalaEventsLogger Playbacklog= new SDCardLogcatOoyalaEventsLogger();

	protected OptimizedOoyalaPlayerLayoutController playerLayoutController;
	protected OoyalaPlayer player;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {


			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
			}
		}
		setTitle(getName());
		setContentView(R.layout.player_simple_frame_layout);

		EMBED = getIntent().getExtras().getString("embed_code");
		PCODE = getIntent().getExtras().getString("pcode");
		DOMAIN = getIntent().getExtras().getString("domain");

		//Initialize the player
		OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

		Options options = new Options.Builder().setUseExoPlayer(true).build();
		player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);
		playerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);
		player.addObserver(this);

		/** DITA_START:<ph id="freewheel_custom"> **/
		OoyalaFreewheelManager fwManager = new OoyalaFreewheelManager(this, playerLayoutController);

		Map<String, String> freewheelParameters = new HashMap<String, String>();
		freewheelParameters.put("fw_android_mrm_network_id",  "380912");
		freewheelParameters.put("fw_android_ad_server", "http://g1.v.fwmrm.net/");
		freewheelParameters.put("fw_android_player_profile",  "90750:ooyala_android");
		freewheelParameters.put("FRMSegment",  "channel=TEST;subchannel=TEST;section=TEST;mode=online;player=ooyala;beta=n");
		freewheelParameters.put("fw_android_site_section_id", "ooyala_android_internalapp");
		freewheelParameters.put("fw_android_video_asset_id",  "NqcGg4bzoOmMiV35ZttQDtBX1oNQBnT-");

		fwManager.overrideFreewheelParameters(freewheelParameters);
		/** DITA_END:</ph> **/

		if (player.setEmbedCode(EMBED)) {
			//Uncomment for Auto Play
			//player.play();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "Player Activity Stopped");
		if (player != null) {
			player.suspend();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "Player Activity Restarted");
		if (player != null) {
			player.resume();
		}
	}

	/**
	 * Listen to all notifications from the OoyalaPlayer
	 */
	@Override
	public void update(Observable arg0, Object argN) {
		final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
		if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
			return;
		}

		// Automation Hook: to write Notifications to a temporary file on the device/emulator
		text="Notification Received: " + arg1 + " - state: " + player.getState();
		// Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
		Playbacklog.writeToSdcardLog(text);

		Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.
					Playbacklog.writeToSdcardLog(text);
				}
				return;
			}
		}
	}
}
