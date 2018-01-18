package com.ooyala.android.analytics;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.SeekInfo;
import com.ooyala.android.item.Video;
import com.ooyala.android.util.DebugMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A class that manages multiple analytics plugins for reporting important analytics events
 */
public class AnalyticsPluginManager implements AnalyticsPluginManagerInterface, Observer {
  private static final String TAG = AnalyticsPluginManager.class.getSimpleName();

  private OoyalaPlayer player;
  private List<AnalyticsPluginInterface> plugins = new ArrayList<>();

  private PlayingReportingState playingReportingState;
  private enum PlayingReportingState {
    PLAYER_NOT_INITIALIZED,
    CURRENT_ITEM_CHANGED_BUT_START_NOT_REPORTED,
    PLAYING_AFTER_START_REPORTED,
    COMPLETE_BUT_REPLAY_NOT_REPORTED,
  }

  private enum ReportType {
    ITEM_ABOUT_TO_PLAY,
    PLAYER_LOAD,
    PLAY_REQUESTED,
    PLAY_STARTED,
    PLAY_PAUSED,
    PLAY_RESUMED,
    PLAY_COMPLETED,
    REPLAY,
    PLAYHEAD,
    SEEK
  }

  private boolean analyticsEnabled;

  private boolean isPlaying;
  /**
   * Create an Analytics Plugin Manager.  Should not used explicitly within applications
   *
   * @param player
   *          the Ooyala Player who owns the Analytics plugin manager
   */
  public AnalyticsPluginManager(OoyalaPlayer player) {
    this.player = player;
    this.playingReportingState = PlayingReportingState.PLAYER_NOT_INITIALIZED;
    this.player.addObserver(this);
    this.analyticsEnabled = true;
  }

  /************* AnalyticsPluginManagerInterface Interface Start ***********/

  @Override
  public boolean registerPlugin(final AnalyticsPluginInterface plugin) {
    if (plugins.contains(plugin)) {
      DebugMode.logD(TAG, "Analytics plugin " + plugin.toString() + "already exists");
      return false;
    }

    for (AnalyticsPluginInterface p : plugins) {
      if (plugin.getClass() == p.getClass()) {
        DebugMode.logD(TAG, "Analytics plugin " + p.toString() + " is same class as "
                + plugin.toString());
      }
    }

    DebugMode.logD(TAG, "Registered analytics plugin" + plugin.toString());
    plugins.add(plugin);

    if (analyticsEnabled) {
      plugin.reportPlayerLoad();
    } else {
      DebugMode.logE(TAG, "Adding an Analytics Plugin, even though Analytics are disabled");
    }
    return true;
  }

  @Override
  public boolean deregisterPlugin(final AnalyticsPluginInterface plugin) {
    if (!plugins.contains(plugin)) {
      DebugMode.logD(TAG, plugin.toString()
              + "is not registered or has been removed");
      return false;
    }

    plugins.remove(plugin);
    DebugMode.logD(TAG, "Deregistered analytics plugin" + plugin.toString());
    return true;
  }

  /**
   * Resume Analytics reporting.  Only to be used after Analytics is disabled.
   */
  public void enableAnalytics() {
    DebugMode.logD(TAG, "Enabled Analytics");
    DebugMode.assertEquals(analyticsEnabled, false, TAG, "Analytics being enabled when it was already enabled");
    analyticsEnabled = true;
  }

