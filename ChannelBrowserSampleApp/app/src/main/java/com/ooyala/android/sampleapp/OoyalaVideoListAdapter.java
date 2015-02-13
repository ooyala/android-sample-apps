package com.ooyala.android.sampleapp;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class OoyalaVideoListAdapter extends SimpleAdapter {
  private ImageDownloader imageDownloader = new ImageDownloader();

  public OoyalaVideoListAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
      String[] from, int[] to) {
    super(context, data, resource, from, to);
  }

  @Override
  public void setViewImage(ImageView v, String value) {
    imageDownloader.download(value, v);
  }
}
