package com.ooyala.sample.fragmentfactory

import com.ooyala.sample.VideoFragment
import com.ooyala.sample.parser.AdType
import com.ooyala.sample.screen.IMAVideoFragment
import com.ooyala.sample.utils.VideoData


class FragmentFactory {

  fun getFragmentByType(adType: AdType): VideoFragment = when (adType) {
    AdType.IMA -> IMAVideoFragment()
    else -> VideoFragment()
  }
}
