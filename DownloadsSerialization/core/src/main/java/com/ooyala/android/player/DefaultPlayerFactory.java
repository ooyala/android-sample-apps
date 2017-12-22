package com.ooyala.android.player;

import com.ooyala.android.OoyalaException;
import com.ooyala.android.item.Stream;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zchen on 3/22/16.
 */
public class DefaultPlayerFactory implements PlayerFactory {
  private Set<String> supportedFormats;


  public DefaultPlayerFactory() {
    this.supportedFormats = new HashSet<String>();
    supportedFormats.add(Stream.DELIVERY_TYPE_MP4);
    supportedFormats.add(Stream.DELIVERY_TYPE_M3U8);
  }

  @Override
  public MoviePlayer createPlayer() throws OoyalaException {
    return new MoviePlayer();
  }

  @Override
  public int priority() {
    return 1;
  }

  @Override
  public StreamPlayer createStreamPlayer() {
    return new BaseStreamPlayer();
  }

  @Override
  public Set<String> getSupportedFormats() {
    return supportedFormats;
  }
}
