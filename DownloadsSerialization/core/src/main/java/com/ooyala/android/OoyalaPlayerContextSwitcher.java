package com.ooyala.android;

import com.ooyala.android.item.Stream;
import com.ooyala.android.item.Video;
import com.ooyala.android.player.MoviePlayer;
import com.ooyala.android.player.PlayerInterface;
import com.ooyala.android.util.DebugMode;
import com.ooyala.android.OoyalaPlayer.State;

import java.util.Set;

/**
 * A class that handles all the different playback capabilities, and will eventually handle all
 * context switching for the ooyala Player (Between Ads, Cast, Content)
 */
class OoyalaPlayerContextSwitcher {
  private static final String TAG = OoyalaPlayerContextSwitcher.class.getName();

  OoyalaPlayer ooyalaPlayer;
  MoviePlayer contentPlayer;
  private CastManagerInterface castManager;
  private AdPluginManager adPluginManager;

  OoyalaPlayerContextSwitcher(OoyalaPlayer player, AdPluginManager adManager) {
    this.ooyalaPlayer = player;
    this.adPluginManager = adManager;
  }

  void setCastManager(CastManagerInterface castManager) {
    this.castManager = castManager;
  }

  CastManagerInterface getCastManager() {
    return castManager;
  }

  AdPluginManager getAdPluginManager() {
    return adPluginManager;
  }

  //TEMPORARY - until contentPlayer is solely in context switcher
  void setContentPlayer(MoviePlayer contentPlayer) {
    this.contentPlayer = contentPlayer;
  }

  void clearContentPlayer() {
    contentPlayer = null;
  }

  /**
   * Assuming current item is created and everything checks out, start the video workflow
   * Either go to cast mode, check for ads who want to control after content is changed, or create the contentPlayer
   * @param currentItem
   */
  void startVideoWorkflow(Video currentItem) {

    if (castManager != null && castManager.isConnectedToReceiverApp()) {
      DebugMode.logD(TAG, "switchToCastMode onChangeCurrentItemAfterFetch");
      ooyalaPlayer.switchToCastMode(currentItem.getEmbedCode());
    } else {
      if (!processAdModes(AdPluginManagerInterface.AdMode.ContentChanged, 0)) {
        contentPlayer = switchToContent(false);
      }
    }
  }

  /**
   * Get the current player, can be either content player or ad player
   *
   * @return the player
   */
  PlayerInterface currentPlayer() {
    PlayerInterface curPlayer;

    // if there is no embedcode, there should be play existed
    if (ooyalaPlayer.getEmbedCode() == null) {
      return null;
    }
    if (isInCastMode()) {
      curPlayer = castManager.getCastPlayer();
    } else if (adPluginManager.inAdMode()) {
      curPlayer = adPluginManager.getPlayerInterface();
    } else {
      curPlayer = contentPlayer;
    }
    return curPlayer;
  }

  /**
   * Create and initialize a content player for an item.
   *
   * @return
   */
  MoviePlayer createAndInitPlayer(MoviePlayerSelector selector, Video item, String closedCaptionsLanguage, boolean seekable) {
    MoviePlayer p = null;
    try {
      p = selector.selectMoviePlayer(item);
    } catch (OoyalaException e) {
      ooyalaPlayer.onError(e, "cannot initialize movie player");
      return null;
    }

    Set<Stream> streams = item.getStreams();

    // Initialize this player
    p.addObserver(ooyalaPlayer);
    p.init(ooyalaPlayer, streams);

    p.setLive(item.isLive());
    p.setClosedCaptionsLanguage(closedCaptionsLanguage);

    // Player must have been initialized, as well as player's basePlayer, in
    // order to continue
    if (p == null || p.getError() != null) {
      DebugMode.assertFail(TAG,
              "movie player has an error when initialize player");
      return null;
    }
    p.setSeekable(seekable);
    return p;
  }


  void suspendCurrentPlayer() {
    if (adPluginManager.inAdMode()) {
      adPluginManager.suspend();
    } else if (contentPlayer != null && !isInCastMode()) {
      contentPlayer.suspend();
    }
  }

