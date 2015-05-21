package com.ooyala.chromecastsampleapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooyala.chromecastsampleapp.R;

public class VideoListAdapter extends ArrayAdapter<Video> {

  private final Context _context;
  private int layoutResourceId;
  private Video[] data;

  public VideoListAdapter(Context context, int layoutResourceId, Video[] data) {
    super(context, layoutResourceId, data);
    this.layoutResourceId = layoutResourceId;
    this._context = context;
    this.data = data;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;
    LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    if (convertView == null) {
      convertView = inflater.inflate(layoutResourceId, parent, false);

      holder = new ViewHolder();
      holder.imgView = (ImageView) convertView.findViewById(R.id.itemImageInRow);
      holder.titleView = (TextView) convertView.findViewById(R.id.itemTitleInRow);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    Video video = data[position];
    holder.imgView.setImageResource(video.icon);
    holder.titleView.setText(video.title);

    return convertView;

  }

  private class ViewHolder {
    TextView titleView;
    ImageView imgView;
  }
}
