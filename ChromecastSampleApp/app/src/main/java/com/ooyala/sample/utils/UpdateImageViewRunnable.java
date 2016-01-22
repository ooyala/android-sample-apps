package com.ooyala.sample.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Update the ImageView's image based on a url
 */
public class UpdateImageViewRunnable implements Runnable {
  private static String TAG = UpdateImageViewRunnable.class.getSimpleName();

  ImageView view;
  String url;

  public UpdateImageViewRunnable(ImageView view, String url) {
    this.view = view;
    this.url = url;
  }
  @Override
  public void run() {
    final Bitmap bitmap =  getImageBitmap(url);

    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
      public void run() {
        if (bitmap != null) {
          view.setImageBitmap(bitmap);
        }
      }
    });
  }

  private Bitmap getImageBitmap(String strUrl) {
    Bitmap bm = null;
    try {
      URL url = new URL(strUrl);
      URLConnection conn = url.openConnection();
      conn.connect();
      InputStream is = conn.getInputStream();
      BufferedInputStream bis = new BufferedInputStream(is);
      bm = BitmapFactory.decodeStream(bis);
      bis.close();
      is.close();
    } catch (IOException e) {
      Log.e(TAG, "Error getting bitmap", e);
    }
    return bm;
  }
}
