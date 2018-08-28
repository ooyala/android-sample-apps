package com.ooyala.sample.fragmentfactory;

import com.ooyala.sample.parser.AdType;
import com.ooyala.sample.screen.IMAVideoFragment;
import com.ooyala.sample.screen.PlaybackSpeedVideoFragment;
import com.ooyala.sample.screen.VideoFragment;
import com.ooyala.sample.utils.VideoFeatureType;

public class FragmentFactory {

  public static VideoFragment getFragmentByType(VideoFeatureType featureType, AdType adType) {
    switch (featureType) {
      case REGULAR:
        return getFragmentByAdType(adType);
      case PLAYBACK_RATE:
        return new PlaybackSpeedVideoFragment();
      default:
        return null;
    }
  }

  private static VideoFragment getFragmentByAdType(AdType adType) {
    switch (adType) {
      case NOADS:
      case OOYALA:
      case VAST:
        return new VideoFragment();
      case IMA:
        return new IMAVideoFragment();
      default:
        return null;
    }
  }
}
