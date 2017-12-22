package com.ooyala.android.captions;

import com.ooyala.android.item.Caption;
import com.ooyala.android.item.ClosedCaptions;
import com.ooyala.android.item.Video;

public class CaptionUtils {

  /**
   * Get the closed caption, if any, for a given playhead position from the currently playing asset.
   * @param currentItem possibly null content from which to read Closed Captions.
   * @param ccLanguage possibly null currently select Closed Caption language.
   * @param playheadTimeMsec the playhead time in the currentItem from which to find a Caption.
   * @return possibly null Caption.
   */
  public static Caption getCaption(final Video currentItem, final String ccLanguage, final int playheadTimeMsec) {
    if( currentItem == null ) { return null; }
    if( ccLanguage == null ) { return null; }
    final ClosedCaptions captions = currentItem.getClosedCaptions();
    if( captions == null ) { return null; }
    return captions.getCaption(ccLanguage, playheadTimeMsec / 1000d);
  }

}

