package com.ooyala.android.ads.vast;

import android.widget.FrameLayout;

import com.ooyala.android.AdIconInfo;
import com.ooyala.android.AdOverlayInfo;
import com.ooyala.android.AdPodInfo;
import com.ooyala.android.AdsLearnMoreButton;
import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaException.OoyalaErrorCode;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayer.State;
import com.ooyala.android.StateNotifier;
import com.ooyala.android.Utils;
import com.ooyala.android.item.AdSpot;
import com.ooyala.android.player.AdMoviePlayer;
import com.ooyala.android.player.StreamPlayer;
import com.ooyala.android.util.DebugMode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Set;

/**
 * A MoviePlayer which helps render VAST advertisements
 */
public class VASTAdPlayer extends AdMoviePlayer {
  private VASTAdSpot _ad;
  private List<Linear> _linearAdQueue = new ArrayList<>();
  private NonLinearAds nonLinearAds;
  private static String TAG = VASTAdPlayer.class.getName();
  private boolean _startSent = false;
  private boolean _firstQSent = false;
  private boolean _midSent = false;
  private boolean _thirdQSent = false;
  private boolean _playQueued = false;

  private int _topMargin;
  private FrameLayout _playerLayout;
  private AdsLearnMoreButton _learnMore;
  private int _adIndex;
  private ArrayList<Boolean> _iconViewTracker;

  private interface TrackingEvent {
    public static final String CREATIVE_VIEW = "creativeView";
    public static final String START = "start";
    public static final String FIRST_QUARTILE = "firstQuartile";
    public static final String MIDPOINT = "midpoint";
    public static final String THIRD_QUARTILE = "thirdQuartile";
    public static final String COMPLETE = "complete";
    public static final String PAUSE = "pause";
    public static final String RESUME = "resume";
  }

  @Override
  public void init(final OoyalaPlayer parent, AdSpot ad, StateNotifier notifier) {
    super.init(parent, ad, notifier);
    if (!(ad instanceof VASTAdSpot)) {
      this._error = new OoyalaException(OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "Invalid Ad");
      setState(State.ERROR);
      return;
    }
    DebugMode.logD(TAG, "VAST Ad Player Loaded");

    _seekable = false;
    _ad = (VASTAdSpot) ad;
    if (!_ad.isInfoFetched()) {
      Utils.sharedExecutorService().submit(new Runnable() {
        @Override
        public void run() {
          if (!_ad.fetchPlaybackInfo(parent.getOoyalaAPIClient(), parent.getPlayerInfo())) {
            setState(State.ERROR);
            return;
          }

          parent.getHandler().post(new Runnable() {
            @Override
            public void run() {
              if (!initAfterFetch(parent)) {
                _error = new OoyalaException(OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "Bad VAST Ad");
                setState(State.ERROR);
              }
            }
          });

        }
      });
    } else {
      // ad info is fetched, report any parsing errors.
      if (!initAfterFetch(parent)) {
        _error = new OoyalaException(OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "Bad VAST Ad");
        setState(State.ERROR);
      }
    }
  }

