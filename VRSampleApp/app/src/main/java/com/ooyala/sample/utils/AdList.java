package com.ooyala.sample.utils;


import android.content.Context;

import com.ooyala.sample.parser.VideoJSONParser;

import java.util.Arrays;
import java.util.List;

public class AdList {

  private static AdList instance;

  private List<VideoData> videoList = null;

  public static AdList getInstance() {
    if (instance == null) {
      instance = new AdList();
    }
    return instance;
  }

  public List<VideoData> getVideoList(Context context) {
    if (videoList == null) {
      videoList = new VideoJSONParser().getVideoData(context.getAssets());
    }
    return videoList;
  }

}
