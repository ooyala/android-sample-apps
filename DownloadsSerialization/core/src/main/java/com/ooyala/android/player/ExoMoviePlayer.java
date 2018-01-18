package com.ooyala.android.player;

import com.ooyala.android.player.exoplayer.ExoStreamPlayer;

/**
 * Created by zchen on 1/29/16.
 */
public class ExoMoviePlayer extends MoviePlayer {

  @Override
  protected StreamPlayer createStreamPlayer() {
    return new ExoStreamPlayer();
  }
}