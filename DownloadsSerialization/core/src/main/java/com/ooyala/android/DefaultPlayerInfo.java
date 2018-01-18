package com.ooyala.android;

import java.util.Set;

public class DefaultPlayerInfo implements PlayerInfo {
  private final Set<String> supportedFormats;
  private final Set<String> supportedProfiles;

  public DefaultPlayerInfo(Set<String> supportedFormats, final Set<String> supportedProfiles) {
    this.supportedFormats = supportedFormats;
    this.supportedProfiles = supportedProfiles;
  }
    @Override
    public Set<String> getSupportedProfiles() {
      return supportedProfiles;
    }

    @Override
    public Set<String> getSupportedFormats() {
      return supportedFormats;
    }

    @Override
    public int getMaxWidth() { return -1; }

    @Override
    public int getMaxHeight() { return -1; }

    @Override
    public int getMaxBitrate() { return -1; }

    @Override
    public String getDevice() { return "android_html"; }

    @Override
    public String getUserAgent() { return null; }
}
