package com.ooyala.sample.parser

import com.google.gson.annotations.SerializedName

enum class AdType {

  @SerializedName("NO-ADS")
  NOADS,

  @SerializedName("OOYALA")
  OOYALA,

  @SerializedName("IMA")
  IMA,

  @SerializedName("VAST")
  VAST
}