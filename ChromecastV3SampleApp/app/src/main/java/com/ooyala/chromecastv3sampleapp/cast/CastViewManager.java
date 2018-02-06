package com.ooyala.chromecastv3sampleapp.cast;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.item.Video;
import com.ooyala.chromecastv3sampleapp.R;

public class CastViewManager {
  private View castView;
  private TextView stateTextView;

  public CastViewManager(Activity activity, CastManager manager) {
    castView = activity.getLayoutInflater().inflate(R.layout.cast_video_view, null);
    manager.setCastView(castView);
    stateTextView = castView.findViewById(R.id.castStateTextView);
  }

  public void configureCastView(Video video) {
    final ImageView castBackgroundImage = castView.findViewById(R.id.castBackgroundImage);

    // Update the ImageView on a separate thread

    new Thread(new UpdateImageViewRunnable(castBackgroundImage, video.getPromoImageURL(100, 100))).start();

    TextView videoTitle = castView.findViewById(R.id.videoTitle);
    videoTitle.setText(video.getTitle());

    TextView videoDescription = castView.findViewById(R.id.videoDescription);
    videoDescription.setText(video.getDescription());
  }

  public void updateCastState(Context c, OoyalaPlayer.State state) {
    //TODO: change to get cast device
    String castDeviceName = "Cast device name";
    if (state == OoyalaPlayer.State.LOADING) {
      stateTextView.setText("loading...");
    } else if (state == OoyalaPlayer.State.PLAYING || state == OoyalaPlayer.State.PAUSED) {
      String statusString = String.format("Casting to ", castDeviceName);
      stateTextView.setText(statusString);
    } else {
      stateTextView.setText("");
    }
  }
}

