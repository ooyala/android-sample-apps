package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.utils.CustomPlayerInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * This class asks permission for WRITE_EXTERNAL_STORAGE. We need it for automation hooks
 * as we need to write into the SD card and automation will parse this file.
 */
public abstract class AbstractHookActivity extends Activity implements Observer, DefaultHardwareBackBtnHandler {

	public static final String EXTRA_EMBED_CODE = "embed_code";
	public static final String EXTRA_PCODE = "pcode";
	public static final String EXTRA_DOMAIN = "domain";
	public static final String EXTRA_AUTO_PLAY = "autoPlay";
	public static final String EXTRA_SELECTED_FORMAT = "selectedFormat";
	public static final String EXTRA_MARKERS = "markers";

	private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
	String TAG = this.getClass().toString();
	protected OoyalaSkinLayoutController playerLayoutController;

	SDCardLogcatOoyalaEventsLogger log = new SDCardLogcatOoyalaEventsLogger();

	protected String embedCode;
	protected String pcode;
	protected String domain;
	protected String apiKey;
	protected String secret;
	protected String accountId;
	protected String selectedFormat;
	protected String hevcMode;
	protected boolean isStaging;
	protected String markersFileName = "";

	OoyalaPlayer player;
	protected OoyalaSkinLayout skinLayout;


	boolean writePermission = false;
	boolean asked = false;
	boolean autoPlay = false;

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
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			embedCode = extras.getString(EXTRA_EMBED_CODE);
			pcode = extras.getString(EXTRA_PCODE);
			domain = extras.getString(EXTRA_DOMAIN);
			autoPlay = extras.getBoolean(EXTRA_AUTO_PLAY,false);
			apiKey = extras.getString("apiKey");
			secret = extras.getString("secret");
			accountId = extras.getString("accountId");
			selectedFormat = extras.getString(EXTRA_SELECTED_FORMAT,"default");
			hevcMode = extras.getString("hevc_mode","NoPreference");
			isStaging = extras.getBoolean("is_staging",false);
			markersFileName = extras.getString(EXTRA_MARKERS,"");
		}
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
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "Player Activity Restarted");
		if (null != player) {
			player.resume();
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
			if (null != player && null != player.getError()) {
				Log.e(TAG, msg, player.getError());
			}
			else {
				Log.e(TAG, msg);
			}
			return;
		}

		if (arg1.equalsIgnoreCase(OoyalaSkinLayoutController.FULLSCREEN_CHANGED_NOTIFICATION_NAME)) {
			Log.d(TAG, "Fullscreen Notification received : " + arg1 + " - fullScreen: " + ((OoyalaNotification)argN).getData());
		}

		// Automation Hook: to write Notifications to a temporary file on the device/emulator
		String text = getLog(argN);
		// Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
		log.writeToSdcardLog(text);
		Log.d(TAG, text);
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
		if (null != player) {
			player.suspend();
		}
		if (null != playerLayoutController) {
			playerLayoutController.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (null != playerLayoutController) {
			playerLayoutController.onResume( this, this );
		}
	}

	@Override
	public void onBackPressed() {
		if (null != playerLayoutController) {
			playerLayoutController.onBackPressed();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != playerLayoutController) {
			playerLayoutController.onDestroy();
		}
	}

  private String getLog(Object argN) {
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    final Object data = ((OoyalaNotification) argN).getData();
    String text = "Notification Received: " + arg1 + " - state: " + player.getState();

    if (arg1.equalsIgnoreCase(OoyalaPlayer.MULTI_AUDIO_ENABLED_NOTIFICATION_NAME)) {
      if (data != null && data instanceof Boolean) {
        boolean isMultiAudioEnabled = (Boolean) data;
        String multiAudioState = isMultiAudioEnabled ? " is enabled" : " is disabled";
        text = "Notification Received: " + arg1 + multiAudioState + " - state: " + player.getState();
      }
    }
    return text;
  }

  protected Options getOptions(){
	  Options.Builder optionBuilder = new Options.Builder().setShowNativeLearnMoreButton(false).setShowPromoImage(false).setUseExoPlayer(true);
	  if(!selectedFormat.equalsIgnoreCase("default")) {
		  optionBuilder.setPlayerInfo(new CustomPlayerInfo(selectedFormat));
	  }
	  if(hevcMode.equalsIgnoreCase("HEVCPreferred")) {
		  optionBuilder.enableHevc(true);
	  }
	  else if(hevcMode.equalsIgnoreCase("HEVCNotPreferred")) {
		  optionBuilder.enableHevc(false);
	  }
	  if (!markersFileName.isEmpty()) {
	  	  try {
			  optionBuilder.setMarkers(loadJSONFromAsset(markersFileName));
		  }  catch (JSONException e) {
              Log.e(TAG, "Exception Thrown while json file loading from assets", e);
	  	  }
	  }
	  Options options =  optionBuilder.build();
	  return options;
  }

	/**
	 *
	 * @param name of asset file in a JSON format
	 * @return asset file as JSON object
	 */
	public JSONObject loadJSONFromAsset(String name) throws JSONException {
		String json;
		try {
			InputStream is = getAssets().open(name);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return new JSONObject(json);
	}
}
