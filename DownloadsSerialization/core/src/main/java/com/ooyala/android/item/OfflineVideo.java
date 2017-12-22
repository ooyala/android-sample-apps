package com.ooyala.android.item;

import android.net.Uri;
import com.ooyala.android.util.DebugMode;
import java.io.File;

/**
 * A class that holds an offline video
 */
public class OfflineVideo extends UnbundledVideo {
  private static final String TAG = OfflineVideo.class.getSimpleName();

  private final File folder;

  /**
   * constructor, users should not directly call this, call the static method getVideo instead
   * @param stream the stream object
   * @param folder the folder
   */
  private OfflineVideo(Stream stream, File folder) {
    super(stream);
    this.folder = folder;
  }

  /**
   * @return the folder that holds offline video
   */
  public File getFolder() {
    return folder;
  }

  /**
   * create a DASH video object for offline playback
   * @param folder the folder that holds offline video
   * @return an OfflineVideo object on success, null otherwise
   */
  public static OfflineVideo getVideo(File folder) {
    if (folder == null || !folder.exists() || !folder.isDirectory()) {
      DebugMode.logE(TAG, "folder does not exist");
      return null;
    }

    String mpdFileString = null;
    for (String f : folder.list()) {
      if (f.indexOf(".mpd") > 0) {
        mpdFileString = f;
        break;
      }
    }

    if (mpdFileString == null) {
      DebugMode.logE(TAG, "manifest does not exist under " + folder.getAbsolutePath());
      return null;
    }

    File mpdFile = new File(folder, mpdFileString);
    Stream s = new Stream();
    s.setUrlFormat(Stream.STREAM_URL_FORMAT_TEXT);
    s.setUrl(Uri.fromFile(mpdFile).toString());
    s.setDeliveryType(Stream.DELIVERY_TYPE_DASH);
    return new OfflineVideo(s, folder);
  }
}