  void resumeCurrentPlayer() {
    if (adPluginManager.inAdMode()) {
      adPluginManager.resume();
    } else if (contentPlayer != null) {
      // Connect to chromecast device in another activity and then come back to this ooyalaPlayer
      // In this case we need to check should we switch to cast mode
      if (castManager != null && castManager.isConnectedToReceiverApp()) {
        DebugMode.logD(TAG, "Switch to cast mode when resume current player");
        ooyalaPlayer.switchToCastMode(ooyalaPlayer.getCurrentItem().getEmbedCode());
      } else {
        contentPlayer.resume();
        ooyalaPlayer.playIfDesired();
      }
    }
  }

  /*
   * Process adMode event.  If something happens in the player, use this to determine if any
   * ad managers need ad mode for what particularly happened
   *
   * @return true if adManager require ad mode, false otherwise
   */
  boolean processAdModes(AdPluginManagerInterface.AdMode mode, int parameter) {
    boolean result = false;
    if (!isInCastMode()) {
      result = getAdPluginManager().onAdMode(mode, parameter);
    }
    if (result) {
      switchToAdMode();
    } else {
      processExitAdModes(mode, false);
    }
    return result;
  }

  /*
   * Process what happens after an ad mode is completed.
   *
   */
  void processExitAdModes(AdPluginManagerInterface.AdMode mode, boolean adsDidPlay) {
    if (adsDidPlay) {
      DebugMode.logD(TAG, "exit admode from mode " + mode.toString());
      ooyalaPlayer.sendNotification( OoyalaPlayer.AD_POD_COMPLETED_NOTIFICATION_NAME );
      ooyalaPlayer.showTVRatingAfterAd = true;
    }
    switch (mode) {
      case ContentChanged:
        DebugMode.logD(TAG, "post content changed");
        // cleanupPlayers(); disabling this call to cleanupPlayers(), see PBA-1750.
        if (ooyalaPlayer.getOptions().getPreloadContent()) {
          ooyalaPlayer.prepareContent(false);
        }

        if (ooyalaPlayer.getOptions().getShowPromoImage()) {
          ooyalaPlayer.stateManager.setState(State.LOADING);
          ooyalaPlayer.showPromoImage();
        } else if (!ooyalaPlayer.getOptions().getPreloadContent()) {
          // state will be set to ready by either prepare content or load promo
          // image
          // if both of them are disabled, directly set state to ready
          ooyalaPlayer.stateManager.setState(State.READY);
          ooyalaPlayer.playIfDesired();
        }
        break;
      case InitialPlay:
      case Playhead:
      case CuePoint:
      case PluginInitiated:
        if (adsDidPlay) {
          ooyalaPlayer.getHandler().post(new Runnable() {
            @Override
            public void run() {
              switchToContent(true);
            }
          });
        }
        break;
      case ContentFinished:
        ooyalaPlayer.onComplete();
        break;
      case ContentError:
        ooyalaPlayer.cleanupPlayers();
        ooyalaPlayer.onError(null, null);
        break;
      default:
        DebugMode.assertFail(TAG,
                "exitAdMode with unknown mode " + mode.toString() + "adsDidPlay "
                        + String.valueOf(adsDidPlay));
        break;
    }
  }

  /**
   * Something wants ad mode, physically force ad mode to happen by suspending the content player
   *  and inform the adPluginManager
   */
  void switchToAdMode() {
    DebugMode.logD(TAG, "switchToAdMode");

    ooyalaPlayer.showTVRatingAfterAd = false;
    int adPodStartTime = 0;
    if (contentPlayer != null) {
      contentPlayer.suspend();
      adPodStartTime = contentPlayer.currentTime();
    }

    // sends the main content playhead as the notification body
    ooyalaPlayer.sendNotification( OoyalaPlayer.AD_POD_STARTED_NOTIFICATION_NAME, adPodStartTime );
    ooyalaPlayer.hidePromoImage();
    getAdPluginManager().onAdModeEntered();
  }

  /**
   * Switch to the Content Player for whatever reason
   * @param forcePlay
   * @return
   */
  MoviePlayer switchToContent(boolean forcePlay) {
    DebugMode.logD(TAG, "switchToContent");

    if (contentPlayer == null) {
      ooyalaPlayer.prepareContent(forcePlay);
    } else if (contentPlayer.getState() == State.SUSPENDED) {
      if (forcePlay) {
        contentPlayer.resume(contentPlayer.timeToResume(), State.PLAYING);
      } else {
        contentPlayer.resume();
      }
    }
    ooyalaPlayer.maybeReshowTVRating();
    return contentPlayer;
  }


  public boolean isInCastMode() {
    return castManager != null && castManager.isInCastMode();
  }
}
