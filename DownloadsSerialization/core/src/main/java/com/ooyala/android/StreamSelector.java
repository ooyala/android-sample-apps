package com.ooyala.android;

import com.ooyala.android.item.Stream;

import java.util.Set;

public interface StreamSelector {
  /**
   * The method used to select the correct Stream to play.
   * @param streams the array of streams to select from
   * @param isWifiEnabled true if wifi enabled, false otherwise
   * @return the Stream to play
   */
  public Stream bestStream(Set<Stream> streams, boolean isWifiEnabled);
}
