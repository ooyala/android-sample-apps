package com.ooyala.sample.players;

/**
 * Created by FTT on 30/05/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.utils.VideoItem;

import java.util.Observable;
import java.util.Observer;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * This class asks permission for WRITE_EXTERNAL_STORAGE. We need it for automation hooks
 * as we need to write into the SD card and automation will parse this file.
 */
public abstract class AbstractHookActivity extends Activity implements Observer, DefaultHardwareBackBtnHandler {
	private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
	String TAG = this.getClass().toString();
	final int CLICK_THROUGH_REQUEST = 11234;
	protected OoyalaSkinLayoutController playerSkinLayoutController;
	final String LOG_TAG = this.getClass().toString();

	SDCardLogcatOoyalaEventsLogger log = new SDCardLogcatOoyalaEventsLogger();

	String embedCode;
	String pcode;
	String domain;

	OoyalaPlayer player;

	boolean writePermission = false;
	boolean asked = false;

	// complete player setup after we asked for permission to write into external storage
	abstract void completePlayerSetup(final boolean asked);

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
		} else {
			writePermission= true;
			asked = true;
		}

		embedCode = getIntent().getExtras().getString("embed_code");
		pcode = getIntent().getExtras().getString("pcode");
		domain = getIntent().getExtras().getString("domain");
	}

	protected void setPlayerLayoutController(OoyalaSkinLayoutController playerSkinLayoutController) {
		this.playerSkinLayoutController = playerSkinLayoutController;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
			asked = true;
			if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
				writePermission = true;
			}
			completePlayerSetup(asked);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		playerSkinLayoutController.onKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}

	/** Start DefaultHardwareBackBtnHandler **/
	@Override
	public void invokeDefaultOnBackPressed() {
		super.onBackPressed();
	}
	/** End DefaultHardwareBackBtnHandler **/

	/** Start Activity methods for Skin **/
	@Override
	protected void onPause() {
		super.onPause();
		if (playerSkinLayoutController != null) {
			playerSkinLayoutController.onPause();
		}
		Log.d(LOG_TAG, "Player Activity Paused");
		if (player != null) {
			player.suspend();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (playerSkinLayoutController != null) {
			playerSkinLayoutController.onResume( this, this );
		}
		Log.d(LOG_TAG, "Player Activity Resumed");
		if (player != null) {
			player.resume();
		}
	}

	@Override
	public void onBackPressed() {
		if (playerSkinLayoutController != null) {
			playerSkinLayoutController.onBackPressed();
		} else {
			super.onBackPressed();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (playerSkinLayoutController != null) {
			playerSkinLayoutController.onDestroy();
		}
	}

	@Override
	public void update(Observable arg0, Object argN) {

		final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
		if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
			return;
		}

		if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
			final String msg = "Error Event Received";
			if (player != null && player.getError() != null) {
				Log.e(TAG, msg, player.getError());
			}
			else {
				Log.e(TAG, msg);
			}
			return;
		}

		if (arg1 == OoyalaSkinLayoutController.FULLSCREEN_CHANGED_NOTIFICATION_NAME) {
			Log.d(TAG, "Fullscreen Notification received : " + arg1 + " - fullScreen: " + ((OoyalaNotification)argN).getData());
		}

		// Automation Hook: to write Notifications to a temporary file on the device/emulator
		String text="Notification Received: " + arg1 + " - state: " + player.getState();
		// Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
		log.writeToSdcardLog(text);

		Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if((resultCode == RESULT_OK || resultCode == RESULT_CANCELED) && requestCode == CLICK_THROUGH_REQUEST){
			Log.i("DemoIntegration","Came back from clickthrough");
			onResume();
		}
	}

	/**
	 * Create a VideoItem from the bundled information send to this activity.
	 * @return The created {@link VideoItem}.
	 */
	public VideoItem getVideoItem() {
		VideoItem videoItem = new VideoItem();

		videoItem.setTags(getIntent().getExtras().getStringArray("contentMetadataTags"));
		videoItem.setMidrollPositions(getIntent().getExtras().getFloatArray("midrollPositions"));
		videoItem.setContentTitle(getIntent().getExtras().getString("contentTitle"));
		videoItem.setContentId(getIntent().getExtras().getString("contentId"));
		videoItem.setCategory(getIntent().getExtras().getString("category"));
		videoItem.setContentCode(getIntent().getExtras().getString("embedCode"));

		return videoItem;
	}

}
