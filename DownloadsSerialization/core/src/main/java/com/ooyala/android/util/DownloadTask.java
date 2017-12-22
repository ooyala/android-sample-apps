package com.ooyala.android.util;

import com.ooyala.android.offline.DashDownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

/**
 * A class that handles single file download
 */
public class DownloadTask implements Callable<Boolean> {
  private static final String TAG = DownloadTask.class.getSimpleName();

  private final String urlString;
  private final File destinationFile;
  private final DashDownloader.Listener listener;
  private final String embedCode;

  public DownloadTask(final String url, final File destinationFile, DashDownloader.Listener listener, String embedCode) {
    this.urlString = url;
    this.destinationFile = destinationFile;
    this.listener = listener;
    this.embedCode = embedCode;
  }

  public Boolean call() {
    Boolean success = false;
    InputStream is = null;
    FileOutputStream fos = null;

    try {
      URL url = new URL(urlString); //you can write here any link
      if (!destinationFile.exists()) {
        URLConnection ucon = url.openConnection();
        is = ucon.getInputStream();
        fos = new FileOutputStream(destinationFile);

        byte[] buffer = new byte[4096];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
          fos.write(buffer, 0, length);
        }
      }
      success = true;
    } catch (IOException e) {
      DebugMode.logE(TAG, "Failed to download " + destinationFile.getAbsolutePath() + " from " + urlString + "due to:" + e.getLocalizedMessage(), e);
      boolean deleted = destinationFile.delete();
      DebugMode.logE(TAG, "Attempting to delete " + destinationFile.getAbsolutePath() + ". Success? " + deleted, e);
      //listener.onError(embedCode, e);
    } finally {
      try {
        if (is != null) {
          is.close();
        }
        if (fos != null) {
          fos.flush();
          fos.close();
        }
      } catch (IOException ex) {
        DebugMode.logE(TAG, "download failed due to " + ex.getLocalizedMessage(), ex);
        //listener.onError(embedCode, ex);
      }
    }
    return success;
  }
}
