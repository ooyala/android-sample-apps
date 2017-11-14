package com.ooyala.sample.fragmentfactory;

import com.ooyala.sample.parser.AdType;
import com.ooyala.sample.screen.IMAVideoFragment;
import com.ooyala.sample.screen.VideoFragment;

public class FragmentFactory {

  public static VideoFragment getFragmentByAdType(AdType type) {
    switch (type) {
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
