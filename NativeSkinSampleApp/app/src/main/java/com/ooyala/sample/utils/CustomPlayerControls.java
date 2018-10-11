package com.ooyala.sample.utils;

import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ooyala.android.util.DebugMode;
import com.ooyala.android.LocalizationSupport;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer.SeekStyle;
import com.ooyala.android.OoyalaPlayer.State;
import com.ooyala.android.OoyalaPlayerLayout;

public class CustomPlayerControls extends AbstractDefaultOoyalaPlayerControls implements
SeekBar.OnSeekBarChangeListener, Button.OnClickListener, Observer {

  private static final String TAG = CustomPlayerControls.class.getSimpleName();

  private LinearLayout _bottomBar = null;
  private LinearLayout _seekWrapper = null;
  private LinearLayout _liveSliderModeWrapper = null;
  private LinearLayout _liveWrapper = null;
  private PlayPauseButton _playPause = null;
  private FullscreenButton _fullscreen = null;
  private ClosedCaptionsButton _closedCaptions = null;
  private CuePointsSeekBar _seek = null;
  private CuePointsSeekBar _seekLive = null;
  private TextView _currTime = null;
  private TextView _currTimeLive = null;
  private TextView _duration = null;
  private TextView _liveIndicator = null;
  private TextView _liveDVRIndicator = null;
  private ProgressBar _spinner = null;
  private boolean _wasPlaying;
  private boolean _seeking;
  private boolean _fullscreenButtonShowing = true;

  public CustomPlayerControls(OoyalaPlayer player, OoyalaPlayerLayout layout) {
    setParentLayout(layout);
    setOoyalaPlayer(player);
    setupControls();
  }

  @SuppressLint("NewApi")
  @Override
  protected void updateButtonStates() {
    if (_playPause != null) {
      _playPause.setPlaying(_player.isPlaying());
    }

    if (_fullscreen != null) {
      _fullscreen.setFullscreen(_player.isFullscreen());
      _fullscreen.setVisibility(_fullscreenButtonShowing ? View.VISIBLE : View.GONE);
    }
    
    if (_seekWrapper != null && _player.getCurrentItem() != null) {
      if(_player.getCurrentItem().isLive()) {
        _seekWrapper.setVisibility(View.GONE);
      } else {
        _seekWrapper.setVisibility(View.VISIBLE);
        _seekWrapper.setEnabled(!_player.isAdPlaying());
      }
    }

    if (_liveWrapper != null && _player.getCurrentItem() != null) {
      _liveWrapper.setVisibility((_player.getCurrentItem().isLive() && !_player.options().getShowLiveControls()) ? View.VISIBLE : View.GONE);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        _liveWrapper.setAlpha(_player.isShowingAd() ? 0.4f : 1f); // supported only 11+
      }
    }
    
    if (_liveSliderModeWrapper != null && _player.getCurrentItem() != null) {
      _liveSliderModeWrapper.setVisibility((_player.getCurrentItem().isLive() && _player.options().getShowLiveControls()) ? View.VISIBLE : View.GONE);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        _liveSliderModeWrapper.setAlpha(_player.isShowingAd() ? 0.4f : 1f); // supported only 11+
      }
    }

    // Show Closed Captions only if there is a language to select
    if (_closedCaptions != null && _player.getCurrentItem() != null && !_player.isShowingAd()) {
      _closedCaptions.setVisibility(_player.getAvailableClosedCaptionsLanguages().isEmpty() ?
          View.GONE : View.VISIBLE);
    }
    else {
      _closedCaptions.setVisibility(View.GONE);
    }
  }

  @Override
  protected void setupControls() {
    if (_layout == null) { return; }
    _baseLayout = new FrameLayout(_layout.getContext());
    _baseLayout.setBackgroundColor(Color.TRANSPARENT);

    _bottomBar = new LinearLayout(_baseLayout.getContext());
    _bottomBar.setOrientation(LinearLayout.HORIZONTAL);
    _bottomBar.setBackgroundColor(Color.BLUE);
    _bottomBar.setGravity(Gravity.CENTER_HORIZONTAL);
//    _bottomBar.setBackgroundDrawable(Images.gradientBackground(GradientDrawable.Orientation.BOTTOM_TOP));

    _playPause = new PlayPauseButton(_bottomBar.getContext());
    _playPause.setPlaying(_player.isPlaying());
    ViewGroup.LayoutParams ppLP = new ViewGroup.LayoutParams(Images.dpToPixels(_baseLayout.getContext(),
        PREFERRED_BUTTON_WIDTH_DP), Images.dpToPixels(_baseLayout.getContext(), PREFERRED_BUTTON_HEIGHT_DP));
    _playPause.setLayoutParams(ppLP);
    _playPause.setOnClickListener(this);


    _seekWrapper = new LinearLayout(_bottomBar.getContext());
    _seekWrapper.setOrientation(LinearLayout.HORIZONTAL);
    _currTime = new TextView(_seekWrapper.getContext());
    _currTime.setText("00:00:00");
    LinearLayout.LayoutParams currTimeLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    currTimeLP.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
    _currTime.setLayoutParams(currTimeLP);
    _seek = new CuePointsSeekBar(_seekWrapper.getContext());
    LinearLayout.LayoutParams seekLP = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
        1f);
    seekLP.gravity = Gravity.CENTER;
    seekLP.leftMargin = Images.dpToPixels(_baseLayout.getContext(), MARGIN_SIZE_DP);
    seekLP.rightMargin = Images.dpToPixels(_baseLayout.getContext(), MARGIN_SIZE_DP);
    _seek.setLayoutParams(seekLP);
    _seek.setOnSeekBarChangeListener(this);
    _duration = new TextView(_seekWrapper.getContext());
    _duration.setText("00:00:00");
    LinearLayout.LayoutParams durationLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    durationLP.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
    _duration.setLayoutParams(durationLP);
    _seekWrapper.addView(_currTime);
    _seekWrapper.addView(_seek);
    _seekWrapper.addView(_duration);
    LinearLayout.LayoutParams seekWrapperLP = new LinearLayout.LayoutParams(0,
        ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
    seekWrapperLP.gravity = Gravity.CENTER;
    seekWrapperLP.leftMargin = Images.dpToPixels(_baseLayout.getContext(), MARGIN_SIZE_DP);
    seekWrapperLP.rightMargin = Images.dpToPixels(_baseLayout.getContext(), MARGIN_SIZE_DP);
    _seekWrapper.setLayoutParams(seekWrapperLP);

    _liveWrapper = new LinearLayout(_bottomBar.getContext());
    _liveWrapper.setVisibility(View.GONE);
    _liveWrapper.setOrientation(LinearLayout.HORIZONTAL);
    _liveIndicator = new TextView(_liveWrapper.getContext());
    _liveIndicator.setText(LocalizationSupport.localizedStringFor("LIVE"));
    _liveIndicator.setGravity(Gravity.CENTER_HORIZONTAL);
    LinearLayout.LayoutParams liveIndicatorLP = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    liveIndicatorLP.gravity = Gravity.CENTER;
    _liveIndicator.setLayoutParams(liveIndicatorLP);
    _liveWrapper.addView(_liveIndicator);
    LinearLayout.LayoutParams liveWrapperLP = new LinearLayout.LayoutParams(0,
        ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
    liveWrapperLP.gravity = Gravity.CENTER;
    liveWrapperLP.leftMargin = Images.dpToPixels(_baseLayout.getContext(), MARGIN_SIZE_DP);
    liveWrapperLP.rightMargin = Images.dpToPixels(_baseLayout.getContext(), MARGIN_SIZE_DP);
    _liveWrapper.setLayoutParams(liveWrapperLP);
    
    _liveSliderModeWrapper = new LinearLayout(_bottomBar.getContext());
    _liveSliderModeWrapper.setOrientation(LinearLayout.HORIZONTAL);
    // CurrentTime TextView
    _currTimeLive = new TextView(_liveSliderModeWrapper.getContext());
    _currTimeLive.setText("00:00:00");
    LinearLayout.LayoutParams currTimeLiveLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    currTimeLiveLP.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
    _currTimeLive.setLayoutParams(currTimeLiveLP);
    // Scrubber
    _seekLive = new CuePointsSeekBar(_liveSliderModeWrapper.getContext());
    LinearLayout.LayoutParams seekLiveLP = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,
        1f);
    seekLiveLP.gravity = Gravity.CENTER;
    seekLiveLP.leftMargin = Images.dpToPixels(_baseLayout.getContext(), MARGIN_SIZE_DP);
    seekLiveLP.rightMargin = Images.dpToPixels(_baseLayout.getContext(), MARGIN_SIZE_DP);
    _seekLive.setLayoutParams(seekLiveLP);
    _seekLive.setOnSeekBarChangeListener(this);
    // Live Indicator
    _liveDVRIndicator = new TextView(_liveSliderModeWrapper.getContext());
    _liveDVRIndicator.setText(LocalizationSupport.localizedStringFor("LIVE"));
    LinearLayout.LayoutParams liveDVRIndicatorLP = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    liveDVRIndicatorLP.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
    _liveDVRIndicator.setLayoutParams(liveDVRIndicatorLP);
    _liveSliderModeWrapper.addView(_currTimeLive);
    _liveSliderModeWrapper.addView(_seekLive);
    _liveSliderModeWrapper.addView(_liveDVRIndicator);
    LinearLayout.LayoutParams seekWrapperLiveLP = new LinearLayout.LayoutParams(0,
        ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
    seekWrapperLiveLP.gravity = Gravity.CENTER;
    seekWrapperLiveLP.leftMargin = Images.dpToPixels(_baseLayout.getContext(), MARGIN_SIZE_DP);
    seekWrapperLiveLP.rightMargin = Images.dpToPixels(_baseLayout.getContext(), MARGIN_SIZE_DP);
    _liveSliderModeWrapper.setLayoutParams(seekWrapperLiveLP);

    _fullscreen = new FullscreenButton(_bottomBar.getContext());
    _fullscreen.setFullscreen(_player.isFullscreen());
    LinearLayout.LayoutParams fsLP = new LinearLayout.LayoutParams(Images.dpToPixels(
        _baseLayout.getContext(), PREFERRED_BUTTON_HEIGHT_DP), Images.dpToPixels(_baseLayout.getContext(),
            PREFERRED_BUTTON_HEIGHT_DP));
    fsLP.leftMargin = (PREFERRED_BUTTON_WIDTH_DP - PREFERRED_BUTTON_HEIGHT_DP) / 2;
    fsLP.rightMargin = (PREFERRED_BUTTON_WIDTH_DP - PREFERRED_BUTTON_HEIGHT_DP) / 2;
    _fullscreen.setLayoutParams(fsLP);
    _fullscreen.setOnClickListener(this);

    _closedCaptions = new ClosedCaptionsButton(_bottomBar.getContext());
    ViewGroup.LayoutParams ccLP = new ViewGroup.LayoutParams(Images.dpToPixels(_baseLayout.getContext(),
        PREFERRED_BUTTON_WIDTH_DP), Images.dpToPixels(_baseLayout.getContext(), PREFERRED_BUTTON_HEIGHT_DP));
    _closedCaptions.setLayoutParams(ccLP);
    _closedCaptions.setOnClickListener(this);

    _bottomBar.addView(_playPause);
//    _bottomBar.addView(_seekWrapper);
//    _bottomBar.addView(_liveWrapper);
//    _bottomBar.addView(_liveSliderModeWrapper);
    _bottomBar.addView(_closedCaptions);
//    _bottomBar.addView(_fullscreen);
    FrameLayout.LayoutParams bottomBarLP = new FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
        | Gravity.CENTER_HORIZONTAL);
    _baseLayout.addView(_bottomBar, bottomBarLP);

    _spinner = new ProgressBar(_layout.getContext());
    _spinner.setVisibility(View.INVISIBLE);
    FrameLayout.LayoutParams spinnerLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
        FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
    _layout.addView(_spinner, spinnerLP);

    FrameLayout.LayoutParams baseLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT);
    _layout.addView(_baseLayout, baseLP);
    hide();
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    if (_seeking) {
      //boolean includeHours = _player.getDuration() >= 1000 * 60 * 60;
      _currTime.setText(DateUtils.formatElapsedTime((int)((seekBar.getProgress() / (100f)) * _player.getDuration() / 1000)));
    }
    if (fromUser && _player.getSeekStyle() == SeekStyle.ENHANCED) {
      _player.seekToPercent(progress);
      update(null, null);
    }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    _seeking = true;
    _wasPlaying = _player.isPlaying();
    _player.pause();
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    DebugMode.logV(TAG, "onStopTrackingTouch(): _wasPlaying=" + _wasPlaying + ", " + "percent=" + seekBar.getProgress());
    _player.seekToPercent(seekBar.getProgress());
    update(null, null);
    _seeking = false;
    if (_wasPlaying) {
      _player.play();
    }
  }

  @Override
  public void onClick(View v) {
    if (v == _playPause) {
      if (_player.isPlaying()) {
        _player.pause();
      } else {
        _player.play();
      }
      show();
    } else if (v == _fullscreen && _isPlayerReady) {
      _player.setFullscreen(!_player.isFullscreen());
      updateButtonStates();
      hide();
    } else if (v == _closedCaptions) {
      _layout.getLayoutController().showClosedCaptionsMenu();
    }
  }

  @Override
  public void update(Observable arg0, Object argN) {
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (_seek != null && !_seeking) {
      _seek.setProgress(_player.getPlayheadPercentage());
      _seek.setSecondaryProgress(_player.getBufferPercentage());
      _seek.setCuePoints(_player.getCuePointsInPercentage());
    }
    
    if (_seekLive != null && !_seeking) {
      _seekLive.setProgress(_player.getPlayheadPercentage());
      _seekLive.setSecondaryProgress(_player.getBufferPercentage());
      _seekLive.setCuePoints(_player.getCuePointsInPercentage());
    }
    
    if (_duration != null && _currTime != null) {
      //boolean includeHours = _player.getDuration() >= 1000 * 60 * 60;
      _duration.setText(DateUtils.formatElapsedTime(_player.getDuration() / 1000));
      _currTime.setText(DateUtils.formatElapsedTime(_player.getPlayheadTime() / 1000));
    }
    
    if (_currTimeLive != null) {
      String currentTime = DateUtils.formatElapsedTime(Math.abs(_player.getPlayheadTime()) / 1000);
      if (_player.getPlayheadTime() < 0) {
        currentTime = "-" + currentTime;
      }
      _currTimeLive.setText(currentTime);
    }

    // update UI on adStarted/adCompleted
    if(arg1 == OoyalaPlayer.AD_STARTED_NOTIFICATION_NAME) {
      _isPlayerReady = true;
      if (_player.options().getShowAdsControls() == false) {
        hide();
      } else {
        updateButtonStates();
      }
    }

    if(arg1 == OoyalaPlayer.AD_COMPLETED_NOTIFICATION_NAME ||
        arg1 == OoyalaPlayer.AD_SKIPPED_NOTIFICATION_NAME ||
        arg1 == OoyalaPlayer.AD_ERROR_NOTIFICATION_NAME ) {
      _isPlayerReady = false;
      updateButtonStates();
    }

    // update spinner
    if (arg1 == OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME) {
      State currentState = _player.getState();

      updateButtonStates();

      if (currentState == State.LOADING && _isVisible) {
        _spinner.setVisibility(View.VISIBLE);
      } else {
        _spinner.setVisibility(View.INVISIBLE);
      }

      if (currentState == State.READY || currentState == State.PLAYING || currentState == State.PAUSED) {
        _isPlayerReady = true;
      }

      if (currentState == State.SUSPENDED || currentState == State.ERROR) {
        _isPlayerReady = false;
        hide();
      }
      if (!isShowing() && currentState != State.INIT && currentState != State.LOADING
          && currentState != State.ERROR && currentState != State.SUSPENDED && !_player.isFullscreen()) {
        show();
      }
    }
  }

  @Override
  public int bottomBarOffset() {
    return (PREFERRED_BUTTON_HEIGHT_DP * 2 + MARGIN_SIZE_DP * 4);
  }

  @Override
  public void setFullscreenButtonShowing(boolean showing) {
    _fullscreenButtonShowing = showing;
  }
}
