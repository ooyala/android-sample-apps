package com.ooyala.sample.utils;

import com.ooyala.android.PlayerInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomPlayerInfo implements PlayerInfo {
    Set<String> supportedFormats;

    public CustomPlayerInfo() {
      supportedFormats = new HashSet<>();
      // add the normal supported formats for Android
      supportedFormats.add("dash");
      supportedFormats.add("akamai_hd2_vod_hls");
      supportedFormats.add("mp4");
      supportedFormats.add("m3u8");
      supportedFormats.add("hls");
      supportedFormats.add("akamai_hd2_hls");
    }
    
    public CustomPlayerInfo(String format) {
        supportedFormats = new HashSet<>();
        supportedFormats.add(format);
    }

    @Override
    public Set<String> getSupportedFormats() {
      return supportedFormats;
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

  // we want to tell the server that we want to get elements as if we were using iOS
    // we would get different streams because of it
    @Override
    public String getDevice() {
      return "ios";
    }
  }
