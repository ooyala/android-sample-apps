package com.ooyala.android;

import com.ooyala.android.OoyalaPlayer.State;
import com.ooyala.android.ads.vast.VASTAdSpot;
import com.ooyala.android.item.OoyalaManagedAdSpot;
import com.ooyala.android.item.Stream;
import com.ooyala.android.player.AdMoviePlayer;
import com.ooyala.android.player.Player;
import com.ooyala.android.player.PlayerInterface;
import com.ooyala.android.plugin.AdPluginInterface;
import com.ooyala.android.plugin.ManagedAdsPlugin;
import com.ooyala.android.util.DebugMode;

/**
 * Ooyala managed ads plugin manages ooyala and vast ads.
 */
public class OoyalaManagedAdsPlugin extends
    ManagedAdsPlugin<OoyalaManagedAdSpot> implements
    AdPluginInterface, StateNotifierListener {
  private static final String TAG = OoyalaManagedAdsPlugin.class.getName();
  private AdMoviePlayer _adPlayer;
  private boolean _seekable = false;
  protected OoyalaPlayer _player;
  private StateNotifier _stateNotifier;

  /**
   * Ooyala Managed Ads Plugin manages VAST and Ooyala ads
   */
  public OoyalaManagedAdsPlugin(OoyalaPlayer player) {
    super();
    _player = player;
    _stateNotifier = player.createStateNotifier();
    _stateNotifier.addListener(this);
  }

  /**
   * called when plugin should be reset
   */
  @Override
  public void reset() {
    // TODO Auto-generated method stub

  }

  /**
   * called when plugin should be suspended
   */
  @Override
  public void suspend() {
    if (_adPlayer != null) {
      _adPlayer.suspend();
    }
  }

  /**
   * called when plugin should be resumed
   */
  @Override
  public void resume() {
    if (_adPlayer != null) {
      _adPlayer.resume();
    }
  }

  /**
   * called when plugin should be resumed
   *
   * @param timeInMilliSecond
   *          playhead time to seek after resume
   * @param stateToResume
   *          player state after resume
   */
  @Override
  public void resume(int timeInMilliSecond, State stateToResume) {
    if (_adPlayer != null) {
      _adPlayer.resume(timeInMilliSecond, stateToResume);
    }
  }

  /**
   * called when plugin should be destroyed
   */
  @Override
  public void destroy() {
    if (_adPlayer != null) {
      _adPlayer.destroy();
    }
  }

  /**
   * called when content is changed
   */
  @Override
  public boolean onContentChanged() {
    super.onContentChanged();
    _adSpotManager.insertAds(_player.getCurrentItem().getAds());
    if (Stream.streamSetContainsDeliveryType(_player.getCurrentItem()
        .getStreams(), Stream.DELIVERY_TYPE_HLS)) {
      _adSpotManager.setAlignment(10000);
    }
    return false;
  }

  /**
   * Insert an Ooyala Managed Ad into the plugin's list of ads
   * @param adSpot either an OoyalaAdSpot or VASTAdSpot
   */
  public void insertAd(OoyalaManagedAdSpot adSpot) {
    _adSpotManager.insertAd(adSpot);
  }

  private boolean initializeAdPlayer(AdMoviePlayer p, OoyalaManagedAdSpot ad) {
    if (p == null) {
      DebugMode.assertFail(TAG, "initializeAdPlayer when adPlayer is null");
      return false;
    }
    if (ad == null) {
      DebugMode.assertFail(TAG, "initializeAdPlayer when ad is null");
      return false;
    }

    p.init(_player, ad, _stateNotifier);
    // if (p.getError() != null) {
    // return false;
    // }
    p.setSeekable(_seekable);
    return true;
  }

  private boolean initializeAd(OoyalaManagedAdSpot ad) {
    DebugMode.logD(TAG, "Ooyala Player: Playing Ad");

    cleanupExistingAdPlayer();

    AdMoviePlayer adPlayer = null;
    try {
      Class<? extends AdMoviePlayer> adPlayerClass = _player
          .getAdPlayerClass(ad);
      if (adPlayerClass != null) {
        adPlayer = adPlayerClass.newInstance();
      }
    } catch (InstantiationException e) {
      DebugMode.assertFail(TAG, e.toString());
    } catch (IllegalAccessException e) {
      DebugMode.assertFail(TAG, e.toString());
    }

    if (adPlayer == null) {
      return false;
    }

    _adPlayer = adPlayer;
    if (!initializeAdPlayer(adPlayer, ad)) {
      _adPlayer = null;
      return false;
    }

    return true;
  }

  private void cleanupExistingAdPlayer() {
    // If an ad is playing, take it out of playedAds, and save it for playback
    // later
    if (_adPlayer != null) {
      cleanupPlayer(_adPlayer);
      _adPlayer = null;
    }
  }

  /**
   * event observer
   */
  @Override
  public void onStateChange(StateNotifier notifier) {

    if (_adPlayer == null || _adPlayer.getNotifier() != notifier) {
      return;
    }

    switch (notifier.getState()) {
    case COMPLETED:
      if (!playAdsBeforeTime()) {
        notifier.notifyAdCompleted();
        cleanupPlayer(_adPlayer);
        _player.exitAdMode(this);
      }
      break;
    case ERROR:
      DebugMode.logE(TAG, "Error recieved from Ad.  Cleaning up everything");
      cleanupPlayer(_adPlayer);
      _player.exitAdMode(this);
      break;
    default:
      break;
    }
  }

  public void setSeekable(boolean s) {
    _seekable = s;
    if (_adPlayer != null) {
      _adPlayer.setSeekable(_seekable);
    }
  }

  private void cleanupPlayer(Player p) {
    if (p != null) {
      p.destroy();
    }
  }

  @Override
  protected boolean playAd(OoyalaManagedAdSpot ad) {
    if (!initializeAd(ad)) {
      return false;
    }
    _adPlayer.play();
    return true;
  }

  @Override
  protected void processNonLinearAd(OoyalaManagedAdSpot ad) {
    if (ad instanceof VASTAdSpot) {
      VASTAdSpot vastAd = (VASTAdSpot)ad;

      // if this is a VMAP, insert ads and return;
      if (vastAd.getVMAPAdSpots() != null && vastAd.getVMAPAdSpots().size() > 0) {
        // this is a vmap spot, refresh ads with vmap spots
        _adSpotManager.insertAds(vastAd.getVMAPAdSpots());
      } else {
        AdOverlayInfo info = ((VASTAdSpot) ad).getAdOverlayInfo();
        if (info != null) {
          _stateNotifier.notifyAdOverlay(info);
        }
      }
    }
  }

  @Override
  public void skipAd() {
    if (_adPlayer != null) {
      _adPlayer.skipAd();
    }
  }

  /**
   * get the ad player, used to update UI controls
   *
   * @return the ad player
   */
  @Override
  public PlayerInterface getPlayerInterface() {
    return _adPlayer;
  }

  @Override
  public void processClickThrough() {
    if (_adPlayer != null) {
      _adPlayer.processClickThrough();
    }
  }

  @Override
  public void onAdIconClicked(int index) {
    if (_adPlayer != null) {
      _adPlayer.onAdIconClicked(index);
    }
  }
}
