package com.ooyala.sample.players;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.performance.PerformanceMonitor;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

import java.util.Observable;
import java.util.Observer;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


abstract public class AbstractHookActivity extends Activity implements Observer {
	private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


	private String TAG = this.getClass().toString();

	private SDCardLogcatOoyalaEventsLogger log = new SDCardLogcatOoyalaEventsLogger();
	protected OptimizedOoyalaPlayerLayoutController ooyalaPlayerLayoutController;
	protected OoyalaPlayerLayoutController playerLayoutController;
	protected PerformanceMonitor performanceMonitor;
	protected String embedCode;
	protected String pcode;
	protected String domain;

	protected OoyalaPlayer player;

	boolean writePermission = false;
	boolean asked = false;

	final String PERFORMANCE_MONITOR_TAG = "MONITOR_" + TAG;

	// complete player setup after we asked for permission to write into external storage
	abstract void completePlayerSetup(final boolean asked);

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
		} else {
			writePermission = true;
			asked = true;
		}

		embedCode = getIntent().getExtras().getString("embed_code");
		pcode = getIntent().getExtras().getString("pcode");
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
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "Player Activity Stopped");
		if (null != player) {
			player.suspend();
		}
		if(TAG.equalsIgnoreCase("class com.ooyala.sample.players.InsertAdPlayerActivity"))
		{
			Log.d(PERFORMANCE_MONITOR_TAG, performanceMonitor.buildStatisticsSnapshot().generateReport());
			performanceMonitor.destroy();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "App Restarted");
		if (player != null && player != null) {
			player.resume();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		final String arg1 = OoyalaNotification.getNameOrUnknown(arg);
		// pass player's notifications to performance monitor
		if(TAG.equalsIgnoreCase("com.ooyala.sample.players.InsertAdPlayerActivity")) {
			performanceMonitor.update(o, arg);
		}
		if (arg1.equals(OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME)) {
			if (TAG.equalsIgnoreCase("class com.ooyala.sample.players.ProgrammaticVolumePlayerActivity")) {
				player.setVolume(player.getVolume() + .025f);
				return;
			}
			return;
		}
		String text = "Notification Received: " + arg1 + " - state: " + player.getState();
		Log.d(TAG, text);

		if (writePermission) {
			Log.d(TAG, "Writing log to SD card");
			// Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
			log.writeToSdcardLog(text);
		}
	}
}