package com.ooyala.android.ads.ooyala;

import android.widget.FrameLayout;

import com.ooyala.android.AdIconInfo;
import com.ooyala.android.AdPodInfo;
import com.ooyala.android.AdsLearnMoreButton;
import com.ooyala.android.OoyalaAPIClient;
import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaException.OoyalaErrorCode;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayer.State;
import com.ooyala.android.PlayerInfo;
import com.ooyala.android.StateNotifier;
import com.ooyala.android.Utils;
import com.ooyala.android.item.AdSpot;
import com.ooyala.android.item.ContentItem;
import com.ooyala.android.player.AdMoviePlayer;
import com.ooyala.android.util.DebugMode;

import java.net.URL;
import java.util.List;

public class OoyalaAdPlayer extends AdMoviePlayer {
  private static String TAG = OoyalaAdPlayer.class.getName();

  private OoyalaAdSpot _ad;
  private Object _fetchTask;
  private boolean _playQueued = false;


  private int _topMargin;
  private FrameLayout _playerLayout;
  private AdsLearnMoreButton _learnMore;

  public OoyalaAdPlayer() {
    super();
  }

  @Override
  public void init(final OoyalaPlayer parent, AdSpot ad, StateNotifier notifier) {
    super.init(parent, ad, notifier);
    if (!(ad instanceof OoyalaAdSpot)) {
      this._error = new OoyalaException(OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "Invalid Ad");
      setState(State.ERROR);
      return;
    }
    DebugMode.logD(TAG, "Ooyala Ad Player Loaded");

    _seekable = false;
    _ad = (OoyalaAdSpot) ad;

    //If this ad tried to authorize and failed
    if(!_ad.isAuthorized() && _ad.getAuthCode() > 0) {
      this._error = new OoyalaException(OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "This ad was unauthorized to play: " + ContentItem.getAuthError(_ad.getAuthCode()));
      setState(State.ERROR);
      return;
    }

    if (_ad.getStream() == null || getBasePlayer() != null) {
      Utils.sharedExecutorService().submit(new Runnable() {
        @Override
        public void run() {
          PlayerInfo info = parent.getPlayerInfo();
          OoyalaAPIClient api = parent.getOoyalaAPIClient();
          if (!_ad.fetchPlaybackInfo(api, info)) {
            setState(State.ERROR);
            return;
          }
          parent.getHandler().post(new Runnable() {
            @Override
            public void run() {
              initAfterFetch(parent);
            }
          });
        }
      });
    } else {
      initAfterFetch(parent);
    }
  }

  private void initAfterFetch(OoyalaPlayer parent) {
    super.init(parent, _ad.getStreams());
    _playerLayout = parent.getLayout();
    //Add Learn More button if there is a click through URL
    if (_learnMore == null && _ad.getClickURL() != null && _parent.getOptions().getShowNativeLearnMoreButton()) {
      _topMargin = parent.getTopBarOffset();
      _learnMore = new AdsLearnMoreButton(_playerLayout.getContext(), this, _topMargin);
      _playerLayout.addView(_learnMore);
    }

    if (_ad.getTrackingURLs() != null) {
      for (URL url : _ad.getTrackingURLs()) {
        Utils.pingUrl(url);
      }
    }

    dequeuePlay();
    sendAdInfoNotification();
  }

  void sendAdInfoNotification() {
    String title = "";
    String description = "";
    String url = "";
    if(_ad.getClickURL() != null) {
      url = _ad.getClickURL().toString();
    }
    int adsCount = 1;
    int unplayedCount = 0;
    double skipoffset = -1.0;
    List<AdIconInfo> icons = null;
    _notifier.notifyAdStartWithAdInfo(new AdPodInfo(title,description,url,adsCount,unplayedCount, skipoffset, true,true, icons));
  }

  private void dequeuePlay() {
    if (_playQueued) {
      _playQueued = false;
      play();
    }
  }

  private void queuePlay() {
    _playQueued = true;
  }

  @Override
  public void play() {
    if (this.getBasePlayer() == null) {
      queuePlay();
      return;
    }
    super.play();
  }

  @Override
  public OoyalaAdSpot getAd() {
    return _ad;
  }

  /**
   * Called by OoyalaPlayer when going in and out of fullscreen using OoyalaPlayerLayoutController
   * @param layout the new layout to add the Learn More button
   * @param topMargin the pixels to shift the Learn More button down
   */
  @Override
  public void updateLearnMoreButton(FrameLayout layout, int topMargin) {
    //If topMargin did not change, return
    if (_topMargin == topMargin) {
      return;
    }

    if (_learnMore != null) {
      //Remove the Learn More button from the old playerLayout and set the new playerLayout
      _playerLayout.removeView(_learnMore);
      _playerLayout = layout;

      //Set the new topMargin and add the Learn More button to new playerLayout
      _learnMore.setTopMargin(topMargin);
      _playerLayout.addView(_learnMore);
    }
  }

  /**
   * Called by the Learn More button's onClick event.
   * Opens browser to click through URL
   */
  @Override
  public void processClickThrough() {
    String url = _ad.getClickURL().toString();
    if (url != null) {
      pause();
      queuePlay();
      Utils.openUrlInBrowser(_playerLayout.getContext(), url);
    }
  }

  @Override
  public void resume() {
    super.resume();

    //Bring Learn More button to front when play resumes so it does not get hidden beneath the video view.
    if (_learnMore != null) {
      _playerLayout.bringChildToFront(_learnMore);
    }

    dequeuePlay();
  }

  @Override
  public void destroy() {
    //Remove Learn More button if it exists
    if (_learnMore != null) {
      _playerLayout.removeView(_learnMore);
      _learnMore.destroy();
      _learnMore = null;
    }

    super.destroy();
  }

  @Override
  public void skipAd() {
    getNotifier().notifyAdSkipped();
    setState(State.COMPLETED);
  }
}
