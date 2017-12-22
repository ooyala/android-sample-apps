package com.ooyala.android.player.exoplayer;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.ooyala.android.util.DebugMode;

class SeekCompleteObserver implements Player.EventListener {

  private static final String TAG = SeekCompleteObserver.class.getSimpleName();
  private SeekCompleteCallback seekCompleteCallback;
  private SimpleExoPlayer player;

  SeekCompleteObserver(SeekCompleteCallback seekCompleteCallback) {
    this.seekCompleteCallback = seekCompleteCallback;
  }

  void subscribe(SimpleExoPlayer player) {
    if (this.player == null) {
      this.player = player;
      player.addListener(this);
    }
  }

  void unsubscribe() {
    if (player != null) {
      player.removeListener(this);
      player = null;
    }
  }

  public interface SeekCompleteCallback {
    void onSeekCompleteCallback();
  }

  @Override
  public void onTimelineChanged(Timeline timeline, Object manifest) {

  }

  @Override
  public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

  }

  @Override
  public void onLoadingChanged(boolean isLoading) {

  }

  @Override
  public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
    DebugMode.logD(TAG, "SeekCompleteObserver.OnPlayerStateChanged, playWhenReady" + playWhenReady + "state" + playbackState);
    switch (playbackState) {
      case Player.STATE_BUFFERING:
        DebugMode.logD(TAG, "Buffering to seek point");
        break;
      case Player.STATE_READY:
        DebugMode.logD(TAG, "Seeking completed and ready to play");
        seekCompleteCallback.onSeekCompleteCallback();
        break;
      default:
        break;
    }
  }

  @Override
  public void onRepeatModeChanged(int repeatMode) {

  }

  @Override
  public void onPlayerError(ExoPlaybackException error) {

  }

  @Override
  public void onPositionDiscontinuity() {

  }

  @Override
  public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

  }
}