  private boolean initAfterFetch(OoyalaPlayer parent) {
    reportAdErrors();

    _adIndex = 0;
    for (Ad ad : _ad.getAds()) {
      // Add to the list of impression URLs to be called when player is loaded
      if (ad.getLinearCreative() != null &&
          ad.getLinearCreative().getLinear() != null &&
          ad.getLinearCreative().getLinear().getStream() != null) {
          _linearAdQueue.add(ad.getLinearCreative().getLinear());
      } else if (ad.getNonLinearCreatives() != null && ad.getNonLinearCreatives().size() > 0) {
        nonLinearAds = ad.getNonLinearCreatives().get(0).getNonLinearAds();
      }
    }

    if (_linearAdQueue.isEmpty()) {
      AdOverlayInfo info = _ad.getAdOverlayInfo();
      if (info != null) {
        _notifier.notifyAdOverlay(info);
        setState(State.COMPLETED);
        return true;
      } else {
        return false;
      }
    }
    if (_linearAdQueue.get(0) == null || _linearAdQueue.get(0).getStreams() == null)  { return false; }

    addQuartileBoundaryObserver();

    super.init(parent, _linearAdQueue.get(0).getStreams());

    //Get the _playerLayout and _topMargin for the Learn More button
    _playerLayout = parent.getLayout();
    _topMargin = parent.getTopBarOffset();

    //Add Learn More button if there is a click through URL
    if (currentLinearAd() != null && currentLinearAd().getClickThroughURL() != null && _parent.getOptions().getShowNativeLearnMoreButton()) {
      _learnMore = new AdsLearnMoreButton(_playerLayout.getContext(), this, _topMargin);
      _playerLayout.addView(_learnMore);
    }

    if (_ad.getTrackingURLs() != null) {
      for (URL url : _ad.getTrackingURLs()) {
        Utils.pingUrl(url);
      }
    }

    dequeuePlay();
    return true;
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
    if (_linearAdQueue.isEmpty()) {
      setState(State.COMPLETED);
      return;
    }

    if (currentTime() != 0) {
      sendTrackingEvent(TrackingEvent.RESUME);
    }

    super.play();
  }

  @Override
  public void pause() {
    if (_linearAdQueue.isEmpty()) {
      setState(State.COMPLETED);
      return;
    }
    if (getState() != State.PLAYING) {
      sendTrackingEvent(TrackingEvent.PAUSE);
    }
    super.pause();
  }

  @Override
  public void resume() {
    super.resume();

    //Bring Learn More button to front when play resumes so it does not get hidden beneath the video view.
    if (_learnMore != null) {
      _playerLayout.bringChildToFront(_learnMore);
    }
  }

  @Override
  public VASTAdSpot getAd() {
    return _ad;
  }

  @Override
  protected void setState(State state) {
    //look for state changing to complete here to ensure it happens before any observers notified.
    if (state == State.COMPLETED) {
      if(_linearAdQueue.size() > 0) _linearAdQueue.remove(0);
      sendTrackingEvent(TrackingEvent.COMPLETE);
      if (!_linearAdQueue.isEmpty()) {
        addQuartileBoundaryObserver();
        super.init(_parent, _linearAdQueue.get(0).getStreams());
        return;
      }
    }
    super.setState(state);
  }

  private Linear currentLinearAd() {
    return _linearAdQueue.isEmpty() ? null : _linearAdQueue.get(0);
  }

