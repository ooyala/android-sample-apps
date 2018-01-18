package com.ooyala.android.player.exoplayer;

import com.ooyala.android.OoyalaException;
import com.ooyala.android.item.Stream;
import com.ooyala.android.player.ExoMoviePlayer;
import com.ooyala.android.player.MoviePlayer;
import com.ooyala.android.player.PlayerFactory;
import com.ooyala.android.player.StreamPlayer;

import java.util.HashSet;
import java.util.Set;

public class ExoPlayerFactory implements PlayerFactory {
  private int priority;
  private Set<String> supportedFormats;


  public ExoPlayerFactory(int priority) {
    this.priority = priority;
    this.supportedFormats = new HashSet<String>();
    supportedFormats.add(Stream.DELIVERY_TYPE_DASH);
    supportedFormats.add(Stream.DELIVERY_TYPE_HLS);
    supportedFormats.add(Stream.DELIVERY_TYPE_AKAMAI_HD2_VOD_HLS);
    supportedFormats.add(Stream.DELIVERY_TYPE_AKAMAI_HD2_HLS);
    supportedFormats.add(Stream.DELIVERY_TYPE_M3U8);
    supportedFormats.add(Stream.DELIVERY_TYPE_MP4);
  }

  @Override
  public MoviePlayer createPlayer() throws OoyalaException {
    return new ExoMoviePlayer();
  }

  @Override
  public StreamPlayer createStreamPlayer() {
    return new ExoStreamPlayer();
  }

  @Override
  public int priority() {
    return priority;
  }

  @Override
  public Set<String> getSupportedFormats() {
    return supportedFormats;
  }
}
