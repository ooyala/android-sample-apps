package com.ooyala.sample.screen

import com.ooyala.android.imasdk.OoyalaIMAManager
import com.ooyala.sample.VideoFragment
import com.ooyala.sample.utils.VideoData
import kotlinx.android.synthetic.main.video_fragment.*

class IMAVideoFragment(videoData: VideoData) : VideoFragment(videoData) {


  override fun initAdManager() {
    val imaManager = OoyalaIMAManager(player, playerSkinLayout)
  }
}