  private void addQuartileBoundaryObserver() {
    _startSent = false;
    _firstQSent = false;
    _midSent = false;
    _thirdQSent = false;
    _iconViewTracker = new ArrayList<Boolean>();
    for (int i = 0; i < currentLinearAd().getIcons().size(); ++i) {
      _iconViewTracker.add(false);
    }
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    final String name = OoyalaNotification.getNameOrUnknown(arg1);
    if (name == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      if (!_startSent && currentTime() > 0) {
        sendTrackingEvent(TrackingEvent.CREATIVE_VIEW);
        sendTrackingEvent(TrackingEvent.START);
        _startSent = true;
        Linear linearAd = currentLinearAd();
        String title = _ad.getAds().get(_adIndex).getTitle();
        String description = _ad.getAds().get(_adIndex).getDescription();
        String url = currentLinearAd().getClickThroughURL();
        int adsCount = _ad.getAds().size();
        int unplayedCount = adsCount - _adIndex - 1;
        double skipoffset = currentLinearAd().getSkippable() ? currentLinearAd().getSkipOffset() : -1.0;
        List<AdIconInfo> icons = null;
        if (currentLinearAd().getIcons().size() > 0) {
          icons = new ArrayList<AdIconInfo>();
          for (int i = 0; i < currentLinearAd().getIcons().size(); ++i) {
            Icon icon = currentLinearAd().getIcons().get(i);
            if (icon.getResource().getType() != Resource.Type.Static) {
              DebugMode.logD(TAG, "unsupported icon resource type:" + icon.getResource().getType().toString() + " uri:" + icon.getResource().getUri());
              continue;
            }
            AdIconInfo iconInfo =
              new AdIconInfo(i, icon.getWidth(), icon.getHeight(), icon.getXPosition(), icon.getYPosition(), icon.getOffset(), icon.getDuration(), icon.getResource().getUri());
            icons.add(iconInfo);
          }
        }
        _notifier.notifyAdStartWithAdInfo(new AdPodInfo(title,description,url,adsCount,unplayedCount, skipoffset, true,true, icons));
        sendImpressionTrackingEvent();
      } else if (!_firstQSent && currentTime() > (currentLinearAd().getDuration() * 1000 / 4)) {
        sendTrackingEvent(TrackingEvent.FIRST_QUARTILE);
        _firstQSent = true;
      } else if (!_midSent && currentTime() > (currentLinearAd().getDuration() * 1000 / 2)) {
        sendTrackingEvent(TrackingEvent.MIDPOINT);
        _midSent = true;
      } else if (!_thirdQSent && currentTime() > (3 * currentLinearAd().getDuration() * 1000 / 4)) {
        sendTrackingEvent(TrackingEvent.THIRD_QUARTILE);
        _thirdQSent = true;
      }

      for (int i = 0; i < currentLinearAd().getIcons().size(); ++i) {
        if (!_iconViewTracker.get(i) && currentTime() * 1000 > currentLinearAd().getIcons().get(i).getOffset()) {
          // send view pings
          _iconViewTracker.set(i, true);
          for (String viewTracker : currentLinearAd().getIcons().get(i).getViewTrackings()) {
            final URL url = VASTUtils.urlFromAdUrlString(viewTracker);
            Utils.pingUrl(url);
          }
        }
      }
    }
    else if (name == OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME) {
      OoyalaPlayer.State state;
      try {
        state = ((StreamPlayer) arg0).getState();
      } catch (Exception e) {
        DebugMode.logE(TAG, "arg0 should be a StreamPlayer but is not!" + arg0.toString());
        return;
      }
        // If player is completed, send completed tracking event
      if (state == State.COMPLETED) {
        sendTrackingEvent(TrackingEvent.COMPLETE);
        // more ads to play, DO NOT update state. otherwise ad plugin will exit ad mode.
        if (proceedToNextAd()) {
          // If we don't send the COMPLETE event, we will need to send AdCompleted events for intermediary ads
          // TODO: AdCompletes should be consistent - and should be fired from the ad player, not from OoyalaManagedAdsPlugin
          getNotifier().notifyAdCompleted();
          return;
        }
      }
    }
    super.update(arg0,  arg1);
  }

  /*
   * proceed linear ads complete, move to next one if any
   * returns true if more ads to play, false otherwise
   */
  private boolean proceedToNextAd() {
    _adIndex++;

    //If there are more ads to play, play them
    if(_linearAdQueue.size() > 0) {
      _linearAdQueue.remove(0);
    }

    if (_linearAdQueue.isEmpty()) {
      return false;
    }

    super.destroy();
    addQuartileBoundaryObserver();
    super.init(_parent, _linearAdQueue.get(0).getStreams());
    super.play();

    //If the next linear ad has a clickThrough URL, create the Learn More button only if it doesn't exist
    if (currentLinearAd() != null && currentLinearAd().getClickThroughURL() != null && _parent.getOptions().getShowNativeLearnMoreButton()) {
      if (_learnMore == null) {
          _learnMore = new AdsLearnMoreButton(_playerLayout.getContext(), this, _topMargin);
          _playerLayout.addView(_learnMore);
      } else {
          _playerLayout.bringChildToFront(_learnMore);
      }
    }
    //If there is no clickThrough and Learn More button exists from previous ad, remove it
    else if (_learnMore != null) {
      _playerLayout.removeView(_learnMore);
      _learnMore = null;
    }

    return true;
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

    //If Learn More button exists, add it to the new playerLayout with the topMargin
    if (_learnMore != null) {
      //Remove the Learn More button from the old playerLayout and set the new playerLayout and topMargin
      _playerLayout.removeView(_learnMore);
      _playerLayout = layout;
      _topMargin = topMargin;

      //Set the new topMargin and add the Learn More button to new playerLayout
      _learnMore.setTopMargin(_topMargin);
      _playerLayout.addView(_learnMore);
    }
    //Else, keep track of the new player layout and topMargin for next linear ad
    else {
      _playerLayout = layout;
      _topMargin = topMargin;
    }
  }

