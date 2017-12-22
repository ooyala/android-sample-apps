package com.ooyala.android.player;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.view.SurfaceHolder;

import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaException.OoyalaErrorCode;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayer.SeekStyle;
import com.ooyala.android.OoyalaPlayer.State;
import com.ooyala.android.SeekInfo;
import com.ooyala.android.item.Stream;
import com.ooyala.android.util.DebugMode;

import java.util.Set;

/**
 * A wrapper around android.media.MediaPlayer
 * http://developer.android.com/reference/android/media/MediaPlayer.html
 *
 * For a list of Android supported media formats, see:
 * http://developer.android.com/guide/appendix/media-formats.html
 */
public class BaseStreamPlayer extends StreamPlayer implements OnBufferingUpdateListener, OnCompletionListener, OnErrorListener,
    OnPreparedListener, OnVideoSizeChangedListener, OnInfoListener, OnSeekCompleteListener,
    SurfaceHolder.Callback {

  private static final String TAG = BaseStreamPlayer.class.getName();
  protected MediaPlayer _player = null;
  protected SurfaceHolder _holder = null;
  protected String _streamUrl = "";
  protected int _width = 0;
  protected int _height = 0;
  private boolean _playQueued = false;
  private boolean _completedQueued = false;

  private boolean _playerPrepared = false;
  private int _timeBeforeSuspend = -1;
  private State _stateBeforeSuspend = State.INIT;
  Stream stream = null;

  @Override
  public void init(OoyalaPlayer parent, Set<Stream> streams) {
    setParent(parent);
    WifiManager wifiManager = (WifiManager)parent.getLayout().getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    boolean isWifiEnabled = wifiManager.isWifiEnabled();
    stream =  Stream.bestStream(streams, isWifiEnabled);
    if (stream == null) {
      DebugMode.logE(TAG, "ERROR: Invalid Stream (no valid stream available)");
      this._error = new OoyalaException(OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "Invalid Stream");
      setState(State.ERROR);
      return;
    }

    if (parent == null) {
      this._error = new OoyalaException(OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "Invalid Parent");
      setState(State.ERROR);
      return;
    }
    setState(State.LOADING);
    _streamUrl = stream.getUrlFormat().equals(Stream.STREAM_URL_FORMAT_B64) ? stream.decodedURL().toString().trim() : stream.getUrl().trim();
    setupView();
    if (_player != null) { _player.reset(); }
  }

  @Override
  public void pause() {
    _playQueued = false;
    switch (getState()) {
      case PLAYING:
        stopPlayheadTimer();
        _player.pause();
        setState(State.PAUSED);
      default:
        break;
    }
  }

  @Override
  public void play() {
    _playQueued = false;
    switch (getState()) {
      case INIT:
      case LOADING:
        queuePlay();
        break;
      case PAUSED:
      case READY:
      case COMPLETED:
        DebugMode.logD(TAG, "BaseStreamPlayer.play() has been called when _playerPrepared = " + _playerPrepared);
        if (_playerPrepared) { 
          _player.start();
          _view.setBackgroundColor(Color.TRANSPARENT);
          setState(State.PLAYING);
          startPlayheadTimer();
        } else {
          queuePlay();
        }
      default:
        break;
    }
  }

  @Override
  public void stop() {
    stopPlayheadTimer();
    _playQueued = false;
    if (_playerPrepared) {
      _player.stop();
    }
    _player.release();
    _player = null;
    _playerPrepared = false;
  }

  @Override
  public void reset() {
    suspend(0, State.PAUSED);
    setState(State.LOADING);
    setupView();
    resume();
  }

  @Override
  public int currentTime() {
    if (getState() == State.SUSPENDED) {
      return _timeBeforeSuspend;
    } else if (_player == null) {
      return 0;
    }
    switch (getState()) {
      case INIT:
      case SUSPENDED:
        return 0;
      default:
        break;
    }
    try {
      return _player.getCurrentPosition();
    } catch (IllegalStateException e) {
      DebugMode.logE(TAG, "getCurrentPsition called when state is invalid ", e);
      return 0;
    }
  }

  @Override
  public int duration() {
    if (_player == null) { return 0; }
    if (!_playerPrepared) {
      DebugMode.logE(TAG, "Trying to getDuration without MediaPlayer");
      return 0;
    }
    switch (getState()) {
    case INIT:
    case SUSPENDED:
      return 0;
    default:
      break;
    }
    return _player.getDuration();
  }

  @Override
  public int buffer() {
    return this._buffer;
  }

  @Override
  public void seekToTime(int timeInMillis) {
    if (_player != null && isSeekAllowed()) {
      _timeBeforeSuspend = timeInMillis;
      int seekStartTime = _player.getCurrentPosition();
      _player.seekTo(timeInMillis);
      setChanged();
      notifyObservers(new OoyalaNotification(OoyalaPlayer.SEEK_STARTED_NOTIFICATION_NAME,new SeekInfo(seekStartTime,timeInMillis,_player.getDuration())));
    }
  }

  private void seekToTimeOnPrepared( int timeInMillis ) {
    if (_player != null) {
      int seekStartTime = _player.getCurrentPosition();
      _player.seekTo(timeInMillis);
      setChanged();
      notifyObservers(new OoyalaNotification(OoyalaPlayer.SEEK_STARTED_NOTIFICATION_NAME,new SeekInfo(seekStartTime,timeInMillis,_player.getDuration())));
    }
  }

  private boolean isSeekAllowed() {
    return getState() == State.PAUSED || getState() == State.READY
        || getState() == State.COMPLETED || getState() == State.PLAYING;
  }

  protected void createMediaPlayer() {
    try {
      if (_player != null) {
        DebugMode
            .logD(TAG, "createMediaPlayer: reset the existing mediaplayer");
        _playerPrepared = false;
        _player.reset();
      } else {
        DebugMode
                .logD(TAG, "createMediaPlayer: creating a new mediaplayer");
        _player = new MediaPlayer();
      }
      // Set cookies if they exist for 4.0+ Secure HLS Support
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        _player.setDataSource(_parent.getLayout().getContext(), Uri.parse(_streamUrl));
      } else {
        _player.setDataSource(_streamUrl);
      }
      _player.setDisplay(_holder);
      _player.setAudioStreamType(AudioManager.STREAM_MUSIC);
      _player.setScreenOnWhilePlaying(true);
      _player.setOnPreparedListener(this);
      _player.setOnCompletionListener(this);
      _player.setOnBufferingUpdateListener(this);
      _player.setOnErrorListener(this);
      _player.setOnInfoListener(this);
      _player.setOnSeekCompleteListener(this);
      _player.setOnVideoSizeChangedListener(this);
      _player.prepareAsync();

      setVolume(_parent.getVolume()); // Set the initial volume to its intended level as early as possible
    } catch (Throwable t) {
    }
  }

  @Override
  public boolean onError(MediaPlayer mp, int what, int extra) {
    this._error = new OoyalaException(OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "MediaPlayer Error: " + what + " " + extra);
    if (what == -10 && extra == -10) {  //I think this means unsupported format
      DebugMode.logE(TAG, "Unsupported video type given to base media player");
    }
    if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
      stop();
    }

    setState(State.ERROR);
    return false;
  }

  @Override
  public void onPrepared(MediaPlayer mp) {
    DebugMode.logD(TAG, "MediaPlayer is prepared.");
    if (_width == 0 && _height == 0) {
      if (mp.getVideoHeight() > 0 && mp.getVideoWidth() > 0) {
        setVideoSize(mp.getVideoWidth(), mp.getVideoHeight());
      }
    }
    if (_timeBeforeSuspend > 0) {
      seekToTimeOnPrepared(_timeBeforeSuspend);
    }
    _playerPrepared = true;
    setState(State.READY);
  }

  @Override
  public void onBufferingUpdate(MediaPlayer mp, int percent) {
    this._buffer = percent;
    setChanged();
    notifyObservers(new OoyalaNotification(OoyalaPlayer.BUFFER_CHANGED_NOTIFICATION_NAME));
  }

  @Override
  public boolean onInfo(MediaPlayer mp, int what, int extra) {

    //Set the visibility of the View above the surface to transparent, in order to show the video
    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
      _view.setBackgroundColor(Color.TRANSPARENT);
    }
    //These refer to when mid-playback buffering happens.  This doesn't apply to initial buffer
    else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
      DebugMode.logD(TAG, "onInfo: Buffering Starting! " + what + ", extra: " + extra);
      setState(State.LOADING);
    } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
      DebugMode.logD(TAG, "onInfo: Buffering Done! " + what + ", extra: " + extra);
      if (_player.isPlaying()) {
        setState(State.PLAYING);
      }
      else {
        setState(State.PAUSED);
      }
    }
    return true;
  }

  @Override
  public void onCompletion(MediaPlayer mp) {
    currentItemCompleted();
  }

  @Override
  public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
    setVideoSize(width, height);
  }

  @Override
  public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
  }

  @Override
  public void surfaceCreated(SurfaceHolder arg0) {
    DebugMode.logI(TAG, "Surface Created");

    createMediaPlayer();
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder arg0) {
    DebugMode.logI(TAG, "Surface Destroyed");
  }

  @Override
  public void setParent(OoyalaPlayer parent) {
    super.setParent(parent);
  }

  @SuppressWarnings("deprecation")
  private void setupView() {
    createView(_parent.getOptions().getPreventVideoViewSharing(), _parent.getLayout().getContext());
    _parent.addVideoView( _view );

    // Try to figure out the video size.  If not, use our default
    if (stream.getWidth() > 0 && stream.getHeight() > 0) {
      setVideoSize(stream.getWidth(), stream.getHeight());
    } else {
      setVideoSize(16,9);
    }

    _holder = _view.getHolder();
    _holder.addCallback(this);
    _holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  private void createView(boolean preventVideoViewSharing, Context c) {
    _view = new MovieView(preventVideoViewSharing, c);
    _view.setBackgroundColor(Color.BLACK);
  }

  private void removeView() {
    _parent.removeVideoView();
    if (_holder != null) {
      _holder.removeCallback(this);
    }
    _view = null;
    _holder = null;
  }

  @Override
  public void onSeekComplete(MediaPlayer arg0) {

    // For m3u8s on 3+ phones, seeking before start() doesn't work.  If we're told seek is done
    // but seek isn't actaully done, try it again

    setChanged();
    notifyObservers(new OoyalaNotification(OoyalaPlayer.SEEK_COMPLETED_NOTIFICATION_NAME,new SeekInfo(0,_player.getCurrentPosition(),_player.getDuration())));
    // If we're resuming, and we're not near the desired seek position, try again
    if(_timeBeforeSuspend >= 0 && Math.abs(_player.getCurrentPosition() - _timeBeforeSuspend) > 3000) {
      DebugMode.logI(this.getClass().getName(), "Seek failed. currentPos: " + _player.getCurrentPosition() +
          ", timeBefore" + _timeBeforeSuspend + "duration: " + _player.getDuration());

      // This looks pretty nasty, but it's a very specific case of HLS videos during the race condition of
      // seek right when play starts
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        DebugMode.logE(TAG, "Caught!", e);
      }
      _player.seekTo(_timeBeforeSuspend);
    }

    // Seeking SHOULD work if our duration actually exists.  This is just in case, so we don't infinite loop
    if (_player.getDuration() != 0) {
      _timeBeforeSuspend = -1;
    }
    dequeuePlay();
  }

  @Override
  public void suspend() {
    int millisToResume = -1;
    if ((_player != null) && (_playerPrepared)) {
      millisToResume = _player.getCurrentPosition();
    }
    suspend(millisToResume, getState());
  }

  private void suspend(int millisToResume, State stateToResume) {
    DebugMode.logD(TAG, "suspend with time " + millisToResume + "state" + stateToResume.toString());
    if (getState() == State.SUSPENDED) {
      DebugMode.logD(TAG, "Suspending an already suspended player");
      return;
    }
    if (_player == null) {
      DebugMode.logD(TAG, "Suspending with a null player");
      return;
    }

    if (millisToResume >= 0) {
      _timeBeforeSuspend = millisToResume;
      _stateBeforeSuspend = stateToResume;
    }

    if (_player != null) {
      stop();
    }
    removeView();
    _width = 0;
    _height = 0;
    _buffer = 0;
    setState(State.SUSPENDED);
  }

  @Override
  public void resume() {
    resume(_timeBeforeSuspend, _stateBeforeSuspend);
  }

  @Override
  public void resume(int millisToResume, State stateToResume) {
    DebugMode.logD(TAG, "Resuming. time to resume: " + millisToResume + ", state to resume: " + stateToResume);
    _timeBeforeSuspend = millisToResume;
    if (stateToResume == State.PLAYING) {
      queuePlay();
    } else if (stateToResume == State.COMPLETED) {
      queueCompleted();
    }
  }

  @Override
  public void destroy() {
    if (_player != null) {
      stop();
    }
    removeView();
    _parent = null;
    _width = 0;
    _height = 0;
    _buffer = 0;
    _playQueued = false;
    _timeBeforeSuspend = -1;
    setState(State.INIT);
  }

  private void setVideoSize(int width, int height) {
    _width = width;
    _height = height;
    ((MovieView) _view).setAspectRatio(((float) _width) / ((float) _height));
  }

  protected void currentItemCompleted() {
    stopPlayheadTimer();
    setState(State.COMPLETED);
  }

  private void queueCompleted() {
    _completedQueued = true;
  }

  private boolean dequeueCompleted() {
    if (_completedQueued) {
      _playQueued = false;
      _completedQueued = false;
      return true;
    }
    return false;
  }

  // Must queue play and wait for ready
  private void queuePlay() {
    _playQueued = true;
  }

  private void dequeuePlay() {
    if (_playQueued) {
      switch (getState()) {
        case PAUSED:
        case READY:
        case COMPLETED:
          _playQueued = false;
          play();
        default:
          break;
      }
    }
  }

  @Override
  public boolean isPlaying() {
    return _player != null && _player.isPlaying();
  }

  @Override
  protected void setState(State state) {
    if (dequeueCompleted()) {
      super.setState(State.COMPLETED);
    } else {
      super.setState(state);
      dequeuePlay();
    }
  }

  @Override
  public SeekStyle getSeekStyle() {
    // *** Disable ENHANCED seek due to some media player bug.

    // if(stream == null ||
    // Stream.DELIVERY_TYPE_HLS.equals(stream.getDeliveryType()) ||
    // stream.decodedURL().toString().contains("m3u8")) {
      return SeekStyle.BASIC;
    // }
    // else {
    // return SeekStyle.ENHANCED;
    // }
  }

  /**
   * Called once when player is created, and whenever setVolume() is called from OoyalaPlayer
   * @param volume
   */
  public void setVolume(float volume) {
    if (_player != null) {
      _player.setVolume(volume,volume);
      DebugMode.logV(TAG, "Volume set: " + volume);
    }
  }
}