  /**
   * Stop Analytics from being reported to any analytics plugin
   */
  public void disableAnalytics() {
    DebugMode.logD(TAG, "Disabled Analytics");
    DebugMode.assertEquals(analyticsEnabled, true, TAG, "Analytics being disabled when it was already disabled");
    analyticsEnabled = false;
  }
/************* AnalyticsPluginManagerInterface Interface End ***********/

/************* Observer Interface Start ***********/
  @Override
  public void update(Observable observable, Object parameter) {

    if (observable != player) {
      return;
    }

    final String arg1 = OoyalaNotification.getNameOrUnknown(parameter);

    // If a new video is selected, prepare to fire PLAY_STARTED again
    if (arg1 == OoyalaPlayer.CURRENT_ITEM_CHANGED_NOTIFICATION_NAME) {
      playingReportingState = PlayingReportingState.CURRENT_ITEM_CHANGED_BUT_START_NOT_REPORTED;
      notifyPlugins(ReportType.ITEM_ABOUT_TO_PLAY, player.getCurrentItem());
      isPlaying = false;

      // If there was a playRequested (DesiredState == DESIRED_PLAY) that happened before currentItemChanged, re-play that event
      // One could consider a queue of unplayed events, but it doesn't show too much value other than this specific event.
      if (player.getDesiredState() == OoyalaPlayer.DesiredState.DESIRED_PLAY) {
        notifyPlugins(ReportType.PLAY_REQUESTED, null);
      }
    // If Play Completed, remember so we can replay if we see PLAYING again
    } else if (arg1 == OoyalaPlayer.PLAY_COMPLETED_NOTIFICATION_NAME) {
      playingReportingState = PlayingReportingState.COMPLETE_BUT_REPLAY_NOT_REPORTED;
      notifyPlugins(ReportType.PLAY_COMPLETED, null);
    } else if (arg1 == OoyalaPlayer.DESIRED_STATE_CHANGED_NOTIFICATION_NAME) {

      //If we are told to start playback, report Play Requested
      if (player.getDesiredState() == OoyalaPlayer.DesiredState.DESIRED_PLAY) {
        notifyPlugins(ReportType.PLAY_REQUESTED, null);
      }
    } else if (arg1 == OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME) {
      if (player.isAdPlaying()) {
        return;
      }
      OoyalaPlayer.State playerState = player.getState();
      if (playerState == OoyalaPlayer.State.PLAYING) {
        if (!isPlaying) {
          isPlaying = true;
          //If first time we notice video is successfully playing back
          if (playingReportingState == PlayingReportingState.CURRENT_ITEM_CHANGED_BUT_START_NOT_REPORTED) {
            playingReportingState = PlayingReportingState.PLAYING_AFTER_START_REPORTED;
            notifyPlugins(ReportType.PLAY_STARTED, null);
            // If playing after video complete, report replay
          } else if (playingReportingState == PlayingReportingState.COMPLETE_BUT_REPLAY_NOT_REPORTED) {
            playingReportingState = PlayingReportingState.PLAYING_AFTER_START_REPORTED;
            notifyPlugins(ReportType.REPLAY, null);
          } else {
            notifyPlugins(ReportType.PLAY_RESUMED, null);
          }
        }
      } else if (playerState == OoyalaPlayer.State.PAUSED ||
                 playerState == OoyalaPlayer.State.SUSPENDED ||
                 playerState == OoyalaPlayer.State.ERROR ||
                 playerState == OoyalaPlayer.State.COMPLETED) {
        if (isPlaying) {
          isPlaying = false;
          notifyPlugins(ReportType.PLAY_PAUSED, null);
        }
      }
    } else if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      
      //Only report playhead updates if we're playing content
      if (!player.isAdPlaying()) {
        Integer playhead = player.getPlayheadTime();
        notifyPlugins(ReportType.PLAYHEAD, playhead);
      }
    } else if(arg1 == OoyalaPlayer.SEEK_STARTED_NOTIFICATION_NAME) {
      notifyPlugins(ReportType.SEEK, parameter);          //send seek info object

    }
  }

/************* Observer Interface End ***********/


    //Notes:
    // NEW: OoyalaPlayerLoaded =   void reportPlayerLoad();
  // DesiredState -> PLAYING = reportPlayRequested
  //  // ActualPlaybackState -> PLAYING , and first time since CURRENT_ITEM_CHANGED = void reportPlayStarted();
    // ActualPlaybackSTate -> PLAYING after COMPLETED   void reportReplay();
  // PLAYHEAD_TIME_CHANGED =   void reportPlayheadUpdate();

  private void notifyPlugins(ReportType type, Object parameter) {
    if (!analyticsEnabled) {
      return;
    }

    for (AnalyticsPluginInterface p : plugins) {
      switch (type) {
        case ITEM_ABOUT_TO_PLAY:
          p.onCurrentItemAboutToPlay((Video)parameter);
          break;
        case PLAYER_LOAD:
          p.reportPlayerLoad();
          break;
        case PLAY_REQUESTED:
          p.reportPlayRequested();
          break;
        case PLAY_STARTED:
          p.reportPlayStarted();
          break;
        case PLAY_PAUSED:
          p.reportPlayPaused();
          break;
        case PLAY_RESUMED:
          p.reportPlayResumed();
          break;
        case PLAY_COMPLETED:
          p.reportPlayCompleted();
          break;
        case REPLAY:
          p.reportReplay();
          break;
        case PLAYHEAD:
          p.reportPlayheadUpdate((Integer)parameter);
          break;
        case SEEK:
          OoyalaNotification ooyalaNotification = (OoyalaNotification) parameter;
          SeekInfo seekInfo = (SeekInfo) ooyalaNotification.getData();
          p.reportSeek(seekInfo);
          break;
      }
    }
  }
}
