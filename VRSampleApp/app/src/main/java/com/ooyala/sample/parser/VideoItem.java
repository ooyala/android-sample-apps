package com.ooyala.sample.parser;


import com.google.gson.annotations.SerializedName;

public class VideoItem {

  @SerializedName("title")
  private String title;

  @SerializedName("embed-code")
  private String embedCode;

  @SerializedName("provider-code")
  private String pCode = "BzY2syOq6kIK6PTXN7mmrGVSJEFj";

  @SerializedName("ad-type")
  private AdType type;

  public String getTitle() {
    return title;
  }

  public String getEmbedCode() {
    return embedCode;
  }

  public String getpCode() {
    return pCode;
  }

  public AdType getType() {
    return type;
  }
}
