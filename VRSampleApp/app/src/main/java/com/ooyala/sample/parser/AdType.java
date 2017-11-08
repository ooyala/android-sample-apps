package com.ooyala.sample.parser;

import com.google.gson.annotations.SerializedName;

public enum AdType {
  @SerializedName("NO-ADS")
  NOADS,

  @SerializedName("OOYALA")
  OOYALA,

  @SerializedName("IMA")
  IMA,

  @SerializedName("VAST")
  VAST
}
