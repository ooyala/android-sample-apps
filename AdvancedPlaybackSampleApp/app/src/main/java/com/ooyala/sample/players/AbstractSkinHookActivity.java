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
public abstract class AbstractSkinHookActivity extends Activity implements Observer, DefaultHardwareBackBtnHandler {
  String TAG = this.getClass().toString();

  private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

  protected OoyalaSkinLayoutController playerLayoutController;
  protected String embedCode;
  protected String pcode;
  protected String domain;
  protected String selectedFormat;

  protected OoyalaPlayer player;
  protected OoyalaSkinLayout skinLayout;

  protected boolean writePermission = false;
  protected boolean asked = false;
  protected boolean autoPlay = false;

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
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      embedCode = extras.getString("embed_code");
      pcode = extras.getString("pcode");
      domain = extras.getString("domain");
      autoPlay = extras.getBoolean("autoPlay",false);
      selectedFormat = extras.getString("selectedFormat","default");
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
}
