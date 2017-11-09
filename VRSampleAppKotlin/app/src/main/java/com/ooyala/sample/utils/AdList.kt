package com.ooyala.sample.utils

import android.content.Context
import com.ooyala.sample.parser.VideoJSONParser

class AdList {

  private var adList: List<VideoData>? = null

  companion object {
    val instance : AdList = AdList()
  }

  fun getVideoList(context: Context): List<VideoData> {
    if (adList == null) {
      adList = VideoJSONParser().getVideoList(context.assets)
    }
    return adList!!
  }
}
