package com.ooyala.sample.players;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

import java.util.Observable;
import java.util.Observer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
  private SDCardLogcatOoyalaEventsLogger log = new SDCardLogcatOoyalaEventsLogger();

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
      writePermission = true;
      asked = true;
    }
  }

  /** Start DefaultHardwareBackBtnHandler**/
  @Override
  public void invokeDefaultOnBackPressed() {
    super.onBackPressed();
  }
  /** End DefaultHardwareBackBtnHandler **/

  /** Start Activity methods for Skin **/
  @Override
  protected void onPause() {
    super.onPause();
    if (null != playerSkinLayoutController) {
      playerSkinLayoutController.onPause();
    }
    if (null != player) {
      player.suspend();
    }
    Log.d(TAG, "Player Activity Paused");
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (null != playerSkinLayoutController) {
      playerSkinLayoutController.onResume(this, this);
    }
    Log.d(TAG, "Player Activity Resumed");
    if (null != player) {
      player.resume();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (null != playerSkinLayoutController) {
      playerSkinLayoutController.onDestroy();
    }
  }

  @Override
  public void onBackPressed() {
    if (null != playerSkinLayoutController) {
      playerSkinLayoutController.onBackPressed();
    } else {
      super.onBackPressed();
    }
  }
  /** End Activity methods for Skin**/

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
  public void update(Observable arg0, Object argN) {
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
      final String msg = "Error Event Received";
      if (null != player && null != player.getError()) {
        Log.e(TAG, msg, player.getError());
      } else {
        Log.e(TAG, msg);
      }
      return;
    }

    if (arg1 == OoyalaSkinLayoutController.FULLSCREEN_CHANGED_NOTIFICATION_NAME) {
      Log.d(TAG, "Fullscreen Notification received : " + arg1 + " - fullScreen: " + ((OoyalaNotification) argN).getData());
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text = "Notification Received: " + arg1 + " - state: " + player.getState();
    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    log.writeToSdcardLog(text);

    Log.d(TAG, text);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if ((resultCode == RESULT_OK || resultCode == RESULT_CANCELED) && requestCode == CLICK_THROUGH_REQUEST) {
      Log.i("DemoIntegration", "Came back from clickthrough");
      onResume();
    }
  }
}