  /**
   * Called by the Learn More button's onClick event.
   * Sends the click tracking pings and opens the browser.
   */
  @Override
  public void processClickThrough() {
    if (currentLinearAd() != null && currentLinearAd().getClickThroughURL() != null) {
      //Open browser to click through URL
      processClick(currentLinearAd().getClickThroughURL(), currentLinearAd().getClickTrackingURLs());
    }
  }

  @Override
  public void onAdIconClicked(int index) {
    List<Icon> icons = currentLinearAd().getIcons();
    if (icons == null || index >= icons.size()) {
      DebugMode.logE(TAG, "cannot find icon, index is " + index);
      return;
    }
    Icon icon = icons.get(index);
    if (icon.getClickThrough() != null) {
      processClick(icon.getClickThrough(), icon.getClickTrackings());
    }
  }

  private void processClick(String clickUrl, Set<String> trackingUrls) {
    suspend();
    if (trackingUrls != null) {
      //send trackings
      for (String s : trackingUrls) {
        final URL url = VASTUtils.urlFromAdUrlString(s);
        Utils.pingUrl(url);
      }
    }
    Utils.openUrlInBrowser(_playerLayout.getContext(), clickUrl);
  }

  public void sendTrackingEvent(String event) {
    if (currentLinearAd() == null || currentLinearAd().getTrackingEvents() == null) { return; }
    Set<String> urls = currentLinearAd().getTrackingEvents().get(event);
    if (urls != null) {
      for (String urlStr : urls) {
        final URL url = VASTUtils.urlFromAdUrlString(urlStr);
        DebugMode.logI(TAG, "Sending " + event + " Tracking Ping: " + url);
        Utils.pingUrl(url);
      }
    }
  }

  private void sendImpressionTrackingEvent() {
    if (_adIndex < 0 || _adIndex >= _ad.getAds().size()) {
      return;
    }
    List<String> urls = _ad.getAds().get(_adIndex).getImpressionURLs();
    if(urls != null){
      for(String urlStr: urls) {
        final URL url = VASTUtils.urlFromAdUrlString(urlStr);
        DebugMode.logI(TAG, "Sending Impression Tracking Ping: " + url);
        Utils.pingUrl(url);
      }
    }
  }

  private Linear linearAdsForAdIndex(int adIndex){
    Ad ad = _ad.getAds().get(adIndex);

    return ad.getLinearCreative() == null ? null : ad.getLinearCreative().getLinear();
  }

  @Override
  public void destroy() {
    //Remove Learn More button if it exists
    if (_learnMore != null) {
      _playerLayout.removeView(_learnMore);
      _learnMore.destroy();
      _learnMore = null;
    }

    deleteObserver(this);
    super.destroy();
  }

  @Override
  public void skipAd() {
    getNotifier().notifyAdSkipped();
    if (!proceedToNextAd()) {
      setState(State.COMPLETED);
    }
  }

  /**
   * report errors generated during info fecthing
   */
  private void reportAdErrors() {
    reportErrors(_ad.getErrorUrls(), _ad.getErrors());
    for (Ad ad : _ad.getAds()) {
      reportErrors(ad.getErrorURLs(), ad.getErrors());
    }
  }

  private void reportLinearErrors() {
    // TODO: report linear errors during playback
  }

  /**
   * Send error reports to VAST server
   * @param errorUrls the error urls to send errors
   * @param errors the error codes
   */
  private void reportErrors(List<String> errorUrls, Set<Integer> errors) {
    if (errorUrls == null || errors == null || errors.size() <= 0) {
      // no errors to report
      return;
    }

    for (String errorUrl : errorUrls) {
      URL url = VASTUtils.formatErrorUrl(errorUrl, errors);
      if (url != null) {
        Utils.pingUrl(url);
      }
    }
  }
}