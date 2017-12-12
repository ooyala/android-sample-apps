package com.ooyala.sample.parser;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SectionItem {

  @SerializedName("title")
  private String title;

  @SerializedName("videos")
  private List<VideoItem> videoItemList;

  public String getTitle() {
    return title;
  }

  public List<VideoItem> getVideoItemList() {
    return videoItemList;
  }
}
