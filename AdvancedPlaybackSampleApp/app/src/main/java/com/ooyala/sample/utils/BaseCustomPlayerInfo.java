package com.ooyala.sample.utils;

import com.ooyala.android.PlayerInfo;

import java.util.Map;
import java.util.Set;

public abstract class BaseCustomPlayerInfo implements PlayerInfo {

  @Override
  public Set<String> getSupportedFormats() {
    return null;
  }

  @Override
  public Set<String> getSupportedProfiles() {
    return null;
  }

  @Override
  public int getMaxWidth() {
    return -1;
  }

  @Override
  public int getMaxHeight() {
    return -1;
  }

  @Override
  public int getMaxBitrate() {
    return -1;
  }

  @Override
  public String getUserAgent() {
    return null;
  }

  @Override
  public Map<String, String> getAdditionalParams() {
    return null;
  }

  @Override
  public String getDevice() {
    return "android_html";
  }
}
