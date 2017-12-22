package com.ooyala.android;

import java.util.Set;

/**
 * PlayerInfo is a collection of information that describes the capabilities of the playback device.
 * These are used e.g. when negotiating with servers so they can choose the most appropriate
 * asset encodings.
 */
public interface PlayerInfo {
  /**
   * The string that represents the device for playback authentication
   * @return a denotation of the device.
   */
  public String getDevice();
  /**
   * A set of formats supported by the device.  See Stream class' Delivery Types
   * @return a set of supported formats.
   */
  public Set<String> getSupportedFormats();
  /**
   * A set of profiles supported by the device.
   * @return a set of supported profiles.
   */
  public Set<String> getSupportedProfiles();
  /**
   * Returns the maximum video width supported by the device
   * @return the maximum allowed width
   */
  public int getMaxWidth();
  /**
   * Returns the maximum video height supported by the device
   * @return the maximum allowed height
   */
  public int getMaxHeight();
  /**
   * Returns the maximum bitrate supported by the device
   * @return the maximum allowed bitrate
   */
  public int getMaxBitrate();
  public String getUserAgent();
}
