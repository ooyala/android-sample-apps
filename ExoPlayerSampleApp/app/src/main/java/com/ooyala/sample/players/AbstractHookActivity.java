package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

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

	protected OoyalaPlayer player;
	protected OoyalaSkinLayout skinLayout;
	protected OoyalaSkinLayoutController playerLayoutController;

	protected String embedCode;
	protected String pcode;
	protected String domain;
	protected String TAG = this.getClass().toString();

	// An account ID, if you are using Concurrent Streams or Entitlements
	protected final String ACCOUNT_ID = "Account_ID";
	protected final String APIKEY = "Use this for testing, don't keep your secret in the application";
	protected final String SECRET = "Use this for testing, don't keep your secret in the application";

	protected boolean writePermission = false;
	protected boolean asked = false;

	private SDCardLogcatOoyalaEventsLogger log = new SDCardLogcatOoyalaEventsLogger();

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

		embedCode = getIntent().getExtras().getString("embed_code");;
		pcode = getIntent().getExtras().getString("pcode");;
		domain = getIntent().getExtras().getString("domain");
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
	public void onStart() {
		super.onStart();
		if (null != player) {
			player.resume();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (player != null) {
			player.suspend();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (playerLayoutController != null) {
			playerLayoutController.onPause();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (playerLayoutController != null) {
			playerLayoutController.onResume(this, this);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		destroyPlayer();
	}

	private void destroyPlayer() {
		if (player != null) {
			player.destroy();
			player = null;
		}
		if (skinLayout != null) {
			skinLayout.release();
		}
		if (playerLayoutController != null) {
			playerLayoutController.destroy();
			playerLayoutController = null;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		playerLayoutController.onKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}

	/** Start DefaultHardwareBackBtnHandler **/
	@Override
	public void invokeDefaultOnBackPressed() {
		super.onBackPressed();
	}
	/** End DefaultHardwareBackBtnHandler **/



	@Override
	public void onBackPressed() {
		if (null != playerLayoutController) {
			playerLayoutController.onBackPressed();
		} else {
			super.onBackPressed();
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
			if (null != player &&  null != player.getError()) {
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
		Log.d(TAG, text);
	}

	protected Options createOptions() {
		return new Options.Builder()
			.setShowPromoImage(false)
			.setShowNativeLearnMoreButton(false)
			.setUseExoPlayer(true)
			.build();
	}
}
