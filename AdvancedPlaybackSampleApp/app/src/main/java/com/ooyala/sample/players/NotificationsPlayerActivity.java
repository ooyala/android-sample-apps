package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.notifications.BitrateChangedNotificationInfo;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.R;

import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you can play a video with an initial time set
 *
 * This can be used in conjunction with Cross Device Resume (XDR) to start videos where
 * an end user left off
 *
 *
 */
public class NotificationsPlayerActivity extends Activity implements Observer {
  public final static String getName() {
    return "Notifications Demonstration";
  }
  final String TAG = this.getClass().toString();

  String EMBED = null;
  String PCODE = null;
  String DOMAIN = null;

  protected OoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayer player;

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger playbacklog = new SDCardLogcatOoyalaEventsLogger();

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());
    setContentView(R.layout.player_simple_layout);

    EMBED = getIntent().getExtras().getString("embed_code");
    PCODE = getIntent().getExtras().getString("pcode");
    DOMAIN = getIntent().getExtras().getString("domain");

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

    Options options = new Options.Builder().setUseExoPlayer(true).build();
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);
    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
    player.addObserver(this);

    if (player.setEmbedCode(EMBED)) {
      player.play();
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


/** Handle all notifications from the OoyalaPlayer
  *  Filter on "Note" in logcat logs to see key points in the notification workflow
  *  Filter on "Notification Received:" in logcat logs to see all notifications in the notification workflow
  */
  /** NOTE: there could also be UI-related notifications from your OoyalaPlayerLayoutController or SkinViewController that are not represented here **/
  @Override
  public void update(Observable arg0, Object argN) {
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);

    switch (arg1) {
      case OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME:
        // Ignore TimeChanged Notifications for shorter logs
        return;

      case OoyalaPlayer.EMBED_CODE_SET_NOTIFICATION_NAME:
        Log.d("Note", "The Embed Code has been set, effectively restarting the OoyalaPlayer");
        break;

      // Notifications for when Ooyala API requests are completed
      case OoyalaPlayer.CONTENT_TREE_READY_NOTIFICATION_NAME:
        break;
      case OoyalaPlayer.METADATA_READY_NOTIFICATION_NAME:
        break;
      case OoyalaPlayer.AUTHORIZATION_READY_NOTIFICATION_NAME:
        break;

      // Notification when player has all information from Ooyala APIs
      case OoyalaPlayer.CURRENT_ITEM_CHANGED_NOTIFICATION_NAME:
        Log.d("Note", "Current Item has now changed, and all metadata has been updated for it");
        break;

      // Notification when the playback starts or completes
      case OoyalaPlayer.PLAY_STARTED_NOTIFICATION_NAME:
        Log.d("Note", "The player has started playback of this asset for the first time");
        break;
      case OoyalaPlayer.PLAY_COMPLETED_NOTIFICATION_NAME:
        Log.d("Note", "The player has reached the end of the current asset");
        break;

      // Notification when the player goes into one of the OoyalaPlayer State
      case OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME:
        switch (player.getState()) {

          /** Initial state, player is created but no content is loaded */
          case INIT:
            break;

          /** Loading content */
          case LOADING:
            Log.d("Note", "State: Video is buffering");
            break;
          case READY:
            Log.d("Note", "State: Video is initially ready");
            break;
          case PLAYING:
            Log.d("Note", "State: Video is playing");
            break;
          case PAUSED:
            Log.d("Note", "State: Video is paused");
            break;
          case COMPLETED:
            Log.d("Note", "State: Video is now complete");
            break;
          case SUSPENDED:
            Log.d("Note", "State: Video is now suspended");
            break;
          case ERROR:
            /** NOTE: It's suggested to listen to the Error Notification instead of handling errors here **/
            Log.d("Note", "State: Video has run into an error");
          default:
            break;

        }
        break;
      case OoyalaPlayer.DESIRED_STATE_CHANGED_NOTIFICATION_NAME:
        switch (player.getDesiredState()) {
          case DESIRED_PLAY:
            Log.d("Note", "Desired state is playing. After any loading, video should be actively rendering");
            break;
          case DESIRED_PAUSE:
            Log.d("Note", "Desired state is paused. Video should not be actively rendering while desired state is paused");
            break;
          default:
            break;
        }
        break;
      case OoyalaPlayer.BUFFER_CHANGED_NOTIFICATION_NAME:
        break;

      case OoyalaPlayer.SEEK_STARTED_NOTIFICATION_NAME:
        Log.d("Note", "Seek Started");
        break;
      case OoyalaPlayer.SEEK_COMPLETED_NOTIFICATION_NAME:
        Log.d("Note", "Seek Complete");
        break;

      case OoyalaPlayer.ERROR_NOTIFICATION_NAME:
        Log.d("Note", "Playback Error. Code:" + player.getError().getCode() + ", Description: " + player.getError().getMessage());
        break;

      case OoyalaPlayer.CC_STYLING_CHANGED_NOTIFICATION_NAME:
        break;

      case OoyalaPlayer.AD_OVERLAY_NOTIFICATION_NAME:
        break;
      case OoyalaPlayer.AD_STARTED_NOTIFICATION_NAME:
        break;
      case OoyalaPlayer.AD_COMPLETED_NOTIFICATION_NAME:
        break;
      case OoyalaPlayer.AD_POD_STARTED_NOTIFICATION_NAME:
        Log.d("Note", "An ad manager has taken control of the OoyalaPlayer");
        break;
      case OoyalaPlayer.AD_POD_COMPLETED_NOTIFICATION_NAME:
        Log.d("Note", "An ad manager has returned control of the OoyalaPlayer");
        break;
      case OoyalaPlayer.AD_SKIPPED_NOTIFICATION_NAME:
        break;
      case OoyalaPlayer.AD_ERROR_NOTIFICATION_NAME:
        Log.d("Note", "Ad Playback Error");
        break;


      // NOTE: Unused for BaseStreamPlayer. However, these are usable in ExoPlayer
      case OoyalaPlayer.BUFFERING_STARTED_NOTIFICATION_NAME:
        break;
      // NOTE: Unused for BaseStreamPlayer. However, these are usable in ExoPlayer
      case OoyalaPlayer.BUFFERING_COMPLETED_NOTIFICATION_NAME:
        break;

      // Deprecated: Used specifically for DRM notifications within SecurePlayer playback
      case OoyalaPlayer.DRM_RIGHTS_ACQUISITION_STARTED_NOTIFICATION_NAME:
        break;

      // Deprecated: Used specifically for DRM notifications within SecurePlayer playback
      case OoyalaPlayer.DRM_RIGHTS_ACQUISITION_COMPLETED_NOTIFICATION_NAME:
        break;

      case OoyalaPlayer.LIVE_CC_AVAILABILITY_CHANGED_NOTIFICATION_NAME:
        break;
      case OoyalaPlayer.LIVE_CC_CHANGED_NOTIFICATION_NAME:
        break;
      case OoyalaPlayer.CC_CHANGED_NOTIFICATION_NAME:
        break;
      case OoyalaPlayer.CLOSED_CAPTIONS_LANGUAGE_CHANGED_NAME:
        break;
      case OoyalaPlayer.BITRATE_CHANGED_NOTIFICATION_NAME:
        BitrateChangedNotificationInfo info = (BitrateChangedNotificationInfo)((OoyalaNotification) argN).getData();
        Log.d("Note", "Bitrate Changed. Old: " + info.getOldBitrate() + ", New: " + info.getNewBitrate());
        break;
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text="Notification Received: " + arg1 + " - state: " + player.getState();
    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    playbacklog.writeToSdcardLog(text);

    //Generic notification handler
    Log.d(TAG, text);
  }

}
